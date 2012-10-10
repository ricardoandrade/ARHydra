package br.unb.unbiquitous.util;

/**
 * Classe responsável por representar uma medição
 * para os testes de reconhecimento dos marcadores. 
 * 
 * @author ricardoandrade
 *
 */
public class Medicao {

	/*********************************************
	 * VARIABLES
	 *********************************************/
	
	private TipoMedicao tipoMedicao;
	private Float tempo;
	
	/*********************************************
	 * GETTERS AND SETTERS
	 *********************************************/
	
	public TipoMedicao getTipoMedicao() {
		return tipoMedicao;
	}
	public void setTipoMedicao(TipoMedicao tipoMedicao) {
		this.tipoMedicao = tipoMedicao;
	}
	public Float getTempo() {
		return tempo;
	}
	public void setTempo(Float tempo) {
		this.tempo = tempo;
	}
	
	

}
