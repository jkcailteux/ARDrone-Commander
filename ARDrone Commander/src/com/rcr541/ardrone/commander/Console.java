package com.rcr541.ardrone.commander;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
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
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Console extends FragmentActivity implements LocationListener {

	// Commands to send
	public static final String msg_takeoff = "takeoff";
	public static final String msg_land = "land";
	public static final String msg_turnleft = "turnleft";
	public static final String msg_turnright = "turnright";
	public static final String msg_moveup = "moveup";
	public static final String msg_movedown = "movedown";
	public static final String msg_moveforward = "moveforward";
	public static final String msg_moveback = "moveback";
	public static final String msg_moveleft = "moveleft";
	public static final String msg_moveright = "moveright";

	public boolean connected = false;
	public static String RPIP = "192.168.1.2";
	public static final int RPPORT = 5558;
	public static final int RPPORTnav = 5559;

	// Setting up Socket to send cmd
	BufferedWriter out = null;
	Socket ss = null;
	InetAddress piAddr = null;

	// Setting up Socket to get nav
	BufferedReader in = null;
	ServerSocket sserver = null;
	Socket snav = null;
	Handler handler;

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

		setServerStatusText("Not Connected");

		// connect to sockets
		cmdAsync ca = new cmdAsync();
		ca.execute();
	}

	private class cmdAsync extends AsyncTask<Void, Void, Void> {

		String msg;

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				// connect to the socket for cmd
				piAddr = InetAddress.getByName(RPIP);
				ss = new Socket(piAddr, RPPORT);

				// connect to the socket for navdata
				sserver = new ServerSocket(RPPORTnav, 100, piAddr);
				snav = sserver.accept();

				// get writer to socket
				out = new BufferedWriter(new OutputStreamWriter(
						ss.getOutputStream()));

				// get reader from socket
				in = new BufferedReader(new InputStreamReader(
						snav.getInputStream()));

				// update views
				msg = "Connected";
				
			} catch (InterruptedIOException e) {
				msg ="Timeout";
				e.printStackTrace();
			} catch (UnknownHostException e) {
				msg = "Unknown Host Exception";
				e.printStackTrace();
			} catch (IOException e) {
				msg = "IO Exception";
				e.printStackTrace();
			}

			super.onPostExecute(null);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			setServerStatusText(msg);
		}

	}

	private void setServerStatusText(String s) {
		((TextView) findViewById(R.id.server_status)).setText(s);
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
	protected void onPause() {
		try {
			ss.close();
			out.close();
			snav.close();
			sserver.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		super.onPause();
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

		if (((Button) findViewById(R.id.takeoffbutton)).getText().equals(
				"Takeoff")) {
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

	public void sendcommand(String s) {

		if (!((TextView) findViewById(R.id.server_status)).getText().equals(
				"Connected")) {
			return;
		}
		// turns into cmd <cmd>
		String temp = "cmd " + s;

		System.out.println(temp);

		// write command to socket
		try {
			out.write(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void onLocationChanged(Location arg0) {
		if (map != null) {
			LatLng ll = new LatLng(getLocation().getLatitude(), getLocation()
					.getLongitude());
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(CameraPosition.fromLatLngZoom(ll,
							(float) 19.5)));
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
