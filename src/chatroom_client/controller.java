package chatroom_client;




import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Locale;

import static chatroom_client.transl.get;

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
        sendingEventHandler();
        //connectToServer();
        //startThreadListener();
        //startStates();




    }
    //Manage States when not connected in a thread
    private void startStates() {
        view.mvLogin.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            return (!model.connected);
        }));


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
                } catch (IOException e) {
                    break;
                }
                if (msg == null) {
                    model.connected = false;
                    break; // In case the server closes the socket
                }
                Platform.runLater(() -> {
                    displayInfo2(msg);
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
            Platform.runLater(() -> {
                alertBox.display(parts[1]);
            });
        }else if(MessageType.equals("MessageText")){
            Platform.runLater(() -> {
                view.cMessagesContainer.getChildren().add(new message(parts[3], parts[2], true));
            });


        }
    }

    private void resetLastAnswer(){model.lastAnswer="";}

    private void wait(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean getSuccess(){
        while(model.isAnswerReady()){
            wait(10);
        } //wait for message to come in
        wait(100);
        String[] parts = model.lastAnswer.split("\\|");
        if(parts.length==2){
            resetLastAnswer();
            //System.out.println("ResetAnswer");
        }//reset lastAnswer when only checking for success  bool
        displayInfo2(parts.toString());
        return Boolean.parseBoolean(parts[1]);

    }

    private void setToken(){
        wait(100);
        String[] parts = model.lastAnswer.split("\\|");
        //System.out.println(parts.toString());

        model.token=parts[2];
        resetLastAnswer(); //reset lastAnswer to null
    }

    private String[] getList(){
        wait(100);
        String[] parts = model.lastAnswer.split("\\|"); //May throw nullpointer Exception

        int listItems = parts.length-2;
        String[] list = new String[listItems];
        for(int i = 0; i<listItems;i++){
            list[i] = parts[i+2];
        }
        resetLastAnswer(); //reset lastAnswer to null
        return list;

    }

    /**
     * Tests Connection after socket has been established
     * ONLY SENDS A PING
     * @return boolean result
     */
    private boolean testConnection(){
        boolean result = false;

        try{
            sendString("Ping");
            if(getSuccess()){result=true;}
        } catch (Exception ex){
            alertBox.display("No Connection");
            displayStatus(getBind("null"), "");
        }


        return result;
    }

    private boolean connectToServer(String ip, int port) { //No SSL
        try {

            displayStatus(getBind("ConnectingStatus"), ip);

            socket = new Socket(ip, port);
            startThreadListener();

            if(testConnection()) {
                model.connected = true;
                model.ipAddress = ip;
                model.portNumber = port;
                displayStatus(getBind("ConnectedStatus"), ip);
            }




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

        }catch (IOException ex){
            displayInfo2("FAIL");
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
        wait(100);
        sendMessage("ListChatrooms", model.token);
        if(getSuccess()){
            model.publicChatrooms = getList();
        }
    }

    private void printAnswer(){displayInfo2(model.lastAnswer);}

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
            getSuccess();
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
                displayInfo2(model.token);
            }else{
                model.loggedIn = false;
                displayInfo2("FAILED!");
            }
        });
        view.mvJoinChatroom.setOnAction(e -> { //Join Chatroom
            updatePublicChatrooms();
            String[] data = chatroomJoin.display(model.publicChatrooms);
            String choice = data[0];
            if (choice.equals(" ")) {model.joinedRooms.add(choice);}


            sendMessage("JoinChatroom", model.token, choice, model.currentUser);
            if(getSuccess()){

            }else{

            }

        });
        view.mvLeaveChatoom.setOnAction(e -> { //Leave Chatroom
            String[] data = chatroomLeave.display(model.joinedRooms.toArray(new String[0]));
            String choice = data[0];
            if(choice!=null || !choice.equals(" ")){model.joinedRooms.remove(choice);}

            sendMessage("LeaveChatroom", model.token, choice, model.currentUser);
            if(getSuccess()){

            }else{

            }
        });
        view.mvCreateChatroom.setOnAction(e -> { //Create Chatroom
            String[] data = chatroomCreate.display();
            String choice = data[0];
            String isPublic = data[1];

            sendMessage("CreateChatroom", model.token, choice, isPublic);
            if(getSuccess()){

            }else{

            }
        });
        view.mvDeleteChatroom.setOnAction(e -> { //Delete Chatroom
            updatePublicChatrooms();
            String[] data = chatroomDelete.display(model.publicChatrooms);
            String choice = data[0];
            if(choice!=null){model.joinedRooms.remove(choice);}


            sendMessage("DeleteChatroom", model.token, choice);
            if(getSuccess()){

            }else{

            }
            //TODO ERROR MESSAGES -> ONLY CREATOR CAN DELETE...
        });
        view.mvChangePassword.setOnAction(e -> { //ChangePassword
            String[] data = changePassword.display();
            String newPass = data[0];
            sendMessage("ChangePassword", model.token, newPass);
            if(getSuccess()){

            }else{

            }
        });
        view.mvDeleteLogin.setOnAction(e -> { //Delete Login
            if(confirmBox.display()){sendMessage("DeleteLogin", model.token);}
            if(getSuccess()){

            }else{
                alertBox.display(get("TokenInvalid"));
            }


        });


        view.mvLogout.setOnAction(e -> { //Logout
            sendMessage("Logout");
            getSuccess(); //Always true
            model.loggedIn = false;
            model.token=null;
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
                //view.wbHamMenu.setGraphic(new ImageView(new Image("assets/HamMenuOpen.png")));
                view.wbHamMenu.setText("|conversations|");
            }else{
                view.root.setLeft(view.chatView);
                //view.wbHamMenu.setGraphic(new ImageView(new Image("assets/HamMenu.png")));
                view.wbHamMenu.setText("|menu|");

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

    public void sendingEventHandler(){
        view.cSend.setOnAction(e -> {
            String text = view.cTextField.getText();
            view.cMessagesContainer.getChildren().add(new message(text, model.currentUser, false));
        });
    }

    public StringBinding getBind(String key){
        return transl.createStringBinding(key);
    }

    public String getString(String key){
        return get(key);
    }

}
