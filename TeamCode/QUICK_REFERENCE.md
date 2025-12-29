# Quick Reference - Mecanum Drive Controls

## Team Penguinauts 32240

**Gamepad: Logitech F310** (Set mode switch to **"D"**!)

### 🎮 Standard Mecanum Drive Controls

```
┌─────────────────────────────────────────────┐
│   PENGUINAUTS: MECANUM DRIVE               │
└─────────────────────────────────────────────┘

LEFT STICK:
  ↑ Forward        ↓ Backward
  ← Strafe Left    → Strafe Right

RIGHT STICK:
  ← Rotate Left    → Rotate Right

BUMPERS:
  L1 - Slow Mode (50%)
  R1 - Turbo Mode (100%)
  
DEFAULT SPEED: 75%
```

### 🧭 Field-Relative Drive Controls

```
┌─────────────────────────────────────────────┐
│   PENGUINAUTS: FIELD RELATIVE DRIVE        │
└─────────────────────────────────────────────┘

LEFT STICK:
  Drive in any direction (field-relative)

RIGHT STICK:
  ← Rotate Left    → Rotate Right

BUTTONS:
  A  - Reset field orientation
  L1 - Robot-relative mode (hold)
  R1 - Slow mode (50%)
  
DEFAULT SPEED: 75%
```

### ⚙️ Motor Test Controls

```
┌─────────────────────────────────────────────┐
│   PENGUINAUTS: MOTOR TEST                  │
└─────────────────────────────────────────────┘

FACE BUTTONS:
  Y - Front Right   X - Front Left
  B - Back Right    A - Back Left

D-PAD:
  UP   - All motors forward
  DOWN - All motors backward

TEST POWER: 50%
```

---

## Hardware Configuration Reminder

**Motor Ports (Control Hub):**
- Port 0: front_left_drive
- Port 1: front_right_drive
- Port 2: back_left_drive
- Port 3: back_right_drive

**IMU:** Built-in (name: "imu")

---

**Pro Tips:**
- Start with Motor Test to verify connections
- Use Slow Mode when aligning with game elements
- Field-Relative is easier once you get used to it!
- Press A in Field-Relative to reset if you get disoriented

**Team: Penguinauts 32240** | **Season: INTO THE DEEP 2024-2025**

