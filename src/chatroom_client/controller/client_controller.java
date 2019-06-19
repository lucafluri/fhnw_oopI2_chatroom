package chatroom_client.controller;



import chatroom_client.createAccountPrompt;
import chatroom_client.model.client_model;
import chatroom_client.utils.transl;
import chatroom_client.view.client_view;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Locale;

public class client_controller  {
    private final client_view view;
    private final client_model model;
    private double xOffset = 0;
    private double yOffset = 0;
    private Socket socket = null;
    BufferedReader socketIn;
    OutputStreamWriter socketOut;
    Runnable r;
    Thread t;

    public client_controller(client_model model, client_view view) throws IOException {
        this.model = model;
        this.view = view;

        windowEventHandlers();
        SettingsEventHandlers();
        connectToServer();
        startThread();




    }

    //Derived from TestClient Example
    private void startThread() {
        r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String msg;
                    try {
                        msg = socketIn.readLine();
                        System.out.println("Received: " + msg);
                    } catch (IOException e) {
                        break;
                    }
                    if (msg == null) break; // In case the server closes the socket
                }
            }
        };
        t = new Thread(r);
        t.start();
    }


    private boolean connectToServer() throws IOException {
        try {
            displayStatus(getBind("ConnectingStatus"), model.ipAddress);
            socket = new Socket(model.ipAddress, model.portNumber);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOut = new OutputStreamWriter(socket.getOutputStream());
            System.out.println("Connected");
            displayStatus(getBind("ConnectedStatus"), model.ipAddress);
            return true;
        }catch (IOException e){
            displayStatus(getBind("ConnectingFail"), model.ipAddress);
            return false;
        }finally {

        }

    }

    private void sendString(String text) throws IOException {
        socketOut.write(text);
        socketOut.flush();
    }

    private void sendMessage(String... parts){
        try {
            for (String part : parts) {
                socketOut.write(part + "|");
            }
            socketOut.flush();
        }catch(IOException ex){
            System.out.println("IO EXCEPTION");
        }
    }

    private void SettingsEventHandlers() {
        view.mvSetting1.setOnAction(e -> {
            String[] data = createAccountPrompt.display();
            sendMessage("CreateLogin", data[0], data[1]);
        });
    }

    public void displayStatus(StringBinding status, String ip){
        view.sbServerStatus.textProperty().bind(status);
        view.sbIP.setText(ip);
    }

    //todo switch to StringBinds
    public void displayInfo1(String info){
        view.sbInfo1.setText(info);
    }


    private void windowEventHandlers(){
        //Window Event Handlers
        view.wbClose.setOnAction(e -> {
            view.stop();
            if(t!=null){t.stop();}
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
