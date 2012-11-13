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

public class Map extends MapActivity {
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.ic_launcher);
		CustomItemizedOverlay itemizedOverlay = new CustomItemizedOverlay(
				drawable, this);

		GeoPoint point = new GeoPoint(((Globals) this.getApplication()).getLat(), ((Globals) this.getApplication()).getLong());
		OverlayItem overlayitem = new OverlayItem(point, "Hello",
				"I'm at "+((Globals) this.getApplication()).getLat()+" "+ ((Globals) this.getApplication()).getLong() );

		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);

		MapController mapController = mapView.getController();
		mapController.animateTo(point);
		mapController.setZoom(18);
    
    }
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

    public void exit(View v){
    	finish();
    }
    
    
}
