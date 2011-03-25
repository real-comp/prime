package com.realcomp.data.schema.xml;

import com.realcomp.data.schema.ForeignKey;
import com.realcomp.data.schema.Key;
import com.realcomp.data.schema.RelationalSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.Table;
import com.realcomp.data.view.DataView;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize a
 * Collection of DataViews
 * 
 * @author krenfro
 */
public class DataViewsConverter implements Converter{

    private DataViewConverter dataViewConverter;
    
    public DataViewsConverter(){
        dataViewConverter = new DataViewConverter();
    }

    @Override
    public boolean canConvert(Class type){
        return List.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        List<DataView> views = (List<DataView>) o;
        if (!views.isEmpty()){
            writeViews(views, writer, mc);
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

        List<DataView> views = new ArrayList<DataView>();

        while (reader.hasMoreChildren()){
            reader.moveDown();

            if (reader.getNodeName().equals("view")){
                views.add((DataView) dataViewConverter.unmarshal(reader, uc));
            }

            reader.moveUp();
        }
        
        return views;
    }


    protected void writeViews(
            List<DataView> views, HierarchicalStreamWriter writer, MarshallingContext mc){

        if (views != null){
            for (DataView view: views)
                writeView(view, writer, mc);
        }
    }

    protected void writeView(
            DataView view, HierarchicalStreamWriter writer, MarshallingContext mc){

        dataViewConverter.marshal(view, writer, null);
    }
}
