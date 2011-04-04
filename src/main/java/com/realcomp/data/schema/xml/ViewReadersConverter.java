package com.realcomp.data.schema.xml;

import com.realcomp.data.view.ViewReader;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize a
 * Collection of ViewReader classes
 * 
 * @author krenfro
 */
public class ViewReadersConverter implements Converter{

    protected static final Logger logger = Logger.getLogger(ViewReadersConverter.class.getName());

    @Override
    public boolean canConvert(Class type){
        return List.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        List<Class> readers = (List<Class>) o;
        if (!readers.isEmpty()){
            for (Class c: readers){
                writer.startNode("reader");
                writer.addAttribute("class", c.getName());
                writer.endNode();
            }
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

        List<Class> readers = new ArrayList<Class>();

        while (reader.hasMoreChildren()){
            reader.moveDown();

            if (reader.getNodeName().equals("reader")){

                String classname = reader.getAttribute("class");
                try {
                    readers.add(Class.forName(classname));
                }
                catch (ClassNotFoundException ex) {
                    logger.log(
                            Level.WARNING, "Unable to locate ViewReader class " + classname, ex);
                }
            }

            reader.moveUp();
        }
        
        return readers;
    }
}
