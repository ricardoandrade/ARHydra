package br.unb.unbiquitous.util;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * Classe responsável por fazer a medições dos tempos de reconhecimento.
 * 
 * @author ricardoandrade
 *
 */
public class CalculoMedicao {
	
	/*********************************************
	 * CONSTANTS
	 *********************************************/
	
	private static final String TAG = CalculoMedicao.class.getSimpleName(); 
	
	/*********************************************
	 * VARIABLES
	 *********************************************/
	private static CalculoMedicao calculoMedicao;
	
	private List<Medicao> medicoes = new ArrayList<Medicao>();
	
	private Medicao ultimaMedicao;

	private Float taxaErro;
	private Float taxaNaoDecodificacao;
	private Float tempoMedioPrimeiraAparicao;
	private Float tempoMedioRecorrencia;
	private Float tempoMedioReconhecimentoAoPerderMarcador;
	private Integer totalRecorrencia;
	private Integer totalPrimeiraAparicao;
	private Integer totalReconhecimentoAoPerderMarcador;
	

	/*********************************************
	 * CONSTRUCTOR
	 *********************************************/
	
	private CalculoMedicao(){}
	
	/*********************************************
	 * PUBLIC METHODS
	 *********************************************/
	
	/**
	 * Pega a instância da classe.
	 */
	public static CalculoMedicao getInstance(){
		
		if(calculoMedicao == null){
			calculoMedicao = new CalculoMedicao();
		}
		
		return calculoMedicao;
	}
	
	/**
	 * Registro do tempo medido.
	 * 
	 * @param tipoMedicao
	 * @param tempo
	 */
	public void registrar(TipoMedicao tipoMedicao, Float tempo) {

		if (tempo != null && tempo > 10) return;
		
		Medicao medicao = new Medicao();
		medicao.setTipoMedicao(tipoMedicao);
		medicao.setTempo(tempo);

		Log.e("Medicao", "Tipo: " + tipoMedicao + "\t tempo: "+tempo);
		
		medicoes.add(medicao);
		ultimaMedicao = medicao;
	}

	/**
	 * Cálculo dos tempos médios de reconhecimento, taxa de erro e 
	 * taxa de não decodificação.
	 */
	public void calcular() {

		tempoMedioPrimeiraAparicao = calcularTempoMedio(TipoMedicao.PRIMEIRA_APARICAO);
		tempoMedioRecorrencia = calcularTempoMedio(TipoMedicao.RECORRENCIA);
		tempoMedioReconhecimentoAoPerderMarcador = calcularTempoMedio(TipoMedicao.RECONHECIMENTO_AO_PERDER_MARCADOR);
		
		taxaErro = calcularTaxa(TipoMedicao.PERDEU_MARCADOR) * 100;
		taxaNaoDecodificacao = calcularTaxa(TipoMedicao.NAO_CONSEGUIU_DECODIFICAR) * 100;
		
		totalRecorrencia = getTotal(TipoMedicao.RECORRENCIA);
		totalPrimeiraAparicao = getTotal(TipoMedicao.PRIMEIRA_APARICAO);
		totalReconhecimentoAoPerderMarcador = getTotal(TipoMedicao.RECONHECIMENTO_AO_PERDER_MARCADOR);
		
		Log.e(TAG,"++++++++ Relatório +++++++");
		Log.e(TAG,"Tempo médio da primeira aparição = " + tempoMedioPrimeiraAparicao + "s");
		Log.e(TAG,"Tempo médio de recorrência = " + tempoMedioRecorrencia + "s");
		Log.e(TAG,"Taxa de erro = " + taxaErro + "%");
		Log.e(TAG,"Taxa que não conseguiu decodificar = " + taxaNaoDecodificacao + "%");
		
	}
	
	/**
	 * Zera todas as medições.
	 */
	public void resetarMedicoes(){
		this.medicoes = new ArrayList<Medicao>();
	}

	/*********************************************
	 * PRIVATE METHODS
	 *********************************************/
	
	/**
	 * Método responsável por calcular o tempo médio a partir do tipo de medição
	 * feita.
	 */
	private Float calcularTempoMedio(TipoMedicao tipoMedicao) {

		float total = 0;
		float tempoTotal = 0;

		if(medicoes.isEmpty()) return 0f;
		
		for (Medicao medicao : medicoes) {
			if (medicao.getTipoMedicao().equals(tipoMedicao)) {
				total++;
				tempoTotal += medicao.getTempo();
			}
		}

		return total == 0 ? 0 : Float.valueOf(tempoTotal/total);
	}

	/**
	 * Método responsável por calcular as taxas de acordo com o tipo
	 * de taxa informada por parâmetro.
	 * 
	 * @param tipoMedicao
	 * @return
	 */
	private Float calcularTaxa(TipoMedicao tipoMedicao){
		
		float count = 0;
		
		if(medicoes.isEmpty()) return 0f;
		
		for (Medicao medicao : medicoes) {
			if(medicao.getTipoMedicao().equals(tipoMedicao)){
				count++;
			}
		}
		return Float.valueOf(count / ((float) medicoes.size()));
	}

	/**
	 * Método responsável por retornar a quantidade de medições
	 * registradas para o tipo de medição informada.
	 * 
	 * @param tipoMedicao
	 * @return
	 */
	private Integer getTotal(TipoMedicao tipoMedicao){
		int count = 0;
		
		for (Medicao medicao : medicoes) {
			if(medicao.getTipoMedicao().equals(tipoMedicao)){
				count++;
			}
		}
		
		return count;
	}
	
	/*********************************************
	 * GETTERS AND SETTERS
	 *********************************************/
	
	public List<Medicao> getMedicoes() {
		return medicoes;
	}

	public void setMedicoes(List<Medicao> medicoes) {
		this.medicoes = medicoes;
	}

	public Float getTaxaErro() {
		return taxaErro;
	}

	public void setTaxaErro(Float taxaErro) {
		this.taxaErro = taxaErro;
	}

	public Float getTaxaNaoDecodificacao() {
		return taxaNaoDecodificacao;
	}

	public void setTaxaNaoDecodificacao(Float taxaNaoDecodificacao) {
		this.taxaNaoDecodificacao = taxaNaoDecodificacao;
	}

	public Float getTempoMedioPrimeiraAparicao() {
		return tempoMedioPrimeiraAparicao;
	}

	public void setTempoMedioPrimeiraAparicao(Float tempoMedioPrimeiraAparicao) {
		this.tempoMedioPrimeiraAparicao = tempoMedioPrimeiraAparicao;
	}

	public Float getTempoMedioRecorrencia() {
		return tempoMedioRecorrencia;
	}

	public void setTempoMedioRecorrencia(Float tempoMedioRecorrencia) {
		this.tempoMedioRecorrencia = tempoMedioRecorrencia;
	}

	public Integer getTotalRecorrencia() {
		return totalRecorrencia;
	}

	public void setTotalRecorrencia(Integer totalRecorrencia) {
		this.totalRecorrencia = totalRecorrencia;
	}

	public Integer getTotalPrimeiraAparicao() {
		return totalPrimeiraAparicao;
	}

	public void setTotalPrimeiraAparicao(Integer totalPrimeiraAparicao) {
		this.totalPrimeiraAparicao = totalPrimeiraAparicao;
	}

	public Float getTempoMedioReconhecimentoAoPerderMarcador() {
		return tempoMedioReconhecimentoAoPerderMarcador;
	}

	public void setTempoMedioReconhecimentoAoPerderMarcador(
			Float tempoMedioReconhecimentoAoPerderMarcador) {
		this.tempoMedioReconhecimentoAoPerderMarcador = tempoMedioReconhecimentoAoPerderMarcador;
	}

	public Integer getTotalReconhecimentoAoPerderMarcador() {
		return totalReconhecimentoAoPerderMarcador;
	}

	public void setTotalReconhecimentoAoPerderMarcador(
			Integer totalReconhecimentoAoPerderMarcador) {
		this.totalReconhecimentoAoPerderMarcador = totalReconhecimentoAoPerderMarcador;
	}

	public Medicao getUltimaMedicao() {
		return ultimaMedicao;
	}

	public void setUltimaMedicao(Medicao ultimaMedicao) {
		this.ultimaMedicao = ultimaMedicao;
	}
	
	
	
	
}
