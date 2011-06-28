package com.realcomp.data.schema;

import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.RelationalSchema;
import com.realcomp.data.schema.Table;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author krenfro
 */
public class PrettyPrinter {
    
    private RelationalSchema relationalSchema;
    private Map<String,FileSchema> schemas;

    public PrettyPrinter(){
        schemas = new HashMap<String,FileSchema>();
    }
    
    public RelationalSchema getRelationalSchema() {
        return relationalSchema;
    }

    public void setRelationalSchema(RelationalSchema relationalSchema) {
        this.relationalSchema = relationalSchema;
    }
    
    public void addFileSchema(FileSchema schema){
        schemas.put(schema.getName(), schema);
    }
    
    public void addFileSchemas(List<FileSchema> schemas){
        for (FileSchema s: schemas)
            addFileSchema(s);
    }
    
    public void prettyPrint(Record record, OutputStream out){
        
        if (record == null)
            throw new IllegalArgumentException("record is null");
        if (relationalSchema == null)
            throw new IllegalArgumentException("");
        
        Table primaryTable = relationalSchema.getTables().iterator().next();
        Map<String,Object> map = new HashMap<String,Object>();
        for (Map.Entry<String,Object> e: record.entrySet())
            map.put(e.getKey(), e.getValue());
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        prettyPrint(primaryTable, map, 0, writer);
        writer.flush();
    }
    
    private void prettyPrint(Table table, Map<String,Object> map, int indent, PrintWriter out){
        
        for (int x = 0; x < indent; x++)
            out.print(" ");
        
        FileSchema schema = schemas.get(table.getName());
        out.print(table.getName());
        out.print("[");
        if (schema == null)
            out.print(map.toString());        
        else
            out.print(schema.toString(new Record(map)));
        out.print("]");
        out.println();
        
        if (table.hasChildren()){
            for (Table child: table.getChildren()){                
                List<Map<String,Object>> data = (List<Map<String,Object>>) map.get(child.getName());
                if (data != null && !data.isEmpty()){                    
                    for (Map d: data)
                        prettyPrint(child, d, indent+4, out);
                }
            }
        }
    }
}
