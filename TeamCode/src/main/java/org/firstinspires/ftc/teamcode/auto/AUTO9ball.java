package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Config
@Autonomous
public class AUTO9ball extends LinearOpMode {

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
    public static double P = 8;

    public static double I = 0;
    public static double D = 0;
    public static double F = 12.7;

    private static final double COUNTS_PER_DEGREE = 10.0;
    private static final double TURN_SPEED = 0.4;
    // Shooter velocity (ticks/sec)
    public static double SHOOTER_VELOCITY_FRONT = 1680;
    public static double SHOOTER_VELOCITY_BALL1 = 1550;
    public static double SHOOTER_VELOCITY_BALL2 = 1550;
    public static double SHOOTER_VELOCITY_BALL3 = 1550;
    public static double SHOOTER_VELOCITY_BALL4 = 1550;
    public static double SHOOTER_VELOCITY_BALL5 = 1550;
    public static double SHOOTER_VELOCITY_BALL6 = 1550;

    // Intake power
    public static double INTAKE_POWER = 1.0;

    // Trap door positions
    public static double TRAP_DOOR_CLOSED = 1.0;
    public static double TRAP_DOOR_OPEN = 0.85;
    public static double TURN = -67.5;
    // ===== SHOOTER (SINGLE MOTOR) =====
    private DcMotorEx shooter = null;

    // ===== INTAKES =====
    private DcMotor intakeFront = null;
    private DcMotor intakeBack = null;

    // ===== TRAP DOOR SERVO =====
    private Servo trapDoor = null;

    // ===== SHOOTER (DUAL MOTORS, SINGLE SYSTEM) =====
    private DcMotorEx shooterLeft = null;   // SL
    private DcMotorEx shooterRight = null;  // SR



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

// Trap door servo
        trapDoor = hardwareMap.get(Servo.class, "TD");
        trapDoor.setPosition(TRAP_DOOR_CLOSED);


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

//        ↗ Right-Forward     FL +   BR +
//        ↖ Left-Forward      FR +   BL +
//
//        ↘ Right-Backward    FL −   BR −
//        ↙ Left-Backward     FR −   BL −


        imu.resetYaw();
        double heading = getHeading();
        driveStraight(3, 1, 1,0);
        sleep(100);
        turnDegrees(29);


// Wait until BOTH motors are ready
        startShooter(SHOOTER_VELOCITY_BALL1);

// wait for shooter to reach speed
        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL1)) {
            idle();
        }
        intakeFront.setPower(1);
        intakeBack.setPower(1);
        sleep(600);
        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL2)) {
            idle();
        }
        intakeFront.setPower(1);
        intakeBack.setPower(1);
        sleep(600);
        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL3)) {
            idle();
        }
        intakeFront.setPower(1);
        intakeBack.setPower(1);
        sleep(600);
        stopShooter();
        intakeFront.setPower(0);
        intakeBack.setPower(0);



        driveDiagonalRightForward(21, 1,0);
        turnDegrees(68);
//        driveStraight(19, 1, 0.26,0);
//        sleep(100);
        strafeLeft(8.75,1,0);
        intakeFront.setPower(1);
        intakeBack.setPower(-0.3);
        sleep(80);
        driveStraight(26, 0.70, 0.75,0);
        driveStraight(-22, 1, 1,0);
        driveDiagonalLeftBackward(32, 1, 0);
        turnDegrees(TURN);
        driveStraight(-2.5, 1, 1,0);
        startShooter(SHOOTER_VELOCITY_BALL1);

// wait for shooter to reach speed
        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL4)) {
            idle();
        }
        intakeFront.setPower(1);
        intakeBack.setPower(1);
        sleep(600);
        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL5)) {
            idle();
        }
        intakeFront.setPower(1);
        intakeBack.setPower(1);
        sleep(600);
        while (opModeIsActive() && !shooterAtSpeed(SHOOTER_VELOCITY_BALL6)) {
            idle();
        }
        intakeFront.setPower(1);
        intakeBack.setPower(1);
        sleep(600);
        stopShooter();
        intakeFront.setPower(0);
        intakeBack.setPower(0);

//


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

    public void strafeRight(double inches, double power, double heading) {
        strafe(-Math.abs(inches), power, heading);
    }

    public void strafeLeft(double inches, double power, double heading) {
        strafe(Math.abs(inches), power, heading);
    }
    // ================= DIAGONAL (RIGHT-FORWARD) =================

    public void driveDiagonalLeftForward(double inches, double power, double heading) {
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
    // ================= DIAGONAL (LEFT-BACKWARD) =================

    public void driveDiagonalRightBackward(double inches, double power, double heading) {
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

    public void driveDiagonalLeftBackward(double inches, double power, double heading) {
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



    public void frontIntakeIn() {
        intakeFront.setPower(INTAKE_POWER);
    }

    public void frontIntakeOut() {
        intakeFront.setPower(-INTAKE_POWER);
    }

    public void stopFrontIntake() {
        intakeFront.setPower(0);
    }

    public void backIntakeIn() {
        intakeBack.setPower(INTAKE_POWER);
    }

    public void backIntakeOut() {
        intakeBack.setPower(-INTAKE_POWER);
    }

    public void stopBackIntake() {
        intakeBack.setPower(0);
    }


    public void openTrapDoor() {
        trapDoor.setPosition(TRAP_DOOR_OPEN);
    }

    public void closeTrapDoor() {
        trapDoor.setPosition(TRAP_DOOR_CLOSED);
    }
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
