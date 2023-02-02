package com.pointservice.demo.controller;

import com.pointservice.demo.App;
import com.pointservice.demo.converter.Converter;
import com.pointservice.demo.converter.FileConverter;
import com.pointservice.demo.model.Attribute;
import com.pointservice.demo.model.AttributeType;
import com.pointservice.demo.model.Field;
import com.pointservice.demo.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class FileController {

    @FXML
    private Label errorMessage;

    @FXML
    private Label successMessage;

    @FXML
    private Label inputText;

    private FileConverter converter;

    protected File selectedFile;

    private File selectedDirectory;

    public FileController() {
        this.converter = new FileConverter();
    }


    @FXML
    protected void onSelectFile() {
        FileChooser fc = new FileChooser();

        fc.setTitle("Carica file");

        File file = fc.showOpenDialog(new Stage());

        boolean isValid = this.converter.isFileValid(file);

        this.converter.setSelectedFile(file);

        if(!isValid) {
            errorMessage.setText("Il formato del file non è valido");
            return;
        }

        inputText.setText(file.getName());
    }

    @FXML
    protected void onStartConvert() {
        if(this.converter.getSelectedFile() == null) {
            errorMessage.setText("Nessun file selezionato.");
            return;
        }

        this.converter.readExelFile();
        try {

            String path = this.converter.getSelectedFile().getParent();

            String message = this.converter.generateXMLFile(path);

            this.errorMessage.setText(message);

            boolean result = this.converter.validateXMLFile();

            if(result || !this.converter.isUseValidation()) {
                successMessage.setText("File convertito con successo su " + path);
            } else {
                errorMessage.setText("Il file convertito non è valido.");
            }

            inputText.setText("");

            //FileReader actualFile = new FileReader(this.selectedFile);

            //actualFile.close();

            this.selectedDirectory = null;

        } catch(IOException e) {
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