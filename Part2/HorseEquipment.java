import java.io.Serializable;

public class HorseEquipment implements Serializable {
    private String name;
    private String effect;
    private double confidenceModifier;
    
    public HorseEquipment(String name, String effect, double confidenceModifier) {
        this.name = name;
        this.effect = effect;
        this.confidenceModifier = confidenceModifier;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEffect() {
        return effect;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setEffect(String effect) {
        this.effect = effect;
    }
    
    public double getConfidenceModifier() {
        return confidenceModifier;
    }
    
    public void setConfidenceModifier(double confidenceModifier) {
        this.confidenceModifier = confidenceModifier;
    }
}