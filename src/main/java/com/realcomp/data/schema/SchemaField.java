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
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
@XStreamAlias("field")
public class SchemaField {
    
    /**
     * Some characters are discouraged for use in SchemaField names, as they
     * have special meaning when processing Records.
     * . (period) : delimiter used for nested Records. (e.g., "property.land.acres");
     * [ ]        : open/close brackets are used to identify pieces of a Record.
     *              (e.g., "property.entity[2].name")
     */
    public static final String[] INVALID_NAME_CHARACTERS = new String[]{".","[","]"};
    
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
        checkName(name);
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
        if (length < 0)
            throw new IllegalArgumentException("length < 0");
        this.length = length;
    }

    public SchemaField(SchemaField copy){
        this.name = copy.name;
        this.type = copy.type;
        this.setOperations(copy.operations);
        this.length = copy.length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkName(name);
        this.name = name;
    }
    
    /**
     * Ensure that the provided name is valid.
     * A valid name:
     * <ol>
     *  <li>is not null</li>
     *  <li>is not empty</li>
     *  <li>does not contain and INVALID_NAME_CHARACTER</li>
     * </ol>
     * @param name 
     * @throws IllegalArgumentException if the name is invalid.
     */
    protected void checkName(String name){
        if (name == null)
            throw new IllegalArgumentException("schema field name is null");
        if (name.isEmpty())
            throw new IllegalArgumentException("schema field name is empty");
        for (String s: INVALID_NAME_CHARACTERS)
            if (name.contains(s))
                throw new IllegalArgumentException("schema field name contains invalid character: " + s);
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
        if (type == null)
            type = DataType.STRING;
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
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 59 * hash + (this.operations != null ? this.operations.hashCode() : 0);
        hash = 59 * hash + this.length;
        return hash;
    }
    
}
