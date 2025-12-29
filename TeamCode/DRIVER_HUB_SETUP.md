# Team Penguinauts 32240 - Driver Hub Configuration Guide

## 🤖 **YOUR ROBOT ORIENTATION** (IMPORTANT!)

### **Standing in Front of Robot, Facing the Robot:**

```
         YOU (Driver/Observer)
              👤
              ↓ Looking at robot
              
    ╔═══════════════════════╗
    ║                       ║
    ║  [FL]          [FR]   ║  ← FRONT (closer to you)
    ║   ⚙️             ⚙️    ║
    ║                       ║
    ║   [Control Hub]       ║
    ║                       ║
    ║  [BL]          [BR]   ║  ← BACK (farther from you)
    ║   ⚙️             ⚙️    ║
    ║                       ║
    ╚═══════════════════════╝

FL = Front Left (your left, closer to you)
FR = Front Right (your right, closer to you)
BL = Back Left (your left, farther from you)
BR = Back Right (your right, farther from you)
```

**When robot drives FORWARD (away from you):**
- Front wheels move away from you
- Back wheels move away from you
- Robot moves in the direction the front wheels face

---

## 📱 **DRIVER HUB SETUP - COMPLETE GUIDE**

### **What is the Driver Hub?**
The Driver Hub is your control center - it connects to your Control Hub and lets you drive the robot with your gamepad.

```
[Driver Hub] ←→ WiFi ←→ [Control Hub on Robot]
     ↓                        ↓
[Logitech F310]          [4 Motors]
  (USB Cable)
```

---

## 🔧 **STEP-BY-STEP CONFIGURATION**

### **PART 1: First Time Setup (Do Once)**

#### **Step 1: Turn on Control Hub (on robot)**
1. Make sure battery is connected (>12V)
2. Press the power button on Control Hub
3. Wait for LED to turn solid (about 30 seconds)
4. Control Hub creates WiFi network automatically

#### **Step 2: Turn on Driver Hub**
1. Press power button on Driver Hub
2. Wait for it to boot up (about 45 seconds)
3. Driver Station app should auto-start
   - If not, tap the Driver Station icon

#### **Step 3: Connect Driver Hub to Control Hub (WiFi Direct)**

**On Driver Hub:**
1. Swipe down from top (notification area)
2. Tap WiFi icon
3. Look for network starting with:
   - `DIRECT-` or
   - Your team number or
   - "FTC-" followed by letters/numbers
4. Tap the Control Hub network
5. **IMPORTANT:** First time will ask for password
   - Default password: **`password`** (or check your Control Hub label)
6. Wait for connection (10-20 seconds)

**Connection Success:**
- Driver Station app will show:
  - **Status: Connected** (green text)
  - Robot battery voltage (e.g., 13.2V)
  - Ping time (e.g., 5ms)

---

### **PART 2: Robot Hardware Configuration (Do Once)**

Now we configure which motors are connected to which ports!

#### **Step 1: Open Configuration on Driver Hub**

1. On Driver Station app, tap **≡** (menu icon, top right)
2. Tap **"Configure Robot"**
3. You'll see list of saved configurations (might be empty)

#### **Step 2: Create New Configuration**

1. Tap **"New"** button
2. Give it a name: **`Penguinauts_Robot`**
3. Tap **"Control Hub Portal"**
4. Select **"Control Hub"** from the list

#### **Step 3: Configure Motors (THE IMPORTANT PART!)**

You'll see a screen showing Control Hub ports.

**Motor Ports (0-3):**

Configure each port exactly like this:

```
┌─────────────────────────────────────────┐
│ Control Hub - Motor Ports               │
├─────────────────────────────────────────┤
│ Port 0: [+ Add Device]                  │
│ Port 1: [+ Add Device]                  │
│ Port 2: [+ Add Device]                  │
│ Port 3: [+ Add Device]                  │
└─────────────────────────────────────────┘
```

**For Port 0 (Front Left Motor):**
1. Tap **"Port 0"** or **"+ Add Device"**
2. Select device type: **"Motor"**
3. Choose motor model: **"GoBILDA 5202/5203"** (or your actual motor)
4. Name it: **`front_left_drive`** ⚠️ MUST BE EXACT!
5. Tap **✓** (checkmark) to save

**For Port 1 (Front Right Motor):**
1. Tap **"Port 1"**
2. Select: **"Motor"**
3. Choose: **"GoBILDA 5202/5203"**
4. Name: **`front_right_drive`** ⚠️ MUST BE EXACT!
5. Tap **✓**

**For Port 2 (Back Left Motor):**
1. Tap **"Port 2"**
2. Select: **"Motor"**
3. Choose: **"GoBILDA 5202/5203"**
4. Name: **`back_left_drive`** ⚠️ MUST BE EXACT!
5. Tap **✓**

**For Port 3 (Back Right Motor):**
1. Tap **"Port 3"**
2. Select: **"Motor"**
3. Choose: **"GoBILDA 5202/5203"**
4. Name: **`back_right_drive`** ⚠️ MUST BE EXACT!
5. Tap **✓**

**Final Result Should Look Like:**
```
┌─────────────────────────────────────────┐
│ Control Hub - Motor Ports               │
├─────────────────────────────────────────┤
│ Port 0: front_left_drive    [GoBILDA]  │
│ Port 1: front_right_drive   [GoBILDA]  │
│ Port 2: back_left_drive     [GoBILDA]  │
│ Port 3: back_right_drive    [GoBILDA]  │
└─────────────────────────────────────────┘
```

#### **Step 4: Configure IMU (Built-in sensor)**

Scroll down in the configuration screen:

1. Find **"I2C Bus 0"** or **"Built-in IMU"**
2. The IMU should already be there as **"imu"**
3. If not, tap to add:
   - Type: **"IMU"**
   - Name: **`imu`** ⚠️ MUST BE EXACT!

#### **Step 5: Save Configuration**

1. Tap **"Done"** (top right)
2. Enter config name: **`Penguinauts_Robot`**
3. Tap **"OK"**
4. ⚠️ **CRITICAL:** Tap **"Activate"** or select the radio button
   - Configuration MUST be activated to work!
5. Should show: **"Active Configuration: Penguinauts_Robot"**

---

### **PART 3: Connect Your Gamepad**

#### **Step 1: Plug in Logitech F310**
1. Take USB cable from F310 gamepad
2. Plug into Driver Hub USB port
3. Check mode switch on BACK of gamepad = **"D"**

#### **Step 2: Verify Connection**
On Driver Station screen, look for:
```
[🎮✓]      [🎮 ]
 GP1        GP2
```
- 🎮✓ = Gamepad connected (green checkmark)
- This is Gamepad 1 (what your code uses)

#### **Step 3: Test Gamepad (Optional)**
1. Move left stick - should see values change
2. Move right stick - should see values change
3. Press buttons - should register

---

## 🚀 **HOW TO START DRIVING - COMPLETE PROCEDURE**

### **Pre-Drive Checklist:**
- [ ] Robot battery charged (>12.5V recommended)
- [ ] Control Hub powered on (LED solid)
- [ ] Driver Hub connected to Control Hub (WiFi)
- [ ] Configuration activated: "Penguinauts_Robot"
- [ ] Gamepad connected (🎮✓)
- [ ] Robot on floor or blocks (not moving yet!)

---

### **DRIVING PROCEDURE:**

#### **Step 1: Open Driver Station App**
Should auto-start when Driver Hub boots up. If not, tap the Driver Station icon.

#### **Step 2: Select OpMode**

**For First-Time Motor Testing:**
```
┌─────────────────────────────────────┐
│ Select OpMode:                      │
│ ┌─────────────────────────────────┐ │
│ │ Penguinauts: Motor Test      ▼ │ │ ← Tap this dropdown
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

1. Tap the OpMode dropdown (shows current selection)
2. Scroll through list to find: **"Penguinauts: Motor Test"**
3. Tap to select it

**For Normal Driving:**
- Select: **"Penguinauts: Mecanum Drive"**

#### **Step 3: Initialize (INIT)**

```
┌─────────────────────────────────────┐
│ [INIT]  [▶ START]  [⏹ STOP]       │
└─────────────────────────────────────┘
```

1. Press **INIT** button
2. Watch telemetry screen:
   - Should say: **"Status: Initialized"**
   - Should show: **"Team Penguinauts 32240"**
   - Check for any ERROR messages
3. If errors appear:
   - Check configuration is activated
   - Verify motor names are exact
   - Check Control Hub connection

#### **Step 4: Start (START)**

1. Press **▶ START** button (big green play button)
2. Telemetry should update:
   - **"Status: Running"**
   - Motor power values appear
3. **NOW YOU CAN DRIVE!** 🎮

---

### **FIRST TEST - MOTOR VERIFICATION**

**⚠️ IMPORTANT: Put robot on blocks first so wheels don't touch ground!**

#### **Test All Motors Forward:**
1. Press **D-PAD UP** on gamepad
2. **ALL 4 wheels should spin FORWARD (away from you)**

**What you should see:**
```
    YOU
     ↓
    
  ⚙️→  ⚙️→   ← All wheels spinning forward
  
  
  ⚙️→  ⚙️→   ← All wheels spinning forward
```

**If ANY wheel spins backward:** Stop! We need to fix motor directions in code.

#### **Test Individual Motors:**
- Press **X** = Front Left spins (your left, front)
- Press **Y** = Front Right spins (your right, front)
- Press **A** = Back Left spins (your left, back)
- Press **B** = Back Right spins (your right, back)

**Verify each motor matches its position on the robot!**

---

### **SECOND TEST - DRIVING**

**Now put robot on the ground in open area!**

#### **Step 1: Start in Slow Mode**
1. Select OpMode: **"Penguinauts: Mecanum Drive"**
2. Press **INIT**, then **START**
3. **HOLD LEFT BUMPER (LB)** = Slow mode (50%)

#### **Step 2: Test Basic Movements**

**Forward (away from you):**
```
    YOU
     ↓
    
  [FL] [FR]  ← Robot should move THIS direction →
  
  
  [BL] [BR]
```
- Push **LEFT STICK FORWARD** (up)
- Robot should move AWAY from you
- If robot moves TOWARD you, we need to fix directions

**Backward (toward you):**
- Pull **LEFT STICK BACK** (down)
- Robot should move TOWARD you

**Strafe Left:**
- Push **LEFT STICK LEFT**
- Robot should move to YOUR LEFT (robot's left)
- Robot should NOT rotate

**Strafe Right:**
- Push **LEFT STICK RIGHT**
- Robot should move to YOUR RIGHT (robot's right)
- Robot should NOT rotate

**Rotate Left:**
- Push **RIGHT STICK LEFT**
- Robot should spin counter-clockwise (in place)

**Rotate Right:**
- Push **RIGHT STICK RIGHT**
- Robot should spin clockwise (in place)

#### **Step 3: Practice!**
- Release LB (Left Bumper) for normal speed (75%)
- Practice combined movements
- Try rotating while driving
- Get comfortable!

---

## 🔍 **TROUBLESHOOTING**

### **Problem: Can't Find OpMode**
**Fix:**
1. Make sure code is deployed to Robot Controller
2. Check that OpMode doesn't have `@Disabled` annotation
3. Restart Driver Station app
4. Restart Control Hub

### **Problem: "Error initializing hardware"**
**Fix:**
1. Check configuration is **activated** (not just saved)
2. Verify motor names are **EXACT**:
   - `front_left_drive`
   - `front_right_drive`
   - `back_left_drive`
   - `back_right_drive`
3. Check motors are plugged into correct ports (0-3)

### **Problem: Motor Spins Wrong Direction**
**Fix:** We need to modify the code (I can help with this!)

### **Problem: Robot Moves Wrong Direction**
**Fix:**
- If FORWARD moves robot toward you instead of away:
  - Need to flip motor directions in code
- If STRAFE goes opposite direction:
  - Need to check motor wiring

### **Problem: Gamepad Not Working**
**Fix:**
1. Check mode switch on BACK = **"D"**
2. Verify USB cable plugged in
3. Look for 🎮✓ icon on Driver Station
4. Try different USB port
5. Restart Driver Station app

### **Problem: Driver Hub Won't Connect**
**Fix:**
1. Make sure Control Hub is on (solid LED)
2. Check WiFi network name matches Control Hub
3. Try password: `password` (lowercase)
4. Restart both hubs
5. Check WiFi Direct settings

---

## 📋 **QUICK START SUMMARY**

For future sessions (after initial setup):

```
1. Power ON:
   ✓ Control Hub (on robot)
   ✓ Driver Hub

2. Connect:
   ✓ WiFi: Driver Hub → Control Hub
   ✓ USB: Gamepad → Driver Hub

3. Select:
   ✓ OpMode: "Penguinauts: Mecanum Drive"
   ✓ Configuration: "Penguinauts_Robot" (activated)

4. Run:
   ✓ Press INIT
   ✓ Press START
   ✓ DRIVE!

5. Stop:
   ✓ Press STOP when done
```

---

## 🎯 **FULL ORIENTATION DIAGRAM**

### **Your View (Standing in Front, Facing Robot):**

```
              YOU (Observer/Builder)
                    👤
            ← YOUR LEFT | YOUR RIGHT →
                    
                    ↓ Looking at robot
                    
        ╔═══════════════════════════════╗
        ║                               ║
        ║    FRONT OF ROBOT             ║
        ║    (Closer to you)            ║
        ║                               ║
        ║   [FL]              [FR]      ║
        ║   Port 0            Port 1    ║
        ║    ⚙️ /              \ ⚙️       ║
        ║                               ║
        ║     [Control Hub]             ║
        ║    ┌─────────────┐            ║
        ║    │   REV HUB   │            ║
        ║    │    [IMU]    │            ║
        ║    └─────────────┘            ║
        ║                               ║
        ║   [BL]              [BR]      ║
        ║   Port 2            Port 3    ║
        ║    ⚙️ \              / ⚙️       ║
        ║                               ║
        ║    BACK OF ROBOT              ║
        ║    (Farther from you)         ║
        ║                               ║
        ╚═══════════════════════════════╝
        
        When robot drives FORWARD:
        Robot moves AWAY from you ↑
```

### **Port to Position Mapping:**

| Port | Position | Your View |
|------|----------|-----------|
| 0 | Front Left | Left side, closer to you |
| 1 | Front Right | Right side, closer to you |
| 2 | Back Left | Left side, farther from you |
| 3 | Back Right | Right side, farther from you |

---

## 🐧 **Ready to Drive, Team Penguinauts!**

Follow this guide step-by-step and you'll be driving in no time!

**Remember:**
1. ✅ Configure hardware EXACTLY as shown
2. ✅ Test motors BEFORE driving
3. ✅ Start in SLOW MODE
4. ✅ Have FUN! 🎮🤖

**Need help?** Check the troubleshooting section or ask your mentor!

---

**Team Penguinauts 32240** | **INTO THE DEEP 2024-2025**

