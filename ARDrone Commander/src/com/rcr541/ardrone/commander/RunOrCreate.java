package com.rcr541.ardrone.commander;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.runorcreate);
	}

	public void exit(View v) {
		finish();
	}

	public void send(View v) {
		// create connected socket
		((TextView) findViewById(R.id.textStatus)).setText("Trying to send data");
		sendAsync sa = new  sendAsync();
		sa.execute();
		// send build_list_string() through socket
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
		s += " " + size + " ";

		for (int x = 1; x <= size; x++) {
			s += x + " ";
			s += (prefs.getInt((x + "lat"), 0)*1000000) + " ";
			s += x + " ";
			s += (prefs.getInt((x + "lon"), 0)*1000000) + " ";
		}
		return s;
	}

	private class sendAsync extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			int SERVER_PORT = 5558;
			String ip_dest = "192.168.1.3";// should be address of raspberry pi
			ServerSocket ss = null;
			InetAddress piAddr = null;
			try {

				piAddr = InetAddress.getByName(ip_dest);
				ss = new ServerSocket(SERVER_PORT, 100, piAddr);
				// ss.setSoTimeout(10000);

				// accept connections
				Socket s = ss.accept();// blocks here
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
						s.getOutputStream()));

				// send a message
				String outgoingMsg = build_list_string();
				out.write(outgoingMsg);
				out.flush();

				// Show confirmation of sent message list
				Toast toast = Toast.makeText(getApplicationContext(),
						"List successfully sent", Toast.LENGTH_SHORT);
				toast.show();

				// SystemClock.sleep(5000);
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
				if (ss != null) {
					try {
						ss.close();
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
