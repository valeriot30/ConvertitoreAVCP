package com.pointservice.demo.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.DateUtil;


import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static String stripAccents(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        String str = Normalizer.normalize(value, Normalizer.Form.NFD);
        for (char c : str.toCharArray()) {
            if (c <= '\u007F') sb.append(c);
        }

        return sb.toString();
    }
    public static String getCodiceFiscaleRegex() {
        return "^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$";
    }

    public static String getDateFormatted(String date) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date d = parser.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            return formatter.format(d);
        } catch(ParseException e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    public static String getCellValueFromType(HSSFCell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    return dateFormat.format(cell.getDateCellValue());
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case STRING:
                return cell.getStringCellValue();
            default:
            case BLANK:
                break;
        }

        return "";
    }

    public static String getNormalizedName(String name) {
        String[] multi = name.split(" ");

        if(multi.length > 0) {
            return Utils.stripAccents(multi[multi.length - 1]);
        }

        return name;
    }

    public static int getPreviousYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -1);
        return prevYear.get(Calendar.YEAR);
    }
}
