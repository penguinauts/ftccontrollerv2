# 🔧 TROUBLESHOOTING GUIDE

**Team Penguinauts 32240** | **Quick Problem Solving**

---

## 🚫 **ROBOT WON'T MOVE**

### **Check 1: Gamepad Mode Switch**
**Location:** Back of Logitech F310 gamepad  
**Setting:** Must be on **"D"** (not "X")

### **Check 2: Gamepad Connection**
1. Look at Driver Station screen
2. Should show "Gamepad 1" with ✓ icon
3. If not connected: Unplug and replug USB cable

### **Check 3: Battery**
1. Check battery voltage on Driver Station
2. Should be above 12V
3. If low, replace battery

### **Check 4: OpMode Running**
1. Verify you pressed START (not just INIT)
2. Check Driver Station shows "Running"

---

## 📥 **INTAKE NOT WORKING**

### **Check 1: Telemetry During INIT**
Look for:
```
Intake Front    ✓ Found
Intake Back     ✓ Found
```

**If shows "✗ Not found":**

### **Fix 1: Driver Station Configuration**
1. Tap ⋮ → Configure Robot
2. Tap "Expansion Hub Portal 1" → Motors
3. Verify:
   - Port 2 = `IF` (exact spelling, capitals)
   - Port 3 = `IB` (exact spelling, capitals)
   - Both type = "goBILDA 5203 series"
4. Tap Done to save

### **Fix 2: Physical Connections**
1. Check motor wires plugged into **Expansion Hub** ports 2 and 3
2. Ensure wires fully inserted (should click)
3. Check I2C cable between Control Hub and Expansion Hub
4. Verify Expansion Hub LED is on (powered)

### **Fix 3: Check Control Button**
- Are you pressing **LEFT Trigger**? (Not right trigger)
- Right trigger = shooter, Left trigger = intake

---

## 🎯 **SHOOTER NOT WORKING**

### **Check 1: Telemetry During INIT**
Look for:
```
Shooter Left     ✓ Found
Shooter Right    ✓ Found
```

**If shows "✗ Not found":**

### **Fix 1: Driver Station Configuration**
1. Tap ⋮ → Configure Robot
2. Tap "Expansion Hub Portal 1" → Motors
3. Verify:
   - Port 0 = `SL` (exact spelling, capitals)
   - Port 1 = `SR` (exact spelling, capitals)
   - Both type = "5000 Series 12VDC"
4. Tap Done to save

### **Fix 2: Physical Connections**
1. Check motor wires plugged into **Expansion Hub** ports 0 and 1
2. Ensure wires fully inserted (should click)
3. Check Expansion Hub is connected to Control Hub via I2C

### **Fix 3: Wrong Direction**
**If shooter runs backward:**
- The code already has correct directions
- If still wrong, check motor wire polarity

### **Fix 4: Check Control Button**
- Are you pressing **RIGHT Trigger**? (Not left trigger)
- Left trigger = intake, Right trigger = shooter

---

## 🔄 **DRIVE MOTORS ACTING STRANGE**

### **Problem: Robot Drifts or Moves Wrong Direction**

**Fix: Verify Motor Directions in Code**
All motor directions are set correctly in code. If wrong:

1. Check Driver Station configuration
2. Motor directions should match:
   - FL (Port 2): REVERSE
   - FR (Port 3): FORWARD
   - BL (Port 0): REVERSE
   - BR (Port 1): FORWARD

### **Problem: One Wheel Not Moving**

**Check:**
1. Run "Pre-Match Check" OpMode
2. Test individual motors with Y/X/B/A buttons
3. Identify which motor failed
4. Check physical connection to that port
5. Verify motor is configured in Driver Station

---

## ⚠️ **"NOT ON CLASSPATH" WARNINGS**

**Message:** "File is not on classpath, only syntax errors reported"

**This is NORMAL!** It's just a warning, not an error.

**Why:** Files added during editing session aren't on classpath yet

**Fix:** Build the project (it happens automatically when deploying)

---

## 🔌 **EXPANSION HUB NOT FOUND**

**Symptoms:**
- All shooter/intake motors show "✗ Not found"
- Drive motors work fine

**Fix 1: Check I2C Connection**
1. Verify I2C cable connects Control Hub to Expansion Hub
2. Cable should be in Port 0 on both hubs
3. Unplug and replug both ends firmly

**Fix 2: Power**
1. Check Expansion Hub LED is on
2. If off, check power connection
3. Try rebooting entire robot

**Fix 3: Driver Station Configuration**
1. Open Configure Robot
2. Should see both:
   - "Control Hub Portal"
   - "Expansion Hub Portal 1"
3. If Expansion Hub missing, check I2C cable

---

## 🎮 **GAMEPAD ISSUES**

### **Buttons Don't Respond**
1. Check mode switch on back = "D"
2. Check USB cable connection
3. Try different USB port
4. Try different gamepad

### **Wrong Actions Happen**
1. Verify mode switch = "D" (not "X")
2. X mode has different button mappings

### **Gamepad Disconnects**
1. Check USB cable quality
2. Try securing cable with tape
3. Avoid cable stress/bending

---

## 📊 **TELEMETRY SHOWS ERRORS**

### **"Shooter: ONE MOTOR ONLY"**
- One shooter motor not found
- Robot will work but with reduced power
- Check connections and configuration for failed motor

### **"Intake: ONE MOTOR"**
- One intake motor not found
- Robot will work but with reduced efficiency
- Check connections and configuration for failed motor

### **"Drive: SOME FAILED"**
- One or more drive motors not found
- **Critical issue** - fix before competing
- Run Pre-Match Check to identify which motor

---

## 🔋 **BATTERY ISSUES**

### **Low Battery Warning**
- Replace battery immediately
- Low battery causes:
  - Weak motors
  - Intermittent disconnections
  - Unpredictable behavior

### **Battery Drains Quickly**
- Check for motors running when they shouldn't
- Press X and B buttons to stop intake/shooter
- Normal: 5-6 matches per battery

---

## 💻 **CODE DEPLOYMENT ISSUES**

### **"Build Failed"**
1. Check for syntax errors in code
2. Look at error messages in Android Studio
3. Verify all files saved

### **"Cannot Connect to Robot"**
1. Verify robot WiFi network is visible
2. Check laptop connected to robot network
3. Try USB connection instead

### **Code Deploys but Doesn't Work**
1. Restart OpMode
2. Restart robot (power cycle)
3. Re-deploy code

---

## 🆘 **EMERGENCY PROCEDURES**

### **Robot Out of Control**
1. Press **STOP** button on Driver Station immediately
2. All motors stop instantly
3. Investigate issue before restarting

### **During Match - System Failure**
**If shooter fails:**
- Continue with drive and intake
- Focus on defense or positioning

**If intake fails:**
- Continue with drive and any pre-loaded balls
- Focus on existing game elements

**If drive fails:**
- **STOP IMMEDIATELY**
- Alert referees

---

## 📱 **QUICK DIAGNOSTIC STEPS**

**Before Every Match:**
1. Run "Pre-Match Check" OpMode
2. Verify all ✓ marks appear
3. Test each system:
   - Drive (Y/X/B/A buttons)
   - Shooter (Right Trigger)
   - Intake (Guide button)
4. All work? → Ready!

**If Any System Fails:**
1. Check Driver Station configuration first
2. Check physical connections second
3. Run diagnostic OpMode for that system
4. If can't fix quickly, compete without it

---

## 📞 **GET HELP**

**Before Match:**
- Check this guide first
- Try Pre-Match Check OpMode
- Ask coach or team mentor

**During Competition:**
- Ask in pits area
- Other teams usually help
- Event technicians available

**After Hours:**
- Review ROBOT_SETUP_GUIDE.md
- Check code comments
- FTC forums: ftcforum.firstinspires.org

---

## ✅ **PREVENTIVE MAINTENANCE**

**Before Each Competition:**
- [ ] Test all motors
- [ ] Check all wire connections
- [ ] Verify battery fully charged
- [ ] Update/redeploy code
- [ ] Test gamepad
- [ ] Run Pre-Match Check

**After Each Match:**
- [ ] Check for loose wires
- [ ] Swap battery if voltage < 12V
- [ ] Note any issues for repairs

---

**Most Common Issues (90% of problems):**
1. ✅ Gamepad mode switch on "X" instead of "D"
2. ✅ Wrong button pressed (LT vs RT)
3. ✅ Motor not configured in Driver Station
4. ✅ Loose wire connection
5. ✅ Low battery

**Check these first!**

---

**Team Penguinauts 32240**  
**Updated:** January 13, 2026

