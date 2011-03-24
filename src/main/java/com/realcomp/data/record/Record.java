package com.realcomp.data.record;

import com.realcomp.data.MapField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Simply a MapField with support for the setting of 'id' field names.
 * A record may have more than one 'id' field.  These fields
 * are used to construct a record 'id' that may be useful.
 * By default, the 'id' is the value of the first field in the map.
 * Typically, a field is marked as id="true" in the schema.
 *
 * @author krenfro
 */
public class Record extends MapField implements Serializable{

    private static final long serialVersionUID = 1L;
    protected List<String> idFieldNames;

    public Record(){
        idFieldNames = new ArrayList<String>();
    }

    public List<String> getIdFieldNames() {
        return idFieldNames;
    }

    public void setIdFieldNames(List<String> idFieldNames) {
        if (idFieldNames == null)
            throw new IllegalArgumentException("idFieldNames is null");
        this.idFieldNames = idFieldNames;
    }

    /**
     * Some fields in a record may be the unique identifier for the record.
     * You can set these here to get special help in toString()
     * @param name
     */
    public void addIdFieldName(String name){
        if (name == null)
            throw new IllegalArgumentException("name is null");
        idFieldNames.add(name);
    }

    /**
     *
     * @return the Id of the record as identified by the Id field names property of this class,
     * or the value of the first field if none were set, or null if there are no fields.
     */
    public String getId(){

        String id = null;
        if (!wrapped.isEmpty()){
            if (idFieldNames.isEmpty()){
                id = wrapped.values().iterator().next().getValue().toString();
            }
            else{
                StringBuilder s = new StringBuilder();
                boolean needsDelimiter = false;
                for (String name: idFieldNames){
                    if (needsDelimiter)
                        s.append("|");
                    s.append(get(name).getValue().toString());
                    needsDelimiter = true;
                }
                id = s.toString();
            }
        }

        return id;
    }

    /**
     * @return the id of the record, or super.toString() if no id is available.
     */
    @Override
    public String toString(){
        String id = getId();
        return id == null ? super.toString() : id;
    }
}
