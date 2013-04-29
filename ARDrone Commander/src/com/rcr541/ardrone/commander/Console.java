package com.rcr541.ardrone.commander;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
	public static String RPIP = "192.168.1.5";
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
	boolean isConnected = false;
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
	Marker curpos;
	Timer navtimer;

	// Button Stuff
	boolean button_down = false;

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

		setServerStatusText("Not Connected for commands");
		setNAVStatusText("Not Connected for NAV data");

		// connect to sockets
		cmdAsync ca = new cmdAsync();
		ca.execute();

		// Marker
		curpos = map.addMarker(new MarkerOptions().position(ll).title("Drone")
				.snippet("Position of the Drone").draggable(true)
				.icon(BitmapDescriptorFactory.defaultMarker()));

		// Setup Touch Handlers
		OnTouchListener otl = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction() & MotionEvent.ACTION_MASK) {
				case (MotionEvent.ACTION_DOWN):
					button_down = true;
					switch (v.getId()) {
					case (R.id.btakeoff):
						takeoff(v);
					case (R.id.bturnleft):
						sendcommand(msg_turnleft);
					case (R.id.bturnright):
						sendcommand(msg_turnright);
					case (R.id.bmoveup):
						sendcommand(msg_moveup);
					case (R.id.bmovedown):
						sendcommand(msg_movedown);
					case (R.id.bmoveleft):
						sendcommand(msg_moveleft);
					case (R.id.bmoveright):
						sendcommand(msg_moveright);
					case (R.id.bmoveforward):
						sendcommand(msg_moveforward);
					case (R.id.bmoveback):
						sendcommand(msg_moveback);
					}
					System.out.println("helllllooooooo");
					return true;
				case (MotionEvent.ACTION_UP):
					System.out.println("byeeeeeeeee");
					button_down = false;
					return true;
				}
				return false;

			}
		};

		// Attached ontouch to buttons
		// Land and takeoff do not have one
		((Button) findViewById(R.id.btakeoff)).setOnTouchListener(otl);
		((Button) findViewById(R.id.bturnleft)).setOnTouchListener(otl);
		((Button) findViewById(R.id.bturnright)).setOnTouchListener(otl);
		((Button) findViewById(R.id.bmoveup)).setOnTouchListener(otl);
		((Button) findViewById(R.id.bmovedown)).setOnTouchListener(otl);
		((Button) findViewById(R.id.bmoveleft)).setOnTouchListener(otl);
		((Button) findViewById(R.id.bmoveright)).setOnTouchListener(otl);
		((Button) findViewById(R.id.bmoveforward)).setOnTouchListener(otl);
		((Button) findViewById(R.id.bmoveback)).setOnTouchListener(otl);

		// Setup navtimer
		gpsAsync ga = new gpsAsync();
		ga.execute();

		navtimer = new Timer();
		navTask nt = new navTask();
		navtimer.schedule(nt, 1000);
	}

	private class cmdAsync extends AsyncTask<Void, Void, Void> {

		String msg;

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				// connect to the socket for cmd
				piAddr = InetAddress.getByName(RPIP);
				ss = new Socket(piAddr, RPPORT);

				// get writer to socket
				out = new BufferedWriter(new OutputStreamWriter(
						ss.getOutputStream()));

				// get reader from socket
				// in = new BufferedReader(new InputStreamReader(
				// snav.getInputStream()));
				String temp = "manual";
				ss.getOutputStream().write(temp.getBytes());

				// update views
				msg = "Connected for sending commands";
				isConnected = true;
			} catch (InterruptedIOException e) {
				isConnected = false;
				msg = "Timeout Need to Reconnect";
				e.printStackTrace();
			} catch (UnknownHostException e) {
				isConnected = false;
				msg = "Unknown Host Exception Need to Reconnect";
				e.printStackTrace();
			} catch (IOException e) {
				isConnected = false;
				msg = "IO Exception Need to Reconnect";
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

	private class gpsAsync extends AsyncTask<Void, Void, Void> {

		String msg;

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				// connect to the socket for cmd
				piAddr = InetAddress.getByName(RPIP);
				ss = new Socket(piAddr, RPPORTnav);

				// get writer to socket
				in = new BufferedReader(new InputStreamReader(
						ss.getInputStream()));

				// update views
				msg = "Recieving NAV data";
				isConnected = true;
			} catch (InterruptedIOException e) {
				isConnected = false;
				msg = "Timeout Need to Reconnect";
				e.printStackTrace();
			} catch (UnknownHostException e) {
				isConnected = false;
				msg = "Unknown Host Exception Need to Reconnect";
				e.printStackTrace();
			} catch (IOException e) {
				isConnected = false;
				msg = "IO Exception Need to Reconnect";
				e.printStackTrace();
			}

			super.onPostExecute(null);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			setNAVStatusText(msg);
		}

	}

	class navTask extends TimerTask {
		@Override
		public void run() {
			try {
				String temp = in.readLine();
				ByteBuffer bf = ByteBuffer.allocate(8192);
				Double lat = Double.parseDouble(temp);
				temp = in.readLine();
				Double lon = Double.parseDouble(temp);
				System.out.println(lat + " " + lon);
				LatLng ll = new LatLng(lat, lon);
				map.animateCamera(CameraUpdateFactory
						.newCameraPosition(CameraPosition.fromLatLngZoom(ll,
								(float) 19.5)));
				curpos.setPosition(ll);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

		}
	}

	private void setServerStatusText(String s) {
		((TextView) findViewById(R.id.server_status)).setText(s);
	}

	private void setNAVStatusText(String s) {
		((TextView) findViewById(R.id.nav_status)).setText(s);
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
			sendcommand("land");
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
		sendcommand("land");
		finish();
	}

	public void map(View v) {
		Intent intent = new Intent(this.getApplicationContext(), Map.class);
		startActivity(intent);
	}

	// send command to takeoff
	public void takeoff(View v) {

		if (((Button) findViewById(R.id.btakeoff)).getText().equals("Takeoff")) {
			sendcommand(msg_takeoff);
			((Button) findViewById(R.id.btakeoff)).setText("Land");

		} else {
			sendcommand(msg_land);
			((Button) findViewById(R.id.btakeoff)).setText("Takeoff");
		}
	}

	public void sendcommand(String s) {
		if (!isConnected) {
			return;
		}
		sendAsync sa = new sendAsync();
		sa.execute(s);

	}

	private class sendAsync extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... arg0) {

			String temp = arg0[0];

			// turns into cmd <cmd>
			temp = "cmd " + temp;
			while (button_down) {
				System.out.println(temp);
				// write command to socket
				try {
					ss.getOutputStream().write(temp.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				// sleep to prevent massive packet transfer
				try {
					Thread.sleep(200);
				} catch (InterruptedException ie) {
					// Handle exception
				}
			}
			super.onPostExecute(null);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
		}
	}

	public void onLocationChanged(Location arg0) {
		if (map != null) {
			LatLng ll = new LatLng(getLocation().getLatitude(), getLocation()
					.getLongitude());
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(CameraPosition.fromLatLngZoom(ll,
							(float) 19.5)));

			curpos.setPosition(ll);
		}
	}

	public void estop(View v) {
		button_down = false;
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
