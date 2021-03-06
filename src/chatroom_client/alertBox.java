package chatroom_client;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;

import java.awt.*;

public class alertBox {

    public static void display(String text){

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.setTitle(transl.get("Confirm"));
        Label label = new Label(text);
        Button OK = new Button(transl.get("ok"));
        HBox Buttons = new HBox();
        Buttons.setPadding(new Insets(10));

        Buttons.getChildren().addAll(OK);

        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, Buttons);
        label.setWrapText(true);





        //Event Handlers
        OK.setOnAction(e -> {

            stage.close();
        });



        layout.getStyleClass().add("background");
        Scene scene = new Scene(layout, 200, 150, Color.BLACK);
        scene.getStylesheets().add("chatroom_client/styles.css");
        stage.setScene(scene);
        stage.showAndWait();


    }

}
