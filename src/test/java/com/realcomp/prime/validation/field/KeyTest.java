package com.realcomp.prime.validation.field;

import com.realcomp.prime.Operation;
import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class KeyTest{

    public KeyTest(){
    }

    @Test
    public void testHashCode() throws Exception{
        List<Operation> keys = new ArrayList<Operation>();
        for (int x = 0; x < 10; x++){
            keys.add(new Key());
        }

        int expected = keys.get(0).hashCode();
        for (Operation key : keys){
            assertEquals(expected, key.hashCode());
        }
    }
}
