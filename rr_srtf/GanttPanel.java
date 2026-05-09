import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GanttPanel extends JPanel {

    private List<GanttEntry> ganttData = new ArrayList<>();
    private List<String> pidList = new ArrayList<>();
    private List<Color> colorList = new ArrayList<>();

    public GanttPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 75));
    }

    public void setData(List<GanttEntry> gantt, List<String> pids, List<Color> colors) {
        this.ganttData = gantt;
        this.pidList = pids;
        this.colorList = colors;
        repaint();
    }

    public void clear() {
        ganttData.clear();
        pidList.clear();
        colorList.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (ganttData == null || ganttData.isEmpty()) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString("Run simulation to see chart", 10, 35);
            return;
        }

        int totalTime = ganttData.get(ganttData.size() - 1).end;
        if (totalTime == 0) totalTime = 1;

        int w = getWidth() - 20;
        int blockH = 38;
        int y = 5;
        int startX = 10;

        for (GanttEntry entry : ganttData) {
            int bx = startX + (int)((double) entry.start / totalTime * w);
            int bw = (int)((double)(entry.end - entry.start) / totalTime * w);
            if (bw < 2) bw = 2;

            // pick color
            Color c = new Color(200, 200, 200); // idle color
            if (!entry.pid.equals("IDLE")) {
                int idx = pidList.indexOf(entry.pid);
                if (idx != -1) {
                    c = colorList.get(idx);
                }
            }

            g.setColor(c);
            g.fillRect(bx, y, bw, blockH);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(bx, y, bw, blockH);

            // write pid inside block if enough space
            if (bw > 16) {
                g.setFont(new Font("Arial", Font.BOLD, 11));
                FontMetrics fm = g.getFontMetrics();
                int tw = fm.stringWidth(entry.pid);
                if (tw + 4 <= bw) {
                    g.setColor(entry.pid.equals("IDLE") ? Color.GRAY : Color.WHITE);
                    g.drawString(entry.pid, bx + (bw - tw) / 2, y + blockH / 2 + 5);
                }
            }
        }

        // draw time numbers below
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(Color.DARK_GRAY);

        for (GanttEntry entry : ganttData) {
            int tx = startX + (int)((double) entry.start / totalTime * w);
            g.drawLine(tx, y + blockH, tx, y + blockH + 4);
            g.drawString(String.valueOf(entry.start), tx - 3, y + blockH + 14);
        }

        // last tick
        int lastTx = startX + w;
        g.drawLine(lastTx, y + blockH, lastTx, y + blockH + 4);
        g.drawString(String.valueOf(totalTime), lastTx - 3, y + blockH + 14);
    }
}
