import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RacePanel extends JPanel {
    private Track track;
    private List<Horse> horses;
    private List<Integer> horsePositions;
    private boolean raceInProgress;
    private ScheduledExecutorService raceExecutor;
    private JButton startButton;
    private JLabel statusLabel;
    private JPanel raceTrackPanel;
    private Map<Horse, PerformanceMetricsPanel> metricsMap;
    private Map<Horse, RaceStatistics> statisticsMap;
    private static final int TRACK_HEIGHT = 400;
    private static final int LANE_HEIGHT = 90;
    private static final int HORSE_SIZE = 48;

    public RacePanel(Track track, List<Horse> selectedHorses) {
        this.track = track;
        this.horses = new ArrayList<>(selectedHorses);
        this.horsePositions = new ArrayList<>();
        this.metricsMap = new HashMap<>();
        this.statisticsMap = new HashMap<>();
        for (int i = 0; i < horses.size(); i++) {
            horsePositions.add(0);
            Horse horse = horses.get(i);
            metricsMap.put(horse, new PerformanceMetricsPanel());
            statisticsMap.put(horse, new RaceStatistics(track.getLength()));
        }

        setLayout(new BorderLayout(20, 20));
        UITheme.stylePanel(this);

        // Add title
        JLabel titleLabel = new JLabel("Race Track");
        UITheme.styleHeader(titleLabel);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Create race track visualization panel with modern styling
        raceTrackPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRaceTrack(g);
            }
        };
        raceTrackPanel.setPreferredSize(new Dimension(800, TRACK_HEIGHT));
        raceTrackPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // Create control panel with modern styling
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(UITheme.BACKGROUND_COLOR);
        
        startButton = new JButton("🏁 Start Race");
        UITheme.styleButton(startButton);
        startButton.addActionListener(e -> toggleRace());
        
        // Add status label with modern styling
        statusLabel = new JLabel("Ready to race!");
        UITheme.styleLabel(statusLabel);
        
        // Add weather info with modern styling
        JLabel weatherLabel = new JLabel("🌤 Weather: " + track.getWeather().toString());
        UITheme.styleLabel(weatherLabel);
        
        controlPanel.add(startButton);
        controlPanel.add(statusLabel);
        controlPanel.add(weatherLabel);

        add(raceTrackPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void drawRaceTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int trackWidth = getWidth() - 40;
        
        // Draw track lanes
        for (int i = 0; i < horses.size(); i++) {
            int laneY = 20 + (i * LANE_HEIGHT);
            
            // Draw lane background with gradient
            GradientPaint gradient = new GradientPaint(
                20, laneY, new Color(240, 240, 240),
                20, laneY + LANE_HEIGHT - 10, new Color(230, 230, 230));
            g2d.setPaint(gradient);
            g2d.fillRoundRect(20, laneY, trackWidth, LANE_HEIGHT - 10, 15, 15);
            
            // Draw lane borders
            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(20, laneY, trackWidth, LANE_HEIGHT - 10, 15, 15);

            // Draw horse
            int horseX = 20 + (int)((trackWidth - HORSE_SIZE) * (horsePositions.get(i) / (double)track.getLength()));
            Horse horse = horses.get(i);
            
            // Draw horse symbol with shadow effect
            g2d.setFont(new Font("Segoe UI", Font.BOLD, HORSE_SIZE));
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.drawString(String.valueOf(horse.getSymbol()), horseX + 2, laneY + LANE_HEIGHT - 18);
            g2d.setColor(UITheme.PRIMARY_COLOR);
            g2d.drawString(String.valueOf(horse.getSymbol()), horseX, laneY + LANE_HEIGHT - 20);
            
            // Draw horse name and confidence with modern styling
            g2d.setFont(UITheme.LABEL_FONT);
            g2d.setColor(UITheme.TEXT_PRIMARY);
            String stats = String.format("%s (Confidence: %.2f)", horse.getName(), horse.getConfidence());
            g2d.drawString(stats, 25, laneY + 25);
            
            // Add metrics panel for each horse
            PerformanceMetricsPanel metricsPanel = metricsMap.get(horse);
            metricsPanel.setBounds(trackWidth + 30, laneY, 200, LANE_HEIGHT - 10);
            raceTrackPanel.add(metricsPanel);
            metricsPanel.updateMetrics(horse, statisticsMap.get(horse));
        }
    }

    private void toggleRace() {
        if (!raceInProgress) {
            startRace();
        } else {
            stopRace();
        }
    }

    private void startRace() {
        raceInProgress = true;
        startButton.setText("Stop Race");
        statusLabel.setText("Race in progress...");

        // Reset positions
        for (int i = 0; i < horses.size(); i++) {
            horsePositions.set(i, 0);
            Horse horse = horses.get(i);
            horse.goBackToStart();
            statisticsMap.put(horse, new RaceStatistics(track.getLength()));
            metricsMap.get(horse).reset();
        }

        raceExecutor = Executors.newSingleThreadScheduledExecutor();
        raceExecutor.scheduleAtFixedRate(() -> {
            updateRace();
            SwingUtilities.invokeLater(() -> repaint());
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void stopRace() {
        if (raceExecutor != null) {
            raceExecutor.shutdown();
            raceInProgress = false;
            startButton.setText("Start Race");
            statusLabel.setText("Race stopped");
        }
    }

    private void updateRace() {
        boolean raceFinished = false;
        Horse winner = null;

        for (int i = 0; i < horses.size(); i++) {
            Horse horse = horses.get(i);
            if (!horse.hasFallen()) {
                // Update horse position based on confidence and track conditions
                if (Math.random() < horse.getConfidence() * track.getSpeedModifier()) {
                    int newPosition = horsePositions.get(i) + 1;
                    horsePositions.set(i, newPosition);

                    // Check for win
                    if (newPosition >= track.getLength()) {
                        winner = horse;
                        raceFinished = true;
                        break;
                    }
                }

                // Check for falls
                if (track.checkFall(horse)) {
                    horse.fall();
                    horse.setSymbol('X');
                    horse.setConfidence(horse.getConfidence() - 0.1);
                    statusLabel.setText(horse.getName() + " has fallen!");
                    // Save updated confidence after fall
                    HorseManager.saveHorses(horses);
                }
            }
        }

        // Check race end conditions
        if (raceFinished && winner != null) {
            final Horse finalWinner = winner; // Make winner effectively final
            SwingUtilities.invokeLater(() -> {
                stopRace();
                // Update winner's confidence and statistics
                double confidenceChange = 0.1;
                finalWinner.setConfidence(finalWinner.getConfidence() + confidenceChange);
                finalWinner.recordRaceResult(true);
                
                // Update race statistics for winner
                RaceStatistics winnerStats = statisticsMap.get(finalWinner);
                winnerStats.recordFinish(System.currentTimeMillis(), true, confidenceChange);
                metricsMap.get(finalWinner).updateMetrics(finalWinner, winnerStats);
                
                // Record race participation and update confidence for all horses
                for (Horse horse : horses) {
                    if (horse != finalWinner) {
                        horse.recordRaceResult(false);
                    }
                }
                
                // Load all existing horses
                List<Horse> allHorses = HorseManager.loadHorses();
                
                // Update statistics for participating horses
                for (Horse raceHorse : horses) {
                    // Find the matching horse in allHorses by name
                    for (int i = 0; i < allHorses.size(); i++) {
                        if (allHorses.get(i).getName().equals(raceHorse.getName())) {
                            // Update the horse's statistics
                            Horse savedHorse = allHorses.get(i);
                            savedHorse.setConfidence(raceHorse.getConfidence());
                            if (raceHorse == finalWinner) {
                                savedHorse.recordRaceResult(true);
                            } else {
                                savedHorse.recordRaceResult(false);
                            }
                            break;
                        }
                    }
                }
                
                // Save all horses back to maintain the complete list
                HorseManager.saveHorses(allHorses);
                
                // Update the display with race results
                statusLabel.setText(finalWinner.getName() + " wins the race!");
                JOptionPane.showMessageDialog(this,
                    finalWinner.getName() + " has won the race!\n" +
                    "New confidence level: " + String.format("%.2f", finalWinner.getConfidence()) + "\n" +
                    "Races won: " + finalWinner.getRacesWon() + " of " + finalWinner.getRacesAttended(),
                    "Race Finished",
                    JOptionPane.INFORMATION_MESSAGE);
            });
        } else if (allHorsesFallen()) {
            SwingUtilities.invokeLater(() -> {
                stopRace();
                statusLabel.setText("Race ended - All horses have fallen!");
                JOptionPane.showMessageDialog(this,
                    "Race ended - All horses have fallen!",
                    "Race Finished",
                    JOptionPane.WARNING_MESSAGE);
            });
        }
    }

    private boolean allHorsesFallen() {
        for (Horse horse : horses) {
            if (!horse.hasFallen()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (raceExecutor != null) {
            raceExecutor.shutdown();
        }
    }
}