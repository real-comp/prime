package com.realcomp.prime.record.io.delimited;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class UnterminatedQuotedStringMechanicTest {

    public UnterminatedQuotedStringMechanicTest() {
    }

    @Test
    public void testRepairs(){

        UnterminatedQuotedStringMechanic mechanic = new UnterminatedQuotedStringMechanic();

        String problem = "\"101247473946\",\"CRYER, DUANE PETE\"\",\"PANA\",\"IL\",\"62557\",\"RR 3 BOX 250B\",\"\",\"\"";
        String expected = "\"101247473946\",\"CRYER, DUANE PETE\\\"\",\"PANA\",\"IL\",\"62557\",\"RR 3 BOX 250B\",\"\",\"\"";
        assertEquals(expected, mechanic.repair(problem));
        //if it is already escaped, don't escape it again
        assertEquals(expected, mechanic.repair(expected));


        problem =  "18 R000034990,9934859,3/12/2003 12:00:00 AM,7/2/1997 12:00:00 AM,42,1722,LINDSEY ALICE E TRUST ESTATE \",";
        expected = "18 R000034990,9934859,3/12/2003 12:00:00 AM,7/2/1997 12:00:00 AM,42,1722,LINDSEY ALICE E TRUST ESTATE \"\",";
        assertEquals(expected, mechanic.repair(problem));
        //if it is already escaped, don't escape it again
        assertEquals(expected, mechanic.repair(expected));


        problem = "2016,\"R006935\",\"180270000102003000\",\"180270000102003000\",\"R\",\"BARRETT DOYCE N\",\"\\\",\"\",\"\",\"216 S BAY DR\"";
        expected = "2016,\"R006935\",\"180270000102003000\",\"180270000102003000\",\"R\",\"BARRETT DOYCE N\",\"\\\\\",\"\",\"\",\"216 S BAY DR\"";
        assertEquals(expected, mechanic.repair(problem));
        //if it is already escaped, don't escape it again
        assertEquals(expected, mechanic.repair(expected));

    }

}