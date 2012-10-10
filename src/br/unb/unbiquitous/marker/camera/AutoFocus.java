package br.unb.unbiquitous.marker.camera;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.util.Log;

public class AutoFocus implements AutoFocusCallback{

	@Override
	public void onAutoFocus(boolean success, Camera camera) {

		Log.i("AutoFocus", "onAutoFocus()");
		
	}

}
