import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JButton barnButton;
    private JButton trackButton;
    private JButton raceButton;
    private JButton statsButton;
    private JLabel balanceLabel;
    private User currentUser;
    
    public MainWindow() {
        // Show login dialog
        String username = JOptionPane.showInputDialog(this, "Enter your username:", "Login", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            System.exit(0);
        }
        currentUser = UserManager.loginUser(username.trim());
        
        setTitle("Horse Racing Game - User: " + currentUser.getUsername() + " (Balance: $" + currentUser.getBalance() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Set the window background color
        getContentPane().setBackground(UITheme.BACKGROUND_COLOR);
        
        // Initialize main panel with modern styling
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        UITheme.stylePanel(mainPanel);
        
        // Create title label with modern styling
        JLabel titleLabel = new JLabel("Welcome to Horse Racing");
        UITheme.styleTitle(titleLabel);
        titleLabel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        // Create button panel with modern styling
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 20, 20));
        buttonPanel.setBorder(new EmptyBorder(20, 200, 40, 200));
        buttonPanel.setOpaque(false);
        
        // Create balance label with modern styling
        balanceLabel = new JLabel(String.format("Balance: $%.2f", currentUser.getBalance()));
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        UITheme.styleLabel(balanceLabel);
        balanceLabel.setBorder(new EmptyBorder(10, 0, 0, 20));
        mainPanel.add(balanceLabel, BorderLayout.NORTH);

        // Initialize buttons with modern styling
        barnButton = new JButton("🐎  Visit Barn");
        trackButton = new JButton("🏁  Track Selection");
        raceButton = new JButton("⚡  Start Race");
        statsButton = new JButton("📊  Betting Stats");
        
        // Apply modern styling to buttons
        UITheme.styleButton(barnButton);
        UITheme.styleButton(trackButton);
        UITheme.styleButton(raceButton);
        UITheme.styleButton(statsButton);
        
        // Add action listeners
        barnButton.addActionListener(e -> visitBarn());
        trackButton.addActionListener(e -> selectTrack());
        raceButton.addActionListener(e -> startRaceGUI());
        statsButton.addActionListener(e -> showBettingStats());
        
        // Add buttons to panel
        buttonPanel.add(barnButton);
        buttonPanel.add(trackButton);
        buttonPanel.add(raceButton);
        buttonPanel.add(statsButton);
        
        // Add components to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
    }
    

    
    private java.util.List<Horse> loadHorses() {
        java.util.List<Horse> horses = HorseManager.loadHorses();
        
        // Add initial sample horses if list is empty
        if (horses.isEmpty()) {
            Horse horse1 = new Horse('♞', "Thunder", "Arabian", "Bay");
            horse1.addEquipment(new HorseEquipment("Racing Saddle", "Increases speed by 10%", 0.1));
            horses.add(horse1);
            
            Horse horse2 = new Horse('♘', "Storm", "Thoroughbred", "Black");
            horse2.addEquipment(new HorseEquipment("Lightweight Shoes", "Improves acceleration", 0.08));
            horses.add(horse2);
            
            Horse horse3 = new Horse('♞', "Spirit", "Mustang", "Palomino");
            horse3.addEquipment(new HorseEquipment("Endurance Gear", "Boosts stamina", 0.12));
            horses.add(horse3);
            
            // Save initial horses
            HorseManager.saveHorses(horses);
        }
        
        return horses;
    }
    
    private void visitBarn() {
        // Load horses from storage
        java.util.List<Horse> horses = loadHorses();
        
        // Create and show the barn panel in a new window
        JFrame barnFrame = new JFrame("Horse Barn");
        barnFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        barnFrame.setSize(600, 500);
        barnFrame.setLocationRelativeTo(this);
        
        HorseBarnPanel barnPanel = new HorseBarnPanel(horses);
        barnFrame.add(barnPanel);
        
        barnFrame.setVisible(true);
    }
    
    private void selectTrack() {
        // Load tracks from storage
        java.util.List<Track> tracks = TrackManager.loadTracks();
        
        // Create and show the track selection panel in a new window
        JFrame trackFrame = new JFrame("Track Selection");
        trackFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        trackFrame.setSize(600, 500);
        trackFrame.setLocationRelativeTo(this);
        
        TrackBarnPanel trackPanel = new TrackBarnPanel(tracks);
        trackFrame.add(trackPanel);
        
        trackFrame.setVisible(true);
    }
    
    private void updateBalance() {
        balanceLabel.setText(String.format("Balance: $%.2f", currentUser.getBalance()));
        setTitle("Horse Racing Game - User: " + currentUser.getUsername() + " (Balance: $" + currentUser.getBalance() + ")");
    }

    private void showBettingStats() {
        JFrame statsFrame = new JFrame("Betting Statistics");
        statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statsFrame.setSize(400, 300);
        statsFrame.setLocationRelativeTo(this);
        
        BettingStatsPanel statsPanel = new BettingStatsPanel(currentUser);
        statsFrame.add(statsPanel);
        
        statsFrame.setVisible(true);
    }

    private void startRaceGUI() {
        // Load available horses and tracks
        java.util.List<Horse> availableHorses = loadHorses();
        java.util.List<Track> availableTracks = TrackManager.loadTracks();
        
        if (availableHorses.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please add some horses in the barn first!",
                "No Horses Available",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (availableTracks.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please create a track first!",
                "No Tracks Available",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create race setup dialog
        JDialog setupDialog = new JDialog(this, "Race Setup", true);
        setupDialog.setLayout(new BorderLayout(10, 10));
        setupDialog.setSize(400, 400);
        setupDialog.setLocationRelativeTo(this);

        // Create selection panels
        JPanel selectionPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Track selection
        selectionPanel.add(new JLabel("Select Track:"));
        JComboBox<Track> trackBox = new JComboBox<>(availableTracks.toArray(new Track[0]));
        trackBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Track) {
                    setText(((Track) value).getDisplayName());
                }
                return this;
            }
        });
        selectionPanel.add(trackBox);

        // Horse selection
        JLabel horseSelectionLabel = new JLabel();
        selectionPanel.add(horseSelectionLabel);
        java.util.List<JCheckBox> horseCheckboxes = new java.util.ArrayList<>();
        for (Horse horse : availableHorses) {
            JCheckBox checkbox = new JCheckBox(horse.getName());
            horseCheckboxes.add(checkbox);
            selectionPanel.add(checkbox);
        }

        // Update horse selection label based on selected track
        Track initialTrack = (Track) trackBox.getSelectedItem();
        horseSelectionLabel.setText(String.format("Select Horses (up to %d horses):", initialTrack.getLaneCount()));

        // Add track selection listener to update requirements
        trackBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Track selectedTrack = (Track) trackBox.getSelectedItem();
                horseSelectionLabel.setText(String.format("Select Horses (up to %d horses):", selectedTrack.getLaneCount()));
            }
        });

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton nextButton = new JButton("Next");
        JButton cancelButton = new JButton("Cancel");

        nextButton.addActionListener(e -> {
            // Get selected track
            Track selectedTrack = (Track) trackBox.getSelectedItem();

            // Get selected horses
            java.util.List<Horse> selectedHorses = new java.util.ArrayList<>();
            for (int i = 0; i < horseCheckboxes.size(); i++) {
                if (horseCheckboxes.get(i).isSelected()) {
                    selectedHorses.add(availableHorses.get(i));
                }
            }

            // Validate selection
            if (selectedHorses.isEmpty()) {
                JOptionPane.showMessageDialog(setupDialog,
                    "Please select at least one horse for the race!",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (selectedHorses.size() > selectedTrack.getLaneCount()) {
                JOptionPane.showMessageDialog(setupDialog,
                    String.format("This track only has %d lanes. Please select fewer horses!", selectedTrack.getLaneCount()),
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (selectedHorses.size() < 2) {
                JOptionPane.showMessageDialog(setupDialog,
                    "Please select at least 2 horses for the race!",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Close setup dialog
            setupDialog.dispose();

            // Show betting dialog
            JDialog bettingDialog = new JDialog(this, "Place Your Bet", true);
            bettingDialog.setLayout(new BorderLayout(10, 10));
            bettingDialog.setSize(350, 250);
            bettingDialog.setLocationRelativeTo(this);

            JPanel bettingPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            bettingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            UITheme.stylePanel(bettingPanel);

            JLabel balanceLabel = new JLabel("Current Balance: $" + currentUser.getBalance());
            UITheme.styleLabel(balanceLabel);
            
            JLabel selectHorseLabel = new JLabel("Select Horse to Bet on:");
            UITheme.styleLabel(selectHorseLabel);
            
            JComboBox<Horse> betHorseBox = new JComboBox<>(selectedHorses.toArray(new Horse[0]));
            betHorseBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Horse) {
                        setText(((Horse) value).getName());
                    }
                    return this;
                }
            });
            
            JLabel betAmountLabel = new JLabel("Bet Amount:");
            UITheme.styleLabel(betAmountLabel);
            
            JSpinner betAmount = new JSpinner(new SpinnerNumberModel(10, 1, currentUser.getBalance(), 5));

            bettingPanel.add(balanceLabel);
            bettingPanel.add(selectHorseLabel);
            bettingPanel.add(betHorseBox);
            bettingPanel.add(betAmountLabel);
            bettingPanel.add(betAmount);

            JPanel betButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton startRaceButton = new JButton("Start Race");
            JButton cancelBetButton = new JButton("Cancel");
            UITheme.styleButton(startRaceButton);
            UITheme.styleButton(cancelBetButton);

            startRaceButton.addActionListener(ev -> {
                bettingDialog.dispose();

                // Create and show race window
                JFrame raceFrame = new JFrame("Horse Race");
                raceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                raceFrame.setSize(800, 600);
                raceFrame.setLocationRelativeTo(this);

                Horse selectedBetHorse = (Horse) betHorseBox.getSelectedItem();
                double selectedBetAmount = ((Number) betAmount.getValue()).doubleValue();
                RacePanel racePanel = new RacePanel(selectedTrack, selectedHorses, currentUser, selectedBetHorse, selectedBetAmount);
                raceFrame.add(racePanel);
                raceFrame.setVisible(true);
                
                // Update title to reflect new balance
                updateBalance();
            });

            cancelBetButton.addActionListener(ev -> bettingDialog.dispose());

            betButtonPanel.add(startRaceButton);
            betButtonPanel.add(cancelBetButton);

            bettingDialog.add(bettingPanel, BorderLayout.CENTER);
            bettingDialog.add(betButtonPanel, BorderLayout.SOUTH);
            bettingDialog.setVisible(true);
        });

        cancelButton.addActionListener(e -> setupDialog.dispose());

        buttonPanel.add(nextButton);
        buttonPanel.add(cancelButton);

        // Add components to dialog
        setupDialog.add(selectionPanel, BorderLayout.CENTER);
        setupDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Show dialog
        setupDialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}