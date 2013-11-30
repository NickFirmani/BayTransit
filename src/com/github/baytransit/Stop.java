package com.github.baytransit;

public abstract class Stop {
    private String _stopCode;
    private String _stopName;
    public Stop(String stopCode) {
        _stopCode = stopCode;
    }
    public String getStopCode() {
        return _stopCode;
    }
    public void setStopName(String name) {
        _stopName = name;
    }
    public String getStopName() {
        return _stopName;
    }
}
    
