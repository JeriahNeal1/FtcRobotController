# Deep Research Report: Continuous-Cycle Autonomous and Vision Constraints for FTC DECODE

## Scope, sources, and versioning

This report synthesizes constraints and capabilities relevant to building a **high-throughput, “continuous-cycle” scoring routine** for the 2025–2026 DECODE season (DECODE presented by entity["company","RTX","aerospace defense company"]), with special focus on (a) autonomous-period legality and (b) practical vision integration using AprilTags and the HuskyLens. citeturn18search5turn19view0

Primary sources were the official **DECODE Competition Manual** (Team Update 22 edition, posted on FIRST Resources), official **ftc-docs** pages on AprilTags and HuskyLens, and primary device documentation for HuskyLens and REV Through Bore Encoders. citeturn18search5turn24view0turn15view0turn24view3

A key framing constraint: FTC match flow is **30 seconds Autonomous**, an **8-second AUTO→TELEOP transition**, then **2:00 minutes TELEOP**—so a “forever-running” fully autonomous routine is **not compatible with match structure as written**. However, you *can* design:  
- a continuous cycle **within the 30-second Autonomous period**, and  
- “automation macros” during TELEOP *initiated and interruptible by drivers*, consistent with remote operation expectations in TELEOP. citeturn36view1turn11view1

## Autonomous-related rule constraints for a continuous-cycle scorer with GATE resets

### Match structure and timing constraints that govern “continuous loop” behavior

A DECODE match consists of:  
- **AUTO (0:30)**, robot operates with *no driver control/input*;  
- an **8-second scoring-relevant transition** between AUTO and TELEOP;  
- **TELEOP (2:00)**, drivers control robots. citeturn36view1turn36view4

Two timing-related “hard stops” matter for automation design:

- **No powered motion during the AUTO→TELEOP transition**: any powered movement of the robot or mechanisms during that transition is a **MAJOR FOUL**. This applies to code that auto-switches OpModes, re-initializes subsystems, or continues “background” motion after AUTO ends. citeturn11view1turn36view4

- **No powered motion after TELEOP ends** (until signaled to retrieve robots). Violations can escalate sharply, including **MAJOR FOUL per artifact if a post-TELEOP launch scores**, and **MAJOR FOUL if the robot contacts a GATE after TELEOP end**. citeturn36view5

Implication for a continuous-cycle scorer: your autonomy/scheduler must be **time-aware**, with explicit “arming/disarming” of shooter and GATE-reset actions near period boundaries (not just relying on driver behavior). citeturn36view5turn11view1

### Scoring, scoring limits, and the mechanical reality of “goal resets”

In DECODE, the key scoring element is the **ARTIFACT** (purple/green ball-like element), with **24 purple and 12 green** in a match (noting that FIRST Championship/Premier Events can modify distribution/quantities per manual). citeturn8view0turn29view8turn9view3

#### Artifact scoring and the “classifier” mechanics
To earn **CLASSIFIED** or **OVERFLOW** points, an artifact must:  
- enter the GOAL through the open top,  
- exit via the GOAL archway,  
- pass through the **SQUARE**, then come to rest on the RAMP (or exit the RAMP), and  
- be released by a robot (not “stuck” in the robot). citeturn4view0turn4view1turn12view6

Classification is determined by where it comes to rest:  
- **CLASSIFIED** if it comes to rest on the ramp **not** contacting the SQUARE. citeturn4view0  
- **OVERFLOW** if it comes to rest **while contacting the SQUARE**, and overflow conditions also arise when artifacts pass over/through the classifier system such that they don’t settle as “classified.” citeturn4view0turn29view5

#### Point values and ranking thresholds
The manual’s point table is explicit and is essential for strategy modeling:

- **LEAVE** (AUTO): 3 points  
- **CLASSIFIED** artifact: 3 points (AUTO and TELEOP)  
- **OVERFLOW** artifact: 1 point (AUTO and TELEOP)  
- **DEPOT**: 1 point (TELEOP scored, assessed at end of match)  
- **PATTERN**: 2 points per artifact that matches the motif index (AUTO and TELEOP; assessed at end of each relevant period)  
- **BASE**: 5 points partial return; 10 points full return; plus a 10-point bonus if both alliance robots are fully returned. citeturn39view0

MINOR FOUL and MAJOR FOUL values (important because “illegal scoring” can erase gains):  
- MINOR FOUL = +5 points to opponent  
- MAJOR FOUL = +15 points to opponent citeturn39view2

Ranking-point thresholds (as of Team Update 22) are event-type dependent; for “All Other Events” (typical qualifiers), thresholds are:  
- MOVEMENT RP: 16  
- GOAL RP: 36  
- PATTERN RP: 18  
(Regional Championships and FIRST Championship thresholds were listed as “TBA” pending Team Updates.) citeturn39view0

#### The “goal reset” is the GATE release into the opponent’s zone
What teams often describe as “resetting the goal” is, in DECODE terms, using the **GATE** at the bottom of the classifier ramp:

- The **RAMP fits up to 9 classified artifacts**. citeturn29view3turn29view5  
- The **GATE prevents classified artifacts from exiting into the opposing alliance’s SECRET TUNNEL ZONE**; however, **OVERFLOW artifacts can pass over the top of the GATE** into the opposing alliance’s SECRET TUNNEL ZONE. citeturn29view5  
- The GATE is a **robot-activated, push-to-open** mechanism that releases **classified artifacts** from your ramp. citeturn29view5  
- The manual warns that the GATE may close before clearing all artifacts, and it is **not an arena fault**; teams should be prepared to **hold the GATE open** to fully clear the ramp. citeturn29view5

Engineering implications for autonomous design:
- Your “reset” routine must treat the GATE as **variable-duration** and handle partial clears robustly (e.g., hold open, re-contact if it closes early). citeturn29view5  
- The mechanical interface is quantified: the GATE contact point ranges roughly **3.75–5.5 inches** (closed to open interface range described) and requires about **2 inches of horizontal displacement** to open. citeturn29view7  
- Clearing your ramp sends artifacts into an area the *opponent can potentially access* (their SECRET TUNNEL ZONE), which has strategic consequences and interacts with protected-zone contact rules described below. citeturn29view5turn13view8

### Pattern scoring, why resets can cost you, and when that matters

Pattern scoring is assessed at:  
- the **end of AUTO** (AUTO pattern points), and  
- the **end of the match** after all robots/artifacts come to rest (TELEOP pattern points). citeturn36view5

A ramp artifact contributes to PATTERN points only if:  
- it is **directly on the RAMP**,  
- its color order matches the motif index, and  
- it is **retained by the GATE** (i.e., not released). citeturn36view5

Therefore, a GATE “reset” is not free: releasing classified artifacts can reduce (or eliminate) your PATTERN score at the scoring checkpoint. The decision to reset should be tied to match timing and whether your alliance is pursuing PATTERN points / PATTERN RP vs raw classified throughput. citeturn36view5turn39view0

### Illegal scoring actions and high-risk behaviors for a high-speed shooter

A continuous cycle scorer must avoid *especially costly* scoring-related violations:

- **Launching only from legal zones:** Robots may only **LAUNCH** scoring elements when inside a **LAUNCH ZONE** or overlapping a **LAUNCH LINE**. Launching outside these areas is a **MINOR FOUL per launched element**, escalating to a **MAJOR FOUL per launched element if it enters the open top of the GOAL**. citeturn10view2turn10view3

  “LAUNCHED” includes being shot into the air, propelled across the floor, or thrown forcefully; “bulldozing” is explicitly not launching. citeturn10view2turn10view3

- **Launch into your own goal; don’t “place” onto ramps:** Robots may not intentionally place or launch artifacts directly onto their own ramp, nor place/launch into the opponent goal or ramp. Violations incur **MAJOR FOUL per artifact**, and certain cases award the opponent the PATTERN RP. citeturn12view4turn12view6

- **Don’t use artifacts to “game” field elements:** A robot may not deliberately use a scoring element to ease/amplify a challenge associated with a field element beyond intended use—explicitly including **using an artifact to hold open the GATE**. Penalty: **MAJOR FOUL per scoring element**. citeturn40view0turn40view1

- **Keep artifacts in-bounds:** Intentionally ejecting a scoring element from the field is a **MAJOR FOUL per element**, though elements leaving during scoring attempts are not automatically “intentional ejections.” This still pushes a high-speed shooter toward careful control, especially near field boundaries and with ricochets. citeturn40view1turn40view2

- **Don’t damage scoring elements:** Damaging artifacts triggers escalating consequences; repeated damage can cause disabling and require corrective action/reinspection. citeturn40view2turn40view3

### Quantitative “possession” constraint: CONTROL limit for cycling

Robots may not simultaneously **CONTROL more than 3 artifacts**. Over-limit penalties are **MINOR FOUL per artifact over the limit** with a **YELLOW CARD** if excessive. The manual explicitly warns teams to design so it’s impossible to inadvertently control more than the limit. citeturn40view3turn40view4turn40view5

This is a critical design constraint for a roller intake + internal canister + shooter: your mechanism and/or logic must ensure you cannot accumulate 4+ artifacts in a way that meets the rule’s definition of “control.” citeturn40view3turn40view5

### Autonomous-specific opponent interference constraints

During AUTO, the field is partitioned into “blue side” vs “red side” by tile columns (A–C vs D–F). During AUTO a robot may not:  
- contact an opposing robot that is completely within the opposing side (directly or via an artifact), or  
- disrupt a pre-staged artifact on the opposing side (directly, transitively, or by launching into it).  
Penalties: **MAJOR FOUL per instance** (robot contact) and **MAJOR FOUL per artifact** (pre-staged disruption). citeturn11view0turn11view1

Implication: an AUTO routine optimized purely for throughput but crossing into the opponent side is **legally risky**, particularly for an omni drive that can strafe aggressively and for a shooter that might scatter artifacts. citeturn11view0turn11view1

### Protected zones and robot-robot contact constraints that shape cycle paths

High-cadence cycling will cause close interactions with opponents near chokepoints. DECODE defines several protected interaction zones (mostly relevant during TELEOP but strategically important for an “autonomous-like” cycle running under driver initiation):

- **Opponent GATE ZONE protection:** You may not contact an opponent robot if either robot is in the opponent’s GATE ZONE (with a specific exception that defers to SECRET TUNNEL rules). Penalty: MINOR FOUL. citeturn13view7

- **Opponent SECRET TUNNEL protection:** If you are in the opponent’s SECRET TUNNEL ZONE, you may not contact the opponent robot regardless of who initiates. Penalty: MINOR FOUL. citeturn14view0turn13view8

- **Opponent LOADING ZONE protection:** No contact while either robot is in the opponent’s LOADING ZONE. Penalty: MINOR FOUL. citeturn14view0turn13view7

- **Opponent BASE ZONE protection in endgame:** In the last 20 seconds, no contact while either robot is in the opponent’s BASE ZONE. Penalty: MAJOR FOUL and awards the contacted robot fully-returned-to-base points. citeturn14view0turn13view3

Opponent interaction rules also include: prohibition on deliberate functional impairment (can escalate to cards), a 3-second PIN limit, and “don’t shut down major gameplay” constraints (e.g., blocking access to opponent gate). citeturn10view9turn10view14turn13view7

### Safety and inspection-related constraints that can affect autonomous behavior

Even a legal scoring routine can become illegal if it becomes unsafe or damages field elements:

- Robots must not pose an undue hazard; dangerous operation can lead to **DISABLED** plus warnings/cards if repeated. citeturn40view6turn40view7  
- Field damage is prohibited; especially relevant here, “field damage includes… causing the GATE to bend or break off.” Reinspection/corrective action may be required after damage. citeturn40view8turn40view9  
- Scoring-element damage can also trigger disable and corrective action, pushing shooter designs away from destructive wheel pinch points or sharp edges. citeturn40view2turn40view3

Operationally, the match system expects teams intending to run AUTO to have an OpMode selected with the **30-second timer enabled** on Driver Station. citeturn9view5turn36view1

## Vision-related constraints and capabilities for AprilTags and HuskyLens in FTC DECODE

image_group{"layout":"carousel","aspect_ratio":"16:9","query":["FTC DECODE field AprilTag locations diagram","FTC DECODE goal classifier gate ramp square","FTC DECODE obelisk AprilTag"],"num_per_query":1}

### AprilTags in DECODE: IDs, placement, and intended uses

In DECODE, AprilTags are 36h11-family targets sized **8.125 in (~20.65 cm) square**. citeturn19view0turn29view8

Their placements and IDs are specified in the Competition Manual:

- **Blue alliance goal AprilTag ID = 20**  
- **Red alliance goal AprilTag ID = 24** citeturn19view0turn29view8

The OBELISK has one AprilTag on each of its 3 rectangular faces with IDs **21, 22, 23**, each corresponding to a different motif sequence (GPP, PGP, PPG). citeturn19view0turn29view1

The OBELISK orientation is randomized each match by field staff after teams set up; importantly, the manual says the OBELISK location is **not intended to be deterministic** relative to the field coordinate system and **should not be used for navigation**. citeturn19view0turn29view2

FTC’s AprilTag tips page for DECODE describes three primary uses:  
- identifying the randomized motif (OBELISK),  
- targeting the goal for more accurate launching (goal tags), and  
- localization (visual odometry style) using goal tags, with reference to AprilTag localization docs. citeturn15view0

### Environmental sensitivities: lighting and multi-tag visibility

FTC explicitly warns that AprilTag detection can fail when lighting reduces contrast (e.g., sunlight “washing out” the tag). A recommended mitigation is adjusting camera exposure and gain (reducing exposure while increasing gain), also beneficial for reducing motion blur while moving. citeturn15view0

FTC also warns that robots near the goal may see **multiple OBELISK tags** at once, and that there is **no defined order** for the OBELISK tags, making “read two tags and infer which is front” unreliable. The recommended workaround is moving to a viewpoint where **only the front-face tag is visible**. citeturn16view1turn15view0

### What FTC’s “standard” AprilTag pipeline provides: VisionPortal pose vs pixel boxes

FTC’s AprilTag software stack (VisionPortal + AprilTag Processor) is designed to provide more than just an ID: it can provide **pose estimation** (translation XYZ and rotations) relative to the camera, conditioned on tag metadata (including physical size) and camera calibration. citeturn33view0turn33view1

Key coordinate-frame guidance from FTC docs:
- The FTC SDK’s AprilTag pose uses axes where **X points right**, **Y points forward (range)**, and **Z points up**; with a forward-facing camera, these axes match the robot coordinate conventions used for IMU navigation. citeturn33view1turn15view2  
- The SDK supplies derived quantities like range, bearing, and elevation (useful for “aim at tag” control). citeturn15view2turn33view1

FTC further cautions that vision processing consumes CPU and USB bandwidth, and teams may need to balance resolution/FPS against resource limits; the SDK provides tools like selecting resolution and controlling previews. citeturn33view1

### HuskyLens in FTC: what it can do, what it can’t, and why it matters

The HuskyLens is treated as a **vision coprocessor**: it performs onboard recognition and sends simplified results to the robot controller. FTC’s HuskyLens tutorial explicitly states it plugs into a REV Hub **I2C** port, is not a USB webcam, and does not use VisionPortal. citeturn24view0turn28view5

#### Data output model (HuskyLens)
FTC’s HuskyLens tutorial describes HuskyLens AprilTag detection output as **bounding box telemetry**:
- number of “blocks” detected,  
- an ID code that “might not be correct or meaningful,”  
- bounding box size in pixels,  
- bounding box center position in pixels with origin at **top-left**; screen is **320×240**, center **(160,120)**. citeturn28view0turn28view3

For Java-side access, FTC describes fields like `.width`, `.height`, `.left`, `.top`, `.x`, `.y`, and `.id`. citeturn28view1turn28view0

DFRobot documentation adds that HuskyLens uses a coordinate system tied to its screen for object frame positions and provides its base hardware specs (Kendryte K210 processor, OV2640/GC0328 image sensor, 320×240 display, interfaces including UART/I2C, and “Tag Recognition” among built-in algorithms). citeturn27view2turn27view5turn26view0

#### I2C vs USB connectivity reality
Even though the device has a micro-USB connector for power/configuration in many contexts, **FTC’s supported robot integration is I2C**, and FTC guidance suggests using I2C buses 1–3 to avoid (unlikely) overload (bus 0 often has the built-in IMU on port 0). citeturn24view0turn28view4

At the protocol level, a public HuskyLens protocol reference specifies:  
- **I2C speed 100 kbit/s**  
- **I2C address 0x32**  
- bounding-box (“block”) data includes x/y center, width/height, and an ID, with x range **0–319** and y range **0–239** (matching 320×240). citeturn32view0turn27view2

#### Practical limitation vs VisionPortal AprilTag: no pose, and ID semantics differ
FTC’s own HuskyLens tutorial strongly implies that, for AprilTag navigation and targeting, teams “may find much more value from a UVC webcam and the FTC VisionPortal software.” citeturn28view3

This statement is consistent with the data models:
- HuskyLens gives **pixel-space** boxes (and potentially unreliable IDs), which require camera FOV calibration and filtering to produce stable controls. citeturn28view0turn32view0turn27view2  
- VisionPortal provides **estimated 3D pose** relative to the camera (when properly calibrated and metadata-configured), enabling localization or distance-aware aiming. citeturn33view0turn33view1turn15view2

### FTC’s rules for vision sensors, coprocessors, and external compute

FTC robot construction rules constrain what vision hardware can be used and how it can be integrated:

- Teams may not modify software on coprocessors unless explicitly allowed; manufacturer binary firmware updates may be applied. citeturn20view3turn20view4  
- Programmable vision coprocessors are only allowed if natively supported and listed; the supported list includes **Limelight 3A**, and other programmable vision devices are prohibited. HuskyLens is cited as configurable but not programmable and is treated like other coprocessors. citeturn19view1turn20view6  
- USB usage is restricted: only webcams/optical vision sensors (and a USB hub/switch, plus an Expansion Hub) are allowed on USB; only UVC webcams and allowed vision coprocessors may connect for USB vision. citeturn20view11turn20view12

Most importantly for “dashboard + external compute” ideas:
- During a MATCH, **only** the Robot Controller and Driver Station may originate communications on the robot network; **no other devices may connect**, and programming laptops/other devices must be disconnected prior to and during a match. citeturn22view4turn22view5  
- Software streaming over Wi‑Fi is restricted: only robot control/debug/telemetry may be streamed; **no continuous video streaming** is allowed. citeturn22view1turn21view8  
- Additionally, event policy prohibits participants from setting up their own Wi‑Fi networks, Bluetooth, or other 2.4/5 GHz systems in the venue. citeturn21view1turn21view6

This means dashboards like Panels (discussed below) are best treated as **development tooling outside matches**, not competition-match tooling.

### Panels dashboard capabilities vs competition constraints

Panels (an open-source dashboard) advertises features including:  
- starting/stopping/switching OpModes via a desktop UI,  
- live value configuration,  
- telemetry visualization, and  
- recording/replay sessions; it uses WebSockets and a Kotlin backend integrated with the FTC SDK. citeturn38view0

However, the Competition Manual’s control-system rules prohibit connecting other devices to the robot controller Wi‑Fi network during a match, which would generally prevent using a separate computer dashboard in-match. citeturn22view4turn22view5

## Prioritized requirements checklist for a legal, robust continuous-cycle autonomous system

The checklists below translate rules and device constraints into concrete design requirements. “Complexity” estimates assume an FTC Java codebase with a maintained subsystem architecture and automated testing where feasible.

### MVP requirements

| MVP requirement | Rationale (rules/device constraints) | Complexity | Suggested verification / test steps |
|---|---|---|---|
| Time-aware match-state handling (AUTO, transition, TELEOP, match end) with explicit “safe stop” | Must obey 30s AUTO + 8s transition + 2:00 TELEOP; no powered motion during transition and after TELEOP end. | Medium | Run scripted tests that intentionally attempt motion during transition/end and confirm system inhibits outputs; dry-run with DS timer cues; field test with refs observing. citeturn36view1turn11view1turn36view5 |
| Enforce “launch only in LAUNCH ZONE/LINE” gating for shooter | Illegal launches outside zone incur escalating penalties, including MAJOR FOUL if scored. | Medium | Mark launch boundaries on practice field; log robot pose when firing; test “edge cases” near line overlap; verify no fires outside zone. citeturn10view2turn10view3 |
| “Launch into open top” scoring strategy (no intentional ramp placement) | Illegal to intentionally place/launch directly onto ramp or into opponent goal/ramp; severe penalties and potential PATTERN RP effects. | Medium | Video review of shots; confirm trajectories aim at open top; include safeguards to stop firing if robot is misaligned or too close to ramp interface. citeturn12view4turn12view6 |
| Hard limit: never CONTROL >3 artifacts | CONTROL limit is central to intake/shooter design; penalties scale and can trigger YELLOW CARD if excessive. | Medium (often mech+code) | Stress test: feed artifacts rapidly and verify max retained at any moment; log intake counts; demonstrate “impossible to accidentally hold 4+” per rule guidance. citeturn40view3turn40view5 |
| Robust GATE reset routine (open + hold as needed, without using artifacts as wedges) | Gate may close early; teams should hold open to clear ramp; but using artifacts to hold gate open is a MAJOR FOUL per artifact. | Medium | Repeatability test: fill ramp with classified artifacts, run reset routine 50+ cycles; confirm full clear without wedging artifacts; inspect gate for damage. citeturn29view5turn40view1turn40view9 |
| Autonomous “stay on own side” plan unless deliberately accepting G402 risk | During AUTO you cannot disrupt opponent pre-staged artifacts or contact opponent robot on their side; penalties are MAJOR FOUL per instance/artifact. | Medium | Simulated path overlays; field tests with staged artifacts on both sides; verify robot never enters opponent side or never launches artifacts that could disturb opponent staging. citeturn11view0turn11view1 |
| Protected-zone compliance during TELEOP cycle automation | Contact rules in opponent GATE/SECRET TUNNEL/LOADING zones and in opponent BASE during last 20 seconds can penalize aggressive cycling and defense. | Medium | Practice scrimmages with “zone sentries”: designate observers to call protected-zone violations; track contact events in logs; tune fallback behaviors. citeturn13view7turn14view0turn14view0 |
| Minimum viable localization for repeatable launching positions | Launch-zone constraint + scoring accuracy requires consistent pose; VisionPortal pose requires webcam; HuskyLens alone gives pixel boxes. | Medium to High (depends on sensor choice) | Baseline: measure shot consistency from fixed pose; evaluate drift across a full match; test recovery after pushing or wheel slip. citeturn10view2turn28view0turn33view1 |
| Centralized configuration for hardware names + constants (single source of truth) | Reduces integration risk across multiple OpModes; supports rapid iteration and compliance checks. (This is a best-practice requirement; not mandated by rules but supports safe stop/zone logic.) | Low | Create a configuration review checklist; “swap motor name in one place” test; regression test that all OpModes still initialize. |
| Safety interlocks for out-of-bounds ejections and dangerous behavior | Intentional ejection is penalized; dangerous operation can lead to DISABLED; field damage (incl. bending gate) can require corrective action and reinspection. | Medium | Boundary tests near field edges; high-speed shot tests with backstops; current monitoring and automatic shutdown if erratic behavior observed. citeturn40view1turn40view6turn40view9 |

### Nice-to-have requirements

| Nice-to-have feature | Rationale (rules/device constraints) | Complexity | Suggested verification / test steps |
|---|---|---|---|
| VisionPortal AprilTag goal alignment + distance-aware aiming (webcam) | DECODE goal tags have known IDs (20 blue, 24 red) and known physical size; VisionPortal yields pose, enabling better accuracy than HuskyLens bounding boxes. | High | Calibrate camera; validate pose repeatability under varied lighting; drive-and-shoot tests while moving; tune exposure/gain per FTC AprilTag lighting guidance. citeturn19view0turn33view1turn15view0 |
| AprilTag-assisted localization with periodic corrections (“sensor fusion” with odometry + IMU) | FTC describes goal tags as usable for localization; mitigates wheel-odometry drift during long cycles. | High | Run long-duration drift tests; compare estimated pose vs measured floor marks; quantify correction frequency vs CPU/bandwidth use. citeturn15view0turn33view1turn15view2 |
| Multi-camera setup: HuskyLens for motif/secondary perception + webcam for AprilTags | FTC notes robots may use HuskyLens and USB webcams; HuskyLens not VisionPortal compatible. | High | Confirm USB compliance; confirm CPU headroom; test I2C bus stability (avoid bus 0 overload) and webcam stability simultaneously. citeturn28view5turn20view11turn24view0 |
| Add external odometry pods (2-wheel or 3-wheel) using Through Bore Encoders | FTC hub supports incremental quadrature via motor encoder ports; Through Bore Encoder provides 8192 counts/rev; improves localization and path repeatability. | Medium (hardware+integration) | Encoder linearity + slip tests; validate counts->distance conversion; repeatable path tests on worn tiles and after minor collisions. citeturn24view3turn24view4 |
| Dynamic obstacle avoidance and “protected-zone aware” rerouting | Collision/contact constraints (protected zones, pin rule, no gameplay shutdown) make dynamic routing beneficial late match. | High | Controlled scrimmage scenarios with moving obstacles; verify no protected-zone contact; measure cycle-time impact. citeturn13view7turn10view14turn13view7 |
| TeleOp “macro autonomy” (driver-initiated cycles with cancel) | Preserves driver-control requirement in TELEOP while achieving “continuous-loop” performance. Must still obey zone rules and end-of-period stops. | High | Human-in-the-loop testing: repeated macro activation/cancel; verify transition/endgame lockouts; confirm drivers can always interrupt. citeturn36view1turn36view5turn14view0 |
| Development dashboards (Panels) for tuning + record/replay outside matches | Panels provides live config, telemetry visualization, record/replay; but match rules prohibit additional devices connected during matches. | Medium | Use only in practice: confirm laptop disconnect before scrimmage/match; keep DS telemetry as competition-safe fallback. citeturn38view0turn22view4turn22view5 |
| Lighting-robust AprilTag configuration (exposure/gain profiles) | FTC warns lighting can wash tags out; recommends exposure/gain tuning; improves reliability across venues. | Medium | Venue simulation: bright side-lighting tests; motion blur tests at drive speed; confirm detection under multiple light conditions. citeturn15view0 |

### Simple conceptual data flow diagram to guide the eventual implementation

A practical architecture for a modular autonomous system (no code here, just structure) looks like:

**Sensors (IMU + encoders + vision)** → **Pose Estimator** → **Planner/Path-Follower** → **Action Scheduler** → **Subsystem Commands (drive, intake, shooter, gate reset)** → **Telemetry/logging**

This structure maps directly to (a) time/zone gating requirements (launch-zone checks, period transitions), (b) modular repeatable actions (intake cycle, shoot cycle, gate reset), and (c) the need to swap sensor strategies (HuskyLens-only vs webcam+VisionPortal) without rewriting everything. citeturn10view2turn28view0turn33view1

## Assumptions and open questions that must be answered before coding

### Explicit assumptions made due to ambiguity or missing info

- **Assumption:** “Goal reset” refers to operating the **GATE** to release classified artifacts from the ramp, consistent with DECODE’s classifier design and common team terminology. citeturn29view5turn29view6  
- **Assumption:** “Continuous-loop autonomous forever” means “continuous cycling behavior,” likely spanning TELEOP via driver-initiated automation macros, because fully autonomous operation is constrained to the 30-second AUTO period. citeturn36view1turn11view1  
- **Assumption:** HuskyLens AprilTag “ID” readouts may not reliably match DECODE tag IDs (20/24/21–23), because FTC explicitly warns the HuskyLens-provided ID might not be correct/meaningful; thus HuskyLens use may default to aiming via bounding box center rather than ID-based selection. citeturn28view0turn19view0  
- **Assumption:** HuskyLens effective update rate for tag detection is **not specified** in the primary FTC docs; I2C bandwidth (100 kbit/s) and DFRobot performance claims (e.g., “faces at 30 fps”) indicate potential but are not a guarantee for AprilTag workloads. This requires measurement on your exact firmware and mounting geometry. citeturn32view0turn31view2turn28view0

### Open questions to resolve before any architecture or code work begins

**Robot and drivetrain**
- Do you have a calibrated **IMU** available/enabled (Control Hub built-in orientation), and what is the hub mounting orientation on the robot? (This affects every pose/heading calculation.) citeturn24view0turn15view2  
- Which motors are actually encoder-equipped (built-in encoders on all 4 drive motors vs only one active), and will you add external encoder pods? The feasibility of precise pathing depends heavily on this. citeturn40view3turn24view3  
- Exact wheelbase geometry (trackwidth, wheel diameter, gear ratio), and whether omni wheel slip is acceptable for your desired accuracy.

**Field plan and legal start/scoring choices**
- Which **LAUNCH LINE** do you plan to start on (there are multiple launch zones/lines), and what is your intended first shooting position that remains legal under G416? citeturn10view2turn36view5  
- Do you intend to pursue **PATTERN points / PATTERN RP** aggressively, or will you accept pattern loss in exchange for frequent GATE resets and higher classified throughput? citeturn36view5turn39view0

**Vision hardware strategy**
- Are you willing/able to add a **UVC webcam** to use VisionPortal AprilTag pose (recommended by FTC for navigation/AprilTags), or must the system rely on HuskyLens-only bounding boxes? citeturn28view3turn33view0turn20view11  
- HuskyLens mounting offsets: exact camera position relative to robot center, height from tiles, and the actual pitch/yaw offsets (you stated ~10° up and slightly off-center—quantify this). These are required for meaningful geometry from either AprilTag pose or pixel-to-angle conversions. citeturn15view2turn28view0

**Competition tooling constraints**
- Do you expect to use a laptop dashboard at events? If yes, you will need a competition-safe plan because other devices must disconnect before and during matches, and no continuous video streaming is allowed. citeturn22view4turn22view5turn22view1

**Collisions and multi-robot interactions**
- Do you plan to run cycle automation during TELEOP in a way that enters protected zones (SECRET TUNNEL, LOADING ZONE, GATE ZONE), where contact rules can penalize you even if the opponent initiates contact? citeturn14view0turn13view7  
- What is your tolerance for defensive contact and risk of pinning situations (3-count pin rule), and do you want the automation to include “yield/escape” behaviors? citeturn10view14turn13view7