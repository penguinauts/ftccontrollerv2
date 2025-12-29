# Team Penguinauts 32240 - Robot Programming Guide

Welcome to Team Penguinauts' robot programming setup! This guide will help you get your mecanum drive robot up and running.

## 📋 Hardware Configuration

### Motor Configuration on Control Hub
Before running any OpModes, you need to configure your motors in the Robot Controller app:

1. **Connect to your Control Hub** via WiFi
2. **Open the Robot Controller app** on the Control Hub
3. **Go to Configure → New Configuration**
4. **Select Control Hub** and configure the motors as follows:

   | Port | Motor Name         | Type                |
   |------|--------------------|---------------------|
   | 0    | front_left_drive   | GoBILDA 5202/5203 (or your motor type) |
   | 1    | front_right_drive  | GoBILDA 5202/5203 |
   | 2    | back_left_drive    | GoBILDA 5202/5203 |
   | 3    | back_right_drive   | GoBILDA 5202/5203 |

5. **Configure the IMU** (for field-relative drive):
   - The Control Hub has a built-in IMU
   - Name it: `imu`

6. **Save the configuration** with a meaningful name like "Penguinauts_Robot"
7. **Activate the configuration**

### Control Hub Orientation
For field-relative drive to work correctly, you need to tell the code how your Control Hub is mounted:

In `Penguinauts_FieldRelativeDrive.java`, update these lines (around line 95-98):

```java
RevHubOrientationOnRobot.LogoFacingDirection logoDirection = 
        RevHubOrientationOnRobot.LogoFacingDirection.UP;
RevHubOrientationOnRobot.UsbFacingDirection usbDirection = 
        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
```

**Logo Direction Options:**
- `UP` - Logo facing up (recommended)
- `DOWN` - Logo facing down
- `LEFT` - Logo facing left
- `RIGHT` - Logo facing right
- `FORWARD` - Logo facing forward
- `BACKWARD` - Logo facing backward

**USB Direction Options:**
- `FORWARD` - USB port facing forward (robot front)
- `BACKWARD` - USB port facing backward (robot back)
- `LEFT` - USB port facing left
- `RIGHT` - USB port facing right
- `UP` - USB port facing up
- `DOWN` - USB port facing down

## 🚀 OpModes Available

### 1. **Penguinauts: Motor Test** ⚙️
**Purpose:** Verify all motors are connected and spinning correctly.

**Controls:**
- `X` - Test front left motor
- `Y` - Test front right motor
- `A` - Test back left motor
- `B` - Test back right motor
- `DPAD UP` - All motors forward
- `DPAD DOWN` - All motors backward

**Start Here!** Run this first to ensure your motors are working correctly.

### 2. **Penguinauts: Mecanum Drive** 🎮
**Purpose:** Standard robot-relative mecanum drive control.

**Controls:**
- `Left Stick` - Drive forward/backward and strafe left/right
- `Right Stick` - Rotate left/right
- `Left Bumper` - Slow mode (50% speed)
- `Right Bumper` - Turbo mode (100% speed)
- Default speed: 75%

**Best For:** Beginners, precise control, when you want to drive like an RC car.

### 3. **Penguinauts: Field Relative Drive** 🧭
**Purpose:** Advanced field-relative mecanum drive (direction relative to field, not robot).

**Controls:**
- `Left Stick` - Drive in any direction (field-relative)
- `Right Stick` - Rotate left/right
- `A Button` - Reset field orientation
- `Left Bumper` - Switch to robot-relative mode
- `Right Bumper` - Slow mode (50% speed)
- Default speed: 75%

**Best For:** Experienced drivers, competition play, intuitive field navigation.

## 📝 Testing Procedure

### Step 1: Motor Testing
1. Deploy the code to your Robot Controller
2. Connect your Driver Station to the Robot Controller
3. Select "Penguinauts: Motor Test" from the TeleOp menu
4. Press START
5. Test each motor individually:
   - Press `DPAD UP` - all wheels should move forward
   - If any wheel moves backward, you need to flip its direction in the code
6. **Troubleshooting:**
   - If motors don't spin, check your hardware configuration
   - If motors spin in wrong direction, see "Adjusting Motor Directions" below

### Step 2: Basic Drive Testing
1. Select "Penguinauts: Mecanum Drive"
2. Press START
3. Test movements:
   - Push left stick forward - robot moves forward
   - Push left stick left/right - robot strafes
   - Push right stick left/right - robot rotates
4. Test speed modes with bumpers

### Step 3: Field-Relative Testing (Optional)
1. Select "Penguinauts: Field Relative Drive"
2. Press START
3. Drive around and notice the controls stay oriented to the field
4. Press `A` to reset orientation when needed

## 🔧 Adjusting Motor Directions

If motors are spinning in the wrong direction, edit the respective OpMode file and change the motor direction:

```java
// Change FORWARD to REVERSE or vice versa
frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
backRightDrive.setDirection(DcMotor.Direction.FORWARD);
```

**Standard Configuration (most robots):**
- Left motors: REVERSE
- Right motors: FORWARD

## 🎯 Understanding Mecanum Drive

Mecanum wheels have rollers at 45° angles that allow the robot to move in any direction:

```
    Front
 [FL]  [FR]    FL = Front Left
              FR = Front Right
 [BL]  [BR]    BL = Back Left
              BR = Back Right
    Back
```

**Wheel Roller Orientation (looking from above):**
- Front Left: Rollers point to front-right (/)
- Front Right: Rollers point to front-left (\)
- Back Left: Rollers point to back-right (\)
- Back Right: Rollers point to back-left (/)

This creates an "X" pattern when viewed from above.

## ⚡ Speed Modes Explained

### Robot-Relative Drive:
- **Slow Mode (50%)**: Precise movements, good for aligning with game elements
- **Normal Mode (75%)**: Default, balanced speed and control
- **Turbo Mode (100%)**: Maximum speed, use when you need to cross the field quickly

### Field-Relative Drive:
- **Slow Mode (50%)**: Precise positioning
- **Normal Mode (75%)**: Default field-relative driving
- **Robot-Relative Mode**: Hold left bumper for traditional RC-style control

## 📚 Next Steps

1. **Practice driving** in an open area
2. **Tune speed multipliers** if needed (edit the `MULTIPLIER` constants in the code)
3. **Add autonomous routines** for competition
4. **Add additional mechanisms** (arms, claws, etc.) to your OpModes

## 🐧 Team Penguinauts 32240

Good luck with your robot! Remember:
- Test thoroughly before competition
- Practice, practice, practice!
- Have fun and be creative!

For FTC documentation and resources:
- Official Docs: https://ftc-docs.firstinspires.org/
- Game Manual 0: https://gm0.org/
- FTC Discord: https://discord.gg/first-tech-challenge

---
*Created for Team Penguinauts 32240 - INTO THE DEEP Season 2024-2025*

