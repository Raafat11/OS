import java.util.*;

public class InputHandler {

    private Scanner scanner;

    public InputHandler() {
        this.scanner = new Scanner(System.in);
    }

//Read number of processes
    public int readProcessCount() {
        while (true) {
            System.out.print("  Enter number of processes (1–20): ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) {
                System.out.println("  [ERROR] Input cannot be empty.");
                continue;
            }
            try {
                int n = Integer.parseInt(raw);
                if (n >= 1 && n <= 20) return n;
                System.out.println("  [ERROR] Must be between 1 and 20.");
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Enter a valid integer.");
            }
        }
    }

//  Read process list 
    public List<Process> readProcesses(int n) {
        List<Process> list    = new ArrayList<>();
        Set<String>   usedIDs = new HashSet<>();

        System.out.println("  Enter Arrival Time and Burst Time for each process:");
        System.out.println("  (Burst Time must be >= 1 ;  Arrival Time must be >= 0)");
        System.out.println("  (PID is auto-assigned as P1, P2 ... but you may override it)");

        for (int i = 1; i <= n; i++) {
            //  PID 
            String pid = readPID("  Process " + i + " PID (default P" + i + "): ", "P" + i, usedIDs);
            usedIDs.add(pid);

            //  Arrival Time 
            int at = readNonNegativeInt("  " + pid + " Arrival Time : ");
             //  Burst Time 
            int bt = readPositiveInt   ("  " + pid + " Burst Time   : ");
            list.add(new Process(pid, at, bt));
        }
        return list;
    }

    //  Read quantum
    public int readQuantum() {
        while (true) {
            System.out.print("  Enter Time Quantum for Round Robin (must be >= 1): ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) {
                System.out.println("  [ERROR] Quantum cannot be empty. Enter a positive integer.");
                continue;
            }
            try {
                int q = Integer.parseInt(raw);
                if (q >= 1) return q;
                System.out.println("  [ERROR] Quantum must be >= 1. Value " + q + " rejected.");
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] '" + raw + "' is not a valid integer.");
            }
        }
    }

    // ReadMenu choice
    public int readMenuChoice(int min, int max) {
        while (true) {
            System.out.print("  Choice: ");
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) {
                System.out.println("  [ERROR] Input cannot be empty.");
                continue;
            }
            try {
                int c = Integer.parseInt(raw);
                if (c >= min && c <= max) return c;
                System.out.println("  [ERROR] Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] Enter a valid integer.");
            }
        }
    }

    //  helpers 
    private String readPID(String prompt, String defaultPID, Set<String> usedIDs) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            String pid = raw.isEmpty() ? defaultPID : raw.toUpperCase();

            if (pid.isEmpty()) {
                System.out.println("  [ERROR] PID cannot be empty.");
                continue;
            }
            if (usedIDs.contains(pid)) {
                System.out.println("  [ERROR] Duplicate PID '" + pid + "'. Each process must have a unique ID.");
                continue;
            }
            return pid;
        }
    }

    private int readNonNegativeInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) {
                System.out.println("  [ERROR] Field cannot be empty. Enter an integer >= 0.");
                continue;
            }
            try {
                int v = Integer.parseInt(raw);
                if (v >= 0) return v;
                System.out.println("  [ERROR] Value must be >= 0.");
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] '" + raw + "' is not a valid integer.");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) {
                System.out.println("  [ERROR] Field cannot be empty. Burst Time must be >= 1.");
                continue;
            }
            try {
                int v = Integer.parseInt(raw);
                if (v >= 1) return v;
                System.out.println("  [ERROR] Burst Time must be >= 1. Value " + v + " rejected.");
            } catch (NumberFormatException e) {
                System.out.println("  [ERROR] '" + raw + "' is not a valid integer.");
            }
        }
    }
        
    // Pause – press Enter to continue 
    public void pressEnter() {
        System.out.print("\n  Press ENTER to continue...");
        scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }
}
