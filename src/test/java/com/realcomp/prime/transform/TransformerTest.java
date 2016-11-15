/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.prime.transform;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.conversion.Trim;
import com.realcomp.prime.record.RecordKeyException;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
import com.realcomp.prime.validation.ValidationException;
import com.realcomp.prime.validation.field.RequiredValidator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class TransformerTest{

    public TransformerTest(){
    }

    @Test
    public void testA() throws ConversionException, ValidationException, RecordKeyException{

        Transformer t = new Transformer();
        FieldList fields = new FieldList();
        Field field = new Field("a");
        field.addOperation(new Trim());
        fields.add(field);
        t.setFields(fields);

        Record record = new Record();
        record.put("a", "asdf  ");
        record.put("b", "83838383");

        TransformContext context = new TransformContext();
        context.setRecord(record);

        assertEquals("asdf  ", record.get("a"));
        t.transform(context);
        assertEquals("asdf", record.get("a"));

        field.addOperation(new RequiredValidator());
        fields.add(field);
        t.setFields(fields);

    }
}
