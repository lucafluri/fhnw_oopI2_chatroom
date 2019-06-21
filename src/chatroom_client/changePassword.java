package chatroom_client;


import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class changePassword {
    static String[] data = new String[1];

    public static String[] display(){
        data[0] = " ";


        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(transl.get("ChangePassword"));
        Label passwordLabel = new Label(transl.get("newPassword")); //TODO add password stars and option to show plain text
        TextField passwordInput = new TextField();
        Button OK = new Button(transl.get("ok"));
        Button Cancel = new Button(transl.get("cancel"));
        HBox Buttons = new HBox();
        Buttons.setPadding(new Insets(10));

        Buttons.getChildren().addAll(OK, Cancel);

        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(passwordLabel, passwordInput, Buttons);





        //LOGIC
        //Disable Binding
        BooleanBinding validInputBinding = new BooleanBinding() {
            {
                super.bind(passwordInput.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (passwordInput.getText().length() < 3);
            }
        };

        OK.disableProperty().bind(validInputBinding);

        //Event Handlers
        OK.setOnAction(e -> {

            String pass = passwordInput.getText();

            data[0] = pass;
            stage.close();
        });
        Cancel.setOnAction(e -> {
            data[0] = " ";
            stage.close();
        });





        layout.getStyleClass().add("background");
        Scene scene = new Scene(layout, 200, 200, Color.BLACK);
        scene.getStylesheets().add("chatroom_client/styles.css");
        stage.setScene(scene);
        stage.showAndWait();

        return data;

    }

}
