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
 * - X Button: Test LEFT shooter motor only
 * - B Button: Test RIGHT shooter motor only
 * - Y Button: Run both motors at 50% speed (for testing)
 * - Left Bumper: Test FRONT zone speed (63%)
 * - Right Bumper: Test BACK zone speed (73%)
 * 
 * - D-Pad Up: Increase shooter speed by 10%
 * - D-Pad Down: Decrease shooter speed by 10%
 *
 * Shooting Zone Speeds:
 *   Front Zone: 63% power (for close shots)
 *   Back Zone:  73% power (for far shots)
 *
 * Motor Configuration:
 *   Shooter Left  (Expansion Hub port 0) - "SL"
 *   Shooter Right (Expansion Hub port 1) - "SR"
 */

package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name="Penguinauts: Shooter Test", group="Penguinauts")
public class Penguinauts_ShooterTest extends LinearOpMode {

    // Shooter motors (using DcMotorEx for velocity control)
    private DcMotorEx shooterLeft = null;
    private DcMotorEx shooterRight = null;

    // Voltage sensor for battery compensation
    private VoltageSensor voltageSensor = null;

    private ElapsedTime runtime = new ElapsedTime();

    // Nominal voltage for compensation (fully charged battery)
    private static final double NOMINAL_VOLTAGE = 12.0;

    // Configurable shooter velocity in ticks per second (adjust in FTC Dashboard)
    // REV HD Hex motor: 28 ticks/rev * gear ratio
    // At full speed (~6000 RPM for HD Hex), max velocity is approximately 2800 ticks/sec
    public static double TEST_VELOCITY = 2800.0;  // ticks per second

    // Zone velocities (matching TeleOp settings) - in ticks per second
    // These replace the old power percentages (0.63 -> ~1764 ticks/sec, 0.73 -> ~2044 ticks/sec)
    public static double FRONT_ZONE_VELOCITY = 1680.0;  // Front shooting zone
    public static double BACK_ZONE_VELOCITY = 1940.0;   // Back shooting zone

    // Legacy power values for fallback/reference
    public static double FRONT_ZONE_SPEED = 0.63;  // Front shooting zone (power fallback)
    public static double BACK_ZONE_SPEED = 0.73;   // Back shooting zone (power fallback)

    // Track current speed adjustment
    private double currentSpeed = 1.0;
    private double currentVelocity = 2800.0;
    
    // Debounce for button presses
    private boolean dpadUpPressed = false;
    private boolean dpadDownPressed = false;

    @Override
    public void runOpMode() {
        // Send telemetry to both Driver Station and FTC Dashboard
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        telemetry.addData("Status", "Initializing Shooter Test...");
        telemetry.update();
        
        // Initialize shooter motors (using DcMotorEx for velocity control)
        try {
            shooterLeft = hardwareMap.get(DcMotorEx.class, "SL");
            shooterRight = hardwareMap.get(DcMotorEx.class, "SR");
            telemetry.addData("OK", "Shooter motors found!");
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

        // Initialize voltage sensor for battery compensation
        voltageSensor = hardwareMap.voltageSensor.iterator().next();
        telemetry.addData("Voltage", "%.2fV", voltageSensor.getVoltage());

        // Set motor directions
        // Both motors spin inward to propel ball forward
        shooterLeft.setDirection(DcMotor.Direction.REVERSE);  // REVERSED
        shooterRight.setDirection(DcMotor.Direction.FORWARD);  // REVERSED

        // Set to float when stopped (less strain on motors)
        shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // IMPORTANT: Use RUN_USING_ENCODER for velocity control
        // This enables the SDK's built-in PID to maintain consistent velocity
        // regardless of battery voltage or load (ball passing through)
        shooterLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Initial stop
        shooterLeft.setVelocity(0);
        shooterRight.setVelocity(0);
        
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
        telemetry.addData("Left Bumper", "Test FRONT zone (63%)");
        telemetry.addData("Right Bumper", "Test BACK zone (73%)");
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
                currentVelocity = Math.min(TEST_VELOCITY, currentVelocity + 280.0);  // +10% of max velocity
                dpadUpPressed = true;
            } else if (!gamepad1.dpad_up) {
                dpadUpPressed = false;
            }

            if (gamepad1.dpad_down && !dpadDownPressed) {
                currentSpeed = Math.max(0.0, currentSpeed - 0.1);
                currentVelocity = Math.max(0.0, currentVelocity - 280.0);  // -10% of max velocity
                dpadDownPressed = true;
            } else if (!gamepad1.dpad_down) {
                dpadDownPressed = false;
            }

            // Use TEST_VELOCITY from dashboard if not manually adjusted
            double targetVelocity = (currentVelocity == TEST_VELOCITY) ? TEST_VELOCITY : currentVelocity;

            // Get current battery voltage for telemetry
            double batteryVoltage = voltageSensor.getVoltage();

            String status = "IDLE";
            String action = "Waiting for input...";

            // Right Trigger: Run both motors at target velocity
            if (gamepad1.right_trigger > 0.1) {
                shooterLeft.setVelocity(targetVelocity);
                shooterRight.setVelocity(targetVelocity);
                status = "SHOOTING";
                action = "Both motors at " + String.format("%.0f", targetVelocity) + " ticks/sec";
            }
            // A Button: Stop all
            else if (gamepad1.a) {
                shooterLeft.setVelocity(0);
                shooterRight.setVelocity(0);
                status = "STOPPED";
                action = "All motors stopped";
            }
            // X Button: Test LEFT motor only
            else if (gamepad1.x) {
                shooterLeft.setVelocity(targetVelocity);
                shooterRight.setVelocity(0);
                status = "TEST LEFT";
                action = "LEFT motor only at " + String.format("%.0f", targetVelocity) + " ticks/sec";
            }
            // B Button: Test RIGHT motor only
            else if (gamepad1.b) {
                shooterLeft.setVelocity(0);
                shooterRight.setVelocity(targetVelocity);
                status = "TEST RIGHT";
                action = "RIGHT motor only at " + String.format("%.0f", targetVelocity) + " ticks/sec";
            }
            // Y Button: Run both at 50% velocity (safe test)
            else if (gamepad1.y) {
                shooterLeft.setVelocity(TEST_VELOCITY * 0.5);
                shooterRight.setVelocity(TEST_VELOCITY * 0.5);
                status = "TEST 50%";
                action = "Both motors at 50% velocity";
            }
            // Left Bumper: Test FRONT zone velocity
            else if (gamepad1.left_bumper) {
                shooterLeft.setVelocity(FRONT_ZONE_VELOCITY);
                shooterRight.setVelocity(FRONT_ZONE_VELOCITY);
                status = "FRONT ZONE";
                action = "Both motors at FRONT zone (" + String.format("%.0f", FRONT_ZONE_VELOCITY) + " ticks/sec)";
            }
            // Right Bumper: Test BACK zone velocity
            else if (gamepad1.right_bumper) {
                shooterLeft.setVelocity(BACK_ZONE_VELOCITY);
                shooterRight.setVelocity(BACK_ZONE_VELOCITY);
                status = "BACK ZONE";
                action = "Both motors at BACK zone (" + String.format("%.0f", BACK_ZONE_VELOCITY) + " ticks/sec)";
            }

            // Get current motor velocities (actual measured velocity)
            double leftVelocity = shooterLeft.getVelocity();
            double rightVelocity = shooterRight.getVelocity();
            
            // Update telemetry
            telemetry.addData("Status", status);
            telemetry.addData("Action", action);
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("", "");
            telemetry.addData("=== BATTERY ===", "");
            telemetry.addData("Voltage", "%.2fV (nominal: %.1fV)", batteryVoltage, NOMINAL_VOLTAGE);
            telemetry.addData("", "");
            telemetry.addData("=== MOTOR VELOCITIES ===", "");
            telemetry.addData("Left Motor", "%.0f ticks/sec (target: %.0f)", leftVelocity, targetVelocity);
            telemetry.addData("Right Motor", "%.0f ticks/sec (target: %.0f)", rightVelocity, targetVelocity);
            telemetry.addData("Velocity Error", "L: %.0f, R: %.0f", targetVelocity - leftVelocity, targetVelocity - rightVelocity);
            telemetry.addData("", "");
            telemetry.addData("=== VELOCITY SETTINGS ===", "");
            telemetry.addData("Target Velocity", "%.0f ticks/sec", targetVelocity);
            telemetry.addData("Dashboard Velocity", "%.0f ticks/sec", TEST_VELOCITY);
            telemetry.addData("Manual Adjust", "%.0f ticks/sec (D-Pad Up/Down)", currentVelocity);
            telemetry.addData("", "");
            telemetry.addData("=== CONTROLS ===", "");
            telemetry.addData("RT=Full", "A=Stop | X=Left | B=Right | Y=50%");
            telemetry.addData("LB=Front Zone", "RB=Back Zone");
            telemetry.addData("", "D-Pad Up/Down to adjust velocity");
            telemetry.addData("", "");
            telemetry.addData("=== ZONE VELOCITIES ===", "");
            telemetry.addData("Front Zone", "%.0f ticks/sec", FRONT_ZONE_VELOCITY);
            telemetry.addData("Back Zone", "%.0f ticks/sec", BACK_ZONE_VELOCITY);
            telemetry.addData("", "");

            // Direction verification
            telemetry.addData("=== MOTOR CHECK ===", "");
            if (leftVelocity > 100 || rightVelocity > 100) {
                telemetry.addData("Direction", "Motors should spin INWARD");
                telemetry.addData("", "Ball should shoot FORWARD");
                telemetry.addData("Mode", "Velocity Control (PID active)");
            } else {
                telemetry.addData("", "Press RT to test direction");
            }
            
            telemetry.update();
        }
        
        // Safety: Stop all motors when OpMode ends
        shooterLeft.setVelocity(0);
        shooterRight.setVelocity(0);
    }
}

