package com.github.baytransit;

import com.github.baytransit.Stop;

public class StopNextBus extends Stop {
	private double _lat;
	private double _lon;

	public StopNextBus(String stopCode) {
		super(stopCode);
	}
	public StopNextBus(String stopCode, String stopName) {
		super(stopCode, stopName);
	}
	public StopNextBus(String stopCode, String stopName, double lat, double lon) {
		super(stopCode, stopName);
		_lat = lat;
		_lon = lon;
	}
	public double[] getLatLon() {
		double[] ret = {_lat, _lon};
		return ret;
	}
}
