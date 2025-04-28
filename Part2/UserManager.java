import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USER_FILE = "users.dat";
    private static Map<String, User> users = new HashMap<>();

    static {
        loadUsers();
    }

    public static User loginUser(String username) {
        User user = users.get(username);
        if (user == null) {
            user = new User(username);
            users.put(username, user);
            saveUsers();
        }
        return user;
    }

    public static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadUsers() {
        if (new File(USER_FILE).exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(USER_FILE))) {
                users = (Map<String, User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                users = new HashMap<>();
            }
        }
    }

    public static User getUser(String username) {
        return users.get(username);
    }
}