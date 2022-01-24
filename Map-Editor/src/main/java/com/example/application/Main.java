package com.example.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        try {
            Scene scene = new Scene(root,1410,905);
            scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
        root.setCenter(new RootLayout());

    }

    public static void main(String[] args) {
        launch(args);
    }
}