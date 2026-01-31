package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Config
@Autonomous(name="Auto9BALLFront_BLUE", group="Auto")
@Disabled
public class Auto9BALLFrontBlue extends LinearOpMode {

    // ===== MOTORS =====
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    // ===== CONSTANTS =====
    static final double TICKS_PER_REV = 537.6;
    static final double WHEEL_DIAMETER_IN = 3.78;
    static final double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_IN);

    public static double HEADING_KP = 0.06;
    public static double MAX_CORRECTION = 0.3;

    private static final double COUNTS_PER_DEGREE = 10.0;
    private static final double TURN_SPEED = 0.4;

    // Shooter velocity (ticks/sec)
    public static double SHOOTER_VELOCITY_BALL1 = 1200;
    public static double SHOOTER_VELOCITY_BALL2 = 1225;
    public static double SHOOTER_VELOCITY_BALL3 = 1225;
    public static double SHOOTER_VELOCITY_BALL4 = 1250;
    public static double SHOOTER_VELOCITY_BALL5 = 1250;
    public static double SHOOTER_VELOCITY_BALL6 = 1250;
    public static double SHOOTER_VELOCITY_BALL7 = 1250;
    public static double SHOOTER_VELOCITY_BALL8 = 1250;
    public static double SHOOTER_VELOCITY_BALL9 = 1250;

    // BLUE MIRROR: flip signs vs Red
    public static double TURN_TO_INTAKE = -46;
    public static double TURN = +51;

    // Trap door positions (unchanged)
    public static double TRAP_DOOR_CLOSED = 1.0;
    public static double TRAP_DOOR_OPEN = 0.85;

    // Intake power
    public static double INTAKE_POWER = 1.0;

    // ===== INTAKES =====
    private DcMotor intakeFront = null;
    private DcMotor intakeBack = null;
    public static double P = 8;

    public static double I = 0;
    public static double D = 0;
    public static double F = 12.7;

    // ===== TRAP DOOR SERVO =====
    private Servo trapDoor = null;

    // ===== SHOOTER (DUAL MOTORS) =====
    private DcMotorEx shooterLeft;   // SL
    private DcMotorEx shooterRight;  // SR

    @Override
    public void runOpMode() {

        // ===== HARDWARE MAP =====
        frontLeft  = hardwareMap.get(DcMotor.class, "FL"); //front_left_drive
        frontRight = hardwareMap.get(DcMotor.class, "FR"); //front_right_drive
        backLeft   = hardwareMap.get(DcMotor.class, "BL"); //back_left_drive
        backRight  = hardwareMap.get(DcMotor.class, "BR"); //back_right_drive

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        setBrake(true);

        // Shooter Left
        shooterLeft = hardwareMap.get(DcMotorEx.class, "SL");
        shooterLeft.setDirection(DcMotor.Direction.REVERSE);
        shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Shooter Right
        shooterRight = hardwareMap.get(DcMotorEx.class, "SR");
        shooterRight.setDirection(DcMotor.Direction.FORWARD);
        shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Front intake
        intakeFront = hardwareMap.get(DcMotor.class, "IF");
        intakeFront.setDirection(DcMotor.Direction.FORWARD);
        intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Back intake
        intakeBack = hardwareMap.get(DcMotor.class, "IB");
        intakeBack.setDirection(DcMotor.Direction.FORWARD);
        intakeBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //set PIDF
        setShooterPIDF();

        // Trap door servo
        trapDoor = hardwareMap.get(Servo.class, "TD");
        trapDoor.setPosition(TRAP_DOOR_CLOSED);

        // ===== IMU =====
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        ));

        telemetry.addLine("Ready (BLUE)");
        telemetry.update();

        waitForStart();
        if (!opModeIsActive()) return;

        imu.resetYaw();

        // =========================
        // BLUE AUTO (MIRROR OF RED)
        // =========================

        driveStraight(-20, 1, 1, 0);

        // Shoot 3
        startShooter(SHOOTER_VELOCITY_BALL1);
        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL1)) idle();
        intakeFront.setPower(1); intakeBack.setPower(1); sleep(600);

        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL2)) idle();
        intakeFront.setPower(1); intakeBack.setPower(1); sleep(600);

        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL3)) idle();
        intakeFront.setPower(1); intakeBack.setPower(1); sleep(600);

        stopShooter();
        driveStraight(-10, 1, 1, 0);
        intakeFront.setPower(0); intakeBack.setPower(0);

        // MIRROR TURN
        turnDegrees(TURN_TO_INTAKE);

        // MIRROR STRAFE RIGHT -> LEFT
        strafeRight(9, 1, 0);

        intakeFront.setPower(1);
        intakeBack.setPower(-0.5);

        driveStraight(33.5, 0.68, 0.68, 0);
        driveStraight(-30, 1, 1, 0);

        // MIRROR TURN
        turnDegrees(TURN);

        driveStraight(10, 1, 1, 0);

        // Shoot 3
        startShooter(SHOOTER_VELOCITY_BALL4);
        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL4)) idle();
        intakeFront.setPower(1); intakeBack.setPower(1); sleep(600);

        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL5)) idle();
        intakeFront.setPower(1); intakeBack.setPower(1); sleep(600);

        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL6)) idle();
        intakeFront.setPower(1); intakeBack.setPower(1); sleep(600);

        stopShooter();
//        driveStraight(-10, 1, 1, 0);
        intakeFront.setPower(0); intakeBack.setPower(0);

        // Collect / move to next
        intakeFront.setPower(1);
        intakeBack.setPower(-0.5);

        // MIRROR TURN 45 -> -45
        turnDegrees(-53);

        // MIRROR STRAFE RIGHT -> LEFT
        strafeRight(30, 1, 0);
//
//        // MIRROR STRAFE RIGHT -> LEFT
//        strafeLeft(12.7, 1, 0);

        driveStraight(33, 0.68, 0.68, 0);
        driveStraight(-30, 1, 1, 0);

        // MIRROR DIAGONAL RightBackward -> LeftBackward
//        driveDiagonalRightForward(-24, 1, 0);
        strafeLeft(30,1,0);

        // MIRROR TURN -55 -> +55
        turnDegrees(65);

        // MIRROR STRAFE LEFT -> RIGHT
//        strafeLeft(16, 1, 0);
        driveStraight(5,1,1,0);

        // Shoot 3
        startShooter(SHOOTER_VELOCITY_BALL7);
        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL7)) idle();
        intakeFront.setPower(1); intakeBack.setPower(1); sleep(600);

        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL8)) idle();
        intakeFront.setPower(1); intakeBack.setPower(1); sleep(600);

        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL9)) idle();
        intakeFront.setPower(1); intakeBack.setPower(1); sleep(600);

        stopShooter();
        intakeFront.setPower(0); intakeBack.setPower(0);
        strafeRight(15,1,0);
    }


    // ================= MOTOR UTIL =================
    private void setMotorMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    private void setBrake(boolean brake) {
        DcMotor.ZeroPowerBehavior zpb = brake ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT;
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

            double max = Math.max(1, Math.max(Math.abs(fl),
                    Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br)))));

            frontLeft.setPower(fl / max);
            frontRight.setPower(fr / max);
            backLeft.setPower(bl / max);
            backRight.setPower(br / max);
        }
        stopMotors();
    }

    public void strafeRight(double inches, double power, double heading) {
        strafe(-Math.abs(inches), power, heading);
    }

    public void strafeLeft(double inches, double power, double heading) {
        strafe(Math.abs(inches), power, heading);
    }

    // ================= DIAGONALS =================
    public void driveDiagonalLeftBackward(double inches, double power, double heading) {
        int ticks = (int) (Math.abs(inches) * TICKS_PER_INCH);
        double dir = -Math.signum(inches); // backward

        resetEncoders();

        while (opModeIsActive()) {
            double avg = (Math.abs(frontLeft.getCurrentPosition()) + Math.abs(backRight.getCurrentPosition())) / 2.0;
            if (avg >= ticks) break;

            double correction = headingCorrection(heading);

            double fl = (power * dir) - correction;
            double fr = 0 + correction;
            double bl = 0 - correction;
            double br = (power * dir) + correction;

            double max = Math.max(1, Math.max(Math.abs(fl),
                    Math.max(Math.abs(fr), Math.max(Math.abs(bl), Math.abs(br)))));

            frontLeft.setPower(fl / max);
            frontRight.setPower(fr / max);
            backLeft.setPower(bl / max);
            backRight.setPower(br / max);
        }
        stopMotors();
    }
    public void driveDiagonalRightForward(double inches, double power, double heading) {
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

    // (Keep your other diagonal methods here if you use them elsewhere)

    // ================= SHOOTER =================
    public void startShooter(double velocity) {
        shooterLeft.setVelocity(velocity);
        shooterRight.setVelocity(velocity);
    }

    public void stopShooter() {
        shooterLeft.setVelocity(0);
        shooterRight.setVelocity(0);
    }

    public boolean shooterAtSpeed(double targetVelocity) {
        double leftVel = shooterLeft.getVelocity();
        double rightVel = shooterRight.getVelocity();

        return Math.abs(leftVel - targetVelocity) < 150
                && Math.abs(rightVel - targetVelocity) < 150;
    }

    private void setShooterPIDF() {
        shooterLeft.setVelocityPIDFCoefficients(P, I, D, F);
        shooterRight.setVelocityPIDFCoefficients(P, I, D, F);
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
