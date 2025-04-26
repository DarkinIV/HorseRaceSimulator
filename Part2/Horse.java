
/**
 * Write a description of class Horse here.
 * 
 * @author Asim Yilmaz
 * @version 31/04/2025
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Horse implements Serializable
{
    //Fields of class Horse
    public char horseSymbol;
    public String horseName;
    public String breed;
    public String coatColor;
    public double horseConfidence;
    public int distanceTravelled;
    public boolean horseHasFallen;
    private List<HorseEquipment> equipment;
    private int racesAttended;
    private int racesWon;
    private Random random;
    
    
    //Constructor of class Horse
    /**
     * Constructor for objects of class Horse
     */
    public Horse(char horseSymbol, String horseName, String breed, String coatColor)
    {
        this.horseSymbol = horseSymbol;
        this.horseName = horseName;
        this.breed = breed;
        this.coatColor = coatColor;
        this.distanceTravelled = 0;
        this.horseHasFallen = false;
        this.equipment = new ArrayList<>();
        this.racesAttended = 0;
        this.racesWon = 0;
        this.random = new Random();
        this.horseConfidence = Math.round(random.nextDouble() * 100.0) / 100.0 / 100.0; // Convert to 0-1 range
    }
    
    
    
    //Other methods of class Horse
    public void fall()
    {
        this.horseHasFallen = true;
    }
    
    public double getConfidence()
    {
        double totalConfidence = this.horseConfidence;
        for (HorseEquipment eq : equipment) {
            totalConfidence += eq.getConfidenceModifier();
        }
        return Math.min(1.0, Math.max(0.0, Math.round(totalConfidence * 100.0) / 100.0));
    }
    
    public int getDistanceTravelled()
    {
        return this.distanceTravelled;
    }
    
    public String getName()
    {
        return this.horseName;
    }
    
    public char getSymbol()
    {
        return this.horseSymbol;
    }
    
    public void goBackToStart()
    {
        this.distanceTravelled = 0;
        this.horseHasFallen = false;
    }
    
    public boolean hasFallen()
    {
        return this.horseHasFallen;
    }

    public void moveForward()
    {
        this.distanceTravelled += 1;
    }

    public void setConfidence(double newConfidence)
    {
        this.horseConfidence = newConfidence;
    }
    
    public void setSymbol(char newSymbol)
    {
        this.horseSymbol = newSymbol;
    }

    public void setName(String newName) {
        this.horseName = newName;
    }

    public void setBreed(String newBreed) {
        this.breed = newBreed;
    }

    public void setCoatColor(String newCoatColor) {
        this.coatColor = newCoatColor;
    }
    
    public String getBreed() {
        return breed;
    }
    
    public String getCoatColor() {
        return coatColor;
    }
    
    public List<HorseEquipment> getEquipment() {
        return equipment;
    }
    
    public void addEquipment(HorseEquipment equipment) {
        this.equipment.add(equipment);
    }

    public void removeEquipment(HorseEquipment equipment) {
        this.equipment.remove(equipment);
    }

    public void clearEquipment() {
        this.equipment.clear();
    }

    public int getRacesAttended() {
        return racesAttended;
    }

    public int getRacesWon() {
        return racesWon;
    }

    public double getWinRate() {
        if (racesAttended == 0) return 0.0;
        return (double) racesWon / racesAttended * 100;
    }

    public void recordRaceResult(boolean won) {
        racesAttended++;
        if (won) racesWon++;
    }
}