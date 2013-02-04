package com.rcr541.ardrone.commander;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Coordlist extends Activity {
	
	View view_to_delete = null;
	// Dialog box builder
	AlertDialog.Builder builder =null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordlist);
        
		// Listener for dialog box
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					view_to_delete.setVisibility(View.INVISIBLE);
					((LinearLayout)view_to_delete.getParent()).removeView(view_to_delete);
					//DELETE DATA HERE
					view_to_delete=null;
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					break;
				}
			}
		};
		builder= new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete the list of coordinates?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener);
        
        
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
