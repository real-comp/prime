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
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

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

        new FastClasspathScanner("com.realcomp")
                .matchClassesWithAnnotation(Validator.class,
                        c -> {
                            xstream.alias(c.getAnnotation(Validator.class).value(), c);
                            xstream.processAnnotations(c);
                        })
                .matchClassesWithAnnotation(Converter.class,
                        c -> {
                            xstream.alias(c.getAnnotation(Converter.class).value(), c);
                            xstream.processAnnotations(c);
                        })
                .scan();


        return xstream;
    }
}
