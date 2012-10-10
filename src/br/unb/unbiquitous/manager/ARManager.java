package br.unb.unbiquitous.manager;

import java.util.HashMap;

import android.util.Log;
import br.unb.unbiquitous.activity.MainUOSActivity;
import br.unb.unbiquitous.marker.setup.SingleMarkerSetup;
import br.unb.unbiquitous.marker.virtual.object.MeuObjetoVirtual;

import com.google.droidar.gl.MarkerObject;
import com.google.droidar.system.Setup;

/**
 * Classe responsável por gerenciar a criação e o reposicionamento
 * dos objetos virtuais da realidade aumentada.
 * 
 * @author ricardoandrade
 *
 */
public class ARManager {
	
	/************************************************
	 * VARIABLES
	 ************************************************/
	
	private Setup setup;
	private HashMap<String, MarkerObject> markerObjectMap;
	
	private MeuObjetoVirtual ultimoObjetoVirtual;
	
	/************************************************
	 * CONSTRUCTOR
	 ************************************************/
	
	/**
	 * 
	 */
	public ARManager(Setup setup, HashMap<String, MarkerObject> markerObjectMap){
		this.setup = setup;
		this.markerObjectMap = markerObjectMap;
	}

	/************************************************
	 * PUBLIC METHODS
	 ************************************************/
	
	/**
	 * A DecodeQRCodeThread invoca esse método quando um QRCode é decodificado.
	 * É feita uma verificação na Hydra para saber se o QRCode decodificado é
	 * válido. Caso se a aplicação seja válida e já foi reconhecida anteriormente
	 * o objeto virtual correspondente é reposicionado. Se a for uma aplicação nova
	 * é gerado o objeto virtual correspondente.
	 * 
	 * Retorna verdadeiro caso a aplicação esteja ativa e o objeto virtual for
	 * incluído com sucesso. Caso contrário, retorna falso. 
	 * 
	 */
	public boolean inserirObjetoVirtual(String appName, float[] rotacao) {

		if (isAppNameValid(appName)) {

			Log.e("AppName", "AppName decodificado = " + appName);
			
			
			MarkerObject markerObj = markerObjectMap.get(appName);

			if (markerObj != null) {
				this.ultimoObjetoVirtual = (MeuObjetoVirtual) markerObj;
				markerObj.OnMarkerPositionRecognized(rotacao, 1, 16);
			} else {
				
				if(ultimoObjetoVirtual != null){
					retirarObjetosVirtuais();
				}
				
				this.ultimoObjetoVirtual = criarObjetoVirtual(appName);
			}
			return true;
		}else{
			retirarObjetosVirtuais();
			return false;
		}
	}

	/**
	 * Método invocado pela RepositionThread responsável pelo reposicionamento
	 * do objeto virtual na tela. O reposicionamento ocorre enquanto a decodificação
	 * do QRCode está sendo feita.
	 * 
	 * @param appName
	 * @param rotacao
	 */
	public void reposicionarObjetoVirtual(String appName, float[] rotacao){

		MarkerObject markerObj = markerObjectMap.get(appName);

		if (markerObj != null) {
			markerObj.OnMarkerPositionRecognized(rotacao, 1, 16);
		}
	}
	
	/**
	 * Método que remove o objeto virtual apresentado ao usuário.
	 */
	public void retirarObjetosVirtuais() {
		if(ultimoObjetoVirtual != null){
			((SingleMarkerSetup) setup).removeMarkerObject(ultimoObjetoVirtual);
		}
		ultimoObjetoVirtual = null;
	}
	
	/************************************************
	 * PRIVATE METHODS
	 ************************************************/
	
	/**
	 * Método responsável por verificar, na Hydra, a validade da aplicação informada, decodificada a 
	 * partir do QRCode.
	 */
	private boolean isAppNameValid(String appName){
		return ((MainUOSActivity)setup.getActivity()).getHydraConnection().isDeviceValid(appName);
	}
	
	/**
	 * Método responsável por invocar o método para criar o objeto virtual na tela.
	 */
	private MeuObjetoVirtual criarObjetoVirtual(String appName) {
		return ((SingleMarkerSetup) setup).addMarkerObject(appName);
	}

	
}
