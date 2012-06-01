package org.onexus.core.query;


import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public abstract class Filter implements Serializable {

    public Filter() {
        super();
    }

    public String toString() {
        return toString(new StringBuilder(), true).toString();
    }

    public abstract StringBuilder toString(StringBuilder oql, boolean prettyPrint);


    public static String convertToOQL(Object value) {

        if (value instanceof Date ||
                value instanceof Time ||
                value instanceof Timestamp
                ) {
            return "#" + String.valueOf(value) + "#";
        }

        if (value instanceof String) {
            return Query.escapeString(value.toString());
        }

        return String.valueOf(value);
    }

    public static Long convertToLong(String oqlValue) {
        return (oqlValue == null ? null : Long.decode(oqlValue));
    }

    public static String convertToString(String oqlValue) {
        return (oqlValue == null ? null : oqlValue);
    }

    public static Date convertToDate(String oqlValue) {
        return (oqlValue == null ? null : Date.valueOf(oqlValue));
    }

    public static Double convertToDouble(String oqlValue) {
        return (oqlValue == null ? null : Double.valueOf(oqlValue));
    }

    public static String endingTabs(StringBuilder oql) {
        String prevTabs = "";
        int l = oql.length();
        for (int i = l - 1; oql.charAt(i) == '\t' && i > 0; i--) {
            prevTabs += "\t";
        }

        return prevTabs;
    }

}
