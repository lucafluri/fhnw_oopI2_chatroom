package chatroom_client;


import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class chatroomJoin {
    static String[] data = new String[1];

    public static String[] display(String[] publicChatrooms){
        data[0] = "";

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(transl.get("JoinChatroom"));
        Label chooseLabel = new Label();
        ChoiceBox<String> publicServerList = new ChoiceBox<>();
        Button Join = new Button(transl.get("Join"));
        Button Cancel = new Button(transl.get("cancel"));
        HBox Buttons = new HBox();
        Buttons.setPadding(new Insets(10));

        Buttons.getChildren().addAll(Join, Cancel);

        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(chooseLabel, publicServerList, Buttons);





        //LOGIC

        publicServerList.setItems(FXCollections.observableArrayList(publicChatrooms));
        publicServerList.setValue(publicChatrooms[0]);


        //Event Handlers
        Join.setOnAction(e -> {
            data[0] = publicServerList.getValue();
            stage.close();
        });
        Cancel.setOnAction(e -> {
            data = new String[1];
            stage.close();
        });






        Scene scene = new Scene(layout, 200, 200);
        scene.getStylesheets().add("chatroom_client/styles.css");
        stage.setScene(scene);
        stage.showAndWait();

        return data;

    }

}
