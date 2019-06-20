package chatroom_client;


import javafx.beans.binding.BooleanBinding;
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

public class login {
    static String[] data = new String[2];

    public static String[] display(){
        data[0] = " ";
        data[1] = " ";

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(transl.get("Login"));
        Label usernameLabel = new Label(transl.get("username"));
        TextField usernameInput = new TextField();
        Label passwordLabel = new Label(transl.get("password")); //TODO add password stars and option to show plain text
        TextField passwordInput = new TextField();
        Button Login = new Button(transl.get("Login"));
        Button Cancel = new Button(transl.get("cancel"));
        HBox Buttons = new HBox();
        Buttons.setPadding(new Insets(10));

        Buttons.getChildren().addAll(Login, Cancel);

        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, Buttons);


        //Disable Binding
        BooleanBinding validInputBinding = new BooleanBinding() {
            {
                super.bind(usernameInput.textProperty(), passwordInput.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (usernameInput.getText().length() < 3 || passwordInput.getText().length() < 3);
            }
        };

        Login.disableProperty().bind(validInputBinding);


        Login.setOnAction(e -> {
            String username = usernameInput.getText();
            String pass = passwordInput.getText();
            data[0] = username;
            data[1] = pass;


            stage.close();
        });
        Cancel.setOnAction(e -> {
            data[0] = " ";
            data[1] = " ";
            stage.close();
        });

        Scene scene = new Scene(layout, 200, 200);
        scene.getStylesheets().add("chatroom_client/styles.css");
        stage.setScene(scene);
        stage.showAndWait();

        return data;


    }

}
