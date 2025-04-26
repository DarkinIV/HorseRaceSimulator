import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TrackBarnPanel extends JPanel {
    private List<Track> tracks;
    private static final int GRID_COLS = 3;
    private static final int CELL_PADDING = 15;
    private JPanel contentPanel;

    public TrackBarnPanel(List<Track> tracks) {
        this.tracks = tracks;
        setLayout(new BorderLayout(0, 20));
        UITheme.stylePanel(this);

        // Add title
        JLabel titleLabel = new JLabel("Track Selection");
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
        createTrackCells();
    }

    private void createTrackCells() {
        contentPanel.removeAll();
        // Add all track cells
        for (Track track : tracks) {
            contentPanel.add(createTrackCell(track));
        }

        // Add one empty cell for new track creation
        contentPanel.add(createEmptyCell());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createTrackCell(Track track) {
        JPanel cell = new JPanel();
        cell.setLayout(new BorderLayout(8, 8));
        UITheme.styleCell(cell);

        // Add remove button
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
                "Are you sure you want to remove this track?",
                "Remove Track",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                tracks.remove(track);
                TrackManager.saveTracks(tracks);
                createTrackCells();
            }
        });

        // Track shape and details at the top
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(UITheme.CELL_BACKGROUND);
        
        JLabel shapeLabel = new JLabel(getShapeSymbol(track.getShape()), SwingConstants.CENTER);
        shapeLabel.setFont(UITheme.TITLE_FONT);
        shapeLabel.setForeground(UITheme.PRIMARY_COLOR);
        topPanel.add(shapeLabel, BorderLayout.WEST);
        
        JLabel nameLabel = new JLabel(track.getDisplayName(), SwingConstants.CENTER);
        nameLabel.setFont(UITheme.HEADER_FONT);
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);
        topPanel.add(nameLabel, BorderLayout.CENTER);
        
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRightPanel.setBackground(Color.WHITE);
        topRightPanel.add(removeButton);
        topPanel.add(topRightPanel, BorderLayout.EAST);
        
        cell.add(topPanel, BorderLayout.NORTH);

        // Track attributes in the center
        JPanel attributesPanel = new JPanel();
        attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.Y_AXIS));
        attributesPanel.setBackground(UITheme.CELL_BACKGROUND);
        attributesPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        addTextAttribute(attributesPanel, "Length", track.getLength() + " m");
        addTextAttribute(attributesPanel, "Lanes", String.valueOf(track.getLaneCount()));
        addTextAttribute(attributesPanel, "Weather", track.getWeather().toString());

        // Add weather effects
        addTextAttribute(attributesPanel, "Speed Mult", String.format("%.1fx", track.getWeather().getSpeedMultiplier()));
        addTextAttribute(attributesPanel, "Fall Risk", String.format("%.1f%%", track.getWeather().getFallRisk() * 100));

        cell.add(attributesPanel, BorderLayout.CENTER);

        // Add preview panel at the bottom
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTrackPreview(g, track);
            }
        };
        previewPanel.setPreferredSize(new Dimension(120, 80));
        previewPanel.setBackground(Color.WHITE);
        cell.add(previewPanel, BorderLayout.SOUTH);

        // Add hover effect and click listener
        cell.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cell.setBackground(new Color(240, 248, 255)); // Alice blue
            }

            public void mouseExited(MouseEvent e) {
                cell.setBackground(Color.WHITE);
            }

            public void mouseClicked(MouseEvent e) {
                if (e.getSource() != removeButton) {
                    showTrackDetails(track);
                }
            }
        });

        return cell;
    }

    private String getShapeSymbol(Track.Shape shape) {
        switch (shape) {
            case OVAL: return "⭕";
            case FIGURE_EIGHT: return "∞";
            case CUSTOM: return "⚡";
            default: return "?";
        }
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

    private void drawTrackPreview(Graphics g, Track track) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = 100;
        int height = 60;
        int x = 10;
        int y = 10;
        
        g2d.setColor(Color.LIGHT_GRAY);
        
        switch (track.getShape()) {
            case OVAL:
                g2d.drawOval(x, y, width, height);
                break;
            case FIGURE_EIGHT:
                int centerX = x + width / 2;
                g2d.drawOval(x, y, width/2, height);
                g2d.drawOval(centerX, y, width/2, height);
                break;
            case CUSTOM:
                int[] xPoints = {x, x + width/2, x + width};
                int[] yPoints = {y + height, y, y + height};
                g2d.drawPolyline(xPoints, yPoints, 3);
                break;
        }
    }

    private JPanel createEmptyCell() {
        JPanel cell = new JPanel(new BorderLayout());
        UITheme.styleCell(cell);
        cell.setBackground(new Color(248, 249, 250));
        
        JButton addButton = new JButton("➕ New Track");
        UITheme.styleButton(addButton);
        addButton.addActionListener(e -> showTrackCustomization());
        
        cell.add(addButton, BorderLayout.CENTER);
        return cell;
    }

    private void showTrackCustomization() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Create New Track", true);
        TrackPanel customPanel = new TrackPanel();
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Track");
        saveButton.addActionListener(e -> {
            Track newTrack = customPanel.getTrack();
            tracks.add(newTrack);
            TrackManager.saveTracks(tracks);
            createTrackCells();
            dialog.dispose();
        });
        buttonPanel.add(saveButton);
        
        dialog.setLayout(new BorderLayout());
        dialog.add(customPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showTrackDetails(Track track) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Track Details", true);
        TrackPanel detailPanel = new TrackPanel();
        detailPanel.setTrack(track);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton removeButton = new JButton("Remove");
        removeButton.setForeground(Color.RED);
        removeButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this track?",
                "Remove Track",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                tracks.remove(track);
                TrackManager.saveTracks(tracks);
                createTrackCells();
                dialog.dispose();
            }
        });
        buttonPanel.add(removeButton);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            Track updatedTrack = detailPanel.getTrack();
            int index = tracks.indexOf(track);
            if (index != -1) {
                tracks.set(index, updatedTrack);
                TrackManager.saveTracks(tracks);
                createTrackCells();
            }
            dialog.dispose();
        });
        buttonPanel.add(saveButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(detailPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}