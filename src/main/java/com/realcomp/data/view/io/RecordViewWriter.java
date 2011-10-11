package com.realcomp.data.view.io;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.io.RecordWriter;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import com.realcomp.data.view.RecordView;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author krenfro
 */
public interface RecordViewWriter {
    
    public void setRecordWriter(RecordWriter writer);
    
    public RecordWriter getRecordWriter();
    
    /**
     * @param clazz a RecordView class
     * @return true if this reader supports reading instances of the provided RecordView class
     */
    boolean supports(Class clazz);

    /**
     * Set all the RecordView class names this reader supports.
     * @param viewClassNames All the view class names this reader supports
     * @throws IllegalArgumentException if one of the view classes specified was not found.
     */
    void setViews(List<String> viewClassNames);
    
    /**
     *
     * @return list of RecordView class names this reader supports. never null
     */
    List<String> getViews();

    /**
     * write a RecordView
     * @param view the RecordView to write; not null
     * @throws IOException
     */
    void write(RecordView view)
            throws IOException, ValidationException, ConversionException, SchemaException;


}
