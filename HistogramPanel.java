import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HistogramPanel extends JPanel {
    private int histogramHeight = 200;
    private int barGap = 10;
    private JPanel barPanel;
    private JPanel labelPanel;
    private List<Bar> bars = new ArrayList<>();
    private int barWidth;  // Dynamic bar width based on number of bars

    public HistogramPanel() {
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());

        barPanel = new JPanel(new GridLayout(1, 0, barGap, 0));
        barPanel.setPreferredSize(new Dimension(1000, histogramHeight)); // Set a large width for horizontal scrolling
        JScrollPane scrollPane = new JScrollPane(barPanel);
        add(scrollPane, BorderLayout.CENTER);

        labelPanel = new JPanel(new GridLayout(1, 0, barGap, 0));
        labelPanel.setBorder(new EmptyBorder(5, 10, 0, 10));
        add(labelPanel, BorderLayout.PAGE_END);
    }

    public void addHistogramColumn(String label, int value, Color color) {
        if (value < 0) {
            value = 0;
        }
        Bar bar = new Bar(label, value, color);
        bars.add(bar);
    }

    public void layoutHistogram() {
        barPanel.removeAll();
        labelPanel.removeAll();
    
        int maxValue = 0;
    
        // Find the maximum value in the bars list for scaling
        for (Bar bar : bars) {
            maxValue = Math.max(maxValue, bar.getValue());
        }
    
        // If maxValue is 0, we can't scale the bars, so set a default value to avoid division by zero
        if (maxValue == 0) {
            maxValue = 1;  // Set to 1 or any default value to avoid division by zero
        }
    
        // Dynamically adjust the bar width based on the number of bars
        barWidth = Math.max(50, 1000 / bars.size()); // Ensure bars are wide enough
    
        for (Bar bar : bars) {
            // Create label for the bar value
            JLabel label = new JLabel(String.valueOf(bar.getValue()));
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.TOP);
            label.setVerticalAlignment(JLabel.BOTTOM);
            label.setToolTipText(bar.getLabel());  // Tooltip for long labels
    
            // Calculate bar height and scale according to max value
            int barHeight = (bar.getValue() * histogramHeight) / maxValue;
            if (barHeight > histogramHeight) {
                barHeight = histogramHeight;
            }
    
            // Add a small margin between the bar and the label if necessary
            int labelMargin = 10;  // Adjust this value based on your needs
            Icon icon = new ColorIcon(bar.getColor(), barWidth, barHeight - labelMargin); // Reduce height slightly for label margin
            label.setIcon(icon);
    
            // Add the bar icon and label to the panels
            barPanel.add(label);
            JLabel barLabel = new JLabel(bar.getLabel());
            barLabel.setHorizontalAlignment(JLabel.CENTER);
            barLabel.setFont(barLabel.getFont().deriveFont(10f));  // Reduce font size for many bars
            labelPanel.add(barLabel);
        }
    
        // Revalidate and repaint the panel after updating bars
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw gridlines on the panel
        drawGridlines(g);
    }

    private void drawGridlines(Graphics g) {
        // Set color for gridlines
        g.setColor(Color.LIGHT_GRAY);

        // Draw horizontal gridlines
        int numHorizontalLines = 5;
        int step = histogramHeight / numHorizontalLines;
        for (int i = 1; i <= numHorizontalLines; i++) {
            g.drawLine(0, i * step, getWidth(), i * step); // Horizontal lines
        }

        // Draw vertical gridlines (every 100 pixels or according to bar width)
        for (int i = barWidth; i < getWidth(); i += barWidth + barGap) {
            g.drawLine(i, 0, i, histogramHeight); // Vertical lines
        }
    }
    
    private class Bar {
        private String label;
        private int value;
        private Color color;

        public Bar(String label, int value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }
    }

    private class ColorIcon implements Icon {
        private int shadow = 3;
        private Color color;
        private int width;
        private int height;

        public ColorIcon(Color color, int width, int height) {
            this.color = color;
            this.width = width;
            this.height = height;
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, width - shadow, height);
            g.setColor(Color.GRAY);
            g.fillRect(x + width - shadow, y + shadow, shadow, height - shadow);
        }
    }
}
