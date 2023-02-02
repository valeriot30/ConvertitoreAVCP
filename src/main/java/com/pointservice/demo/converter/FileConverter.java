package com.pointservice.demo.converter;

import com.pointservice.demo.model.Attribute;
import com.pointservice.demo.model.Field;
import com.pointservice.demo.utils.Utils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;

import javax.swing.text.DateFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FileConverter extends Converter {

    private File selectedFile;

    /**
     * Ottieni il nome del comune. In questo caso, se si usa la modalità <b>File</b>,
     * il nome del comune è rappresentato del nome del file, es: (Roma.xlxs) -> COMUNE DI ROMA
     * Questa soluzione è utile quando si vuole effettuare una conversione di tipo BULK
     */
    @Override
    protected String getComuneName() {
        return this.selectedFile.getName();
    }

    /**
     * Converti un intero documento excel in un arraylist di <code>Field</code>
     * La lista è una struttura dati complessa perchè ogni <code>Field</code> ha un array di attributi.
     */
    public void readExelFile() {
        SimpleDateFormat dataFormatter = new SimpleDateFormat("dd-MM-yyyy");
        DataFormatter formatter = new DataFormatter();

        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(this.selectedFile));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell;

            int rows;
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 0;
            int tmp = 0;

            for(int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if(row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if(tmp > cols) cols = tmp;
                }
            }

            for(int r1 = 0; r1 < rows; r1++) {
                row = sheet.getRow(r1);

                for(int c = 0; c < cols; c++) {
                    cell = row.getCell((short)c);

                    if(cell != null) {
                        this.getDefaultAttributes().add(new Attribute(c, Utils.getCellValueFromType(cell)));
                    }
                }
            }

            for(int r = 1; r < rows; r++) {
                row = sheet.getRow(r);

                Field field = new Field();

                if(row != null) {
                    for(int c = 0; c < 14; c++) {
                        cell = row.getCell((short)c);

                        if(cell != null) {
                            field.addValue(Utils.getCellValueFromType(cell));
                        } else {
                            field.addValue("");
                            System.out.println("La cella [" + r + " " + c + "] è nulla");
                        }
                    }
                }

                this.fields.add(field);
            }
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }


    /**
     * Verifica o no se il file selezionato è valido, verificando tutte le possibili estensioni di excel.
     * @param file
     * @return
     */
    public boolean isFileValid(File file) {

        if(file == null) return false;

        String extension = this.getExtension(file);
        return extension.toLowerCase().equals("xls") || extension.equals("xlsx") || extension.equals(".xlsm") || extension.equals(".xlsb");
    }

    private boolean isValidDate(SimpleDateFormat dateFormatter, String date) {
        try {
            dateFormatter.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Estrai l'estensione di un file dal suo nome completo.
     * L'estensione è l'ultimo elemento dell'array che si ottiene splittando il nome di un file per "."
     * "Comune_di_Roma.xlsx" -> xlsx
     * @param file
     * @return
     */
    private String getExtension(File file) {
        String extension = "";

        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i+1);
        }

        return extension;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

}
