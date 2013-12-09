package com.github.baytransit;

import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public abstract class Route implements Parcelable {
	
    private final String _routeCode = "invalid";
    private String _routeName;
    private String _direction1;
    private String _direction2;

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
    		Log.e("RouteNextBus", "Malformed Args");
    	} else {
    		_direction1 = names[0];
    		_direction2 = names[1];
    	}
    }
    public String getDirNames(int n) {
    	if (n == 0) {
    		return _direction1;
    	} else {
    		return _direction2;
    	}
    }
    
    /** Parcelable required resources below */

    public int describeContents() {
        return 0;
    }

    public abstract void writeToParcel(Parcel out, int flags);
    
    public abstract void addStop(String args, Stop args2);
    
    public abstract Map<String, Stop> listStops(int dirNum);

}
