/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * This is the field-relative TeleOp program for Team Penguinauts' mecanum drive robot.
 * Field-relative driving means the robot moves in the direction you push the joystick
 * relative to the field, not relative to the robot's orientation.
 *
 * DRIVE CONTROLS:
 * - Left stick: Drive in any direction (field-relative)
 * - Right stick: Rotate left/right
 * - A button: Reset field orientation (set current direction as forward)
 * - Drive speed: 63% (fixed)
 *
 * SHOOTER CONTROLS:
 * - Left Bumper: Start shooter at FRONT zone speed (63%) - close shots
 * - Left Trigger: Start shooter at BACK zone speed (73%) - far shots
 * - B Button: Stop shooter motors
 *
 * FRONT INTAKE CONTROLS (Front + Middle rollers):
 * - Right Trigger: Run forward (collect) - hold to run
 * - Y Button: Run reverse (eject) - hold to run
 *
 * BACK INTAKE CONTROLS (Last roller - for shooting):
 * - Right Bumper: Open trap door + run back intake (outtake ball) - hold to run
 * - A Button: Run reverse (pull back) - hold to run
 *
 * TRAP DOOR SERVO:
 *   Control Hub Servo Port 0 - "TD"
 *   REV 41-1097 270-degree servo
 *   Closed when released, Open when RB pressed
 *
 * Motor Configuration (as seen from behind the robot):
 *   Drive Motors (Control Hub):
 *     Back Left   (port 0) - "BL"
 *     Back Right  (port 1) - "BR"
 *     Front Left  (port 2) - "FL"
 *     Front Right (port 3) - "FR"
 *   Shooter Motors (Expansion Hub):
 *     Shooter Left  (port 0) - "SL"
 *     Shooter Right (port 1) - "SR"
 *   Intake Motors (Expansion Hub):
 *     Intake Front (port 2) - "IF" - Controls front and middle rollers
 *     Intake Back  (port 3) - "IB" - Controls last roller (shooting)
 *
 * IMU Configuration:
 *   Control Hub IMU - "imu"
 */

package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@Config
@TeleOp(name="Penguinauts: Field Relative Drive", group="Penguinauts")
public class Penguinauts_FieldRelativeDrive extends LinearOpMode {

    // Declare drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    
    // Declare shooter motors (using DcMotorEx for velocity control)
    private DcMotorEx shooterLeft = null;
    private DcMotorEx shooterRight = null;

    // Voltage sensor for battery compensation
    private VoltageSensor voltageSensor = null;
    
    // Declare intake motors
    private DcMotor intakeFront = null;
    private DcMotor intakeBack = null;

    // Declare trap door servo
    private Servo trapDoor = null;

    // PIDF coefficients for shooter velocity control - configurable via FTC Dashboard
    public static double P = 10.0;
    public static double I = 0.0;
    public static double D = 0.0;
    public static double F = 13.5;

    // Declare IMU
    private IMU imu = null;
    
    private ElapsedTime runtime = new ElapsedTime();
    
    // Speed multiplier for drive
    private static final double DRIVE_SPEED = 1;  // 63% drive speed
    
    // Shooter velocities for different zones - configurable via FTC Dashboard
    // Using velocity (ticks/sec) instead of power for consistent speed regardless of battery voltage
    // REV HD Hex motor: max ~2800 ticks/sec at full speed
    public static double SHOOTER_VELOCITY_FRONT = 1200.0;  // Front shooting zone - ~60% of max velocity
    public static double SHOOTER_VELOCITY_BACK = 1550.0;   // Back shooting zone - ~69% of max velocity

    // Legacy power values for reference/fallback
    public static double SHOOTER_POWER_FRONT = 0.63;  // Front shooting zone - 63% power
    public static double SHOOTER_POWER_BACK = 0.73;   // Back shooting zone - 73% power
    
    // Intake speed - configurable via FTC Dashboard
    public static double INTAKE_POWER = 1.0;  // Default to full speed (adjust in FTC Dashboard)
    public static double BACK_INTAKE_SLOW_REVERSE = 0.2;  // Slow reverse speed (20%) for back intake when RT pressed

    // Trap door servo positions - configurable via FTC Dashboard
    public static double TRAP_DOOR_CLOSED = 1.0;  // Closed position
    public static double TRAP_DOOR_OPEN = 0.85;   // Open position

    // Track selected shooter zone (LB = FRONT, LT = BACK)
    private double selectedShooterVelocity = SHOOTER_VELOCITY_FRONT;  // Default to front zone
    private String selectedZone = "FRONT";

    // Trap door state (default open)
    private boolean trapDoorOpen = true;
    

    @Override
    public void runOpMode() {
        // Send telemetry to both Driver Station and FTC Dashboard
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // Initialize the hardware variables
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        
        // Initialize drive motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "FL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "FR");
        backLeftDrive = hardwareMap.get(DcMotor.class, "BL");
        backRightDrive = hardwareMap.get(DcMotor.class, "BR");

        // Initialize voltage sensor for battery compensation
        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        // Initialize shooter motors independently for fault tolerance
        // Using DcMotorEx for velocity control - maintains consistent speed regardless of battery/load
        try {
            shooterLeft = hardwareMap.get(DcMotorEx.class, "SL");
            telemetry.addData("Shooter Left", "OK Found");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Shooter Left", "X Not found");
            shooterLeft = null;
        }

        try {
            shooterRight = hardwareMap.get(DcMotorEx.class, "SR");
            telemetry.addData("Shooter Right", "OK Found");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Shooter Right", "X Not found");
            shooterRight = null;
        }
        
        // Display shooter status
        if (shooterLeft != null && shooterRight != null) {
            telemetry.addData("Shooter Status", "✓ BOTH motors ready (full power)");
        } else if (shooterLeft != null || shooterRight != null) {
            telemetry.addData("Shooter Status", "⚠ ONE motor ready (reduced power)");
        } else {
            telemetry.addData("Shooter Status", "✗ No shooter configured");
        }
        
        // Initialize intake motors independently
        try {
            intakeFront = hardwareMap.get(DcMotor.class, "IF");
            telemetry.addData("Intake Front", "✓ Found");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Intake Front", "✗ Not found");
            intakeFront = null;
        }
        
        try {
            intakeBack = hardwareMap.get(DcMotor.class, "IB");
            telemetry.addData("Intake Back", "✓ Found");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Intake Back", "✗ Not found");
            intakeBack = null;
        }
        
        // Display intake status
        if (intakeFront != null && intakeBack != null) {
            telemetry.addData("Intake Status", "✓ BOTH motors ready");
        } else if (intakeFront != null || intakeBack != null) {
            telemetry.addData("Intake Status", "⚠ ONE motor ready");
        } else {
            telemetry.addData("Intake Status", "✗ No intake configured");
        }

        // Initialize trap door servo
        try {
            trapDoor = hardwareMap.get(Servo.class, "TD");
            trapDoor.setPosition(TRAP_DOOR_OPEN);  // Start in OPEN position
            telemetry.addData("Trap Door", "✓ Found (Open)");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Trap Door", "✗ Not found");
            trapDoor = null;
        }

        telemetry.update();

        // Set drive motor directions
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Set shooter motor directions independently
        // Using RUN_USING_ENCODER for velocity control - fixes velocity drop and battery issues
        if (shooterLeft != null) {
            shooterLeft.setDirection(DcMotor.Direction.REVERSE);
            shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            shooterLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            shooterLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            shooterLeft.setVelocityPIDFCoefficients(P, I, D, F);
        }

        if (shooterRight != null) {
            shooterRight.setDirection(DcMotor.Direction.FORWARD);
            shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            shooterRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            shooterRight.setVelocityPIDFCoefficients(P, I, D, F);
        }
        
        // Set intake motor directions
        // Both motors run same direction to move game elements through
        if (intakeFront != null) {
            intakeFront.setDirection(DcMotor.Direction.FORWARD);
            intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        
        if (intakeBack != null) {
            intakeBack.setDirection(DcMotor.Direction.FORWARD);
            intakeBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            intakeBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        // Set drive motors to brake when power is zero
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
        // Hub is mounted vertically with logo facing left, USB facing up
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection =
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection =
                RevHubOrientationOnRobot.UsbFacingDirection.UP;
        
        RevHubOrientationOnRobot orientationOnRobot = 
                new RevHubOrientationOnRobot(logoDirection, usbDirection);
        
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        // Wait for the game to start
        telemetry.addData("Status", "Initialized - Team Penguinauts 32240");
        telemetry.addData("Mode", "FIELD RELATIVE DRIVE");
        telemetry.addData("", "");
        telemetry.addData("DRIVE Controls", "Left stick: Drive (field-relative)");
        telemetry.addData("", "Right stick: Rotate");
        telemetry.addData("", "A: Reset field orientation (when not shooting)");
        
        // Show shooter controls if at least one motor is available
        if (shooterLeft != null || shooterRight != null) {
            telemetry.addData("", "");
            telemetry.addData("SHOOTER Controls", "");
            telemetry.addData("Left Bumper", "FRONT zone %.0f%% (close)", SHOOTER_POWER_FRONT * 100);
            telemetry.addData("Left Trigger", "BACK zone %.0f%% (far)", SHOOTER_POWER_BACK * 100);
            telemetry.addData("B Button", "Stop shooter");
            
            // Show which motors are available
            if (shooterLeft != null && shooterRight != null) {
                telemetry.addData("Shooter Mode", "✓ DUAL MOTOR (full power)");
            } else if (shooterLeft != null) {
                telemetry.addData("Shooter Mode", "⚠ LEFT MOTOR ONLY");
            } else {
                telemetry.addData("Shooter Mode", "⚠ RIGHT MOTOR ONLY");
            }
        }
        
        // Show front intake controls if motor is available
        if (intakeFront != null) {
            telemetry.addData("", "");
            telemetry.addData("FRONT INTAKE", "(Front + Middle rollers)");
            telemetry.addData("Controls", "RT: Collect | Y: Eject");
        }
        
        // Show back intake controls
        if (intakeBack != null) {
            telemetry.addData("", "");
            telemetry.addData("BACK INTAKE", "");
            telemetry.addData("RB", "Outtake ball");
            telemetry.addData("A", "Pull back");
        }
        
        if (intakeFront != null || intakeBack != null) {
            telemetry.addData("Intake Speed", "%.0f%% (adjust in Dashboard)", INTAKE_POWER * 100);
        }
        
        telemetry.addData("", "");
        telemetry.addData("IMU Status", "Ready");
        telemetry.update();

        waitForStart();
        runtime.reset();
        
        // Reset IMU yaw to zero at start
        imu.resetYaw();

        // Run until the driver presses STOP
        while (opModeIsActive()) {
            
            // ========== DRIVE CONTROLS ==========
            
            // Get joystick inputs
            double axial = -gamepad1.left_stick_y;   // Forward/backward
            double lateral = gamepad1.left_stick_x;   // Strafe left/right
            double yaw = gamepad1.right_stick_x;      // Rotate left/right

            // Reset field orientation with A button (only if back intake not being used)
            // Note: A button also controls back intake reverse
            if (gamepad1.a && intakeBack == null) {
                // Only reset IMU if back intake is not configured (A is used for back intake)
                imu.resetYaw();
            }

            // Fixed drive speed
            double speedMultiplier = DRIVE_SPEED;
            String driveMode = "Field-Relative (63%)";

            // Field-relative drive
            driveFieldRelative(axial, lateral, yaw, speedMultiplier);

            // ========== SHOOTER CONTROLS ==========

            // Update PIDF coefficients from Dashboard (allows live tuning)
            if (shooterLeft != null) shooterLeft.setVelocityPIDFCoefficients(P, I, D, F);
            if (shooterRight != null) shooterRight.setVelocityPIDFCoefficients(P, I, D, F);

            String shooterStatus = "STOPPED";
            String shooterMode = "N/A";
            String shootingZone = "NONE";
            double currentShooterVelocity = 0.0;

            // Operate shooter if at least ONE motor is available
            if (shooterLeft != null || shooterRight != null) {
                // Left Trigger: SELECT BACK zone and START shooter - far shots
                if (gamepad1.left_trigger > 0.1) {
                    selectedShooterVelocity = SHOOTER_VELOCITY_BACK;
                    selectedZone = "BACK";
                    // Start shooter at selected velocity
                    if (shooterLeft != null) shooterLeft.setVelocity(SHOOTER_VELOCITY_BACK);
                    if (shooterRight != null) shooterRight.setVelocity(SHOOTER_VELOCITY_BACK);
                }
                // Left Bumper: SELECT FRONT zone and START shooter - close shots
                else if (gamepad1.left_bumper) {
                    selectedShooterVelocity = SHOOTER_VELOCITY_FRONT;
                    selectedZone = "FRONT";
                    // Start shooter at selected velocity
                    if (shooterLeft != null) shooterLeft.setVelocity(SHOOTER_VELOCITY_FRONT);
                    if (shooterRight != null) shooterRight.setVelocity(SHOOTER_VELOCITY_FRONT);
                }

                // B Button: Stop shooter motors
                if (gamepad1.b) {
                    if (shooterLeft != null) {
                        shooterLeft.setVelocity(0);
                    }
                    if (shooterRight != null) {
                        shooterRight.setVelocity(0);
                    }
                    shooterStatus = "STOPPED";
                    currentShooterVelocity = 0.0;
                }

                // Get current shooter velocity (actual measured velocity)
                if (shooterLeft != null) {
                    currentShooterVelocity = shooterLeft.getVelocity();
                } else if (shooterRight != null) {
                    currentShooterVelocity = shooterRight.getVelocity();
                }

                // Update status display
                if (currentShooterVelocity > 100) {
                    shooterStatus = "SPINNING";
                    if (Math.abs(currentShooterVelocity - SHOOTER_VELOCITY_BACK) < 200) {
                        shootingZone = "BACK (" + String.format("%.0f", SHOOTER_VELOCITY_BACK) + ")";
                    } else {
                        shootingZone = "FRONT (" + String.format("%.0f", SHOOTER_VELOCITY_FRONT) + ")";
                    }
                } else {
                    shooterStatus = "STOPPED";
                    shootingZone = selectedZone + " (selected)";
                }

                // Determine shooter mode
                if (shooterLeft != null && shooterRight != null) {
                    shooterMode = "DUAL (Velocity PID)";
                } else if (shooterLeft != null) {
                    shooterMode = "LEFT ONLY";
                } else {
                    shooterMode = "RIGHT ONLY";
                }
            }
            
            // ========== FRONT INTAKE CONTROLS (Front + Middle rollers) ==========
            
            String frontIntakeStatus = "STOPPED";
            double frontIntakePower = 0.0;
            
            // Operate front intake independently
            if (intakeFront != null) {
                // Right Trigger: Run front intake FORWARD (collect) - only while held
                if (gamepad1.right_trigger > 0.1) {
                    intakeFront.setPower(INTAKE_POWER);
                    frontIntakeStatus = "COLLECTING";
                    frontIntakePower = INTAKE_POWER;
                }
                // Y Button: Run front intake REVERSE (eject) - only while held
                else if (gamepad1.y) {
                    intakeFront.setPower(-INTAKE_POWER);
                    frontIntakeStatus = "EJECTING";
                    frontIntakePower = -INTAKE_POWER;
                }
                // No button pressed: Stop front intake automatically
                else {
                    intakeFront.setPower(0);
                    frontIntakeStatus = "STOPPED";
                    frontIntakePower = 0.0;
                }
            }
            
            // ========== BACK INTAKE CONTROLS ==========

            String backIntakeStatus = "STOPPED";
            double backIntakePower = 0.0;

            // Right Bumper: Start shooter + run back intake immediately
            if (gamepad1.right_bumper) {
                // Start shooter
                if (shooterLeft != null) shooterLeft.setVelocity(selectedShooterVelocity);
                if (shooterRight != null) shooterRight.setVelocity(selectedShooterVelocity);
                // Run back intake immediately
                if (intakeBack != null) {
                    intakeBack.setPower(INTAKE_POWER);
                    backIntakeStatus = "SHOOTING!";
                    backIntakePower = INTAKE_POWER;
                }
            }
            // A Button: Run back intake REVERSE (pull back)
            else if (gamepad1.a && intakeBack != null) {
                intakeBack.setPower(-INTAKE_POWER);
                backIntakeStatus = "PULLING BACK";
                backIntakePower = -INTAKE_POWER;
            }
            // RT pressed (without RB): Run back intake REVERSE at slow speed - holds ball back
            else if (gamepad1.right_trigger > 0.1 && intakeBack != null) {
                intakeBack.setPower(-BACK_INTAKE_SLOW_REVERSE);
                backIntakeStatus = "SLOW REVERSE";
                backIntakePower = -BACK_INTAKE_SLOW_REVERSE;
            }
            // No button pressed: Stop back intake
            else {
                if (intakeBack != null) {
                    intakeBack.setPower(0);
                    backIntakeStatus = "STOPPED";
                    backIntakePower = 0.0;
                }
            }

            // ========== TRAP DOOR CONTROLS ==========
            // X = Close, Start = Open (default open)
            String trapDoorStatus = "N/A";
            if (trapDoor != null) {
                if (gamepad1.x) {
                    trapDoor.setPosition(TRAP_DOOR_CLOSED);
                    trapDoorOpen = false;
                    trapDoorStatus = "CLOSED";
                } else if (gamepad1.start) {
                    trapDoor.setPosition(TRAP_DOOR_OPEN);
                    trapDoorOpen = true;
                    trapDoorStatus = "OPEN";
                } else {
                    trapDoorStatus = trapDoorOpen ? "OPEN" : "CLOSED";
                }
            }

            // Get current robot heading for telemetry
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
            double heading = orientation.getYaw(AngleUnit.DEGREES);

            // Display telemetry
            telemetry.addData("Status", "Running - Team 32240");
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("", "");
            telemetry.addData("Drive Mode", driveMode);
            telemetry.addData("Robot Heading", "%.1f degrees", heading);
            telemetry.addData("Joystick", "Axial: %.2f, Lateral: %.2f, Yaw: %.2f", axial, lateral, yaw);
            
            // Shooter telemetry
            if (shooterLeft != null || shooterRight != null) {
                telemetry.addData("", "");
                telemetry.addData("=== SHOOTER ===", "");
                telemetry.addData("Status", shooterStatus);
                telemetry.addData("Zone", shootingZone);
                telemetry.addData("Velocity", "%.0f ticks/sec (target: %.0f)", currentShooterVelocity, selectedShooterVelocity);
                telemetry.addData("Velocity Error", "%.0f ticks/sec", selectedShooterVelocity - currentShooterVelocity);
                telemetry.addData("Battery", "%.2fV", voltageSensor.getVoltage());
                telemetry.addData("PIDF", "P=%.1f I=%.1f D=%.1f F=%.1f", P, I, D, F);
                telemetry.addData("Motor Mode", shooterMode);
                telemetry.addData("", "");
                telemetry.addData("Controls", "LB=Front | LT=Back | B=Stop");
            }
            
            // Display Front Intake telemetry
            if (intakeFront != null) {
                telemetry.addData("", "");
                telemetry.addData("=== FRONT INTAKE ===", "(Front + Middle)");
                telemetry.addData("Status", frontIntakeStatus);
                telemetry.addData("Power", "%.0f%%", frontIntakePower * 100);
                telemetry.addData("Controls", "RT=Collect | Y=Eject");
            }
            
            // Display Back Intake telemetry
            if (intakeBack != null) {
                telemetry.addData("", "");
                telemetry.addData("=== BACK INTAKE ===", "");
                telemetry.addData("Back Intake", "%s (%.0f%%)", backIntakeStatus, backIntakePower * 100);
                telemetry.addData("Controls", "RB=Outtake | A=Pull Back");
            }

            // Display Trap Door telemetry
            if (trapDoor != null) {
                telemetry.addData("", "");
                telemetry.addData("=== TRAP DOOR ===", "");
                telemetry.addData("Status", trapDoorStatus);
                telemetry.addData("Controls", "X=Close | Start=Open");
            }

            // Show intake speed config if any intake is available
            if (intakeFront != null || intakeBack != null) {
                telemetry.addData("", "");
                telemetry.addData("Intake Config Speed", "%.0f%% (Dashboard)", INTAKE_POWER * 100);
            }
            
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

