package com.rcr541.ardrone.commander;

import android.app.Application;

public class Globals extends Application {

	//to be set by input data
	private int latitudeE6 = 38971667;
	private int longitudeE6 = -95235278;
	
	public void setLat(int x){
		latitudeE6=x;
	}
	public void setLong(int x){
		longitudeE6=x;
	}
	public int getLat(){
		return latitudeE6;
	}
	public int getLong(){
		return longitudeE6;
	}
	
}
