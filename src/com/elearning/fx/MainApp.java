package com.elearning.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	
    @Override
    public void start(Stage stage) {
        LoginFX login = new LoginFX(stage);

        Scene scene = new Scene(login.getView(), 400, 300);

        stage.setTitle("E-Learning System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {	
        launch();
    }
}