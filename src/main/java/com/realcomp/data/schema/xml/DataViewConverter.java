package com.realcomp.data.schema.xml;

import com.realcomp.data.view.DataView;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize a DataView
 * DataViews are optional, and if the class is not found on the classpath, a warning
 * is logged.
 * 
 * @author krenfro
 */
public class DataViewConverter implements Converter{

    private static final Logger logger = Logger.getLogger(DataViewConverter.class.getName());

    private PropertyReader propertyReader;
    private PropertyWriter propertyWriter;
    
    public DataViewConverter(){
        propertyReader = new PropertyReader();
        propertyReader.addIgnoredProperty("name");
        propertyWriter = new PropertyWriter();
    }

    @Override
    public boolean canConvert(Class type){
        return DataView.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {

        try {
            writer.startNode("view");
            for(Map.Entry<String,Object> entry: propertyReader.read(o).entrySet()){

                if (entry.getValue() instanceof Class)
                    writer.addAttribute(entry.getKey(), ((Class)entry.getValue()).getName().toString());
                else
                    writer.addAttribute(entry.getKey(), entry.getValue().toString());
            }
            writer.endNode();
        }
        catch (DynamicPropertyException ex) {
            throw new ConversionException(ex);
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

        try {
            Class dataViewClass = Class.forName(reader.getAttribute("class"));
            DataView dataView = (DataView) dataViewClass.newInstance();
            Map<String,String> properties = new HashMap<String,String>();
            Iterator<String> itr = reader.getAttributeNames();
            while(itr.hasNext()){
                String name = itr.next();
                String value = reader.getAttribute(name);
                if (!name.equals("class") && value != null)
                    properties.put(name, value);
            }
            propertyWriter.write(dataView, properties);
            return dataView;
        }
        catch (DynamicPropertyException ex) {
            throw new ConversionException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new ConversionException(ex);
        }
        catch (InstantiationException ex) {
            throw new ConversionException(ex);
        }
        catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, "DataView class not found.", ex);
            return null;
        }        
    }


}
