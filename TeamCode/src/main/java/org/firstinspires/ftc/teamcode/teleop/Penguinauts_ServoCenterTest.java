/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * Simple utility OpMode to center a servo at position 0.5
 * Use this when mounting a new servo horn to ensure it's centered.
 *
 * INSTRUCTIONS:
 * 1. Connect the servo to Control Hub Servo Port 0 (named "TD")
 * 2. Do NOT attach the servo horn yet
 * 3. Run this OpMode
 * 4. The servo will move to position 0.5 (center)
 * 5. While the OpMode is running, attach the servo horn in your desired orientation
 * 6. Stop the OpMode when done
 *
 * You can also use the gamepad to fine-tune the position:
 * - D-pad Up: Increase position by 0.05
 * - D-pad Down: Decrease position by 0.05
 * - A Button: Reset to 0.5 (center)
 */

package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Penguinauts: Servo Center Test", group="Penguinauts")
public class Penguinauts_ServoCenterTest extends LinearOpMode {

    private Servo servo = null;
    private double servoPosition = 0.5;  // Start at center
    
    // Debounce for d-pad
    private boolean dpadUpPressed = false;
    private boolean dpadDownPressed = false;

    @Override
    public void runOpMode() {
        
        // Initialize the servo
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        
        try {
            servo = hardwareMap.get(Servo.class, "TD");
            telemetry.addData("Servo", "✓ Found on port TD");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Servo", "✗ NOT FOUND - Check connection!");
            telemetry.addData("", "Make sure servo is on Control Hub port 0");
            telemetry.addData("", "and named 'TD' in robot configuration");
            telemetry.update();
            
            // Wait for user to see the error
            waitForStart();
            return;
        }
        
        // Set servo to center position
        servo.setPosition(servoPosition);
        
        telemetry.addData("Status", "Ready - Servo at CENTER (0.5)");
        telemetry.addData("", "");
        telemetry.addData("INSTRUCTIONS", "");
        telemetry.addData("1.", "Press START to begin");
        telemetry.addData("2.", "Servo will hold at center position");
        telemetry.addData("3.", "Attach servo horn in desired orientation");
        telemetry.addData("4.", "Use D-pad to fine-tune if needed");
        telemetry.addData("", "");
        telemetry.addData("Controls", "D-pad Up/Down: Adjust position");
        telemetry.addData("", "A Button: Reset to 0.5");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            
            // D-pad Up: Increase position
            if (gamepad1.dpad_up && !dpadUpPressed) {
                servoPosition = Math.min(1.0, servoPosition + 0.05);
                servo.setPosition(servoPosition);
                dpadUpPressed = true;
            } else if (!gamepad1.dpad_up) {
                dpadUpPressed = false;
            }
            
            // D-pad Down: Decrease position
            if (gamepad1.dpad_down && !dpadDownPressed) {
                servoPosition = Math.max(0.0, servoPosition - 0.05);
                servo.setPosition(servoPosition);
                dpadDownPressed = true;
            } else if (!gamepad1.dpad_down) {
                dpadDownPressed = false;
            }
            
            // A Button: Reset to center
            if (gamepad1.a) {
                servoPosition = 0.5;
                servo.setPosition(servoPosition);
            }
            
            // Display status
            telemetry.addData("=== SERVO CENTER TEST ===", "");
            telemetry.addData("", "");
            telemetry.addData("Current Position", "%.2f", servoPosition);
            telemetry.addData("", "");
            
            // Visual position indicator
            int markerPos = (int)(servoPosition * 20);
            StringBuilder positionBar = new StringBuilder("[");
            for (int i = 0; i < markerPos; i++) positionBar.append("=");
            positionBar.append("|");
            for (int i = markerPos; i < 20; i++) positionBar.append("=");
            positionBar.append("]");
            telemetry.addData("Range", positionBar.toString());
            telemetry.addData("", "0.0            0.5            1.0");
            telemetry.addData("", "");
            telemetry.addData("Controls", "");
            telemetry.addData("D-pad Up", "Increase (+0.05)");
            telemetry.addData("D-pad Down", "Decrease (-0.05)");
            telemetry.addData("A Button", "Reset to 0.5 (center)");
            telemetry.addData("", "");
            telemetry.addData("TIP", "Attach horn while servo holds position");
            
            telemetry.update();
        }
    }
}
