package com.realcomp.data.schema.xml;

import com.realcomp.data.annotation.Converter;
import com.realcomp.data.annotation.Validator;
import com.realcomp.data.conversion.ComplexConverter;
import com.realcomp.data.conversion.SimpleConverter;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.RelationalSchema;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.Table;
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
 *
 * @author krenfro
 */
public class XStreamFactory {



    public static XStream build(){

        //use reflection to find all Validatior and Converter annotated classes.
        Configuration conf = new ConfigurationBuilder()
            .setUrls(ClasspathHelper.getUrlsForPackagePrefix("com.realcomp"));
            //.setScanners(new TypeElementsScanner());

        //turn off INFO logging of Reflections
        Logger.getLogger(Reflections.class.getName());
        LoggingMXBean mxBean = LogManager.getLoggingMXBean();
        mxBean.setLoggerLevel(Reflections.class.getName(), Level.WARNING.getName());
        
        Reflections reflections = new Reflections(conf);
        Set<Class<?>> validators = reflections.getTypesAnnotatedWith(Validator.class);
        Set<Class<?>> converters = reflections.getTypesAnnotatedWith(Converter.class);

        XStream xstream = new XStream(new StaxDriver());
        xstream.processAnnotations(FileSchema.class);
        xstream.processAnnotations(RelationalSchema.class);
        xstream.processAnnotations(Table.class);
        xstream.processAnnotations(Field.class);
        xstream.processAnnotations(SimpleConverter.class);
        xstream.processAnnotations(ComplexConverter.class);
        xstream.registerConverter(new OperationConverter());
        xstream.registerConverter(new DataTypeConverter());
        xstream.registerConverter(new DelimiterConverter());
        xstream.registerConverter(new FieldListConverter());
        
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
