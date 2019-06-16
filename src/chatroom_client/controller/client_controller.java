package chatroom_client.controller;

import chatroom_client.abstractClasses.*;
import chatroom_client.model.client_model;
import chatroom_client.view.client_view;

public class client_controller extends Controller<client_model, client_view> {
    protected client_controller(client_model model, client_view view) {
        super(model, view);
    }
}
