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
	public void gangnamstyle(){
		latitudeE6=3752766;
		longitudeE6=12703064;
	}
	public int coordtoE6(int d, int m, int s, char z){
		int x=d;
		x+=((m*60+s)/3600);
		
		if (z=='N' || z=='E'){
			return x;
		}
		else{
			return (-1*x);
		}
		
	}
	
}
