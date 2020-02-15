package com.sgdata.mapping.client.beans;

/**
 *
 * @author Francisco Ruiz
 */

public class InfoMappingDTO {
    private String curp;
    private String rfc;
    private boolean lco;
    
    public InfoMappingDTO() { }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public boolean isLco() {
        return lco;
    }

    public void setLco(boolean lco) {
        this.lco = lco;
    }
    
}
