package com.realcomp.data.schema.xml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Writes properties, dynamically, to a JavaBean
 *
 * @author krenfro
 */
public class DynamicPropertySetter {

    private Set<String> ignoredProperties;

    public DynamicPropertySetter(){
        ignoredProperties = new HashSet<String>();
    }

    public void addIgnoredProperty(String name){
        ignoredProperties.add(name);
    }


    /**
     *
     * @param bean
     * @param properties to be applied
     * @throws DynamicPropertyException
     */
    public void setProperties(Object bean, Map<String, String> properties)
            throws DynamicPropertyException {

        if (properties == null || properties.isEmpty())
            return;

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                setProperty(pd.getName(), properties.get(pd.getName()), pd, bean);
            }
        }
        catch (IntrospectionException ex) {
            throw new DynamicPropertyException(ex);
        }
        catch (IllegalStateException ex) {
            throw new DynamicPropertyException(ex);
        }
        catch (IllegalArgumentException ex) {
            throw new DynamicPropertyException(ex);
        }
    }

    /**
     * @param name
     * @param value
     * @param pd
     * @param bean
     * @return
     */
    protected boolean setObject(String name, String value, PropertyDescriptor pd, Object bean) {


        boolean success = false;

        Method setter = pd.getWriteMethod();
        if (setter != null){
            Class[] parameterTypes = setter.getParameterTypes();
            try {

                if (parameterTypes.length > 0) {
                    Class parameterType = parameterTypes[0];

                    if (parameterType == String.class) {
                        setter.invoke(bean, value);
                        success = true;
                    }
                    else if(parameterType == Integer.class) {
                        setter.invoke(bean, Integer.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Float.class) {
                        setter.invoke(bean, Float.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Double.class) {
                        setter.invoke(bean, Double.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Long.class) {
                        setter.invoke(bean, Long.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Short.class) {
                        setter.invoke(bean, Short.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Boolean.class) {
                        setter.invoke(bean, Boolean.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Character.class && value.length() == 1) {
                        setter.invoke(bean, Character.valueOf(value.charAt(0)));
                        success = true;
                    }
                }
            }
            catch (IllegalAccessException ex) {
            }
            catch (IllegalArgumentException ex) {
            }
            catch (InvocationTargetException ex) {
            }
        }

        return success;
    }

    /**
     * Converts the <i>value</i> to the enum described by the PropertyDescriptor
     * and sets the value on the <i>bean</i> object.
     * 
     * @param name name of the javabean property to be set.
     * @param value value of the enum as a string
     * @param pd
     * @param bean The javabean that has a set method that accepts an enum
     * @return true if successful; else false
     */
    protected boolean setEnum(String name, String value, PropertyDescriptor pd, Object bean) {

        assert (name != null);
        assert (value != null);
        assert (pd != null);
        assert (bean != null);

        boolean success = false;
        Class propertyType = pd.getPropertyType();

        try {
            try {
                pd.getWriteMethod().invoke(bean, Enum.valueOf(propertyType, value));
                success = true;
            }
            catch (IllegalArgumentException e) {

                //check if the emum has a parse(String) method, and use it if it does.
                Method parseMethod = propertyType.getDeclaredMethod("parse", String.class);
                Enum parsedEnum = (Enum) parseMethod.invoke(
                        propertyType.getEnumConstants()[0], value);
                pd.getWriteMethod().invoke(bean, parsedEnum);
                success = true;
            }
        }
        catch (IllegalAccessException ex) {
        }
        catch (InvocationTargetException ex) {
        }
        catch (NoSuchMethodException ex) {
        }
        catch (SecurityException ex) {
        }

        return success;
    }

    protected void setProperty(String name, String value, PropertyDescriptor pd, Object bean) {

        if (name == null)
            throw new IllegalArgumentException("name is null");
        if (bean == null)
            throw new IllegalArgumentException("bean is null");
        if (pd == null)
            throw new IllegalArgumentException("pd is null");
        if (name.isEmpty())
            throw new IllegalArgumentException("name is empty");


        if (isValidProperty(name, value)){

            boolean success = false;

            if (pd.getPropertyType().isEnum()) {
                success = setEnum(name, value, pd, bean);
            }
            else{
                success = setObject(name, value, pd, bean);
            }

            if (!success)
                throw new IllegalStateException("unable to set property for: " + name);
        }
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
        if (ignoredProperties.contains(name))
            return false;

        return true;
    }

}
