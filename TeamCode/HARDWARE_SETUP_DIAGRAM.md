# Team Penguinauts 32240 - Hardware Setup Diagram

## 🤖 Robot Overview (Top View)

```
              FORWARD (Front of Robot)
                     ↑
                     
    ╔════════════════════════════════╗
    ║                                ║
    ║  [FL Motor]      [FR Motor]    ║  FL = Front Left (Port 0)
    ║     ⚙️ /            \ ⚙️         ║  FR = Front Right (Port 1)
    ║                                ║
    ║       [Control Hub]            ║  
    ║      ┌─────────────┐           ║  
    ║      │  REV HUB    │           ║  
    ║      │   [IMU]     │           ║  IMU = Built-in
    ║      │  USB Port   │           ║  
    ║      └─────────────┘           ║  
    ║                                ║
    ║  [BL Motor]      [BR Motor]    ║  BL = Back Left (Port 2)
    ║     ⚙️ \            / ⚙️         ║  BR = Back Right (Port 3)
    ║                                ║
    ╚════════════════════════════════╝

              BACKWARD (Back of Robot)
                     ↓
```

## 🔌 Control Hub Port Configuration

```
┌─────────────────────────────────────┐
│        REV Control Hub              │
├─────────────────────────────────────┤
│ MOTOR PORTS:                        │
│  [0] front_left_drive  (FL) ←─ ⚙️   │
│  [1] front_right_drive (FR) ←─ ⚙️   │
│  [2] back_left_drive   (BL) ←─ ⚙️   │
│  [3] back_right_drive  (BR) ←─ ⚙️   │
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
│  [0] (Available for sensors)        │
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
       │    └──→ 4x Motors
       │    └──→ Servos (when added)
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
       ├── USB ──→ Motor 0 (FL)
       ├── USB ──→ Motor 1 (FR)
       ├── USB ──→ Motor 2 (BL)
       └── USB ──→ Motor 3 (BR)
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
   - [ ] Motors securely mounted
   - [ ] Motor cables connected to correct ports (0-3)
   - [ ] Control Hub securely mounted

2. **Electrical:**
   - [ ] Battery fully charged (>12.5V)
   - [ ] All motor connectors firmly seated
   - [ ] Control Hub power switch ON
   - [ ] Control Hub LED shows activity

3. **Software:**
   - [ ] Robot Controller app running on Control Hub
   - [ ] Motors configured with correct names
   - [ ] Configuration activated
   - [ ] Driver Station connected to Control Hub
   - [ ] OpMode visible in TeleOp menu

4. **Safety:**
   - [ ] Robot on blocks or held (for initial motor test)
   - [ ] Clear space around robot
   - [ ] Driver ready at Driver Station
   - [ ] Emergency stop plan in place

## 🔧 Troubleshooting Quick Reference

| Problem | Solution |
|---------|----------|
| Motor doesn't spin | Check cable connection, verify port in config |
| Wrong direction | Change `FORWARD` ↔ `REVERSE` in code |
| Can't strafe | Check mecanum wheel orientation (X pattern) |
| Robot drifts | May need motor power tuning or wheel alignment |
| IMU not working | Check Control Hub orientation in code |
| Can't connect | Check WiFi Direct pairing, restart devices |

## 📞 Support Resources

- **REV Robotics:** https://docs.revrobotics.com/
- **FTC Control System:** https://ftc-docs.firstinspires.org/
- **Team Forum:** https://ftc-community.firstinspires.org/

---

**Team Penguinauts 32240** | Season: INTO THE DEEP 2024-2025

*Print this diagram and keep it with your robot for reference!*

