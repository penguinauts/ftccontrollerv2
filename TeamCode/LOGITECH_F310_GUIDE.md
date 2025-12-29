# Team Penguinauts 32240 - Logitech F310 Gamepad Guide

## 🎮 **Your Gamepad: Logitech F310**

The Logitech F310 is one of the most popular and reliable gamepads used in FTC. All your OpModes are already configured to work perfectly with it!

**Source:** [Game Manual 0 - Gamepad Usage](https://gm0.org/en/latest/docs/software/tutorials/gamepad.html)

---

## 🕹️ **Logitech F310 Layout**

```
                    Mode LED
                      ●
         [LB]                    [RB]
          ┌─────────────────────┐
         /                       \
        /    (D-Pad)              \
       │      ↑         [Y]        │
       │   ←  ●  →   [X]   [B]    │
       │      ↓         [A]        │
       │                           │
       │   [LS]              [RS]  │
       │    ●                 ●    │
        \                         /
         \  [Back] [Logo] [Start]/
          └─────────────────────┘

LB/RB = Left/Right Bumper (top buttons)
LS/RS = Left/Right Stick (press down to click)
```

---

## ⚙️ **IMPORTANT: Set to DirectInput Mode (D)**

**Before using, make sure the mode switch on the back is set to "D" (DirectInput):**

```
Back of Gamepad:
┌──────────────┐
│   [X] [D]    │  ← Switch should be on "D"
└──────────────┘
```

- **D (DirectInput)**: ✅ Use this for FTC
- **X (XInput)**: ❌ Don't use this

If the gamepad isn't responding, check this switch!

---

## 🎯 **Controls for Your OpModes**

### **Penguinauts: Mecanum Drive**

```
╔═══════════════════════════════════════╗
║  LOGITECH F310 - MECANUM DRIVE       ║
╚═══════════════════════════════════════╝

LEFT STICK (LS):
  ↑ Push Forward    = Robot moves forward
  ↓ Pull Back       = Robot moves backward
  ← Push Left       = Robot strafes left
  → Push Right      = Robot strafes right
  
  Diagonal = Combined movement!

RIGHT STICK (RS):
  ← Push Left       = Robot rotates left
  → Push Right      = Robot rotates right

BUMPERS:
  LB = Slow Mode (50% speed)
  RB = Turbo Mode (100% speed)
  
  Neither = Normal Mode (75% speed)

TIP: You can move sticks simultaneously!
      Left stick forward + Right stick left
      = Drive forward while rotating
```

### **Penguinauts: Field Relative Drive**

```
╔═══════════════════════════════════════╗
║  LOGITECH F310 - FIELD RELATIVE      ║
╚═══════════════════════════════════════╝

LEFT STICK (LS):
  Any Direction = Robot moves that direction
                  (relative to field, not robot!)

RIGHT STICK (RS):
  ← → = Rotate robot

FACE BUTTONS:
  [A] = Reset field orientation
        (Press when robot facing forward)

BUMPERS:
  LB = Switch to Robot-Relative mode (hold)
  RB = Slow Mode (50% speed)
  
  Neither = Normal Field-Relative (75%)
```

### **Penguinauts: Motor Test**

```
╔═══════════════════════════════════════╗
║  LOGITECH F310 - MOTOR TEST          ║
╚═══════════════════════════════════════╝

FACE BUTTONS (test individual motors):
  [Y] = Front Right motor
  [X] = Front Left motor
  [B] = Back Right motor
  [A] = Back Left motor

D-PAD (test all motors):
  ↑ = All motors FORWARD
  ↓ = All motors BACKWARD

Each motor runs at 50% power while button held.
```

---

## 📋 **Logitech F310 Specifications**

| Feature | Status |
|---------|--------|
| **Compatibility** | ✅ FTC Legal |
| **Connection** | USB wired (reliable!) |
| **Dual Analog Sticks** | ✅ Yes |
| **D-Pad** | ✅ Yes (8-way) |
| **Triggers** | ✅ Yes (analog) |
| **Rumble** | ❌ No rumble motors |
| **LED** | ❌ No RGB LED |
| **Mode Switch** | ✅ D/X switch on back |
| **Reliability** | ⭐⭐⭐⭐⭐ Excellent |

**Good News:** The F310 is extremely reliable because:
- Wired connection (no interference)
- No batteries needed
- Durable construction
- Widely used in FTC

**Note:** The F310 doesn't have rumble or LED features, but these aren't needed for basic robot operation. The gamepad feedback code in the SDK won't do anything with this gamepad, but that's totally fine!

---

## 🔧 **Driver Station Setup**

### **Connecting the F310:**

1. **Plug into Driver Hub/Phone:**
   - Use the USB cable to plug F310 into Driver Hub
   - Or use USB-C adapter for phone
   - Should detect automatically

2. **Check Connection:**
   - Open Driver Station app
   - Look for gamepad icon (should show "✓")
   - Move sticks - values should appear on screen

3. **Assign as Gamepad 1 or 2:**
   - Gamepad 1 = Primary driver (your current code uses this)
   - Gamepad 2 = Secondary operator (for future mechanisms)

### **Connection Status Icons:**

```
Driver Station Screen:

┌─────────────────────┐
│ [🎮✓] [🎮 ]        │  
│  GP1   GP2          │
└─────────────────────┘

🎮✓ = Connected
🎮  = Not connected
```

---

## 💡 **Pro Tips for F310**

### **1. Dead Zone (Built into Code)**
The sticks won't register tiny movements - this prevents drift. If you notice the robot moving when sticks are centered, the gamepad may need calibration.

### **2. Analog Triggers**
The F310 has analog triggers (LT/RT) that weren't used in your current code. You can add these later for fine control of mechanisms:
```java
double triggerValue = gamepad1.left_trigger;  // Value 0.0 to 1.0
```

### **3. Stick Precision**
For precise movements:
- Use slow mode (LB)
- Make small stick movements
- Practice smooth inputs

### **4. Two-Handed Operation**
Best technique:
- Left hand on left stick (drive/strafe)
- Right hand on right stick (rotate)
- Right fingers reach bumpers for speed control

### **5. Mode Switch**
Always check the D/X switch before competition:
- **D = DirectInput** ✅ (Use this!)
- **X = XInput** ❌ (Won't work properly)

---

## 🎓 **Driver Training Exercises**

### **Exercise 1: Stick Sensitivity (5 min)**
- Start in Slow Mode (hold LB)
- Practice making smallest possible movements
- Goal: Move just 1 foot forward

### **Exercise 2: Independent Control (10 min)**
- Drive forward while rotating (both sticks)
- Strafe sideways while rotating
- Circle around a cone while facing it

### **Exercise 3: Speed Transitions (10 min)**
- Drive in slow mode
- Release LB for normal speed
- Press RB for turbo
- Practice smooth transitions

### **Exercise 4: Precision Box (15 min)**
- Mark a 3' × 3' square
- Navigate the perimeter using only strafing
- Don't cross the lines!

---

## 🔍 **Troubleshooting F310**

| Problem | Solution |
|---------|----------|
| **Gamepad not detected** | • Check USB connection<br>• Verify mode switch on "D"<br>• Replug USB cable<br>• Restart Driver Station app |
| **Robot not responding** | • Check gamepad icon has ✓<br>• Verify OpMode is started<br>• Test with Motor Test OpMode |
| **Sticks seem backwards** | • This is normal! Forward = negative value<br>• Code already handles this |
| **Robot drifts when released** | • Normal with worn sticks<br>• Code has dead zone built in<br>• Try different gamepad if severe |
| **Buttons don't work** | • Verify mode switch on "D"<br>• Check button mapping in code<br>• Test buttons in Driver Station |

---

## 📊 **Gamepad Values Reference**

When you look at the Driver Station telemetry, here's what the values mean:

```
Gamepad 1:
  left_stick_y: -1.0 to 1.0   (forward = negative!)
  left_stick_x: -1.0 to 1.0   (left = negative)
  right_stick_x: -1.0 to 1.0  (left = negative)
  
  left_bumper: true/false
  right_bumper: true/false
  
  a, b, x, y: true/false
  
  dpad_up/down/left/right: true/false
```

**Note:** The code handles the negative values automatically - you don't need to worry about it!

---

## 🎯 **Competition Day Checklist**

**Before Each Match:**
- [ ] F310 plugged into Driver Hub
- [ ] Mode switch set to "D"
- [ ] Driver Station shows gamepad connected (✓)
- [ ] Test sticks in Driver Station (move and watch values)
- [ ] Test buttons in Motor Test OpMode
- [ ] Driver is comfortable and ready

**Spare Gamepad:**
- [ ] Bring a backup F310 to competition
- [ ] Pre-test backup before event
- [ ] Keep in robot box

---

## 🆚 **Why F310 is Great for FTC**

✅ **Advantages:**
1. **Wired = Reliable** - No wireless interference
2. **No batteries** - Never runs out during match
3. **Durable** - Handles drops and impacts
4. **Widely used** - Easy to find help/examples
5. **Affordable** - ~$25, good for spares
6. **Consistent** - Same feel every time

❌ **Disadvantages:**
1. **No rumble** - Can't use haptic feedback
2. **Cable** - Can get tangled (organize it!)
3. **No wireless** - Driver must be near hub

**Overall:** The F310 is one of the best choices for FTC! You made a great decision.

---

## 📚 **Additional Resources**

- **Game Manual 0 - Gamepad Guide:** https://gm0.org/en/latest/docs/software/tutorials/gamepad.html
- **Logitech F310 Manual:** Check Logitech website
- **FTC Control System:** https://ftc-docs.firstinspires.org/

---

## 🐧 **For Team Penguinauts 32240**

Your code is **100% compatible** with the Logitech F310. No changes needed!

All three OpModes work perfectly:
- ✅ Penguinauts: Mecanum Drive
- ✅ Penguinauts: Field Relative Drive  
- ✅ Penguinauts: Motor Test

**Just plug it in and drive!** 🎮🤖

---

*Keep this guide handy during practice and competition!*

**Team Penguinauts 32240** | **Season: INTO THE DEEP 2024-2025**

