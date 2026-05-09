import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    // some colors to use for the process blocks in gantt chart
    Color[] colors = {
        new Color(70, 130, 180),
        new Color(60, 179, 113),
        new Color(210, 105, 30),
        new Color(147, 112, 219),
        new Color(220, 20, 60),
        new Color(32, 178, 170),
        new Color(255, 140, 0),
        new Color(105, 105, 105)
    };

    // input stuff
    JTextField quantumField;
    JPanel processRowsPanel;
    List<JTextField[]> rows = new ArrayList<>();

    // gantt charts
    GanttPanel ganttRR;
    GanttPanel ganttSRTF;

    // tables
    DefaultTableModel rrModel;
    DefaultTableModel srtfModel;
    JTable rrTable;
    JTable srtfTable;

    // other output areas
    JTextArea queueArea;
    JTextArea comparisonArea;
    JTextArea conclusionArea;

    JLabel statusBar;

    public MainWindow() {
        setTitle("Round Robin vs SRTF - CPU Scheduling");
        setSize(1150, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);
    }

    void buildUI() {
        setLayout(new BorderLayout());

        // top title
        JLabel title = new JLabel("Round Robin vs SRTF - CPU Scheduling Comparison", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 0));
        add(title, BorderLayout.NORTH);

        // split pane: left = input, right = output
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildInputPanel(), buildOutputPanel());
        split.setDividerLocation(290);
        add(split, BorderLayout.CENTER);

        // status bar at the bottom
        statusBar = new JLabel("  Ready. Add processes and click Run.");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setFont(new Font("Arial", Font.PLAIN, 12));
        add(statusBar, BorderLayout.SOUTH);
    }

    // builds the left side where user enters data
    JScrollPane buildInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // quantum
        JLabel qLabel = new JLabel("Time Quantum (Round Robin):");
        qLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        qLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(qLabel);
        panel.add(Box.createVerticalStrut(4));

        quantumField = new JTextField("3");
        quantumField.setMaximumSize(new Dimension(80, 28));
        quantumField.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(quantumField);
        panel.add(Box.createVerticalStrut(14));

        // process input section header
        JLabel procLabel = new JLabel("Processes:");
        procLabel.setFont(new Font("Arial", Font.BOLD, 13));
        procLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(procLabel);
        panel.add(Box.createVerticalStrut(4));

        // column headers row
        JPanel colHeaders = new JPanel(new GridLayout(1, 4, 4, 0));
        colHeaders.setMaximumSize(new Dimension(270, 20));
        colHeaders.setAlignmentX(LEFT_ALIGNMENT);
        colHeaders.add(new JLabel("PID", SwingConstants.CENTER));
        colHeaders.add(new JLabel("Arrival", SwingConstants.CENTER));
        colHeaders.add(new JLabel("Burst", SwingConstants.CENTER));
        colHeaders.add(new JLabel("", SwingConstants.CENTER));
        panel.add(colHeaders);
        panel.add(Box.createVerticalStrut(3));

        // scrollable area for process rows
        processRowsPanel = new JPanel();
        processRowsPanel.setLayout(new BoxLayout(processRowsPanel, BoxLayout.Y_AXIS));
        JScrollPane rowsScroll = new JScrollPane(processRowsPanel);
        rowsScroll.setPreferredSize(new Dimension(270, 200));
        rowsScroll.setMaximumSize(new Dimension(270, 200));
        rowsScroll.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(rowsScroll);
        panel.add(Box.createVerticalStrut(8));

        // add default rows to start with
        addRow("P1", "0", "8");
        addRow("P2", "1", "4");
        addRow("P3", "2", "9");

        // buttons
        JButton addBtn = new JButton("+ Add Process");
        addBtn.setAlignmentX(LEFT_ALIGNMENT);
        addBtn.addActionListener(e -> addRow("", "", ""));
        panel.add(addBtn);
        panel.add(Box.createVerticalStrut(6));

        JButton runBtn = new JButton("Run Simulation");
        runBtn.setFont(new Font("Arial", Font.BOLD, 13));
        runBtn.setBackground(new Color(70, 130, 180));
        runBtn.setForeground(Color.WHITE);
        runBtn.setOpaque(true);
        runBtn.setAlignmentX(LEFT_ALIGNMENT);
        runBtn.setMaximumSize(new Dimension(180, 32));
        runBtn.addActionListener(e -> runSimulation());
        panel.add(runBtn);
        panel.add(Box.createVerticalStrut(6));

        JButton clearBtn = new JButton("Clear");
        clearBtn.setAlignmentX(LEFT_ALIGNMENT);
        clearBtn.addActionListener(e -> clearAll());
        panel.add(clearBtn);
        panel.add(Box.createVerticalStrut(14));

        // --- Reset Input Button ---
        JButton resetBtn = new JButton("Reset Input");
        resetBtn.setAlignmentX(LEFT_ALIGNMENT);
        resetBtn.setMaximumSize(new Dimension(180, 28));
        resetBtn.setBackground(new Color(180, 80, 80));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setOpaque(true);
        resetBtn.addActionListener(e -> resetInput());
        panel.add(resetBtn);
        panel.add(Box.createVerticalStrut(14));

        // --- Scenario Buttons ---
        JLabel scenarioLabel = new JLabel("Load Scenario:");
        scenarioLabel.setFont(new Font("Arial", Font.BOLD, 13));
        scenarioLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(scenarioLabel);
        panel.add(Box.createVerticalStrut(5));

        // scenario A
        JButton scABtn = new JButton("Scenario A - Basic Mixed");
        scABtn.setAlignmentX(LEFT_ALIGNMENT);
        scABtn.setMaximumSize(new Dimension(270, 28));
        scABtn.addActionListener(e -> loadScenarioA());
        panel.add(scABtn);
        panel.add(Box.createVerticalStrut(4));

        // scenario B
        JButton scBBtn = new JButton("Scenario B - Quantum Sensitivity");
        scBBtn.setAlignmentX(LEFT_ALIGNMENT);
        scBBtn.setMaximumSize(new Dimension(270, 28));
        scBBtn.addActionListener(e -> loadScenarioB());
        panel.add(scBBtn);
        panel.add(Box.createVerticalStrut(4));

        // scenario C
        JButton scCBtn = new JButton("Scenario C - Short Job Heavy");
        scCBtn.setAlignmentX(LEFT_ALIGNMENT);
        scCBtn.setMaximumSize(new Dimension(270, 28));
        scCBtn.addActionListener(e -> loadScenarioC());
        panel.add(scCBtn);
        panel.add(Box.createVerticalStrut(4));

        // scenario D
        JButton scDBtn = new JButton("Scenario D - Fairness Case");
        scDBtn.setAlignmentX(LEFT_ALIGNMENT);
        scDBtn.setMaximumSize(new Dimension(270, 28));
        scDBtn.addActionListener(e -> loadScenarioD());
        panel.add(scDBtn);
        panel.add(Box.createVerticalStrut(4));

        // scenario E
        JButton scEBtn = new JButton("Scenario E - Validation Demo");
        scEBtn.setAlignmentX(LEFT_ALIGNMENT);
        scEBtn.setMaximumSize(new Dimension(270, 28));
        scEBtn.addActionListener(e -> loadScenarioE());
        panel.add(scEBtn);
        panel.add(Box.createVerticalStrut(6));

        return new JScrollPane(panel);
    }

    // builds the right side where results are shown
    JScrollPane buildOutputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Gantt Charts ---
        JLabel ganttTitle = new JLabel("Gantt Charts");
        ganttTitle.setFont(new Font("Arial", Font.BOLD, 14));
        ganttTitle.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(ganttTitle);
        panel.add(Box.createVerticalStrut(5));

        JPanel ganttBothPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        ganttBothPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 195));
        ganttBothPanel.setAlignmentX(LEFT_ALIGNMENT);

        ganttRR = new GanttPanel();
        JPanel rrBox = new JPanel(new BorderLayout());
        rrBox.setBorder(BorderFactory.createTitledBorder("Round Robin"));
        rrBox.add(new JScrollPane(ganttRR), BorderLayout.CENTER);
        ganttBothPanel.add(rrBox);

        ganttSRTF = new GanttPanel();
        JPanel srtfBox = new JPanel(new BorderLayout());
        srtfBox.setBorder(BorderFactory.createTitledBorder("SRTF"));
        srtfBox.add(new JScrollPane(ganttSRTF), BorderLayout.CENTER);
        ganttBothPanel.add(srtfBox);

        panel.add(ganttBothPanel);
        panel.add(Box.createVerticalStrut(10));

        // --- Ready Queue View ---
        JLabel queueTitle = new JLabel("Ready Queue Log (Round Robin)");
        queueTitle.setFont(new Font("Arial", Font.BOLD, 14));
        queueTitle.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(queueTitle);
        panel.add(Box.createVerticalStrut(4));

        queueArea = new JTextArea(4, 40);
        queueArea.setEditable(false);
        queueArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        queueArea.setText("Queue log will appear here...");
        JScrollPane queueScroll = new JScrollPane(queueArea);
        queueScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        queueScroll.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(queueScroll);
        panel.add(Box.createVerticalStrut(10));

        // --- Results Tables ---
        JLabel tablesTitle = new JLabel("Results Tables");
        tablesTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tablesTitle.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(tablesTitle);
        panel.add(Box.createVerticalStrut(5));

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        tablesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        tablesPanel.setAlignmentX(LEFT_ALIGNMENT);

        String[] cols = {"PID", "AT", "BT", "CT", "TAT", "WT", "RT"};

        rrModel = new DefaultTableModel(cols, 0);
        rrTable = new JTable(rrModel);
        rrTable.setEnabled(false);
        JPanel rrTableBox = new JPanel(new BorderLayout());
        rrTableBox.setBorder(BorderFactory.createTitledBorder("Round Robin Results"));
        rrTableBox.add(new JScrollPane(rrTable));
        tablesPanel.add(rrTableBox);

        srtfModel = new DefaultTableModel(cols, 0);
        srtfTable = new JTable(srtfModel);
        srtfTable.setEnabled(false);
        JPanel srtfTableBox = new JPanel(new BorderLayout());
        srtfTableBox.setBorder(BorderFactory.createTitledBorder("SRTF Results"));
        srtfTableBox.add(new JScrollPane(srtfTable));
        tablesPanel.add(srtfTableBox);

        panel.add(tablesPanel);
        panel.add(Box.createVerticalStrut(10));

        // --- Comparison Summary ---
        JLabel compTitle = new JLabel("Comparison Summary");
        compTitle.setFont(new Font("Arial", Font.BOLD, 14));
        compTitle.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(compTitle);
        panel.add(Box.createVerticalStrut(4));

        comparisonArea = new JTextArea(4, 40);
        comparisonArea.setEditable(false);
        comparisonArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        comparisonArea.setText("Comparison will appear here...");
        JScrollPane compScroll = new JScrollPane(comparisonArea);
        compScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        compScroll.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(compScroll);
        panel.add(Box.createVerticalStrut(10));

        // --- Final Conclusion ---
        JLabel conclusionTitle = new JLabel("Final Conclusion");
        conclusionTitle.setFont(new Font("Arial", Font.BOLD, 14));
        conclusionTitle.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(conclusionTitle);
        panel.add(Box.createVerticalStrut(4));

        conclusionArea = new JTextArea(5, 40);
        conclusionArea.setEditable(false);
        conclusionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        conclusionArea.setLineWrap(true);
        conclusionArea.setWrapStyleWord(true);
        conclusionArea.setText("Conclusion will appear here...");
        JScrollPane conclusionScroll = new JScrollPane(conclusionArea);
        conclusionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        conclusionScroll.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(conclusionScroll);
        panel.add(Box.createVerticalStrut(10));

        return new JScrollPane(panel);
    }

    void addRow(String pid, String at, String bt) {
        JTextField pidField = new JTextField(pid, 4);
        JTextField atField  = new JTextField(at, 4);
        JTextField btField  = new JTextField(bt, 4);

        JTextField[] fields = {pidField, atField, btField};

        JPanel row = new JPanel(new GridLayout(1, 4, 4, 0));
        row.setMaximumSize(new Dimension(270, 28));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(pidField);
        row.add(atField);
        row.add(btField);

        JButton removeBtn = new JButton("X");
        removeBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        removeBtn.addActionListener(e -> {
            processRowsPanel.remove(row);
            rows.remove(fields);
            processRowsPanel.revalidate();
            processRowsPanel.repaint();
        });
        row.add(removeBtn);

        rows.add(fields);
        processRowsPanel.add(row);
        processRowsPanel.revalidate();
        processRowsPanel.repaint();
    }

    // clears only the process rows and quantum, does not touch the output side
    void resetInput() {
        processRowsPanel.removeAll();
        rows.clear();
        processRowsPanel.revalidate();
        processRowsPanel.repaint();
        quantumField.setText("3");
        addRow("", "", "");
        addRow("", "", "");
        statusBar.setText("  Input reset. Enter new processes.");
    }

    // loads scenario data into the input panel
    void loadScenario(String[][] data, String quantum) {
        processRowsPanel.removeAll();
        rows.clear();
        processRowsPanel.revalidate();
        processRowsPanel.repaint();
        quantumField.setText(quantum);
        for (String[] d : data) {
            addRow(d[0], d[1], d[2]);
        }
        statusBar.setText("  Scenario loaded. Click Run Simulation.");
    }

    void loadScenarioA() {
        loadScenario(new String[][]{
            {"P1", "0", "8"},
            {"P2", "1", "4"},
            {"P3", "2", "9"},
            {"P4", "3", "5"},
            {"P5", "4", "2"}
        }, "3");
    }

    void loadScenarioB() {
        loadScenario(new String[][]{
            {"P1", "0", "10"},
            {"P2", "0", "5"},
            {"P3", "0", "8"},
            {"P4", "0", "3"}
        }, "2");
        statusBar.setText("  Scenario B loaded (Q=2). Run, then change quantum to 8 and run again.");
    }

    void loadScenarioC() {
        loadScenario(new String[][]{
            {"P1", "0", "2"},
            {"P2", "0", "1"},
            {"P3", "1", "3"},
            {"P4", "2", "1"},
            {"P5", "2", "2"},
            {"P6", "3", "1"}
        }, "2");
    }

    void loadScenarioD() {
        loadScenario(new String[][]{
            {"P1", "0", "10"},
            {"P2", "0", "8"},
            {"P3", "0", "6"},
            {"P4", "0", "4"}
        }, "2");
    }

    void loadScenarioE() {
        // loads intentionally bad data to demonstrate validation
        processRowsPanel.removeAll();
        rows.clear();
        processRowsPanel.revalidate();
        processRowsPanel.repaint();
        quantumField.setText("0");   // invalid quantum
        addRow("P1", "-1", "5");     // invalid arrival time
        addRow("P1", "0", "0");      // duplicate pid + invalid burst
        statusBar.setText("  Scenario E loaded. Click Run to see validation errors.");
    }

    void runSimulation() {
        // validate quantum first
        int quantum;
        try {
            quantum = Integer.parseInt(quantumField.getText().trim());
            if (quantum < 1) {
                JOptionPane.showMessageDialog(this, "Time Quantum must be at least 1.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Time Quantum must be a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // need at least one process
        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one process.", "No Processes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // validate and read all process rows
        List<Process> processList = new ArrayList<>();
        List<String> usedPids = new ArrayList<>();

        for (JTextField[] row : rows) {
            String pid = row[0].getText().trim();
            String atText = row[1].getText().trim();
            String btText = row[2].getText().trim();

            if (pid.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Process ID cannot be empty.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (usedPids.contains(pid)) {
                JOptionPane.showMessageDialog(this, "Duplicate process ID: " + pid, "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            usedPids.add(pid);

            int at;
            try {
                at = Integer.parseInt(atText);
                if (at < 0) {
                    JOptionPane.showMessageDialog(this, "Arrival time for " + pid + " cannot be negative.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Arrival time for " + pid + " is not a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int bt;
            try {
                bt = Integer.parseInt(btText);
                if (bt < 1) {
                    JOptionPane.showMessageDialog(this, "Burst time for " + pid + " must be at least 1.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Burst time for " + pid + " is not a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            processList.add(new Process(pid, at, bt));
        }

        // assign a color to each process
        List<String> pidColorList = new ArrayList<>();
        List<Color> colorAssigned = new ArrayList<>();
        for (int i = 0; i < processList.size(); i++) {
            pidColorList.add(processList.get(i).pid);
            colorAssigned.add(colors[i % colors.length]);
        }

        // run both algorithms using ScenarioRunner
        ScenarioRunner runner = new ScenarioRunner();
        runner.run(processList, quantum);

        // update gantt charts
        ganttRR.setData(runner.rrResult.gantt, pidColorList, colorAssigned);
        ganttSRTF.setData(runner.srtfResult.gantt, pidColorList, colorAssigned);

        // update queue log
        StringBuilder qLog = new StringBuilder();
        for (String line : runner.rrResult.queueLog) {
            qLog.append(line).append("\n");
        }
        queueArea.setText(qLog.toString());
        queueArea.setCaretPosition(0);

        // fill RR table
        rrModel.setRowCount(0);
        for (Process p : runner.rrProcs) {
            rrModel.addRow(new Object[]{
                p.pid, p.arrivalTime, p.burstTime,
                p.completionTime, p.turnaroundTime, p.waitingTime, p.responseTime
            });
        }
        rrModel.addRow(new Object[]{
            "Avg", "-", "-", "-",
            String.format("%.2f", runner.rrResult.avgTAT),
            String.format("%.2f", runner.rrResult.avgWT),
            String.format("%.2f", runner.rrResult.avgRT)
        });

        // fill SRTF table
        srtfModel.setRowCount(0);
        for (Process p : runner.srtfProcs) {
            srtfModel.addRow(new Object[]{
                p.pid, p.arrivalTime, p.burstTime,
                p.completionTime, p.turnaroundTime, p.waitingTime, p.responseTime
            });
        }
        srtfModel.addRow(new Object[]{
            "Avg", "-", "-", "-",
            String.format("%.2f", runner.srtfResult.avgTAT),
            String.format("%.2f", runner.srtfResult.avgWT),
            String.format("%.2f", runner.srtfResult.avgRT)
        });

        // comparison summary
        double rrWT  = runner.rrResult.avgWT;
        double rrTAT = runner.rrResult.avgTAT;
        double rrRT  = runner.rrResult.avgRT;
        double srWT  = runner.srtfResult.avgWT;
        double srTAT = runner.srtfResult.avgTAT;
        double srRT  = runner.srtfResult.avgRT;

        String wtWinner  = srWT  < rrWT  ? "SRTF"         : (rrWT  < srWT  ? "Round Robin" : "Tie");
        String tatWinner = srTAT < rrTAT ? "SRTF"         : (rrTAT < srTAT ? "Round Robin" : "Tie");
        String rtWinner  = rrRT  < srRT  ? "Round Robin"  : (srRT  < rrRT  ? "SRTF"        : "Tie");

        comparisonArea.setText(
            String.format("%-20s %-14s %-14s %s%n", "Metric", "Round Robin", "SRTF", "Winner") +
            "-------------------------------------------------------\n" +
            String.format("%-20s %-14s %-14s %s%n", "Avg Waiting Time",
                String.format("%.2f", rrWT), String.format("%.2f", srWT), wtWinner) +
            String.format("%-20s %-14s %-14s %s%n", "Avg Turnaround",
                String.format("%.2f", rrTAT), String.format("%.2f", srTAT), tatWinner) +
            String.format("%-20s %-14s %-14s %s%n", "Avg Response Time",
                String.format("%.2f", rrRT), String.format("%.2f", srRT), rtWinner)
        );

        // conclusion
        StringBuilder conc = new StringBuilder();
        conc.append("Summary (Quantum = ").append(quantum).append(")\n\n");

        conc.append("Waiting Time: ").append(wtWinner).append(" was better");
        conc.append(String.format(" (RR = %.2f, SRTF = %.2f).\n", rrWT, srWT));

        conc.append("Turnaround Time: ").append(tatWinner).append(" was better");
        conc.append(String.format(" (RR = %.2f, SRTF = %.2f).\n", rrTAT, srTAT));

        conc.append("Response Time: ").append(rtWinner).append(" gave faster first response");
        conc.append(String.format(" (RR = %.2f, SRTF = %.2f).\n", rrRT, srRT));

        conc.append("Fairness: Round Robin is fairer because every process gets equal CPU time slices.\n");
        conc.append("Short Jobs: SRTF is better for short jobs because it always runs the shortest burst first.\n");

        if (quantum <= 2) {
            conc.append("Quantum (" + quantum + "): Very small quantum, so RR has many context switches but quick response times.\n");
        } else if (quantum >= 7) {
            conc.append("Quantum (" + quantum + "): Large quantum, so RR acts more like FCFS with fewer context switches.\n");
        } else {
            conc.append("Quantum (" + quantum + "): Moderate quantum gives a balance between fairness and performance.\n");
        }

        if (srWT < rrWT) {
            conc.append("Recommendation: Use SRTF to minimize waiting time. Use Round Robin when fairness is more important.");
        } else {
            conc.append("Recommendation: Round Robin performed well here. SRTF mainly helps when there are lots of short jobs.");
        }

        conclusionArea.setText(conc.toString());
        conclusionArea.setCaretPosition(0);

        statusBar.setText("  Done. Quantum = " + quantum + ", Processes = " + processList.size());
    }

    void clearAll() {
        processRowsPanel.removeAll();
        rows.clear();
        processRowsPanel.revalidate();
        processRowsPanel.repaint();

        ganttRR.clear();
        ganttSRTF.clear();
        rrModel.setRowCount(0);
        srtfModel.setRowCount(0);
        queueArea.setText("Queue log will appear here...");
        comparisonArea.setText("Comparison will appear here...");
        conclusionArea.setText("Conclusion will appear here...");
        quantumField.setText("3");
        statusBar.setText("  Cleared.");

        addRow("", "", "");
        addRow("", "", "");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
