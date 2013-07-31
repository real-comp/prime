package com.realcomp.data.record.io.delimited;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
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
    }

}