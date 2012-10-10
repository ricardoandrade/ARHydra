package br.unb.unbiquitous.marker.command;

import java.util.ArrayList;

import android.content.Intent;
import br.unb.unbiquitous.activity.ListViewActivity;
import br.unb.unbiquitous.marker.virtual.object.MeuObjetoVirtual;

import com.google.droidar.commands.Command;
import com.google.droidar.util.Log;

/**
 * Classe responsável por implemetar um commando,
 * sendo este acionado toda vez que o objeto virtual
 * seja clicado.
 *  
 * @author ricardoandrade
 *
 */
public class VirtualObjectCommand extends Command {
	
	/************************************************
	 * VARIABLES
	 ************************************************/
	
	private MeuObjetoVirtual objetoVirtual;
	
	/************************************************
	 * PUBLIC METHODS
	 ************************************************/

	/**
	 * Método chamado toda vez que o objeto virtual for clicado.
	 * O nome dos drivers implementados para a aplicação selecionada 
	 * é repassado para a activity que listará todos os drivers, podendo
	 * ser feito o redirecionamento ou liberação dos recursos. 
	 * 
	 * 
	 */
	@Override
	public boolean execute() {
		Log.i("VirtualObjectCommand", "objeto clicado.");
		
		Intent intent = new Intent(objetoVirtual.getActivity(), ListViewActivity.class);
		
		ListViewActivity.hydraConnection = objetoVirtual.getHydraConnection();
		
		ArrayList<String> driversName = new ArrayList<String>();
		
		for (String driver : objetoVirtual.getNomeDrivers()) {
			driversName.add(driver);
		}
		
		intent.putStringArrayListExtra("driversName", driversName);
		objetoVirtual.getActivity().startActivity(intent);
		
		return true;
	}

	/************************************************
	 * GETTERS AND SETTERS
	 ************************************************/
	
	public MeuObjetoVirtual getObjetoVirtual() {
		return objetoVirtual;
	}

	public void setObjetoVirtual(MeuObjetoVirtual objetoVirtual) {
		this.objetoVirtual = objetoVirtual;
	}
	
}
