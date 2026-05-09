import java.util.*;

public class RoundRobin {

    //Results (filled after simulate())
    List<GanttEntry> gantt      = new ArrayList<>();
    List<String>     queueLog   = new ArrayList<>();  // ready-queue snapshots
    double avgWT  = 0;
    double avgTAT = 0;
    double avgRT  = 0;

    //Core simulation

    /**
     * Runs Round Robin on a *copy* of the given processes.
     * The original list is not modified.
     */
    public void simulate(List<Process> original, int quantum) {
        // Deep-copy so the caller's list stays clean
        List<Process> procs = deepCopy(original);
         // Sort by arrival time
        procs.sort(Comparator.comparingInt(p -> p.arrivalTime));

        Queue<Process> readyQueue = new LinkedList<>();
        int time    = 0;
        int done    = 0;
        int n       = procs.size();
        int nextIdx = 0;// index into arrival-sorted list

        gantt.clear();
        queueLog.clear();

        while (done < n) {
             // Enqueue all processes that have arrived by now
            while (nextIdx < n && procs.get(nextIdx).arrivalTime <= time) {
                readyQueue.add(procs.get(nextIdx));
                nextIdx++;
            }

        if (readyQueue.isEmpty()) {
            // CPU is idle – jump to next arrival
            int nextArrival = procs.get(nextIdx).arrivalTime;
            gantt.add(new GanttEntry("IDLE", time, nextArrival));
            time = nextArrival;
            // Enqueue newly arrived
            while (nextIdx < n && procs.get(nextIdx).arrivalTime <= time) {
                readyQueue.add(procs.get(nextIdx));
                nextIdx++;
                }
                continue;
            }
             // Log the ready queue state before picking
            queueLog.add("t=" + time + " | Queue: " + queueSnapshot(readyQueue));

            Process current = readyQueue.poll();

            // First time this process gets the CPU → record response time
            if (current.firstRun) {
                current.responseTime = time - current.arrivalTime;
                current.firstRun = false;
            }

              // How long does it run this burst?
            int runTime = Math.min(quantum, current.remainingTime);
            int startTime = time;
            time += runTime;
            current.remainingTime -= runTime;

           // Enqueue processes that arrived during this burst (before re-adding current)
            while (nextIdx < n && procs.get(nextIdx).arrivalTime <= time) {
                readyQueue.add(procs.get(nextIdx));
                nextIdx++;
            }

            // Record Gantt block
            gantt.add(new GanttEntry(current.pid, startTime, time));

            if (current.remainingTime == 0) {
                // Process finished
                current.completionTime  = time;
                current.turnaroundTime  = current.completionTime - current.arrivalTime;
                current.waitingTime     = current.turnaroundTime - current.burstTime;
                done++;
            } else {
                // Not finished – re-enter the queue
                readyQueue.add(current);
            }
        }
        // Compute averages
        double sumWT = 0, sumTAT = 0, sumRT = 0;
        for (Process p : procs) {
            sumWT  += p.waitingTime;
            sumTAT += p.turnaroundTime;
            sumRT  += p.responseTime;
        }
        avgWT  = sumWT  / n;
        avgTAT = sumTAT / n;
        avgRT  = sumRT  / n;
         // Write computed metrics back to original list (matched by pid)
        for (Process orig : original) {
            for (Process sim : procs) {
                if (orig.pid.equals(sim.pid)) {
                    orig.completionTime = sim.completionTime;
                    orig.waitingTime    = sim.waitingTime;
                    orig.turnaroundTime = sim.turnaroundTime;
                    orig.responseTime   = sim.responseTime;
                    break;
                }
            }
        }
    }
    
     //Helpers
    private List<Process> deepCopy(List<Process> src) {
        List<Process> copy = new ArrayList<>();
        for (Process p : src)
            copy.add(new Process(p.pid, p.arrivalTime, p.burstTime));
        return copy;
    }

    private String queueSnapshot(Queue<Process> q) {
        if (q.isEmpty()) return "(empty)";
        StringBuilder sb = new StringBuilder();
        for (Process p : q) {
            if (sb.length() > 0) sb.append(" → ");
            sb.append(p.pid).append("(").append(p.remainingTime).append(")");
        }
        return sb.toString();
    }
}
