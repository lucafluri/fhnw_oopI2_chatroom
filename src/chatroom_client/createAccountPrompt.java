package chatroom_client;

import chatroom_client.utils.transl;
import chatroom_client.model.client_model;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class createAccountPrompt {
    //vars
    client_model model;

    static String[] data = new String[2];

    public static String[] display(){
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Create new Account");
        Label usernameLabel = new Label(transl.get("newUsername"));
        TextField usernameInput = new TextField();
        Label passwordLabel = new Label(transl.get("newPassword"));
        TextField passwordInput = new TextField();
        Button OK = new Button(transl.get("ok"));
        Button Cancel = new Button(transl.get("cancel"));
        HBox Buttons = new HBox();
        Buttons.setPadding(new Insets(10));

        Buttons.getChildren().addAll(OK, Cancel);

        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, Buttons);

        OK.setOnAction(e -> {
            data[0] = usernameInput.getText();
            data[1] = passwordInput.getText();
            stage.close();
        });
        Cancel.setOnAction(e -> {
            data = new String[2];
            stage.close();
        });

        Scene scene = new Scene(layout, 200, 200);
        scene.getStylesheets().add("chatroom_client/view/styles.css");
        stage.setScene(scene);
        stage.showAndWait();



        return data;

    }

}
