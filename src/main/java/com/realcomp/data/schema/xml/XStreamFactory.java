package com.realcomp.data.schema.xml;

import com.realcomp.data.annotation.Converter;
import com.realcomp.data.annotation.Validator;
import com.realcomp.data.conversion.ComplexConverter;
import com.realcomp.data.conversion.SimpleConverter;
import com.realcomp.data.record.io.Format;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.RelationalSchema;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Table;
import com.realcomp.data.transform.Transformer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.LoggingMXBean;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;


/**
 * Creates correctly configured XStream instances.
 * 
 * 
 * @author krenfro
 */
public class XStreamFactory {


    /**
     * 
     * @return new XStream instance without pretty printing.
     */
    public static XStream build(){
        return build(false);
    }

    /**
     * 
     * @param pretty should serialization be pretty printed at the cost of performance.
     * @return 
     */
    public static XStream build(boolean pretty){
        

        //use reflection to find all Validatior and Converter annotated classes.
        Configuration conf = new ConfigurationBuilder()
            .setUrls(ClasspathHelper.getUrlsForPackagePrefix("com.realcomp"));
            //.setScanners(new TypeElementsScanner());

        //turn off INFO logging of Reflections
       // Logger.getLogger(Reflections.class.getName());
       // LoggingMXBean mxBean = LogManager.getLoggingMXBean();
       // mxBean.setLoggerLevel(Reflections.class.getName(), Level.WARNING.getName());
        
        XStream xstream = pretty ? new XStream() : new XStream(new StaxDriver());
        xstream.processAnnotations(Schema.class);
        xstream.processAnnotations(RelationalSchema.class);
        xstream.processAnnotations(Table.class);
        xstream.processAnnotations(Field.class);
        xstream.processAnnotations(SimpleConverter.class);
        xstream.processAnnotations(ComplexConverter.class);
        xstream.processAnnotations(FieldList.class);
        xstream.processAnnotations(Transformer.class);
        xstream.processAnnotations(Format.class);
        
        
        xstream.registerConverter(new OperationConverter());
        xstream.registerConverter(new DataTypeConverter());
        xstream.registerConverter(new FieldListConverter());
        xstream.registerConverter(new AttributesConverter());
        

        /* use reflection to get all classes on the classpath annotated as a validator or converter */
        Reflections reflections = new Reflections(conf);
        Set<Class<?>> validators = reflections.getTypesAnnotatedWith(Validator.class);
        Set<Class<?>> converters = reflections.getTypesAnnotatedWith(Converter.class);
        
        for (Class c: validators){
            Validator annotation = (Validator) c.getAnnotation(Validator.class);
            xstream.alias(annotation.value(), c);
            xstream.processAnnotations(c);
        }

        for (Class c: converters){
            Converter annotation = (Converter) c.getAnnotation(Converter.class);
            xstream.alias(annotation.value(), c);
            xstream.processAnnotations(c);
        }

        return xstream;
    }
}
