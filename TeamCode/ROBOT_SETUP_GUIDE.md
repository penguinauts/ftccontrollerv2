# 🔧 ROBOT SETUP GUIDE

**Team Penguinauts 32240** | **Complete Technical Reference**

---

## 📋 **TABLE OF CONTENTS**

1. [Hardware Configuration](#hardware-configuration)
2. [Driver Station Setup](#driver-station-setup)
3. [Motor Configuration](#motor-configuration)
4. [OpMode Reference](#opmode-reference)
5. [Advanced Features](#advanced-features)

---

## 🔌 **HARDWARE CONFIGURATION**

### **Control Hub (Main Hub)**

**Motor Ports:**
- Port 0: `BL` (Back Left drive)
- Port 1: `BR` (Back Right drive)
- Port 2: `FL` (Front Left drive)
- Port 3: `FR` (Front Right drive)

**I2C Ports:**
- Port 0: Expansion Hub (connected via I2C cable)
- Internal: IMU (built-in)

### **Expansion Hub (Extension Hub)**

**Motor Ports:**
- Port 0: `SL` (Shooter Left) - 5000 Series 12VDC
- Port 1: `SR` (Shooter Right) - 5000 Series 12VDC
- Port 2: `IF` (Intake Front) - goBILDA 5203-2402-0014
- Port 3: `IB` (Intake Back) - goBILDA 5203 Series

**Connection:** Via I2C to Control Hub Port 0

---

## 📱 **DRIVER STATION SETUP**

### **Step 1: Connect to Robot**
1. Turn on Control Hub
2. On Driver Station phone/tablet:
   - Settings → Wi-Fi
   - Connect to robot network (format: FTC-XXXX)
3. Open FTC Driver Station app

### **Step 2: Configure Motors**

**Control Hub Motors:**
1. Tap ⋮ menu → "Configure Robot"
2. Tap "Control Hub Portal"
3. Tap "Motors"
4. Configure each port:

| Port | Name | Type | Direction |
|------|------|------|-----------|
| 0 | `BL` | REV HD Hex Motor | REVERSE |
| 1 | `BR` | REV HD Hex Motor | FORWARD |
| 2 | `FL` | REV HD Hex Motor | REVERSE |
| 3 | `FR` | REV HD Hex Motor | FORWARD |

**Expansion Hub Motors:**
1. Go back, tap "Expansion Hub Portal 1"
2. Tap "Motors"
3. Configure each port:

| Port | Name | Type | Direction |
|------|------|------|-----------|
| 0 | `SL` | 5000 Series 12VDC | REVERSE |
| 1 | `SR` | 5000 Series 12VDC | FORWARD |
| 2 | `IF` | goBILDA 5203 series | FORWARD |
| 3 | `IB` | goBILDA 5203 series | FORWARD |

5. Tap "Done" to save configuration

### **Step 3: Verify Configuration**
1. Select any OpMode
2. Press INIT
3. Check telemetry shows all motors "✓ Found"

---

## 🎮 **OPMODE REFERENCE**

### **1. Penguinauts: Mecanum Drive** (Main TeleOp)
**Use For:** Matches, robot-relative driving

**Features:**
- Mecanum drive (omnidirectional)
- Robot-relative control
- 3 speed modes (Slow/Normal/Turbo)
- Full intake and shooter control

**Controls:**
- Drive: Left/Right sticks + bumpers
- Intake: Left Trigger (collect), Y (eject), X (stop)
- Shooter: Right Trigger (start), B (stop)

---

### **2. Penguinauts: Field Relative Drive** (Advanced TeleOp)
**Use For:** Experienced drivers, field-relative control

**Features:**
- Field-relative driving (uses IMU)
- Left stick moves in field direction regardless of robot rotation
- Same intake/shooter controls as Mecanum Drive

**Additional Controls:**
- A Button: Reset field orientation
- Left Bumper (hold): Switch to robot-relative mode temporarily

---

### **3. Penguinauts: Pre-Match Check** (Testing)
**Use For:** Pre-match validation, troubleshooting

**Tests:**
- All 4 drive motors individually
- Both shooter motors (together and individually)
- Both intake motors (forward, reverse, stop)

**Controls:**
- Y/X/B/A: Test individual drive motors
- D-Pad Up/Down: Test all drive motors
- Right Trigger: Both shooters
- Left Trigger: Left shooter only
- Right Bumper: Right shooter only
- Left Bumper: Stop shooters
- Guide Button: Cycle intake (FWD→REV→STOP)
- Back Button: Emergency stop intake

---

### **4. Penguinauts: Shooter Test** (Diagnostics)
**Use For:** Shooter-specific testing and tuning

**Features:**
- Test individual shooter motors
- Adjust speed with D-Pad
- Real-time speed display

---

## ⚙️ **ADVANCED FEATURES**

### **FTC Dashboard (Real-Time Tuning)**

**Access:**
1. Connect laptop to robot WiFi
2. Open browser: `http://192.168.43.1:8080/dash`

**Adjustable Variables:**
- `SHOOTER_POWER` (0.0 to 1.0) - Default: 1.0
- `INTAKE_POWER` (0.0 to 1.0) - Default: 1.0

**When to Adjust:**
- Shooter too weak/strong: Adjust SHOOTER_POWER
- Intake too weak/strong: Adjust INTAKE_POWER
- Changes apply immediately (no restart needed)

---

### **Fault Tolerance**

All systems are fault-tolerant:

**If one shooter motor fails:**
- System continues with one motor (reduced power)
- Telemetry shows: "⚠ ONE MOTOR ONLY"

**If one intake motor fails:**
- System continues with one motor (reduced efficiency)
- Telemetry shows: "⚠ FRONT ONLY" or "⚠ BACK ONLY"

**If both motors of a system fail:**
- Other systems continue to work normally
- Drive is unaffected

---

## 📊 **MOTOR SPECIFICATIONS**

### **Drive Motors**
- Type: REV HD Hex Motors (mecanum configuration)
- Mode: RUN_USING_ENCODER
- Zero Power: BRAKE
- Power Range: -1.0 to 1.0

### **Shooter Motors**
- Type: 5000 Series 12VDC Motor with 8mm REX Pinion Shaft
- SKU: 5000-0002-4008
- Mode: RUN_WITHOUT_ENCODER
- Zero Power: FLOAT
- Power Range: 0.0 to 1.0 (forward only)

### **Intake Motors**
- Front: goBILDA 5203-2402-0014
- Back: goBILDA 5203 Series
- Mode: RUN_WITHOUT_ENCODER
- Zero Power: BRAKE
- Power Range: -1.0 to 1.0 (bidirectional)

---

## 🔄 **SOFTWARE UPDATE PROCEDURE**

1. Connect laptop to robot via USB or WiFi
2. Open Android Studio
3. Build → Make Project
4. Run → Run 'TeamCode'
5. Wait for "BUILD SUCCESSFUL"
6. Code deploys automatically to robot

---

## 📞 **SUPPORT RESOURCES**

**Documentation:**
- QUICK_START.md - Quick reference for drivers
- TROUBLESHOOTING.md - Problem solving guide
- This file - Complete technical reference

**Code Files:**
- `Penguinauts_MecanumDrive.java` - Main robot-relative drive
- `Penguinauts_FieldRelativeDrive.java` - Field-relative drive
- `Penguinauts_PreMatchCheck.java` - Pre-match testing
- `Penguinauts_ShooterTest.java` - Shooter diagnostics

---

**Last Updated:** January 13, 2026  
**Team:** Penguinauts 32240  
**Season:** Into The Deep 2024-2025

