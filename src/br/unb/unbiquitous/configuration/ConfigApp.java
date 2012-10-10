package br.unb.unbiquitous.configuration;

import br.unb.unbiquitous.util.DecodeProgram;

public class ConfigApp {

	/*********************************************
	 * VARIABLES
	 *********************************************/
	
	private static ConfigApp configApp;
	private DecodeProgram decodeProgram = DecodeProgram.ZBAR;
	
	/*********************************************
	 * CONSTRUCTOR
	 *********************************************/
	
	private ConfigApp(){}
	
	/*********************************************
	 * PUBLIC METHODS
	 *********************************************/
	
	/**
	 * Método responsável por retornar a instância da classe.
	 */
	public static ConfigApp getInstance(){
	
		if( configApp == null){
			configApp = new ConfigApp();
		}
		
		return configApp;
	}

	/*********************************************
	 * GETTERS AND SETTERS
	 *********************************************/

	public DecodeProgram getDecodeProgram() {
		return decodeProgram;
	}

	public void setDecodeProgram(DecodeProgram decodeProgram) {
		this.decodeProgram = decodeProgram;
	}
	
	
	
}
