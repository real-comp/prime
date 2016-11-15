package com.realcomp.prime.record.io;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads, dynamically, properties of a JavaBean.
 *
 */
public class DynamicPropertyGetter{

    private static final Logger logger = Logger.getLogger(DynamicPropertyGetter.class.getName());

    private Set<String> ignoredProperties;

    public DynamicPropertyGetter(){
        ignoredProperties = new HashSet<String>();
    }

    public void addIgnoredProperty(String name){
        ignoredProperties.add(name);
    }

    /**
     * @param bean not null
     * @return all valid properties of the specified Object
     * @throws IntrospectionException
     */
    public Map<String, Object> getProperties(Object bean) throws IntrospectionException{

        if (bean == null){
            throw new IllegalArgumentException("javabean is null");
        }


        Map<String, Object> props = new HashMap<String, Object>();
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()){
            Object value = getProperty(pd, bean);
            if (value != null){
                props.put(pd.getName(), value);
            }
        }
        return props;

    }

    protected Object getProperty(PropertyDescriptor pd, Object o){

        try{
            Object value = pd.getReadMethod().invoke(o, (Object[]) null);
            return isValidProperty(pd.getName(), value) ? value : null;
        }
        catch (Exception ex){
            logger.log(Level.FINE,
                       String.format("Unable to set property [%s] on bean of class [%s]",
                                     new Object[]{pd.getName(), o.getClass().getName()}),
                       ex);
        }

        return null;
    }

    /**
     *
     * @param name
     * @param value
     * @return true if the property is valid.
     */
    protected boolean isValidProperty(String name, Object value){
        if (name == null || value == null){
            return false;
        }
        if (ignoredProperties.contains(name)){
            return false;
        }
        if (isDefaultValue(value)){
            return false;
        }

        return true;
    }

    /**
     * Override this method if you want different behavior.
     *
     * @param value
     * @return true if the value is a Number and equal to Zero; else false
     */
    protected boolean isDefaultValue(Object value){

        if (value instanceof Number && ((Number) value).equals(0)){
            return true;
        }

        return false;
    }
}
