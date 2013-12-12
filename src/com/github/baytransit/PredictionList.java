package com.github.baytransit;

import java.util.ArrayList;

public class PredictionList {
	private ArrayList<String[]> _predLineList;
	//String array in format Seconds, routeCode, routeName, dirName
	private String _code;
	
	public PredictionList(String stopC) {
		_code = stopC;
		_predLineList = new ArrayList<String[]>();
	}
	
	public String getCode() {
		return _code;
	}
	public void addPrediction(String routeCode, String routeName, String dirName, String minutes) {
		int seconds = Integer.parseInt(minutes) * 60;
		String[] temp = {Integer.toString(seconds), routeCode, routeName, dirName};
		_predLineList.add(temp);
	}
	public void addPrediction(String routeCode, String routeName, String dirName, String minutes, String seconds) {
		String[] temp = {seconds, routeCode, routeName, dirName};
		_predLineList.add(temp);
	}
	public ArrayList<String[]> getList() {
		return _predLineList;
	}
}