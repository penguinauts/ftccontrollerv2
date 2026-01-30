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
 * Auto Blue Back 6Ball - 6/9 Ball Autonomous
 * Starting position: Blue alliance, back of field
 *
 * This autonomous routine scores 6 or 9 balls:
 *   Phase 1: Drive to shooting position, shoot 3 preloaded balls
 *   Phase 2: Collect 3 balls from first stack, return and shoot
 *   Phase 3: (Optional) Collect 3 balls from second stack, return and shoot
 *   Phase 4: Park out of the starting zone
 *
 * Set ENABLE_PHASE_3 = 1 to run Phase 3 (9 balls), 0 to skip (6 balls)
 *
 * Features:
 * - Named constants for all values (Dashboard tunable via @Config)
 * - Timeout protection on shooter waits
 * - Power normalization in drive methods
 * - Real-time telemetry for debugging
 * - Heading correction using IMU
 */
@Config
@Autonomous(name = "Auto Blue Back 6Ball", group = "Auto")
public class AutoBlueBack6Ball extends LinearOpMode {

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
    public static double BACK_INTAKE_HOLD = -0.5;
    public static int BALL_INDEX_DELAY_MS = 600;

    // ========== CONSTANTS - TRAP DOOR ==========
    public static double TRAP_DOOR_CLOSED = 1.0;

    // ========== SEQUENCE CONSTANTS - PHASE 1 ==========
    public static double PHASE1_DRIVE_TO_SHOOT = 5.0;
    public static double PHASE1_DRIVE_POWER = 0.5;
    public static double PHASE1_TURN_TO_GOAL = -25.0;

    // ========== SEQUENCE CONSTANTS - PHASE 2 ==========
    public static double PHASE2_DRIVE_FORWARD = 14.0;
    public static double PHASE2_TURN_TO_BALLS = -90.0;
    public static double PHASE2_DRIVE_TO_BALLS = 35.0;
    public static double PHASE2_TURN_BACK = 90.0;
    public static double PHASE2_DRIVE_BACK = -14.0;

    // ========== SEQUENCE CONSTANTS - PHASE 3 ==========
    public static int ENABLE_PHASE_3 = 0;                     // 0 = skip Phase 3, 1 = run Phase 3
    public static double PHASE3_TURN_TO_BALLS = -65.0;
    public static double PHASE3_STRAFE_ADJUST = 4.0;
    public static double PHASE3_DRIVE_TO_BALLS = 45.0;
    public static int PHASE3_INTAKE_WAIT_MS = 500;
    public static double PHASE3_TURN_BACK = 90.0;
    public static double PHASE3_DRIVE_ADJUST = 4.0;

    // ========== SEQUENCE CONSTANTS - PARKING ==========
    public static double PARK_STRAFE = 30.0;

    @Override
    public void runOpMode() {
        initHardware();

        telemetry.addLine("=== Auto Blue Back 6Ball ===");
        telemetry.addLine(ENABLE_PHASE_3 == 1 ? "9 Ball Autonomous" : "6 Ball Autonomous");
        telemetry.addLine("Ready to start");
        telemetry.update();

        waitForStart();

        if (!opModeIsActive()) return;

        setShooterPIDF();
        imu.resetYaw();
        double heading;

        // ===== PHASE 1: Shoot 3 preloaded balls =====
        updateTelemetry("Phase 1", "Driving to shooting position");
        startShooter(SHOOTER_VELOCITY_MAIN);
        driveStraight(PHASE1_DRIVE_TO_SHOOT, PHASE1_DRIVE_POWER, 0);
        turnDegrees(PHASE1_TURN_TO_GOAL);

        updateTelemetry("Phase 1", "Shooting balls 1-3");
        shootBalls(3, SHOOTER_VELOCITY_MAIN);

        // ===== PHASE 2: Collect and shoot balls 4-6 =====
        updateTelemetry("Phase 2", "Navigating to first ball stack");
        turnDegrees(-PHASE1_TURN_TO_GOAL);
        driveStraight(PHASE2_DRIVE_FORWARD, PHASE1_DRIVE_POWER, 0);
        turnDegrees(PHASE2_TURN_TO_BALLS);

        updateTelemetry("Phase 2", "Collecting balls 4-6");
        heading = getHeading();
        startIntakeForCollection();
        driveStraight(PHASE2_DRIVE_TO_BALLS, PHASE1_DRIVE_POWER, heading);

        updateTelemetry("Phase 2", "Returning to shoot position");
        heading = getHeading();
        driveStraight(-PHASE2_DRIVE_TO_BALLS, PHASE1_DRIVE_POWER, heading);
        turnDegrees(PHASE2_TURN_BACK);
        driveStraight(PHASE2_DRIVE_BACK, PHASE1_DRIVE_POWER, 0);
        turnDegrees(PHASE1_TURN_TO_GOAL);

        updateTelemetry("Phase 2", "Shooting balls 4-6");
        shootBalls(3, SHOOTER_VELOCITY_MAIN);

        // ===== PHASE 3: Collect and shoot balls 7-9 (optional) =====
        if (ENABLE_PHASE_3 == 1) {
            updateTelemetry("Phase 3", "Navigating to second ball stack");
            turnDegrees(PHASE3_TURN_TO_BALLS);
            heading = getHeading();
            strafeLeft(PHASE3_STRAFE_ADJUST, heading);

            updateTelemetry("Phase 3", "Collecting balls 7-9");
            heading = getHeading();
            startIntakeForCollection();
            driveStraight(PHASE3_DRIVE_TO_BALLS, PHASE1_DRIVE_POWER, heading);
            sleep(PHASE3_INTAKE_WAIT_MS);

            updateTelemetry("Phase 3", "Returning to shoot position");
            heading = getHeading();
            driveStraight(-PHASE3_DRIVE_TO_BALLS, .8, heading);
            turnDegrees(PHASE3_TURN_BACK);
            heading = getHeading();
            driveStraight(PHASE3_DRIVE_ADJUST, 0.8, heading);
            turnDegrees(PHASE1_TURN_TO_GOAL);

            updateTelemetry("Phase 3", "Shooting balls 7-9");
            shootBalls(3, SHOOTER_VELOCITY_MAIN);
        } else {
            updateTelemetry("Phase 3", "Skipped");
        }

        // ===== PHASE 4: Park =====
        updateTelemetry("Phase 4", "Parking");
        strafeLeft(PARK_STRAFE, 0);

        // ===== CLEANUP =====
        stopAll();

        updateTelemetry("Complete", ENABLE_PHASE_3 == 1 ? "9 balls scored!" : "6 balls scored!");
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

    private void startIntakeForCollection() {
        intakeFront.setPower(INTAKE_POWER);
        intakeBack.setPower(BACK_INTAKE_HOLD);
    }

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

    private void strafeLeft(double inches, double heading) {
        strafe(-Math.abs(inches), DRIVE_POWER, heading);
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
