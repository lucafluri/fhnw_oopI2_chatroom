package chatroom_client;


import java.lang.reflect.Array;
import java.util.*;

public class model {
    boolean maximized = false;
    boolean settingsOpen = false;
    String ipAddress = "127.0.0.1";
    int portNumber = 31415;
    boolean secure = false;
    boolean connected = false;
    String currentUser = "";
    String currentChatroom = "";
    String token = "";
    String lastAnswer = "";
    HashSet<String> joinedRooms = new HashSet();
    HashSet<String> joinableRooms = new HashSet<>();
    boolean loggedIn = false;
    ArrayList<String> contacts = new ArrayList<>();
    ArrayList<String> blockedUsers = new ArrayList<>();
    String[] publicChatrooms = new String[1];
    TreeMap<String, ArrayList<message>> messages = new TreeMap(); //temporary Message Storage -> NOT PERSISTENT



    boolean isAnswerReady(){
        return !lastAnswer.equals("");
    }
}
