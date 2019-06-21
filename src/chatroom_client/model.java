package chatroom_client;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

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
    ArrayList<String> joinedRooms = new ArrayList<>();
    String[] joinableRooms = new String[1];
    boolean loggedIn = false;
    ArrayList<String> contacts = new ArrayList<>();
    ArrayList<String> blockedUsers = new ArrayList<>();
    String[] publicChatrooms = new String[1];
    TreeMap<String, message[]> messages = new TreeMap();



    boolean isAnswerReady(){
        return !lastAnswer.equals("");
    }
}
