/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * This is a motor testing OpMode for Team Penguinauts.
 * Use this to verify that all motors are connected correctly and spinning in the right direction.
 *
 * Controls:
 * - X button: Test front left motor
 * - Y button: Test front right motor
 * - A button: Test back left motor
 * - B button: Test back right motor
 * - DPAD UP: Test all motors forward
 * - DPAD DOWN: Test all motors backward
 *
 * Each motor will run at 50% power when its button is pressed.
 * Watch the wheels and verify they spin in the correct direction.
 */

package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Penguinauts: Motor Test", group="Penguinauts")
public class Penguinauts_MotorTest extends LinearOpMode {

    // Declare drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    
    private ElapsedTime runtime = new ElapsedTime();
    
    private static final double TEST_POWER = 0.5;

    @Override
    public void runOpMode() {
        
        // Initialize the hardware variables
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        // Set motor directions (standard configuration)
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Set motors to brake when power is zero
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wait for the game to start
        telemetry.addData("Status", "MOTOR TEST - Team Penguinauts 32240");
        telemetry.addData("", "");
        telemetry.addData("Instructions", "Press buttons to test motors:");
        telemetry.addData("", "X = Front Left");
        telemetry.addData("", "Y = Front Right");
        telemetry.addData("", "A = Back Left");
        telemetry.addData("", "B = Back Right");
        telemetry.addData("", "DPAD UP = All Forward");
        telemetry.addData("", "DPAD DOWN = All Backward");
        telemetry.addData("", "");
        telemetry.addData("Ready", "Press START to begin");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Run until the driver presses STOP
        while (opModeIsActive()) {
            
            // Default all motors to zero power
            double flPower = 0;
            double frPower = 0;
            double blPower = 0;
            double brPower = 0;
            String activeTest = "None - Press a button";

            // Test individual motors
            if (gamepad1.x) {
                flPower = TEST_POWER;
                activeTest = "Front Left Motor (X)";
            } else if (gamepad1.y) {
                frPower = TEST_POWER;
                activeTest = "Front Right Motor (Y)";
            } else if (gamepad1.a) {
                blPower = TEST_POWER;
                activeTest = "Back Left Motor (A)";
            } else if (gamepad1.b) {
                brPower = TEST_POWER;
                activeTest = "Back Right Motor (B)";
            } 
            // Test all motors together
            else if (gamepad1.dpad_up) {
                flPower = frPower = blPower = brPower = TEST_POWER;
                activeTest = "All Motors Forward (DPAD UP)";
            } else if (gamepad1.dpad_down) {
                flPower = frPower = blPower = brPower = -TEST_POWER;
                activeTest = "All Motors Backward (DPAD DOWN)";
            }

            // Set motor powers
            frontLeftDrive.setPower(flPower);
            frontRightDrive.setPower(frPower);
            backLeftDrive.setPower(blPower);
            backRightDrive.setPower(brPower);

            // Display telemetry
            telemetry.addData("Status", "MOTOR TEST - Team 32240");
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("", "");
            telemetry.addData("Active Test", activeTest);
            telemetry.addData("Test Power", "%.1f%%", TEST_POWER * 100);
            telemetry.addData("", "");
            telemetry.addData("Motor Powers", "FL: %.2f, FR: %.2f", flPower, frPower);
            telemetry.addData("", "BL: %.2f, BR: %.2f", blPower, brPower);
            telemetry.addData("", "");
            telemetry.addData("Expected Behavior", "When pressing DPAD UP:");
            telemetry.addData("", "All wheels should spin FORWARD");
            telemetry.addData("", "If not, check motor directions!");
            telemetry.update();
        }
        
        // Stop all motors
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);
    }
}

