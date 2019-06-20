package chatroom_client;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class model {
    boolean maximized = false;
    boolean settingsOpen = false;
    String ipAddress = "127.0.0.1";
    int portNumber = 31415;
    boolean secure = false;
    boolean connected = false;
    String currentUser = null;
    String token = null;
    String lastAnswer = null;
    ArrayList<String> joinedRooms = new ArrayList<>();
    boolean loggedIn = false;
    ArrayList<String> contacts = new ArrayList<>();
    ArrayList<String> blockedUsers = new ArrayList<>();
    String[] publicChatrooms = null;








    public model(){

    }
}
