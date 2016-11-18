package com.realcomp.prime.schema;

import com.realcomp.prime.schema.xml.XStreamFactory;
import com.realcomp.prime.transform.Transformer;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SchemaFactory{


    public static Schema buildSchema(String xml){
        Objects.requireNonNull(xml);
        XStream xstream = XStreamFactory.build();
        return new Schema((Schema) xstream.fromXML(xml));
    }

    public static Schema buildSchema(InputStream in){
        Objects.requireNonNull(in);
        try{
            String xml = IOUtils.toString(in);
            return buildSchema(xml);
        }
        catch (IOException e){
            throw new IllegalStateException(e);
        }
    }


    public static Transformer buildTransformer(String xml){
        Objects.requireNonNull(xml);
        XStream xstream = XStreamFactory.build();
        return (Transformer) xstream.fromXML(xml);
    }


    public static Transformer buildTransformer(InputStream in){
        Objects.requireNonNull(in);
        try{
            String xml = IOUtils.toString(in);
            return buildTransformer(xml);
        }
        catch (IOException e){
            throw new IllegalStateException(e);
        }
    }

    public static RelationalSchema buildRelationalSchema(String xml){
        Objects.requireNonNull(xml);
        XStream xstream = XStreamFactory.build();
        return (RelationalSchema) xstream.fromXML(xml);
    }

    public static RelationalSchema buildRelationalSchema(InputStream in){
        Objects.requireNonNull(in);
        try{
            String xml = IOUtils.toString(in);
            return buildRelationalSchema(xml);
        }
        catch (IOException e){
            throw new IllegalStateException(e);
        }
    }
}
