package com.github.baytransit;

public class StopFiveOneOne extends Stop {
    private short _lat;
    private short _lon;
    
    public StopFiveOneOne(String stopCode) {
        super(stopCode);
    }
    public void setStopLatLong(short lat, short lon) {
        _lat = lat;
        _lon = lon;
    }
}
