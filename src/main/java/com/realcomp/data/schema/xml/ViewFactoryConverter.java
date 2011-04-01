package com.realcomp.data.schema.xml;

import com.realcomp.data.view.ViewFactory;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize a ViewFactory.
 * ViewFactories are optional, and if the class is not found on the classpath, a warning
 * is logged.
 * 
 * @author krenfro
 */
public class ViewFactoryConverter implements Converter{

    private static final Logger logger = Logger.getLogger(ViewFactoryConverter.class.getName());

    private DynamicPropertyGetter propertyReader;
    private DynamicPropertySetter propertyWriter;
    
    public ViewFactoryConverter(){
        propertyReader = new DynamicPropertyGetter();
        propertyReader.addIgnoredProperty("name");
        propertyWriter = new DynamicPropertySetter();
    }

    @Override
    public boolean canConvert(Class type){
        return ViewFactory.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {

        writer.startNode("viewFactory");
        writer.addAttribute("class", o.getClass().getName());
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

        try {
            Class viewFactoryClass = Class.forName(reader.getAttribute("class"));
            return (ViewFactory) viewFactoryClass.newInstance();
        }
        catch (IllegalAccessException ex) {
            throw new ConversionException(ex);
        }
        catch (InstantiationException ex) {
            throw new ConversionException(ex);
        }
        catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, "ViewFactory class not found.", ex);
            return null;
        }        
    }


}
