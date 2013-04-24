package com.rcr541.ardrone.commander;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class RunOrCreate extends Activity {

	boolean sent_success=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.runorcreate);
	}

	public void exit(View v) {
		finish();
	}

	public void send(View v) {
		Toast toast = Toast.makeText(getApplicationContext(),
				"Trying to send data", Toast.LENGTH_SHORT);
		toast.show();

		System.out.println(build_list_string());

		sendAsync sa = new sendAsync();
		sa.execute();
		if(sent_success){
			// Show confirmation of sent message list
			Toast toast1 = Toast.makeText(getApplicationContext(),
					"List successfully sent", Toast.LENGTH_SHORT);
			toast1.show();
			sent_success=false;
		}
	}

	public void create(View v) {
		Intent intent = new Intent(this.getApplicationContext(),
				Waypointslist.class);
		startActivity(intent);
	}

	// turns into list <size> <lat1> <lon1> <lat2> <lon2> ...
	public String build_list_string() {
		String s = "";
		SharedPreferences prefs = getSharedPreferences(
				"com.rcr541.ardrone.commander", Context.MODE_PRIVATE);

		s += "list ";
		int size = prefs.getInt("size", 0);
		s += size + " ";

		for (int x = 1; x <= size; x++) {
			s += (float) (prefs.getInt((x + "lat"), 0) / 1000000.0) + " ";
			s += (float) (prefs.getInt((x + "lon"), 0) / 1000000.0) + " ";
		}
		return s;
	}

	private class sendAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			int SERVER_PORT = 5558;
			String ip_dest = "192.168.1.5";// should be address of raspberry pi
			Socket s = null;
			InetAddress piAddr = null;
			try {

				piAddr = InetAddress.getByName(ip_dest);
				s = new Socket(piAddr, SERVER_PORT);
				// ss.setSoTimeout(10000);

				// send a message
				String outgoingMsg = build_list_string();
				s.getOutputStream().write(outgoingMsg.getBytes());
				
				sent_success=true;
				s.close();
			} catch (InterruptedIOException e) {
				// if timeout occurs
				// Show confirmation of sent message list
				Toast toast = Toast.makeText(getApplicationContext(),
						"Timeout", Toast.LENGTH_SHORT);
				toast.show();
				e.printStackTrace();
			} catch (UnknownHostException e) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Unknown Host", Toast.LENGTH_SHORT);
				toast.show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"IO Exception", Toast.LENGTH_SHORT);
				toast.show();
				e.printStackTrace();
			} finally {
				if (s != null) {
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			((TextView) findViewById(R.id.textStatus)).setText("");
		}

	}
}
