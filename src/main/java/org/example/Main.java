package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


//public class Main {
    public class Main extends Application {
    private Stage primaryStage;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Пример");
        //primaryStage.setWidth(900);
        //primaryStage.setHeight(500);
        showBaseWindow();
    }

    public void showBaseWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Controller.class.getResource("/org.example/second.fxml"));
            AnchorPane rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            loader.getController();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
//}
