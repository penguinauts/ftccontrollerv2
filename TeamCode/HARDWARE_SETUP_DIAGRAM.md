# Team Penguinauts 32240 - Hardware Setup Diagram

## 🤖 Robot Overview (Top View)

```
              FORWARD (Front of Robot)
                     ↑
                     
    ╔════════════════════════════════╗
    ║                                ║
    ║  🎯 INTAKE FRONT (IF) 🎯       ║  IF = Intake Front (Exp Port 2)
    ║         ⚙️ ↓ ↓ ↓ ⚙️              ║  goBILDA 5203-2402-0014
    ║                                ║
    ║  [FL Motor]      [FR Motor]    ║  FL = Front Left (Port 2)
    ║     ⚙️ /            \ ⚙️         ║  FR = Front Right (Port 3)
    ║                                ║
    ║       [Control Hub]            ║  
    ║      ┌─────────────┐           ║  
    ║      │  REV HUB    │           ║  
    ║      │   [IMU]     │           ║  IMU = Built-in
    ║      │  USB Port   │           ║  
    ║      └──────┬──────┘           ║  
    ║             │ I2C              ║
    ║      ┌──────┴──────┐           ║
    ║      │ EXP HUB     │           ║  Extension Hub
    ║      └─────────────┘           ║  
    ║                                ║
    ║  🎯 INTAKE MIDDLE & BACK 🎯    ║  IB = Intake Back (Exp Port 3)
    ║         ⚙️ ↓ ↓ ↓ ⚙️              ║  goBILDA 5203 Series
    ║                                ║  Drives both middle & back
    ║  [BL Motor]      [BR Motor]    ║  BL = Back Left (Port 0)
    ║     ⚙️ \            / ⚙️         ║  BR = Back Right (Port 1)
    ║                                ║
    ║    🎯 SHOOTER WHEEL (BACK) 🎯   ║
    ║   [SL Motor]    [SR Motor]     ║  SL = Shooter Left (Exp Port 0)
    ║      ⚙️ ══════════ ⚙️            ║  SR = Shooter Right (Exp Port 1)
    ║        Dual Motor Shooter       ║  5000 Series 12VDC Motors
    ╚════════════════════════════════╝

              BACKWARD (Back of Robot)
                     ↓
```

## 🔌 Control Hub Port Configuration

```
┌─────────────────────────────────────┐
│        REV Control Hub (Main)       │
├─────────────────────────────────────┤
│ MOTOR PORTS:                        │
│  [0] BL (Back Left)         ←─ ⚙️   │
│  [1] BR (Back Right)        ←─ ⚙️   │
│  [2] FL (Front Left)        ←─ ⚙️   │
│  [3] FR (Front Right)       ←─ ⚙️   │
│                                     │
│ SERVO PORTS:                        │
│  [0] (Available for future use)     │
│  [1] (Available for future use)     │
│  [2] (Available for future use)     │
│  [3] (Available for future use)     │
│  [4] (Available for future use)     │
│  [5] (Available for future use)     │
│                                     │
│ I2C PORTS:                          │
│  Internal: IMU (built-in)           │
│  [0] REV Expansion Hub ←─ Connected │
│  [1] (Available for sensors)        │
│  [2] (Available for sensors)        │
│  [3] (Available for sensors)        │
│                                     │
│ DIGITAL PORTS:                      │
│  [0-7] (Available for sensors)      │
│                                     │
│ ANALOG PORTS:                       │
│  [0-3] (Available for sensors)      │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│    REV Expansion Hub (Extension)    │
├─────────────────────────────────────┤
│ CONNECTION:                         │
│  Via I2C Port 0 on Control Hub      │
│  (Use REV I2C cable)                │
│                                     │
│ MOTOR PORTS:                        │
│  [0] SL (Shooter Left)      ←─ ⚙️   │
│  [1] SR (Shooter Right)     ←─ ⚙️   │
│  [2] (Available for future)         │
│  [3] (Available for future)         │
│                                     │
│ MOTOR SPECS:                        │
│  Model: 5000 Series 12VDC Motor     │
│  SKU: 5000-0002-4008                │
│  Shaft: 8mm REX™ Pinion             │
│  Purpose: Dual shooter wheel drive  │
│  Max Speed: High (low torque)       │
│                                     │
│ SERVO PORTS:                        │
│  [0-5] (Available for future use)   │
│                                     │
│ I2C PORTS:                          │
│  [0-3] (Available for sensors)      │
│                                     │
│ DIGITAL PORTS:                      │
│  [0-7] (Available for sensors)      │
│                                     │
│ ANALOG PORTS:                       │
│  [0-3] (Available for sensors)      │
└─────────────────────────────────────┘
```

## 🎡 Mecanum Wheel Orientation

**CRITICAL: Mecanum wheels MUST be installed correctly!**

### View from ABOVE the robot:

```
     FRONT
      ↑
      
  / ⚙️    ⚙️ \      The rollers form an "X" pattern
             when viewed from above
 ⚙️         ⚙️    
             
  \ ⚙️    ⚙️ /      ⚙️ = Wheel
             
     BACK
      ↓
```

### Wheel Details:

| Position     | Roller Direction | Notes                      |
|--------------|------------------|----------------------------|
| Front Left   | Rollers: `/`     | Runs to front-right (45°)  |
| Front Right  | Rollers: `\`     | Runs to front-left (45°)   |
| Back Left    | Rollers: `\`     | Runs to back-right (45°)   |
| Back Right   | Rollers: `/`     | Runs to back-left (45°)    |

**⚠️ IMPORTANT:** If wheels are installed incorrectly, the robot will not strafe properly!

## 🧭 Control Hub Mounting

The Control Hub has a built-in IMU (Inertial Measurement Unit) for field-relative driving.

### Default Configuration in Code:
```
Logo: Facing UP (toward sky)
USB:  Facing FORWARD (toward front of robot)
```

### If your Control Hub is mounted differently:

```
                  [LOGO]
                    
     ┌─────────────────────┐
     │                     │
     │    REV Hub          │
[USB]│                     │
     │                     │
     └─────────────────────┘
```

Update these lines in `Penguinauts_FieldRelativeDrive.java`:

```java
// Line 95-98
RevHubOrientationOnRobot.LogoFacingDirection logoDirection = UP;  // Change this
RevHubOrientationOnRobot.UsbFacingDirection usbDirection = FORWARD; // Change this
```

**Options:** UP, DOWN, LEFT, RIGHT, FORWARD, BACKWARD

## 🔋 Power Distribution

```
┌──────────────────────────┐
│   12V Battery            │
│   (Fully charged:        │
│   ~13.5V recommended)    │
└──────┬───────────────────┘
       │
       ├──→ Control Hub
       │    ├──→ 4x Drive Motors (FL, FR, BL, BR)
       │    └──→ Power to Expansion Hub
       │         └──→ 2x Shooter Motors (SL, SR)
       │         └──→ (Additional motors/servos)
       │
       └──→ Driver Hub (via WiFi)
            └──→ Gamepad 1
            └──→ Gamepad 2
```

## 📱 Connection Diagram

```
[Driver Station Phone]
       │
       │ WiFi Direct
       │ (Password: Set during pairing)
       ↓
[Control Hub Phone/Control Hub]
       │
       ├── Motor Port 0 ──→ Front Left Drive (FL)
       ├── Motor Port 1 ──→ Front Right Drive (FR)
       ├── Motor Port 2 ──→ Back Left Drive (BL)
       ├── Motor Port 3 ──→ Back Right Drive (BR)
       │
       └── I2C Port 0 ──→ [REV Expansion Hub]
                           ├── Motor Port 0 ──→ Shooter Left (SL)
                           └── Motor Port 1 ──→ Shooter Right (SR)
```

## 🎮 Gamepad Button Layout

```
           [Y]                    
     [X]   [B]         [LB]  [RB]
           [A]          
                        
    [LS]      [RS]     LS = Left Stick
                       RS = Right Stick
                       LB = Left Bumper
                       RB = Right Bumper
```

## ✅ Pre-Drive Checklist

Before testing your robot:

1. **Mechanical:**
   - [ ] All 4 mecanum wheels installed correctly (X pattern)
   - [ ] Drive motors securely mounted
   - [ ] Shooter wheel and motors installed on back
   - [ ] Both shooter motors (left & right) securely mounted
   - [ ] Motor cables connected to correct ports
   - [ ] Control Hub securely mounted
   - [ ] Expansion Hub securely mounted

2. **Electrical:**
   - [ ] Battery fully charged (>12.5V)
   - [ ] All drive motor connectors firmly seated (Main Hub 0-3)
   - [ ] Both shooter motor connectors firmly seated (Exp Hub 0-1)
   - [ ] I2C cable connecting Control Hub to Expansion Hub
   - [ ] Control Hub power switch ON
   - [ ] Expansion Hub power LED active
   - [ ] Control Hub LED shows activity

3. **Software:**
   - [ ] Robot Controller app running on Control Hub
   - [ ] Expansion Hub detected in configuration
   - [ ] All 6 motors configured with correct names
   - [ ] Configuration activated
   - [ ] Driver Station connected to Control Hub
   - [ ] OpMode visible in TeleOp menu

4. **Safety:**
   - [ ] Robot on blocks or held (for initial motor test)
   - [ ] Clear space around robot (especially behind for shooter)
   - [ ] NO objects near shooter wheel
   - [ ] Driver ready at Driver Station
   - [ ] Emergency stop plan in place
   - [ ] Shooter test with LOW power first

## 🔧 Troubleshooting Quick Reference

| Problem | Solution |
|---------|----------|
| Motor doesn't spin | Check cable connection, verify port in config |
| Wrong direction | Change `FORWARD` ↔ `REVERSE` in code |
| Can't strafe | Check mecanum wheel orientation (X pattern) |
| Robot drifts | May need motor power tuning or wheel alignment |
| IMU not working | Check Control Hub orientation in code |
| Can't connect | Check WiFi Direct pairing, restart devices |
| Expansion Hub not detected | Check I2C cable connection, verify in config |
| Shooter motors not working | Check Expansion Hub power, verify motor names |
| Shooter spins slowly | Check battery charge, verify motor direction |
| One shooter motor not working | Check connection, swap motors to test |

## 📞 Support Resources

- **REV Robotics:** https://docs.revrobotics.com/
- **FTC Control System:** https://ftc-docs.firstinspires.org/
- **Team Forum:** https://ftc-community.firstinspires.org/

---

**Team Penguinauts 32240** | Season: INTO THE DEEP 2024-2025

*Print this diagram and keep it with your robot for reference!*

