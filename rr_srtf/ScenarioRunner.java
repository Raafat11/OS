import java.util.*;

public class ScenarioRunner {

    private RoundRobin rr   = new RoundRobin();
    private SRTF       srtf = new SRTF();

    //Result holders
    public List<Process> rrProcs;
    public List<Process> srtfProcs;
    public RoundRobin    rrResult;
    public SRTF          srtfResult;

    public void run(List<Process> processes, int quantum) {
        // Make independent copies
        rrProcs   = copy(processes);
        srtfProcs = copy(processes);
    
        // Run algorithms
        rr.simulate(rrProcs, quantum);
        srtf.simulate(srtfProcs);

        rrResult   = rr;
        srtfResult = srtf;
    }

    private List<Process> copy(List<Process> src) {
        List<Process> out = new ArrayList<>();
        for (Process p : src)
            out.add(new Process(p.pid, p.arrivalTime, p.burstTime));
        return out;
    }
}
