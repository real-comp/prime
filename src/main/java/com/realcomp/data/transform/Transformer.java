package com.realcomp.data.transform;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.RecordValueAssembler;
import com.realcomp.data.record.RecordValueException;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.Field;
import com.realcomp.data.validation.ValidationException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author krenfro
 */
@XStreamAlias("transform")
@XmlRootElement
public class Transformer {
    
    private List<Operation> before;
    private List<Operation> after;
    
    @XStreamImplicit
    private List<Field> fields;
    private ValueSurgeon surgeon;
    
    public Transformer(){
        fields = new FieldList();
        surgeon = new ValueSurgeon();
    }
    
    public Transformer(Transformer copy){
        super();
        if (copy.before != null)
            setBefore(copy.before);
        if (copy.after != null)
            setAfter(copy.after);
        setFields(copy.fields);
    }
    
    public void transform(TransformContext context) throws ConversionException, ValidationException{
        
        context.setFields(fields);
        
        for (Field field: fields){            
            context.setKey(field.getName());            
            Object result = surgeon.operate(getOperations(field), context);
            
            try {
                RecordValueAssembler.assemble(context.getRecord(), field.getName(), result);
            }
            catch (RecordValueException ex) {
                throw new ConversionException(ex);
            }
        }        
    }
    
    protected List<Operation> getOperations(Field field){
        
        List<Operation> operations = new ArrayList<Operation>();
        if (before != null)
            operations.addAll(before);
        operations.addAll(field.getOperations());
        if (after != null)
            operations.addAll(after);
        return operations;
    }
    
    public void addField(Field field){
        fields.add(new Field(field));
    }
    

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        if (fields == null)
            throw new IllegalArgumentException("fields is null");
        
        this.fields.clear();
        for (Field field: fields)
            addField(field);
    }

    
    /**
     * @return all Operations to perform on all Fields after all Field specific operations are
     * finished, or null if none specified.
     *
     */
    public List<Operation> getAfter() {
        return after;
    }

    /**
     * Set all Operations to perform on all Fields after all Field specific operations are finished.
     * @param after null will clear existing list
     */
    public void setAfter(List<Operation> after) {

        if (after == null){
            this.after = null;
        }
        else{
            if (this.after != null)
                this.after.clear();
            for (Operation op: after)
                addAfter(op);
        }
    }

    /**
     * Add an Operation to the after operations group, to be run after all Field specific
     * Operations are performed.
     * @param op not null
     */
    public void addAfter(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");
        if (after == null)
            after = new ArrayList<Operation>();

        this.after.add(op);
    }
    
    
    /**
     *
     * @return all Operations to perform on all Fields before any Field specific Operations are
     * performed, or null if none specified.
     */
    public List<Operation> getBefore() {
        return before;
    }

    /**
     * Set all Operations to perform on all Fields before any Field specific Operations are
     * performed.
     * @param before null will clear list
     */
    public void setBefore(List<Operation> before) {
        if (before == null){
            this.before = null;
        }
        else{
            if (this.before != null)
                this.before.clear();
            for (Operation op: before)
                addBefore(op);
        }
    }

    /**
     * Add an Operation to the before operations group, to be run before all Field specific
     * Operations are performed.
     * @param op not null, not a MultiFieldOperation
     */
    public void addBefore(Operation op){
        if (op == null)
            throw new IllegalArgumentException("op is null");
        if (op instanceof MultiFieldOperation)
            throw new IllegalArgumentException(
                    "You cannot specify a MultiFieldOperation as a 'before' operation");

        if (before == null)
            before = new ArrayList<Operation>();
        this.before.add(op);
    }


    
}
