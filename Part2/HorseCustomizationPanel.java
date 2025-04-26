import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

public class HorseCustomizationPanel extends JPanel {
    private Horse editingHorse;
    private static final List<String> BREEDS = Arrays.asList(
        "Thoroughbred", "Arabian", "Quarter Horse", "Mustang", "Appaloosa"
    );
    private static final List<String> COAT_COLORS = Arrays.asList(
        "Brown", "Black", "White", "Grey", "Chestnut", "Bay", "Palomino"
    );
    private static final List<String> SYMBOLS = Arrays.asList(
        "🐎", "🦄", "♘", "♞", "⚡", "★", "♠", "♥", "♦", "♣"
    );
    private static final List<String> ACCESSORIES = Arrays.asList(
        "Bridle", "Blanket", "Hat", "Ribbon", "Feathers"
    );

    private JTextField nameField;
    private JComboBox<String> breedBox;
    private JComboBox<String> colorBox;
    private JComboBox<String> symbolBox;
    private JList<String> accessoryList;
    private JComboBox<String> equipmentBox;
    private JLabel previewLabel;
    private JLabel confidenceLabel;
    private static final double BASE_CONFIDENCE = 0.5;
    private static final java.util.Map<String, Double> BREED_CONFIDENCE = new java.util.HashMap<String, Double>() {{
        put("Thoroughbred", 0.65);
        put("Arabian", 0.60);
        put("Quarter Horse", 0.55);
        put("Mustang", 0.50);
        put("Appaloosa", 0.45);
    }};

    public HorseCustomizationPanel() {
        this(null);
    }

    public HorseCustomizationPanel(Horse horse) {
        this.editingHorse = horse;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name input
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1;
        contentPanel.add(nameField, gbc);

        // Breed selection
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("Breed:"), gbc);
        breedBox = new JComboBox<>(BREEDS.toArray(new String[0]));
        gbc.gridx = 1;
        contentPanel.add(breedBox, gbc);

        // Color selection
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(new JLabel("Coat Color:"), gbc);
        colorBox = new JComboBox<>(COAT_COLORS.toArray(new String[0]));
        gbc.gridx = 1;
        contentPanel.add(colorBox, gbc);

        // Symbol selection
        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(new JLabel("Symbol:"), gbc);
        symbolBox = new JComboBox<>(SYMBOLS.toArray(new String[0]));
        gbc.gridx = 1;
        contentPanel.add(symbolBox, gbc);

        // Equipment selection
        gbc.gridx = 0; gbc.gridy = 4;
        contentPanel.add(new JLabel("Equipment:"), gbc);
        equipmentBox = new JComboBox<>(new String[]{"None", "Racing Saddle", "Lightweight Horseshoes", "Premium Bridle"});
        gbc.gridx = 1;
        contentPanel.add(equipmentBox, gbc);

        // Accessories selection
        gbc.gridx = 0; gbc.gridy = 5;
        contentPanel.add(new JLabel("Accessories:"), gbc);
        accessoryList = new JList<>(ACCESSORIES.toArray(new String[0]));
        accessoryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane accessoryScroll = new JScrollPane(accessoryList);
        accessoryScroll.setPreferredSize(new Dimension(150, 80));
        gbc.gridx = 1;
        contentPanel.add(accessoryScroll, gbc);

        // Preview panel
        JPanel previewPanel = new JPanel(new BorderLayout(5, 5));
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        previewLabel = new JLabel("", SwingConstants.CENTER);
        previewLabel.setFont(new Font("Arial", Font.BOLD, 48));
        previewPanel.add(previewLabel, BorderLayout.CENTER);
        confidenceLabel = new JLabel("Base Confidence: 0.50", SwingConstants.CENTER);
        previewPanel.add(confidenceLabel, BorderLayout.SOUTH);

        // Add listeners for real-time preview updates
        ItemListener updatePreview = e -> updatePreview();
        breedBox.addItemListener(updatePreview);
        colorBox.addItemListener(updatePreview);
        symbolBox.addItemListener(updatePreview);
        equipmentBox.addItemListener(updatePreview);
        accessoryList.addListSelectionListener(e -> updatePreview());

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton actionButton = new JButton(editingHorse == null ? "Create Horse" : "Save Horse");
        actionButton.addActionListener(e -> saveHorse());
        buttonPanel.add(actionButton);

        // Add all components to the main panel
        add(contentPanel, BorderLayout.CENTER);
        add(previewPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial preview update
        updatePreview();
    }

    private void updatePreview() {
        String symbol = (String) symbolBox.getSelectedItem();
        previewLabel.setText(symbol);

        // Get breed-specific base confidence
        String breed = (String) breedBox.getSelectedItem();
        double confidence = BREED_CONFIDENCE.get(breed);

        // Equipment effects
        String equipment = (String) equipmentBox.getSelectedItem();
        if (!equipment.equals("None")) {
            switch(equipment) {
                case "Racing Saddle": confidence += 0.05; break;
                case "Lightweight Horseshoes": confidence += 0.03; break;
                case "Premium Bridle": confidence += 0.04; break;
            }
        }

        // Accessories effect (each accessory adds 1% confidence)
        confidence += accessoryList.getSelectedIndices().length * 0.01;

        // Cap confidence at 1.0
        confidence = Math.min(1.0, confidence);

        // Update confidence label
        confidenceLabel.setText(String.format("Confidence: %.2f", confidence));
    }

    public void setHorseValues(Horse horse) {
        nameField.setText(horse.getName());
        breedBox.setSelectedItem(horse.getBreed());
        colorBox.setSelectedItem(horse.getCoatColor());
        symbolBox.setSelectedItem(String.valueOf(horse.getSymbol()));
        
        // Set equipment if any
        if (!horse.getEquipment().isEmpty()) {
            HorseEquipment eq = horse.getEquipment().get(0);
            equipmentBox.setSelectedItem(eq.getName());
        } else {
            equipmentBox.setSelectedItem("None");
        }
        
        updatePreview();
    }

    private void saveHorse() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name for your horse.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate names only when creating a new horse
        if (editingHorse == null) {
            List<Horse> existingHorses = HorseManager.loadHorses();
            boolean nameExists = existingHorses.stream()
                .filter(h -> !h.equals(editingHorse))
                .anyMatch(h -> h.getName().equalsIgnoreCase(name));
            if (nameExists) {
                JOptionPane.showMessageDialog(this, "A horse with this name already exists. Please choose a different name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String symbol = (String) symbolBox.getSelectedItem();
        String breed = (String) breedBox.getSelectedItem();
        String color = (String) colorBox.getSelectedItem();
        String equipment = (String) equipmentBox.getSelectedItem();
        List<String> selectedAccessories = accessoryList.getSelectedValuesList();

        Horse horse;
        if (editingHorse == null) {
            // Create new horse
            horse = new Horse(
                symbol.charAt(0),
                name,
                breed,
                color
            );
        } else {
            // Update existing horse
            horse = editingHorse;
            horse.setSymbol(symbol.charAt(0));
            horse.setName(name);
            horse.setBreed(breed);
            horse.setCoatColor(color);
        }
        
        // Set confidence and equipment
        double confidence = calculateFinalConfidence();
        horse.setConfidence(confidence);
        
        horse.clearEquipment();
        if (!equipment.equals("None")) {
            double equipmentBonus = 0.0;
            switch(equipment) {
                case "Racing Saddle": equipmentBonus = 0.05; break;
                case "Lightweight Horseshoes": equipmentBonus = 0.03; break;
                case "Premium Bridle": equipmentBonus = 0.04; break;
            }
            horse.addEquipment(new HorseEquipment(equipment, "Improves performance", equipmentBonus));
        }

        // Fire property change event with the horse
        firePropertyChange("newHorse", null, horse);
        
        if (editingHorse == null) {
            // Save new horse
            List<Horse> existingHorses = HorseManager.loadHorses();
            existingHorses.add(horse);
            HorseManager.saveHorses(existingHorses);
        }

        // Reset form
        resetForm();
    }

    private double calculateFinalConfidence() {
        // Get breed-specific base confidence
        String breed = (String) breedBox.getSelectedItem();
        double confidence = BREED_CONFIDENCE.get(breed);

        // Add equipment bonus
        String equipment = (String) equipmentBox.getSelectedItem();
        if (!equipment.equals("None")) {
            switch(equipment) {
                case "Racing Saddle": confidence += 0.05; break;
                case "Lightweight Horseshoes": confidence += 0.03; break;
                case "Premium Bridle": confidence += 0.04; break;
            }
        }

        // Add accessories bonus (each accessory adds 1%)
        confidence += accessoryList.getSelectedIndices().length * 0.01;

        return Math.min(1.0, confidence); // Cap at 100%
    }

    private void resetForm() {
        nameField.setText("");
        breedBox.setSelectedIndex(0);
        colorBox.setSelectedIndex(0);
        symbolBox.setSelectedIndex(0);
        equipmentBox.setSelectedIndex(0);
        accessoryList.clearSelection();
        updatePreview();
    }
}