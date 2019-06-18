package chatroom_client.controller;



import chatroom_client.model.client_model;
import chatroom_client.view.client_view;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class client_controller  {
    private final client_view view;
    private final client_model model;
    private double xOffset = 0;
    private double yOffset = 0;

    public client_controller(client_model model, client_view view) {
        this.model = model;
        this.view = view;

        windowEventHandlers();




    }



    private void windowEventHandlers(){
        //Window Event Handlers
        view.wbClose.setOnAction(e -> Platform.exit());
        view.wbMinimize.setOnAction(e -> view.stage.setIconified(true));
        view.wbMaximize.setOnAction(e -> {
            model.maximized = !model.maximized;
            view.stage.setMaximized(model.maximized);

        });

        //Make Window Draggable
        //derived from https://stackoverflow.com/questions/13206193/how-to-make-an-undecorated-window-movable-draggable-in-javafx
        view.windowBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        view.windowBar.setOnMouseDragged(e -> {
            view.stage.setX(e.getScreenX() - xOffset);
            view.stage.setY(e.getScreenY() - yOffset);
        });


        //EventHandler for Settings Window
        view.wbHamMenu.setOnAction(e -> {
            model.settingsOpen = !model.settingsOpen;
            if(model.settingsOpen){
                view.root.setLeft(view.menuView);
                view.wbHamMenu.setGraphic(new ImageView(new Image("assets/HamMenuOpen.png")));
            }else{
                view.root.setLeft(view.chatView);
                view.wbHamMenu.setGraphic(new ImageView(new Image("assets/HamMenu.png")));
            }
        });

    }
}
