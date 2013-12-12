package com.github.baytransit;

public abstract class Stop {
    protected String _stopCode;
    private String _stopName;
    public Stop(String stopCode) {
        _stopCode = stopCode;
    }
    public Stop(String stopCode, String stopName) {
    	_stopCode = stopCode;
    	_stopName = stopName;
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
    
