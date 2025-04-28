import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class BettingStatsPanel extends JPanel {
    private User user;
    private JLabel totalBetsLabel;
    private JLabel wonBetsLabel;
    private JLabel winRateLabel;
    private JLabel balanceLabel;

    public BettingStatsPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        UITheme.stylePanel(this);

        // Create header
        JLabel headerLabel = new JLabel("Betting Statistics");
        UITheme.styleTitle(headerLabel);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Create stats panel
        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        statsPanel.setOpaque(false);

        // Initialize statistics labels
        totalBetsLabel = createStatsLabel("Total Bets: " + user.getTotalBets());
        wonBetsLabel = createStatsLabel("Won Bets: " + user.getWonBets());
        winRateLabel = createStatsLabel(String.format("Win Rate: %.1f%%", user.getWinRate() * 100));
        balanceLabel = createStatsLabel(String.format("Current Balance: $%.2f", user.getBalance()));

        // Add labels to stats panel
        statsPanel.add(totalBetsLabel);
        statsPanel.add(wonBetsLabel);
        statsPanel.add(winRateLabel);
        statsPanel.add(balanceLabel);

        // Add components to main panel
        add(headerLabel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
    }

    private JLabel createStatsLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.HEADER_FONT);
        label.setForeground(UITheme.TEXT_PRIMARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public void updateStats() {
        totalBetsLabel.setText("Total Bets: " + user.getTotalBets());
        wonBetsLabel.setText("Won Bets: " + user.getWonBets());
        winRateLabel.setText(String.format("Win Rate: %.1f%%", user.getWinRate() * 100));
        balanceLabel.setText(String.format("Current Balance: $%.2f", user.getBalance()));
    }
}