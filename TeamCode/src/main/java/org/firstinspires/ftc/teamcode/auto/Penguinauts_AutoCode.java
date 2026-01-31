package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Config
@Autonomous(name = "FIXED_Mecanum_Encoder_Gyro_Auto")
@Disabled
public class Penguinauts_AutoCode extends LinearOpMode {

    // ===== MOTORS =====
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    // ===== CONSTANTS =====
    static final double TICKS_PER_REV = 537.6;
    static final double WHEEL_DIAMETER_IN = 3.78;
    static final double TICKS_PER_INCH =
            TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_IN);

    public static double HEADING_KP = 0.06;
    public static double MAX_CORRECTION = 0.3;
    public static double LEFT_MOTOR_POWER = 0.5;
    public static double RIGHT_MOTOR_POWER = 0.5;
    public static double DRIVE_DISTANCE = 15;

    private static final double COUNTS_PER_DEGREE = 10.0;
    private static final double TURN_SPEED = 0.4;

    @Override
    public void runOpMode() {

        // ===== HARDWARE MAP =====
        frontLeft  = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeft   = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRight  = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        setBrake(true);

        // ===== IMU =====
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        ));

        telemetry.addLine("Ready");
        telemetry.update();

        waitForStart();

        if (!opModeIsActive()) return;

        imu.resetYaw();
        double heading = getHeading();
        driveStraight(20, 1, 0.4,0);
        sleep(200);
        turnDegrees(360);
        sleep(200);
        driveStraight(-20, 0.4, 0.4,0);
        sleep(200);
        strafeRight(20,0.4,0);
        sleep(200);
        strafeLeft(20,0.4,0);



    }

    // ================= MOTOR UTIL =================

    private void setMotorMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    private void setBrake(boolean brake) {
        DcMotor.ZeroPowerBehavior zpb =
                brake ? DcMotor.ZeroPowerBehavior.BRAKE
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

    // ================= GYRO =================

    private double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    private double headingCorrection(double target) {
        double error = AngleUnit.normalizeDegrees(target - getHeading());
        double correction = error * HEADING_KP;
        return Math.max(-MAX_CORRECTION, Math.min(MAX_CORRECTION, correction));
    }

    // ================= DRIVE =================

    public void driveStraight(double inches, double leftPower, double rightPower, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);

        resetEncoders();

        leftPower *= Math.signum(inches);
        rightPower *= Math.signum(inches);

        while (opModeIsActive()
                && Math.abs(frontLeft.getCurrentPosition()) < ticks
                && Math.abs(frontRight.getCurrentPosition()) < ticks) {

            double correction = headingCorrection(heading);

            frontLeft.setPower(leftPower - correction);
            backLeft.setPower(leftPower - correction);
            frontRight.setPower(rightPower + correction);
            backRight.setPower(rightPower + correction);
        }

        stopMotors();
    }

    // ================= STRAFE =================

    public void strafe(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double dir = Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()) {

            double avg =
                    (Math.abs(frontLeft.getCurrentPosition())
                            + Math.abs(frontRight.getCurrentPosition())
                            + Math.abs(backLeft.getCurrentPosition())
                            + Math.abs(backRight.getCurrentPosition())) / 4.0;

            if (avg >= ticks) break;

            double correction = headingCorrection(heading);

            double fl =  power * dir - correction;
            double fr = -power * dir + correction;
            double bl = -power * dir - correction;
            double br =  power * dir + correction;

            double max = Math.max(1,
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

    public void strafeLeft(double inches, double power, double heading) {
        strafe(-Math.abs(inches), power, heading);
    }

    public void strafeRight(double inches, double power, double heading) {
        strafe(Math.abs(inches), power, heading);
    }
    // ================= DIAGONAL (RIGHT-FORWARD) =================

    public void driveDiagonalRight(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double dir = Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()) {

            double avg =
                    (Math.abs(frontLeft.getCurrentPosition())
                            + Math.abs(backRight.getCurrentPosition())) / 2.0;

            if (avg >= ticks) break;

            double correction = headingCorrection(heading);

            // Diagonal right-forward
            double fl = (power * dir) - correction;
            double fr = 0 + correction;
            double bl = 0 - correction;
            double br = (power * dir) + correction;

            double max = Math.max(1,
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
    // ================= DIAGONAL (LEFT-FORWARD) =================

    public void driveDiagonalLeft(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double dir = Math.signum(inches);

        resetEncoders();

        while (opModeIsActive()) {

            double avg =
                    (Math.abs(frontRight.getCurrentPosition())
                            + Math.abs(backLeft.getCurrentPosition())) / 2.0;

            if (avg >= ticks) break;

            double correction = headingCorrection(heading);

            // Diagonal left-forward
            double fl = 0 - correction;
            double fr = (power * dir) + correction;
            double bl = (power * dir) - correction;
            double br = 0 + correction;

            double max = Math.max(1,
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
    // ================= DIAGONAL (LEFT-BACKWARD) =================

    public void driveDiagonalLeftBack(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double dir = -Math.signum(inches); // backward

        resetEncoders();

        while (opModeIsActive()) {

            double avg =
                    (Math.abs(frontRight.getCurrentPosition())
                            + Math.abs(backLeft.getCurrentPosition())) / 2.0;

            if (avg >= ticks) break;

            double correction = headingCorrection(heading);

            double fl = 0 - correction;
            double fr = (power * dir) + correction;
            double bl = (power * dir) - correction;
            double br = 0 + correction;

            double max = Math.max(1,
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
// ================= DIAGONAL (RIGHT-BACKWARD) =================

    public void driveDiagonalRightBack(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double dir = -Math.signum(inches); // backward

        resetEncoders();

        while (opModeIsActive()) {

            double avg =
                    (Math.abs(frontLeft.getCurrentPosition())
                            + Math.abs(backRight.getCurrentPosition())) / 2.0;

            if (avg >= ticks) break;

            double correction = headingCorrection(heading);

            double fl = (power * dir) - correction;
            double fr = 0 + correction;
            double bl = 0 - correction;
            double br = (power * dir) + correction;

            double max = Math.max(1,
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


    // ================= TURN =================

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
}
