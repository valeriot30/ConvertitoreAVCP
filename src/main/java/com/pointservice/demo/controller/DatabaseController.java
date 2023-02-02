package com.pointservice.demo.controller;

import com.pointservice.demo.App;
import com.pointservice.demo.Start;
import com.pointservice.demo.converter.Converter;
import com.pointservice.demo.converter.DatabaseConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseController {

    @FXML
    private javafx.scene.control.Label successMessage;

    @FXML
    private Label errorMessage;

    private DatabaseConverter converter;

    public DatabaseController() {

        this.converter = new DatabaseConverter();
    }

    private void loadDatabase(String user, String password, String databaseName) {
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();

            String connectionUrl =
                    "jdbc:sqlserver://localhost:1433;"
                            + "databaseName=psr;"
                            + "user=sa;"
                            + "password=barcatfilcat;"
                            + "encrypt=true;"
                            + "trustServerCertificate=true;"
                            + "loginTimeout=30;";

            Connection connection = DriverManager.getConnection(connectionUrl);

            this.converter.setConnection(connection);


        } catch(SQLException | ClassNotFoundException e) {
           System.out.println(e.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void onStartConvert(ActionEvent actionEvent) {

        //TODO Mettere la connessione dinamica al database
        this.loadDatabase("", "", "");

        this.converter.loadFromDatabase();


        DirectoryChooser fc = new DirectoryChooser();

        fc.setTitle("Scegli il percorso");

        File file = fc.showDialog(new Stage());

        if(file == null) return;

        String path = file.getAbsolutePath();

        try {
            String message = this.converter.generateXMLFile(path);

            this.errorMessage.setText(message);

            boolean result = this.converter.validateXMLFile();

            if(result || !this.converter.isUseValidation()) {
                successMessage.setText("File convertito con successo su " + path);
            } else {
                errorMessage.setText("Il file convertito non è valido.");
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onBackButton(javafx.event.ActionEvent actionEvent) {
        try {
            Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            FXMLLoader secondPageLoader = new FXMLLoader(App.class.getResource("view.fxml"));
            Parent secondPane = secondPageLoader.load();
            Scene secondScene = new Scene(secondPane, 500, 400);


            primaryStage.setScene(secondScene);
            primaryStage.show();
        } catch(IOException e) {
            throw new Error(e);
        }
    }
}
