/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * This is the main TeleOp program for Team Penguinauts' mecanum drive robot.
 * This OpMode provides robot-relative control with the following features:
 *
 * DRIVE CONTROLS:
 * - Left stick: Forward/backward and strafe left/right
 * - Right stick: Rotate left/right
 * - Left bumper: Slow mode (50% speed)
 * - Right bumper: Turbo mode (100% speed)
 * - Default speed: 75%
 *
 * SHOOTER CONTROLS:
 * - Right Trigger: Start/run shooter motors at configured speed
 * - B Button: Stop shooter motors
 *
 * INTAKE CONTROLS:
 * - Left Trigger: Run intake forward (collect) - hold to run
 * - Y Button: Run intake reverse (eject) - hold to run
 * - Release button: Intake stops automatically
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
 *     Intake Front (port 2) - "IF"
 *     Intake Back  (port 3) - "IB"
 */

package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name="Penguinauts: Mecanum Drive", group="Penguinauts")
public class Penguinauts_MecanumDrive extends LinearOpMode {

    // Declare drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    
    // Declare shooter motors
    private DcMotor shooterLeft = null;
    private DcMotor shooterRight = null;
    
    // Declare intake motors
    private DcMotor intakeFront = null;
    private DcMotor intakeBack = null;
    
    private ElapsedTime runtime = new ElapsedTime();
    
    // Speed multipliers for drive
    private static final double SLOW_MODE_MULTIPLIER = 0.5;
    private static final double NORMAL_MODE_MULTIPLIER = 0.75;
    private static final double TURBO_MODE_MULTIPLIER = 1.0;
    
    // Shooter speed - configurable via FTC Dashboard
    public static double SHOOTER_POWER = 0.6;  // 60% power (adjust in FTC Dashboard)
    
    // Intake speed - configurable via FTC Dashboard
    public static double INTAKE_POWER = 1.0;  // Default to full speed (adjust in FTC Dashboard)

    @Override
    public void runOpMode() {
        
        // Initialize the hardware variables
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        
        // Initialize drive motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "FL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "FR");
        backLeftDrive = hardwareMap.get(DcMotor.class, "BL");
        backRightDrive = hardwareMap.get(DcMotor.class, "BR");

        // Initialize shooter motors independently for fault tolerance
        // This allows the shooter to work even if one motor fails
        try {
            shooterLeft = hardwareMap.get(DcMotor.class, "SL");
            telemetry.addData("Shooter Left", "✓ Found");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Shooter Left", "✗ Not found");
            shooterLeft = null;
        }
        
        try {
            shooterRight = hardwareMap.get(DcMotor.class, "SR");
            telemetry.addData("Shooter Right", "✓ Found");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Shooter Right", "✗ Not found");
            shooterRight = null;
        }
        
        // Display shooter status
        if (shooterLeft != null && shooterRight != null) {
            telemetry.addData("Shooter Status", "✓ BOTH motors ready (full power)");
        } else if (shooterLeft != null || shooterRight != null) {
            telemetry.addData("Shooter Status", "⚠ ONE motor ready (reduced power)");
            telemetry.addData("", "Robot will still work!");
        } else {
            telemetry.addData("Shooter Status", "✗ No shooter configured");
            telemetry.addData("", "Drive system will work normally");
        }
        
        // Initialize intake motors independently for fault tolerance
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
        telemetry.update();

        // Set drive motor directions
        // Left motors need to be reversed for mecanum drive
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Set shooter motor directions independently
        // Both motors spin inward to propel ball forward
        if (shooterLeft != null) {
            shooterLeft.setDirection(DcMotor.Direction.REVERSE);  // REVERSED
            shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            shooterLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        
        if (shooterRight != null) {
            shooterRight.setDirection(DcMotor.Direction.FORWARD);  // REVERSED
            shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            shooterRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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

        // Use encoders for better control (if connected)
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized - Team Penguinauts 32240");
        telemetry.addData("", "");
        telemetry.addData("DRIVE Controls", "Left stick: Drive/Strafe");
        telemetry.addData("", "Right stick: Rotate");
        telemetry.addData("", "L Bumper: Slow | R Bumper: Turbo");
        
        // Show shooter controls if at least one motor is available
        if (shooterLeft != null || shooterRight != null) {
            telemetry.addData("", "");
            telemetry.addData("SHOOTER Controls", "Right Trigger: Start Shooter");
            telemetry.addData("", "B Button: Stop Shooter");
            telemetry.addData("Shooter Speed", "%.0f%% (adjust in Dashboard)", SHOOTER_POWER * 100);
            
            // Show which motors are available
            if (shooterLeft != null && shooterRight != null) {
                telemetry.addData("Shooter Mode", "✓ DUAL MOTOR (full power)");
            } else if (shooterLeft != null) {
                telemetry.addData("Shooter Mode", "⚠ LEFT MOTOR ONLY");
            } else {
                telemetry.addData("Shooter Mode", "⚠ RIGHT MOTOR ONLY");
            }
        }
        
        // Show intake controls if at least one motor is available
        if (intakeFront != null || intakeBack != null) {
            telemetry.addData("", "");
            telemetry.addData("INTAKE Controls", "Left Trigger: Collect (hold)");
            telemetry.addData("", "Y Button: Eject (hold)");
            telemetry.addData("Intake Speed", "%.0f%% (adjust in Dashboard)", INTAKE_POWER * 100);
            
            // Show which motors are available
            if (intakeFront != null && intakeBack != null) {
                telemetry.addData("Intake Mode", "✓ FRONT & BACK");
            } else if (intakeFront != null) {
                telemetry.addData("Intake Mode", "⚠ FRONT ONLY");
            } else {
                telemetry.addData("Intake Mode", "⚠ BACK ONLY");
            }
        }
        
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Run until the driver presses STOP
        while (opModeIsActive()) {
            
            // ========== DRIVE CONTROLS ==========
            
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
            
            // ========== SHOOTER CONTROLS ==========
            
            String shooterStatus = "Not configured";
            String shooterMode = "N/A";
            double currentShooterPower = 0.0;
            
            // Operate shooter if at least ONE motor is available
            if (shooterLeft != null || shooterRight != null) {
                // Right Trigger: Start shooter motors
                if (gamepad1.right_trigger > 0.1) {
                    if (shooterLeft != null) {
                        shooterLeft.setPower(SHOOTER_POWER);
                    }
                    if (shooterRight != null) {
                        shooterRight.setPower(SHOOTER_POWER);
                    }
                    shooterStatus = "🔥 SHOOTING";
                    currentShooterPower = SHOOTER_POWER;
                }
                // B Button: Stop shooter motors
                else if (gamepad1.b) {
                    if (shooterLeft != null) {
                        shooterLeft.setPower(0);
                    }
                    if (shooterRight != null) {
                        shooterRight.setPower(0);
                    }
                    shooterStatus = "STOPPED";
                    currentShooterPower = 0.0;
                }
                // Maintain current state
                else {
                    // Check power from whichever motor is available
                    if (shooterLeft != null) {
                        currentShooterPower = shooterLeft.getPower();
                    } else if (shooterRight != null) {
                        currentShooterPower = shooterRight.getPower();
                    }
                    
                    if (currentShooterPower > 0.01) {
                        shooterStatus = "RUNNING";
                    } else {
                        shooterStatus = "STOPPED";
                    }
                }
                
                // Determine shooter mode
                if (shooterLeft != null && shooterRight != null) {
                    shooterMode = "DUAL (Full Power)";
                } else if (shooterLeft != null) {
                    shooterMode = "LEFT ONLY ⚠";
                } else {
                    shooterMode = "RIGHT ONLY ⚠";
                }
            }
            
            // ========== INTAKE CONTROLS ==========
            
            String intakeStatus = "STOPPED";
            String intakeMode = "N/A";
            double currentIntakePower = 0.0;
            
            // Operate intake if at least ONE motor is available
            if (intakeFront != null || intakeBack != null) {
                // Left Trigger: Run intake FORWARD (collect) - only while held
                if (gamepad1.left_trigger > 0.1) {
                    if (intakeFront != null) {
                        intakeFront.setPower(INTAKE_POWER);
                    }
                    if (intakeBack != null) {
                        intakeBack.setPower(INTAKE_POWER);
                    }
                    intakeStatus = "COLLECTING";
                    currentIntakePower = INTAKE_POWER;
                }
                // Y Button: Run intake REVERSE (eject) - only while held
                else if (gamepad1.y) {
                    if (intakeFront != null) {
                        intakeFront.setPower(-INTAKE_POWER);
                    }
                    if (intakeBack != null) {
                        intakeBack.setPower(-INTAKE_POWER);
                    }
                    intakeStatus = "EJECTING";
                    currentIntakePower = -INTAKE_POWER;
                }
                // No button pressed: Stop intake automatically
                else {
                    if (intakeFront != null) {
                        intakeFront.setPower(0);
                    }
                    if (intakeBack != null) {
                        intakeBack.setPower(0);
                    }
                    intakeStatus = "STOPPED";
                    currentIntakePower = 0.0;
                }
                
                // Determine intake mode
                if (intakeFront != null && intakeBack != null) {
                    intakeMode = "FRONT & BACK";
                } else if (intakeFront != null) {
                    intakeMode = "FRONT ONLY ⚠";
                } else {
                    intakeMode = "BACK ONLY ⚠";
                }
            }

            // Display telemetry data
            telemetry.addData("Status", "Running - Team 32240");
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("", "");
            telemetry.addData("Drive Mode", driveMode);
            telemetry.addData("Joystick", "Axial: %.2f, Lateral: %.2f, Yaw: %.2f", axial, lateral, yaw);
            telemetry.addData("", "");
            telemetry.addData("Drive Power", "FL: %.2f, FR: %.2f", frontLeftPower, frontRightPower);
            telemetry.addData("", "BL: %.2f, BR: %.2f", backLeftPower, backRightPower);
            
            if (shooterLeft != null || shooterRight != null) {
                telemetry.addData("", "");
                telemetry.addData("=== SHOOTER ===", "");
                telemetry.addData("Mode", shooterMode);
                telemetry.addData("Status", shooterStatus);
                telemetry.addData("Power", "%.0f%% (%.2f)", currentShooterPower * 100, currentShooterPower);
                telemetry.addData("Config Speed", "%.0f%% (Dashboard)", SHOOTER_POWER * 100);
                telemetry.addData("", "");
                telemetry.addData("Controls", "RT=Start | B=Stop");
                
                // Show which motors are actually running
                if (shooterLeft != null && shooterRight != null) {
                    double leftPwr = shooterLeft.getPower();
                    double rightPwr = shooterRight.getPower();
                    telemetry.addData("Motor Status", "L:%.0f%% R:%.0f%%", leftPwr*100, rightPwr*100);
                } else if (shooterLeft != null) {
                    telemetry.addData("Motor Status", "LEFT: %.0f%% | RIGHT: ✗ FAILED", shooterLeft.getPower()*100);
                } else {
                    telemetry.addData("Motor Status", "LEFT: ✗ FAILED | RIGHT: %.0f%%", shooterRight.getPower()*100);
                }
            }
            
            if (intakeFront != null || intakeBack != null) {
                telemetry.addData("", "");
                telemetry.addData("=== INTAKE ===", "");
                telemetry.addData("Mode", intakeMode);
                telemetry.addData("Status", intakeStatus);
                telemetry.addData("Power", "%.0f%% (%.2f)", currentIntakePower * 100, currentIntakePower);
                telemetry.addData("Config Speed", "%.0f%% (Dashboard)", INTAKE_POWER * 100);
                telemetry.addData("", "");
                telemetry.addData("Controls", "LT=Collect | Y=Eject");
                
                // Show which motors are actually running
                if (intakeFront != null && intakeBack != null) {
                    double frontPwr = intakeFront.getPower();
                    double backPwr = intakeBack.getPower();
                    telemetry.addData("Motor Status", "Front:%.0f%% Back:%.0f%%", frontPwr*100, backPwr*100);
                } else if (intakeFront != null) {
                    telemetry.addData("Motor Status", "FRONT: %.0f%% | BACK: ✗ FAILED", intakeFront.getPower()*100);
                } else {
                    telemetry.addData("Motor Status", "FRONT: ✗ FAILED | BACK: %.0f%%", intakeBack.getPower()*100);
                }
            }
            
            telemetry.update();
        }
    }
}

