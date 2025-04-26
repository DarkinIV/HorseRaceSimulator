import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class HorseBarnPanel extends JPanel {
    private List<Horse> horses;
    private static final int GRID_COLS = 3;
    private static final int CELL_PADDING = 15;
    private JPanel contentPanel;

    public HorseBarnPanel(List<Horse> horses) {
        this.horses = horses;
        setLayout(new BorderLayout(0, 20));
        UITheme.stylePanel(this);

        // Add title
        JLabel titleLabel = new JLabel("Horse Barn");
        UITheme.styleHeader(titleLabel);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Create a panel for the grid content with modern styling
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(0, GRID_COLS, CELL_PADDING, CELL_PADDING));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(CELL_PADDING, CELL_PADDING, CELL_PADDING, CELL_PADDING));
        contentPanel.setBackground(UITheme.BACKGROUND_COLOR);

        // Wrap the content panel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
        createHorseCells();
    }

    private void createHorseCells() {
        contentPanel.removeAll();
        // Add all horse cells
        for (Horse horse : horses) {
            contentPanel.add(createHorseCell(horse));
        }

        // Add one empty cell for new horse creation
        contentPanel.add(createEmptyCell());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createHorseCell(Horse horse) {
        JPanel cell = new JPanel();
        cell.setLayout(new BorderLayout(8, 8));
        UITheme.styleCell(cell);

        // Add remove button with modern styling
        JButton removeButton = new JButton("×");
        removeButton.setFont(UITheme.HEADER_FONT);
        removeButton.setForeground(UITheme.ACCENT_COLOR);
        removeButton.setContentAreaFilled(false);
        removeButton.setBorderPainted(false);
        removeButton.setFocusPainted(false);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove " + horse.getName() + " from the barn?",
                "Remove Horse",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                horses.remove(horse);
                HorseManager.saveHorses(horses);
                createHorseCells();
            }
        });
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRightPanel.setBackground(Color.WHITE);
        topRightPanel.add(removeButton);

        // Horse name and symbol at the top
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(UITheme.CELL_BACKGROUND);
        
        JLabel symbolLabel = new JLabel(String.valueOf(horse.getSymbol()), SwingConstants.CENTER);
        symbolLabel.setFont(UITheme.TITLE_FONT);
        symbolLabel.setForeground(UITheme.PRIMARY_COLOR);
        topPanel.add(symbolLabel, BorderLayout.WEST);
        
        JLabel nameLabel = new JLabel(horse.getName(), SwingConstants.CENTER);
        nameLabel.setFont(UITheme.HEADER_FONT);
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);
        topPanel.add(nameLabel, BorderLayout.CENTER);
        
        cell.add(topPanel, BorderLayout.NORTH);

        // Horse attributes in the center
        JPanel attributesPanel = new JPanel();
        attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.Y_AXIS));
        attributesPanel.setBackground(UITheme.CELL_BACKGROUND);
        attributesPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        addTextAttribute(attributesPanel, "Breed", horse.getBreed());
        addTextAttribute(attributesPanel, "Color", horse.getCoatColor());
        addTextAttribute(attributesPanel, "Confidence", String.format("%.2f", horse.getConfidence()));
        addTextAttribute(attributesPanel, "Races", horse.getRacesAttended() + " ("+horse.getRacesWon()+" wins)");
        addTextAttribute(attributesPanel, "Win Rate", String.format("%.1f%%", horse.getWinRate()));

        cell.add(attributesPanel, BorderLayout.CENTER);

        // Equipment at the bottom
        JPanel equipmentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        equipmentPanel.setBackground(Color.WHITE);
        if (!horse.getEquipment().isEmpty()) {
            for (HorseEquipment eq : horse.getEquipment()) {
                JLabel equipLabel = new JLabel("⚔ " + eq.getName());
                equipLabel.setForeground(new Color(139, 69, 19)); // Saddle brown color
                equipmentPanel.add(equipLabel);
            }
        }
        cell.add(equipmentPanel, BorderLayout.SOUTH);

        // Add hover effect and click listener
        cell.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cell.setBackground(new Color(240, 248, 255)); // Alice blue
            }

            public void mouseExited(MouseEvent e) {
                cell.setBackground(Color.WHITE);
            }

            public void mouseClicked(MouseEvent e) {
                showHorseDetails(horse);
            }
        });

        return cell;
    }

    private void addTextAttribute(JPanel panel, String name, String value) {
        JPanel attrPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        attrPanel.setBackground(UITheme.CELL_BACKGROUND);
        
        JLabel nameLabel = new JLabel(name + ":");
        nameLabel.setFont(UITheme.SMALL_FONT);
        nameLabel.setForeground(UITheme.TEXT_SECONDARY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.LABEL_FONT);
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);

        attrPanel.add(nameLabel);
        attrPanel.add(valueLabel);
        panel.add(attrPanel);
    }

    private JPanel createEmptyCell() {
        JPanel cell = new JPanel(new BorderLayout());
        UITheme.styleCell(cell);
        cell.setBackground(new Color(248, 249, 250));
        
        JButton addButton = new JButton("➕ New Horse");
        UITheme.styleButton(addButton);
        addButton.addActionListener(e -> showHorseCustomization());
        
        cell.add(addButton, BorderLayout.CENTER);
        return cell;
    }

    private void showHorseCustomization() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Create New Horse", true);
        HorseCustomizationPanel customPanel = new HorseCustomizationPanel();
        
        customPanel.addPropertyChangeListener("newHorse", evt -> {
            Horse newHorse = (Horse)evt.getNewValue();
            horses.add(newHorse);
            HorseManager.saveHorses(horses);
            createHorseCells();
            dialog.dispose();
        });
        
        dialog.add(customPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showHorseEdit(Horse horse) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Edit Horse", true);
        HorseCustomizationPanel customPanel = new HorseCustomizationPanel(horse);
        customPanel.setHorseValues(horse);
        
        customPanel.addPropertyChangeListener("newHorse", evt -> {
            // Save changes
            HorseManager.saveHorses(horses);
            // Refresh the display
            createHorseCells();
            dialog.dispose();
        });
        
        dialog.add(customPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        rowPanel.setBackground(Color.WHITE);
        
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setForeground(Color.GRAY);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setForeground(Color.BLACK);
        valueComponent.setFont(new Font("Arial", Font.BOLD, 12));
        
        rowPanel.add(labelComponent);
        rowPanel.add(valueComponent);
        panel.add(rowPanel);
    }

    private void showHorseDetails(Horse horse) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Horse Details", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        detailsPanel.setBackground(Color.WHITE);

        // Add horse details
        addDetailRow(detailsPanel, "Name", horse.getName());
        addDetailRow(detailsPanel, "Breed", horse.getBreed());
        addDetailRow(detailsPanel, "Color", horse.getCoatColor());
        addDetailRow(detailsPanel, "Confidence", String.format("%.2f", horse.getConfidence()));
        addDetailRow(detailsPanel, "Races", horse.getRacesAttended() + " (" + horse.getRacesWon() + " wins)");
        addDetailRow(detailsPanel, "Win Rate", String.format("%.1f%%", horse.getWinRate()));

        // Add equipment section if any
        if (!horse.getEquipment().isEmpty()) {
            detailsPanel.add(Box.createVerticalStrut(10));
            JLabel equipmentLabel = new JLabel("Equipment:");
            equipmentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailsPanel.add(equipmentLabel);
            
            for (HorseEquipment eq : horse.getEquipment()) {
                JLabel eqLabel = new JLabel("• " + eq.getName());
                eqLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                detailsPanel.add(eqLabel);
            }
        }

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeButton = new JButton("Remove");
        removeButton.setForeground(Color.RED);
        removeButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove " + horse.getName() + " from the barn?",
                "Remove Horse",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                horses.remove(horse);
                HorseManager.saveHorses(horses);
                createHorseCells();
                dialog.dispose();
            }
        });
        buttonPanel.add(removeButton);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> {
            dialog.dispose();
            showHorseEdit(horse);
        });
        buttonPanel.add(editButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


}