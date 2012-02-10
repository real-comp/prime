package com.realcomp.data.schema;

import com.realcomp.data.schema.xml.XStreamFactory;
import com.thoughtworks.xstream.XStream;
import java.io.InputStream;

/**
 *
 * @author krenfro
 */
public class SchemaFactory {


    public static Schema buildSchema(InputStream in){
        
        if (in == null)
            throw new IllegalArgumentException("InputStream is null");
        XStream xstream = XStreamFactory.build();    
        return (Schema) xstream.fromXML(in);
    }

    public static RelationalSchema buildRelationalSchema(InputStream in){
        
        if (in == null)
            throw new IllegalArgumentException("InputStream is null");
        XStream xstream = XStreamFactory.build();    
        return (RelationalSchema) xstream.fromXML(in);
    }
}
