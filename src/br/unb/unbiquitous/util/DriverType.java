package br.unb.unbiquitous.util;

public enum DriverType {
	
	CAMERA("br.unb.unbiquitous.ubiquitos.driver.cameraDriver"),
	HYDRA("br.unb.unbiquitous.ubiquitos.driver.HydraDriver"),
	KEYBOARD("br.unb.unbiquitous.ubiquitos.driver.keyboardDriver"),
//	MOUSE("br.unb.unbiquitous.ubiquitos.driver.mouseDriver"),
	MOUSE("mouse"),
	SCREEN("br.unb.unbiquitous.ubiquitos.driver.screenDriver"),
	REGISTER("br.unb.unbiquitous.ubiquitos.uos.driver.RegisterDriver"),
	ALL("");
	
	private String path;
	
	
	private DriverType (String path){
		this.path = path;
	}
	
	public String getPath(){
		return this.path;
	}

}
