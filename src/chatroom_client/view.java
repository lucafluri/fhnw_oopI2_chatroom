package chatroom_client;



import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class view {
    //Main Elements
    public model model;
    public Stage stage;

    public BorderPane root = new BorderPane();
    public ScrollPane chatView = new ScrollPane(); //cv
    public ScrollPane menuView = new ScrollPane(); //mv
    public HBox windowBar = new HBox(); //wb
    public HBox statusbar = new HBox(); //sb
    public VBox center = new VBox(); //c

    //CenterElements
    public ScrollPane cMessagesScroll = new ScrollPane();
    public VBox cMessagesContainer = new VBox();
    public HBox cControls = new HBox();
    public TextField cTextField = new TextField();
    public JFXButton cSend = new JFXButton();
    //TODO MESSAGE CLASS AND TESTS

    //ChatView Elements
    public VBox cvContainerLeft = new VBox();
    public JFXButton cvTestConv = new JFXButton("TEST CONVERSATION");

    //MenuView Elements
    public VBox mvContainerLeft = new VBox();
    public JFXButton mvSetting1 = new JFXButton();
    public JFXButton mvSetting2 = new JFXButton();
    public JFXButton mvSetting3 = new JFXButton();
    public JFXButton mvSetting4 = new JFXButton();
    public JFXButton mvSetting5 = new JFXButton();
    public JFXButton mvSetting6 = new JFXButton();
    public JFXButton mvSetting7 = new JFXButton();
    public ChoiceBox dropDownRooms = new ChoiceBox();
    public ChoiceBox dropDownR = new ChoiceBox();
    public HBox mvLangs = new HBox();
    public JFXButton mvToEN = new JFXButton("EN");
    public JFXButton mvToDE = new JFXButton("DE");



    //Windowbar Elements
    public Button wbHamMenu;
    public Button wbMinimize;
    public Button wbMaximize;
    public Button wbClose;
    public Region wbSpacer;

    //Statusbar Elements
    public Label sbServerStatus = new Label();
    public Label sbIP = new Label();
    public Label sbInfo1 = new Label();


    /**
     * Set any options for the stage in the subclass constructor
     *
     * @param stage
     * @param model
     */
    public view(Stage stage, model model) {
        this.model = model;
        this.stage = stage;

        stage.initStyle(StageStyle.UNDECORATED);


        setIDs();

        //setup all views
        setWindowBar();
        setChatView();
        setCenterView();
        setMenuView();
        setStatusbarView();

        root.setLeft(chatView);
        root.setTop(windowBar);
        root.setCenter(center);
        root.setBottom(statusbar);

        Scene scene = new Scene(root, 750, 500);
        scene.getStylesheets().add("chatroom_client/styles.css");


        stage.setScene(scene);


    }



    private void setStatusbarView() {
        statusbar.getChildren().addAll(sbServerStatus, sbIP, sbInfo1);
    }

    private void setCenterView() {
        cSend.textProperty().bind(getBind("send"));


        cMessagesScroll.setContent(cMessagesContainer);
        cControls.getChildren().addAll(cTextField, cSend);
        center.getChildren().addAll(cMessagesScroll, cControls);

    }

    private void setChatView() {
        cvContainerLeft.getChildren().addAll(cvTestConv);
        chatView.setContent(cvContainerLeft);

    }

    private void setMenuView() {
        mvSetting1.textProperty().bind(getBind("CreateAccount"));
        mvSetting2.textProperty().bind(getBind("Login"));
        mvSetting3.textProperty().bind(getBind("JoinChatroom"));
        mvSetting4.textProperty().bind(getBind("CreateChatroom"));
        mvSetting5.textProperty().bind(getBind("ChangePassword"));
        mvSetting6.textProperty().bind(getBind("DeleteAccount"));
        mvSetting7.textProperty().bind(getBind("Logout"));


        mvLangs.getChildren().addAll(mvToEN, mvToDE);
        mvContainerLeft.getChildren().addAll(mvSetting1, mvSetting2, mvSetting3, mvSetting4, mvSetting5, mvSetting6, mvSetting7, mvLangs);
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


    public StringBinding getBind(String key){
        return transl.createStringBinding(key);
    }

    public String getString(String key){
        return transl.get(key);
    }

    public void start(){ stage.show();}
    public void stop() {
        stage.hide();
    }
    public Stage getStage() {
        return stage;
    }

    private void setIDs() {
        root.setId("root");
        chatView.setId("chatView");
        menuView.setId("menuView");
        windowBar.setId("windowBar");
        statusbar.setId("statusbar");
        center.setId("center");
        cMessagesScroll.setId("cMessagesScroll");
        cMessagesContainer.setId("cMessagesContainer");
        cControls.setId("cControls");
        //TODO CONTINUE ADDING ID's!!!
    }
}
