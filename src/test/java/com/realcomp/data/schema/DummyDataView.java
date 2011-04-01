package com.realcomp.data.schema;

import com.realcomp.data.record.Record;
import com.realcomp.data.trait.ViewFactory;
import com.realcomp.data.trait.*;

/**
 *
 * @author krenfro
 */

public class DummyDataView implements ViewFactory {


    private String test = "asdf";

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DummyDataView other = (DummyDataView) obj;
        if ((this.test == null) ? (other.test != null) : !this.test.equals(other.test))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.test != null ? this.test.hashCode() : 0);
        return hash;
    }

    @Override
    public void parse(Record record) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

}
