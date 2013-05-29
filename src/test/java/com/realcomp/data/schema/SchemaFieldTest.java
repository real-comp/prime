/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.data.schema;

import java.util.regex.Pattern;
import com.realcomp.data.conversion.Trim;
import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class SchemaFieldTest{

    public SchemaFieldTest(){
    }

    /**
     * Test of checkName method, of class Field.
     */
    @Test
    public void testCheckName(){
        Field instance = new Field();
        instance.checkName("asdf");


        try{
            instance.checkName(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            instance.checkName("");
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            instance.checkName("asdf.asdf");
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            instance.checkName("asdf[");
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected){
        }

        try{
            instance.checkName("asdf]");
            fail("should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected){
        }

    }

    @Test
    public void testCopyConstructor(){

        Field original = new Field("original");
        original.addOperation(new Trim());

        Field copy = new Field(original);
        assertEquals(original, copy);
        assertTrue(copy.getOperations().size() == 1);
        assertEquals(new Trim(), copy.getOperations().get(0));
        copy.clearOperations();

        assertTrue(copy.getOperations().size() == 0);
        assertTrue(original.getOperations().size() == 1);


    }

    @Test
    public void testClassifierRegex(){

        String NOT_A_TAB = "A-Za-z_0-9\"'\\.\\- ;&:/%\\(\\)\\{\\}\\[\\]\\\\";

        String tabDelimitedRecord =
                "0010010000013	2011	CITY OF HOUSTON	PO BOX 1562	 HOUSTON	TX        	77251-1562	 	  	0	   	COMMERCE 	  	 	0 COMMERCE	HOUSTON	77002	X1  	1	5457A	493L	5900.00	0	4001	0	 	E	CV	0	0	                    	  	1	0	44431	1.0200	N	N  	00	0	0	0	0	0	0	0	0	0	00	0	0	Noticed	Y	2011-05-06 00:00:00.000	N	2011-08-12 00:00:00.000	1995-11-22 00:00:00.000	00180	1988-01-02 00:00:00.000	ALL BLK 1	SSBB	 	 	001 040 048 061 265 268 281 576";
        String[] tokens = tabDelimitedRecord.split("\t");
        assertEquals(64, tokens.length);
        String regex = "[" + NOT_A_TAB + "]*(\\t[" + NOT_A_TAB + "]*){63}";
        Pattern p = Pattern.compile(regex);
        assertTrue(p.matcher(tabDelimitedRecord).matches());

        tabDelimitedRecord =
                "0010010000013	2011	CITY OF HOUSTON	PO BOX 1562	 HOUSTON	TX        	77251-1562	 	  	0	   	COMMERCE 	  	 	0 COMMERCE	HOUSTON	77002	X1  	1	5457A	493L	5900.00	0	4001	0	 	E	CV	0	0	                    	  	1	0	44431	1.0200	N	N  	00	0	0	0	0	0	0	0	0	0	00	0	0	Noticed	Y	2011-05-06 00:00:00.000	N	2011-08-12 00:00:00.000	1995-11-22 00:00:00.000	00180	1988-01-02 00:00:00.000	ALL BLK 1	SSBB	 	 	001 040 048 061 265 268 281 576";
        //against + [A-Za-z_0-9"'\.\- ;&:]+(\t[A-Za-z_0-9"'\.\- ;&:]+){63}
        regex = "[" + NOT_A_TAB + "]*(\\t[" + NOT_A_TAB + "]*){63}";
        p = Pattern.compile(regex);
        assertTrue(p.matcher(tabDelimitedRecord).matches());


        tabDelimitedRecord =
                "0010030000001	2011	BUFFALO BAYOU PARTNERSHIP	1113 VINE ST STE 200	 	HOUSTON	TX        	77002-1045	 	  	1019	   	COMMERCE	ST	  	 	1019 COMMERCE STHOUSTON	77002	X1  	1	5457A	493M	5900.00	0	4001	5001	1E	CV	1930	0	                    	  	2	0	8750	.2009	N	N  	0	0	0	0	0	0	00	0	0	0	0	0	0	0	Noticed	Y	2011-08-26 00:00:00.000	N		1995-11-22 00:00:00.000	00180	2002-05-02 00:00:00.000	.50 U/D INT IN TR 13 BLK 3	SSBB	 	 	001 040 048 061 265 268 281 576";
        tokens = tabDelimitedRecord.split("\t");
        assertEquals(65, tokens.length);
        regex = "[" + NOT_A_TAB + "]*(\\t[" + NOT_A_TAB + "]*){64}";
        p = Pattern.compile(regex);
        assertTrue(p.matcher(tabDelimitedRecord).matches());

    }
}
