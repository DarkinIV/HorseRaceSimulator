import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TrackPanel extends JPanel {
    private Track track;
    private JPanel controlPanel;
    private JTextField nameField;
    private JComboBox<Track.Shape> shapeSelector;
    private JSpinner lengthSpinner;
    private JSpinner laneSpinner;
    private JComboBox<Track.Weather> weatherSelector;
    
    public Track getTrack() {
        return track;
    }
    
    public void setTrack(Track track) {
        this.track = track;
        shapeSelector.setSelectedItem(track.getShape());
        lengthSpinner.setValue(track.getLength());
        laneSpinner.setValue(track.getLaneCount());
        weatherSelector.setSelectedItem(track.getWeather());
        repaint();
    }
    
    public TrackPanel() {
        setLayout(new BorderLayout());
        
        // Initialize track with default values
        track = new Track("New Track", Track.Shape.OVAL, 1000, 4);
        
        // Create control panel
        createControlPanel();
        
        // Create track display panel
        JPanel trackDisplayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTrack(g);
            }
        };
        trackDisplayPanel.setPreferredSize(new Dimension(600, 400));
        
        // Add panels
        add(controlPanel, BorderLayout.EAST);
        add(trackDisplayPanel, BorderLayout.CENTER);
    }
    
    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Track name input
        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Track Name: "));
        nameField = new JTextField(15);
        nameField.setText(track.getName());
        nameField.addActionListener(e -> updateTrack());
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateTrack();
            }
        });
        namePanel.add(nameField);

        // Track shape selector
        JPanel shapePanel = new JPanel();
        shapePanel.add(new JLabel("Track Shape: "));
        shapeSelector = new JComboBox<>(Track.Shape.values());
        shapeSelector.addActionListener(e -> updateTrack());
        shapePanel.add(shapeSelector);
        
        // Track length slider
        JPanel lengthPanel = new JPanel();
        lengthPanel.add(new JLabel("Length (units): "));
        JSlider lengthSlider = new JSlider(JSlider.HORIZONTAL, 25, 100, 50);
        lengthSlider.setMajorTickSpacing(10);
        lengthSlider.setMinorTickSpacing(5);
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.addChangeListener(e -> {
            lengthSpinner.setValue(lengthSlider.getValue());
            updateTrack();
        });
        lengthSpinner = new JSpinner(new SpinnerNumberModel(50, 25, 100, 5));
        lengthSpinner.addChangeListener(e -> {
            lengthSlider.setValue((Integer)lengthSpinner.getValue());
            updateTrack();
        });
        lengthPanel.add(lengthSlider);
        lengthPanel.add(lengthSpinner);
        
        // Lane count spinner
        JPanel lanePanel = new JPanel();
        lanePanel.add(new JLabel("Lanes: "));
        laneSpinner = new JSpinner(new SpinnerNumberModel(4, 2, 8, 1));
        laneSpinner.addChangeListener(e -> updateTrack());
        lanePanel.add(laneSpinner);
        
        // Weather selector
        JPanel weatherPanel = new JPanel();
        weatherPanel.add(new JLabel("Weather: "));
        weatherSelector = new JComboBox<>(Track.Weather.values());
        weatherSelector.addActionListener(e -> updateTrack());
        weatherPanel.add(weatherSelector);
        
        // Add all panels to control panel
        controlPanel.add(namePanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(shapePanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(lengthPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(lanePanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(weatherPanel);
    }
    
    private void updateTrack() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            name = "New Track";
            nameField.setText(name);
        }
        Track.Shape shape = (Track.Shape) shapeSelector.getSelectedItem();
        int length = (Integer) lengthSpinner.getValue();
        int lanes = (Integer) laneSpinner.getValue();
        
        track = new Track(name, shape, length, lanes);
        track.setWeather((Track.Weather) weatherSelector.getSelectedItem());
        
        repaint();
    }
    
    private void drawTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth() - 40;
        int height = getHeight() - 40;
        int x = 20;
        int y = 20;
        
        g2d.setColor(Color.GREEN.darker());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setColor(Color.GRAY);
        
        switch (track.getShape()) {
            case OVAL:
                drawOvalTrack(g2d, x, y, width, height);
                break;
            case FIGURE_EIGHT:
                drawFigureEightTrack(g2d, x, y, width, height);
                break;
            case CUSTOM:
                drawCustomTrack(g2d, x, y, width, height);
                break;
        }
    }
    
    private void drawOvalTrack(Graphics2D g2d, int x, int y, int width, int height) {
        for (int i = 0; i < track.getLaneCount(); i++) {
            int padding = i * 20;
            g2d.drawOval(x + padding, y + padding, 
                        width - (padding * 2), height - (padding * 2));
        }
    }
    
    private void drawFigureEightTrack(Graphics2D g2d, int x, int y, int width, int height) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
        for (int i = 0; i < track.getLaneCount(); i++) {
            int padding = i * 20;
            // Draw left circle
            g2d.drawOval(x + padding, y + padding, 
                        width/2 - padding, height - (padding * 2));
            // Draw right circle
            g2d.drawOval(centerX + padding, y + padding, 
                        width/2 - padding, height - (padding * 2));
        }
    }
    
    private void drawCustomTrack(Graphics2D g2d, int x, int y, int width, int height) {
        // Draw a simple zigzag track
        int segments = 4;
        int segmentWidth = width / segments;
        
        for (int i = 0; i < track.getLaneCount(); i++) {
            int padding = i * 10;
            int[] xPoints = new int[segments + 1];
            int[] yPoints = new int[segments + 1];
            
            for (int j = 0; j <= segments; j++) {
                xPoints[j] = x + padding + (j * segmentWidth);
                yPoints[j] = y + padding + ((j % 2 == 0) ? 0 : height - padding * 2);
            }
            
            g2d.drawPolyline(xPoints, yPoints, segments + 1);
        }
    }
}