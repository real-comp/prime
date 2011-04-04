package com.realcomp.data.view;

/**
 *
 * @author krenfro
 */
public class ExampleView{

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ExampleView other = (ExampleView) obj;
        if ((this.data == null) ? (other.data != null) : !this.data.equals(other.data))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

}
