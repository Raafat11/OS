public class GanttEntry {

    String pid;   // Process ID that was running ("IDLE" when CPU is idle)
    int    start; // Start time of the block
    int    end;   // End time of the block

    public GanttEntry(String pid, int start, int end) {
        this.pid   = pid;
        this.start = start;
        this.end   = end;
    }

    @Override
    public String toString() {
        return String.format("[%s  %d→%d]", pid, start, end);
    }
}
