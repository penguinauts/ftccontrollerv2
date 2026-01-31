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
 * Auto Red Front - 9 Ball Autonomous
 * Starting position: Red alliance, front of field
 *
 * This autonomous routine scores 9 balls in 3 phases:
 *   Phase 1: Drive to shooting position, shoot 3 preloaded balls
 *   Phase 2: Collect 3 balls from first stack, return and shoot
 *   Phase 3: Collect 3 balls from second stack, return and shoot
 *
 * Features:
 * - Named constants for all values (Dashboard tunable via @Config)
 * - Timeout protection on shooter waits
 * - Power normalization in drive methods
 * - Real-time telemetry for debugging
 * - Heading correction using IMU
 */
@Config
@Autonomous(name = "Red - 9Ball - Front", group = "Red")
public class AutoRedFront extends LinearOpMode {

    // ========== HARDWARE ==========
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotorEx shooterLeft, shooterRight;
    private DcMotor intakeFront, intakeBack;
    private Servo trapDoor;
    private IMU imu;

    // ========== CONSTANTS - DRIVE ==========
    private static final double TICKS_PER_REV = 537.6;        // GoBilda 312 RPM motor encoder ticks
    private static final double WHEEL_DIAMETER_IN = 3.78;     // Mecanum wheel diameter in inches
    private static final double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_IN);
    private static final double COUNTS_PER_DEGREE = 10.0;     // Encoder counts per degree of rotation

    public static double HEADING_KP = 0.06;                   // Proportional gain for heading correction
    public static double MAX_CORRECTION = 0.3;                // Maximum heading correction power
    public static double DRIVE_POWER = 0.8;                   // Default drive power
    public static double TURN_SPEED = 0.4;                    // Speed for encoder-based turns

    // ========== CONSTANTS - CONSISTENCY ==========
    public static int SETTLE_TIME_MS = 100;                   // Time to let robot settle after movements
    public static double SLOW_APPROACH_POWER = 0.3;           // Slower power for final approach
    public static double SLOW_APPROACH_DISTANCE = 5.0;        // Inches - slow down for last X inches

    // ========== CONSTANTS - SHOOTER ==========
    public static double P = 10.0;                            // PIDF proportional coefficient
    public static double I = 0.0;                             // PIDF integral coefficient
    public static double D = 0.0;                             // PIDF derivative coefficient
    public static double F = 13.5;                            // PIDF feedforward coefficient
    public static double SHOOTER_TOLERANCE = 100.0;           // Velocity tolerance in ticks/sec
    public static int SHOOTER_TIMEOUT_MS = 3000;              // Max wait time for shooter to spin up

    // ========== CONSTANTS - INTAKE ==========
    public static double INTAKE_POWER = 1.0;                  // Full power for intake
    public static double BACK_INTAKE_HOLD = -0.5;             // Negative power to hold balls in hopper
    public static int BALL_INDEX_DELAY_MS = 600;              // Time between shooting each ball

    // ========== CONSTANTS - TRAP DOOR ==========
    public static double TRAP_DOOR_CLOSED = 1.0;              // Servo position when closed
    public static double TRAP_DOOR_OPEN = 0.85;               // Servo position when open

    // ========== SEQUENCE CONSTANTS - SHOOTER VELOCITIES ==========
    public static double SHOOTER_VELOCITY_MAIN = 1180.0;      // Main shooting velocity (ticks/sec)

    // ========== SEQUENCE CONSTANTS - PHASE 1 (First 3 balls) ==========
    public static double PHASE1_DRIVE_TO_SHOOT = -20.0;       // Drive backward to shooting position
    public static double PHASE1_DRIVE_POWER = 0.5;            // Slower drive for accuracy

    // ========== SEQUENCE CONSTANTS - PHASE 2 (Balls 4-6) ==========
    public static double PHASE2_TURN_ANGLE = 45.0;            // Turn toward first ball stack
    public static double PHASE2_STRAFE_TO_BALLS = 25.0;       // Strafe right to align with balls
    public static double PHASE2_DRIVE_TO_BALLS = 22.0;        // Drive forward to collect balls
    public static double PHASE2_DRIVE_POWER_COLLECT = 0.5;    // Slower drive while collecting
    public static double PHASE2_DRIVE_BACK = -20.0;           // Drive backward after collecting
    public static double PHASE2_DRIVE_BACK_POWER = 0.8;       // Faster drive when returning
    public static double PHASE2_STRAFE_BACK = 20.0;           // Strafe left to shooting position

    // ========== SEQUENCE CONSTANTS - PHASE 3 (Balls 7-9) ==========
    public static double PHASE3_STRAFE_TO_BALLS = 42.0;       // Strafe right to second ball stack
    public static double PHASE3_DRIVE_TO_BALLS = 26.0;        // Drive forward to collect balls
    public static double PHASE3_DRIVE_BACK = -24.0;           // Drive backward after collecting
    public static double PHASE3_STRAFE_BACK = 42.0;           // Strafe left to shooting position

    // ========== SEQUENCE CONSTANTS - PARKING ==========
    public static double PARK_STRAFE = 25.0;                  // Final strafe to park

    @Override
    public void runOpMode() {
        // Initialize all hardware components
        initHardware();

        // Display ready status
        telemetry.addLine("=== Auto Red Front ===");
        telemetry.addLine("9 Ball Autonomous");
        telemetry.addLine("Ready to start");
        telemetry.update();

        waitForStart();

        // Exit if stop is pressed during init
        if (!opModeIsActive()) return;

        // Configure shooter PIDF and reset IMU heading
        setShooterPIDF();
        imu.resetYaw();

        // ===== PHASE 1: Shoot 3 preloaded balls =====
        updateTelemetry("Phase 1", "Driving to shooting position");

        // Start shooter early so it's at speed when we arrive
        startShooter(SHOOTER_VELOCITY_MAIN);

        // Drive backward to optimal shooting distance
        driveStraight(PHASE1_DRIVE_TO_SHOOT, PHASE1_DRIVE_POWER, 0);

        // Shoot 3 preloaded balls
        updateTelemetry("Phase 1", "Shooting 3 preloaded balls");
        shootBalls(3, SHOOTER_VELOCITY_MAIN);

        // ===== PHASE 2: Collect and shoot balls 4-6 =====
        updateTelemetry("Phase 2", "Turning to first ball stack");

        // Turn toward the first ball stack
        turnDegrees(PHASE2_TURN_ANGLE);
        double heading = getHeading();  // Capture actual IMU heading after turn

        // Strafe right to align with balls
        strafeRight(PHASE2_STRAFE_TO_BALLS, heading);

        // Start intakes before driving into balls
        updateTelemetry("Phase 2", "Collecting balls 4-6");
        startIntakeForCollection();

        // Drive forward slowly to collect balls
        driveStraight(PHASE2_DRIVE_TO_BALLS, PHASE2_DRIVE_POWER_COLLECT, heading);

        // Drive backward to clear the ball area
        driveStraight(PHASE2_DRIVE_BACK, PHASE2_DRIVE_BACK_POWER, heading);

        // Strafe left back toward shooting position
        strafeLeft(PHASE2_STRAFE_BACK, heading);

        // Turn back to face the goal
        turnDegrees(-PHASE2_TURN_ANGLE);
        heading = getHeading();  // Capture actual IMU heading after turn

        // Spin up shooter and shoot balls 4-6
        updateTelemetry("Phase 2", "Shooting balls 4-6");
        startShooter(SHOOTER_VELOCITY_MAIN);
        shootBalls(3, SHOOTER_VELOCITY_MAIN);

        // ===== PHASE 3: Collect and shoot balls 7-9 =====
        updateTelemetry("Phase 3", "Turning to second ball stack");

        // Turn toward the second ball stack
        turnDegrees(PHASE2_TURN_ANGLE);
        heading = getHeading();  // Capture actual IMU heading after turn

        // Strafe further right to reach second stack
        strafeRight(PHASE3_STRAFE_TO_BALLS, heading);

        // Start intakes before driving into balls
        updateTelemetry("Phase 3", "Collecting balls 7-9");
        startIntakeForCollection();

        // Drive forward slowly to collect balls
        driveStraight(PHASE3_DRIVE_TO_BALLS, PHASE2_DRIVE_POWER_COLLECT, heading);

        // Drive backward to clear the ball area
        driveStraight(PHASE3_DRIVE_BACK, PHASE2_DRIVE_BACK_POWER, heading);

        // Strafe left back toward shooting position
        strafeLeft(PHASE3_STRAFE_BACK, heading);

        // Turn back to face the goal
        turnDegrees(-PHASE2_TURN_ANGLE);
        heading = getHeading();  // Capture actual IMU heading after turn

        // Spin up shooter and shoot balls 7-9
        updateTelemetry("Phase 3", "Shooting balls 7-9");
        startShooter(SHOOTER_VELOCITY_MAIN);
        shootBalls(3, SHOOTER_VELOCITY_MAIN);

        // ===== PARKING =====
        updateTelemetry("Parking", "Moving to park position");

        // Strafe right to parking zone
        strafeRight(PARK_STRAFE, heading);

        // ===== CLEANUP =====
        stopAll();

        updateTelemetry("Complete", "Auto finished - 9 balls scored!");

        // Keep telemetry displayed until auto ends
        while (opModeIsActive()) {
            telemetry.addData("Status", "Autonomous Complete");
            telemetry.addData("Final Heading", "%.2f degrees", getHeading());
            telemetry.update();
            idle();
        }
    }

    // ========== SHOOTING HELPER METHODS ==========

    /**
     * Shoots a specified number of balls at a given velocity.
     * Waits for shooter to reach target velocity before each ball.
     *
     * @param count    Number of balls to shoot
     * @param velocity Target shooter velocity in ticks/sec
     */
    private void shootBalls(int count, double velocity) {
        startShooter(velocity);
        for (int i = 0; i < count; i++) {
            // Wait for shooter wheels to reach target velocity
            if (!waitForShooterWithTimeout(velocity, SHOOTER_TIMEOUT_MS)) {
                telemetry.addData("WARNING", "Shooter timeout on ball " + (i + 1));
                telemetry.update();
            }
            // Run intakes to feed ball into shooter
            runIntakes(INTAKE_POWER);
            sleep(BALL_INDEX_DELAY_MS);
        }
        stopShooter();
        stopIntakes();
    }

    /**
     * Shoots balls with individual velocities for each ball.
     * Useful when different balls need different velocities.
     *
     * @param velocities Array of velocities, one per ball
     */
    private void shootBallsWithVelocities(double[] velocities) {
        for (int i = 0; i < velocities.length; i++) {
            // Set shooter to this ball's velocity
            startShooter(velocities[i]);

            // Wait for shooter to reach target velocity
            if (!waitForShooterWithTimeout(velocities[i], SHOOTER_TIMEOUT_MS)) {
                telemetry.addData("WARNING", "Shooter timeout on ball " + (i + 1));
                telemetry.update();
            }

            // Run intakes to feed ball into shooter
            runIntakes(INTAKE_POWER);
            sleep(BALL_INDEX_DELAY_MS);
        }
        stopShooter();
        stopIntakes();
    }

    /**
     * Starts intakes configured for ball collection.
     * Front intake runs forward, back intake runs reverse to hold balls.
     */
    private void startIntakeForCollection() {
        intakeFront.setPower(INTAKE_POWER);
        intakeBack.setPower(BACK_INTAKE_HOLD);
    }

    /**
     * Waits for shooter to reach target velocity with timeout protection.
     * Displays real-time shooter velocity on telemetry.
     *
     * @param velocity  Target velocity in ticks/sec
     * @param timeoutMs Maximum time to wait in milliseconds
     * @return true if velocity reached, false if timeout
     */
    private boolean waitForShooterWithTimeout(double velocity, int timeoutMs) {
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && timer.milliseconds() < timeoutMs) {
            if (shooterAtSpeed(velocity)) {
                return true;
            }
            // Display current shooter status
            telemetry.addData("Shooter", "Spinning up...");
            telemetry.addData("Left Velocity", "%.0f / %.0f", shooterLeft.getVelocity(), velocity);
            telemetry.addData("Right Velocity", "%.0f / %.0f", shooterRight.getVelocity(), velocity);
            telemetry.addData("Time", "%.1f / %.1f sec", timer.seconds(), timeoutMs / 1000.0);
            telemetry.update();
            idle();
        }
        return false;
    }

    /**
     * Updates telemetry with current phase and step information.
     * Also displays current robot heading for debugging.
     *
     * @param phase Current phase name (e.g., "Phase 1")
     * @param step  Current step description
     */
    private void updateTelemetry(String phase, String step) {
        telemetry.addData("Phase", phase);
        telemetry.addData("Step", step);
        telemetry.addData("Heading", "%.2f degrees", getHeading());
        telemetry.update();
    }

    // ========== SHOOTER METHODS ==========

    /**
     * Starts shooter motors at specified velocity.
     *
     * @param velocity Target velocity in ticks/sec
     */
    private void startShooter(double velocity) {
        shooterLeft.setVelocity(velocity);
        shooterRight.setVelocity(velocity);
    }

    /**
     * Stops shooter motors.
     */
    private void stopShooter() {
        shooterLeft.setVelocity(0);
        shooterRight.setVelocity(0);
    }

    /**
     * Checks if shooter is at target velocity (within tolerance).
     *
     * @param targetVelocity Target velocity to check against
     * @return true if both shooter motors are within tolerance
     */
    private boolean shooterAtSpeed(double targetVelocity) {
        double leftVel = shooterLeft.getVelocity();
        double rightVel = shooterRight.getVelocity();
        return Math.abs(leftVel - targetVelocity) < SHOOTER_TOLERANCE
                && Math.abs(rightVel - targetVelocity) < SHOOTER_TOLERANCE;
    }

    /**
     * Configures shooter PIDF coefficients for velocity control.
     */
    private void setShooterPIDF() {
        shooterLeft.setVelocityPIDFCoefficients(P, I, D, F);
        shooterRight.setVelocityPIDFCoefficients(P, I, D, F);
    }

    // ========== INTAKE METHODS ==========

    /**
     * Runs both intakes at specified power.
     *
     * @param power Motor power (-1.0 to 1.0)
     */
    private void runIntakes(double power) {
        intakeFront.setPower(power);
        intakeBack.setPower(power);
    }

    /**
     * Stops both intake motors.
     */
    private void stopIntakes() {
        intakeFront.setPower(0);
        intakeBack.setPower(0);
    }

    // ========== DRIVE METHODS ==========

    /**
     * Drives straight with heading correction, power normalization, and slow approach.
     * Automatically slows down for the final inches to improve consistency.
     *
     * @param inches  Distance to drive (negative = backward)
     * @param power   Maximum motor power (0.0 to 1.0)
     * @param heading Target heading to maintain (degrees)
     */
    private void driveStraight(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        int slowApproachTicks = (int) (SLOW_APPROACH_DISTANCE * TICKS_PER_INCH);
        double direction = Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()
                && Math.abs(frontLeft.getCurrentPosition()) < ticks
                && Math.abs(frontRight.getCurrentPosition()) < ticks) {

            // Calculate average position and remaining distance
            int currentTicks = (Math.abs(frontLeft.getCurrentPosition()) + Math.abs(frontRight.getCurrentPosition())) / 2;
            int ticksRemaining = ticks - currentTicks;

            // Slow down for final approach to improve stopping accuracy
            double currentPower = power;
            if (ticksRemaining < slowApproachTicks) {
                double rampFactor = (double) ticksRemaining / slowApproachTicks;
                currentPower = SLOW_APPROACH_POWER + (power - SLOW_APPROACH_POWER) * rampFactor;
            }

            // Calculate heading correction to drive straight
            double correction = headingCorrection(heading);

            // Apply power with heading correction
            double leftPower = (currentPower * direction) - correction;
            double rightPower = (currentPower * direction) + correction;

            // Normalize power to prevent values > 1.0
            double maxPower = Math.max(Math.abs(leftPower), Math.abs(rightPower));
            if (maxPower > 1.0) {
                leftPower /= maxPower;
                rightPower /= maxPower;
            }

            // Set motor powers
            frontLeft.setPower(leftPower);
            backLeft.setPower(leftPower);
            frontRight.setPower(rightPower);
            backRight.setPower(rightPower);
        }

        stopMotors();
        sleep(SETTLE_TIME_MS);  // Let robot settle after movement
    }

    /**
     * Strafes with heading correction, power normalization, and slow approach.
     * Positive inches = strafe right, negative = strafe left.
     *
     * @param inches  Distance to strafe (positive = right, negative = left)
     * @param power   Maximum motor power (0.0 to 1.0)
     * @param heading Target heading to maintain (degrees)
     */
    private void strafe(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        int slowApproachTicks = (int) (SLOW_APPROACH_DISTANCE * TICKS_PER_INCH);
        double dir = Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()) {
            // Calculate average position across all wheels
            double avg = (Math.abs(frontLeft.getCurrentPosition())
                    + Math.abs(frontRight.getCurrentPosition())
                    + Math.abs(backLeft.getCurrentPosition())
                    + Math.abs(backRight.getCurrentPosition())) / 4.0;

            if (avg >= ticks) break;

            double ticksRemaining = ticks - avg;

            // Slow down for final approach
            double currentPower = power;
            if (ticksRemaining < slowApproachTicks) {
                double rampFactor = ticksRemaining / slowApproachTicks;
                currentPower = SLOW_APPROACH_POWER + (power - SLOW_APPROACH_POWER) * rampFactor;
            }

            // Calculate heading correction
            double correction = headingCorrection(heading);

            // Mecanum strafe: diagonal pairs move same direction
            // FL and BR move together, FR and BL move together (opposite)
            double fl =  currentPower * dir - correction;
            double fr = -currentPower * dir + correction;
            double bl = -currentPower * dir - correction;
            double br =  currentPower * dir + correction;

            // Normalize power to keep all values <= 1.0
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
        sleep(SETTLE_TIME_MS);  // Let robot settle after movement
    }

    /**
     * Convenience method to strafe left.
     *
     * @param inches  Distance to strafe (positive value)
     * @param heading Target heading to maintain
     */
    private void strafeLeft(double inches, double heading) {
        strafe(-Math.abs(inches), DRIVE_POWER, heading);
    }

    /**
     * Convenience method to strafe right.
     *
     * @param inches  Distance to strafe (positive value)
     * @param heading Target heading to maintain
     */
    private void strafeRight(double inches, double heading) {
        strafe(Math.abs(inches), DRIVE_POWER, heading);
    }

    /**
     * Turns robot using encoder-based RUN_TO_POSITION mode.
     * Positive degrees = turn left (counterclockwise when viewed from above).
     *
     * @param degrees Degrees to turn (positive = left, negative = right)
     */
    private void turnDegrees(double degrees) {
        int turnCounts = (int) (degrees * COUNTS_PER_DEGREE);

        resetEncoders();

        // Set target positions: left wheels forward, right wheels backward = turn left
        frontLeft.setTargetPosition(turnCounts);
        backLeft.setTargetPosition(turnCounts);
        frontRight.setTargetPosition(-turnCounts);
        backRight.setTargetPosition(-turnCounts);

        // Switch to RUN_TO_POSITION mode
        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set turn speed for all motors
        frontLeft.setPower(TURN_SPEED);
        frontRight.setPower(TURN_SPEED);
        backLeft.setPower(TURN_SPEED);
        backRight.setPower(TURN_SPEED);

        // Wait until all motors reach their target
        while (opModeIsActive()
                && frontLeft.isBusy()
                && frontRight.isBusy()
                && backLeft.isBusy()
                && backRight.isBusy()) {
            idle();
        }

        stopMotors();
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sleep(SETTLE_TIME_MS);  // Let robot settle after turn
    }

    // ========== UTILITY METHODS ==========

    /**
     * Initializes all hardware components from the hardware map.
     * Configures motor directions, encoder modes, and default positions.
     */
    private void initHardware() {
        telemetry.addLine("Initializing hardware...");
        telemetry.update();

        // === Drive Motors ===
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");

        // Set motor directions (left side reversed for forward motion)
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Configure encoders and braking
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        setBrake(true);

        // === Shooter Motors ===
        shooterLeft = hardwareMap.get(DcMotorEx.class, "SL");
        shooterLeft.setDirection(DcMotor.Direction.REVERSE);
        shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);  // Coast when stopped
        shooterLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooterRight = hardwareMap.get(DcMotorEx.class, "SR");
        shooterRight.setDirection(DcMotor.Direction.FORWARD);
        shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);  // Coast when stopped
        shooterRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // === Intake Motors ===
        intakeFront = hardwareMap.get(DcMotor.class, "IF");
        intakeFront.setDirection(DcMotor.Direction.FORWARD);
        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeBack = hardwareMap.get(DcMotor.class, "IB");
        intakeBack.setDirection(DcMotor.Direction.FORWARD);
        intakeBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // === Trap Door Servo ===
        trapDoor = hardwareMap.get(Servo.class, "TD");
        trapDoor.setPosition(TRAP_DOOR_CLOSED);

        // === IMU (Inertial Measurement Unit) ===
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

    /**
     * Sets the run mode for all drive motors.
     *
     * @param mode The DcMotor.RunMode to set
     */
    private void setMotorMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    /**
     * Sets brake or coast behavior for all drive motors.
     *
     * @param brake true for brake mode, false for coast/float mode
     */
    private void setBrake(boolean brake) {
        DcMotor.ZeroPowerBehavior zpb = brake
                ? DcMotor.ZeroPowerBehavior.BRAKE
                : DcMotor.ZeroPowerBehavior.FLOAT;
        frontLeft.setZeroPowerBehavior(zpb);
        frontRight.setZeroPowerBehavior(zpb);
        backLeft.setZeroPowerBehavior(zpb);
        backRight.setZeroPowerBehavior(zpb);
    }

    /**
     * Stops all drive motors.
     */
    private void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /**
     * Resets drive motor encoders and sets them back to RUN_USING_ENCODER mode.
     */
    private void resetEncoders() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Gets the current robot heading from the IMU.
     *
     * @return Current yaw angle in degrees
     */
    private double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    /**
     * Calculates the heading correction needed to maintain target heading.
     * Uses proportional control with clamping.
     *
     * @param target Target heading in degrees
     * @return Correction value to apply to motor powers
     */
    private double headingCorrection(double target) {
        double error = AngleUnit.normalizeDegrees(target - getHeading());
        double correction = error * HEADING_KP;
        return Math.max(-MAX_CORRECTION, Math.min(MAX_CORRECTION, correction));
    }

    /**
     * Stops all motors (drive, shooter, and intakes).
     */
    private void stopAll() {
        stopMotors();
        stopShooter();
        stopIntakes();
    }
}
