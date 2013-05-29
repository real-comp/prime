package com.realcomp.data.annotation;

import com.realcomp.data.annotation.Validator;
import com.realcomp.data.validation.field.LengthValidator;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Set;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 */
public class ReflectionTest{

    @Test
    public void testValidatorAnnotations(){

        //Configuration conf = new ConfigurationBuilder()
        //       .setUrls(ClasspathHelper.getUrlsForPackagePrefix("com.realcomp.data"));
        //.setScanners(new TypeElementsScanner());

        Reflections reflections = new Reflections("com.realcomp");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Validator.class);
        assertFalse(annotated.isEmpty());
        assertTrue(annotated.contains(LengthValidator.class));

    }
}