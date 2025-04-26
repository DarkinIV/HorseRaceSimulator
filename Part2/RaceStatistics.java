import java.io.Serializable;

public class RaceStatistics implements Serializable {
    private double averageSpeed;
    private long finishingTime;
    private boolean won;
    private double confidenceChange;
    private int trackLength;
    private long startTime;
    
    public RaceStatistics(int trackLength) {
        this.trackLength = trackLength;
        this.startTime = System.currentTimeMillis();
    }
    
    public void recordFinish(long endTime, boolean won, double confidenceChange) {
        this.finishingTime = endTime - startTime;
        this.won = won;
        this.confidenceChange = confidenceChange;
        // Calculate average speed in meters per second
        this.averageSpeed = (trackLength * 1000.0) / finishingTime;
    }
    
    public double getAverageSpeed() {
        return averageSpeed;
    }
    
    public long getFinishingTime() {
        return finishingTime;
    }
    
    public boolean hasWon() {
        return won;
    }
    
    public double getConfidenceChange() {
        return confidenceChange;
    }
    
    public String getFormattedFinishTime() {
        long seconds = finishingTime / 1000;
        long milliseconds = finishingTime % 1000;
        return String.format("%d.%03d seconds", seconds, milliseconds);
    }
}