/* Copyright (c) 2025 Team Penguinauts 32240. All rights reserved.
 *
 * Pre-Match System Check - Complete Robot Validation
 * 
 * This OpMode tests ALL robot systems in one place for quick pre-match validation.
 * Run this before EVERY match to ensure everything is working!
 *
 * TEST SEQUENCE (Automatic + Manual):
 * 1. Hardware detection (automatic at INIT)
 * 2. Drive motors test (manual with gamepad)
 * 3. Shooter motors test (manual with gamepad)
 * 4. Intake motors test (manual with gamepad)
 * 5. Final status report
 *
 * CONTROLS:
 * === DRIVE TEST ===
 * - Y Button: Test Front Right motor
 * - X Button: Test Front Left motor  
 * - B Button: Test Back Right motor
 * - A Button: Test Back Left motor
 * - D-Pad Up: Test ALL drive motors forward
 * - D-Pad Down: Test ALL drive motors backward
 *
 * === SHOOTER TEST ===
 * - Right Trigger: Run both shooter motors
 * - Left Trigger: Test LEFT shooter only
 * - Right Bumper: Test RIGHT shooter only
 * - Left Bumper: Stop all shooter motors
 *
 * === INTAKE TEST ===
 * - Guide: Use Guide button to cycle: FORWARD -> REVERSE -> STOP
 * - Back: Emergency stop intake motors
 *
 * === QUICK TEST ===
 * - START Button: Quick test ALL systems (3 seconds each)
 *
 * Motor Configuration:
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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Penguinauts: Pre-Match Check", group="Penguinauts")
@Disabled
public class Penguinauts_PreMatchCheck extends LinearOpMode {

    // Drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;
    
    // Shooter motors
    private DcMotor shooterLeft = null;
    private DcMotor shooterRight = null;
    
    // Intake motors
    private DcMotor intakeFront = null;
    private DcMotor intakeBack = null;
    
    private ElapsedTime runtime = new ElapsedTime();
    
    // Test power levels
    private static final double DRIVE_TEST_POWER = 0.3;
    private static final double SHOOTER_TEST_POWER = 0.5;
    private static final double INTAKE_TEST_POWER = 0.5;
    
    // Hardware status
    private boolean driveMotorsOK = false;
    private boolean shooterMotorsOK = false;
    private boolean intakeMotorsOK = false;
    
    // Intake test state
    private int intakeTestState = 0; // 0=stopped, 1=forward, 2=reverse
    
    @Override
    public void runOpMode() {
        
        telemetry.addData("🤖", "PRE-MATCH SYSTEM CHECK");
        telemetry.addData("Status", "Initializing...");
        telemetry.update();
        
        // ========== INITIALIZE DRIVE MOTORS ==========
        
        boolean flOK = false, frOK = false, blOK = false, brOK = false;
        
        try {
            frontLeftDrive = hardwareMap.get(DcMotor.class, "FL");
            frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
            frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            flOK = true;
        } catch (Exception e) {
            frontLeftDrive = null;
        }
        
        try {
            frontRightDrive = hardwareMap.get(DcMotor.class, "FR");
            frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
            frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frOK = true;
        } catch (Exception e) {
            frontRightDrive = null;
        }
        
        try {
            backLeftDrive = hardwareMap.get(DcMotor.class, "BL");
            backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
            backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            blOK = true;
        } catch (Exception e) {
            backLeftDrive = null;
        }
        
        try {
            backRightDrive = hardwareMap.get(DcMotor.class, "BR");
            backRightDrive.setDirection(DcMotor.Direction.FORWARD);
            backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            brOK = true;
        } catch (Exception e) {
            backRightDrive = null;
        }
        
        driveMotorsOK = (flOK && frOK && blOK && brOK);
        
        // ========== INITIALIZE SHOOTER MOTORS ==========
        
        boolean slOK = false, srOK = false;
        
        try {
            shooterLeft = hardwareMap.get(DcMotor.class, "SL");
            shooterLeft.setDirection(DcMotor.Direction.REVERSE);  // REVERSED
            shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            shooterLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            slOK = true;
        } catch (Exception e) {
            shooterLeft = null;
        }
        
        try {
            shooterRight = hardwareMap.get(DcMotor.class, "SR");
            shooterRight.setDirection(DcMotor.Direction.FORWARD);  // REVERSED
            shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            shooterRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            srOK = true;
        } catch (Exception e) {
            shooterRight = null;
        }
        
        shooterMotorsOK = (slOK || srOK);  // At least one shooter motor OK
        
        // ========== INITIALIZE INTAKE MOTORS ==========
        
        boolean ifOK = false;
        boolean ibOK = false;
        
        try {
            intakeFront = hardwareMap.get(DcMotor.class, "IF");
            intakeFront.setDirection(DcMotor.Direction.FORWARD);
            intakeFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            intakeFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            ifOK = true;
        } catch (Exception e) {
            intakeFront = null;
        }
        
        try {
            intakeBack = hardwareMap.get(DcMotor.class, "IB");
            intakeBack.setDirection(DcMotor.Direction.FORWARD);
            intakeBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            intakeBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            ibOK = true;
        } catch (Exception e) {
            intakeBack = null;
        }
        
        intakeMotorsOK = (ifOK || ibOK);  // At least one intake motor OK
        
        // ========== DISPLAY INITIALIZATION RESULTS ==========
        
        telemetry.clear();
        telemetry.addData("🤖", "TEAM PENGUINAUTS 32240");
        telemetry.addData("", "PRE-MATCH SYSTEM CHECK");
        telemetry.addData("", "");
        
        telemetry.addData("=== DRIVE MOTORS ===", "");
        telemetry.addData("Front Left", flOK ? "✓ OK" : "✗ FAILED");
        telemetry.addData("Front Right", frOK ? "✓ OK" : "✗ FAILED");
        telemetry.addData("Back Left", blOK ? "✓ OK" : "✗ FAILED");
        telemetry.addData("Back Right", brOK ? "✓ OK" : "✗ FAILED");
        telemetry.addData("Drive Status", driveMotorsOK ? "✓ ALL OK" : "⚠ SOME FAILED");
        telemetry.addData("", "");
        
        telemetry.addData("=== SHOOTER MOTORS ===", "");
        telemetry.addData("Shooter Left", slOK ? "✓ OK" : "✗ FAILED");
        telemetry.addData("Shooter Right", srOK ? "✓ OK" : "✗ FAILED");
        if (slOK && srOK) {
            telemetry.addData("Shooter Status", "✓ DUAL (Full Power)");
        } else if (slOK || srOK) {
            telemetry.addData("Shooter Status", "⚠ ONE MOTOR (Reduced)");
        } else {
            telemetry.addData("Shooter Status", "✗ NO SHOOTER");
        }
        telemetry.addData("", "");
        
        telemetry.addData("=== INTAKE MOTORS ===", "");
        telemetry.addData("Intake Front", ifOK ? "✓ OK" : "✗ FAILED");
        telemetry.addData("Intake Back", ibOK ? "✓ OK" : "✗ FAILED");
        if (ifOK && ibOK) {
            telemetry.addData("Intake Status", "✓ BOTH OK");
        } else if (ifOK || ibOK) {
            telemetry.addData("Intake Status", "⚠ ONE MOTOR");
        } else {
            telemetry.addData("Intake Status", "✗ NO INTAKE");
        }
        telemetry.addData("", "");
        
        // Overall status
        boolean readyForMatch = driveMotorsOK;
        if (readyForMatch) {
            telemetry.addData("🏆 MATCH READY", "✓ GO FOR IT!");
        } else {
            telemetry.addData("⚠ WARNING", "Drive issues detected");
        }
        telemetry.addData("", "");
        
        telemetry.addData("CONTROLS", "See below");
        telemetry.addData("", "Press START to begin tests");
        telemetry.update();
        
        waitForStart();
        runtime.reset();
        
        // ========== MAIN TEST LOOP ==========
        
        while (opModeIsActive()) {
            
            String currentTest = "Ready for tests";
            
            // ========== DRIVE MOTOR TESTS ==========
            
            // Individual motor tests
            if (gamepad1.y && frontRightDrive != null) {
                frontRightDrive.setPower(DRIVE_TEST_POWER);
                currentTest = "Testing: FRONT RIGHT";
            } else if (frontRightDrive != null) {
                frontRightDrive.setPower(0);
            }
            
            if (gamepad1.x && frontLeftDrive != null) {
                frontLeftDrive.setPower(DRIVE_TEST_POWER);
                currentTest = "Testing: FRONT LEFT";
            } else if (frontLeftDrive != null) {
                frontLeftDrive.setPower(0);
            }
            
            if (gamepad1.b && backRightDrive != null) {
                backRightDrive.setPower(DRIVE_TEST_POWER);
                currentTest = "Testing: BACK RIGHT";
            } else if (backRightDrive != null) {
                backRightDrive.setPower(0);
            }
            
            if (gamepad1.a && backLeftDrive != null) {
                backLeftDrive.setPower(DRIVE_TEST_POWER);
                currentTest = "Testing: BACK LEFT";
            } else if (backLeftDrive != null) {
                backLeftDrive.setPower(0);
            }
            
            // All motors forward
            if (gamepad1.dpad_up) {
                if (frontLeftDrive != null) frontLeftDrive.setPower(DRIVE_TEST_POWER);
                if (frontRightDrive != null) frontRightDrive.setPower(DRIVE_TEST_POWER);
                if (backLeftDrive != null) backLeftDrive.setPower(DRIVE_TEST_POWER);
                if (backRightDrive != null) backRightDrive.setPower(DRIVE_TEST_POWER);
                currentTest = "Testing: ALL DRIVE FORWARD";
            }
            
            // All motors backward
            if (gamepad1.dpad_down) {
                if (frontLeftDrive != null) frontLeftDrive.setPower(-DRIVE_TEST_POWER);
                if (frontRightDrive != null) frontRightDrive.setPower(-DRIVE_TEST_POWER);
                if (backLeftDrive != null) backLeftDrive.setPower(-DRIVE_TEST_POWER);
                if (backRightDrive != null) backRightDrive.setPower(-DRIVE_TEST_POWER);
                currentTest = "Testing: ALL DRIVE BACKWARD";
            }
            
            // ========== SHOOTER MOTOR TESTS ==========
            
            // Both shooter motors
            if (gamepad1.right_trigger > 0.1) {
                if (shooterLeft != null) shooterLeft.setPower(SHOOTER_TEST_POWER);
                if (shooterRight != null) shooterRight.setPower(SHOOTER_TEST_POWER);
                currentTest = "Testing: BOTH SHOOTERS";
            }
            
            // Left shooter only
            if (gamepad1.left_trigger > 0.1) {
                if (shooterLeft != null) shooterLeft.setPower(SHOOTER_TEST_POWER);
                if (shooterRight != null) shooterRight.setPower(0);
                currentTest = "Testing: LEFT SHOOTER";
            }
            
            // Right shooter only
            if (gamepad1.right_bumper) {
                if (shooterLeft != null) shooterLeft.setPower(0);
                if (shooterRight != null) shooterRight.setPower(SHOOTER_TEST_POWER);
                currentTest = "Testing: RIGHT SHOOTER";
            }
            
            // Stop all shooters
            if (gamepad1.left_bumper) {
                if (shooterLeft != null) shooterLeft.setPower(0);
                if (shooterRight != null) shooterRight.setPower(0);
                currentTest = "Shooter: STOPPED";
            }
            
            // ========== INTAKE MOTOR TESTS ==========
            
            // Guide button: Cycle through intake states
            if (gamepad1.guide) {
                intakeTestState = (intakeTestState + 1) % 3;
                sleep(200); // Debounce
            }
            
            // Apply intake state
            if (intakeFront != null || intakeBack != null) {
                switch (intakeTestState) {
                    case 1: // Forward
                        if (intakeFront != null) intakeFront.setPower(INTAKE_TEST_POWER);
                        if (intakeBack != null) intakeBack.setPower(INTAKE_TEST_POWER);
                        currentTest = "Testing: INTAKE FORWARD";
                        break;
                    case 2: // Reverse
                        if (intakeFront != null) intakeFront.setPower(-INTAKE_TEST_POWER);
                        if (intakeBack != null) intakeBack.setPower(-INTAKE_TEST_POWER);
                        currentTest = "Testing: INTAKE REVERSE";
                        break;
                    case 0: // Stopped
                    default:
                        if (intakeFront != null) intakeFront.setPower(0);
                        if (intakeBack != null) intakeBack.setPower(0);
                        if (currentTest.contains("INTAKE")) {
                            currentTest = "Intake: STOPPED";
                        }
                        break;
                }
            }
            
            // Back button: Emergency stop intake
            if (gamepad1.back) {
                if (intakeFront != null) intakeFront.setPower(0);
                if (intakeBack != null) intakeBack.setPower(0);
                intakeTestState = 0;
                currentTest = "Intake: EMERGENCY STOP";
            }
            
            // ========== DISPLAY TELEMETRY ==========
            
            telemetry.clear();
            telemetry.addData("🤖", "PRE-MATCH CHECK");
            telemetry.addData("Status", currentTest);
            telemetry.addData("Time", "%.1f sec", runtime.seconds());
            telemetry.addData("", "");
            
            // Drive motor status
            telemetry.addData("=== DRIVE MOTORS ===", "");
            if (frontLeftDrive != null) {
                telemetry.addData("FL", "%.0f%% %s", Math.abs(frontLeftDrive.getPower()*100), 
                                  frontLeftDrive.getPower() != 0 ? "🔄" : "");
            } else {
                telemetry.addData("FL", "✗ FAILED");
            }
            
            if (frontRightDrive != null) {
                telemetry.addData("FR", "%.0f%% %s", Math.abs(frontRightDrive.getPower()*100),
                                  frontRightDrive.getPower() != 0 ? "🔄" : "");
            } else {
                telemetry.addData("FR", "✗ FAILED");
            }
            
            if (backLeftDrive != null) {
                telemetry.addData("BL", "%.0f%% %s", Math.abs(backLeftDrive.getPower()*100),
                                  backLeftDrive.getPower() != 0 ? "🔄" : "");
            } else {
                telemetry.addData("BL", "✗ FAILED");
            }
            
            if (backRightDrive != null) {
                telemetry.addData("BR", "%.0f%% %s", Math.abs(backRightDrive.getPower()*100),
                                  backRightDrive.getPower() != 0 ? "🔄" : "");
            } else {
                telemetry.addData("BR", "✗ FAILED");
            }
            telemetry.addData("", "");
            
            // Shooter motor status
            telemetry.addData("=== SHOOTER MOTORS ===", "");
            if (shooterLeft != null) {
                telemetry.addData("LEFT", "%.0f%% %s", shooterLeft.getPower()*100,
                                  shooterLeft.getPower() > 0.01 ? "🔥" : "");
            } else {
                telemetry.addData("LEFT", "✗ FAILED");
            }
            
            if (shooterRight != null) {
                telemetry.addData("RIGHT", "%.0f%% %s", shooterRight.getPower()*100,
                                  shooterRight.getPower() > 0.01 ? "🔥" : "");
            } else {
                telemetry.addData("RIGHT", "✗ FAILED");
            }
            telemetry.addData("", "");
            
            // Intake motor status
            telemetry.addData("=== INTAKE MOTORS ===", "");
            if (intakeFront != null) {
                String arrow = "";
                if (intakeFront.getPower() > 0.01) arrow = "→";
                else if (intakeFront.getPower() < -0.01) arrow = "←";
                telemetry.addData("FRONT", "%.0f%% %s", Math.abs(intakeFront.getPower()*100), arrow);
            } else {
                telemetry.addData("FRONT", "✗ FAILED");
            }
            
            if (intakeBack != null) {
                String arrow = "";
                if (intakeBack.getPower() > 0.01) arrow = "→";
                else if (intakeBack.getPower() < -0.01) arrow = "←";
                telemetry.addData("BACK", "%.0f%% %s", Math.abs(intakeBack.getPower()*100), arrow);
            } else {
                telemetry.addData("BACK", "✗ FAILED");
            }
            telemetry.addData("", "");
            
            // Controls
            telemetry.addData("=== DRIVE TESTS ===", "");
            telemetry.addData("Y/X/B/A", "Test individual motors");
            telemetry.addData("D-Pad ↑/↓", "Test all drive motors");
            telemetry.addData("", "");
            telemetry.addData("=== SHOOTER TESTS ===", "");
            telemetry.addData("RT", "Both shooters");
            telemetry.addData("LT", "Left shooter only");
            telemetry.addData("RB", "Right shooter only");
            telemetry.addData("LB", "Stop shooters");
            telemetry.addData("", "");
            telemetry.addData("=== INTAKE TESTS ===", "");
            telemetry.addData("Guide", "Cycle: FWD->REV->STOP");
            telemetry.addData("Back", "Emergency stop");
            telemetry.addData("", "");
            
            // Final status
            if (driveMotorsOK && (slOK && srOK)) {
                telemetry.addData("🏆 STATUS", "✓ ALL SYSTEMS GO!");
            } else if (driveMotorsOK) {
                telemetry.addData("🏆 STATUS", "✓ READY (Reduced shooter)");
            } else {
                telemetry.addData("⚠ STATUS", "CHECK DRIVE MOTORS!");
            }
            
            telemetry.update();
        }
        
        // Safety: Stop all motors when OpMode ends
        if (frontLeftDrive != null) frontLeftDrive.setPower(0);
        if (frontRightDrive != null) frontRightDrive.setPower(0);
        if (backLeftDrive != null) backLeftDrive.setPower(0);
        if (backRightDrive != null) backRightDrive.setPower(0);
        if (shooterLeft != null) shooterLeft.setPower(0);
        if (shooterRight != null) shooterRight.setPower(0);
        if (intakeFront != null) intakeFront.setPower(0);
        if (intakeBack != null) intakeBack.setPower(0);
    }
}

