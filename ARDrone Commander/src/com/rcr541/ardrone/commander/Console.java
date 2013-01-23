package com.rcr541.ardrone.commander;


import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class Console extends Activity {

	String uri_a="rtsp://v5.cache1.c.youtube.com/CjYLENy73wIaLQnhycnrJQ8qmRMYESARFEIJbXYtZ29vZ2xlSARSBXdhdGNoYPj_hYjnq6uUTQw=/0/0/0/video.3gp";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console);
        
        VideoView mVideoView = (VideoView)findViewById(R.id.videoView1);
       // mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() +"/"+R.raw.gangnam));
       // mVideoView.setVideoURI(Uri.parse(uri_a));
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
      //  mVideoView.start();
    
    } 
    public void exit(View v){
    	finish();
    }
    public void map(View v){
    	Intent intent = new Intent(this.getApplicationContext(), Map.class);
        startActivity(intent);
    }
    //send command to takeoff
    public void takeoff(View v){
    	if(((Button) findViewById(R.id.takeoffbutton)).getText()=="Takeoff"){
    		
    		((Button) findViewById(R.id.takeoffbutton)).setText("Land");
    		
    	} else{
    		
    		((Button) findViewById(R.id.takeoffbutton)).setText("Takeoff");
    	}
    	
    	
    }

}
