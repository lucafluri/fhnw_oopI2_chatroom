package chatroom_client;

import chatroom_client.controller.client_controller;
import chatroom_client.model.client_model;
import chatroom_client.view.client_view;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class chatroom_client extends Application {
    //MVC Stuff
    client_model model;
    client_view view;
    client_controller controller;


    @Override
    public void start(Stage primaryStage) throws Exception{
       //Init
        model = new client_model();
        view = new client_view(primaryStage, model);
        controller = new client_controller(model, view);

        view.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}