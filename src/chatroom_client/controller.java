package chatroom_client;




import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class controller {
    private final view view;
    private final model model;
    private double xOffset = 0;
    private double yOffset = 0;
    private Socket socket = null;
    BufferedReader socketIn;
    OutputStreamWriter socketOut;
    Runnable r;
    Thread t;
    Runnable r1;
    Thread t1;

    public controller(model model, view view) throws IOException {
        this.model = model;
        this.view = view;

        windowEventHandlers();
        SettingsEventHandlers();
        //connectToServer();
        //startThreadListener();
        startThreadStates();




    }
    //Manage States when not connected in a thread
    private void startStates() {
        r1 = () -> {
            while(true) {
                //CheckConnection State
                //If not Connected
                if (model.connected) {
                    //TODO States to active
                    view.mvCreateLogin.setDisable(false);
                    view.mvLogin.setDisable(false);
                    view.mvJoinChatroom.setDisable(false);
                    view.mvLeaveChatoom.setDisable(false);
                    view.mvCreateChatroom.setDisable(false);
                    view.mvDeleteChatroom.setDisable(false);
                    view.mvChangePassword.setDisable(false);
                    view.mvDeleteLogin.setDisable(false);
                    view.mvLogout.setDisable(false);
                    displayStatus(getBind("ConnectedStatus"), model.ipAddress);



                    while(model.connected){}

                } else {
                    //TODO States to disabled
                    displayStatus(getBind("ConnectingFail"), model.ipAddress);
                    view.mvCreateLogin.setDisable(true);
                    view.mvJoinChatroom.setDisable(true);
                    view.mvLogin.setDisable(true);
                    view.mvLeaveChatoom.setDisable(true);
                    view.mvCreateChatroom.setDisable(true);
                    view.mvDeleteChatroom.setDisable(true);
                    view.mvChangePassword.setDisable(true);
                    view.mvDeleteLogin.setDisable(true);
                    view.mvLogout.setDisable(true);

                    while(!model.connected){}

                }



            }
        };
        t1 = new Thread(r1);
        t1.start();
    }

    //Derived from TestClient Example
    private void startThreadListener() { //TODO only use for messages from server, redirect answers!
        setReaderWriters(socket);
        // Create thread to read incoming messages
        r = () -> {
            while (true) {
                String msg;
                try {
                    msg = socketIn.readLine();
                    processMsg(msg);
                    System.out.println("Received: " + msg);
                } catch (IOException e) {
                    break;
                }
                if (msg == null) {
                    model.connected = false;
                    break; // In case the server closes the socket
                }
                Platform.runLater(() -> {
                    displayInfo1(msg);
                    //view.cMessagesContainer.getChildren().add(new Label(msg));
                });
            }
        };
        t = new Thread(r);
        t.start();
    }

    private void stopThreadListener(){
        try{
            t.stop();
        }catch (Exception e){

        }
    }

    private static boolean validateIpAddress(String ipAddress) {
        boolean formatOK = false;
        // Check for validity (not complete, but not bad)
        String ipPieces[] = ipAddress.split("\\."); // Must escape (see
        // documentation)
        // Must have 4 parts
        if (ipPieces.length == 4) {
            // Each part must be an integer 0 to 255
            formatOK = true; // set to false on the first error
            int byteValue = -1;
            for (String s : ipPieces) {
                byteValue = Integer.parseInt(s); // may throw
                // NumberFormatException
                if (byteValue < 0 | byteValue > 255) formatOK = false;
            }
        }
        return formatOK;
    }

    private static boolean validatePortNumber(String portText) {
        boolean formatOK = false;
        try {
            int portNumber = Integer.parseInt(portText);
            if (portNumber >= 1024 & portNumber <= 65535) {
                formatOK = true;
            }
        } catch (NumberFormatException e) {
        }
        return formatOK;
    }


    private void processMsg(String msg){
        //vars
        String MessageType;
        String ErrorMessage;
        String name;
        String target;
        String Text;



        String[] parts = msg.split("\\|");
        int length = parts.length;
        MessageType = parts[0];
        if(MessageType.equals("Result")){

            model.lastAnswer = msg;
        }else if(MessageType.equals("MessageError")){
            //TODO DISPLAY ERROR
        }else if(MessageType.equals("MessageText")){
            //TODO Show Message
        }
    }

    private void resetLastAnswer(){model.lastAnswer=null;}


    private boolean getSuccess(){
        while(model.lastAnswer==null){displayInfo2("waiting for Answer...");} //wait for message to come in
        displayInfo2("");
        String[] parts = model.lastAnswer.split("\\|");
        if(parts.length==2){resetLastAnswer();}//reset lastAnswer when only checking for success  bool

        return Boolean.parseBoolean(parts[1]);

    }

    private void setToken(){
        String[] parts = model.lastAnswer.split("\\|");
        System.out.println(parts.toString());

        model.token=parts[2];
        resetLastAnswer(); //reset lastAnswer to null
    }

    private String[] getList(){
        String[] parts = model.lastAnswer.split("\\|");
        resetLastAnswer(); //reset lastAnswer to null
        int listItems = parts.length-2;
        String[] list = new String[listItems];
        for(int i = 0; i<listItems;i++){
            list[i] = parts[i+2];
        }
        return list;

    }


    private boolean connectToServer(String ip, int port) { //No SSL
        try {
            socket = new Socket(ip, port);
            model.connected = true;
            model.ipAddress = ip;
            model.portNumber = port;
            startThreadListener();
            return true;
        }catch (IOException e){
            model.connected = false;
            return false;
        }finally {

        }

    }



    private void setReaderWriters(Socket socket) {
        try {
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOut = new OutputStreamWriter(socket.getOutputStream());
            System.out.println("READER WRITER ESTABLISHED");
        }catch (IOException ex){
            System.out.println("FAIL");
        }

    }

    private void sendString(String text) {

        try {
            socketOut.write(text + "\n");
            socketOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String... parts){
        try {
            for (String part : parts) {
                socketOut.write(part + "|");
            }
            socketOut.write("\n"); //Only works with lines...
            socketOut.flush();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }


    public void displayStatus(StringBinding status, String ip){
        view.sbServerStatus.textProperty().bind(status);
        view.sbIP.setText(ip);
    }

    //todo switch to StringBinds
    public void displayInfo1(String info){
        view.sbInfo1.setText(" | " + info);
    }

    public void displayInfo2(String info){
        view.sbInfo2.setText(" | " + info);
    }

    private void updatePublicChatrooms(){
        sendMessage("ListChatrooms", model.token);
        if(getSuccess()){
            model.publicChatrooms = getList();
        }else{

        }
    }

    private void printAnswer(){System.out.println(model.lastAnswer);}

    private void SettingsEventHandlers() {
        view.mvServerConnect.setOnAction(e -> { //Server Connect
            String ip = view.mvServerIPInput.getText();
            String port = view.mvServerPortInput.getText();
            if(validateIpAddress(ip)
                    && validatePortNumber(port)){
                connectToServer(ip, Integer.parseInt(port));
            }
        });
        view.mvCreateLogin.setOnAction(e -> { //Create Login
            String[] data = createLogin.display();
            sendMessage("CreateLogin", data[0], data[1]);

            if(getSuccess()){

            }
            else{

            }
        });
        view.mvLogin.setOnAction(e -> { //Login
            String[] data = login.display();
            String user = data[0];
            String pass = data[1];


            sendMessage("Login", user, pass);
            if(getSuccess()){
                setToken();
                model.currentUser=user;
                model.loggedIn = true;
                System.out.println(model.token);
            }else{
                model.loggedIn = false;
                System.out.println("FAILED!");
            }
        });
        view.mvJoinChatroom.setOnAction(e -> { //Join Chatroom
            updatePublicChatrooms();
            String[] data = joinChatroom.display(model.publicChatrooms);
            String choice = data[0];


            sendMessage("JoinChatroom", model.token, choice, model.currentUser);
            if(getSuccess()){
                model.joinedRooms.add(choice);
            }else{

            }
        });




        view.mvLogout.setOnAction(e -> { //Logout
            sendMessage("Logout");
            getSuccess(); //Always true
            model.loggedIn = false;
        });

    }

    private void windowEventHandlers(){
        //Window Event Handlers
        view.wbClose.setOnAction(e -> {
            view.stop();
            if(t!=null){t.stop();}
            if(t1!=null){t1.stop();}
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            Platform.exit();
        });
        view.wbMinimize.setOnAction(e -> view.stage.setIconified(true));
        view.wbMaximize.setOnAction(e -> {
            model.maximized = !model.maximized;
            view.stage.setMaximized(model.maximized);

        });

        //Make Window Draggable
        //derived from https://stackoverflow.com/questions/13206193/how-to-make-an-undecorated-window-movable-draggable-in-javafx
        view.windowBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        view.windowBar.setOnMouseDragged(e -> {
            if(!model.maximized) {
                view.stage.setX(e.getScreenX() - xOffset);
                view.stage.setY(e.getScreenY() - yOffset);
            }
        });


        //EventHandler for Settings Window
        view.wbHamMenu.setOnAction(e -> {
            model.settingsOpen = !model.settingsOpen;
            if(model.settingsOpen){
                view.root.setLeft(view.menuView);
                view.wbHamMenu.setGraphic(new ImageView(new Image("assets/HamMenuOpen.png")));
            }else{
                view.root.setLeft(view.chatView);
                view.wbHamMenu.setGraphic(new ImageView(new Image("assets/HamMenu.png")));
            }
        });

        //Language Change Handlers
        view.mvToDE.setOnAction(e -> {
            transl.setLocale(Locale.GERMAN);
            //System.out.println("GERMAN");
        });
        view.mvToEN.setOnAction(e -> {
            transl.setLocale(Locale.ENGLISH);
            //System.out.println("ENGLISH");
        });

    }

    public StringBinding getBind(String key){
        return transl.createStringBinding(key);
    }

    public String getString(String key){
        return transl.get(key);
    }

}
