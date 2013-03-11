package com.rcr541.ardrone.commander;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Main extends Activity {

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
    public void console(View v){
    	Intent intent = new Intent(this.getApplicationContext(), Console.class);
        startActivity(intent);
    }
    public void map(View v){
    	Intent intent = new Intent(this.getApplicationContext(), Map.class);
        startActivity(intent);
    }
    public void waypointslist(View v){
    	Intent intent = new Intent(this.getApplicationContext(), RunOrCreate.class);
        startActivity(intent);
    }
}
