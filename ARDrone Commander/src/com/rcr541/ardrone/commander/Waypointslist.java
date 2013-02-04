package com.rcr541.ardrone.commander;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class Waypointslist extends Activity {

	View view_to_delete = null;
	// Dialog box builder
	AlertDialog.Builder builder =null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waypointslist);

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
		builder.setMessage("Are you sure you want to delete the list of waypoints?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener);

		// Setup listeners
		OnClickListener ocl = new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Coordlist.class);
				myIntent.putExtra("id", 0); // 0 means create
				startActivity(myIntent);
				finish();
			}
		};
		OnLongClickListener ocl_delete = new OnLongClickListener() {
			public boolean onLongClick(View v) {
				view_to_delete=v;
				builder.show();
				return false;
			}
		};
		OnClickListener ocl_create = new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Coordlist.class);
				myIntent.putExtra("id", v.getId()); // send ids
				startActivity(myIntent);
				finish();
			}
		};

		// setup create button
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 20);

		Button bcreate = new Button(this);
		bcreate.setText("Create a new list");
		bcreate.setOnClickListener(ocl_create);
		bcreate.setLayoutParams(params);
		((LinearLayout) findViewById(R.id.linearlayout1)).addView(bcreate);

		// Populate with Waypointlist buttons here
		Button b1 = new Button(this);
		b1.setId(1);// assign ids starting with 1
		b1.setText("Test Coordinates List");
		b1.setOnClickListener(ocl);
		b1.setOnLongClickListener(ocl_delete);
		b1.setLayoutParams(params);
		((LinearLayout) findViewById(R.id.linearlayout1)).addView(b1);

	}

	public void exit(View v) {
		finish();
	}
}
