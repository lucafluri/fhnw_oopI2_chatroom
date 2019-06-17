package chatroom_client.controller;


import chatroom_client.model.client_model;
import chatroom_client.view.client_view;

public class client_controller  {
    private final client_view view;
    private final client_model model;

    public client_controller(client_model model, client_view view) {
        this.model = model;
        this.view = view;

    }
}
