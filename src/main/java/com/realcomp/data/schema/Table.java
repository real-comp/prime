package com.realcomp.data.schema;

import com.realcomp.data.schema.xml.RelationalSchemaConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author krenfro
 */
@XStreamAlias("table")
public class Table {

    @XStreamAsAttribute
    protected String name;    
    
    @XStreamImplicit
    protected Set<SchemaField> keys;

    @XStreamConverter(RelationalSchemaConverter.class)
    protected Set<Table> tables;
    
    protected transient Table parent;

    protected Table(){
    }

    public Table(String name){
        if (name == null)
            throw new IllegalArgumentException("name is null");

        this.name = name;
    }

    public Table getParent() {
        return parent;
    }

    public void setParent(Table parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("name is null");
        
        this.name = name;
    }

    public Set<SchemaField> getKeys(){
        return keys;
    }

    public void setKeys(Set<SchemaField> keys) throws SchemaException {
        this.keys = keys;
    }


    public void addKey(SchemaField key) throws SchemaException{
        if (key == null)
            throw new IllegalArgumentException("key is null");

        if (keys == null)
            keys = new HashSet<SchemaField>();
        if (!keys.add(key)){
            throw new SchemaException(
                String.format(
                    "A key field with name [%s] is already defined in %s",
                    name,
                    this.toString()));
        }
    }
    
    public Set<Table> getTables() {
        return tables;
    }

    public void setTables(Set<Table> tables) throws SchemaException {
        this.tables = tables;
    }

    public void addTable(Table table) throws SchemaException{
        if (table == null)
            throw new IllegalArgumentException("table is null");

        if (tables == null)
            tables = new HashSet<Table>();
        table.setParent(this);
        if (!tables.add(table)){
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
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Table other = (Table) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        if (this.keys != other.keys && (this.keys == null || !this.keys.equals(other.keys)))
            return false;
        if (this.tables != other.tables && (this.tables == null || !this.tables.equals(other.tables)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 31 * hash + (this.keys != null ? this.keys.hashCode() : 0);
        hash = 31 * hash + (this.tables != null ? this.tables.hashCode() : 0);
        return hash;
    }

    
}
