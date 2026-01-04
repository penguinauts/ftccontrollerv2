/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * TURN CALIBRATION TEST - Use this to calibrate your robot's turning
 * 
 * This OpMode helps you find the correct COUNTS_PER_DEGREE value.
 * 
 * INSTRUCTIONS:
 * 1. Mark your robot's starting position and orientation
 * 2. Run this OpMode
 * 3. Use gamepad to test different turn angles
 * 4. Adjust COUNTS_PER_DEGREE until turns are accurate
 * 5. Copy the final value to Penguinauts_AutoTemplate.java
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Penguinauts: Turn Calibration", group="Penguinauts")
public class AutoCode extends LinearOpMode {

    // Declare drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    
    private ElapsedTime runtime = new ElapsedTime();
    
    // ⚙️ ADJUST THIS VALUE TO CALIBRATE YOUR ROBOT! ⚙️
    // Start with 10.0, then adjust based on test results
    // If robot turns too far: DECREASE this value
    // If robot turns too little: INCREASE this value
    private static final double COUNTS_PER_DEGREE = 10.0;
    
    // Other constants (from AutoTemplate)
    private static final double COUNTS_PER_MOTOR_REV = 537.7;
    private static final double DRIVE_GEAR_REDUCTION = 1.0;
    private static final double WHEEL_DIAMETER_INCHES = 4.0;
    private static final double TURN_SPEED = 0.4;

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
        
        // Reset encoders
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Display calibration instructions
        telemetry.addLine("🎯 TURN CALIBRATION TEST");
        telemetry.addLine();
        telemetry.addData("Current COUNTS_PER_DEGREE", "%.2f", COUNTS_PER_DEGREE);
        telemetry.addLine();
        telemetry.addLine("TEST SEQUENCE:");
        telemetry.addLine("1. 360° turn (full rotation)");
        telemetry.addLine("2. 90° turn right");
        telemetry.addLine("3. -90° turn left (back to start)");
        telemetry.addLine("4. 180° turn around");
        telemetry.addLine("5. -180° turn back");
        telemetry.addLine();
        telemetry.addLine("Mark robot position now!");
        telemetry.addData("Status", "Ready to Start");
        telemetry.update();
        
        waitForStart();
        runtime.reset();

        if (opModeIsActive()) {
            
            // Test 1: Full 360-degree rotation
            telemetry.addLine("🔄 TEST 1: 360° ROTATION");
            telemetry.addLine("Robot should return to EXACT starting position");
            telemetry.addLine("Press STOP to abort");
            telemetry.update();
            sleep(3000);  // Give time to prepare
            
            turnDegrees(360);
            
            telemetry.addLine("✅ 360° turn complete!");
            telemetry.addLine();
            telemetry.addLine("Did robot return to exact starting position?");
            telemetry.addLine("- YES: Calibration good! ✅");
            telemetry.addLine("- Turned too far: DECREASE COUNTS_PER_DEGREE");
            telemetry.addLine("- Turned too little: INCREASE COUNTS_PER_DEGREE");
            telemetry.update();
            sleep(5000);
            
            // Test 2: 90-degree right turn
            telemetry.addLine("🔄 TEST 2: 90° RIGHT TURN");
            telemetry.update();
            sleep(2000);
            
            turnDegrees(90);
            
            telemetry.addLine("✅ 90° right turn complete!");
            telemetry.addLine("Check if angle is accurate");
            telemetry.update();
            sleep(3000);
            
            // Test 3: 90-degree left turn (back to start)
            telemetry.addLine("🔄 TEST 3: 90° LEFT TURN");
            telemetry.addLine("(Should return to original orientation)");
            telemetry.update();
            sleep(2000);
            
            turnDegrees(-90);
            
            telemetry.addLine("✅ 90° left turn complete!");
            telemetry.addLine("Back to starting orientation?");
            telemetry.update();
            sleep(3000);
            
            // Test 4: 180-degree turn
            telemetry.addLine("🔄 TEST 4: 180° TURN");
            telemetry.addLine("(Turn around)");
            telemetry.update();
            sleep(2000);
            
            turnDegrees(180);
            
            telemetry.addLine("✅ 180° turn complete!");
            telemetry.addLine("Robot should face opposite direction");
            telemetry.update();
            sleep(3000);
            
            // Test 5: -180-degree turn (back to start)
            telemetry.addLine("🔄 TEST 5: 180° RETURN");
            telemetry.update();
            sleep(2000);
            
            turnDegrees(-180);
            
            // Final results
            telemetry.clear();
            telemetry.addLine("🏁 CALIBRATION TESTS COMPLETE!");
            telemetry.addLine();
            telemetry.addData("COUNTS_PER_DEGREE used", "%.2f", COUNTS_PER_DEGREE);
            telemetry.addLine();
            telemetry.addLine("NEXT STEPS:");
            telemetry.addLine("1. Check if all turns were accurate");
            telemetry.addLine("2. If not, adjust COUNTS_PER_DEGREE");
            telemetry.addLine("3. Run test again");
            telemetry.addLine("4. When accurate, copy value to AutoTemplate");
            telemetry.addLine();
            telemetry.addLine("See TURN_CALIBRATION_GUIDE.md for help");
            telemetry.update();
            
            sleep(10000);  // Display results for 10 seconds
        }
    }

    /**
     * Turn the robot by a specific number of degrees in place
     * (Same function as in AutoTemplate)
     */
    private void turnDegrees(double degrees) {
        if (!opModeIsActive()) return;
        
        telemetry.addData("⚙️ Turning", "%.1f degrees...", degrees);
        telemetry.update();
        
        // Calculate encoder counts needed for this turn
        int turnCounts = (int)(degrees * COUNTS_PER_DEGREE);
        
        // For turning in place:
        // - Left motors move forward for clockwise turn (positive)
        // - Right motors move backward for clockwise turn (negative)
        int flTarget = frontLeftDrive.getCurrentPosition() + turnCounts;
        int frTarget = frontRightDrive.getCurrentPosition() - turnCounts;
        int blTarget = backLeftDrive.getCurrentPosition() + turnCounts;
        int brTarget = backRightDrive.getCurrentPosition() - turnCounts;
        
        // Set target positions
        frontLeftDrive.setTargetPosition(flTarget);
        frontRightDrive.setTargetPosition(frTarget);
        backLeftDrive.setTargetPosition(blTarget);
        backRightDrive.setTargetPosition(brTarget);
        
        // Switch to RUN_TO_POSITION mode
        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        // Set motor power
        frontLeftDrive.setPower(Math.abs(TURN_SPEED));
        frontRightDrive.setPower(Math.abs(TURN_SPEED));
        backLeftDrive.setPower(Math.abs(TURN_SPEED));
        backRightDrive.setPower(Math.abs(TURN_SPEED));
        
        // Wait until motors reach target
        while (opModeIsActive() && 
               (frontLeftDrive.isBusy() && frontRightDrive.isBusy() && 
                backLeftDrive.isBusy() && backRightDrive.isBusy())) {
            
            // Show progress
            telemetry.addData("⚙️ Turning", "%.1f degrees...", degrees);
            telemetry.addData("FL", "Target: %d, Current: %d", flTarget, frontLeftDrive.getCurrentPosition());
            telemetry.addData("FR", "Target: %d, Current: %d", frTarget, frontRightDrive.getCurrentPosition());
            telemetry.addData("BL", "Target: %d, Current: %d", blTarget, backLeftDrive.getCurrentPosition());
            telemetry.addData("BR", "Target: %d, Current: %d", brTarget, backRightDrive.getCurrentPosition());
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
