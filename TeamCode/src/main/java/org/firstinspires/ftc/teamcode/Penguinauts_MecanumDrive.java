/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * This is the main TeleOp program for Team Penguinauts' mecanum drive robot.
 * This OpMode provides robot-relative control with the following features:
 *
 * - Left stick: Forward/backward and strafe left/right
 * - Right stick: Rotate left/right
 * - Left bumper: Slow mode (50% speed)
 * - Right bumper: Turbo mode (100% speed)
 * - Default speed: 75%
 *
 * Motor Configuration (as seen from behind the robot):
 *   Front Left  (port 0) - "front_left_drive"
 *   Front Right (port 1) - "front_right_drive"
 *   Back Left   (port 2) - "back_left_drive"
 *   Back Right  (port 3) - "back_right_drive"
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Penguinauts: Mecanum Drive", group="Penguinauts")
public class Penguinauts_MecanumDrive extends LinearOpMode {

    // Declare drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    
    private ElapsedTime runtime = new ElapsedTime();
    
    // Speed multipliers
    private static final double SLOW_MODE_MULTIPLIER = 0.5;
    private static final double NORMAL_MODE_MULTIPLIER = 0.75;
    private static final double TURBO_MODE_MULTIPLIER = 1.0;

    @Override
    public void runOpMode() {
        
        // Initialize the hardware variables
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        // Set motor directions
        // Left motors need to be reversed for mecanum drive
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Set motors to brake when power is zero
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Use encoders for better control (if connected)
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized - Team Penguinauts 32240");
        telemetry.addData("Controls", "Left stick: Drive/Strafe");
        telemetry.addData("", "Right stick: Rotate");
        telemetry.addData("", "L Bumper: Slow | R Bumper: Turbo");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Run until the driver presses STOP
        while (opModeIsActive()) {
            
            // Get joystick inputs
            // Note: Pushing stick forward gives negative value, so we negate it
            double axial = -gamepad1.left_stick_y;  // Forward/backward
            double lateral = gamepad1.left_stick_x;  // Strafe left/right
            double yaw = gamepad1.right_stick_x;     // Rotate left/right

            // Determine speed multiplier based on bumpers
            double speedMultiplier = NORMAL_MODE_MULTIPLIER;
            String driveMode = "Normal (75%)";
            
            if (gamepad1.left_bumper) {
                speedMultiplier = SLOW_MODE_MULTIPLIER;
                driveMode = "Slow (50%)";
            } else if (gamepad1.right_bumper) {
                speedMultiplier = TURBO_MODE_MULTIPLIER;
                driveMode = "Turbo (100%)";
            }

            // Calculate wheel powers using mecanum drive kinematics
            double frontLeftPower = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower = axial - lateral + yaw;
            double backRightPower = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 1.0
            double maxPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            maxPower = Math.max(maxPower, Math.abs(backLeftPower));
            maxPower = Math.max(maxPower, Math.abs(backRightPower));

            if (maxPower > 1.0) {
                frontLeftPower /= maxPower;
                frontRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }

            // Apply speed multiplier
            frontLeftPower *= speedMultiplier;
            frontRightPower *= speedMultiplier;
            backLeftPower *= speedMultiplier;
            backRightPower *= speedMultiplier;

            // Send calculated power to wheels
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

            // Display telemetry data
            telemetry.addData("Status", "Running - Team 32240");
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("Drive Mode", driveMode);
            telemetry.addData("", "");
            telemetry.addData("Joystick", "Axial: %.2f, Lateral: %.2f, Yaw: %.2f", axial, lateral, yaw);
            telemetry.addData("", "");
            telemetry.addData("Motor Power", "FL: %.2f, FR: %.2f", frontLeftPower, frontRightPower);
            telemetry.addData("", "BL: %.2f, BR: %.2f", backLeftPower, backRightPower);
            telemetry.update();
        }
    }
}

