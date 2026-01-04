# 🎯 Turn Calibration Guide - Team Penguinauts 32240

## Overview

The new `turnDegrees()` function allows your robot to turn by a specific number of degrees in place. However, it needs to be calibrated for your specific robot because different robots have:
- Different wheel spacing
- Different friction characteristics  
- Different motor configurations
- Different weight distributions

## 🚀 Quick Calibration Steps

### Step 1: Prepare Your Robot

1. ✅ Place robot on the field or mat (same surface you'll compete on)
2. ✅ Ensure battery is fully charged (>12.5V)
3. ✅ Mark the robot's starting orientation (use tape to mark a "front" line)
4. ✅ Have a protractor, angle finder, or smartphone angle app ready

### Step 2: Initial Test

1. Open `Penguinauts_AutoTemplate.java`
2. Find the `COUNTS_PER_DEGREE` constant (around line 52):
   ```java
   private static final double COUNTS_PER_DEGREE = 10.0;  // Start value
   ```
3. Deploy the code to your robot
4. Run the autonomous OpMode
5. Watch the robot complete a 90-degree turn (Step 3 in the example)

### Step 3: Measure and Adjust

#### Test Turn
Run `turnDegrees(360)` to make a complete rotation:

```java
// In runOpMode(), replace the example with this test:
telemetry.addData("Test", "360 Degree Turn");
telemetry.update();
turnDegrees(360);
```

#### Measure Result
- **Perfect:** Robot returns exactly to starting position → You're done! ✅
- **Robot turned TOO FAR** (more than 360°):
  - **Decrease** `COUNTS_PER_DEGREE`
  - Example: Change from `10.0` to `9.0`
- **Robot turned TOO LITTLE** (less than 360°):
  - **Increase** `COUNTS_PER_DEGREE`
  - Example: Change from `10.0` to `11.0`

### Step 4: Calculate Precise Value

For more accurate calibration:

1. Measure how many degrees the robot actually turned
2. Use this formula:

   ```
   New Value = Current Value × (360 / Actual Degrees Turned)
   ```

   **Example:**
   - Current `COUNTS_PER_DEGREE` = 10.0
   - Robot actually turned 320 degrees (undershot)
   - New value = 10.0 × (360 / 320) = 11.25
   
   Update the constant:
   ```java
   private static final double COUNTS_PER_DEGREE = 11.25;
   ```

3. Test again with 360-degree turn
4. Repeat until accurate

### Step 5: Verify with Different Angles

Once 360 degrees is accurate, test other angles:

```java
// Test different angles
turnDegrees(90);   // Should turn exactly 90°
sleep(1000);
turnDegrees(-90);  // Should return to start
sleep(1000);
turnDegrees(180);  // Should turn around
```

All angles should be accurate if 360° is calibrated correctly.

---

## 📝 Usage Examples

### Basic Turns

```java
// Turn right 90 degrees
turnDegrees(90);

// Turn left 90 degrees  
turnDegrees(-90);

// Turn around
turnDegrees(180);

// Small adjustment
turnDegrees(15);
```

### In Autonomous Sequences

```java
// Navigate to scoring position
driveForward(0.5, 36);      // Drive forward 36 inches
turnDegrees(90);             // Turn right 90 degrees
driveForward(0.5, 24);      // Drive forward 24 inches
turnDegrees(-45);            // Turn left 45 degrees to face target
// Score element
```

### Combined with Other Movements

```java
// Square pattern
driveForward(0.5, 24);
turnDegrees(90);
driveForward(0.5, 24);
turnDegrees(90);
driveForward(0.5, 24);
turnDegrees(90);
driveForward(0.5, 24);
turnDegrees(90);  // Back to start facing original direction
```

---

## 🔧 Advanced Calibration

### Factors Affecting Accuracy

1. **Surface Friction:**
   - Carpet vs smooth floor makes a big difference
   - Always calibrate on competition surface
   
2. **Battery Level:**
   - Low battery = less power = slower turns
   - Calibrate with fully charged battery
   
3. **Weight Distribution:**
   - Changes in robot configuration affect turning
   - Recalibrate if you add/remove heavy components
   
4. **Wheel Condition:**
   - Worn wheels may slip more
   - Clean wheels regularly

### Multiple Calibration Values

For competition, you might want different values for different conditions:

```java
// Option 1: Different speeds
private static final double COUNTS_PER_DEGREE_SLOW = 10.5;
private static final double COUNTS_PER_DEGREE_FAST = 10.0;

// Option 2: Different surfaces
private static final double COUNTS_PER_DEGREE_CARPET = 11.0;
private static final double COUNTS_PER_DEGREE_TILE = 9.5;
```

Then modify the `turnDegrees()` function to use the appropriate constant.

---

## 🎓 How It Works

### The Math Behind It

When the robot turns in place:
- **Left motors** move FORWARD
- **Right motors** move BACKWARD
- Robot rotates around its center point

The encoder counts required depend on:
- Distance from center to wheels (robot radius)
- Circumference of the turn circle
- Encoder counts per wheel rotation

**Formula:**
```
Arc Length = (Robot Turn Radius) × (Angle in Radians)
Encoder Counts = Arc Length × COUNTS_PER_INCH
COUNTS_PER_DEGREE = Encoder Counts ÷ Degrees
```

### Why Calibration is Needed

The theoretical calculation requires:
- Exact measurement of robot dimensions
- Accounting for wheel slippage
- Compensating for friction variations
- Weight distribution effects

Empirical calibration (testing and adjusting) is much more reliable!

---

## 🐛 Troubleshooting

### Robot Doesn't Turn at All

**Check:**
- [ ] Motors are connected and powered
- [ ] Motor configuration names match exactly
- [ ] Motor directions are set correctly (lines 68-71)
- [ ] `COUNTS_PER_DEGREE` is not zero

### Robot Moves Forward/Backward While Turning

**Problem:** Motors aren't balanced
**Solution:** Check motor power in telemetry - all should show same absolute value

### Turns Are Inconsistent

**Possible causes:**
1. Battery voltage fluctuating → Charge battery
2. Wheels slipping → Clean wheels, check floor surface
3. Encoders not working → Check encoder cables
4. `TURN_SPEED` too high → Decrease from 0.4 to 0.3

### Small Angles Inaccurate but Large Angles OK

**Cause:** Rounding errors with small encoder counts
**Solution:** Increase `COUNTS_PER_DEGREE` (better resolution)

---

## ✅ Calibration Checklist

Complete calibration when you can check all these:

- [ ] 360-degree turn returns to exact starting position (±5°)
- [ ] 90-degree turn is accurate (±3°)
- [ ] 180-degree turn is accurate (±3°)
- [ ] Consecutive turns are consistent
- [ ] Both positive (clockwise) and negative (counter-clockwise) work
- [ ] Turns work on competition surface
- [ ] Documented calibration value in code comments

---

## 📊 Calibration Log Template

Keep track of your calibration attempts:

| Test # | COUNTS_PER_DEGREE | Command | Actual Turn | Error | Notes |
|--------|-------------------|---------|-------------|-------|-------|
| 1 | 10.0 | 360° | 320° | -40° | Too little, increase |
| 2 | 11.25 | 360° | 365° | +5° | Close! |
| 3 | 11.1 | 360° | 358° | -2° | Perfect! ✅ |

**Final Calibrated Value:** `11.1`

---

## 🏆 Competition Tips

1. **Test before every match** - Quick 360° test to verify
2. **Keep spare batteries** - Consistent power = consistent turns  
3. **Practice autonomous** - Know your sequences
4. **Have backup values** - If one isn't working, try another
5. **Document everything** - Write down what works!

---

## 📞 Need Help?

**Common resources:**
- FTC Discord: https://discord.gg/first-tech-challenge
- Game Manual 0: https://gm0.org/
- Team Documentation: See other `.md` files in TeamCode folder

---

**Good luck calibrating, Team Penguinauts! 🐧**

*Last Updated: Created for 2024-2025 Season*

