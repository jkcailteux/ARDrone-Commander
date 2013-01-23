package com.rcr541.ardrone.commander;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class Console extends Activity {

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

	String uri_a = "rtsp://v5.cache1.c.youtube.com/CjYLENy73wIaLQnhycnrJQ8qmRMYESARFEIJbXYtZ29vZ2xlSARSBXdhdGNoYPj_hYjnq6uUTQw=/0/0/0/video.3gp";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);

		VideoView mVideoView = (VideoView) findViewById(R.id.videoView1);
		// mVideoView.setVideoURI(Uri.parse("android.resource://" +
		// getPackageName() +"/"+R.raw.gangnam));
		// mVideoView.setVideoURI(Uri.parse(uri_a));
		mVideoView.setMediaController(new MediaController(this));
		mVideoView.requestFocus();
		// mVideoView.start();
	
		serverStatus = (TextView) findViewById(R.id.server_status);


		// CONNECTING TO DRONE
		try {
			socket = new Socket(DRONEIP, DRONEPORT);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			serverStatus.setText("Connected");
		} catch (UnknownHostException e) {
			serverStatus.setText("Unknown Host Exception");
			e.printStackTrace();
		} catch (IOException e) {
			serverStatus.setText("IO Exception");
			e.printStackTrace();			
		}

	}

	/*
	 * public class ServerThread implements Runnable { public void run() { try {
	 * if (DRONEIP != null) { handler.post(new Runnable() { public void run() {
	 * serverStatus.setText("Listening on IP: " + DRONEIP); } }); serverSocket =
	 * new ServerSocket(DRONEPORT); while (true) { // LISTEN FOR INCOMING
	 * CLIENTS Socket client = serverSocket.accept(); handler.post(new
	 * Runnable() { public void run() { serverStatus.setText("Connected.");
	 * connected = true; } });
	 * 
	 * try { BufferedReader in = new BufferedReader( new InputStreamReader(
	 * client.getInputStream())); String line = null; while ((line =
	 * in.readLine()) != null) { Log.d("ServerActivity", line); handler.post(new
	 * Runnable() { public void run() { // DO WHATEVER YOU WANT TO THE FRONT END
	 * // THIS IS WHERE YOU CAN BE CREATIVE } }); } break; } catch (Exception e)
	 * { handler.post(new Runnable() { public void run() { serverStatus
	 * .setText("Connection interrupted. Please reconnect."); } });
	 * e.printStackTrace(); } } } else { handler.post(new Runnable() { public
	 * void run() { serverStatus
	 * .setText("Couldn't detect internet connection."); } }); } } catch
	 * (Exception e) { handler.post(new Runnable() { public void run() {
	 * serverStatus.setText("Error"); } }); e.printStackTrace(); } } }
	 */
	@Override
	protected void onStop() {
		super.onStop();
		try {
			socket.close();
			dataOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public void sendcommand(String command){
		
		try {
			dataOutputStream.writeUTF(command);
			serverStatus.setText(command + " sent");
		} catch (UnknownHostException e) {
			serverStatus.setText("Unknown Host Exception");
			e.printStackTrace();
		} catch (IOException e) {
			serverStatus.setText("IO Exception");
			e.printStackTrace();
		}
		
	}
}
