package chatroom_client;


import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class chatroomCreate {
    static String[] data = new String[2];

    public static String[] display(){
        data[0] = " ";
        data[1] = " ";

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(transl.get("CreateChatroom"));
        Label nameLabel = new Label(transl.get("ChatroomName"));
        TextField nameInput = new TextField();
        Label publicLabel = new Label(transl.get("isPublic"));
        JFXToggleButton isPublic = new JFXToggleButton();
        Button OK = new Button(transl.get("ok"));
        Button Cancel = new Button(transl.get("cancel"));
        HBox Buttons = new HBox();
        Buttons.setPadding(new Insets(10));

        Buttons.getChildren().addAll(OK, Cancel);

        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(nameLabel, nameInput, publicLabel, isPublic, Buttons);





        //LOGIC
        //Disable Binding
        BooleanBinding validInputBinding = new BooleanBinding() {
            {
                super.bind(nameInput.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (nameInput.getText().length() < 3);
            }
        };

        OK.disableProperty().bind(validInputBinding);

        //Event Handlers
        OK.setOnAction(e -> {
            String username = nameInput.getText();
            boolean publicState = isPublic.isSelected();

            data[0] = username;
            data[1] = String.valueOf(publicState);
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
