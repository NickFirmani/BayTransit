package com.github.NickFirmani.baytransit;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class Agency implements Parcelable {
	private final String _agencyCode;
	private final String _agencyName;
    private final Boolean _hasDir;
    private final int _imageId;
    private AlphaHashSparseArray<Route> _routes = new AlphaHashSparseArray<Route>();
    
    public Agency(String agencyCode, int imageId, String agencyName) {
    	_agencyCode = agencyCode;
    	_imageId = imageId;
    	_hasDir = agencyCode.equals("BART") ? false : true;
    	_agencyName = agencyName;
    }
    private Agency(Parcel in) {
        _imageId = in.readInt();
        String [] tempstr = in.createStringArray();
        _agencyCode = tempstr[0];
        _agencyName = tempstr[1];
        _hasDir = _agencyCode.equals("BART") ? false : true;
    }
    
	public String getCode() {
		return _agencyCode;
	}
    
	public String getName() {
		return _agencyName;
	}
    
    public Boolean gethasDir() {
        return _hasDir;
    }

	public int getImageId() {
		return _imageId;
	}
	
	public int getNumberOfRoutes() {
		return _routes.size();
	}
	
	public void addRoute(String routeCode, Route route) {
        _routes.put(routeCode, route);
    }
	
	public void addRoute(int posNo, Route route) {
		_routes.put(posNo, route);
	}
	
    public Route getRoute(String routeCode) {
        return _routes.get(routeCode);
    }
    
    public Route getRoute(int posNum) {
    	return _routes.get(_routes.keyAt(posNum));
    }
    
    public SparseArray<Route> getAllRoutes() {
    	return _routes;
    }
    
    //zero is 511
    //one is nextbus
    public int getAPIstem() {
    	if (_agencyCode.equals("BART") || _agencyCode.equals("Caltrain") ||
    			_agencyCode.equals("SamTrans") || _agencyCode.equals("VTA") ||
    			_agencyCode.equals("WESTCAT")) {
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
        out.writeInt(_imageId);
        String[] tempstr = {_agencyCode, _agencyName};
        out.writeStringArray(tempstr);
    }
}
