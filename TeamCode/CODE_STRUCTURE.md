# Team Penguinauts 32240 - Code Structure

## 📁 Project Organization

```
V2/
├── TeamCode/
│   ├── src/main/java/org/firstinspires/ftc/teamcode/
│   │   ├── Penguinauts_MecanumDrive.java       ← Standard mecanum drive
│   │   ├── Penguinauts_FieldRelativeDrive.java ← Field-relative drive
│   │   ├── Penguinauts_MotorTest.java          ← Motor testing utility
│   │   ├── Penguinauts_AutoTemplate.java       ← Autonomous template
│   │   └── readme.md
│   ├── PENGUINAUTS_SETUP_GUIDE.md              ← Complete setup guide
│   ├── QUICK_REFERENCE.md                      ← Quick controls reference
│   └── CODE_STRUCTURE.md                       ← This file
└── FtcRobotController/
    └── (FTC SDK files - don't modify)
```

## 🎮 TeleOp OpModes

### 1. Penguinauts_MecanumDrive.java
**Purpose:** Basic robot-relative mecanum drive
**Best for:** Learning, testing, precise control

**Key Features:**
- Robot-relative control (like RC car)
- Three speed modes (Slow 50%, Normal 75%, Turbo 100%)
- Real-time telemetry display
- Motor power monitoring

**When to use:** 
- First time driving
- When precise alignment needed
- Driver prefers traditional RC-style control

### 2. Penguinauts_FieldRelativeDrive.java
**Purpose:** Advanced field-relative mecanum drive with IMU
**Best for:** Competition, experienced drivers

**Key Features:**
- Field-relative control (joystick direction relative to field)
- Robot-relative mode available (hold left bumper)
- IMU-based heading tracking
- Orientation reset (A button)
- Two speed modes (Normal 75%, Slow 50%)

**When to use:**
- Competition matches
- Complex field navigation
- When robot orientation changes frequently

### 3. Penguinauts_MotorTest.java
**Purpose:** Individual motor testing and diagnostics
**Best for:** Setup, troubleshooting

**Key Features:**
- Test each motor individually
- Test all motors together
- Verify motor directions
- Motor power monitoring

**When to use:**
- Initial robot setup
- After hardware changes
- Troubleshooting motor issues
- Before competition to verify connections

## 🤖 Autonomous OpModes

### 4. Penguinauts_AutoTemplate.java
**Purpose:** Template for creating autonomous routines
**Status:** Disabled by default (template)

**Key Features:**
- Encoder-based driving (precise distance)
- Time-based rotation
- Pre-built movement functions:
  - `driveForward(speed, inches)`
  - `strafeRight(speed, inches)` 
  - `rotateRight(speed, seconds)`

**How to use:**
1. Copy this file and rename it (e.g., `Penguinauts_Auto_LeftSide.java`)
2. Remove the `@Disabled` annotation
3. Customize the autonomous sequence
4. Calibrate the encoder constants for your robot

## 🔧 Key Constants to Tune

### Speed Multipliers (in TeleOp files)
```java
private static final double SLOW_MODE_MULTIPLIER = 0.5;   // Adjust 0.0-1.0
private static final double NORMAL_MODE_MULTIPLIER = 0.75; // Adjust 0.0-1.0
private static final double TURBO_MODE_MULTIPLIER = 1.0;   // Adjust 0.0-1.0
```

### Encoder Constants (in Auto files)
```java
private static final double COUNTS_PER_MOTOR_REV = 537.7;  // Check motor specs
private static final double WHEEL_DIAMETER_INCHES = 4.0;    // Measure your wheels
```

### IMU Orientation (in FieldRelativeDrive)
```java
RevHubOrientationOnRobot.LogoFacingDirection logoDirection = UP;
RevHubOrientationOnRobot.UsbFacingDirection usbDirection = FORWARD;
```

## 📝 Naming Conventions

All Team Penguinauts code follows these conventions:

- **Prefix:** `Penguinauts_` for all OpModes
- **TeleOp naming:** `Penguinauts_[Feature]Drive.java`
- **Auto naming:** `Penguinauts_Auto_[Strategy].java`
- **Utility naming:** `Penguinauts_[Purpose]Test.java`

## 🚀 Creating New OpModes

### For TeleOp:
1. Copy `Penguinauts_MecanumDrive.java`
2. Rename to `Penguinauts_[NewFeature].java`
3. Update the `@TeleOp(name="...")` annotation
4. Customize controls and features
5. Test thoroughly

### For Autonomous:
1. Copy `Penguinauts_AutoTemplate.java`
2. Rename to `Penguinauts_Auto_[Strategy].java`
3. Remove `@Disabled` annotation
4. Change `@Autonomous(name="...")` annotation
5. Customize the autonomous sequence
6. Test in practice field

## 🎯 Competition Checklist

Before each match:

- [ ] Motor Test passed - all motors working
- [ ] Driver practiced with chosen OpMode
- [ ] Encoder calibration verified (for auto)
- [ ] IMU orientation confirmed (for field-relative)
- [ ] Battery fully charged
- [ ] All code deployed to Robot Controller
- [ ] Correct OpMode selected on Driver Station

## 📚 Learning Resources

- **FTC Official Docs:** https://ftc-docs.firstinspires.org/
- **Game Manual 0:** https://gm0.org/ (community resource)
- **FTC Discord:** https://discord.gg/first-tech-challenge
- **REV Robotics Docs:** https://docs.revrobotics.com/

## 🐧 Team Contact

**Team:** Penguinauts  
**Team Number:** 32240  
**Season:** INTO THE DEEP (2024-2025)

---

*Keep this file updated as you add new OpModes and features!*

