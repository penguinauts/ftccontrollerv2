/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * This is a template for autonomous OpModes for Team Penguinauts.
 * Use this as a starting point for creating autonomous routines.
 *
 * This template shows basic autonomous movements with mecanum drive:
 * - Drive forward/backward (encoder-based)
 * - Strafe left/right (encoder-based)
 * - Turn by degrees (encoder-based) - NEW!
 * - Combined movements
 * 
 * NEW FEATURE: turnDegrees(degrees)
 * ===================================
 * Turn your robot in place by any number of degrees!
 * 
 * Usage Examples:
 *   turnDegrees(90);    // Turn 90 degrees clockwise (right)
 *   turnDegrees(-90);   // Turn 90 degrees counter-clockwise (left)
 *   turnDegrees(180);   // Turn around 180 degrees
 *   turnDegrees(45);    // Turn 45 degrees clockwise
 * 
 * IMPORTANT - Calibration Required:
 * 1. Adjust COUNTS_PER_DEGREE constant (around line 52)
 * 2. Test: Run turnDegrees(360) and measure actual rotation
 * 3. If robot turns too far: DECREASE the constant
 * 4. If robot turns too little: INCREASE the constant
 * 5. Repeat until a 360-degree turn is accurate
 */

package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Penguinauts: Auto Template", group="Penguinauts")
@Disabled  // Remove this line when you're ready to use this OpMode
public class Penguinauts_AutoTemplate extends LinearOpMode {

    // Declare drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    
    private ElapsedTime runtime = new ElapsedTime();
    
    // Drive constants - adjust these based on your robot
    private static final double DRIVE_SPEED = 0.6;
    private static final double TURN_SPEED = 0.4;
    
    // Encoder counts per inch - you'll need to measure these for your robot
    // These are approximate values for GoBILDA 5202/5203 motors with mecanum wheels
    private static final double COUNTS_PER_MOTOR_REV = 537.7;  // For GoBILDA 5202 (Yellow Jacket)
    private static final double DRIVE_GEAR_REDUCTION = 1.0;    // No external gearing
    private static final double WHEEL_DIAMETER_INCHES = 4.0;   // For 100mm mecanum wheels
    private static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                  (WHEEL_DIAMETER_INCHES * 3.1415);
    
    // Turn calibration - TUNE THIS VALUE for accurate turns!
    // This represents encoder counts needed to turn 1 degree
    // To calibrate: Run turnDegrees(360), measure actual turn, adjust this value
    // If robot turns too far: decrease this value
    // If robot turns too little: increase this value
    private static final double COUNTS_PER_DEGREE = 10.0;  // Start value - needs calibration!

    @Override
    public void runOpMode() {
        
        // Initialize the hardware
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        
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

        // Wait for the game to start
        telemetry.addData("Status", "Ready - Team Penguinauts 32240");
        telemetry.addData("Mode", "AUTONOMOUS");
        telemetry.update();
        
        waitForStart();
        runtime.reset();

        // ======================================================================
        // AUTONOMOUS SEQUENCE - Customize this for your strategy!
        // ======================================================================
        
        if (opModeIsActive()) {
            
            // Example sequence - Replace with your actual autonomous routine
            
            telemetry.addData("Step", "1. Drive Forward");
            telemetry.update();
            driveForward(0.5, 24.0);  // Drive forward 24 inches at 50% speed
            sleep(500);  // Wait 0.5 seconds
            
            telemetry.addData("Step", "2. Strafe Right");
            telemetry.update();
            strafeRight(0.5, 12.0);  // Strafe right 12 inches
            sleep(500);
            
            telemetry.addData("Step", "3. Turn 90 Degrees");
            telemetry.update();
            turnDegrees(90);  // Turn 90 degrees clockwise
            sleep(500);
            
            telemetry.addData("Step", "3.5. Turn -90 Degrees");
            telemetry.update();
            turnDegrees(-90);  // Turn 90 degrees counter-clockwise (back to original)
            sleep(500);
            
            telemetry.addData("Step", "4. Drive Backward");
            telemetry.update();
            driveForward(0.5, -12.0);  // Drive backward 12 inches
            
            telemetry.addData("Status", "Autonomous Complete!");
            telemetry.update();
        }
    }

    /**
     * Drive forward (positive inches) or backward (negative inches)
     */
    private void driveForward(double speed, double inches) {
        if (!opModeIsActive()) return;
        
        // Calculate target positions
        int moveCounts = (int)(inches * COUNTS_PER_INCH);
        
        // Set target positions
        int flTarget = frontLeftDrive.getCurrentPosition() + moveCounts;
        int frTarget = frontRightDrive.getCurrentPosition() + moveCounts;
        int blTarget = backLeftDrive.getCurrentPosition() + moveCounts;
        int brTarget = backRightDrive.getCurrentPosition() + moveCounts;
        
        // Run to position
        runToPosition(flTarget, frTarget, blTarget, brTarget, speed);
    }

    /**
     * Strafe right (positive inches) or left (negative inches)
     */
    private void strafeRight(double speed, double inches) {
        if (!opModeIsActive()) return;
        
        // Calculate target positions
        // For strafing, front-left and back-right move opposite to front-right and back-left
        int moveCounts = (int)(inches * COUNTS_PER_INCH);
        
        int flTarget = frontLeftDrive.getCurrentPosition() + moveCounts;
        int frTarget = frontRightDrive.getCurrentPosition() - moveCounts;
        int blTarget = backLeftDrive.getCurrentPosition() - moveCounts;
        int brTarget = backRightDrive.getCurrentPosition() + moveCounts;
        
        runToPosition(flTarget, frTarget, blTarget, brTarget, speed);
    }

    /**
     * Turn the robot by a specific number of degrees in place
     * Positive degrees = clockwise (right turn)
     * Negative degrees = counter-clockwise (left turn)
     * 
     * Examples:
     *   turnDegrees(90)   - Turn 90 degrees clockwise
     *   turnDegrees(-90)  - Turn 90 degrees counter-clockwise
     *   turnDegrees(180)  - Turn 180 degrees (turn around)
     *   turnDegrees(45)   - Turn 45 degrees clockwise
     * 
     * CALIBRATION REQUIRED:
     * Adjust COUNTS_PER_DEGREE constant at the top of the file
     * Test by running turnDegrees(360) and measuring the actual rotation
     */
    private void turnDegrees(double degrees) {
        if (!opModeIsActive()) return;
        
        // Calculate encoder counts needed for this turn
        // Positive degrees = clockwise, negative = counter-clockwise
        int turnCounts = (int)(degrees * COUNTS_PER_DEGREE);
        
        // For turning in place:
        // - Left motors move forward for clockwise turn (positive)
        // - Right motors move backward for clockwise turn (negative)
        // This makes the robot spin around its center point
        int flTarget = frontLeftDrive.getCurrentPosition() + turnCounts;
        int frTarget = frontRightDrive.getCurrentPosition() - turnCounts;
        int blTarget = backLeftDrive.getCurrentPosition() + turnCounts;
        int brTarget = backRightDrive.getCurrentPosition() - turnCounts;
        
        // Use TURN_SPEED for smooth, controlled rotation
        runToPosition(flTarget, frTarget, blTarget, brTarget, TURN_SPEED);
    }

    /**
     * Rotate right (positive seconds) or left (negative seconds)
     * Uses time-based rotation instead of encoders
     */
    private void rotateRight(double speed, double seconds) {
        if (!opModeIsActive()) return;
        
        // Set motors to run without encoders for rotation
        setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        // Rotate by setting opposite powers to left and right wheels
        frontLeftDrive.setPower(speed);
        frontRightDrive.setPower(-speed);
        backLeftDrive.setPower(speed);
        backRightDrive.setPower(-speed);
        
        // Run for specified time
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < seconds)) {
            telemetry.addData("Rotating", "%.1f seconds", runtime.seconds());
            telemetry.update();
        }
        
        // Stop motors
        stopMotors();
        
        // Reset to encoder mode
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Internal method to run motors to specific positions
     */
    private void runToPosition(int flTarget, int frTarget, int blTarget, int brTarget, double speed) {
        // Set target positions
        frontLeftDrive.setTargetPosition(flTarget);
        frontRightDrive.setTargetPosition(frTarget);
        backLeftDrive.setTargetPosition(blTarget);
        backRightDrive.setTargetPosition(brTarget);
        
        // Switch to RUN_TO_POSITION mode
        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        // Set motor power
        frontLeftDrive.setPower(Math.abs(speed));
        frontRightDrive.setPower(Math.abs(speed));
        backLeftDrive.setPower(Math.abs(speed));
        backRightDrive.setPower(Math.abs(speed));
        
        // Wait until motors reach target or OpMode stops
        while (opModeIsActive() && 
               (frontLeftDrive.isBusy() && frontRightDrive.isBusy() && 
                backLeftDrive.isBusy() && backRightDrive.isBusy())) {
            
            // Display progress
            telemetry.addData("FL Target", "%d, Current: %d", flTarget, frontLeftDrive.getCurrentPosition());
            telemetry.addData("FR Target", "%d, Current: %d", frTarget, frontRightDrive.getCurrentPosition());
            telemetry.addData("BL Target", "%d, Current: %d", blTarget, backLeftDrive.getCurrentPosition());
            telemetry.addData("BR Target", "%d, Current: %d", brTarget, backRightDrive.getCurrentPosition());
            telemetry.update();
        }
        
        // Stop motors
        stopMotors();
        
        // Switch back to RUN_USING_ENCODER mode
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Stop all motors
     */
    private void stopMotors() {
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);
    }

    /**
     * Set the run mode for all motors
     */
    private void setMotorMode(DcMotor.RunMode mode) {
        frontLeftDrive.setMode(mode);
        frontRightDrive.setMode(mode);
        backLeftDrive.setMode(mode);
        backRightDrive.setMode(mode);
    }
}

