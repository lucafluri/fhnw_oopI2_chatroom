package chatroom_client;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class message extends HBox {
    String msg;
    String sender;
    boolean received;

    public message(String msg, String sender, boolean received){
        this.msg = msg;
        this.sender = sender;
        this.received = received;


        Label senderLabel = new Label(sender);
        Label message = new Label(msg);
        Region region = new Region();
        VBox messageBox = new VBox();

        messageBox.getChildren().addAll(senderLabel, message);
        this.setHgrow(region, Priority.ALWAYS);



        if(received){

            messageBox.setId("messageReceived");
            this.getChildren().addAll(messageBox, region);

        } else{
            messageBox.setId("messageSent");

            this.getChildren().addAll(region, messageBox);
        }




    }
}
