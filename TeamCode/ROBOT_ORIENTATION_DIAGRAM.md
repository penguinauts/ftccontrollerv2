# 🎯 ROBOT ORIENTATION & WIRING DIAGRAM
## Team Penguinauts 32240

---

## 📐 YOUR ROBOT ORIENTATION (Critical Reference!)

### **YOU Stand Here When Building/Testing:**

```
                    👤 YOU (Builder/Driver)
                    ↑
              Standing Here
           Facing toward robot
                    
            ← YOUR LEFT | YOUR RIGHT →
                    
                    ↓
                    
        ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
        ┃       ROBOT FRONT          ┃
        ┃    (Closer to you)         ┃
        ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
                    
        ╔═══════════════════════════════╗
        ║                               ║
        ║  [FL Motor]      [FR Motor]   ║
        ║   Port 0          Port 1      ║
        ║    ⚙️ /            \ ⚙️         ║
        ║                               ║
        ║   YOUR LEFT       YOUR RIGHT  ║
        ║                               ║
        ║        [Control Hub]          ║
        ║       ┌─────────────┐         ║
        ║       │ ●  REV HUB  │ ●       ║
        ║       │   [IMU]     │         ║
        ║       │  USB Port ⬛ │         ║
        ║       │   0 1 2 3   │         ║
        ║       └─────────────┘         ║
        ║                               ║
        ║   YOUR LEFT       YOUR RIGHT  ║
        ║                               ║
        ║  [BL Motor]      [BR Motor]   ║
        ║   Port 2          Port 3      ║
        ║    ⚙️ \            / ⚙️         ║
        ║                               ║
        ╚═══════════════════════════════╝
        
        ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
        ┃       ROBOT BACK           ┃
        ┃    (Farther from you)      ┃
        ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

---

## 🔌 CONTROL HUB WIRING (Top View)

```
┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
┃         REV CONTROL HUB              ┃
┃                                      ┃
┃  ● LED                      LOGO ⬆   ┃
┃                                      ┃
┃  ┌────────────────────────────────┐  ┃
┃  │  MOTOR PORTS (Top Row)         │  ┃
┃  │                                │  ┃
┃  │  [0]    [1]    [2]    [3]      │  ┃
┃  │  FL     FR     BL     BR       │  ┃
┃  └────────────────────────────────┘  ┃
┃                                      ┃
┃  ┌────────────────────────────────┐  ┃
┃  │  SERVO PORTS (Middle Row)      │  ┃
┃  │  [0]  [1]  [2]  [3]  [4]  [5]  │  ┃
┃  │  (Available for future use)    │  ┃
┃  └────────────────────────────────┘  ┃
┃                                      ┃
┃  [I2C Ports]  [Digital]  [Analog]    ┃
┃                                      ┃
┃  [USB-C] ⬛  [12V Battery] ⚡         ┃
┃  (To computer)   (Power)             ┃
┃                                      ┃
┃  [XT30 Connector] ⚡                  ┃
┃  (To battery)                        ┃
┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
```

---

## 🔧 MOTOR CONNECTION CHECKLIST

### Port 0 - Front Left Motor
```
Control Hub Port 0
        |
        | JST-VH Cable
        ↓
   [Motor Connector]
        ↓
    FL Motor ⚙️
  (Your left, front)
```
- [ ] Cable plugged into Port 0
- [ ] Cable secure (clicks in)
- [ ] Motor labeled "FL" (optional but helpful)

### Port 1 - Front Right Motor
```
Control Hub Port 1
        |
        | JST-VH Cable
        ↓
   [Motor Connector]
        ↓
    FR Motor ⚙️
  (Your right, front)
```
- [ ] Cable plugged into Port 1
- [ ] Cable secure
- [ ] Motor labeled "FR"

### Port 2 - Back Left Motor
```
Control Hub Port 2
        |
        | JST-VH Cable
        ↓
   [Motor Connector]
        ↓
    BL Motor ⚙️
  (Your left, back)
```
- [ ] Cable plugged into Port 2
- [ ] Cable secure
- [ ] Motor labeled "BL"

### Port 3 - Back Right Motor
```
Control Hub Port 3
        |
        | JST-VH Cable
        ↓
   [Motor Connector]
        ↓
    BR Motor ⚙️
  (Your right, back)
```
- [ ] Cable plugged into Port 3
- [ ] Cable secure
- [ ] Motor labeled "BR"

---

## 🎡 MECANUM WHEEL ORIENTATION

**CRITICAL: Wheels MUST form "X" pattern from above!**

### Correct Orientation (Top View):
```
        FRONT
          ↑
          
    /⚙️      ⚙️\      ✓ Correct!
     FL        FR    
                    Rollers form
                    an "X" pattern
    \⚙️      ⚙️/    
     BL        BR    
          
        BACK
          ↓
```

### Individual Wheel Details:

**Front Left (FL):**
```
  Roller Pattern: /
  
  Looking from above:
  ┌──────┐
  │  / / │
  │ / /  │
  └──────┘
```

**Front Right (FR):**
```
  Roller Pattern: \
  
  Looking from above:
  ┌──────┐
  │ \ \  │
  │  \ \ │
  └──────┘
```

**Back Left (BL):**
```
  Roller Pattern: \
  
  Looking from above:
  ┌──────┐
  │ \ \  │
  │  \ \ │
  └──────┘
```

**Back Right (BR):**
```
  Roller Pattern: /
  
  Looking from above:
  ┌──────┐
  │  / / │
  │ / /  │
  └──────┘
```

---

## 🔋 POWER SYSTEM

```
┌─────────────────────┐
│   12V Battery       │
│   (REV Slim)        │
│   Charged: ~13.5V   │
└──────┬──────────────┘
       │
       │ XT30 Connector
       ↓
┌──────────────────────┐
│   Control Hub        │
│   Main Power Switch  │
│   [●] ON/OFF         │
└──────┬───────────────┘
       │
       ├──→ Motor Port 0 (FL)
       ├──→ Motor Port 1 (FR)
       ├──→ Motor Port 2 (BL)
       └──→ Motor Port 3 (BR)
```

---

## 📱 CONTROL SYSTEM LAYOUT

```
┌─────────────────────┐
│   Driver Station    │
│   (Driver Hub or    │
│    Android Phone)   │
│                     │
│   [OpMode Select ▼] │
│   [INIT] [START]    │
│   [STOP]            │
└──────┬──────────────┘
       │
       │ WiFi Direct
       │ (Wireless)
       ↓
┌─────────────────────┐
│   Robot Controller  │
│   (Control Hub)     │
│    On Robot         │
│                     │
│   Motors: 0 1 2 3   │
└─────────────────────┘

       AND

┌─────────────────────┐
│  Logitech F310      │
│  Gamepad            │
│                     │
│  [LS]         [RS]  │
└──────┬──────────────┘
       │
       │ USB Cable
       ↓
┌─────────────────────┐
│   Driver Hub        │
└─────────────────────┘
```

---

## 🧭 CONTROL HUB IMU ORIENTATION

**How your Control Hub should be mounted:**

```
        Looking from Above:
        
        ┌─────────────────┐
        │  REV  ⬆ LOGO    │  ← Logo facing UP (toward sky)
        │                 │
        │      [IMU]      │
        │                 │
    ⬛  │  USB PORT       │
  FORWARD (toward front of robot)
        │                 │
        └─────────────────┘
```

**Configuration in Code:**
- Logo Direction: **UP** (toward sky)
- USB Direction: **FORWARD** (toward front of robot)

If your hub is mounted differently, you'll need to update `Penguinauts_FieldRelativeDrive.java`

---

## 📊 CONFIGURATION SCREEN REFERENCE

**What Driver Station Configuration Should Look Like:**

```
┌─────────────────────────────────────────┐
│ Configuration: Penguinauts_Robot    [✓] │ ← Must be ACTIVATED
├─────────────────────────────────────────┤
│                                         │
│ Control Hub:                            │
│                                         │
│ Motor Ports:                            │
│   Port 0: BL                 [Motor]   │
│   Port 1: BR                 [Motor]   │
│   Port 2: FL                 [Motor]   │
│   Port 3: FR                 [Motor]   │
│                                         │
│ I2C Bus 0:                              │
│   imu                        [IMU]     │
│                                         │
│ Servo Ports:                            │
│   (Empty - available for future)        │
│                                         │
└─────────────────────────────────────────┘

⚠️ Names MUST match exactly!
⚠️ Configuration MUST be activated!
```

---

## ✅ PRE-DRIVE VISUAL CHECK

**Walk around robot and verify:**

### Front View (Looking at Front):
```
    YOU 👤
     ↓
     
  ⚙️    ⚙️   ← Can you see front wheels?
  FL    FR  ← Are they labeled correctly?
            ← Do rollers form part of X pattern?
```

### Left Side View:
```
         ⚙️  ← Front Left
         
         
         ⚙️  ← Back Left
         
    Can you see cables?
    Are they secure?
```

### Back View:
```
  ⚙️    ⚙️   ← Can you see back wheels?
  BL    BR  ← Cables not tangled?
            ← Battery secure?
```

### Top View:
```
   /⚙️    ⚙️\   ← X pattern visible?
   
   [Hub]     ← Hub secure?
   
   \⚙️    ⚙️/   ← All connections good?
```

---

## 🎯 MOVEMENT VERIFICATION

**When you press D-PAD UP in Motor Test:**

```
         YOU
          ↓
          
    ⚙️→   ⚙️→   All wheels should 
              spin THIS direction
              (AWAY from you)
    ⚙️→   ⚙️→
```

**If ANY wheel spins the wrong way, STOP and tell us!**

---

## 📞 NEED HELP?

Reference these files:
- `DRIVER_HUB_SETUP.md` - Detailed setup
- `QUICK_START_CHECKLIST.md` - Step-by-step
- `HARDWARE_SETUP_DIAGRAM.md` - Hardware details

---

**Team Penguinauts 32240** | **INTO THE DEEP 2024-2025**

*Print this diagram and keep it with your robot!*

