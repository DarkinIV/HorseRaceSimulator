import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TrackManager {
    private static final String TRACKS_FILE = "tracks.dat";
    
    public static void saveTracks(List<Track> tracks) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(TRACKS_FILE))) {
            oos.writeObject(tracks);
        } catch (IOException e) {
            System.err.println("Error saving tracks: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public static List<Track> loadTracks() {
        List<Track> tracks = new ArrayList<>();
        
        // Initialize default track configurations if no saved tracks exist
        if (!new File(TRACKS_FILE).exists()) {
            // Beginner-friendly oval track: shorter length, fewer lanes
            Track ovalTrack = new Track("Beginner's Oval", Track.Shape.OVAL, 40, 4);
            ovalTrack.setWeather(Track.Weather.SUNNY);
            tracks.add(ovalTrack);
            
            // Intermediate figure-eight track: moderate length and lanes
            Track figureEightTrack = new Track("Figure Eight Challenge", Track.Shape.FIGURE_EIGHT, 60, 6);
            figureEightTrack.setWeather(Track.Weather.SUNNY);
            tracks.add(figureEightTrack);
            
            // Advanced custom track: longer length, more lanes, challenging weather
            Track customTrack = new Track("Pro Circuit", Track.Shape.CUSTOM, 80, 8);
            customTrack.setWeather(Track.Weather.RAINY);
            tracks.add(customTrack);
            
            saveTracks(tracks);
            return tracks;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(TRACKS_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                tracks = (List<Track>) obj;
            } else {
                System.err.println("Loaded object is not a List");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading tracks: " + e.getMessage());
        }
        
        return tracks;
    }
}