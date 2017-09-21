package com.realcomp.prime.schema;

import com.realcomp.prime.DataType;
import com.realcomp.prime.Operation;
import com.realcomp.prime.schema.xml.DataTypeConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * A named and typed field of a Schema.
 *
 */
@XStreamAlias("field")
@XmlRootElement
public class Field{

    /**
     * Some characters are discouraged for use in Field names, as they have special meaning when processing Records. .
     * (period) : delimiter used for nested Records. (e.g., "property.land.acres"); [ ] : open/close brackets are used
     * to identify pieces of a Record. (e.g., "property.entity[2].name")
     */
    public static final String[] INVALID_NAME_CHARACTERS = new String[]{".", "[", "]"};

    @XStreamAsAttribute
    @XmlAttribute
    private String name;

    @XStreamAsAttribute
    @XStreamConverter(DataTypeConverter.class)
    private DataType type = DataType.STRING;

    @XStreamImplicit
    private List<Operation> operations;

    @XStreamAsAttribute
    private int length;

    public Field(){
        operations = new ArrayList<>();
    }

    public Field(String name){
        this();
        checkName(name);
        this.name = name;
        this.type = DataType.STRING;
    }

    public Field(String name, DataType type){
        this(name);
        if (type == null){
            throw new IllegalArgumentException("type is null");
        }
        this.type = type;
    }

    public Field(String name, DataType type, int length){
        this(name, type);
        if (length < 0){
            throw new IllegalArgumentException("length < 0");
        }
        this.length = length;
    }

    public Field(Field copy){

        this();
        this.name = copy.name;
        this.type = copy.type;
        for (Operation op : copy.operations){
            operations.add(op.copyOf());
        }
        this.length = copy.length;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        checkName(name);
        this.name = name;
    }

    /**
     * Ensure that the provided name is valid. A valid name:
     * <ol>
     * <li>is not null</li>
     * <li>is not empty</li>
     * <li>does not contain and INVALID_NAME_CHARACTER</li>
     * </ol>
     *
     * @param name
     * @throws IllegalArgumentException if the name is invalid.
     */
    protected void checkName(String name){
        if (name == null){
            throw new IllegalArgumentException("name is null");
        }
        if (name.isEmpty()){
            throw new IllegalArgumentException("name is empty");
        }
        for (String s : INVALID_NAME_CHARACTERS){
            if (name.contains(s)){
                throw new IllegalArgumentException("field name contains invalid character: " + s);
            }
        }
    }

    public int getLength(){
        return length;
    }

    public void setLength(int length){
        if (length < 0){
            throw new IllegalArgumentException(
                    String.format("length: %s is out of range: (>=0)", length));
        }

        this.length = length;
    }

    /**
     *
     * @return all Operations to perform for this field. Null if none specified
     */
    public List<Operation> getOperations(){
        return operations;
    }

    /**
     *
     * @param operations Operations to perform for this field. null will clear any existing.
     */
    public void setOperations(List<Operation> operations){
        if (operations == null){
            throw new IllegalArgumentException("operations is null");
        }
        for (Operation op : operations){
            addOperation(op);
        }
    }

    public void addOperation(Operation operation){
        if (operation == null){
            throw new IllegalArgumentException("operation is null");
        }

        operations.add(operation.copyOf());
    }

    public void clearOperations(){
        operations.clear();
    }

    public DataType getType(){
        return type;
    }

    public void setType(DataType type){
        if (type == null){
            throw new IllegalArgumentException("type is null");
        }
        this.type = type;
    }

    public boolean isKey(){
        for (Operation op : operations){
            if (op instanceof com.realcomp.prime.validation.field.Key){
                return true;
            }
        }
        return false;
    }

    public boolean isForeignKey(){
        for (Operation op : operations){
            if (op instanceof com.realcomp.prime.validation.field.ForeignKey){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        return name;
    }

    /**
     * XStream does not invoke a constructor during de-serialization. It uses default JDK serialization. This method
     * allows me to do work that the default constructor does to ensure my object is de-serialized properly.
     *
     * @see java.io.ObjectInputStream
     * @return this
     */
    private Object readResolve(){
        if (operations == null){
            operations = new ArrayList<>();
        }
        return this;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final Field other = (Field) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)){
            return false;
        }
        if (this.type != other.type){
            return false;
        }
        if (this.operations != other.operations && (this.operations == null || !this.operations.equals(other.operations))){
            return false;
        }
        return this.length == other.length;
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 59 * hash + (this.operations != null ? this.operations.hashCode() : 0);
        hash = 59 * hash + this.length;
        return hash;
    }
}
