import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JButton barnButton;
    private JButton trackButton;
    private JButton raceButton;
    
    public MainWindow() {
        setTitle("Horse Racing Game");
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
        buttonPanel.setLayout(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBorder(new EmptyBorder(20, 200, 40, 200));
        buttonPanel.setOpaque(false);
        
        // Initialize buttons with modern styling
        barnButton = new JButton("🐎  Visit Barn");
        trackButton = new JButton("🏁  Track Selection");
        raceButton = new JButton("⚡  Start Race");
        
        // Apply modern styling to buttons
        UITheme.styleButton(barnButton);
        UITheme.styleButton(trackButton);
        UITheme.styleButton(raceButton);
        
        // Add action listeners
        barnButton.addActionListener(e -> visitBarn());
        trackButton.addActionListener(e -> selectTrack());
        raceButton.addActionListener(e -> startRace());
        
        // Add buttons to panel
        buttonPanel.add(barnButton);
        buttonPanel.add(trackButton);
        buttonPanel.add(raceButton);
        
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
    
    private void startRace() {
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
        setupDialog.setSize(400, 500);
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
        JButton startButton = new JButton("Start Race");
        JButton cancelButton = new JButton("Cancel");

        startButton.addActionListener(e -> {
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

            // Create and show race window
            JFrame raceFrame = new JFrame("Horse Race");
            raceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            raceFrame.setSize(800, 600);
            raceFrame.setLocationRelativeTo(this);

            RacePanel racePanel = new RacePanel(selectedTrack, selectedHorses);
            raceFrame.add(racePanel);
            raceFrame.setVisible(true);
        });

        cancelButton.addActionListener(e -> setupDialog.dispose());

        buttonPanel.add(startButton);
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