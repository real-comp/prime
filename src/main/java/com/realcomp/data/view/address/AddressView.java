package com.realcomp.data.view.address;

import com.realcomp.data.view.DataView;

/**
 *
 * @author krenfro
 */
public interface AddressView extends DataView{

    public String getAddress1();
    public void setAddress1(String address);

    public String getAddress2();
    public void setAddress2(String address);

    public String getAddress3();
    public void setAddress3(String address);

    public String getCity();
    public void setCity(String city);

    public String getState();
    public void setState(String state);

    public String getZip5();
    public void setZip5(String zip5);

    public String getZip4();
    public void setZip4(String zip4);

    public String getCrrt();
    public void setCrrt(String crrt);

    public String getFips();
    public void setFips(String fips);

    public AddressQuality getAddressQuality();
    public void setAddressQuality(AddressQuality quality);

}
