# 🤖 PENGUINAUTS ROBOT - QUICK START

**Team 32240** | **Season: Into The Deep 2024-2025**

---

## 🚀 **5-MINUTE STARTUP**

### **1. Power On Robot**
- Turn on Control Hub
- Wait for green light
- Connect Driver Station

### **2. Select OpMode**
Choose one:
- **"Penguinauts: Mecanum Drive"** - Robot-relative (easier)
- **"Penguinauts: Field Relative Drive"** - Field-relative (advanced)

### **3. Press INIT → Check Status**
Look for these ✓ marks:
```
Drive Motors:     ✓ (all 4)
Shooter Motors:   ✓ (both)
Intake Motors:    ✓ (both)
```

### **4. Press START → Drive!**

---

## 🎮 **GAMEPAD CONTROLS**

### **DRIVE**
- **Left Stick** → Move (forward/back/strafe)
- **Right Stick** → Rotate
- **Left Bumper** → Slow mode (50%)
- **Right Bumper** → Turbo mode (100%)

### **INTAKE** (Collect Game Elements)
- **Left Trigger** → Collect forward
- **Y Button** → Eject backward
- **X Button** → Stop intake

### **SHOOTER** (Launch Game Elements)
- **Right Trigger** → Start shooter
- **B Button** → Stop shooter

---

## 🏆 **MATCH STRATEGY**

### **Collect Phase:**
1. Drive to ball
2. **Press Left Trigger** → Ball collected
3. **Release Left Trigger** → Ball stored
4. Repeat for more balls

### **Shoot Phase:**
1. Drive to shooting zone
2. **Press Right Trigger** → Balls shoot
3. **Press B Button** → Stop

---

## 🔧 **PRE-MATCH CHECK**

Select **"Penguinauts: Pre-Match Check"** OpMode:

1. **Press INIT** → Check all ✓ marks appear
2. **Press START**
3. **Test Drive:** Press Y/X/B/A buttons
4. **Test Shooter:** Press Right Trigger
5. **Test Intake:** Press Guide button (cycles FWD/REV/STOP)
6. All working? → **Ready to compete!**

---

## ⚠️ **QUICK TROUBLESHOOTING**

### **Robot Won't Move**
- Check gamepad mode switch = "D" (not "X")
- Check battery charge
- Verify USB cable to Driver Station

### **Intake Not Working**
- Check telemetry shows "Intake: ✓ Found"
- If ✗, check Driver Station configuration
- Verify IF (port 2) and IB (port 3) are configured

### **Shooter Not Working**
- Check telemetry shows "Shooter: ✓ Found"
- If wrong direction, see TROUBLESHOOTING.md

### **Need Help?**
See **ROBOT_SETUP_GUIDE.md** for detailed info

---

## 📊 **ROBOT LAYOUT**

```
         FRONT
           ↑
    ┌──────────────┐
    │ [IF] Intake  │
    │  FL      FR  │  Drive Motors
    │              │
    │ Control Hub  │
    │              │
    │  BL      BR  │  Drive Motors
    │ [IB] Intake  │
    │ [SL]    [SR] │  Shooter
    └──────────────┘
         BACK
```

---

## 💡 **TIPS FOR DRIVERS**

1. **Practice slow mode first** (Left Bumper)
2. **Keep intake stopped** when not collecting
3. **Only shoot at shooting zone** (saves power)
4. **Test controls** before every match
5. **Gamepad mode** must be "D" (check back of gamepad)

---

## 📱 **FTC DASHBOARD** (Optional Tuning)

Connect laptop to robot WiFi:
- URL: `http://192.168.43.1:8080/dash`
- Adjust **SHOOTER_POWER** (0.5 to 1.0)
- Adjust **INTAKE_POWER** (0.5 to 1.0)
- Changes apply immediately

---

**Questions? Problems?** See **TROUBLESHOOTING.md** or ask your coach!

🐧 **GO PENGUINAUTS!** 🏆

