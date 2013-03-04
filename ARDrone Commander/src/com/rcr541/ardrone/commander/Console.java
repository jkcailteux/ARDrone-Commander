package com.rcr541.ardrone.commander;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Console extends FragmentActivity implements LocationListener {

	// Commands to send
	public static final String msg_takeoff = "MD|takeoff";
	public static final String msg_land = "MD|land";
	public static final String msg_turnleft = "MD|turn|left";
	public static final String msg_turnright = "MD|turn|right";
	public static final String msg_moveup = "MD|alt|up";
	public static final String msg_movedown = "MD|alt|down";
	public static final String msg_moveforward = "MD|move|forward";
	public static final String msg_moveback = "MD|move|back";
	public static final String msg_moveleft = "MD|move|left";
	public static final String msg_moveright = "MD|move|right";

	public boolean connected = false;
	public static String DRONEIP = "192.168.1.1";
	public static final int DRONEPORT = 5557;
	private Handler handler = new Handler();
	private TextView serverStatus;

	// Setting up Socket
	Socket socket = null;
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;

	// GPS and Map Stuff
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
		setContentView(R.layout.console);

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		LatLng ll = new LatLng(getLocation().getLatitude(), getLocation()
				.getLongitude());
		map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition
				.fromLatLngZoom(ll, (float) 19.5)));
		
		
		
		
		serverStatus = (TextView) findViewById(R.id.server_status);
		serverStatus.setText("Not Connected");
	}

	public class ServerThread implements Runnable {
		public void run() {
			try {
				socket = new Socket(DRONEIP, DRONEPORT);
				dataOutputStream = new DataOutputStream(
						socket.getOutputStream());
				serverStatus.setText("Connected");
			} catch (UnknownHostException e) {
				serverStatus.setText("Unknown Host Exception");
				e.printStackTrace();
			} catch (IOException e) {
				serverStatus.setText("IO Exception");
				e.printStackTrace();
			}

		}
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

	@Override
	protected void onStop() {
		/*
		 * try { socket.close(); dataOutputStream.close(); } catch (IOException
		 * e) { e.printStackTrace(); }
		 */
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*
		 * super.onStop(); try { socket.close(); dataOutputStream.close(); }
		 * catch (IOException e) { e.printStackTrace(); }
		 */
	}

	public void exit(View v) {
		finish();
	}

	public void map(View v) {
		Intent intent = new Intent(this.getApplicationContext(), Map.class);
		startActivity(intent);
	}

	// send command to takeoff
	public void takeoff(View v) {
		if (((Button) findViewById(R.id.takeoffbutton)).getText() == "Takeoff") {
			sendcommand(msg_takeoff);
			((Button) findViewById(R.id.takeoffbutton)).setText("Land");

		} else {
			sendcommand(msg_land);
			((Button) findViewById(R.id.takeoffbutton)).setText("Takeoff");
		}
	}

	public void turnleft(View v) {
		sendcommand(msg_turnleft);
	}

	public void turnright(View v) {
		sendcommand(msg_turnright);
	}

	public void moveup(View v) {
		sendcommand(msg_moveup);
	}

	public void movedown(View v) {
		sendcommand(msg_movedown);
	}

	public void moveforward(View v) {
		sendcommand(msg_moveforward);
	}

	public void moveback(View v) {
		sendcommand(msg_moveback);
	}

	public void moveleft(View v) {
		sendcommand(msg_moveleft);
	}

	public void moveright(View v) {
		sendcommand(msg_moveright);
	}

	public void sendcommand(String command) {
		/*
		 * // CONNECTING TO DRONE try { socket = new Socket(DRONEIP, DRONEPORT);
		 * dataOutputStream = new DataOutputStream(socket.getOutputStream());
		 * serverStatus.setText("Connected"); } catch (UnknownHostException e) {
		 * serverStatus.setText("Unknown Host Exception"); e.printStackTrace();
		 * } catch (IOException e) { serverStatus.setText("IO Exception");
		 * e.printStackTrace(); }
		 * 
		 * try { dataOutputStream.writeUTF(command);
		 * serverStatus.setText(command + " sent"); } catch
		 * (UnknownHostException e) {
		 * serverStatus.setText("Unknown Host Exception"); e.printStackTrace();
		 * } catch (IOException e) { serverStatus.setText("IO Exception");
		 * e.printStackTrace(); }
		 */
	}

	public void onLocationChanged(Location arg0) {
		if(map!=null){
			LatLng ll = new LatLng(getLocation().getLatitude(), getLocation()
					.getLongitude());
			map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition
					.fromLatLngZoom(ll, (float) 19.5)));
		}
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}
