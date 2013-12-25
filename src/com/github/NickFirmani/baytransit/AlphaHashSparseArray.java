package com.github.NickFirmani.baytransit;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.SparseArray;


/* This data structure takes a string as the argument and 
 * orders it according to the first 10 letters of it.
 * Works for any string.
 */

public class AlphaHashSparseArray<E> extends SparseArray<E> {
	@SuppressLint("DefaultLocale")
	public static int hashAlpha(String stringIn) {
		int returnInt = 0;
		stringIn = stringIn.toUpperCase();
		stringIn = stringIn.replaceAll("\\W" , "");
		try {
			return Integer.parseInt(stringIn);
		} catch (Exception e) {}
		if (stringIn.length() > 10) {
			stringIn = stringIn.substring(0, 10);
		} else {
            while (stringIn.length() <= 10) {
                stringIn = stringIn + "@";
            }
		}
		Log.d("AHSA", "about to hash: " + stringIn);
        for (int k = 0; k < stringIn.length(); k += 1) {
			int charAt = (int) stringIn.charAt(k);
            returnInt += Math.pow(2, 10 - k) * charAt;
		}
        return returnInt;
	}
	
	public void put(String key, E value) {
		if (value instanceof Route) {
			Route tr = (Route) value;
			Log.d("Putting key", Integer.toString(hashAlpha(key)) + " value: " + tr.getRouteName());
		}
		super.put(hashAlpha(key), value);
	}
	
	public E get(String key) {
		Log.d("Getting key", Integer.toString(hashAlpha(key)));
		return super.get(hashAlpha(key));
	}
	
	@Override
	public void put(int key, E value) {
		Log.w("AlphaHashSparseArray", "This is dangerous, can break the alphabetical order");
		super.put(key, value);
	}
}
