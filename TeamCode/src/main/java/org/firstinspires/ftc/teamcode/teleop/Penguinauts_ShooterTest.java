/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * Shooter Motor Test OpMode
 * 
 * This program allows you to test the shooter motors individually or together.
 * Use this for initial setup, troubleshooting, and finding optimal speed.
 *
 * CONTROLS:
 * - Right Trigger: Run both shooter motors at configured speed
 * - A Button: Stop all shooter motors
 * - X Button: Test LEFT shooter motor only (2 seconds)
 * - B Button: Test RIGHT shooter motor only (2 seconds)
 * - Y Button: Run both motors at 50% speed (for testing)
 * 
 * - D-Pad Up: Increase shooter speed by 10%
 * - D-Pad Down: Decrease shooter speed by 10%
 *
 * Motor Configuration:
 *   Shooter Left  (Expansion Hub port 0) - "SL"
 *   Shooter Right (Expansion Hub port 1) - "SR"
 */

package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name="Penguinauts: Shooter Test", group="Penguinauts")
public class Penguinauts_ShooterTest extends LinearOpMode {

    // Shooter motors
    private DcMotor shooterLeft = null;
    private DcMotor shooterRight = null;
    
    private ElapsedTime runtime = new ElapsedTime();
    
    // Configurable shooter speed (adjust in FTC Dashboard)
    public static double TEST_SPEED = 1.0;
    
    // Track current speed adjustment
    private double currentSpeed = 1.0;
    
    // Debounce for button presses
    private boolean dpadUpPressed = false;
    private boolean dpadDownPressed = false;

    @Override
    public void runOpMode() {
        
        telemetry.addData("Status", "Initializing Shooter Test...");
        telemetry.update();
        
        // Initialize shooter motors
        try {
            shooterLeft = hardwareMap.get(DcMotor.class, "SL");
            shooterRight = hardwareMap.get(DcMotor.class, "SR");
            telemetry.addData("✓", "Shooter motors found!");
        } catch (IllegalArgumentException e) {
            telemetry.addData("ERROR", "Shooter motors not found!");
            telemetry.addData("", "Check configuration:");
            telemetry.addData("", "SL (Exp Hub Port 0)");
            telemetry.addData("", "SR (Exp Hub Port 1)");
            telemetry.update();
            
            // Wait for stop button
            while (!isStopRequested() && !opModeIsActive()) {
                sleep(100);
            }
            return;
        }
        
        // Set motor directions
        // Both motors spin inward to propel ball forward
        shooterLeft.setDirection(DcMotor.Direction.REVERSE);  // REVERSED
        shooterRight.setDirection(DcMotor.Direction.FORWARD);  // REVERSED
        
        // Set to float when stopped (less strain on motors)
        shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        
        // Run without encoders (high speed mode)
        shooterLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        // Initial stop
        shooterLeft.setPower(0);
        shooterRight.setPower(0);
        
        // Display initialization complete
        telemetry.addData("Status", "Ready - Team Penguinauts 32240");
        telemetry.addData("", "");
        telemetry.addData("BASIC CONTROLS", "");
        telemetry.addData("Right Trigger", "Run shooter (full speed)");
        telemetry.addData("A Button", "Stop shooter");
        telemetry.addData("", "");
        telemetry.addData("TEST CONTROLS", "");
        telemetry.addData("X Button", "Test LEFT motor only");
        telemetry.addData("B Button", "Test RIGHT motor only");
        telemetry.addData("Y Button", "Run both at 50%");
        telemetry.addData("", "");
        telemetry.addData("SPEED ADJUST", "");
        telemetry.addData("D-Pad Up", "Increase speed (+10%)");
        telemetry.addData("D-Pad Down", "Decrease speed (-10%)");
        telemetry.addData("", "");
        telemetry.addData("Current Speed", "%.0f%%", currentSpeed * 100);
        telemetry.update();
        
        waitForStart();
        runtime.reset();
        
        // Main loop
        while (opModeIsActive()) {
            
            // Speed adjustment controls with debounce
            if (gamepad1.dpad_up && !dpadUpPressed) {
                currentSpeed = Math.min(1.0, currentSpeed + 0.1);
                dpadUpPressed = true;
            } else if (!gamepad1.dpad_up) {
                dpadUpPressed = false;
            }
            
            if (gamepad1.dpad_down && !dpadDownPressed) {
                currentSpeed = Math.max(0.0, currentSpeed - 0.1);
                dpadDownPressed = true;
            } else if (!gamepad1.dpad_down) {
                dpadDownPressed = false;
            }
            
            // Use TEST_SPEED from dashboard if not manually adjusted
            double targetSpeed = (currentSpeed == 1.0) ? TEST_SPEED : currentSpeed;
            
            String status = "IDLE";
            String action = "Waiting for input...";
            
            // Right Trigger: Run both motors at target speed
            if (gamepad1.right_trigger > 0.1) {
                shooterLeft.setPower(targetSpeed);
                shooterRight.setPower(targetSpeed);
                status = "🔥 SHOOTING";
                action = "Both motors running at " + String.format("%.0f%%", targetSpeed * 100);
            }
            // A Button: Stop all
            else if (gamepad1.a) {
                shooterLeft.setPower(0);
                shooterRight.setPower(0);
                status = "STOPPED";
                action = "All motors stopped";
            }
            // X Button: Test LEFT motor only
            else if (gamepad1.x) {
                shooterLeft.setPower(targetSpeed);
                shooterRight.setPower(0);
                status = "TEST LEFT";
                action = "LEFT motor only at " + String.format("%.0f%%", targetSpeed * 100);
            }
            // B Button: Test RIGHT motor only
            else if (gamepad1.b) {
                shooterLeft.setPower(0);
                shooterRight.setPower(targetSpeed);
                status = "TEST RIGHT";
                action = "RIGHT motor only at " + String.format("%.0f%%", targetSpeed * 100);
            }
            // Y Button: Run both at 50% (safe test)
            else if (gamepad1.y) {
                shooterLeft.setPower(0.5);
                shooterRight.setPower(0.5);
                status = "TEST 50%";
                action = "Both motors at 50% speed";
            }
            
            // Get current motor powers
            double leftPower = shooterLeft.getPower();
            double rightPower = shooterRight.getPower();
            
            // Update telemetry
            telemetry.addData("Status", status);
            telemetry.addData("Action", action);
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("", "");
            telemetry.addData("=== MOTOR POWERS ===", "");
            telemetry.addData("Left Motor", "%.2f (%.0f%%)", leftPower, leftPower * 100);
            telemetry.addData("Right Motor", "%.2f (%.0f%%)", rightPower, rightPower * 100);
            telemetry.addData("", "");
            telemetry.addData("=== SPEED SETTINGS ===", "");
            telemetry.addData("Target Speed", "%.0f%% (%.2f)", targetSpeed * 100, targetSpeed);
            telemetry.addData("Dashboard Speed", "%.0f%% (%.2f)", TEST_SPEED * 100, TEST_SPEED);
            telemetry.addData("Manual Adjust", "%.0f%% (D-Pad Up/Down)", currentSpeed * 100);
            telemetry.addData("", "");
            telemetry.addData("=== CONTROLS ===", "");
            telemetry.addData("RT=Full", "A=Stop | X=Left | B=Right | Y=50%");
            telemetry.addData("", "D-Pad Up/Down to adjust speed");
            telemetry.addData("", "");
            
            // Direction verification
            telemetry.addData("=== MOTOR CHECK ===", "");
            if (leftPower > 0 || rightPower > 0) {
                telemetry.addData("Direction", "Motors should spin INWARD");
                telemetry.addData("", "Ball should shoot FORWARD");
                telemetry.addData("⚠", "If wrong, see SHOOTER_CONFIG_GUIDE.md");
            } else {
                telemetry.addData("", "Press RT to test direction");
            }
            
            telemetry.update();
        }
        
        // Safety: Stop all motors when OpMode ends
        shooterLeft.setPower(0);
        shooterRight.setPower(0);
    }
}

