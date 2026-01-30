package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Auto Red Back 3 Ball - Simple Autonomous
 * Starting position: Red alliance, back of field
 *
 * This autonomous routine:
 *   1. Drives to shooting position
 *   2. Shoots 3 preloaded balls
 *   3. Parks out of the starting zone
 *
 * This is a MIRROR of AutoBlueBack3Ball:
 * - Turn angles are NEGATED
 * - Strafe directions are SWAPPED (left <-> right)
 *
 * Features:
 * - Named constants for all values (Dashboard tunable via @Config)
 * - Timeout protection on shooter waits
 * - Heading correction using IMU
 */
@Config
@Autonomous(name = "Auto Red Back 3Ball", group = "Auto")
public class AutoRedBack3Ball extends LinearOpMode {

    // ========== HARDWARE ==========
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotorEx shooterLeft, shooterRight;
    private DcMotor intakeFront, intakeBack;
    private Servo trapDoor;
    private IMU imu;

    // ========== CONSTANTS - DRIVE ==========
    private static final double TICKS_PER_REV = 537.6;
    private static final double WHEEL_DIAMETER_IN = 3.78;
    private static final double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_IN);
    private static final double COUNTS_PER_DEGREE = 10.0;

    public static double HEADING_KP = 0.06;
    public static double MAX_CORRECTION = 0.3;
    public static double DRIVE_POWER = 0.8;
    public static double TURN_SPEED = 0.4;

    // ========== CONSTANTS - CONSISTENCY ==========
    public static int SETTLE_TIME_MS = 100;
    public static double SLOW_APPROACH_POWER = 0.3;
    public static double SLOW_APPROACH_DISTANCE = 5.0;

    // ========== CONSTANTS - SHOOTER ==========
    public static double P = 10.0;
    public static double I = 0.0;
    public static double D = 0.0;
    public static double F = 13.5;
    public static double SHOOTER_TOLERANCE = 100.0;
    public static int SHOOTER_TIMEOUT_MS = 3000;
    public static double SHOOTER_VELOCITY_MAIN = 1520.0;

    // ========== CONSTANTS - INTAKE ==========
    public static double INTAKE_POWER = 1.0;
    public static int BALL_INDEX_DELAY_MS = 600;

    // ========== CONSTANTS - TRAP DOOR ==========
    public static double TRAP_DOOR_CLOSED = 1.0;

    // ========== SEQUENCE CONSTANTS ==========
    public static double DRIVE_TO_SHOOT = 5.0;
    public static double DRIVE_POWER_APPROACH = 0.5;
    public static double TURN_TO_GOAL = 25.0;                     // MIRRORED: Negated from Blue
    public static double PARK_STRAFE = 30.0;

    @Override
    public void runOpMode() {
        initHardware();

        telemetry.addLine("=== Auto Red Back 3Ball ===");
        telemetry.addLine("3 Ball Autonomous");
        telemetry.addLine("Ready to start");
        telemetry.update();

        waitForStart();

        if (!opModeIsActive()) return;

        setShooterPIDF();
        imu.resetYaw();

        // ===== PHASE 1: Shoot 3 preloaded balls =====
        updateTelemetry("Phase 1", "Driving to shooting position");
        startShooter(SHOOTER_VELOCITY_MAIN);
        driveStraight(DRIVE_TO_SHOOT, DRIVE_POWER_APPROACH, 0);
        turnDegrees(TURN_TO_GOAL);

        updateTelemetry("Phase 1", "Shooting balls 1-3");
        shootBalls(3, SHOOTER_VELOCITY_MAIN);

        // ===== PHASE 2: Park =====
        updateTelemetry("Phase 2", "Parking");
        turnDegrees(-TURN_TO_GOAL);
        strafeRight(PARK_STRAFE, 0);                              // MIRRORED: Right instead of Left

        // ===== CLEANUP =====
        stopAll();

        updateTelemetry("Complete", "3 balls scored!");
        while (opModeIsActive()) {
            telemetry.addData("Status", "Autonomous Complete");
            telemetry.addData("Final Heading", "%.2f degrees", getHeading());
            telemetry.update();
            idle();
        }
    }

    // ========== SHOOTING METHODS ==========

    private void shootBalls(int count, double velocity) {
        startShooter(velocity);
        for (int i = 0; i < count; i++) {
            if (!waitForShooterWithTimeout(velocity, SHOOTER_TIMEOUT_MS)) {
                telemetry.addData("WARNING", "Shooter timeout on ball " + (i + 1));
                telemetry.update();
            }
            runIntakes(INTAKE_POWER);
            sleep(BALL_INDEX_DELAY_MS);
        }
        stopShooter();
        stopIntakes();
    }

    private boolean waitForShooterWithTimeout(double velocity, int timeoutMs) {
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.milliseconds() < timeoutMs) {
            if (shooterAtSpeed(velocity)) {
                return true;
            }
            telemetry.addData("Shooter", "Spinning up...");
            telemetry.addData("Left Velocity", "%.0f / %.0f", shooterLeft.getVelocity(), velocity);
            telemetry.addData("Right Velocity", "%.0f / %.0f", shooterRight.getVelocity(), velocity);
            telemetry.addData("Time", "%.1f / %.1f sec", timer.seconds(), timeoutMs / 1000.0);
            telemetry.update();
            idle();
        }
        return false;
    }

    private void startShooter(double velocity) {
        shooterLeft.setVelocity(velocity);
        shooterRight.setVelocity(velocity);
    }

    private void stopShooter() {
        shooterLeft.setVelocity(0);
        shooterRight.setVelocity(0);
    }

    private boolean shooterAtSpeed(double targetVelocity) {
        double leftVel = shooterLeft.getVelocity();
        double rightVel = shooterRight.getVelocity();
        return Math.abs(leftVel - targetVelocity) < SHOOTER_TOLERANCE
                && Math.abs(rightVel - targetVelocity) < SHOOTER_TOLERANCE;
    }

    private void setShooterPIDF() {
        shooterLeft.setVelocityPIDFCoefficients(P, I, D, F);
        shooterRight.setVelocityPIDFCoefficients(P, I, D, F);
    }

    // ========== INTAKE METHODS ==========

    private void runIntakes(double power) {
        intakeFront.setPower(power);
        intakeBack.setPower(power);
    }

    private void stopIntakes() {
        intakeFront.setPower(0);
        intakeBack.setPower(0);
    }

    // ========== DRIVE METHODS ==========

    private void driveStraight(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        int slowApproachTicks = (int) (SLOW_APPROACH_DISTANCE * TICKS_PER_INCH);
        double direction = Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()
                && Math.abs(frontLeft.getCurrentPosition()) < ticks
                && Math.abs(frontRight.getCurrentPosition()) < ticks) {

            int currentTicks = (Math.abs(frontLeft.getCurrentPosition()) + Math.abs(frontRight.getCurrentPosition())) / 2;
            int ticksRemaining = ticks - currentTicks;

            double currentPower = power;
            if (ticksRemaining < slowApproachTicks) {
                double rampFactor = (double) ticksRemaining / slowApproachTicks;
                currentPower = SLOW_APPROACH_POWER + (power - SLOW_APPROACH_POWER) * rampFactor;
            }

            double correction = headingCorrection(heading);
            double leftPower = (currentPower * direction) - correction;
            double rightPower = (currentPower * direction) + correction;

            double maxPower = Math.max(Math.abs(leftPower), Math.abs(rightPower));
            if (maxPower > 1.0) {
                leftPower /= maxPower;
                rightPower /= maxPower;
            }

            frontLeft.setPower(leftPower);
            backLeft.setPower(leftPower);
            frontRight.setPower(rightPower);
            backRight.setPower(rightPower);
        }

        stopMotors();
        sleep(SETTLE_TIME_MS);
    }

    private void strafe(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        int slowApproachTicks = (int) (SLOW_APPROACH_DISTANCE * TICKS_PER_INCH);
        double dir = Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()) {
            double avg = (Math.abs(frontLeft.getCurrentPosition())
                    + Math.abs(frontRight.getCurrentPosition())
                    + Math.abs(backLeft.getCurrentPosition())
                    + Math.abs(backRight.getCurrentPosition())) / 4.0;

            if (avg >= ticks) break;

            double ticksRemaining = ticks - avg;
            double currentPower = power;
            if (ticksRemaining < slowApproachTicks) {
                double rampFactor = ticksRemaining / slowApproachTicks;
                currentPower = SLOW_APPROACH_POWER + (power - SLOW_APPROACH_POWER) * rampFactor;
            }

            double correction = headingCorrection(heading);
            double fl =  currentPower * dir - correction;
            double fr = -currentPower * dir + correction;
            double bl = -currentPower * dir - correction;
            double br =  currentPower * dir + correction;

            double max = Math.max(1.0,
                    Math.max(Math.abs(fl),
                            Math.max(Math.abs(fr),
                                    Math.max(Math.abs(bl), Math.abs(br)))));

            frontLeft.setPower(fl / max);
            frontRight.setPower(fr / max);
            backLeft.setPower(bl / max);
            backRight.setPower(br / max);
        }

        stopMotors();
        sleep(SETTLE_TIME_MS);
    }

    private void strafeRight(double inches, double heading) {
        strafe(Math.abs(inches), DRIVE_POWER, heading);
    }

    private void turnDegrees(double degrees) {
        int turnCounts = (int) (degrees * COUNTS_PER_DEGREE);

        resetEncoders();

        frontLeft.setTargetPosition(turnCounts);
        backLeft.setTargetPosition(turnCounts);
        frontRight.setTargetPosition(-turnCounts);
        backRight.setTargetPosition(-turnCounts);

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(TURN_SPEED);
        frontRight.setPower(TURN_SPEED);
        backLeft.setPower(TURN_SPEED);
        backRight.setPower(TURN_SPEED);

        while (opModeIsActive()
                && frontLeft.isBusy()
                && frontRight.isBusy()
                && backLeft.isBusy()
                && backRight.isBusy()) {
            idle();
        }

        stopMotors();
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sleep(SETTLE_TIME_MS);
    }

    // ========== UTILITY METHODS ==========

    private void initHardware() {
        telemetry.addLine("Initializing hardware...");
        telemetry.update();

        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        setBrake(true);

        shooterLeft = hardwareMap.get(DcMotorEx.class, "SL");
        shooterLeft.setDirection(DcMotor.Direction.REVERSE);
        shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooterRight = hardwareMap.get(DcMotorEx.class, "SR");
        shooterRight.setDirection(DcMotor.Direction.FORWARD);
        shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeFront = hardwareMap.get(DcMotor.class, "IF");
        intakeFront.setDirection(DcMotor.Direction.FORWARD);
        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeBack = hardwareMap.get(DcMotor.class, "IB");
        intakeBack.setDirection(DcMotor.Direction.FORWARD);
        intakeBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        trapDoor = hardwareMap.get(Servo.class, "TD");
        trapDoor.setPosition(TRAP_DOOR_CLOSED);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        ));

        telemetry.addLine("Hardware initialized!");
        telemetry.update();
    }

    private void updateTelemetry(String phase, String step) {
        telemetry.addData("Phase", phase);
        telemetry.addData("Step", step);
        telemetry.addData("Heading", "%.2f degrees", getHeading());
        telemetry.update();
    }

    private void setMotorMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    private void setBrake(boolean brake) {
        DcMotor.ZeroPowerBehavior zpb = brake
                ? DcMotor.ZeroPowerBehavior.BRAKE
                : DcMotor.ZeroPowerBehavior.FLOAT;
        frontLeft.setZeroPowerBehavior(zpb);
        frontRight.setZeroPowerBehavior(zpb);
        backLeft.setZeroPowerBehavior(zpb);
        backRight.setZeroPowerBehavior(zpb);
    }

    private void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    private void resetEncoders() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    private double headingCorrection(double target) {
        double error = AngleUnit.normalizeDegrees(target - getHeading());
        double correction = error * HEADING_KP;
        return Math.max(-MAX_CORRECTION, Math.min(MAX_CORRECTION, correction));
    }

    private void stopAll() {
        stopMotors();
        stopShooter();
        stopIntakes();
    }
}
