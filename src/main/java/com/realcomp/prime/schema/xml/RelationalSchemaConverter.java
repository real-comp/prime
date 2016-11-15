package com.realcomp.prime.schema.xml;

import com.realcomp.prime.schema.RelationalSchema;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.schema.Table;
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
 * RelationalSchemaConverter
 *
 * @author krenfro
 */
public class RelationalSchemaConverter implements Converter{

    public RelationalSchemaConverter(){
    }

    @Override
    public boolean canConvert(Class type){
        return RelationalSchema.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc){
        RelationalSchema schema = (RelationalSchema) o;
        writer.addAttribute("name", schema.getName());
        writer.addAttribute("version", schema.getVersion());
        writeTables(schema.getTables(), writer);
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc){

        RelationalSchema schema = new RelationalSchema();
        schema.setName(reader.getAttribute("name"));
        schema.setVersion(reader.getAttribute("version"));
        List<Table> tables = new ArrayList<Table>();

        try{
            while (reader.hasMoreChildren()){
                reader.moveDown();

                Table table = readTable(reader);
                if (table != null){
                    tables.add(table);
                }
                reader.moveUp();
            }

            schema.setTables(tables);
        }
        catch (SchemaException e){
            throw new ConversionException(e.getMessage(), e);
        }

        return schema;
    }

    protected Table readTable(HierarchicalStreamReader reader) throws SchemaException{
        return readTable(null, reader);
    }

    protected Table readTable(Table parent, HierarchicalStreamReader reader) throws SchemaException{

        String tableName = reader.getAttribute("name");
        if (tableName == null){
            return null;
        }

        Table table = new Table(tableName);
        table.setParent(parent);

        while (reader.hasMoreChildren()){
            reader.moveDown();
            if (reader.getNodeName().equals("table")){
                table.add(readTable(table, reader));
            }
            reader.moveUp();
        }

        return table;
    }

    protected void writeTables(Collection<Table> tables, HierarchicalStreamWriter writer){
        if (tables != null){
            for (Table table : tables){
                writeTable(table, writer);
            }
        }
    }

    protected void writeTable(Table table, HierarchicalStreamWriter writer){

        if (table != null){
            writer.startNode("table");
            writer.addAttribute("name", table.getName());
            //writeKeys(table.getKeys(), writer);
            writeTables(table.getChildren(), writer);
            writer.endNode();
        }
    }
}
