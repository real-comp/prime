package com.realcomp.data.schema;

import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.Table;
import com.realcomp.data.schema.xml.RelationalSchemaConverter;
import com.realcomp.data.view.DataView;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author krenfro
 */
@XStreamAlias("relational-schema")
@XStreamConverter(RelationalSchemaConverter.class)
public class RelationalSchema {

    protected String name;
    protected String version;
    protected Set<Table> tables;
    protected List<DataView> views;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<DataView> getViews() {
        return views;
    }

    public void setViews(List<DataView> views) throws SchemaException {
        if (views == null){
            this.views = null;
        }
        else{
            if (this.views != null)
                this.views.clear();
            for (DataView d: views)
                addView(d);
        }
    }

    public void addView(DataView view) throws SchemaException{
        if (view == null)
            throw new IllegalArgumentException("view is null");

        if (views == null)
            views = new ArrayList<DataView>();
        views.add(view);
    }

    public Set<Table> getTables() {
        return tables;
    }

    public void setTables(Collection<Table> tables) throws SchemaException {
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
        
        if (tables == null)
            tables = new HashSet<Table>();
        
        if (!tables.add(table)){
             throw new SchemaException(
                String.format(
                    "A table with name [%s] is already defined in schema [%s].",
                    name,
                    this.toString()));
        }
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder(name);
        if (version != null && !version.isEmpty())
            s.append(" (").append(version).append(")");
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RelationalSchema other = (RelationalSchema) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
            return false;
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version))
            return false;
        if (this.tables != other.tables && (this.tables == null || !this.tables.equals(other.tables)))
            return false;
        if (this.views != other.views && (this.views == null || !this.views.equals(other.views)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 53 * hash + (this.tables != null ? this.tables.hashCode() : 0);
        hash = 53 * hash + (this.views != null ? this.views.hashCode() : 0);
        return hash;
    }

}
