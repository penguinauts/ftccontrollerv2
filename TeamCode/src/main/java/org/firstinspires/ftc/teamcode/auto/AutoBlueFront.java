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
 * Auto Blue Front - 9 Ball Autonomous
 * Starting position: Blue alliance, front of field
 *
 * This is a MIRROR of AutoRedFront:
 * - Turn angles are NEGATED
 * - Strafe directions are SWAPPED (left <-> right)
 *
 * Features:
 * - Named constants for all values (Dashboard tunable)
 * - Timeout protection on shooter waits
 * - Power normalization in drive methods
 * - Real-time telemetry for debugging
 * - Helper methods for common sequences
 */
@Config
@Autonomous(name = "Auto Blue Front", group = "Auto")
public class AutoBlueFront extends LinearOpMode {

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

    // ========== CONSTANTS - SHOOTER ==========
    public static double P = 10.0;
    public static double I = 0.0;
    public static double D = 0.0;
    public static double F = 13.5;
    public static double SHOOTER_VELOCITY = 1397.0;
    public static double SHOOTER_VELOCITY_BALL4 = 1405.0;
    public static double SHOOTER_TOLERANCE = 100.0;
    public static int SHOOTER_TIMEOUT_MS = 3000;

    // ========== CONSTANTS - INTAKE ==========
    public static double INTAKE_POWER = 1.0;
    public static double BACK_INTAKE_HOLD = -0.5;
    public static int BALL_INDEX_DELAY_MS = 600;

    // ========== CONSTANTS - TRAP DOOR ==========
    public static double TRAP_DOOR_CLOSED = 1.0;
    public static double TRAP_DOOR_OPEN = 0.85;

    // ========== SEQUENCE CONSTANTS - PHASE 1 (First 3 balls) ==========
    public static double DRIVE_TO_SHOOT_1 = -40.0;

    // ========== SEQUENCE CONSTANTS - PHASE 2 (Balls 4-6) ==========
    // MIRRORED: Turn angles negated, strafe directions swapped
    public static double TURN_TO_INTAKE_1 = -46.0;  // Negated from Red
    public static double STRAFE_TO_INTAKE_1 = 1.9;
    public static double DRIVE_TO_BALLS_1 = 39.0;
    public static double DRIVE_POWER_COLLECT = 0.68;
    public static double DRIVE_BACK_FROM_BALLS_1 = -32.4;
    public static double TURN_TO_SHOOT_2 = 59.9;  // Negated from Red
    public static double STRAFE_ADJUST_2 = 1.55;

    // ========== SEQUENCE CONSTANTS - PHASE 3 (Balls 7-9) ==========
    // MIRRORED: Turn angles negated, strafe directions swapped
    public static double STRAFE_TO_ZONE_3 = 17.0;
    public static double TURN_TO_INTAKE_3 = -55.0;  // Negated from Red
    public static double STRAFE_TO_INTAKE_3 = 13.82;
    public static double DRIVE_TO_BALLS_3 = 40.0;
    public static double DRIVE_BACK_FROM_BALLS_3 = -12.0;
    public static double DIAGONAL_BACK_3 = 12.0;
    public static double TURN_TO_SHOOT_3 = 55.0;  // Negated from Red
    public static double STRAFE_TO_SHOOT_3 = 26.0;
    public static double STRAFE_FINAL = 8.0;

    @Override
    public void runOpMode() {
        initHardware();

        telemetry.addLine("=== Auto Blue Front ===");
        telemetry.addLine("Ready to start");
        telemetry.update();

        waitForStart();

        if (!opModeIsActive()) return;

        setShooterPIDF();
        imu.resetYaw();

        // ===== PHASE 1: First 3 balls =====
        updateTelemetry("Phase 1", "Driving to shoot position");
        driveStraight(DRIVE_TO_SHOOT_1, DRIVE_POWER, 0);

        updateTelemetry("Phase 1", "Shooting balls 1-3");
        shootBalls(3, SHOOTER_VELOCITY);

        // ===== PHASE 2: Collect & shoot balls 4-6 =====
        updateTelemetry("Phase 2", "Turning to intake zone");
        turnDegrees(TURN_TO_INTAKE_1);

        updateTelemetry("Phase 2", "Strafing to intake");
        strafeLeft(STRAFE_TO_INTAKE_1, 0);  // MIRRORED: Left instead of Right

        updateTelemetry("Phase 2", "Collecting balls");
        collectBalls(DRIVE_TO_BALLS_1);

        updateTelemetry("Phase 2", "Driving back");
        driveStraight(DRIVE_BACK_FROM_BALLS_1, DRIVE_POWER, 0);

        updateTelemetry("Phase 2", "Turning to shoot");
        turnDegrees(TURN_TO_SHOOT_2);

        updateTelemetry("Phase 2", "Adjusting position");
        strafeRight(STRAFE_ADJUST_2, 0);  // MIRRORED: Right instead of Left

        updateTelemetry("Phase 2", "Shooting balls 4-6");
        shootBallsWithVelocities(new double[]{SHOOTER_VELOCITY_BALL4, SHOOTER_VELOCITY, SHOOTER_VELOCITY});

        // ===== PHASE 3: Collect & shoot balls 7-9 =====
        updateTelemetry("Phase 3", "Starting intake");
        startIntakeForCollection();

        updateTelemetry("Phase 3", "Strafing to zone");
        strafeLeft(STRAFE_TO_ZONE_3, 0);  // MIRRORED: Left instead of Right

        updateTelemetry("Phase 3", "Turning to intake");
        turnDegrees(TURN_TO_INTAKE_3);

        updateTelemetry("Phase 3", "Strafing to balls");
        strafeLeft(STRAFE_TO_INTAKE_3, 0);  // MIRRORED: Left instead of Right

        updateTelemetry("Phase 3", "Collecting balls");
        driveStraight(DRIVE_TO_BALLS_3, DRIVE_POWER_COLLECT, 0);

        updateTelemetry("Phase 3", "Driving back");
        driveStraight(DRIVE_BACK_FROM_BALLS_3, DRIVE_POWER, 0);

        updateTelemetry("Phase 3", "Diagonal back");
        driveDiagonalLeftBackward(DIAGONAL_BACK_3, DRIVE_POWER, 0);  // MIRRORED: Left instead of Right

        updateTelemetry("Phase 3", "Turning to shoot");
        turnDegrees(TURN_TO_SHOOT_3);

        updateTelemetry("Phase 3", "Strafing to shoot");
        strafeRight(STRAFE_TO_SHOOT_3, 0);  // MIRRORED: Right instead of Left

        updateTelemetry("Phase 3", "Shooting balls 7-9");
        shootBalls(3, SHOOTER_VELOCITY);

        updateTelemetry("Phase 3", "Final strafe");
        strafeLeft(STRAFE_FINAL, 0);  // MIRRORED: Left instead of Right

        // ===== CLEANUP =====
        stopAll();

        updateTelemetry("Complete", "Auto finished!");
        while (opModeIsActive()) {
            idle();
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Shoots a specified number of balls at a given velocity
     */
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

    /**
     * Shoots balls with individual velocities for each ball
     */
    private void shootBallsWithVelocities(double[] velocities) {
        for (int i = 0; i < velocities.length; i++) {
            startShooter(velocities[i]);
            if (!waitForShooterWithTimeout(velocities[i], SHOOTER_TIMEOUT_MS)) {
                telemetry.addData("WARNING", "Shooter timeout on ball " + (i + 1));
                telemetry.update();
            }
            runIntakes(INTAKE_POWER);
            sleep(BALL_INDEX_DELAY_MS);
        }
        stopShooter();
        stopIntakes();
    }

    /**
     * Collects balls by driving forward with intakes running
     */
    private void collectBalls(double distance) {
        startIntakeForCollection();
        driveStraight(distance, DRIVE_POWER_COLLECT, 0);
    }

    /**
     * Starts intakes configured for ball collection
     */
    private void startIntakeForCollection() {
        intakeFront.setPower(INTAKE_POWER);
        intakeBack.setPower(BACK_INTAKE_HOLD);
    }

    /**
     * Waits for shooter to reach target velocity with timeout protection
     * @return true if velocity reached, false if timeout
     */
    private boolean waitForShooterWithTimeout(double velocity, int timeoutMs) {
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.milliseconds() < timeoutMs) {
            if (shooterAtSpeed(velocity)) {
                return true;
            }
            telemetry.addData("Shooter", "Waiting... L:%.0f R:%.0f Target:%.0f",
                    shooterLeft.getVelocity(), shooterRight.getVelocity(), velocity);
            telemetry.update();
            idle();
        }
        return false;
    }

    /**
     * Updates telemetry with current phase and step
     */
    private void updateTelemetry(String phase, String step) {
        telemetry.addData("Phase", phase);
        telemetry.addData("Step", step);
        telemetry.addData("Heading", "%.2f", getHeading());
        telemetry.update();
    }

    // ========== SHOOTER METHODS ==========

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

    // ========== TRAP DOOR METHODS ==========

    private void openTrapDoor() {
        trapDoor.setPosition(TRAP_DOOR_OPEN);
    }

    private void closeTrapDoor() {
        trapDoor.setPosition(TRAP_DOOR_CLOSED);
    }

    // ========== DRIVE METHODS ==========

    /**
     * Drives straight with heading correction and power normalization
     */
    private void driveStraight(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double direction = Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()
                && Math.abs(frontLeft.getCurrentPosition()) < ticks
                && Math.abs(frontRight.getCurrentPosition()) < ticks) {

            double correction = headingCorrection(heading);

            double leftPower = (power * direction) - correction;
            double rightPower = (power * direction) + correction;

            // Normalize power to prevent values > 1.0
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
    }

    /**
     * Strafes with heading correction and power normalization
     */
    private void strafe(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double dir = Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()) {
            double avg = (Math.abs(frontLeft.getCurrentPosition())
                    + Math.abs(frontRight.getCurrentPosition())
                    + Math.abs(backLeft.getCurrentPosition())
                    + Math.abs(backRight.getCurrentPosition())) / 4.0;

            if (avg >= ticks) break;

            double correction = headingCorrection(heading);

            double fl =  power * dir - correction;
            double fr = -power * dir + correction;
            double bl = -power * dir - correction;
            double br =  power * dir + correction;

            // Normalize power
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
    }

    private void strafeLeft(double inches, double heading) {
        strafe(-Math.abs(inches), DRIVE_POWER, heading);
    }

    private void strafeRight(double inches, double heading) {
        strafe(Math.abs(inches), DRIVE_POWER, heading);
    }

    /**
     * Turns using encoder-based RUN_TO_POSITION
     */
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
    }

    /**
     * Diagonal drive left-backward with heading correction (MIRRORED from Red)
     */
    private void driveDiagonalLeftBackward(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double dir = -Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()) {
            double avg = (Math.abs(frontRight.getCurrentPosition())
                    + Math.abs(backLeft.getCurrentPosition())) / 2.0;

            if (avg >= ticks) break;

            double correction = headingCorrection(heading);

            double fl = 0 - correction;
            double fr = (power * dir) + correction;
            double bl = (power * dir) - correction;
            double br = 0 + correction;

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
    }

    // ========== UTILITY METHODS ==========

    private void initHardware() {
        // Drive motors
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

        // Shooter motors
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

        // Intake motors
        intakeFront = hardwareMap.get(DcMotor.class, "IF");
        intakeFront.setDirection(DcMotor.Direction.FORWARD);
        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeBack = hardwareMap.get(DcMotor.class, "IB");
        intakeBack.setDirection(DcMotor.Direction.FORWARD);
        intakeBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Trap door servo
        trapDoor = hardwareMap.get(Servo.class, "TD");
        trapDoor.setPosition(TRAP_DOOR_CLOSED);

        // IMU
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        ));
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
