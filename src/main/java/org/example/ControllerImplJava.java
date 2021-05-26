package org.example;

import akka.actor.ActorRef;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerImplJava extends Controller implements Initializable {
    //private MainScala cluster;

    private ActorRef actor;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        MessageList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getPostText() == null) {
                    setText(null);
                } else {
                    setText(item.getPostText());
                }
            }
        });

        Message n = new Message();
        MessageList.getItems().add(n);
        MessageList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Send.setOnAction(event -> Sender());
        PostText.setPromptText("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435!");

    }

    public void setAkka(String clusterN){

    }

    private void Sender() {
        if (!PostText.getText().equals("")) {
            Message m = new Message(PostText.getText());
            Message def = new Message();

            if (MessageList.getItems().get(0).getPostText().equals("\u0412\u0432\u0435\u0434\u0438\u0442\u0435\u0020\u0442\u0435\u043a\u0441\u0442\u0020\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f"))
                MessageList.getItems().remove(0);
            MessageList.getItems().add(m);
            PostText.clear();
        }
    }
    public void setCluster(){

    }
}
