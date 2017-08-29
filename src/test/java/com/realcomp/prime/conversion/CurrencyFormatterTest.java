package com.realcomp.prime.conversion;

import org.junit.Test;
import static org.junit.Assert.*;

public class CurrencyFormatterTest{


    @Test
    public void convertWithFractionDigits() throws Exception{
        CurrencyFormatter f = new CurrencyFormatter();
        assertEquals(null, f.convert(null));
        assertEquals("", f.convert(""));
        assertEquals("$28,822.50", f.convert("28822.50"));
        assertEquals("$4,848.00", f.convert( "4848" ));
        assertEquals("$4,848.00", f.convert( "4848.00" ));
        assertEquals("$12.10", f.convert( "12.1" ));
        assertEquals("$12.10", f.convert( "12.1 " ));
        assertEquals("$54,511,212,151.22", f.convert( "54511212151.22" ));
        assertEquals("$1.00", f.convert( "00000001" ));
    }

    @Test
    public void convertWithNoFractionDigits() throws Exception{
        CurrencyFormatter f = new CurrencyFormatter(0, 0);
        assertEquals(null, f.convert(null));
        assertEquals("", f.convert(""));
        assertEquals("$28,822", f.convert("28822.50"));
        assertEquals("$4,848", f.convert( "4848" ));
        assertEquals("$4,848", f.convert( "4848.00" ));
        assertEquals("$12", f.convert( "12.1" ));
        assertEquals("$12", f.convert( "12.1 " ));
        assertEquals("$54,511,212,151", f.convert( "54511212151.22" ));
        assertEquals("$1", f.convert( "00000001" ));
    }


}