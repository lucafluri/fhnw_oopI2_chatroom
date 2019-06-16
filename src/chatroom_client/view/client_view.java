package chatroom_client.view;

import chatroom_client.model.client_model;
import javafx.scene.Scene;
import javafx.stage.Stage;
import chatroom_client.abstractClasses.*;

public class client_view extends View<client_model>{
    private client_model model;
    private Stage stage;

    /**
     * Set any options for the stage in the subclass constructor
     *
     * @param stage
     * @param model
     */
    protected client_view(Stage stage, Model model) {
        super(stage, model);
    }

    @Override
    protected Scene create_GUI() {
        return null;
    }
}
