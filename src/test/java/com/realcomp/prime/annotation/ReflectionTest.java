package com.realcomp.prime.annotation;

import com.realcomp.prime.validation.field.LengthValidator;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReflectionTest{

    @Test
    public void testValidatorAnnotations(){
        List<Class> annotated = new ArrayList<>();
        new FastClasspathScanner("com.realcomp")
                .matchClassesWithAnnotation(Validator.class,
                        c -> annotated.add(c))
                .matchClassesWithAnnotation(Converter.class,
                        c -> annotated.add(c))
                .scan();
        assertFalse(annotated.isEmpty());
        assertTrue(annotated.contains(LengthValidator.class));
    }
}