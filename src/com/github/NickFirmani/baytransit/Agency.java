package com.github.NickFirmani.baytransit;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class Agency implements Parcelable {
	private String _nameCode;
	private String _displayName;
    private Boolean _hasDir;
    private int _imageid;
    private SparseArray<Route> _routes = new SparseArray<Route>();
    
    public Agency(String nameCode, int imageid, String dname) {
    	_nameCode = nameCode;
    	_imageid = imageid;
    	_hasDir = nameCode.equals("BART") ? false : true;
    	_displayName = dname;
    }
    private Agency(Parcel in) {
        _imageid = in.readInt();
        String [] tempstr = in.createStringArray();
        _nameCode = tempstr[0];
        _displayName = tempstr[1];
        _hasDir = _nameCode.equals("BART") ? false : true;
    }
    
	public String getNameCode() {
		return _nameCode;
	}
    
    public void setDisplayName(String dName) {
        _displayName = dName;
    }
    
	public String getDisplayName() {
		return _displayName;
	}
    
    public Boolean gethasDir() {
        return _hasDir;
    }

    public void setDir(Boolean hasDir) {
        _hasDir = hasDir;
    }

	public int getImageid() {
		return _imageid;
	}

	public void setImageid(int imageid) {
		this._imageid = imageid;
	}
	
	public void addRoute(String routeCode, Route route) {
        _routes.put(routeCode.hashCode(), route);
    }
	
	public void addRoute(int posNo, Route route) {
		_routes.put(posNo, route);
	}
	
    public Route getRoute(String routeCode) {
        return _routes.get(routeCode.hashCode());
    }
    
    public Route getRoute(int posNum) { //FIXME
    	return _routes.get(_routes.keyAt(posNum));
    }
    
    public SparseArray<Route> getAllRoutes() {
    	return _routes;
    }
    
    //zero is 511
    //one is nextbus
    public int getAPIstem() {
    	if (_nameCode.equals("BART") || _nameCode.equals("Caltrain") ||
    			_nameCode.equals("SamTrans") || _nameCode.equals("VTA") ||
    			_nameCode.equals("WESTCAT")) {
    		return 0;
    	} else {
    		return 1;
    	}
    }
    
    /** Parcelable resources below */

    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<Agency> CREATOR
		= new Parcelable.Creator<Agency>() {
    	public Agency createFromParcel(Parcel in) {
    		return new Agency(in);
    	}

    	public Agency[] newArray(int size) {
    		return new Agency[size];
    	}
    };
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(_imageid);
        String[] tempstr = {_nameCode, _displayName};
        out.writeStringArray(tempstr);
    }
}
