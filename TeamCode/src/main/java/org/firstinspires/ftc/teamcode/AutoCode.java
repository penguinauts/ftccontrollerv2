package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "EncoderForwardBackExample")
public class AutoCode extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight;

    // GoBilda Yellow Jacket + 96mm mecanum assumptions
    static final double TICKS_PER_REV     = 537.6;   // change if your motor is different [web:26]
    static final double WHEEL_DIAMETER_IN = 3.78;    // ~96mm wheel [web:31]
    static final double WHEEL_CIRCUM_IN   = Math.PI * WHEEL_DIAMETER_IN;
    static final double TICKS_PER_INCH    = TICKS_PER_REV / WHEEL_CIRCUM_IN; // [web:26]

    @Override
    public void runOpMode() throws InterruptedException {

        // --- Hardware setup ---
        frontLeft  = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeft   = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRight  = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("Ready to run");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Go forward 24 inches at 50% power
            driveForward(20, 0.5);

            sleep(500); // small pause so you can see the change

            // Go backward 24 inches at 50% power
            driveBackward(20, 0.5);
            sleep(500); //
            strafeLeft(20,0.5);
            sleep(500); //
            strafeRight(20,0.5);

        }
    }

    // ---------- Helpers ----------

    private void prepareEncoders() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Important: set target positions AFTER reset & BEFORE RUN_TO_POSITION. [web:26]
        // (so RUN_TO_POSITION mode is set after targets are assigned)
    }

    private void setRunToPosition() {
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void waitForMotors() {
        while (opModeIsActive()
                && (frontLeft.isBusy() || frontRight.isBusy()
                || backLeft.isBusy() || backRight.isBusy())) {

            telemetry.addData("FL", frontLeft.getCurrentPosition());
            telemetry.addData("FR", frontRight.getCurrentPosition());
            telemetry.addData("BL", backLeft.getCurrentPosition());
            telemetry.addData("BR", backRight.getCurrentPosition());
            telemetry.update();
        }

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    // ---------- Movement methods ----------

    public void driveForward(double inches, double power) {
        int ticks = (int) (inches * TICKS_PER_INCH);

        prepareEncoders();

        frontLeft.setTargetPosition(ticks);
        frontRight.setTargetPosition(ticks);
        backLeft.setTargetPosition(ticks);
        backRight.setTargetPosition(ticks);

        setRunToPosition();

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        waitForMotors();
    }

    public void driveBackward(double inches, double power) {
        int ticks = (int) (inches * TICKS_PER_INCH) * -1;

        prepareEncoders();

        frontLeft.setTargetPosition(ticks);
        frontRight.setTargetPosition(ticks);
        backLeft.setTargetPosition(ticks);
        backRight.setTargetPosition(ticks);

        setRunToPosition();

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        waitForMotors();
    }
    public void strafeRight(double inches, double power) {
        int ticks = (int) (inches * TICKS_PER_INCH);

        prepareEncoders();

        // Mecanum strafe pattern: + - - +
        frontLeft.setTargetPosition( ticks);
        frontRight.setTargetPosition(-ticks);
        backLeft.setTargetPosition(-ticks);
        backRight.setTargetPosition( ticks);

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        waitForMotors();
    }

    // 4) Strafe left X inches
    public void strafeLeft(double inches, double power) {
        int ticks = (int) (inches * TICKS_PER_INCH);

        prepareEncoders();

        // Opposite of strafe right: - + + -
        frontLeft.setTargetPosition(-ticks);
        frontRight.setTargetPosition( ticks);
        backLeft.setTargetPosition( ticks);
        backRight.setTargetPosition(-ticks);

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        waitForMotors();
    }
}
