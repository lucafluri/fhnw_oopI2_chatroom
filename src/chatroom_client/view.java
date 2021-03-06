package chatroom_client;



import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;

import java.util.HashSet;


public class view {
    //Main Elements
    model model;
    Stage stage;

    BorderPane root = new BorderPane();
    ScrollPane chatView = new ScrollPane(); //cv
    ScrollPane menuView = new ScrollPane(); //mv
    HBox windowBar = new HBox(); //wb
    HBox statusbar = new HBox(); //sb
    VBox center = new VBox(); //c

    //CenterElements
    ScrollPane cMessagesScroll = new ScrollPane();
    VBox cMessagesContainer = new VBox();
    HBox cControls = new HBox();
    TextField cTextField = new TextField();
    JFXButton cSend = new JFXButton();
    Label charsLeft = new Label();

    //ChatView Elements
    VBox cvContainerLeft = new VBox();
    HashSet<JFXButton> cvConversationButtons = new HashSet<>();
    JFXButton cvTestConv = new JFXButton("TEST CONVERSATION");

    //MenuView Elements
    VBox mvContainerLeft = new VBox();
    Label mvServerIPLabel = new Label();
    Label mvServerPortLabel = new Label();
    TextField mvServerIPInput = new TextField();
    TextField mvServerPortInput = new TextField();
    VBox mvServerIP = new VBox();
    VBox mvServerPort = new VBox();
    HBox mvServerInput = new HBox();
    JFXButton mvServerConnect = new JFXButton();
    JFXButton mvServerDisconnect = new JFXButton();
    HBox mvConnectionControls = new HBox();
    JFXButton mvCreateLogin = new JFXButton();
    JFXButton mvLogin = new JFXButton();
    JFXButton mvJoinChatroom = new JFXButton();
    JFXButton mvLeaveChatoom = new JFXButton();
    JFXButton mvDeleteChatroom = new JFXButton();
    JFXButton mvCreateChatroom = new JFXButton();
    JFXButton mvChangePassword = new JFXButton();
    JFXButton mvDeleteLogin = new JFXButton();
    JFXButton mvLogout = new JFXButton();
    HBox mvLangs = new HBox();
    JFXButton mvToEN = new JFXButton("EN");
    JFXButton mvToDE = new JFXButton("DE");



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
    Region sbRegion1 = new Region();
    Label sbInfo2 = new Label();


    /**
     * Set any options for the stage in the subclass constructor
     *
     * @param stage
     * @param model
     */
    public view(Stage stage, model model) {
        this.model = model;
        this.stage = stage;

        stage.initStyle(StageStyle.UNDECORATED); //Custom Window Elements




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

        Scene scene = new Scene(root, 750, 500, Color.BLACK);
        scene.getStylesheets().add("chatroom_client/styles.css");

        setIDs();
        stage.setScene(scene);


    }


    /**
     * Setup for Statusbar
     */
    private void setStatusbarView() {

        statusbar.getChildren().addAll(sbServerStatus, sbIP, sbInfo1, sbRegion1, sbInfo2);
        HBox.setHgrow(sbRegion1, Priority.ALWAYS);
    }

    /**
     * Setup for Center View
     */
    private void setCenterView() {
        cSend.textProperty().bind(getBind("send"));
        cMessagesContainer.setSpacing(2);


        cMessagesScroll.setContent(cMessagesContainer);
        cControls.getChildren().addAll(cTextField, cSend);
        center.getChildren().addAll(cMessagesScroll, cControls, charsLeft);

    }

    /**
     * Setup for Conversations View (Left Panel)
     */
    private void setChatView() {
        //Empty Conversation View -> TODO Add label if no conversation open

        cvContainerLeft.setSpacing(2);
        cvConversationButtons.add(cvTestConv);
        //cvContainerLeft.getChildren().addAll(cvConversationButtons);
        chatView.setContent(cvContainerLeft);

    }

    /**
     * Setup for Settings/Menu Bar (Also left Panel)
     */
    private void setMenuView() {
        mvContainerLeft.setSpacing(2);

        //language binds
        mvServerIPLabel.textProperty().bind(getBind("SetServer"));
        mvServerPortLabel.textProperty().bind(getBind("SetPort"));
        mvServerIPInput.setText(model.ipAddress);
        mvServerPortInput.setText(Integer.toString(model.portNumber));

        mvServerConnect.textProperty().bind((getBind("connect")));
        mvServerDisconnect.textProperty().bind((getBind("disconnect")));
        mvCreateLogin.textProperty().bind(getBind("CreateAccount"));
        mvLogin.textProperty().bind(getBind("Login"));
        mvJoinChatroom.textProperty().bind(getBind("JoinChatroom"));
        mvLeaveChatoom.textProperty().bind(getBind("LeaveChatroom"));
        mvCreateChatroom.textProperty().bind(getBind("CreateChatroom"));
        mvDeleteChatroom.textProperty().bind(getBind("DeleteChatroom"));


        mvChangePassword.textProperty().bind(getBind("ChangePassword"));
        mvDeleteLogin.textProperty().bind(getBind("DeleteAccount"));
        mvLogout.textProperty().bind(getBind("Logout"));

        mvServerIP.getChildren().addAll(mvServerIPLabel, mvServerIPInput);
        mvServerPort.getChildren().addAll(mvServerPortLabel, mvServerPortInput);
        mvConnectionControls.getChildren().addAll(mvServerConnect, mvServerDisconnect);
        mvServerInput.getChildren().addAll(mvServerIP, mvServerPort);
        mvLangs.getChildren().addAll(mvToEN, mvToDE);
        mvContainerLeft.getChildren().addAll(mvServerInput, mvConnectionControls, mvCreateLogin, mvLogin, mvJoinChatroom, mvLeaveChatoom,  mvCreateChatroom, mvDeleteChatroom, mvChangePassword, mvDeleteLogin, mvLogout, mvLangs);
        menuView.setContent(mvContainerLeft);



    }


    /**
     * Setup for Window Control elements and menu button
     */
    private void setWindowBar(){
        wbSpacer = new Region();
        wbHamMenu = new Button("|menu|");
        wbMinimize = new Button("|min|");
        wbMaximize = new Button("|max|");
        wbClose = new Button("|close|");




        wbSpacer.setMinWidth(Region.USE_PREF_SIZE);
        //wbHamMenu.setPrefSize(20, 20);
        HBox.setHgrow(wbSpacer, Priority.ALWAYS);
        //windowBar.setPadding(new Insets(10));
        windowBar.setAlignment(Pos.CENTER);
        windowBar.setSpacing(10);



        windowBar.getChildren().addAll(wbHamMenu, wbSpacer, wbMinimize, wbMaximize, wbClose);
    }

    /**
     * Stringbinding Shortcut
     * @param key
     * @return
     */
    public StringBinding getBind(String key){
        return transl.createStringBinding(key);
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
        root.getStyleClass().add("root");
        chatView.setId("chatView");
        chatView.getStyleClass().add("chatView");
        menuView.setId("menuView");
        menuView.getStyleClass().add("menuView");
        windowBar.setId("windowBar");
        statusbar.setId("statusbar");
        center.setId("center");
        center.getStyleClass().add("center");
        cMessagesScroll.setId("cMessagesScroll");
        cMessagesContainer.setId("cMessagesContainer");
        cControls.setId("cControls");
        cTextField.setId("cTextField");
        cSend.setId("cSend");
        charsLeft.setId("charsLeft");
        cvContainerLeft.setId("cvContainerLeft");
        cvContainerLeft.getStyleClass().add("cvContainerLeft");
        cvTestConv.setId("cvTestConv");
        mvContainerLeft.setId("mvContainerLeft");
        mvServerIPLabel.setId("mvServerIPLabel");
        mvServerPortLabel.setId("mvServerPortLabel");
        mvServerIPInput.setId("mvServerIPInput");
        mvServerPortInput.setId("mvServerPortInput");
        mvServerIP.setId("mvServerIP");
        mvServerPort.setId("mvServerPort");
        mvServerInput.setId("mvServerInput");
        mvServerConnect.setId("mvServerConnect");
        mvServerDisconnect.setId("mvServerDisconnect");
        mvConnectionControls.setId("mvConnectionControls");
        mvCreateLogin.setId("mvCreateLogin");
        mvLogin.setId("mvLogin");
        mvJoinChatroom.setId("mvJoinChatroom");
        mvLeaveChatoom.setId("mvLeaveChatoom");
        mvDeleteChatroom.setId("mvDeleteChatroom");
        mvCreateChatroom.setId("mvCreateChatroom");
        mvChangePassword.setId("mvChangePassword");
        mvDeleteLogin.setId("mvDeleteLogin");
        mvLogout.setId("mvLogout");
        mvLangs.setId("mvLangs");
        mvToEN.setId("mvToEN");
        mvToDE.setId("mvToDE");
        wbHamMenu.setId("wbHamMenu");
        wbMinimize.setId("wbMinimize");
        wbMaximize.setId("wbMaximize");
        wbClose.setId("wbClose");
        wbSpacer.setId("wbSpacer");
        sbServerStatus.setId("sbServerStatus");
        sbIP.setId("sbIP");
        sbInfo1.setId("sbInfo1");
        sbRegion1.setId("sbRegion1");
        sbInfo2.setId("sbInfo2");
        //TODO CONTINUE ADDING ID's!!!
    }


}
