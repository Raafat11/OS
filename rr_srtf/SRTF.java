import java.util.*;

public class SRTF {
    //Results (filled after simulate())
    List<GanttEntry> gantt = new ArrayList<>();
    double avgWT  = 0;
    double avgTAT = 0;
    double avgRT  = 0;

    //Core simulation
    public void simulate(List<Process> original) {

        List<Process> procs = deepCopy(original);
        procs.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int n       = procs.size();
        int done    = 0;
        int time    = 0;
        int nextIdx = 0;

        // Min-heap: shortest remaining time first
        PriorityQueue<Process> pq = new PriorityQueue<>(
            Comparator.comparingInt((Process p) -> p.remainingTime)
                    .thenComparingInt(p -> p.arrivalTime)
                    .thenComparing(p -> p.pid)
        );

        gantt.clear();

        while (done < n) {

            // Add all processes that have arrived
            while (nextIdx < n && procs.get(nextIdx).arrivalTime <= time)
                pq.add(procs.get(nextIdx++));

            if (pq.isEmpty()) {
                // CPU idle
                int nextArrival = procs.get(nextIdx).arrivalTime;
                gantt.add(new GanttEntry("IDLE", time, nextArrival));
                time = nextArrival;
                continue;
            }

            Process current = pq.poll();

            // First time on CPU → record response time
            if (current.firstRun) {
                current.responseTime = time - current.arrivalTime;
                current.firstRun = false;
            }

            // Find the next event that could cause preemption:
            //   either the next process arrival or the current process finishing
            int nextArrival2 = (nextIdx < n) ? procs.get(nextIdx).arrivalTime : Integer.MAX_VALUE;
            int finishTime   = time + current.remainingTime;
            int runUntil     = Math.min(finishTime, nextArrival2);

            gantt.add(new GanttEntry(current.pid, time, runUntil));
            current.remainingTime -= (runUntil - time);
            time = runUntil;

            // Enqueue any newly arrived processes
            while (nextIdx < n && procs.get(nextIdx).arrivalTime <= time)
                pq.add(procs.get(nextIdx++));

            if (current.remainingTime == 0) {
                current.completionTime  = time;
                current.turnaroundTime  = current.completionTime - current.arrivalTime;
                current.waitingTime     = current.turnaroundTime - current.burstTime;
                done++;
            } else {
                // Re-insert (a shorter job may have arrived)
                pq.add(current);
            }
        }

        // Merge consecutive Gantt blocks for the same process (cleaner display)
        gantt = mergeGantt(gantt);
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
        // Write back to originals
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
    /** Collapses adjacent entries with the same pid into one block. */
    private List<GanttEntry> mergeGantt(List<GanttEntry> raw) {
        if (raw.isEmpty()) return raw;
        List<GanttEntry> merged = new ArrayList<>();
        GanttEntry cur = raw.get(0);
        for (int i = 1; i < raw.size(); i++) {
            GanttEntry next = raw.get(i);
            if (next.pid.equals(cur.pid) && next.start == cur.end) {
                cur = new GanttEntry(cur.pid, cur.start, next.end);
            } else {
                merged.add(cur);
                cur = next;
            }
        }
        merged.add(cur);
        return merged;
    }
}
