package br.unb.unbiquitous.marker.setup;


import java.util.HashMap;

import com.google.droidar.gl.MarkerObject;



public class MarkerObjectMap extends HashMap<String, MarkerObject> {

	public void put(MarkerObject markerObject) {
		put(markerObject.getMyId(), markerObject);
	}

}
