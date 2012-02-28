package com.realcomp.data.record.io;

import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public abstract class BaseRecordReader extends BaseRecordReaderWriter implements RecordReader{

    private static final Logger logger = Logger.getLogger(BaseRecordReader.class.getName());
    
    
    public BaseRecordReader(){
        super();
    }
    
    public BaseRecordReader(BaseRecordReader copy){
        super(copy);
    }
}
