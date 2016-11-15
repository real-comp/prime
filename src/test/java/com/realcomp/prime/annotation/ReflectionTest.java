package com.realcomp.prime.annotation;

import com.realcomp.prime.validation.field.LengthValidator;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Set;

import org.reflections.Reflections;

/**
 *
 */
public class ReflectionTest{

    @Test
    public void testValidatorAnnotations(){

        //Configuration conf = new ConfigurationBuilder()
        //       .setUrls(ClasspathHelper.getUrlsForPackagePrefix("com.realcomp.prime"));
        //.setScanners(new TypeElementsScanner());

        Reflections reflections = new Reflections("com.realcomp");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Validator.class);
        assertFalse(annotated.isEmpty());
        assertTrue(annotated.contains(LengthValidator.class));

    }
}