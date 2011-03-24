package com.realcomp.data.record.reader;


public enum Delimiter{
    CSV, TAB;

    public static Delimiter parse(String delimiter){

        if (delimiter == null)
            throw new IllegalArgumentException("delimiter is null");

        if (delimiter.equalsIgnoreCase("tab") || delimiter.equalsIgnoreCase("tabbed") || delimiter.equalsIgnoreCase("\t"))
            return TAB;
        else if (delimiter.equalsIgnoreCase("csv") || delimiter.equals(","))
            return CSV;
        throw new IllegalArgumentException("invalid delimiter: " + delimiter);
    }
}