package com.realcomp.data.model;

import com.realcomp.data.record.Record;
import com.realcomp.data.schema.RelationalSchema;
import java.util.List;

/**
 *
 * @author krenfro
 */
public class DataModelBuilder {

    public static DataModel build(RelationalSchema schema, List<Record> records){

        if (records == null)
            throw new IllegalArgumentException("records is null");
        if (records.isEmpty())
            throw new IllegalArgumentException("records is empty");
        
        DataModel dataModel = new DataModel();

        if (records.size() == 1 && schema.getTables().size() == 1){
            dataModel.setData(records.get(0));
        }
        else{
            //HARD
            
        }
        
        return dataModel;
    }
}
