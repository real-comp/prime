package com.realcomp.data.view;

/**
 *
 * @author krenfro
 */
public class DummyView extends BaseRecordView{


    public String getValue(){
        return (String) record.get("value");
    }
    
}
