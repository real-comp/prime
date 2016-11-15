package com.realcomp.prime.schema;

import com.realcomp.prime.schema.xml.XStreamFactory;
import com.realcomp.prime.transform.Transformer;
import com.thoughtworks.xstream.XStream;
import java.io.InputStream;

/**
 *
 * @author krenfro
 */
public class SchemaFactory{


    public static Schema buildSchema(String xml){

        if (xml == null){
            throw new IllegalArgumentException("xml is null");
        }
        XStream xstream = XStreamFactory.build();
        return new Schema((Schema) xstream.fromXML(xml));
    }

    public static Schema buildSchema(InputStream in){

        if (in == null){
            throw new IllegalArgumentException("InputStream is null");
        }
        XStream xstream = XStreamFactory.build();
        return new Schema((Schema) xstream.fromXML(in));
    }


    public static Transformer buildTransformer(String xml){
        if (xml == null){
            throw new IllegalArgumentException("xml is null");
        }
        XStream xstream = XStreamFactory.build();
        return (Transformer) xstream.fromXML(xml);
    }


    public static Transformer buildTransformer(InputStream in){
        if (in == null){
            throw new IllegalArgumentException("inputStream is null");
        }
        XStream xstream = XStreamFactory.build();
        return (Transformer) xstream.fromXML(in);
    }

    public static RelationalSchema buildRelationalSchema(String xml){

        if (xml == null){
            throw new IllegalArgumentException("xml is null");
        }
        XStream xstream = XStreamFactory.build();
        return (RelationalSchema) xstream.fromXML(xml);
    }

    public static RelationalSchema buildRelationalSchema(InputStream in){

        if (in == null){
            throw new IllegalArgumentException("InputStream is null");
        }
        XStream xstream = XStreamFactory.build();
        return (RelationalSchema) xstream.fromXML(in);
    }
}
