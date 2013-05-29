package com.realcomp.data.schema;

import com.realcomp.data.schema.xml.RelationalSchemaConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.HashSet;
import java.util.Set;

/**
 * A Table in a RelationalSchema
 *
 * @author krenfro
 */
@XStreamAlias("table")
public class Table{

    @XStreamAsAttribute
    protected String name;
    
    @XStreamConverter(RelationalSchemaConverter.class)
    protected Set<Table> children;
    protected transient Table parent;

    protected Table(){
    }

    public Table(String name){
        if (name == null){
            throw new IllegalArgumentException("name is null");
        }

        this.name = name;
    }

    public Table getParent(){
        return parent;
    }

    public void setParent(Table parent){
        this.parent = parent;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        if (name == null){
            throw new IllegalArgumentException("name is null");
        }

        this.name = name;
    }

    public Set<Table> getChildren(){
        return children;
    }

    public boolean hasChildren(){
        return children != null && !children.isEmpty();
    }

    public void setChildren(Set<Table> tables) throws SchemaException{

        if (this.children != null){
            this.children.clear();
        }
        for (Table table : tables){
            add(table);
        }
    }

    public void add(Table table) throws SchemaException{
        if (table == null){
            throw new IllegalArgumentException("table is null");
        }

        if (children == null){
            children = new HashSet<Table>();
        }
        table.setParent(this);
        if (!children.add(table)){
            throw new SchemaException(
                    String.format(
                    "A table with name [%s] is already defined in %s",
                    name,
                    this.toString()));
        }
    }

    @Override
    public String toString(){
        return String.format("Table[%s]", name);
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final Table other = (Table) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)){
            return false;
        }
        if (this.children != other.children && (this.children == null || !this.children.equals(other.children))){
            return false;
        }
        //if (this.parent != other.parent && (this.parent == null || !this.parent.equals(other.parent)))
        //    return false;
        return true;
    }

    @Override
    public int hashCode(){
        int hash = 3;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + (this.children != null ? this.children.hashCode() : 0);
        //hash = 89 * hash + (this.parent != null ? this.parent.hashCode() : 0);
        return hash;
    }
}
