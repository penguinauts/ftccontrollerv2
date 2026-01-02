/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * This is the field-relative TeleOp program for Team Penguinauts' mecanum drive robot.
 * Field-relative driving means the robot moves in the direction you push the joystick
 * relative to the field, not relative to the robot's orientation.
 *
 * Controls:
 * - Left stick: Drive in any direction (field-relative)
 * - Right stick: Rotate left/right
 * - A button: Reset field orientation (set current direction as forward)
 * - Left bumper: Robot-relative mode (like RC car)
 * - Right bumper: Slow mode (50% speed)
 * - Default speed: 75%
 *
 * Motor Configuration (as seen from behind the robot):
 *   Front Left  (port 0) - "front_left_drive"
 *   Front Right (port 1) - "front_right_drive"
 *   Back Left   (port 2) - "back_left_drive"
 *   Back Right  (port 3) - "back_right_drive"
 *
 * IMU Configuration:
 *   Control Hub IMU - "imu"
 */

package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name="Penguinauts: Field Relative Drive", group="Penguinauts")
public class Penguinauts_FieldRelativeDrive extends LinearOpMode {

    // Declare drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    
    // Declare IMU
    private IMU imu = null;
    
    private ElapsedTime runtime = new ElapsedTime();
    
    // Speed multipliers
    private static final double SLOW_MODE_MULTIPLIER = 0.5;
    private static final double NORMAL_MODE_MULTIPLIER = 0.75;

    @Override
    public void runOpMode() {
        
        // Initialize the hardware variables
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        
        // Initialize motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        // Set motor directions
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Set motors to brake when power is zero
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Use encoders for better control
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Initialize IMU
        imu = hardwareMap.get(IMU.class, "imu");
        
        // Define the orientation of the Control Hub on the robot
        // Adjust these values based on how your Control Hub is mounted
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = 
                RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = 
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
        
        RevHubOrientationOnRobot orientationOnRobot = 
                new RevHubOrientationOnRobot(logoDirection, usbDirection);
        
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        // Wait for the game to start
        telemetry.addData("Status", "Initialized - Team Penguinauts 32240");
        telemetry.addData("Mode", "FIELD RELATIVE DRIVE");
        telemetry.addData("", "");
        telemetry.addData("Controls", "Left stick: Drive (field-relative)");
        telemetry.addData("", "Right stick: Rotate");
        telemetry.addData("", "A: Reset field orientation");
        telemetry.addData("", "L Bumper: Robot-relative mode");
        telemetry.addData("", "R Bumper: Slow mode");
        telemetry.addData("", "");
        telemetry.addData("IMU Status", "Ready");
        telemetry.update();

        waitForStart();
        runtime.reset();
        
        // Reset IMU yaw to zero at start
        imu.resetYaw();

        // Run until the driver presses STOP
        while (opModeIsActive()) {
            
            // Get joystick inputs
            double axial = -gamepad1.left_stick_y;   // Forward/backward
            double lateral = gamepad1.left_stick_x;   // Strafe left/right
            double yaw = gamepad1.right_stick_x;      // Rotate left/right

            // Reset field orientation with A button
            if (gamepad1.a) {
                imu.resetYaw();
                telemetry.addData("Action", "Field orientation reset!");
            }

            // Determine drive mode and speed
            boolean robotRelative = gamepad1.left_bumper;
            double speedMultiplier = gamepad1.right_bumper ? SLOW_MODE_MULTIPLIER : NORMAL_MODE_MULTIPLIER;
            String driveMode = robotRelative ? "Robot-Relative" : "Field-Relative";
            driveMode += gamepad1.right_bumper ? " (Slow 50%)" : " (Normal 75%)";

            // Choose drive mode
            if (robotRelative) {
                // Robot-relative drive (like RC car)
                driveRobotRelative(axial, lateral, yaw, speedMultiplier);
            } else {
                // Field-relative drive
                driveFieldRelative(axial, lateral, yaw, speedMultiplier);
            }

            // Get current robot heading for telemetry
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
            double heading = orientation.getYaw(AngleUnit.DEGREES);

            // Display telemetry
            telemetry.addData("Status", "Running - Team 32240");
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("Drive Mode", driveMode);
            telemetry.addData("Robot Heading", "%.1f degrees", heading);
            telemetry.addData("", "");
            telemetry.addData("Joystick", "Axial: %.2f, Lateral: %.2f, Yaw: %.2f", axial, lateral, yaw);
            telemetry.update();
        }
    }

    /**
     * Drive the robot in field-relative mode
     */
    private void driveFieldRelative(double axial, double lateral, double yaw, double speedMultiplier) {
        // Get the current robot heading
        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the joystick input by the robot's heading
        double rotX = lateral * Math.cos(-botHeading) - axial * Math.sin(-botHeading);
        double rotY = lateral * Math.sin(-botHeading) + axial * Math.cos(-botHeading);

        // Calculate motor powers
        driveRobotRelative(rotY, rotX, yaw, speedMultiplier);
    }

    /**
     * Drive the robot in robot-relative mode
     */
    private void driveRobotRelative(double axial, double lateral, double yaw, double speedMultiplier) {
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
    }
}

