package br.unb.unbiquitous.thread;

import android.util.Log;
import br.unb.unbiquitous.manager.ARManager;
import br.unb.unbiquitous.marker.decoder.DecodeDTO;

/**
 * Thread responsável por reposicionar o objeto virtual
 * na tela.
 * 
 * @author ricardoandrade
 * 
 */
public class RepositionMarkerThread extends Thread {

	/************************************************
	 * VARIABLES
	 ************************************************/
	private final static String  TAG = RepositionMarkerThread.class.getSimpleName();
	private ARManager arManager;
	private DecodeDTO decodeDTO;
	
	private boolean stopRequest;
	
	/************************************************
	 * CONSTRUCTOR
	 ************************************************/

	public RepositionMarkerThread(ARManager arManager){
		this.arManager = arManager;
		decodeDTO = new DecodeDTO();
	}

	/************************************************
	 * PUBLIC METHODS
	 ************************************************/

	/**
	 * Método invocado quando a thread é inicializada.
	 */
	@Override
	public void run() {
		while(!stopRequest){
			arManager.reposicionarObjetoVirtual(decodeDTO.getAppName(), decodeDTO.getRotacao());
		}
		
		Log.e(TAG, "Finalizando a " + TAG);
		
		
	}

	/************************************************
	 * GETTERS AND SETTERS
	 ************************************************/
	
	public void reposicionar(DecodeDTO decodeDTO){
		this.decodeDTO = decodeDTO;
	}

	public synchronized DecodeDTO getDecodeDTO() {
		return decodeDTO;
	}

	public void setDecodeDTO(DecodeDTO decodeDTO) {
		this.decodeDTO = decodeDTO;
	}
	
	public void setRotacao(float[] rotacao){
		synchronized (decodeDTO) {
			decodeDTO.setRotacao(rotacao);
		}
	}

	public boolean isStopRequest() {
		return stopRequest;
	}

	public void setStopRequest(boolean stopRequest) {
		this.stopRequest = stopRequest;
	}
	
	


}
