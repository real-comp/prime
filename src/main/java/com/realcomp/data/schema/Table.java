package com.realcomp.data.schema;

import com.realcomp.data.schema.xml.RelationalSchemaConverter;
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
@XStreamAlias("table")
public class Table {

    @XStreamAsAttribute
    protected String name;    
    
    @XStreamImplicit
    protected List<Key> keys;

    @XStreamConverter(RelationalSchemaConverter.class)
    protected List<Table> tables;
    
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

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) throws SchemaException {
        if (keys == null){
            this.keys = null;
        }
        else{
            if (this.keys != null)
                this.keys.clear();
            for (Key k: keys)
                addKey(k);
        }

    }

    public void addKey(Key key) throws SchemaException{
        if (key == null)
            throw new IllegalArgumentException("key is null");
        verifyUniqueKeyName(key.getName());
        if (keys == null)
            keys = new ArrayList<Key>();
        keys.add(key);
    }


    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) throws SchemaException {

        if (tables == null){
            this.tables = null;
        }
        else{
            if (this.tables != null)
                this.tables.clear();
            for (Table t: tables)
                addTable(t);
        }
    }

    public void addTable(Table table) throws SchemaException{
        if (table == null)
            throw new IllegalArgumentException("table is null");

        verifyUniqueTableName(table.getName());
        if (tables == null)
            tables = new ArrayList<Table>();
        table.setParent(this);
        tables.add(table);
    }

    protected void verifyUniqueTableName(String name) throws SchemaException{
        if (tables != null){
            for (Table t: tables)
                if (t.getName().equals(name))
                     throw new SchemaException(
                        String.format(
                            "A table with name [%s] is already defined in %s",
                            name,
                            this.toString()));
        }
    }


     protected void verifyUniqueKeyName(String name) throws SchemaException{
        if (keys != null){
            for (Key k: keys)
                if (k.getName().equals(name))
                     throw new SchemaException(
                        String.format(
                            "A key with name [%s] is already defined in %s",
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
