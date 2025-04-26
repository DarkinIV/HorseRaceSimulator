import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HorseManager {
    private static final String SAVE_FILE = "horses.dat";
    
    public static void saveHorses(List<Horse> horses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(new ArrayList<>(horses));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Horse> loadHorses() {
        if (new File(SAVE_FILE).exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(SAVE_FILE))) {
                return (List<Horse>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}