# Round Robin vs SRTF — CPU Scheduling Simulator

A Java Swing desktop application that simulates and compares two CPU scheduling algorithms:
**Round Robin (RR)** and **Shortest Remaining Time First (SRTF)**.

---

## Project Overview

| Item | Details |
|---|---|
| Algorithms | Round Robin (preemptive, time-sliced) vs SRTF (preemptive, shortest-first) |
| Main Focus | Fairness (RR) vs Efficiency (SRTF) |
| Special Input | Time Quantum for Round Robin |
| Language | Java (Swing GUI) |

---

## How to Run

**Requirements:** Java 8 or newer

```bash
# 1. Compile all files
javac *.java

# 2. Run the application
java MainWindow
```

---

## File Structure

| File | Role |
|---|---|
| `Process.java` | Stores process data (PID, AT, BT) and computed metrics (WT, TAT, RT, CT) |
| `GanttEntry.java` | Represents one block on a Gantt chart (pid, start, end) |
| `RoundRobin.java` | Round Robin scheduling algorithm (FIFO queue, time-sliced) |
| `SRTF.java` | SRTF scheduling algorithm (min-heap by remaining time, preemptive) |
| `ScenarioRunner.java` | Runs both algorithms on the same process list and stores results |
| `InputHandler.java` | Console-side input validation (used internally) |
| `GanttPanel.java` | Custom painted Gantt chart component |
| `MainWindow.java` | Main GUI window — assembles all panels and handles simulation |

---

## Interface Sections

| Section | Description |
|---|---|
| Input Panel | Enter PID, Arrival Time, Burst Time for each process |
| Time Quantum Field | Set the quantum for Round Robin (must be ≥ 1) |
| Add / Remove Buttons | Dynamically add or remove process rows |
| Run Simulation | Validates input and runs both algorithms |
| Clear | Clears all input and output |
| Reset Input | Resets only the input fields |
| Scenario Buttons | Load 5 predefined test scenarios instantly |
| Gantt Chart (RR) | Visual timeline of Round Robin execution |
| Gantt Chart (SRTF) | Visual timeline of SRTF execution |
| Ready Queue Log | Snapshot of the RR ready queue at each scheduling step |
| Results Table (RR) | Per-process AT, BT, CT, TAT, WT, RT + averages |
| Results Table (SRTF) | Per-process AT, BT, CT, TAT, WT, RT + averages |
| Comparison Summary | Side-by-side metric comparison with winner highlighted |
| Final Conclusion | Auto-generated analysis, quantum effect, and recommendation |

---

## Metrics Calculated

| Metric | Formula |
|---|---|
| CT (Completion Time) | Time when process finishes |
| TAT (Turnaround Time) | CT − Arrival Time |
| WT (Waiting Time) | TAT − Burst Time |
| RT (Response Time) | First CPU time − Arrival Time |
| Avg WT / TAT / RT | Sum of each metric ÷ number of processes |

---

## Input Validation

The application validates all input before simulation and rejects:

| Validation | Rule |
|---|---|
| Empty PID | PID field cannot be blank |
| Duplicate PID | Each process must have a unique ID |
| Arrival Time | Must be an integer ≥ 0 |
| Burst Time | Must be an integer ≥ 1 |
| Time Quantum | Must be an integer ≥ 1 |
| Non-integer input | Caught and shown as a clear error message |

---

## Test Scenarios

| Scenario | Description | Quantum |
|---|---|---|
| A — Basic Mixed | Normal workload: P1(0,8), P2(1,4), P3(2,9), P4(3,5), P5(4,2) | 3 |
| B — Quantum Sensitivity | All arrive at t=0; run with Q=2 then change to Q=8 to see the difference | 2 |
| C — Short-Job Heavy | Many short-burst processes; SRTF advantage is clearly visible | 2 |
| D — Fairness Case | All arrive at t=0; observe how RR distributes CPU equally | 2 |
| E — Validation Demo | Intentionally invalid data (Q=0, negative AT, duplicate PID, BT=0) | — |

---

## Algorithm Notes

### Round Robin
- Uses a **FIFO ready queue**
- Each process gets at most `quantum` time units per turn
- Processes that arrive during a burst are enqueued **before** the preempted process re-enters
- CPU idles only when the queue is empty and no process has arrived yet

### SRTF (Shortest Remaining Time First)
- Uses a **min-heap (PriorityQueue)** ordered by remaining time
- Preempts immediately when a shorter job arrives
- Tie-break: earlier arrival time → then alphabetical PID
- Adjacent Gantt blocks for the same process are merged for clarity

---

## Comparison Focus

| Question | Answer |
|---|---|
| Which is fairer? | **Round Robin** — every process gets equal CPU slices |
| Which minimizes WT/TAT? | **SRTF** — always runs the shortest remaining job |
| Which gives faster first response? | **Round Robin** (with small quantum) — all processes respond quickly |
| Does SRTF favor short jobs? | Yes — short jobs complete immediately when they arrive |
| Effect of quantum size? | Small Q → more fairness, more switches; Large Q → approaches FCFS |

---

## Conclusion Template (auto-generated in app)

After each simulation the app generates a conclusion covering:
- Which algorithm won on each metric (WT, TAT, RT)
- Fairness analysis
- Short-job behavior
- Observed effect of the selected quantum
- Recommendation for the tested workload
