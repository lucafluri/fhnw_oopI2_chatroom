package chatroom_client;




import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.Control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

import static chatroom_client.transl.get;

public class controller {
    private final view view;
    private final model model;
    private double xOffset = 0;
    private double yOffset = 0;
    private Socket socket = null;
    private BufferedReader socketIn;
    private OutputStreamWriter socketOut;
    private Runnable r, r1, r2;
    private Thread t, t1, t2;



    public controller(model model, view view) throws IOException {
        this.model = model;
        this.view = view;

        windowEventHandlers();
        SettingsEventHandlers();
        sendingEventHandler();
        //connectToServer();
        //startThreadListener();
        startStates();
        startConversationsListener();

        displayInfo2(getString("LoggedOut"));


    }

    private void disable(Control... controls){
        for(Control c: controls){
            if(!c.isDisabled()){
                c.setDisable(true);
            }
        }
    }

    private void enable(Control... controls){
        for(Control c: controls){
            if(c.isDisabled()){
                c.setDisable(false);
            }
        }
    }

    //Manage States in a thread
    private void startStates() {
        r1 = () -> {
            while(true) {
                try { //Only check each 100ms
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //CheckConnection State
                //If not Connected
                if (model.connected) {
                    disable(view.mvServerConnect,
                            view.mvServerIPInput,
                            view.mvServerPortInput);
                    enable(view.mvServerDisconnect,
                            view.mvCreateLogin,
                            view.mvLogin);
                    if(model.loggedIn){ //Not Logged In
                        disable(view.mvLogin);
                        enable(view.mvJoinChatroom,
                                view.mvLeaveChatoom,
                                view.mvCreateChatroom,
                                view.mvDeleteChatroom,
                                view.mvChangePassword,
                                view.mvDeleteLogin,
                                view.mvLogout,
                                view.cSend,
                                view.cTextField);

                        Platform.runLater(() -> {
                            displayInfo2(getString("LoggedInAs") + " " + model.currentUser);
                        });
                    }else{
                        enable(view.mvLogin);
                        disable(view.mvJoinChatroom,
                                view.mvLeaveChatoom,
                                view.mvCreateChatroom,
                                view.mvDeleteChatroom,
                                view.mvChangePassword,
                                view.mvDeleteLogin,
                                view.mvLogout,
                                view.cSend,
                                view.cTextField);
                        Platform.runLater(() -> {
                            displayInfo2(getString("LoggedOut"));
                        });

                    }



                } else{
                    //Not Connected
                    disable(view.mvServerDisconnect,
                            view.mvCreateLogin,
                            view.mvLogin,
                            view.mvJoinChatroom,
                            view.mvLeaveChatoom,
                            view.mvCreateChatroom,
                            view.mvDeleteChatroom,
                            view.mvChangePassword,
                            view.mvDeleteLogin,
                            view.mvLogout,
                            view.cSend,
                            view.cTextField);
                    enable(view.mvServerConnect,
                            view.mvServerIPInput,
                            view.mvServerPortInput);

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
                    if (msg == null) {
                        model.connected = false;
                        break; // In case the server closes the socket
                    }

                    processMsg(msg);
                } catch (IOException e) {
                    break;
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

    private void startConversationsListener() {
        // Create thread to read incoming messages
        r2 = () -> {
            while (true) {
                wait(100);

                Platform.runLater(() -> {
                    conversationButtonsHandlers();
                    view.cMessagesContainer.getChildren().clear();
                    if(model.messages.containsKey(model.currentChatroom)){
                        view.cMessagesContainer.getChildren().addAll(model.messages.get(model.currentChatroom));
                    }else{ //Key not yet in map
                        model.messages.put(model.currentChatroom, new ArrayList());
                    }


                });
            }
        };
        t2 = new Thread(r2);
        t2.start();
    }

    private void stopConversationsListener(){
        try{
            t2.stop();
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


        //wait(100);
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
                //TODO STORE MESSAGES, SORTED AFTER TARGET
                String Message = parts[3];
                String Sender = parts[1];
                String Target = parts[2];
                if(!Sender.equals(model.currentUser)) {
                    //view.cMessagesContainer.getChildren().add(new message(message, Sender, Target, true));
                    model.messages.get(Target).add(new message(Message, Sender, Target, true));
                }
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
        System.out.println("Success checking...");
        while(model.isAnswerReady() && model.lastAnswer.split("\\|").length!=1){
            wait(10);
        } //wait for message to come in
        wait(100);
        String[] parts = model.lastAnswer.split("\\|");
        if(parts.length==2){
            resetLastAnswer();
            //System.out.println("ResetAnswer");
        }//reset lastAnswer when only checking for success  bool
        else if(parts.length<=1){
            System.out.println(parts[0]);
            //alertBox.display("Error, Please Try Again");
            resetLastAnswer();
            return false;
        }
        displayInfo1(parts.toString());
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
        }

    }

    private void logout(){
        model.loggedIn=false;
        model.currentUser="";
        model.token="";
        model.currentChatroom="";
        model.joinedRooms.clear();
        updateJoinedChatrooms();
        displayInfo2(getString("LoggedOut"));
    }

    private void disconnect(){
        try {
            socket.close();
            stopThreadListener();
            model.connected=false;
            logout();
            model.lastAnswer="";
            displayStatus(getBind("Disconnected"), "");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private void setReaderWriters(Socket socket) {
        try {
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOut = new OutputStreamWriter(socket.getOutputStream());

        }catch (IOException ex){
            displayInfo1("FAIL");
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
        view.sbInfo2.setText(info);
    }

    private void updatePublicChatrooms(){
        wait(100);
        sendMessage("ListChatrooms", model.token);
        if(getSuccess()){
            model.publicChatrooms = getList();
            for(String room : model.publicChatrooms){
                model.joinableRooms.add(room);
                if(model.joinedRooms.contains(room)){
                    model.joinableRooms.remove(room);
                }
            }
        }
    }

    private void updateJoinedChatrooms() {
        view.cvConversationButtons.clear();
        for(String room : model.joinedRooms){
            view.cvConversationButtons.add(new JFXButton(room));
        }
        view.cvContainerLeft.getChildren().clear();
        view.cvContainerLeft.getChildren().addAll(view.cvConversationButtons);

            //view.chatView.setContent(view.cvContainerLeft);


    }

    private void printAnswer(){displayInfo1(model.lastAnswer);}

    private void SettingsEventHandlers() {
        view.mvServerConnect.setOnAction(e -> { //Server Connect
            String ip = view.mvServerIPInput.getText();
            String port = view.mvServerPortInput.getText();
            if(validateIpAddress(ip)
                    && validatePortNumber(port)){
                connectToServer(ip, Integer.parseInt(port));
            }
        });

        view.mvServerDisconnect.setOnAction(e -> { //Server Disconnect
            disconnect();
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
                displayInfo2(getString("LoggedInAs") + " " + model.currentUser);
            }else{
                model.loggedIn = false;
                displayInfo1("FAILED!");
            }
        });
        view.mvJoinChatroom.setOnAction(e -> { //Join Chatroom
            updatePublicChatrooms();
            String[] data = chatroomJoin.display(model.joinableRooms.toArray(new String[0]));
            String choice = data[0];

            if(choice!=null && choice.length() > 1){
                // System.out.println("Choice: |" + choice + "|");
                sendMessage("JoinChatroom", model.token, choice, model.currentUser);
                if(getSuccess()){
                    model.joinedRooms.add(choice);
                    updateJoinedChatrooms();
                    System.out.println(view.cvConversationButtons.toString());
                }else{

                }
            }


        });
        view.mvLeaveChatoom.setOnAction(e -> { //Leave Chatroom
            String[] data = chatroomLeave.display(model.joinedRooms.toArray(new String[0]));
            String choice = data[0];
            if(choice!=null && choice.length() > 1){
                sendMessage("LeaveChatroom", model.token, choice, model.currentUser);
                if(getSuccess()){
                    model.joinedRooms.remove(choice);
                    updateJoinedChatrooms();

                }else{

                }
            }



        });
        view.mvCreateChatroom.setOnAction(e -> { //Create Chatroom
            String[] data = chatroomCreate.display();
            String choice = data[0];
            String isPublic = data[1];

            if(choice!=null && !choice.equals(" ")){sendMessage("CreateChatroom", model.token, choice, isPublic);}
            if(getSuccess()){

            }else{

            }
        });
        view.mvDeleteChatroom.setOnAction(e -> { //Delete Chatroom
            updatePublicChatrooms();
            String[] data = chatroomDelete.display(model.publicChatrooms);
            String choice = data[0];
            if(choice!=null && !choice.equals(" ")){model.joinedRooms.remove(choice);}


            sendMessage("DeleteChatroom", model.token, choice);
            if(getSuccess()){

            }else{

            }
            //TODO ERROR MESSAGES -> ONLY CREATOR CAN DELETE...
        });
        view.mvChangePassword.setOnAction(e -> { //ChangePassword
            String[] data = changePassword.display();
            String newPass = data[0];
            if(newPass.length()>=3){sendMessage("ChangePassword", model.token, newPass);}

            if(getSuccess()){

            }else{

            }
        });
        view.mvDeleteLogin.setOnAction(e -> { //Delete Login
            if(confirmBox.display()){sendMessage("DeleteLogin", model.token);}
            if(getSuccess()){
                logout();
            }else{
                //alertBox.display(get("TokenInvalid"));
            }


        });


        view.mvLogout.setOnAction(e -> { //Logout
            sendMessage("Logout");
            getSuccess(); //Always true
            logout();

        });

    }

    private void windowEventHandlers(){
        //Window Event Handlers
        view.wbClose.setOnAction(e -> {
            view.stop();
            if(t!=null){t.stop();}
            if(t1!=null){t1.stop();}
            if(t2!=null){t2.stop();}
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
                view.wbHamMenu.setText("|<--|");
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
            if(getSuccess()){

            }else{
                
            }

            //view.cMessagesContainer.getChildren().add(new message(text, model.currentUser, model.currentChatroom,false));
            if(model.messages.containsKey(model.currentChatroom)) {
                model.messages.get(model.currentChatroom).add(new message(text, model.currentUser, model.currentChatroom, false));
            }
        });
    }

    private void conversationButtonsHandlers(){
        for(JFXButton convButton : view.cvConversationButtons){
            convButton.setOnAction(e -> {
                model.currentChatroom = convButton.getText();
            });
            if(convButton.getText().equals(model.currentChatroom)){
                convButton.setDisable(true);
            }
            else{
                convButton.setDisable(false);
            }
        }
    }

    public StringBinding getBind(String key){
        return transl.createStringBinding(key);
    }

    public String getString(String key){
        return get(key);
    }

}
