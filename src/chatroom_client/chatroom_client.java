package chatroom_client;


import javafx.application.Application;
import javafx.stage.Stage;


public class chatroom_client extends Application {
    //MVC Stuff
    private model model;
    private view view;
    private controller controller;


    Stage window;


    @Override
    public void start(Stage primaryStage) throws Exception{
       //Init
        window = primaryStage;
        model = new model();
        view = new view(window, model);
        controller = new controller(model, view);



        view.start();
    }

    @Override
    public void stop() {
        if (view != null) {
            view.stop();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}