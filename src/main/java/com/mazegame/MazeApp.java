package com.mazegame;

import com.mazegame.ui.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MazeApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NEON MAZE");
        ScreenManager screenManager = new ScreenManager(primaryStage);
        screenManager.showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
