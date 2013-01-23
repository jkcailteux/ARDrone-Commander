package com.rcr541.ardrone.commander;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class Waypointslist extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waypointslist);
    
        //create list of buttons for each waypoint list
        //buttons goto listview of coordinate pairs where you can edit them
        //goes to activity Coordlist
        OnClickListener ocl = new OnClickListener() {
            public void onClick(View v) {
              Intent myIntent = new Intent(v.getContext(), Coordlist.class);
              startActivity(myIntent);
              finish();
            }
          };
          
          //need to pull list of buttons here
          //test button
          Button b1 = new Button(this);
          b1.setText("Test Coordinates List");
          b1.setOnClickListener(ocl);
          ((LinearLayout) findViewById(R.id.linearlayout1)).addView(b1);
          
                
        
    }  
    public void exit(View v){
    	finish();
    }
}
