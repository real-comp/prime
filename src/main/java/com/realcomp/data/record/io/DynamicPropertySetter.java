package com.realcomp.data.record.io;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes properties, dynamically, to a JavaBean
 *
 * @author krenfro
 */
public class DynamicPropertySetter {

    private static final Logger logger = Logger.getLogger(DynamicPropertySetter.class.getName());
    
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
     * @return list of property keys not set because the bean did not expose the property.
     * @throws DynamicPropertyException
     */
    public Set<String> setProperties(Object bean, Map<String, String> properties) throws IntrospectionException {
        
        Set<String> unused = new HashSet<String>();
        if (properties == null || properties.isEmpty())
            return unused;
        
        unused.addAll(properties.keySet());
        
        String name = null;
        String value = null;
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            name = pd.getName();
            value = properties.get(name);
            
            if (value != null){
                try {
                    if (setProperty(name, value, pd, bean)){
                        unused.remove(name);
                    }
                }
                catch (Exception ex) {
                    logger.log(Level.FINE,
                            String.format("Unable to set property [%s] on bean of class [%s]", 
                                          new Object[]{name, bean.getClass().getName()}), 
                            ex);
                }
            }
        }


        return unused;
    }

    
    protected boolean setProperty(String name, String value, PropertyDescriptor pd, Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if (name == null)
            throw new IllegalArgumentException("name is null");
        if (bean == null)
            throw new IllegalArgumentException("bean is null");
        if (pd == null)
            throw new IllegalArgumentException("pd is null");
        if (name.isEmpty())
            throw new IllegalArgumentException("name is empty");

        boolean success = false;

        if (isValidProperty(name, value)){

            if (pd.getPropertyType().isEnum()) {
                success = setEnum(name, value, pd, bean);
            }
            else{
                success = setObject(name, value, pd, bean);
            }
        }
        
        return success;
    }
    
    /**
     * @param name
     * @param value
     * @param pd
     * @param bean
     * @return
     */
    protected boolean setObject(String name, String value, PropertyDescriptor pd, Object bean) 
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {


        boolean success = false;

        Method setter = pd.getWriteMethod();
        if (setter != null){
            Class[] parameterTypes = setter.getParameterTypes();
         

                if (parameterTypes.length > 0) {
                    Class parameterType = parameterTypes[0];

                    if (parameterType == String.class) {
                        setter.invoke(bean, value);
                        success = true;
                    }
                    else if(parameterType == Integer.class || parameterType.getName().equals("int")) {
                        setter.invoke(bean, Integer.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Float.class || parameterType.getName().equals("float")) {
                        setter.invoke(bean, Float.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Double.class || parameterType.getName().equals("double")) {
                        setter.invoke(bean, Double.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Long.class || parameterType.getName().equals("long")) {
                        setter.invoke(bean, Long.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Short.class || parameterType.getName().equals("short")) {
                        setter.invoke(bean, Short.valueOf(value));
                        success = true;
                    }
                    else if (parameterType == Boolean.class || parameterType.getName().equals("boolean")) {
                        setter.invoke(bean, Boolean.valueOf(value));
                        success = true;
                    }
                    else if ((parameterType == Character.class || parameterType.getName().equals("char")) && value.length() == 1) {
                        setter.invoke(bean, Character.valueOf(value.charAt(0)));
                        success = true;
                    }
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
    protected boolean setEnum(String name, String value, PropertyDescriptor pd, Object bean) 
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        assert (name != null);
        assert (value != null);
        assert (pd != null);
        assert (bean != null);

        boolean success = false;
        Class propertyType = pd.getPropertyType();

        
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
        
        return success;
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
