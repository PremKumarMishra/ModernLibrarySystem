package com.dsa.modernlibrarysystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Views/RootView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Font.loadFont(getClass().getResourceAsStream("Assets/Fonts/Poppins.ttf"),15);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Modern Library System");
        stage.getIcons().addAll(new Image(getClass().getResourceAsStream("Assets/Icons/mitm_logo_trans.png")));
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
