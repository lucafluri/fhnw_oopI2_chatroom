package chatroom_client;




import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
        connectToServer();
        startThreadListener();
        startThreadStates();




    }
    //Manage States when not connected in a thread
    private void startThreadStates() {
        r1 = () -> {
            while(true) {
                //If not Connected
                if (checkConnection()) {
                    //TODO States to active
                    view.mvSetting1.setDisable(false);
                    model.connected = true;
                } else {
                    //TODO States to disabled
                    view.mvSetting1.setDisable(true);
                    model.connected = false;
                }
            }
        };
        t1 = new Thread(r1);
        t1.start();
    }

    //Derived from TestClient Example
    private void startThreadListener() {
        setReaderWriters(socket);
        // Create thread to read incoming messages
        r = () -> {
            while (true) {
                String msg;
                try {
                    msg = socketIn.readLine();
                    System.out.println("Received: " + msg);
                } catch (IOException e) {
                    break;
                }
                if (msg == null) break; // In case the server closes the socket

                Platform.runLater(() -> {
                    displayInfo1(msg);
                    view.cMessagesContainer.getChildren().add(new Label(msg));
                });
            }
        };
        t = new Thread(r);
        t.start();
    }


    private boolean connectToServer() { //No SSL
        try {
            displayStatus(getBind("ConnectingStatus"), model.ipAddress);
            socket = new Socket(model.ipAddress, model.portNumber);
            //setReaderWriters(socket);
            System.out.println("Connected");
            displayStatus(getBind("ConnectedStatus"), model.ipAddress);
            model.connected = true;
            return true;
        }catch (IOException e){
            displayStatus(getBind("ConnectingFail"), model.ipAddress);
            model.connected = false;
            return false;
        }finally {

        }

    }

    private boolean checkConnection(){
       return socket.isConnected();
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

    private void SettingsEventHandlers() {
        view.mvSetting1.setOnAction(e -> {
            String[] data = createLogin.display();
            sendMessage("CreateLogin", data[0], data[1]);
            //System.out.println("SENT");

        });
    }

    public void displayStatus(StringBinding status, String ip){
        view.sbServerStatus.textProperty().bind(status);
        view.sbIP.setText(ip);
    }

    //todo switch to StringBinds
    public void displayInfo1(String info){
        view.sbInfo1.setText(" | " + info);
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
