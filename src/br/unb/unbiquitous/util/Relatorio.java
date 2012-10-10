package br.unb.unbiquitous.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import br.unb.unbiquitous.activity.R;

public class Relatorio {

	protected static final String EditText = null;
	/*********************************************
	 * VARIABLES
	 *********************************************/
	
	private static Relatorio relatorio;

	/*********************************************
	 * CONSTRUCTOR
	 *********************************************/
	
	private Relatorio(){}
	
	/*********************************************
	 * PUBLIC METHODS
	 *********************************************/
	
	/**
	 * Método responsável por manter somente uma instância 
	 * do objeto em memória.
	 */
	public static Relatorio getInstance(){
		if (relatorio == null){
			relatorio = new Relatorio();
		}
		return relatorio;
	}
	
	/**
	 * Método responsável por enviar o relatório para o email definido dentro
	 * do método.
	 * 
	 * @param activity
	 */
	public void enviarParaEmail(final Activity  activity){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		
		final EditText userInput = new EditText(activity);
		userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setMessage("Distância dos testes")
			   .setView(userInput)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		        	   CalculoMedicao.getInstance().calcular();
		        	   
		        	    String email = "ricardoflandrade@gmail.com";
		        	   
						String[] recipients = new String[]{email, "",};  
						StringBuilder body = new StringBuilder();  
						body.append("<br> Medições para a distância " + userInput.getText().toString());
						body.append("<br> ------------------------------------------- <br>");
						body.append(getMedicoesFormatadas("<br>"));
						body.append("<br> ------------------------------------------- <br>");
						body.append(getRelatorioFormatado("<br>"));  
						           
						final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  
						emailIntent.setType("text/html");                
						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Testes ARHydra");  
						emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);  
						emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body.toString()));  
						
						activity.startActivity(Intent.createChooser(emailIntent, "Enviar email....")); 
		           }
		       })
		       .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Método responsável por exibir um alerta para o usuário, solicitando
	 * ao mesmo, a confirmação da exclusão dos dados obtidos até o momento.
	 * Em caso afirmativo, os dados serão excluídos. Caso contrário o alerta
	 * é fechado. 
	 * 
	 * @param context
	 */
	public void resetar(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Confirma exclusão do relatório?")
		       .setCancelable(false)
		       .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                CalculoMedicao.getInstance().resetarMedicoes();
		           }
		       })
		       .setNegativeButton("Não", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Método responsável por exbir para o usuário uma apresentação em 
	 * formato de diálogo contendo as informações referentes as medições 
	 * efetuadas, tais como: tempos médios e percentuais de erros.
	 *  
	 * @param context
	 */
	public void exibir(Context context){
		
		//Calculando os valores das medicoes realizadas
		CalculoMedicao.getInstance().calcular();
		
		final Dialog dialog = new Dialog(context);

		dialog.setContentView(R.layout.relatorio);
		dialog.setTitle("Relatório dos testes");

		
		TextView primeiro = (TextView) dialog.findViewById(R.id.relatorio_primeira);
		primeiro.setText(this.getRelatorioFormatado("\n"));
		
		Button button = (Button) dialog.findViewById(R.id.relatorio_botao);
		 
		button.setOnClickListener(new OnClickListener() {
        @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
		dialog.show();
	}
	
	/*********************************************
	 * PRIVATE METHODS
	 *********************************************/
	
	private String getRelatorioFormatado(String delimitador){
		
		NumberFormat format = new DecimalFormat("#.00");
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("Total de primeira aparições= " + CalculoMedicao.getInstance().getTotalPrimeiraAparicao());
		stringBuilder.append(delimitador + "Tempo médio da primeira aparição = "+ format.format(CalculoMedicao.getInstance().getTempoMedioPrimeiraAparicao()) + "s");
		stringBuilder.append(delimitador + "Total de recorrências = " + CalculoMedicao.getInstance().getTotalRecorrencia());
		stringBuilder.append(delimitador + "Tempo médio de recorrência = " + format.format(CalculoMedicao.getInstance().getTempoMedioRecorrencia()) + "s");
		stringBuilder.append(delimitador + "Total de reconhecimento ao perder marcador = " + CalculoMedicao.getInstance().getTotalReconhecimentoAoPerderMarcador());
		stringBuilder.append(delimitador + "Tempo médio de reconhecimento ao perder marcador = " + format.format(CalculoMedicao.getInstance().getTempoMedioReconhecimentoAoPerderMarcador()) + "s");
		stringBuilder.append(delimitador + "Taxa de erro = " + format.format(CalculoMedicao.getInstance().getTaxaErro()) + "%");
		stringBuilder.append(delimitador + "Taxa que não conseguiu decodificar = " + format.format(CalculoMedicao.getInstance().getTaxaNaoDecodificacao()) + "%");
		
		return stringBuilder.toString();
	}

	
	private String getMedicoesFormatadas(String delimitador){
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(Medicao medicao : CalculoMedicao.getInstance().getMedicoes()){
			stringBuilder.append(delimitador + "Tipo: " + medicao.getTipoMedicao() + ", Tempo:" + medicao.getTempo());
		}
		
		return stringBuilder.toString();
	}
	
	
	/*********************************************
	 * GETTERS AND SETTERS
	 *********************************************/
	
}
