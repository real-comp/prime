package com.realcomp.prime.transform;

import com.realcomp.prime.MultiFieldOperation;
import com.realcomp.prime.Operation;
import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.RecordValueAssembler;
import com.realcomp.prime.record.RecordValueException;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
import com.realcomp.prime.schema.xml.FieldListConverter;
import com.realcomp.prime.validation.ValidationException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author krenfro
 */
@XStreamAlias("transform")
@XmlRootElement
public class Transformer{

    private static final Logger logger = Logger.getLogger(Transformer.class.getName());

    private List<Operation> before;
    private List<Operation> after;

    @XStreamConverter(FieldListConverter.class)
    private FieldList fields;
    private ValueSurgeon surgeon;

    public Transformer(){
        fields = new FieldList();
        surgeon = new ValueSurgeon();
    }

    public Transformer(Transformer copy){
        super();
        if (copy.before != null){
            setBefore(copy.before);
        }
        if (copy.after != null){
            setAfter(copy.after);
        }
        setFields(copy.fields);
    }

    public void transform(TransformContext context) throws ConversionException, ValidationException{

        if (surgeon == null){
            surgeon = new ValueSurgeon();
        }

        context.setFields(fields);

        for (Field field : fields){
            context.setKey(field.getName());
            if (surgeon == null){
                throw new IllegalStateException("surgeon is null");
            }
            Object result = surgeon.operate(getOperations(field), context);
            if (result != null){
                if (field.getType() == null){
                    throw new IllegalStateException("Field [" + field.getName() + "] does not have a type");
                }
                result = field.getType().coerce(result);
            }

            try{
                RecordValueAssembler.assemble(context.getRecord(), field.getName(), result);
            }
            catch (RecordValueException ex){
                throw new ConversionException(ex);
            }
        }
    }
    

    protected List<Operation> getOperations(Field field){

        List<Operation> operations = new ArrayList<>();
        if (before != null){
            operations.addAll(before);
        }
        operations.addAll(field.getOperations());
        if (after != null){
            operations.addAll(after);
        }
        return operations;
    }

    public void addField(Field field){
        fields.add(new Field(field));
    }

    public FieldList getFields(){
        return fields;
    }

    public void setFields(FieldList fields){
        if (fields == null){
            throw new IllegalArgumentException("fields is null");
        }

        this.fields.clear();
        this.fields = new FieldList(fields);
    }

    /**
     * @return all Operations to perform on all Fields after all Field specific operations are finished, or null if none
     *         specified.
     *
     */
    public List<Operation> getAfter(){
        return after;
    }

    /**
     * Set all Operations to perform on all Fields after all Field specific operations are finished.
     *
     * @param after null will clear existing list
     */
    public void setAfter(List<Operation> after){

        if (after == null){
            this.after = null;
        }
        else{
            if (this.after != null){
                this.after.clear();
            }
            for (Operation op : after){
                addAfter(op);
            }
        }
    }

    /**
     * Add an Operation to the after operations group, to be run after all Field specific Operations are performed.
     *
     * @param op not null
     */
    public void addAfter(Operation op){
        if (op == null){
            throw new IllegalArgumentException("op is null");
        }
        if (after == null){
            after = new ArrayList<Operation>();
        }

        this.after.add(op);
    }

    /**
     *
     * @return all Operations to perform on all Fields before any Field specific Operations are performed, or null if
     *         none specified.
     */
    public List<Operation> getBefore(){
        return before;
    }

    /**
     * Set all Operations to perform on all Fields before any Field specific Operations are performed.
     *
     * @param before null will clear list
     */
    public void setBefore(List<Operation> before){
        if (before == null){
            this.before = null;
        }
        else{
            if (this.before != null){
                this.before.clear();
            }
            for (Operation op : before){
                addBefore(op);
            }
        }
    }

    /**
     * Add an Operation to the before operations group, to be run before all Field specific Operations are performed.
     *
     * @param op not null, not a MultiFieldOperation
     */
    public void addBefore(Operation op){
        if (op == null){
            throw new IllegalArgumentException("op is null");
        }
        if (op instanceof MultiFieldOperation){
            throw new IllegalArgumentException(
                    "You cannot specify a MultiFieldOperation as a 'before' operation");
        }

        if (before == null){
            before = new ArrayList<Operation>();
        }
        this.before.add(op);
    }
    

    @Generated("NetBeans")
    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final Transformer other = (Transformer) obj;
        if (this.before != other.before && (this.before == null || !this.before.equals(other.before))){
            return false;
        }
        if (this.after != other.after && (this.after == null || !this.after.equals(other.after))){
            return false;
        }
        if (this.fields != other.fields && (this.fields == null || !this.fields.equals(other.fields))){
            return false;
        }
        return true;
    }

    @Generated("NetBeans")
    @Override
    public int hashCode(){
        int hash = 5;
        hash = 83 * hash + (this.before != null ? this.before.hashCode() : 0);
        hash = 83 * hash + (this.after != null ? this.after.hashCode() : 0);
        hash = 83 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        return hash;
    }
}
