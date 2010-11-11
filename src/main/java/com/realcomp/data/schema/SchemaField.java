package com.realcomp.data.schema;

import com.realcomp.data.schema.xml.DataTypeConverter;
import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author krenfro
 */
//@XStreamConverter(SchemaFieldConverter.class)
@XStreamAlias("field")
public class SchemaField {

    @XStreamAsAttribute
    protected String name;
    
    @XStreamAsAttribute
    @XStreamConverter(DataTypeConverter.class)
    protected DataType type = DataType.STRING;

    @XStreamImplicit
    protected List<Operation> operations;

    @XStreamAsAttribute
    protected int length;

    public SchemaField(){
    }

    public SchemaField(String name){
        this();
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    public SchemaField(String name, DataType type){
        this(name);
        if (type == null)
            throw new IllegalArgumentException("type is null");
        this.type = type;
    }

    public SchemaField(String name, DataType type, int length){
        this(name, type);
        if (length <= 0)
            throw new IllegalArgumentException("length <= 0");
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        if (length <= 0)
            throw new IllegalArgumentException(
                    String.format("length: %s is out of range: (>1)", length));
        
        this.length = length;
    }

    /**
     *
     * @return all Operations to perform for this field. Null if none specified
     */
    public List<Operation> getOperations() {
        return operations;
    }

    /**
     *
     * @param operations Operations to perform for this field. null will clear any existing.
     */
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public void addOperation(Operation operation){
        if (operation == null)
            throw new IllegalArgumentException("operation is null");

        if (operations == null)
            operations = new ArrayList<Operation>();
        
        operations.add(operation);
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        if (type == null)
            throw new IllegalArgumentException("type is null");
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SchemaField other = (SchemaField) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        if (this.type != other.type)
            return false;
        if (this.operations != other.operations && (this.operations == null || !this.operations.equals(other.operations)))
            return false;
        if (this.length != other.length)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 59 * hash + (this.operations != null ? this.operations.hashCode() : 0);
        hash = 59 * hash + this.length;
        return hash;
    }
}
