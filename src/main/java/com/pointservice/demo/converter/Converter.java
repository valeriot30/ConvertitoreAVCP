package com.pointservice.demo.converter;

import com.pointservice.demo.model.Attribute;
import com.pointservice.demo.model.AttributeType;
import com.pointservice.demo.model.Field;
import com.pointservice.demo.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.StringUtil;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
public abstract class Converter {


    protected final boolean useValidation = false;

    public ArrayList<Field> fields;

    protected ArrayList<Attribute> defaultAttributes;

    protected HashMap<Integer, String> sceltaContrenteTypes;

    protected int previousYear;



    public Converter() {
        this.fields = new ArrayList<>();
        this.defaultAttributes = new ArrayList<>();
        this.sceltaContrenteTypes = new HashMap<>();

        this.loadScelteTypes();
    }

    private void loadScelteTypes() {
        this.sceltaContrenteTypes.put(0, "TO_MODIFY");
        this.sceltaContrenteTypes.put(1, "01-PROCEDURA APERTA");
        this.sceltaContrenteTypes.put(2, "02-PROCEDURA RISTRETTA");
        this.sceltaContrenteTypes.put(3, "03-PROCEDURA NEGOZIATA PREVIA PUBBLICAZIONE");
        this.sceltaContrenteTypes.put(4, "04-PROCEDURA NEGOZIATA SENZA PREVIA PUBBLICAZIONE");
        this.sceltaContrenteTypes.put(5, "05-DIALOGO COMPETITIVO");
        this.sceltaContrenteTypes.put(6, "06-PROCEDURA NEGOZIATA SENZA PREVIA INDIZIONE DI GARA (SETTORI SPECIALI)");
        this.sceltaContrenteTypes.put(7, "07-SISTEMA DINAMICO DI ACQUISIZIONE");
        this.sceltaContrenteTypes.put(8, "08-AFFIDAMENTO IN ECONOMIA - COTTIMO FIDUCIARIO");
        this.sceltaContrenteTypes.put(14, "14-PROCEDURA SELETTIVA EX ART 238 C.7, D.LGS. 163/2006");
        this.sceltaContrenteTypes.put(17, "17-AFFIDAMENTO DIRETTO EX ART. 5 DELLA LEGGE 381/91");
        this.sceltaContrenteTypes.put(21, "21-PROCEDURA RISTRETTA DERIVANTE DA AVVISI CON CUI SI INDICE LA GARA");
        this.sceltaContrenteTypes.put(22, "22-PROCEDURA NEGOZIATA CON PREVIA INDIZIONE DI GARA (SETTORI SPECIALI)");
        this.sceltaContrenteTypes.put(23, "23-AFFIDAMENTO DIRETTO");
        this.sceltaContrenteTypes.put(24, "24-AFFIDAMENTO DIRETTO A SOCIETA' IN HOUSE");
        this.sceltaContrenteTypes.put(25, "25-AFFIDAMENTO DIRETTO A SOCIETA' RAGGRUPPATE/CONSORZIATE O CONTROLLATE NELLE CONCESSIONI E NEI PARTENARIATI");
        this.sceltaContrenteTypes.put(26, "26-AFFIDAMENTO DIRETTO IN ADESIONE AD ACCORDO QUADRO/CONVENZIONE");
        this.sceltaContrenteTypes.put(27, "27-CONFRONTO COMPETITIVO IN ADESIONE AD ACCORDO QUADRO/CONVENZIONE");
        this.sceltaContrenteTypes.put(28, "28-PROCEDURA AI SENSI DEI REGOLAMENTI DEGLI ORGANI COSTITUZIONALI");
        this.sceltaContrenteTypes.put(29, "29-PROCEDURA RISTRETTA SEMPLIFICATA");
        this.sceltaContrenteTypes.put(30, "30-PROCEDURA DERIVANTE DA LEGGE REGIONALE");
        this.sceltaContrenteTypes.put(31, "31-AFFIDAMENTO DIRETTO PER VARIANTE SUPERIORE AL 20% DELL'IMPORTO CONTRATTUALE");
        this.sceltaContrenteTypes.put(32, "32-AFFIDAMENTO RISERVATO");
        this.sceltaContrenteTypes.put(33, "33-PROCEDURA NEGOZIATA PER AFFIDAMENTI SOTTO SOGLIA");
        this.sceltaContrenteTypes.put(34, "34-PROCEDURA ART.16 COMMA 2-BIS DPR 380/2001 PER OPERE URBANIZZAZIONE A SCOMPUTO PRIMARIE SOTTO SOGLIA COMUNITARIA");
        this.sceltaContrenteTypes.put(35, "35-PARTERNARIATO PER L’INNOVAZIONE");
        this.sceltaContrenteTypes.put(36, "36-AFFIDAMENTO DIRETTO PER LAVORI, SERVIZI O FORNITURE SUPPLEMENTARI");
        this.sceltaContrenteTypes.put(37, "37-PROCEDURA COMPETITIVA CON NEGOZIAZIONE");
        this.sceltaContrenteTypes.put(38, "38-PROCEDURA DISCIPLINATA DA REGOLAMENTO INTERNO PER SETTORI SPECIALI");
        this.sceltaContrenteTypes.put(39, "39-AFFIDAMENTO DIRETTO PER MODIFICHE CONTRATTUALI O VARIANTI PER LE QUALI È NECESSARIA UNA NUOVA PROCEDURA DI AFFIDAMENTO");
    }


    private String getFileName() {
        return "/dataset_" + this.getPreviousYear() + ".xml";
    }

    /**
     * Validiamo il file XML a partire da uno schema XSD.
     * @return true se il file è valido
     */
    public boolean validateXMLFile() {
        try {
            URL xml = new File(this.getFileName()).toURI().toURL();
            URL xsd =  new URL("https://dati.anticorruzione.it/schema/datasetAppaltiL190.xsd");
            XMLReaderJDOMFactory factory = new XMLReaderXSDFactory(xsd);
            SAXBuilder builder = new SAXBuilder(factory);
            Document document = builder.build(xml);
            System.out.println("radice: " + document.getRootElement().getName());
            return true;
        } catch (IOException | JDOMException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
    }
    int getPreviousYear() {
        return this.previousYear;
    }

    /**
     * Aggiungiamo il metadata all'inizio del documento.
     * NB: Il nome del comune, la data di riferimento sono campi generati automaticamente.
     * @param path
     */

    public String generateXMLFile() throws IOException {
        return generateXMLFile("");
    }

    public String generateXMLFile(String path) throws IOException {

        Document doc = new Document();

        Namespace legge190 = Namespace.getNamespace("legge190", "legge190_1_0");

        Element root = new Element("pubblicazione", legge190);
        Namespace XSI = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.addNamespaceDeclaration(legge190);
        root.addNamespaceDeclaration(XSI);
        root.setAttribute("schemaLocation", "legge190_1_0 datasetAppaltiL190.xsd", XSI);

        this.addMetaData(root);

        Element data =new Element("data");

        for(int i = 0; i < fields.size(); i++) {

            ArrayList<String> row = fields.get(i).getValues();

            if(row.size() == 0)
                continue;

            Element lotto = new Element("lotto");
            Element strutturaProponente = new Element("strutturaProponente");
            Element oggetto = new Element("oggetto");
            Element cig = new Element("cig");
            Element codFiscale = new Element("codiceFiscaleProp");
            Element denominazione = new Element("denominazione");
            Element partecipanti = new Element("partecipanti");
            Element aggiudicatari = new Element("aggiudicatari");
            Element tempiCompletamento = new Element("tempiCompletamento");
            Element sceltaContraente = new Element("sceltaContraente");
            Element importoSommeLiquidate = new Element("importoSommeLiquidate");

            String cigElement = row.get(AttributeType.CIG.getValue());
            String subject = row.get(AttributeType.OGGETTO.getValue());
            String denominazioneText = row.get(AttributeType.DENOMINAZIONE.getValue());
            String codFiscaleText = row.get(AttributeType.COD_FISCALE.getValue()).replace(".","");
            String codFiscaleAggiudicante = row.get(AttributeType.AGG_COD_FISCALE.getValue()).replace(".", "");
            String ragioneSociale = this.tryOrGetValue(row, AttributeType.AGG_RAGIONE_SOCIALE.getValue());
            String importoAggiudicazione = this.getNormalizedPrice(this.tryOrGetValue(row, AttributeType.IMPORTO_NETTO.getValue()));
            String dataInizio = this.tryOrGetValue(row, AttributeType.DATA_INIZIO.getValue());
            String dataUltimazione = this.tryOrGetValue(row, AttributeType.DATA_ULTIMAZIONE.getValue());
            String codFiscaleAggiudicante2 = this.tryOrGetValue(row, AttributeType.COD_FISCALE_INVITATO1.getValue()).replace(".", "");
            String ragioneSociale2 = this.tryOrGetValue(row, AttributeType.RAGIONE_SOCIALE_INVITATO1.getValue());
            String ruolo2 = this.tryOrGetValue(row, AttributeType.RUOLO_INVITATO1.getValue());

            int sceltaContraenteNum = 0;

            try {
                sceltaContraenteNum = !this.tryOrGetValue(row, AttributeType.SCELTA_CONTRAENTE.getValue()).equals("") ? (int) Math.floor((int) Double.parseDouble(this.tryOrGetValue(row, AttributeType.SCELTA_CONTRAENTE.getValue()))) : 0;
            } catch(NumberFormatException e) {
                continue;
            }

            if(sceltaContraenteNum == 0) continue;

            String importoSommeLiquidateText = this.tryOrGetValue(row, AttributeType.SOMME_LIQUIDATE.getValue());
            importoSommeLiquidateText = this.getNormalizedPrice(importoSommeLiquidateText);
            String ruolo = this.tryOrGetValue(row, AttributeType.RUOLO_INVITATO1.getValue());

            // Alcune date presentano "." invece che "-" quindi le aggiungiamo
            dataInizio = this.getNormalizedDate(dataInizio.replace(".", "-"));
            dataUltimazione = this.getNormalizedDate(dataUltimazione.replace(".", "-"));

            // ottieni la scelta contranente dal numero
            String sceltaContraenteText = this.sceltaContrenteTypes.get(sceltaContraenteNum);

            if(StringUtils.isAllUpperCase(subject)) {
                subject = subject.toLowerCase();
            }

            oggetto.addContent(String.format("<![CDATA[%s]]>", subject));
            codFiscale.setText(codFiscaleText);
            denominazione.setText(denominazioneText);
            cig.setText(cigElement.replace(".", "").trim());
            sceltaContraente.setText(sceltaContraenteText);
            if(!importoSommeLiquidateText.equals("")) {
                importoSommeLiquidate.addContent((importoSommeLiquidateText));
            } else {
                importoSommeLiquidate.addContent(("0.0"));
            }

            //TODO generarli automaticamente
            Element partecipante = new Element("partecipante");

            Element partecipante2 = new Element("partecipante");

            if(!codFiscaleAggiudicante.equals("")) partecipante.addContent(new Element("codiceFiscale").setText(codFiscaleAggiudicante));
            if(!ragioneSociale.equals("")) partecipante.addContent(new Element("ragioneSociale").addContent(ragioneSociale));
            if(!ruolo.equals("")) partecipante.addContent(new Element("ruolo").addContent(ruolo));
            if(!codFiscaleAggiudicante2.equals("")) partecipante2.addContent(new Element("codiceFiscale").addContent(codFiscaleAggiudicante2));
            if(!ragioneSociale2.equals("")) partecipante2.addContent(new Element("ragioneSociale").addContent(ragioneSociale2));
            if(!ruolo2.equals("")) partecipante2.addContent(new Element("ruolo").addContent(ruolo2));

            tempiCompletamento.addContent(new Element("dataInizio").setText(dataInizio));


            if(!dataUltimazione.equals("")) tempiCompletamento.addContent(new Element("dataUltimazione").setText(dataUltimazione));

            partecipanti.addContent(partecipante);

            if(!codFiscaleAggiudicante2.equals("") && !ragioneSociale2.equals("")) {
                partecipanti.addContent(partecipante2);
            }



            strutturaProponente.addContent(codFiscale);
            strutturaProponente.addContent(denominazione);

            lotto.addContent(cig);
            lotto.addContent(strutturaProponente);
            lotto.addContent(oggetto);
            if(!sceltaContraenteText.equals("")) lotto.addContent(sceltaContraente);
            lotto.addContent(partecipanti);
            lotto.addContent(aggiudicatari);
            if(!importoAggiudicazione.equals("")) {
                lotto.addContent(new Element("importoAggiudicazione").setText(importoAggiudicazione));
            }
            if(!dataInizio.equals("")) lotto.addContent(tempiCompletamento);
            lotto.addContent(importoSommeLiquidate);

            data.addContent(lotto);
        }

        root.addContent(data);
        doc.setRootElement(root);

        XMLOutputter outputter = new XMLOutputter();

        outputter.setFormat(Format.getPrettyFormat());
        outputter.output(doc, new FileWriter(new File(path + this.getFileName())));

        return "";

    }

    /**
     *
     * @param fields i campi su cui controllare
     * @param index l'intero corrispondente al tipo <code>AttributeType</code>
     * @return la stringa dentro la cella, altrimenti ritorna una stringa vuota.
     */
    private String tryOrGetValue(ArrayList<String> fields, Integer index) {
        if (fields.size() > index) {
            try {
                return fields.get(index);
            } catch (IndexOutOfBoundsException e) {
                return "";
            }
        }

        return "";
    }

    private String getNormalizedDate(String date) {

        if(date.equals("")) {
            return "";
        }

        SimpleDateFormat fromUser = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {

            return myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String getNormalizedPrice(String price) {

        if(price.length() == 0) return "";

        if(price.contains(".") && !price.contains("€")) return price;

        String theString = price;

        if(theString.length() == 0) return "";

        theString = theString.replace("€", "").trim();



        String separator = ",";
        String replacement = ".";
        try {
            String newString = theString.substring(0, theString.lastIndexOf(separator)).replaceAll(separator , replacement).concat(theString.substring(theString.lastIndexOf(separator)));

            newString = newString.replaceFirst("^0+(?!$)", "");

            String withComma = newString;

            withComma = withComma.replace(".", "");
            withComma = withComma.replace(",", ".");


            double decimalNumber = Double.parseDouble(withComma);

            double roundOff = Math.round(decimalNumber * 100.0) / 100.0;

            return roundOff + "";
        } catch(StringIndexOutOfBoundsException e) {
            System.out.println(theString);
            e.printStackTrace();
        }

        return "";
    }


    protected abstract String getComuneName();


    /**
     * Aggiungiamo il metadata all'inizio del documento.
     * NB: Il nome del comune, la data di riferimento sono campi generati automaticamente.
     * @param root
     */
    private void addMetaData(Element root) {
        String nameUrl = FilenameUtils.removeExtension(this.getComuneName());

        Element metadata = new Element("metadata");

        Element titolo = new Element("titolo");
        Element abstractElement = new Element("abstract");
        Element dataPubblicazione = new Element("dataPubblicazioneDataset");
        Element pubblicatore = new Element("entePubblicatore");
        Element dataUltimoAggiornamento = new Element("dataUltimoAggiornamentoDataset");
        Element annoRiferimento = new Element("annoRiferimento");
        Element urlFile = new Element("urlFile");
        Element licenza = new Element("licenza");

        int year = Utils.getPreviousYear();

        this.previousYear = year;

        titolo.setText("Pubblicazione legge 190");
        abstractElement.setText("Pubblicazione legge 190 anno riferimento " + year);
        pubblicatore.setText("Comune di " + nameUrl);
        dataPubblicazione.setText(year + "-12-30");
        dataUltimoAggiornamento.setText(year + "-12-30");
        annoRiferimento.setText(year + "");
        urlFile.setText("http://www.servizipa.com/avcp/" + nameUrl.toLowerCase().trim() + this.getFileName());
        licenza.setText("IODL");

        metadata.addContent(titolo);
        metadata.addContent(abstractElement);
        metadata.addContent(dataPubblicazione);
        metadata.addContent(pubblicatore);
        metadata.addContent(dataUltimoAggiornamento);
        metadata.addContent(annoRiferimento);
        metadata.addContent(urlFile);
        metadata.addContent(licenza);

        root.addContent(metadata);
    }
    public boolean isUseValidation() {
        return useValidation;
    }


    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    public ArrayList<Attribute> getDefaultAttributes() {
        return defaultAttributes;
    }

    public void setDefaultAttributes(ArrayList<Attribute> defaultAttributes) {
        this.defaultAttributes = defaultAttributes;
    }

    public HashMap<Integer, String> getSceltaContrenteTypes() {
        return sceltaContrenteTypes;
    }

    public void setSceltaContrenteTypes(HashMap<Integer, String> sceltaContrenteTypes) {
        this.sceltaContrenteTypes = sceltaContrenteTypes;
    }

    public void setPreviousYear(int previousYear) {
        this.previousYear = previousYear;
    }
}
