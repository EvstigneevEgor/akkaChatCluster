package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;

public class Controller {

    public String url = "second.fxml";

    @FXML
    protected ListView<Messenge> ListMessenge;

    @FXML
    protected Button Send;

    @FXML
    protected SplitPane window1;



    @FXML
    protected TextArea PostText;

    @FXML
    void initialize() {
        assert ListMessenge != null : "fx:id=\"ListMessenge\" was not injected: check your FXML file 'first.fxml'.";
        assert Send != null : "fx:id=\"Send\" was not injected: check your FXML file 'first.fxml'.";
        assert PostText != null : "fx:id=\"PostText\" was not injected: check your FXML file 'first.fxml'.";
    }

}