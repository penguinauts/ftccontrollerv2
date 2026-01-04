package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Config
@Autonomous(name = "FIXED_Mecanum_Encoder_Gyro_Auto")
public class AutoCode extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    // ===== ROBOT CONSTANTS =====
    static final double TICKS_PER_REV = 537.6;
    static final double WHEEL_DIAMETER_IN = 3.78;
    static final double TICKS_PER_INCH =
            TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_IN);

    public static double HEADING_KP = 0.06;
    public static double MAX_CORRECTION = 0.3;
    public static double LEFT_MOTOR_POWER = 0.5;
    public static double RIGHT_MOTOR_POWER = 0.5;
    public static double DRIVE_DISTANCE = 15;
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;


    // ⚙️ ADJUST THIS VALUE TO CALIBRATE YOUR ROBOT! ⚙️
    // Start with 10.0, then adjust based on test results
    // If robot turns too far: DECREASE this value
    // If robot turns too little: INCREASE this value
    private static final double COUNTS_PER_DEGREE = 10.0;

    // Other constants (from AutoTemplate)
    private static final double COUNTS_PER_MOTOR_REV = 537.7;
    private static final double DRIVE_GEAR_REDUCTION = 1.0;
    private static final double WHEEL_DIAMETER_INCHES = 4.0;
    private static final double TURN_SPEED = 0.4;


    @Override
    public void runOpMode() {

        // ===== MOTORS =====
        frontLeft  = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeft   = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRight  = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);


        setBrake(true);
        setRunUsingEncoders();

        // ===== IMU =====
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )));   // make sure this matches how the hub is actually mounted [web:17]

        telemetry.addLine("IMU Calibrating...");
        telemetry.update();

        telemetry.addLine("Ready");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            imu.resetYaw();                        // zero heading at start [web:3]
            double heading = getHeading();

            driveStraight(DRIVE_DISTANCE, LEFT_MOTOR_POWER, RIGHT_MOTOR_POWER, heading);
            sleep(100);
            strafeRight(10, 0.5, heading);
            sleep(100);
            strafeLeft(10, 0.5, heading);
            sleep(100);
            // Test 1: Full 360-degree rotation
            telemetry.addLine("🔄 TEST 1: 360° ROTATION");
            telemetry.addLine("Robot should return to EXACT starting position");
            telemetry.addLine("Press STOP to abort");
            telemetry.update();
            sleep(3000);  // Give time to prepare

            turnDegrees(360);

            telemetry.addLine("✅ 360° turn complete!");
            telemetry.addLine();
            telemetry.addLine("Did robot return to exact starting position?");
            telemetry.addLine("- YES: Calibration good! ✅");
            telemetry.addLine("- Turned too far: DECREASE COUNTS_PER_DEGREE");
            telemetry.addLine("- Turned too little: INCREASE COUNTS_PER_DEGREE");
            telemetry.update();
            sleep(5000);

            // Test 2: 90-degree right turn
            telemetry.addLine("🔄 TEST 2: 90° RIGHT TURN");
            telemetry.update();
            sleep(2000);

            turnDegrees(90);

            telemetry.addLine("✅ 90° right turn complete!");
            telemetry.addLine("Check if angle is accurate");
            telemetry.update();
            sleep(3000);

            // Test 3: 90-degree left turn (back to start)
            telemetry.addLine("🔄 TEST 3: 90° LEFT TURN");
            telemetry.addLine("(Should return to original orientation)");
            telemetry.update();
            sleep(2000);

            turnDegrees(-90);

            telemetry.addLine("✅ 90° left turn complete!");
            telemetry.addLine("Back to starting orientation?");
            telemetry.update();
            sleep(3000);

            // Test 4: 180-degree turn
            telemetry.addLine("🔄 TEST 4: 180° TURN");
            telemetry.addLine("(Turn around)");
            telemetry.update();
            sleep(2000);

            turnDegrees(180);

            telemetry.addLine("✅ 180° turn complete!");
            telemetry.addLine("Robot should face opposite direction");
            telemetry.update();
            sleep(3000);

            // Test 5: -180-degree turn (back to start)
            telemetry.addLine("🔄 TEST 5: 180° RETURN");
            telemetry.update();
            sleep(2000);

            turnDegrees(-180);

            // Final results
            telemetry.clear();
            telemetry.addLine("🏁 CALIBRATION TESTS COMPLETE!");
            telemetry.addLine();
            telemetry.addData("COUNTS_PER_DEGREE used", "%.2f", COUNTS_PER_DEGREE);
            telemetry.addLine();
            telemetry.addLine("NEXT STEPS:");
            telemetry.addLine("1. Check if all turns were accurate");
            telemetry.addLine("2. If not, adjust COUNTS_PER_DEGREE");
            telemetry.addLine("3. Run test again");
            telemetry.addLine("4. When accurate, copy value to AutoTemplate");
            telemetry.addLine();
            telemetry.addLine("See TURN_CALIBRATION_GUIDE.md for help");
            telemetry.update();

            sleep(100);
        }
    }

    // ================= ENCODER CORE =================

    private void resetEncoders() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void setRunUsingEncoders() {
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void setRunToPosition() {
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void setBrake(boolean brake) {
        DcMotor.ZeroPowerBehavior zpb =
                brake ? DcMotor.ZeroPowerBehavior.BRAKE :
                        DcMotor.ZeroPowerBehavior.FLOAT;

        frontLeft.setZeroPowerBehavior(zpb);
        frontRight.setZeroPowerBehavior(zpb);
        backLeft.setZeroPowerBehavior(zpb);
        backRight.setZeroPowerBehavior(zpb);
    }

    // ================= GYRO =================

    private double getHeading() {
        // Yaw angle of robot in degrees, -180..180 [web:3][web:6]
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    private double headingCorrection(double target) {
        double error = angleWrap(target - getHeading());
        double correction = error * HEADING_KP;
        return Math.max(-MAX_CORRECTION, Math.min(MAX_CORRECTION, correction));
    }

    // Normalize angle to -180..180 [web:11]
    public double angleWrap(double degrees) {
        degrees = AngleUnit.normalizeDegrees(degrees);
        if (degrees > 180) degrees -= 360;
        if (degrees <= -180) degrees += 360;
        return degrees;
    }

    // ================= MOVEMENT =================

    public void driveStraight(double inches, double leftpower, double rightpower, double heading) {
        int ticks = (int)(Math.abs(inches) * TICKS_PER_INCH);

        resetEncoders();
        setRunUsingEncoders();

        leftpower = Math.abs(leftpower) * Math.signum(inches);
        rightpower = Math.abs(rightpower) * Math.signum(inches);

        while (opModeIsActive() &&
                Math.abs(frontLeft.getCurrentPosition()) < ticks &&
                Math.abs(frontRight.getCurrentPosition()) < ticks) {

            double correction = headingCorrection(heading);

            frontLeft.setPower(leftpower - correction);
            backLeft.setPower(leftpower - correction);
            frontRight.setPower(rightpower + correction);
            backRight.setPower(rightpower + correction);

            telemetry.addData("Heading", getHeading());
            telemetry.addData("Correction", correction);
            telemetry.update();
        }

        stopMotors();
    }

    public void strafe(double inches, double power, double heading) {

        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double direction = Math.signum(inches);
        power = Math.abs(power);

        resetEncoders();
        setRunUsingEncoders();

        while (opModeIsActive()) {

            double avgEncoder =
                    (Math.abs(frontLeft.getCurrentPosition()) +
                            Math.abs(frontRight.getCurrentPosition()) +
                            Math.abs(backLeft.getCurrentPosition()) +
                            Math.abs(backRight.getCurrentPosition())) / 4.0;

            if (avgEncoder >= ticks) break;

            double correction = headingCorrection(heading);

            double fl = ( power * direction) - correction;
            double fr = (-power * direction) + correction;
            double bl = (-power * direction) - correction;
            double br = ( power * direction) + correction;

            double max = Math.max(1.0,
                    Math.max(Math.abs(fl),
                            Math.max(Math.abs(fr),
                                    Math.max(Math.abs(bl), Math.abs(br)))));

            frontLeft.setPower(fl / max);
            frontRight.setPower(fr / max);
            backLeft.setPower(bl / max);
            backRight.setPower(br / max);

            telemetry.addData("Target (ticks)", ticks);
            telemetry.addData("Avg Encoder", avgEncoder);
            telemetry.addData("Heading", getHeading());
            telemetry.addData("Correction", correction);
            telemetry.update();

            idle();
        }

        stopMotors();
    }

    public void strafeLeft(double inches, double power, double heading) {
        strafe(-Math.abs(inches), power, heading);
    }

    public void strafeRight(double inches, double power, double heading) {
        strafe(Math.abs(inches), power, heading);
    }


    private void setMotorMode(DcMotor.RunMode mode) {
        frontLeftDrive.setMode(mode);
        frontRightDrive.setMode(mode);
        backLeftDrive.setMode(mode);
        backRightDrive.setMode(mode);
    }
    // ================= TURN TO HEADING =================
    private void turnDegrees(double degrees) {
        if (!opModeIsActive()) return;

        telemetry.addData("⚙️ Turning", "%.1f degrees...", degrees);
        telemetry.update();

        // Calculate encoder counts needed for this turn
        int turnCounts = (int)(degrees * COUNTS_PER_DEGREE);

        // For turning in place:
        // - Left motors move forward for clockwise turn (positive)
        // - Right motors move backward for clockwise turn (negative)
        int flTarget = frontLeftDrive.getCurrentPosition() + turnCounts;
        int frTarget = frontRightDrive.getCurrentPosition() - turnCounts;
        int blTarget = backLeftDrive.getCurrentPosition() + turnCounts;
        int brTarget = backRightDrive.getCurrentPosition() - turnCounts;

        // Set target positions
        frontLeftDrive.setTargetPosition(flTarget);
        frontRightDrive.setTargetPosition(frTarget);
        backLeftDrive.setTargetPosition(blTarget);
        backRightDrive.setTargetPosition(brTarget);

        // Switch to RUN_TO_POSITION mode
        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set motor power
        frontLeftDrive.setPower(Math.abs(TURN_SPEED));
        frontRightDrive.setPower(Math.abs(TURN_SPEED));
        backLeftDrive.setPower(Math.abs(TURN_SPEED));
        backRightDrive.setPower(Math.abs(TURN_SPEED));

        // Wait until motors reach target
        while (opModeIsActive() &&
                (frontLeftDrive.isBusy() && frontRightDrive.isBusy() &&
                        backLeftDrive.isBusy() && backRightDrive.isBusy())) {

            // Show progress
            telemetry.addData("⚙️ Turning", "%.1f degrees...", degrees);
            telemetry.addData("FL", "Target: %d, Current: %d", flTarget, frontLeftDrive.getCurrentPosition());
            telemetry.addData("FR", "Target: %d, Current: %d", frTarget, frontRightDrive.getCurrentPosition());
            telemetry.addData("BL", "Target: %d, Current: %d", blTarget, backLeftDrive.getCurrentPosition());
            telemetry.addData("BR", "Target: %d, Current: %d", brTarget, backRightDrive.getCurrentPosition());
            telemetry.update();
        }

        // Stop motors
        stopMotors();

        // Switch back to RUN_USING_ENCODER mode
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    // ================= UTIL =================

    private void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
