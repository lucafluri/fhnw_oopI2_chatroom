package chatroom_client.view;

import chatroom_client.model.client_model;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class client_view{
    private client_model model;
    private Stage stage;
    private chatList_view chatView;


    /**
     * Set any options for the stage in the subclass constructor
     *
     * @param stage
     * @param model
     */
    public client_view(Stage stage, client_model model) {
        this.model = model;

        BorderPane root = new BorderPane();
        chatView = new chatList_view();

        root.setLeft(chatView);


    }

    public void start(){ stage.show();}

}
