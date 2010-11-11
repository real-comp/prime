package com.realcomp.data.schema.xml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads, dynamically, properties of a JavaBean.
 *
 * @author krenfro
 */
public class PropertyReader {


    /**
     * @param bean not null
     * @return all valid properties of the specified Object
     * @throws EncodingException
     */
    public Map<String,Object> read(Object bean) throws DynamicPropertyException{

        if (bean == null)
            throw new IllegalArgumentException("javabean is null");
        try {
            Map<String, Object> props = new HashMap<String, Object>();
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                Object value = getProperty(pd, bean);
                if (value != null)
                    props.put(pd.getName(), value);
            }
            return props;
        }
        catch (IllegalArgumentException ex) {
            throw new DynamicPropertyException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new DynamicPropertyException(ex);
        }
        catch (InvocationTargetException ex) {
            throw new DynamicPropertyException(ex);
        }
        catch (IntrospectionException ex) {
            throw new DynamicPropertyException(ex);
        }        
    }

    protected Object getProperty(PropertyDescriptor pd, Object o)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{

        Object value = pd.getReadMethod().invoke(o, (Object[]) null);
        return isValidProperty(pd.getName(), value) ? value : null;
    }

    /**
     *
     * @param name
     * @param value
     * @return true if the property is valid.
     */
    protected boolean isValidProperty(String name, Object value){
        if (name == null || value == null)
            return false;
        if (isDefaultValue(value))
            return false;
        return true;
    }

    /**
     * Override this method if you want different behavior.
     * @param value
     * @return true if the value is a Number and equal to Zero; else false
     */
    protected boolean isDefaultValue(Object value){

        if (value instanceof Number && ((Number) value).equals(0))
            return true;

        return false;
    }

}
