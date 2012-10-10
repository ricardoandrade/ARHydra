package com.google.droidar.system;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import br.unb.unbiquitous.marker.decoder.DecoderObject;
import br.unb.unbiquitous.marker.setup.MarkerDetectionSetup;

import com.google.droidar.util.Log;

public class ArActivity extends Activity implements SensorEventListener{

	private static final String LOG_TAG = "ArActivity";

	private static Setup staticSetupHolder;

	private Setup mySetupToUse;
	private SensorManager sensorManager;

	private Sensor orientationSensor;
	private DecoderObject decoderObject;

	/**
	 * Called when the activity is first created.
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "main onCreate");
		if (staticSetupHolder != null) {
			mySetupToUse = staticSetupHolder;
			staticSetupHolder = null;
			runSetup();
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			decoderObject = ((MarkerDetectionSetup) mySetupToUse).getDecoderObject();
		} else {
			Log.e(LOG_TAG, "There was no Setup specified to use. "
					+ "Please use ArActivity.show(..) when you "
					+ "want to use this way of starting the AR-view!");
			this.finish();
		}
	}

	public static void startWithSetup(Activity currentActivity, Setup setupToUse) {
		ArActivity.staticSetupHolder = setupToUse;
		currentActivity.startActivity(new Intent(currentActivity,ArActivity.class));
	}

	private void runSetup() {
		mySetupToUse.run(this);
	}

	@Override
	protected void onRestart() {
		if (mySetupToUse != null)
			mySetupToUse.onRestart(this);
		super.onRestart();
	}

	@Override
	protected void onResume() {
		if (mySetupToUse != null)
			mySetupToUse.onResume(this);
		
		sensorManager.registerListener(this, orientationSensor,	SensorManager.SENSOR_DELAY_NORMAL);
		
		super.onResume();
	}

	@Override
	protected void onStart() {
		if (mySetupToUse != null)
			mySetupToUse.onStart(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (mySetupToUse != null)
			mySetupToUse.onStop(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (mySetupToUse != null){
			mySetupToUse.onDestroy(this);
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (mySetupToUse != null)
			mySetupToUse.onPause(this);
		
		sensorManager.unregisterListener(this);
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((mySetupToUse != null)
				&& (mySetupToUse.onKeyDown(this, keyCode, event)))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (((mySetupToUse != null) && mySetupToUse.onCreateOptionsMenu(menu)))
			return true;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (mySetupToUse != null)
			return mySetupToUse.onMenuItemSelected(featureId, item);
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(LOG_TAG, "main onConfigChanged");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
			Log.d(LOG_TAG, "orientation changed to landscape");
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
			Log.d(LOG_TAG, "orientation changed to portrait");
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values.clone();
         float z = (float) Math.toDegrees(values[0]);
         
         int orientation = 99;
         
         if ( (z > 16000 && z < 21000) || (z > 0 && z< 1000)){
        	 orientation = 1;
         }else if (z >= 1000 && z < 7000){
        	 orientation = 0;
         }else if(z >= 7000 && z < 11000){
        	 orientation = 3;
         }else{
        	 orientation = 2;
         }
         
//         Log.e("direction", "orientation = "+orientation);
         decoderObject.setOrientation(Integer.valueOf(orientation));
//         Log.e("orientation", " orientation z = " + z);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}