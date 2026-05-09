public class Process {

    //Input fields
    String pid;          // Process ID  (e.g. P1, P2 …)
    int    arrivalTime;  // When the process arrives in the ready queue
    int    burstTime;    // Total CPU time the process needs


    //Fields computed by each algorithm
    int completionTime;  // When execution finishes
    int waitingTime;     // Time spent waiting  (TAT - BT)
    int turnaroundTime;  // completionTime - arrivalTime
    int responseTime;    // First time process got the CPU - arrivalTime

    //Internal simulation helper
    int remainingTime;   // Used by SRTF / RR during simulation
    boolean firstRun;   // Tracks whether the process has run at all (for RT)

    public Process(String pid, int arrivalTime, int burstTime) {
        this.pid         = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime   = burstTime;
        reset();
    }
    
    /** Resets simulation state so the same object can be reused by both algorithms. */
    public void reset() {
        this.remainingTime  = burstTime;
        this.completionTime = 0;
        this.waitingTime    = 0;
        this.turnaroundTime = 0;
        this.responseTime   = -1;   // -1 means "not yet responded"
        this.firstRun       = true;
    }


    @Override
    public String toString() {
        return String.format("%-5s  AT=%-3d  BT=%-3d", pid, arrivalTime, burstTime);
    }
}
