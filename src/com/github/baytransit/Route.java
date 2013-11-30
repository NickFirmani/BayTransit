package com.github.baytransit;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Route implements Parcelable {
	
    private final String _routeCode = "invalid";
    private String _routeName;

    public void setRouteName(String name) {
        _routeName = name;
    }
    public String getRouteName() {
        return _routeName;
    }
    public String getRouteNameCode() {
        return _routeCode;
    }
    
    /** Parcelable required resources below */

    public int describeContents() {
        return 0;
    }

    public abstract void writeToParcel(Parcel out, int flags);
    
    public abstract void addStop(String args, Stop args2);

}
