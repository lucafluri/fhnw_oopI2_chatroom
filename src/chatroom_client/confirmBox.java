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

public class confirmBox {
    static boolean data = false;

    public static boolean display(){
        data = false;

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(transl.get("Confirm"));
        Button OK = new Button(transl.get("ok"));
        Button Cancel = new Button(transl.get("cancel"));
        HBox Buttons = new HBox();
        Buttons.setPadding(new Insets(10));

        Buttons.getChildren().addAll(OK, Cancel);

        VBox layout = new VBox();
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(Buttons);






        //Event Handlers
        OK.setOnAction(e -> {

            data = true;
            stage.close();
        });
        Cancel.setOnAction(e -> {
            data = false;
            stage.close();
        });






        Scene scene = new Scene(layout, 200, 200);
        scene.getStylesheets().add("chatroom_client/styles.css");
        stage.setScene(scene);
        stage.showAndWait();

        return data;

    }

}
