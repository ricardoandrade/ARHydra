package br.unb.unbiquitous.jni;

/**
 * 
 * @author ricardoandrade
 *
 */
public class ZbarJni {
	static {
		System.loadLibrary("zbar");
	}
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param data
	 * @return
	 */
	public native String decode(int width, int height, byte[]data);
	
}
