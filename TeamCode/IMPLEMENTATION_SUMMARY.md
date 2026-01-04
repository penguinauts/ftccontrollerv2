# 🎉 New Feature Added: turnDegrees() Function

## Summary

A new **encoder-based turn function** has been added to your autonomous code! You can now turn your robot precisely by any number of degrees while staying in the same position.

---

## ✅ What Was Added

### 1. **New Function: `turnDegrees(double degrees)`**
Located in: `Penguinauts_AutoTemplate.java`

**Usage:**
```java
turnDegrees(90);    // Turn right 90 degrees
turnDegrees(-90);   // Turn left 90 degrees  
turnDegrees(180);   // Turn around
```

### 2. **Calibration Constant: `COUNTS_PER_DEGREE`**
Located at line 67 in `Penguinauts_AutoTemplate.java`

```java
private static final double COUNTS_PER_DEGREE = 10.0;  // Needs tuning!
```

This value needs to be adjusted for your specific robot.

### 3. **Calibration Test OpMode**
File: `AutoCode.java`
OpMode Name: **"Penguinauts: Turn Calibration"**

This automated test helps you find the correct `COUNTS_PER_DEGREE` value.

### 4. **Documentation**
Three new documentation files:
- **TURN_CALIBRATION_GUIDE.md** - Detailed calibration instructions
- **TURN_DEGREES_QUICK_REFERENCE.txt** - Quick reference card (print this!)
- **IMPLEMENTATION_SUMMARY.md** - This file

---

## 🚀 Quick Start (5 Steps)

### Step 1: Deploy Code
1. Connect to your robot
2. Build and deploy the updated code
3. Wait for deployment to complete

### Step 2: Run Calibration Test
1. Go to Driver Station
2. Select **"Penguinauts: Turn Calibration"** from OpMode list
3. Mark your robot's starting position
4. Press START

### Step 3: Observe Results
The robot will automatically perform these tests:
- 360° full rotation
- 90° right turn
- 90° left turn (back to start)
- 180° turn around
- 180° return

### Step 4: Adjust Calibration
1. Check if the 360° turn returned to exact starting position
2. Open `Penguinauts_AutoTemplate.java`
3. Find `COUNTS_PER_DEGREE` (line 67)
4. Adjust the value:
   - Robot turned **too far**? → **DECREASE** the value
   - Robot turned **too little**? → **INCREASE** the value
5. Use this formula:
   ```
   New Value = Current Value × (360 / Actual Degrees Turned)
   ```

### Step 5: Verify
1. Deploy updated code
2. Run calibration test again
3. Repeat until accurate
4. You're done! ✅

---

## 📖 Documentation Reference

### For Quick Usage
→ Read: **TURN_DEGREES_QUICK_REFERENCE.txt**
- Function signature
- Usage examples
- Common issues
- **Print and keep at driver station!**

### For Detailed Calibration
→ Read: **TURN_CALIBRATION_GUIDE.md**
- Step-by-step calibration process
- Troubleshooting guide
- How it works (the math)
- Advanced tips

### For Code Reference
→ Look at: **Penguinauts_AutoTemplate.java**
- Lines 158-191: The `turnDegrees()` function
- Lines 62-67: Calibration constant
- Lines 102-110: Example usage in autonomous

---

## 💡 Key Features

### ✅ Advantages Over Old Method

**Old Method (Time-Based):**
```java
rotateRight(0.3, 1.0);  // Rotate for 1 second - how many degrees? 🤷
```
- Unpredictable angle
- Varies with battery level
- Varies with surface friction
- Hard to repeat consistently

**New Method (Encoder-Based):**
```java
turnDegrees(90);  // Exactly 90 degrees! 🎯
```
- Precise angle control
- Consistent results
- Repeatable performance
- Works with any angle

### ✅ How It Works

The function uses **encoder counts** to precisely control wheel rotation:

1. Calculates required encoder counts for desired angle
2. Sets opposite targets for left/right motors
3. Left motors go forward, right motors go backward
4. Robot spins in place around its center
5. Stops when encoder targets reached

### ✅ Robot Stays in Place

Unlike a car-like turn, this function makes the robot **rotate around its center point**:
- No forward/backward movement
- No side-to-side movement
- Just pure rotation ↻
- Perfect for tight spaces!

---

## 🎯 Example Autonomous Sequences

### Example 1: Simple Navigation
```java
// Drive forward, turn, drive to target
driveForward(0.5, 36);      // Move forward 36 inches
turnDegrees(90);             // Turn right 90 degrees
driveForward(0.5, 24);      // Drive to target
```

### Example 2: Square Pattern
```java
// Drive in a perfect square
for (int i = 0; i < 4; i++) {
    driveForward(0.5, 24);   // Drive 24 inches
    turnDegrees(90);          // Turn right 90 degrees
}
// Robot returns to exact starting position and orientation!
```

### Example 3: Precision Positioning
```java
// Navigate to scoring position with fine angle adjustments
driveForward(0.5, 30);      // Leave start
strafeRight(0.5, 12);       // Move right
turnDegrees(45);             // Angle toward target
driveForward(0.4, 20);      // Approach carefully
turnDegrees(-15);            // Fine adjustment
// Score here
```

### Example 4: Search Pattern
```java
// Turn and scan multiple directions
turnDegrees(45);             // Look right
sleep(500);
turnDegrees(-90);            // Look left  
sleep(500);
turnDegrees(45);             // Center again
```

---

## 🔧 Calibration Tips

### Best Practices

1. **Always calibrate on competition surface**
   - Carpet vs. tile makes a big difference!
   - Different friction = different results

2. **Use fully charged battery**
   - Low battery = inconsistent performance
   - Always calibrate with >12.5V

3. **Start with 360° test**
   - Easiest to measure
   - Most visible errors
   - Gets you close quickly

4. **Fine-tune with smaller angles**
   - Test 90° for precision
   - Test both positive and negative
   - Verify consistency

5. **Document your value**
   - Write it in code comments
   - Keep a log in your engineering notebook
   - Share with whole team

### Quick Calibration Formula

If your robot turned `X` degrees when you commanded 360:

```
New COUNTS_PER_DEGREE = Current Value × (360 / X)
```

**Examples:**
- Turned 320° → `10.0 × (360/320) = 11.25`
- Turned 400° → `10.0 × (360/400) = 9.0`
- Turned 350° → `10.0 × (360/350) = 10.29`

---

## 🐛 Troubleshooting

### Robot doesn't turn at all
- Check motor connections
- Verify motor names in configuration
- Check encoders are connected
- Verify `COUNTS_PER_DEGREE` is not zero

### Robot moves forward/backward while turning
- Check motor directions (lines 68-71)
- Verify all 4 motors are balanced
- Check telemetry - all motor powers should match

### Inconsistent turns
- Check battery level (charge to >12.5V)
- Clean wheels (remove debris)
- Reduce `TURN_SPEED` (try 0.3 instead of 0.4)
- Check for loose connections

### Turns not accurate
- Calibrate `COUNTS_PER_DEGREE` properly
- Test on actual competition surface
- Verify encoders are working (check telemetry)
- Make sure robot isn't slipping

---

## 📁 Modified Files

### Updated Files:
1. **Penguinauts_AutoTemplate.java**
   - Added `turnDegrees()` function
   - Added `COUNTS_PER_DEGREE` constant
   - Updated example autonomous sequence
   - Added detailed comments

### New Files:
1. **AutoCode.java**
   - Calibration test OpMode
   - Automated testing sequence
   - Real-time feedback

2. **TURN_CALIBRATION_GUIDE.md**
   - Complete calibration instructions
   - How it works explanation
   - Troubleshooting guide

3. **TURN_DEGREES_QUICK_REFERENCE.txt**
   - Quick reference card
   - Usage examples
   - Common issues
   - **Print this for driver station!**

4. **IMPLEMENTATION_SUMMARY.md**
   - This file
   - Overview of changes
   - Getting started guide

---

## ✅ Verification Checklist

Before using in competition:

- [ ] Code deployed successfully
- [ ] Ran "Turn Calibration" OpMode
- [ ] Calibrated `COUNTS_PER_DEGREE` value
- [ ] 360° turn returns to exact start (±5°)
- [ ] 90° turn is accurate (±3°)
- [ ] -90° turn is accurate (±3°)
- [ ] 180° turn is accurate (±3°)
- [ ] Robot doesn't drift during turns
- [ ] Tested on competition surface
- [ ] Documented calibration value
- [ ] Team members know how to use function
- [ ] Printed quick reference card

---

## 🏆 Next Steps

### For Practice:
1. Run the calibration test
2. Dial in your `COUNTS_PER_DEGREE` value
3. Create simple autonomous routines
4. Test different angle combinations
5. Practice on competition field

### For Competition:
1. Copy `Penguinauts_AutoTemplate.java`
2. Rename to your strategy (e.g., `Penguinauts_Auto_LeftSide.java`)
3. Remove `@Disabled` annotation
4. Build your autonomous sequence using:
   - `driveForward(speed, inches)`
   - `strafeRight(speed, inches)`
   - `turnDegrees(degrees)` ← **Your new function!**
5. Test, test, test!

### Advanced Usage:
Consider adding:
- Variable turn speeds based on angle size
- Timeout protection for safety
- IMU verification for ultra-precision
- Smooth acceleration/deceleration curves

---

## 📞 Need Help?

### Documentation Files:
1. **TURN_DEGREES_QUICK_REFERENCE.txt** - Quick answers
2. **TURN_CALIBRATION_GUIDE.md** - Detailed help
3. **CODE_STRUCTURE.md** - Overall code organization
4. **START_HERE.md** - Complete documentation index

### External Resources:
- FTC Discord: https://discord.gg/first-tech-challenge
- Game Manual 0: https://gm0.org/
- FTC Docs: https://ftc-docs.firstinspires.org/

### Code Comments:
All functions have detailed comments in the code itself!

---

## 🎓 Learning Opportunity

This implementation demonstrates several important FTC programming concepts:

1. **Encoder-based movement** - Precise control
2. **Mecanum drive kinematics** - How wheels work together
3. **Motor synchronization** - Coordinated movement
4. **Calibration techniques** - Tuning for real world
5. **Code reusability** - Building a library of functions

Great job adding advanced autonomous capability to your robot! 🎉

---

**Team Penguinauts 32240** | **INTO THE DEEP 2024-2025**

*Feature implemented: January 2025*

---

**Ready to turn some degrees? Let's go! 🐧🤖**

