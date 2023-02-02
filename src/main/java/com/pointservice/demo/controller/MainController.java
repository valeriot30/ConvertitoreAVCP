package com.pointservice.demo.controller;

import com.pointservice.demo.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainController {
    @FXML
    public void onClickedModel1Button(javafx.event.ActionEvent actionEvent) {
        try {
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            FXMLLoader secondPageLoader = new FXMLLoader(App.class.getResource("file.fxml"));
            Parent secondPane = secondPageLoader.load();
            Scene secondScene = new Scene(secondPane, 500, 400);


            primaryStage.setScene(secondScene);
            primaryStage.show();
        } catch(IOException e) {
            throw new Error(e);
        }
    }
    @FXML
    public void onClickedModel2Button(javafx.event.ActionEvent actionEvent) {
        try {
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            FXMLLoader secondPageLoader = new FXMLLoader(App.class.getResource("sql.fxml"));
            Parent secondPane = secondPageLoader.load();
            Scene secondScene = new Scene(secondPane, 500, 400);


            primaryStage.setScene(secondScene);
            primaryStage.show();
        } catch(IOException e) {
            throw new Error(e);
        }
    }
}
