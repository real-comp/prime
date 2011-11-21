package com.realcomp.data.schema;

import com.realcomp.data.schema.xml.XStreamFactory;
import com.thoughtworks.xstream.XStream;
import java.io.InputStream;

/**
 *
 * @author krenfro
 */
public class SchemaFactory {


    public static FileSchema buildFileSchema(InputStream in){
        
        if (in == null)
            throw new IllegalArgumentException("InputStream is null");
        XStream xstream = XStreamFactory.build();    
        return (FileSchema) xstream.fromXML(in);
    }

    public static RelationalSchema buildRelationalSchema(InputStream in){
        
        if (in == null)
            throw new IllegalArgumentException("InputStream is null");
        XStream xstream = XStreamFactory.build();    
        return (RelationalSchema) xstream.fromXML(in);
    }
}
