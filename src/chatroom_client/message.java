package chatroom_client;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class message extends HBox {
    String msg;
    String sender;
    String target;
    boolean received;

    public message(String msg, String sender, String target, boolean received){
        this.msg = msg;
        this.sender = sender;
        this.target = target;
        this.received = received;


        Label senderLabel = new Label(sender);
        Label message = new Label(msg);
        Region region = new Region();
        VBox messageBox = new VBox();

        messageBox.getChildren().addAll(senderLabel, message);
        this.setHgrow(region, Priority.ALWAYS);

        if(received){
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }else{
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        }


        senderLabel.setId("SenderLabel");
        this.setId("messageHBox");
        this.setAlignment(Pos.CENTER);
        senderLabel.setFont(Font.font("Arial", FontWeight.BLACK, 12));


        if(received){
            messageBox.setId("messageReceived");
            this.getChildren().addAll(messageBox, region);

        } else{
            messageBox.setId("messageSent");
            this.getChildren().addAll(region, messageBox);
        }




    }
}
