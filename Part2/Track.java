import java.io.Serializable;
import java.util.ArrayList;

public class Track implements Serializable {
    public enum Shape {
        OVAL,
        FIGURE_EIGHT,
        CUSTOM
    }
    
    public enum Weather {
        SUNNY(1.0, 0.0, 1.0),
        RAINY(0.8, 0.2, 0.9),
        MUDDY(0.6, 0.3, 0.7),
        ICY(0.5, 0.4, 0.6);
        
        private final double speedMultiplier;
        private final double fallRisk;
        private final double confidenceMultiplier;
        
        Weather(double speedMult, double fallRisk, double confMult) {
            this.speedMultiplier = speedMult;
            this.fallRisk = fallRisk;
            this.confidenceMultiplier = confMult;
        }
        
        public double getSpeedMultiplier() { return speedMultiplier; }
        public double getFallRisk() { return fallRisk; }
        public double getConfidenceMultiplier() { return confidenceMultiplier; }
    }
    
    private String name;
    private Shape shape;
    private int length;
    private int laneCount;
    private Weather weather;
    private ArrayList<Horse> horses;
    private static final long serialVersionUID = 1L;
    
    public Track(String name, Shape shape, int length, int laneCount) {
        this.name = name;
        this.shape = shape;
        // Ensure length is within 10-100 range
        this.length = Math.max(10, Math.min(100, length));
        this.laneCount = laneCount;
        this.weather = Weather.SUNNY; // Default weather
        this.horses = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplayName() {
        return String.format("%s (%dm)", name, length);
    }
    
    public boolean addHorse(Horse horse) {
        if (horses.size() < laneCount) {
            horses.add(horse);
            return true;
        }
        return false;
    }
    
    public int getRemainingLanes() {
        return laneCount - horses.size();
    }
    
    public void setWeather(Weather weather) {
        this.weather = weather;
        // Update all horses' confidence based on weather
        for (Horse horse : horses) {
            horse.setConfidence(horse.getConfidence() * weather.getConfidenceMultiplier());
        }
    }
    
    public boolean checkFall(Horse horse) {
        // Calculate fall probability based on weather and horse confidence
        // Balance between overconfidence and underconfidence risks
        double confidence = horse.getConfidence();
        // Overconfidence factor increases risk for very confident horses
        double overconfidenceFactor = confidence > 0.7 ? Math.pow((confidence - 0.7) * 3.33, 2) : 0;
        // Underconfidence factor increases risk for very unconfident horses
        double underconfidenceFactor = confidence < 0.3 ? Math.pow((0.3 - confidence) * 3.33, 2) : 0;
        // Combine both factors with weather risk
        double fallProbability = weather.getFallRisk() * (0.2 + overconfidenceFactor + underconfidenceFactor);
        // Ensure probability stays within reasonable bounds
        fallProbability = Math.min(0.8, Math.max(0.0, fallProbability));
        return Math.random() < fallProbability;
    }
    
    public double getSpeedModifier() {
        // Get speed modifier based on track shape and weather
        double baseModifier = weather.getSpeedMultiplier();
        
        switch (shape) {
            case FIGURE_EIGHT:
                // Reduce speed at intersections
                return baseModifier * 0.8;
            case CUSTOM:
                // Variable speed based on track complexity
                return baseModifier * 0.9;
            default: // OVAL
                return baseModifier;
        }
    }
    
    // Getters
    public Shape getShape() { return shape; }
    public int getLength() { return length; }
    public int getLaneCount() { return laneCount; }
    public Weather getWeather() { return weather; }
    public ArrayList<Horse> getHorses() { return horses; }
}