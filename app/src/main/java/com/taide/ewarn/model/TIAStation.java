package com.taide.ewarn.model;

import org.litepal.crud.LitePalSupport;

public class TIAStation extends LitePalSupport {
    private String SN="";
    private String NETCODE="";
    private String STACODE="";
    private String DEVTYPE="";
    private String LONGITUDE="";
    private String LATITUDE="";
    private String SSID = "";
    private String PWD = "123456";

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getNETCODE() {
        return NETCODE;
    }

    public void setNETCODE(String NETCODE) {
        this.NETCODE = NETCODE;
    }

    public String getSTACODE() {
        return STACODE;
    }

    public void setSTACODE(String STACODE) {
        this.STACODE = STACODE;
    }

    public String getDEVTYPE() {
        return DEVTYPE;
    }

    public void setDEVTYPE(String DEVTYPE) {
        this.DEVTYPE = DEVTYPE;
    }

    public String getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public String getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getPWD() {
        return PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    @Override
    public String toString() {
        return SN + "|" + NETCODE + "|" + STACODE + "|" + DEVTYPE;
    }




}
