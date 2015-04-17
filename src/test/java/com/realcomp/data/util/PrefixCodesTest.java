package com.realcomp.data.util;

import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

public class PrefixCodesTest {

    public PrefixCodesTest() {
    }
    
    @Test
    public void testPrefixCodes(){
        
        Properties props = new Properties();
        props.setProperty("ABC", "yes");
        props.setProperty("MH", "yes");
        PrefixCodes codes = new PrefixCodes(props);
        assertEquals("yes", codes.translate("ABC"));
        assertEquals("yes", codes.translate("ABCD"));
        assertEquals("yes", codes.translate("ABCDE"));
        assertNull(codes.translate("AB"));
        assertEquals("yes", codes.translate("MH"));
        assertEquals("yes", codes.translate("MH/XYZ"));
        
        
    }

}