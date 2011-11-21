/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcomp.data.record;

import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class RecordKeyTest {
    
    public RecordKeyTest() {
    }

    @Test
    public void testEmptyString(){
        
        List<RecordKey> keys = RecordKey.parse("");
        assertTrue(keys.isEmpty());
    }
}
