package com.rcr541.ardrone.commander;

import android.os.Bundle;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Console extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void exit(View v){
    	finish();
    }
    public void map(View v){
    	//Intent intent = new Intent(this.getApplicationContext(), Maps.class);
        //startActivity(intent);
    }
}
