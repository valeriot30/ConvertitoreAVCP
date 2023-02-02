package com.pointservice.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 400);

        scene.getStylesheets().add(String.valueOf((App.class.getResource("style.css"))));

        stage.getIcons().add(new Image("https://i.imgur.com/mgMYpun.png"));

        stage.setTitle("Convertitore Exel a XML");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}