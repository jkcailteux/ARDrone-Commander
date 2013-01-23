package com.rcr541.ardrone.commander;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Coordlist extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordlist);
        
        //test data
        int test_lat=3752766,
		test_long=12703064;
    
        //test button of coord 
        Button b1 = new Button(this);
        b1.setText(coordstring(1,test_lat, test_long));
        //b1.setOnClickListener(ocl);
        ((LinearLayout) findViewById(R.id.linearlayout1)).addView(b1);
        //end test
        Coordinate x=new Coordinate(test_lat,test_long);
        x.E6tolat(x.latE6);
        System.out.println(x.lat[0]+ " d "+ x.lat[1]+" m "+ x.lat[2]+" s ");
        
        
        
    }  
    public void exit(View v){
    	finish();
    }
    String coordstring(int order, int latitude, int longitude){
    	String temp="";
    	temp= "Coordinate #" + order+" \n";
    	temp+="Latitude: " + latitude +",  Longitude:" + longitude;
    	return temp;
    	
    }
}
