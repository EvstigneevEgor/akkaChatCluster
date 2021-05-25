package org.example;

import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerImplJava extends Controller implements Initializable{
    /*
    @Override
    public void getPost() {
        super.getPost();
    }

    @Override
    public void sendPost(Messenge messenge) {
        super.sendPost(messenge);
    }
*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {

            ListMessenge.setCellFactory(param -> new ListCell<Messenge>() {
                @Override
                protected void updateItem(Messenge item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null || item.getPostText() == null ) {
                        setText(null);
                    } else {
                        setText(item.getPostText());
                    }
                }
            });


        //ListMessenge.getItems().addAll(addTestStudents());
        Messenge n = new Messenge();
        ListMessenge.getItems().add(n);
        ListMessenge.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
       /* ListMessenge.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                showMessenge(newValue);
            });*/
            Send.setOnAction(event -> Sender());



    }
/*
    private void showMessenge(Messenge newValue) {

        if(newValue!= null){

        }else{

        }
    }*/

    private void Sender() {
        Messenge m = new Messenge(PostText.getText());
        ListMessenge.getItems().add(m);
        PostText.clear();
    }
}
