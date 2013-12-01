package com.github.baytransit;

import java.util.LinkedHashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RouteFiveOneOne extends Route implements Parcelable {
    private String _routeName;
    private String _routeCode;
    private Boolean _directional;
    private String _direction1;
    private String _direction2;
    private Map<String, Stop> _stops1 = new LinkedHashMap<String, Stop>();
    private Map<String, Stop> _stops2 = new LinkedHashMap<String, Stop>();
    
    public RouteFiveOneOne(Parcel in) {
    	String[] tempsarr = in.createStringArray();
    	_routeName = tempsarr[0];
    	_routeCode = tempsarr[1];
    	_direction1 = tempsarr[2];
    	_direction2 = tempsarr[3];
    	_directional = in.createBooleanArray()[0];
    }
    
    public RouteFiveOneOne(String routeCode) {
        _routeCode = routeCode;
        _directional = false;
    }
    public RouteFiveOneOne(String routeCode, String routeName) {
    	_routeCode = routeCode;
    	_routeName = routeName;
    }

    public RouteFiveOneOne(String routeCode, Boolean directional) {
        _routeCode = routeCode;
        _directional = directional;
    }
    public void writeToParcel(Parcel out, int flags) {
    	try {
    		String[] temparr = {_routeName, _routeCode, _direction1, _direction2};
    		out.writeStringArray(temparr);
    		boolean[] tempbool = {_directional};
    		out.writeBooleanArray(tempbool);
    	} catch (NullPointerException e) { // Not good code, bool should be init.
    		Log.e("511WriteParcel", "Null pointer :(");
    	}
    }
    public void setDirectional(boolean val) {
    	_directional = val;
    }
    public void setRouteName(String name) {
        _routeName = name;
    }
    public String getRouteName() {
        return _routeName;
    }
    public String getRouteNameCode() {
        return _routeCode;
    }
    public void setDirNames(String[] names) {
    	if (names.length != 2) {
    		Log.e("Route511", "Malformed Args");
    	} else {
    		_direction1 = names[0];
    		_direction2 = names[1];
    	}
    }
        
    public void addStop(String stopCode, Stop stop, int dirNum) {
        if (dirNum == 1) {
            _stops1.put(stopCode, stop);
        } else if (dirNum == 2) {
            _stops2.put(stopCode,stop);
        } else {Log.e("Route511", "Malformed args");}
    }
    @Override
    public void addStop(String stopCode, Stop stop) {
        _stops1.put(stopCode, stop);
    }
    public Map<String, Stop> listStops(int dirNum) {
        if (dirNum == 1) {
            return _stops1;
        } else if (dirNum == 2) {
            return _stops2;
        } else {
        	Log.e("Route511", "Malformed args");
            return null;
        }
    }
    public Map<String, Stop> listStops() {
        return _stops1;
    }
    public static final Parcelable.Creator<RouteFiveOneOne> CREATOR
		= new Parcelable.Creator<RouteFiveOneOne>() {
    	public RouteFiveOneOne createFromParcel(Parcel in) {
    		return new RouteFiveOneOne(in);
    	}

    	public RouteFiveOneOne[] newArray(int size) {
    		return new RouteFiveOneOne[size];
    	}
    };
}
