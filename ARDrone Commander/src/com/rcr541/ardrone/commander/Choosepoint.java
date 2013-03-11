package com.rcr541.ardrone.commander;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class Choosepoint extends FragmentActivity implements LocationListener {

	int point_number;

	// GPS and Map Stuff
	LatLng ll;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location;
	double latitude;
	double longitude;
	GoogleMap map;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	protected LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosepoint);


		((TextView) findViewById(R.id.text_pt_num)).setText("Point #"
				+ getIntent().getExtras().getInt("pt_num", 0));

		GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		ll = new LatLng(getLocation().getLatitude(), getLocation()
				.getLongitude());

		map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition
				.fromLatLngZoom(ll, (float) 19.5)));

	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) this
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						;
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	public void mark(View v) {
		LatLng ll = new LatLng(getLocation().getLatitude(), getLocation()
				.getLongitude());
		
		SharedPreferences pref= getSharedPreferences("com.rcr541.ardrone.commander",
				Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		int size=pref.getInt("size", 0);
		
		System.out.println(ll.latitude);
		System.out.println(ll.longitude);
		
		//if new point
		if(point_number == 0){
			edit.putInt("size", size+1);
			edit.putInt(((size+1) + "lat"), (int) (ll.latitude*(1000000)));
			edit.putInt(((size+1) + "lon"), (int) (ll.longitude*(1000000)));
		} else {
			//if changing old point
			edit.putInt((point_number + "lat"), (int) (ll.latitude*(1000000)));
			edit.putInt((point_number + "lon"), (int) (ll.longitude*(1000000)));
		}
		
		edit.commit();
		
		finish();
	}

	public void exit(View v) {
		finish();
	}

	public void onLocationChanged(Location location) {
		if (map != null) {
			LatLng ll = new LatLng(getLocation().getLatitude(), getLocation()
					.getLongitude());
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(CameraPosition.fromLatLngZoom(ll,
							(float) 19.5)));
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
