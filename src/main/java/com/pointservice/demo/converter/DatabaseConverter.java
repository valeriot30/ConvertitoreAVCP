package com.pointservice.demo.converter;

import com.pointservice.demo.model.Field;
import com.pointservice.demo.utils.Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseConverter extends Converter{

    private Connection connection;

    public DatabaseConverter() {

    }

    /**
     * ottieni gli aggiudicatari dal database in base al cig (Viene preso solo un aggiudicatario)
     * @param cig
     * @return
     */
    private String[] getAggiudicatari(String cig) {
        try {

            String sql = "SELECT * FROM TRegistroLG190_2012_Ditta WHERE cig = ?";

            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);

            preparedStatement.setString(1, cig);

            ResultSet set = preparedStatement.executeQuery();

            while(set.next()) {
                return new String[]{set.getString("CodiceFiscale"), set.getString("RagioneSociale")};
            }


        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
    public void loadFromDatabase() {
        try {
            Statement statement = connection.createStatement();

            ResultSet set = statement.executeQuery("SELECT * FROM dbo.TRegistroLG190_2012 WHERE anno > 2021 AND anno < 2024");

            while(set.next()) {
                Field field = new Field();

                field.addValue(set.getString("CIG"));
                field.addValue(set.getString("Oggetto"));
                field.addValue(set.getString("Prop_denominazione"));
                field.addValue(set.getString("Prop_CodiceFiscale"));

                String[] aggiudicatari = this.getAggiudicatari(set.getString("CIG"));

                if(aggiudicatari != null && aggiudicatari.length > 0) {
                    if(aggiudicatari[0] != "") field.addValue(aggiudicatari[0]);
                    if(aggiudicatari[1] != "") field.addValue(aggiudicatari[1]);
                }
                field.addValue(set.getString("ImportoAggiudicazione"));
                field.addValue(Utils.getDateFormatted(set.getString("DataInizio")));
                field.addValue(Utils.getDateFormatted(set.getString("DataFine")));
                field.addValue(set.getString("ImportoLiquidato"));
                field.addValue("");
                field.addValue("");
                field.addValue("");
                field.addValue(set.getString("IdLG190_2012_SContr"));

                this.fields.add(field);
            }


        } catch(SQLException e) {
            throw new Error(e);
        }
    }

    @Override
    protected String getComuneName() {
        try {
            Statement statement = connection.createStatement();

            ResultSet set = statement.executeQuery("SELECT citta FROM dbo.TLicenza WHERE idLicenza > 0");

            set.next();

            String citta = set.getString("citta");

            return citta.substring(0, 1).toUpperCase() + citta.substring(1);
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
