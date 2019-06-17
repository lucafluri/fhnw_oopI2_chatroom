package chatroom_client.controller;

import chatroom_client.model.chatList_model;
import chatroom_client.view.chatList_view;

public class chatList_controller {
    private chatList_view view;
    private chatList_model model;

    protected chatList_controller(chatList_model model, chatList_view view) {
        this.model = model;
        this.view = view;
    }
}
