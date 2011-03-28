package com.realcomp.data.schema.xml;

import com.realcomp.data.schema.RelationalSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.schema.Table;
import com.realcomp.data.view.DataView;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize a
 * RelationalSchemaConverter
 * 
 * @author krenfro
 */
public class RelationalSchemaConverter implements Converter{

    private DataViewConverter dataViewConverter;
    
    public RelationalSchemaConverter(){
        dataViewConverter = new DataViewConverter();
    }

    @Override
    public boolean canConvert(Class type){
        return RelationalSchema.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        RelationalSchema schema = (RelationalSchema) o;
        writer.addAttribute("name", schema.getName());
        writer.addAttribute("version", schema.getVersion());
        writeViews(schema.getViews(), writer, mc);
        writeTables(schema.getTables(), writer);
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

        RelationalSchema schema = new RelationalSchema();
        schema.setName(reader.getAttribute("name"));
        schema.setVersion(reader.getAttribute("version"));
        List<Table> tables = new ArrayList<Table>();

        try{
            while (reader.hasMoreChildren()){
                reader.moveDown();

                if (reader.getNodeName().equals("view")){
                    schema.addView((DataView) dataViewConverter.unmarshal(reader, uc));
                }
                else{
                    Table table = readTable(reader);
                    if (table != null)
                        tables.add(table);
                }
                reader.moveUp();
            }

            schema.setTables(tables);
        }
        catch(SchemaException e){
            throw new ConversionException(e.getMessage(), e);
        }
        
        return schema;
    }


    protected Table readTable(HierarchicalStreamReader reader) throws SchemaException{
        return readTable(null, reader);
    }
    
    protected Table readTable(Table parent, HierarchicalStreamReader reader) throws SchemaException{

        String tableName = reader.getAttribute("name");
        if (tableName == null)
            return null;

        Table table = new Table(tableName);
        table.setParent(parent);
        
        while(reader.hasMoreChildren()){
            reader.moveDown();
            if (reader.getNodeName().equals("table")){
                table.addTable(readTable(table, reader));
            }
            else{
                table.addKey(readKey(reader));
            }
            reader.moveUp();
        }

        return table;
    }

    protected SchemaField readKey(HierarchicalStreamReader reader){
        String name = reader.getAttribute("name");
        //SchemaField field = new SchemaField();
        //return reader.getNodeName().equals("key") ? new Key(name) : new ForeignKey(name);

        //TODO
        return null;
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


    protected void writeTables(List<Table> tables, HierarchicalStreamWriter writer){
        if (tables != null){
            for (Table table: tables)
                writeTable(table, writer);
        }
    }

    protected void writeTable(Table table, HierarchicalStreamWriter writer){

        if (table != null){
            writer.startNode("table");
            writer.addAttribute("name", table.getName());
//            writeKeys(table.getKeys(), writer);
            //writeTables(table.getTables(), writer);
            writer.endNode();
        }
    }

    /*
    protected void writeKeys(List<Key> keys, HierarchicalStreamWriter writer){
        if (keys != null){
            for (Key key: keys)
                writeKey(key, writer);
        }
    }


    protected void writeKey(Key key, HierarchicalStreamWriter writer){
        if (key != null){
            writer.startNode(key instanceof ForeignKey ? "foreign" : "key");
            writer.addAttribute("name", key.getName());
            writer.endNode();
        }
    }
     * 
     */
}
