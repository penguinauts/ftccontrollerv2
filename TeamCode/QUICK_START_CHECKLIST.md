# 🚀 PENGUINAUTS QUICK START CHECKLIST
## Team 32240 - Driving in 5 Minutes!

Print this page and follow step-by-step! ✓ each box as you complete it.

---

## 📋 PART 1: HARDWARE SETUP (One-Time Setup)

### Control Hub Configuration

**Step 1: Connect to Control Hub**
- [ ] Turn on Control Hub (on robot)
- [ ] Turn on Driver Hub
- [ ] Connect Driver Hub to Control Hub WiFi:
  - Network name starts with: `DIRECT-` or `FTC-`
  - Password: `password` (or check hub label)

**Step 2: Create Robot Configuration**
- [ ] Open Driver Station app
- [ ] Tap menu (≡) → "Configure Robot"
- [ ] Tap "New"
- [ ] Name it: `Penguinauts_Robot`
- [ ] Select "Control Hub Portal" → "Control Hub"

**Step 3: Configure Motors (⚠️ MUST BE EXACT!)**
- [ ] Port 0: Motor → Name: `BL`
- [ ] Port 1: Motor → Name: `BR`
- [ ] Port 2: Motor → Name: `FL`
- [ ] Port 3: Motor → Name: `FR`

**Step 4: Save and Activate**
- [ ] Tap "Done"
- [ ] Save as: `Penguinauts_Robot`
- [ ] ⚠️ **TAP "ACTIVATE"** ← DON'T SKIP THIS!
- [ ] Verify: Shows "Active Configuration: Penguinauts_Robot"

---

## 🎮 PART 2: GAMEPAD SETUP

- [ ] Check mode switch on BACK of F310 = **"D"**
- [ ] Plug F310 USB cable into Driver Hub
- [ ] Look for 🎮✓ icon on Driver Station screen
- [ ] Test: Move sticks, watch values change

---

## 🧪 PART 3: MOTOR TEST (First Time Only)

**⚠️ PUT ROBOT ON BLOCKS (wheels off ground)!**

- [ ] Select OpMode: "Penguinauts: Motor Test"
- [ ] Press **INIT** button
- [ ] Check telemetry: Should say "Initialized"
- [ ] Press **START** button
- [ ] Press **D-PAD UP** on gamepad
- [ ] ✓ All 4 wheels spin FORWARD (away from you)
  - If any spin backward, STOP! (see troubleshooting)

**Test Each Motor:**
- [ ] Press **X** = Front Left spins
- [ ] Press **Y** = Front Right spins  
- [ ] Press **A** = Back Left spins
- [ ] Press **B** = Back Right spins

- [ ] Press **STOP** when done testing

---

## 🏎️ PART 4: START DRIVING!

**Put robot on GROUND in open area!**

### First Drive - Slow Mode

- [ ] Select OpMode: "Penguinauts: Mecanum Drive"
- [ ] Press **INIT**
- [ ] Press **START**
- [ ] **HOLD LEFT BUMPER** (slow mode 50%)

### Test Movements:

```
    YOU
     ↓
    
  [FL] [FR] ← Front
  
  [BL] [BR] ← Back
```

- [ ] LEFT STICK UP → Robot moves AWAY from you ✓
- [ ] LEFT STICK DOWN → Robot moves TOWARD you ✓
- [ ] LEFT STICK LEFT → Robot strafes LEFT ✓
- [ ] LEFT STICK RIGHT → Robot strafes RIGHT ✓
- [ ] RIGHT STICK LEFT → Robot rotates LEFT ✓
- [ ] RIGHT STICK RIGHT → Robot rotates RIGHT ✓

### Try Normal Speed:
- [ ] Release Left Bumper (75% speed)
- [ ] Practice driving around

### Try Turbo Mode:
- [ ] Hold Right Bumper (100% speed)
- [ ] Be careful!

---

## ✅ DAILY OPERATION (After Initial Setup)

Use this for every practice/match:

### Power On:
- [ ] Control Hub ON (LED solid)
- [ ] Driver Hub ON
- [ ] Battery >12V

### Connect:
- [ ] Driver Hub connected to Control Hub (WiFi)
- [ ] Gamepad connected (🎮✓)
- [ ] Mode switch = "D"

### Drive:
- [ ] Select: "Penguinauts: Mecanum Drive"
- [ ] Press: INIT → START
- [ ] **DRIVE!** 🎮

### Done:
- [ ] Press STOP
- [ ] Power off Control Hub
- [ ] Power off Driver Hub

---

## 🐛 QUICK TROUBLESHOOTING

**OpMode not found:**
- Restart Driver Station app
- Check code is deployed

**"Error initializing hardware":**
- Is configuration ACTIVATED? ←← Most common!
- Are motor names EXACT? (no spaces, underscores)

**Motors wrong direction:**
- Check you're looking at robot from FRONT
- May need to modify code (tell us!)

**Gamepad not working:**
- Mode switch = "D" (not X)
- Replug USB cable
- Check for 🎮✓ icon

**Can't connect to Control Hub:**
- Control Hub LED solid?
- WiFi password correct?
- Restart both hubs

---

## 📐 ORIENTATION REMINDER

**YOU stand HERE when testing:**

```
         👤 YOU
         ↓
         
   [FL]    [FR]  ← FRONT (closer to you)
   
   
   [BL]    [BR]  ← BACK (farther from you)
```

**"Forward" = robot moves AWAY from you**

---

## 🎯 GAMEPAD CONTROLS

```
LEFT STICK:
  ↑ = Forward (away from you)
  ↓ = Backward (toward you)
  ← = Strafe left
  → = Strafe right

RIGHT STICK:
  ← = Rotate left
  → = Rotate right

BUMPERS:
  LB = Slow (50%)
  RB = Turbo (100%)
```

---

## 📞 HELP

Stuck? Check these files:
- `DRIVER_HUB_SETUP.md` - Detailed setup guide
- `LOGITECH_F310_GUIDE.md` - Gamepad help
- `PENGUINAUTS_SETUP_GUIDE.md` - Complete guide

---

## ✨ YOU'RE READY!

Once all boxes are checked, you're ready to:
✓ Practice driving
✓ Run autonomous (when ready)
✓ Compete in matches!

**Good luck, Team Penguinauts! 🐧🤖🏆**

---

**Team Penguinauts 32240** | **INTO THE DEEP 2024-2025**

