package com.realcomp.data.view;

import com.realcomp.data.record.Record;

/**
 *
 * @author krenfro
 */
public class ProperName implements DataView {

    //TODO: FINISH ME

    private String test = "asdf";


    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getFirst(){
        return null;
    }

    public boolean isCompanyName(){
        return false;
    }

    public void setCompanyName(boolean companyName){
        
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ProperName other = (ProperName) obj;
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
