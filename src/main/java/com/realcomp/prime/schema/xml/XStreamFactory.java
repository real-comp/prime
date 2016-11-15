package com.realcomp.prime.schema.xml;

import com.realcomp.prime.annotation.Converter;
import com.realcomp.prime.annotation.Validator;
import com.realcomp.prime.conversion.ComplexConverter;
import com.realcomp.prime.conversion.SimpleConverter;
import com.realcomp.prime.conversion.Trim;
import com.realcomp.prime.record.io.Format;
import com.realcomp.prime.schema.*;
import com.realcomp.prime.transform.Transformer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Creates correctly configured XStream instances.
 *
 *
 */
public class XStreamFactory{

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

        /*
         * //use reflection to find all Validatior and Converter annotated classes.
         * Configuration conf = new ConfigurationBuilder()
         * .setUrls(ClasspathHelper.getUrlsForPackagePrefix("com.realcomp"));
         * //.setScanners(new TypeElementsScanner());
         */

        Reflections reflections = new Reflections("com.realcomp");

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
        xstream.processAnnotations(Trim.class);


        xstream.registerConverter(new OperationConverter());
        xstream.registerConverter(new DataTypeConverter());
        xstream.registerConverter(new FieldListConverter());
        xstream.registerConverter(new AttributesConverter());


        /* use reflection to get all classes on the classpath annotated as a validator or converter */
        //Reflections reflections = new Reflections(conf);
        Set<Class<?>> validators = reflections.getTypesAnnotatedWith(Validator.class);
        Set<Class<?>> converters = reflections.getTypesAnnotatedWith(Converter.class);

        for (Class c : validators){
            Validator annotation = (Validator) c.getAnnotation(Validator.class);
            xstream.alias(annotation.value(), c);
            xstream.processAnnotations(c);
        }

        for (Class c : converters){
            Converter annotation = (Converter) c.getAnnotation(Converter.class);
            xstream.alias(annotation.value(), c);
            xstream.processAnnotations(c);
        }

        return xstream;
    }
}
