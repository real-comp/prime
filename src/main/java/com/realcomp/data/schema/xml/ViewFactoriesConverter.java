package com.realcomp.data.schema.xml;

import com.realcomp.data.view.ViewFactory;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize a
 * Collection of ViewFactories
 * 
 * @author krenfro
 */
public class ViewFactoriesConverter implements Converter{

    private ViewFactoryConverter factoryConverter;
    
    public ViewFactoriesConverter(){
        factoryConverter = new ViewFactoryConverter();
    }

    @Override
    public boolean canConvert(Class type){
        return List.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        List<ViewFactory> factories = (List<ViewFactory>) o;
        if (!factories.isEmpty()){
            writeFactory(factories, writer, mc);
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

        List<ViewFactory> factories = new ArrayList<ViewFactory>();

        while (reader.hasMoreChildren()){
            reader.moveDown();

            if (reader.getNodeName().equals("viewFactory")){
                factories.add((ViewFactory) factoryConverter.unmarshal(reader, uc));
            }

            reader.moveUp();
        }
        
        return factories;
    }


    protected void writeFactory(
            List<ViewFactory> factories, HierarchicalStreamWriter writer, MarshallingContext mc){

        if (factories != null){
            for (ViewFactory factory: factories)
                writeTrait(factory, writer, mc);
        }
    }

    protected void writeTrait(
            ViewFactory factory, HierarchicalStreamWriter writer, MarshallingContext mc){

        factoryConverter.marshal(factory, writer, null);
    }
}
