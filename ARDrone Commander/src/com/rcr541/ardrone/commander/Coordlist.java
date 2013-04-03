package com.rcr541.ardrone.commander;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class Coordlist extends Activity {

	View view_to_delete = null;
	// Dialog box builder
	AlertDialog.Builder builder = null;
	SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coordlist);
			
		populate_coords();
		
	}

	public void choosepoint(int point_number) {
		Intent intent = new Intent(this.getApplicationContext(),
				Choosepoint.class);
		intent.putExtra("pt_num", point_number);
		intent.putExtra("lat", (prefs.getInt(point_number + "lat", 0)));
		intent.putExtra("lon", (prefs.getInt(point_number + "lon", 0)));
		startActivity(intent);
	}

	public void exit(View v) {
		finish();
	}

	String coordstring(int order, int latitude, int longitude) {
		String temp = "";
		temp = "Coordinate #" + order + " \n";
		temp += "Latitude: " + latitude + ",  Longitude:" + longitude;
		return temp;

	}

	public void populate_coords() {
		//get prefs
		prefs=getSharedPreferences("com.rcr541.ardrone.commander",
				Context.MODE_PRIVATE);
		// setup handlers and listeners

		// Listener for dialog box
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					delete_point(view_to_delete.getId());
					view_to_delete.setVisibility(View.INVISIBLE);
					((LinearLayout) view_to_delete.getParent())
							.removeView(view_to_delete);
					((LinearLayout) findViewById(R.id.linearlayout1)).removeAllViewsInLayout();
				    populate_coords();
					view_to_delete = null;
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					// No button clicked
					break;
				}
			}
		};
		builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete the  coordinate?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener);
		
		// listeners
		OnLongClickListener ocl_delete = new OnLongClickListener() {
			public boolean onLongClick(View v) {
				view_to_delete = v;
				builder.show();
				return false;
			}
		};
		OnClickListener ocl = new OnClickListener() {
			public void onClick(View v) {
				choosepoint(v.getId());
			}
		};		
		
		// step 1
		// get size
		int size = prefs.getInt("size", 0);

		// step 2
		// for loop to create button with text for order=pt_num, lat=pt_num lat,
		// lon=pt_num lon
		// change button id to pt_num
		// add button to linearlayout1
		// add handlers for delete and edit
		for(int x=1; x<=size;x++){
			Button b1 = new Button(this);
			b1.setId(x);
			b1.setText(coordstring(x, prefs.getInt((x + "lat"), 0), prefs.getInt((x + "lon"), 0)));
			b1.setOnClickListener(ocl);
			b1.setOnLongClickListener(ocl_delete);
			((LinearLayout) findViewById(R.id.linearlayout1)).addView(b1);
		}

	}
	@Override
	protected void onResume() {
	    super.onResume();
	    ((LinearLayout) findViewById(R.id.linearlayout1)).removeAllViewsInLayout();
	    populate_coords();
	    
	}
	
	public void addnew(View v) {
		prefs=getSharedPreferences("com.rcr541.ardrone.commander",
				Context.MODE_PRIVATE);
		
		Intent intent = new Intent(this.getApplicationContext(),
				Choosepoint.class);
		intent.putExtra("pt_num", (prefs.getInt("size", 0) + 1));
		startActivity(intent);
	}
	public void delete_point(int pt_num) {
		prefs=getSharedPreferences("com.rcr541.ardrone.commander",
				Context.MODE_PRIVATE);
		Editor edit=prefs.edit();
		int size=prefs.getInt("size",0);

		for(int i=(pt_num+1); i<=size;i++){
			int lat=prefs.getInt(i+"lat", 0);
			int lon=prefs.getInt(i+"lon", 0);
			edit.putInt((i-1)+ "lat", lat);
			edit.putInt((i-1)+ "lon", lon);
			
			
		}
		edit.remove(size+"lat");
		edit.remove(size+"lon");
		edit.putInt("size", size-1);
		edit.commit();

	}
}
