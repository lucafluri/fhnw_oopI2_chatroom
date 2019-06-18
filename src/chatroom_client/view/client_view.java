package chatroom_client.view;


import chatroom_client.model.client_model;
import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class client_view{
    //Main Elements
    public BorderPane root;
    public client_model model;
    public Stage stage;
    public ScrollPane chatView; //cv
    public ScrollPane menuView; //mv
    public HBox windowBar; //wb
    public VBox center; //c

    //CenterElements
    public ScrollPane cMessagesScroll = new ScrollPane();
    public VBox cMessagesContainer = new VBox();
    public HBox cControls = new HBox();
    public TextArea cTextArea = new TextArea();
    public JFXButton cSend = new JFXButton("SEND");

    //ChatView Elements
    public VBox cvContainerLeft = new VBox();
    public JFXButton cvTestConv = new JFXButton("TEST CONVERSATION");

    //MenuView Elements
    public VBox mvContainerLeft = new VBox();
    public JFXButton mvSetting1 = new JFXButton("Setting 1");
    public JFXButton mvSetting2 = new JFXButton("Setting 2");
    public JFXButton mvSetting3 = new JFXButton("Setting 3");
    public HBox mvLangs = new HBox();
    public JFXButton mvToEN = new JFXButton("EN");
    public JFXButton mvToDE = new JFXButton("DE");



    //Windowbar Elements
    public Button wbHamMenu;
    public Button wbMinimize;
    public Button wbMaximize;
    public Button wbClose;
    public Region wbSpacer;


    /**
     * Set any options for the stage in the subclass constructor
     *
     * @param stage
     * @param model
     */
    public client_view(Stage stage, client_model model) {
        this.model = model;
        this.stage = stage;

        stage.initStyle(StageStyle.UNDECORATED);

        root = new BorderPane();
        windowBar = new HBox();
        chatView = new ScrollPane();
        menuView = new ScrollPane();
        center = new VBox();

        //setup all views
        setWindowBar();
        setChatView();
        setCenterView();
        setMenuView();

        root.setLeft(chatView);
        root.setTop(windowBar);
        root.setCenter(center);

        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());


        stage.setScene(scene);


    }

    private void setCenterView() {
        cMessagesScroll.setContent(cMessagesContainer);
        cControls.getChildren().addAll(cTextArea, cSend);
        center.getChildren().addAll(cMessagesScroll, cControls);

    }

    private void setChatView() {
        cvContainerLeft.getChildren().addAll(cvTestConv);
        chatView.setContent(cvContainerLeft);

    }

    private void setMenuView() {
        mvLangs.getChildren().addAll(mvToEN, mvToDE);
        mvContainerLeft.getChildren().addAll(mvSetting1, mvSetting2, mvSetting3, mvLangs);
        menuView.setContent(mvContainerLeft);


    }



    private void setWindowBar(){
        wbSpacer = new Region();
        Image menuIcon = new Image("assets/HamMenu.png");
        wbHamMenu = new Button("", new ImageView(menuIcon));
        Image minIcon = new Image("assets/minimize.png");
        wbMinimize = new Button("", new ImageView(minIcon));
        Image maxIcon = new Image("assets/maximize.png");
        wbMaximize = new Button("", new ImageView(maxIcon));
        Image closeIcon = new Image("assets/close.png");
        wbClose = new Button("", new ImageView(closeIcon));


        wbSpacer.setMinWidth(Region.USE_PREF_SIZE);
        HBox.setHgrow(wbSpacer, Priority.ALWAYS);
        windowBar.setPadding(new Insets(10));
        windowBar.setAlignment(Pos.CENTER);
        windowBar.setSpacing(10);



        windowBar.getChildren().addAll(wbHamMenu, wbSpacer, wbMinimize, wbMaximize, wbClose);
    }


    public void start(){ stage.show();}
    public void stop() {
        stage.hide();
    }
    public Stage getStage() {
        return stage;
    }
}
