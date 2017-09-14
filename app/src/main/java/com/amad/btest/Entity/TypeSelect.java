package com.amad.btest.Entity;

/**
 * Created by pushparajparab on 9/13/17.
 */

public class TypeSelect {
    private Double _avg;
    private String _minor;

    @Override
    public String toString() {
        return "TypeSelect{" +
                "_avg=" + _avg +
                ", _minor='" + _minor + '\'' +
                '}';
    }

    public TypeSelect(Double avg, String minor)
    {
        this._avg= avg;
        this._minor = minor;
    }
    public Double getAvg() {
        return _avg;
    }

    public void setAvg(Double avg) {
        this._avg = avg;
    }

    public String getMinor() {
        return _minor;
    }

    public void setMinor(String minor) {
        this._minor = minor;
    }
}
