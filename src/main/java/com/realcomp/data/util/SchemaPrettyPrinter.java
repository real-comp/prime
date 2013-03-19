package com.realcomp.data.util;

import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.SchemaFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Prints a schema for human consumption
 *
 * @author krenfro
 */
public class SchemaPrettyPrinter {


    public void prettyPrint(Schema schema, OutputStream outputStream) throws IOException{

        PrintWriter out = new PrintWriter(outputStream);
        boolean fixed = schema.getFormat().get("type").equalsIgnoreCase("FIXED");

        out.println(schema.getName());
        out.println("format:");
        for (Entry<String,String> entry: schema.getFormat().entrySet()){
            out.println(entry.getKey() + " = " + entry.getValue());
        }
        out.println("field lists:");
        for (FieldList fieldList: schema.getFieldLists()){
            out.println("classifier: " + fieldList.getClassifier().toString());

            if (fixed){
                out.println("field\tlength\tstart\tstop");
            }
            else{
                out.println("field\tlength\tstart\tstop");
            }
            int start = 0;
            int stop = 0;
            for (Field field: fieldList){
                if (fixed){
                    stop += field.getLength();
                    out.println(field.getName() + "\t" + field.getLength() + "\t" + start + "\t" + stop);
                    start = stop + 1;
                }
                else{
                    out.println(field.getName() + "\t" + field.getLength());
                }
            }
        }
        out.flush();
    }





    public static void main(String[] args){

        SchemaPrettyPrinter generator = new SchemaPrettyPrinter();
        try{
            generator.prettyPrint(SchemaFactory.buildSchema(new FileInputStream(args[0])), System.out);
        }
        catch (IOException ex){
            Logger.getLogger(SchemaPrettyPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
