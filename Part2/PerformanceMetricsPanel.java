import javax.swing.*;
import java.awt.*;

public class PerformanceMetricsPanel extends JPanel {
    private JLabel speedLabel;
    private JLabel timeLabel;
    private JLabel winRatioLabel;
    private JLabel confidenceChangeLabel;
    
    public PerformanceMetricsPanel() {
        setLayout(new GridLayout(4, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Performance Metrics"));
        setBackground(UITheme.BACKGROUND_COLOR);
        
        // Initialize labels with modern styling
        speedLabel = createStyledLabel("Average Speed: -- m/s");
        timeLabel = createStyledLabel("Finishing Time: -- seconds");
        winRatioLabel = createStyledLabel("Win Ratio: --%");
        confidenceChangeLabel = createStyledLabel("Confidence Change: --");
        
        // Add components
        add(speedLabel);
        add(timeLabel);
        add(winRatioLabel);
        add(confidenceChangeLabel);
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.LABEL_FONT);
        label.setForeground(UITheme.TEXT_PRIMARY);
        return label;
    }
    
    public void updateMetrics(Horse horse, RaceStatistics stats) {
        if (stats != null) {
            speedLabel.setText(String.format("Average Speed: %.2f m/s", stats.getAverageSpeed()));
            timeLabel.setText("Finishing Time: " + stats.getFormattedFinishTime());
        }
        
        if (horse != null) {
            int totalRaces = horse.getRacesAttended();
            int wins = horse.getRacesWon();
            double winRatio = totalRaces > 0 ? (wins * 100.0 / totalRaces) : 0;
            winRatioLabel.setText(String.format("Win Ratio: %.1f%% (%d/%d)", winRatio, wins, totalRaces));
            
            double confidenceChange = stats != null ? stats.getConfidenceChange() : 0;
            String changeSymbol = confidenceChange >= 0 ? "+" : "";
            confidenceChangeLabel.setText(String.format("Confidence Change: %s%.2f", changeSymbol, confidenceChange));
        }
    }
    
    public void reset() {
        speedLabel.setText("Average Speed: -- m/s");
        timeLabel.setText("Finishing Time: -- seconds");
        winRatioLabel.setText("Win Ratio: --%");
        confidenceChangeLabel.setText("Confidence Change: --");
    }
}