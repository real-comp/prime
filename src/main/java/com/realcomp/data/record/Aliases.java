package com.realcomp.data.record;

import com.realcomp.data.Operation;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author krenfro
 */
public class Aliases {
    
    
    /**
     * build the alias map for all fields in the schema.
     * @param schema
     * @return  
     */
    public static Map<String, List<String>> getAliases(FileSchema schema){
        Map<String,List<String>> aliases = new HashMap<String,List<String>>();
        for (Map.Entry<Pattern, List<SchemaField>> fields: schema.getFields().entrySet()){
            for (SchemaField field: fields.getValue()){
                List<String> definedAliases = getAliases(field);
                if (definedAliases != null)
                    aliases.put(field.getName(), definedAliases);
            }
        }
        return aliases;
    }
    
    /**
     * Aliases are defined as Alias converters in the schema.
     * 
     * @param field
     * @return all aliases for the specified field, or null if none defined.
     * @see com.realcomp.data.conversion.Alias
     */
    public static List<String> getAliases(SchemaField field){
        
        List<String> retVal = null;
        List<Operation> operations = field.getOperations();
        if (operations != null){
            for (Operation operation: operations){
                if (operation instanceof Alias){
                    String alias = ((Alias) operation).getName();
                    if (alias != null){
                        if (retVal == null)
                             retVal = new ArrayList<String>();
                        
                        retVal.add(alias);
                    }
                }
            }
        }
        
        return retVal;
    }

    
}
