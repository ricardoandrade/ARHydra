package br.unb.unbiquitous.thread;

import org.apache.http.util.ByteArrayBuffer;

import android.os.SystemClock;
import android.util.Log;
import br.unb.unbiquitous.configuration.ConfigApp;
import br.unb.unbiquitous.manager.ARManager;
import br.unb.unbiquitous.manager.DecodeManager;
import br.unb.unbiquitous.marker.decoder.DecodeDTO;
import br.unb.unbiquitous.util.CalculoMedicao;
import br.unb.unbiquitous.util.Medicao;
import br.unb.unbiquitous.util.TipoMedicao;

/**
 * Thread responsável pela decodificação do QRCode.
 * 
 * @author ricardoandrade
 *
 */
final class DecodeQRCodeThread extends Thread {
	
	/************************************************
	 * CONSTANTS
	 ************************************************/	
	
	private static final String TAG = DecodeQRCodeThread.class.getSimpleName();

	private int frameWidth;
	private int frameHeight;

	/************************************************
	 * VARIABLES
	 ************************************************/
	
	private DecodeManager decodeManager ;
	
	private ByteArrayBuffer byteArrayBuffer;
	private float[] rotacao; 
	private DecodeDTO decodeDTO;
	
	private boolean busy;
	private boolean stopRequest;
	              
	private RepositionMarkerThread repositionMarkerThread;
	private ARManager arManager;
	
	private Long tempoInicio;
	private Long tempoFim;
//	private static final String TAG_MEDICAO = "TESTES";
	private boolean primeiraAparicao = true;


	/************************************************
	 * CONSTRUCTOR
	 ************************************************/

	public DecodeQRCodeThread(DecodeManager decodeManager, RepositionMarkerThread repositionMarkerThread, ARManager arManager){
		this.decodeManager = decodeManager;
		this.repositionMarkerThread = repositionMarkerThread;
		this.arManager = arManager;
	}
	

	/************************************************
	 * PUBLIC METHODS
	 ************************************************/

	/**
	 * Método invocado quando a thread é inicializada.
	 */
	@Override
	public synchronized void run() {

		// aguardando a inicializacao do decodeDTO pela RepositionThread
		while (decodeDTO == null){
			decodeDTO = repositionMarkerThread.getDecodeDTO();
		}
		
		while(!stopRequest){
			
			while(!busy){
				
				try {
					wait(); // esperando pelo novo frame
				} catch (InterruptedException e) {}
				
				// problema de setar o stopRequest para true mas a thread ainda estar em wait.
				if(stopRequest){
					Log.e(TAG, "Finalizando a " + TAG);
					return;
				}
				Log.i(TAG, "Frame recebido: Decodificando QRCode.");
				
				// Tentando decodificar o QRCode.
				if(decodeManager.isQRCodeFound(byteArrayBuffer.toByteArray(), frameWidth, frameHeight, ConfigApp.getInstance().getDecodeProgram())){
					
					Log.i(TAG, "Frame recebido: QRCode decodificado com sucesso.");
					
					synchronized (decodeDTO) {
						
						decodeDTO.setAppName(decodeManager.getLastMarkerName());
						decodeDTO.setRotacao(rotacao);
						
						if(arManager.inserirObjetoVirtual(decodeManager.getLastMarkerName(), rotacao)){
							realizarMedicao();
						}
					}

				}else{
					
					CalculoMedicao.getInstance().registrar(TipoMedicao.NAO_CONSEGUIU_DECODIFICAR, null);
//					Log.e(TAG_MEDICAO, "+++++++++ [TESTE] NAO CONSEGUIU DECODIFICAR +++++++++");
//					primeiraAparicao = true;
					
					arManager.retirarObjetosVirtuais();
				}

				synchronized (this) {
					busy = false;
				}

			}

		}
		
		
	}
	
	/**
	 * Método responsável por receber o frame contendo o marcador detectado 
	 * pela DetectionThread. Seta os dados necessários para a decodificação 
	 * e aciona esta thread para fazer uma nova detecção.
	 * 
	 * 
	 * @param byteArrayBuffer
	 * @param rotacao
	 */
	public void registerFrame(ByteArrayBuffer byteArrayBuffer, float[] rotacao, Long tempoInicio){
	
		if(stopRequest || decodeDTO == null) return;
		
		Log.i(TAG, "Setando rotação.");
		synchronized (decodeDTO) {
			decodeDTO.setRotacao(rotacao);
		}

		Log.i(TAG, "Frame recebido.");
		if(!busy){
			Log.e(TAG, "Busy = " + busy);
			this.byteArrayBuffer = byteArrayBuffer;
			this.rotacao = rotacao;
			
			this.tempoInicio = tempoInicio;
			
			busy = true;
			synchronized (this) {
				this.notify();
			}
		}
		
	}

	private void realizarMedicao(){
		
		if(stopRequest) return;
		
		tempoFim = SystemClock.uptimeMillis();
		
		Float tempoTotal = (float) (((float)tempoFim - (float) tempoInicio) / 1000);
		
		if(primeiraAparicao){
			
			CalculoMedicao.getInstance().registrar(TipoMedicao.PRIMEIRA_APARICAO, tempoTotal);
//			Log.e(TAG_MEDICAO, "+++++++++ [TESTE] TEMPO PRIMEIRA APARICAO = " + tempoTotal + "s.");
	
			primeiraAparicao = false;
		
		}else{
			
			Medicao ultimaMedicao = CalculoMedicao.getInstance().getUltimaMedicao();
			
			if(		ultimaMedicao.getTipoMedicao().equals(TipoMedicao.PERDEU_MARCADOR) || 
					ultimaMedicao.getTipoMedicao().equals(TipoMedicao.NAO_CONSEGUIU_DECODIFICAR)){

				CalculoMedicao.getInstance().registrar(TipoMedicao.RECONHECIMENTO_AO_PERDER_MARCADOR, tempoTotal);
				
			}else{
				CalculoMedicao.getInstance().registrar(TipoMedicao.RECORRENCIA, tempoTotal);
			}
			
//			Log.e(TAG_MEDICAO, "+++++++++ [TESTE] RECORRENCIA = " + tempoTotal + "s.");
		}
		
		tempoInicio = null;
		tempoFim = null;
	}
	
	/************************************************
	 * GETTERS AND SETTERS
	 ************************************************/
	

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}


	public boolean isStopRequest() {
		return stopRequest;
	}


	public void setStopRequest(boolean stopRequest) {
		this.stopRequest = stopRequest;
		synchronized (this) {
			this.notify();
			this.interrupt();
		}
	}


	public int getFrameWidth() {
		return frameWidth;
	}


	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}


	public int getFrameHeight() {
		return frameHeight;
	}


	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
	}

	

	
}
