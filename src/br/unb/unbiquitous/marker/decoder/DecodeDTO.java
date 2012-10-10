package br.unb.unbiquitous.marker.decoder;

import org.apache.http.util.ByteArrayBuffer;

/**
 * 
 * @author ricardoandrade
 *
 */
public class DecodeDTO {

	/************************************************
	 * VARIABLES
	 ************************************************/
	
	private float[] rotacao;
	private String appName;
	private ByteArrayBuffer byteArrayBuffer;

	/************************************************
	 * GETTERS AND SETTERS
	 ************************************************/
	
	public float[] getRotacao() {
		return rotacao;
	}

	public void setRotacao(float[] rotacao) {
		this.rotacao = rotacao;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public ByteArrayBuffer getByteArrayBuffer() {
		return byteArrayBuffer;
	}

	public void setByteArrayBuffer(ByteArrayBuffer byteArrayBuffer) {
		this.byteArrayBuffer = byteArrayBuffer;
	}

	

}
