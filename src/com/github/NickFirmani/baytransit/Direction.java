package com.github.NickFirmani.baytransit;

import android.os.Parcel;
import android.os.Parcelable;

public class Direction implements Parcelable {
	private String _dirCode;
	private String _dirName;
	public Direction(String dirCode, String dirName) {
		_dirCode = dirCode;
		_dirName = dirName;
	}
	public Direction(Parcel parcelIn) {
		String[] tempArr = parcelIn.createStringArray();
		_dirCode = tempArr[0];
		_dirName = tempArr[1];
	}
	public String getName() {
		return _dirName;
	}
	public String getCode() {
		return _dirCode;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		String[] tempArr = {_dirCode, _dirName};
		dest.writeStringArray(tempArr);
	}
	public static final Parcelable.Creator<Direction> CREATOR
		= new Parcelable.Creator<Direction>() {
		public Direction createFromParcel(Parcel in) {
			return new Direction(in);
		}
		public Direction[] newArray(int size) {
			return new Direction[size];
		}
	};
}