package com.realcomp.data.model;

import com.realcomp.data.Field;
import com.realcomp.data.record.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krenfro
 */
public class DataModel{

    protected List<String> dataViewClassNames;
    protected Record data;

    public Field get(String name){
        return data == null ? null : data.get(name);
    }

    public Field put(String name, Field value){
        if (data == null)
            data = new Record();
        return data.put(name, value);
    }

    public Record getData() {
        return data;
    }

    public void setData(Record data) {
        if (data == null)
            throw new IllegalArgumentException("data is null");
        this.data = data;
    }
    
    /**
     *
     * @return List of DataView class names this DataModel supports
     */
    public List<String> getDataViewClassNames() {
        return dataViewClassNames;
    }

    /**
     *
     * @param dataViewClassNames List of DataView class names this DataModel supports. may be null.
     */
    public void setDataViewClassNames(List<String> dataViewClassNames) {
        this.dataViewClassNames = dataViewClassNames;
    }

    /**
     * Adds a DataModel class name that this DataModel supports.
     * @param className
     */
    public void addDataViewClassName(String className){
        if (className == null)
            throw new IllegalArgumentException("className is null");
        if (className.isEmpty())
            throw new IllegalArgumentException("className is empty");

        if (dataViewClassNames == null)
            dataViewClassNames = new ArrayList<String>();
        dataViewClassNames.add(className);
    }


    /**
     * Find and instantiate a DataView that implements the specified DataView class.
     * @param dataViewClass not null
     * @return a DataView instance for the specified DataView class
     * @throws IllegalArgumentException if the DataView class is null or not supported by this model.
     */
    public ViewFactory getDataView(Class<ViewFactory> dataViewClass){

        if (dataViewClass == null)
            throw new IllegalArgumentException("dataViewClass is null");

        try {
            Class<ViewFactory> implementation = getImplementation(dataViewClass);
            if (implementation == null)
                throw new IllegalArgumentException(
                        "dataView is not supported: " + dataViewClass.getName());
            return implementation.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(
                    "Unable to instantiate DataView class: " + dataViewClass.getName(), ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(
                    "Unable to instantiate DataView class: " + dataViewClass.getName(), ex);
        }
    }


    /**
     * Returns a class that implements the specified dataViewClass, or null if not supported.
     *
     * @param dataViewClass not null
     * @return a DataView class or null
     */
    protected Class<ViewFactory> getImplementation(Class<ViewFactory> dataViewClass){

        if (dataViewClass == null)
            throw new IllegalArgumentException("dataViewClass is null");

        if (dataViewClassNames == null || dataViewClassNames.isEmpty())
            return null;

        for (String className : dataViewClassNames) {
            try {
                @SuppressWarnings("unchecked")
                Class<ViewFactory> c = (Class<ViewFactory>) Class.forName(className, true, null);
                if (c.isAssignableFrom(dataViewClass))
                    return c;
            }
            catch(ClassCastException ex){
                Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
                throw new IllegalStateException(
                        "Unable to cast the specified class as a DataView: " + className, ex);
            }
            catch (ClassNotFoundException ex) {
                Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
                throw new IllegalStateException("Unable to load DataView class: " + className, ex);
            }
        }
        
        return null;
    }

    /**
     * Determines whether this DataModel supports the specified DataView class
     * @param dataViewClass not null
     * @return true if this DataModel supports the specified DataView; else false
     */
    public boolean supports(Class<ViewFactory> dataViewClass) {

        if (dataViewClass == null)
            throw new IllegalArgumentException("dataViewClass is null");

        return getImplementation(dataViewClass) != null;
    }
}
