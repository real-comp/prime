package com.realcomp.data.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Guesses the delimiter for a '\n' delimited file. Currently only supports only CSV and TAB delimited.
 *
 * @author krenfro
 */
public class DelimiterGuesser{

    /**
     *
     * @param file
     * @return ',', '\t', or null
     * @throws IOException
     */
    public static String guess(File file) throws IOException{

        List<String> records = readLines(file, 10);

        int csvCount = count(records, ",");
        int tabCount = count(records, "\t");

        if (csvCount > 0 && csvCount > tabCount){
            return ",";
        }
        else if (tabCount > 0 && tabCount > csvCount){
            return "\t";
        }
        else{
            return null;
        }
    }

    /**
     * @param records
     * @param delimiter
     * @return average number of delimiters per record.
     */
    protected static int count(List<String> records, String delimiter){
        int total = 0;
        for (String record : records){
            total += record.split(delimiter).length;
        }
        return Math.round((float) total / records.size());
    }

    /**
     * Reads up to <i>count</i> lines from <i>file</i>.
     *
     * @param file
     * @param count
     * @return
     * @throws IOException
     */
    protected static List<String> readLines(File file, int count) throws IOException{

        List<String> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){

            String s = reader.readLine();
            while (records.size() < count && s != null){
                records.add(s);
                s = reader.readLine();
            }
        }

        return records;
    }
}
