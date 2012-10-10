package br.unb.unbiquitous.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import br.unb.unbiquitous.hydra.HydraConnection;
import br.unb.unbiquitous.listView.Item;
import br.unb.unbiquitous.listView.ItemListAdapter;

/**
 * 
 * @author ricardoandrade
 *
 */
public class ListViewActivity extends ListActivity implements OnItemClickListener {


	/************************************************************
	 * CONSTANTS
	 ************************************************************/
	private static final String TAG = ListActivity.class.getSimpleName();
	
	/************************************************************
	 * VARIABLES
	 ************************************************************/
	private List<Item> data;
	private ListView listView;
	private ItemListAdapter adapter;

	public static HydraConnection hydraConnection;
	


	/************************************************************
	 * PUBLIC METHODS
	 ************************************************************/
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_check);
		
		data = new ArrayList<Item>();
		
		Intent intent = getIntent();
		
		ArrayList<String> drivers = intent.getStringArrayListExtra("driversName");
		
		for ( int i =0; i< drivers.size(); i++){
			
			
			
			data.add(	new Item(	Integer.valueOf(i).longValue(), 
									drivers.get(i), 
									hydraConnection.getDriverData(drivers.get(i))
								)
					);
		}
		
		Collections.sort(data, new Comparator<Item>() {
			public int compare(Item i1, Item i2) {
				return i1.getCaption().compareTo(i2.getCaption());
			}
		});

		adapter = new ItemListAdapter(this, data);
		setListAdapter(adapter);

		listView = getListView();
		listView.setItemsCanFocus(false);

		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(this);

	}

	/**
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		boolean isItemMarcado = false;
		
		// Pega o item que foi selecionado.
		Item item = adapter.getItem(arg2);
		
		for(int i= 0; i < listView.getCheckItemIds().length; i++){
			if(item.getId() == listView.getCheckedItemIds().clone()[i]){
				isItemMarcado = true;
			}
		}
		
		Log.i(TAG, "Recurso selecionado = " + item.getCaption());
	
		try{
		
			// Redirecionar ou liberar recurso
			if(isItemMarcado){
				
				Log.i(TAG, "Pedido para redirecionamento do recurso = " + item.getCaption());
				
				hydraConnection.redirectResource(item.getDriverData());
				item.setDriverInUse(true);
				
				Toast.makeText(this, "Recurso " + item.getCaption() + " redirecionado com sucesso.",Toast.LENGTH_SHORT).show();
				Log.i(TAG, "Recurso " + item.getCaption() + " redirecionado com sucesso.");
				
			}else{
				
				Log.i(TAG, "Pedido para liberação do recurso = " + item.getCaption());
				
				hydraConnection.releaseResource(item.getDriverData());
				item.setDriverInUse(false);
				
				Toast.makeText(this, "Recurso " + item.getCaption() + " liberado com sucesso.",Toast.LENGTH_SHORT).show();
				Log.i(TAG, "Recurso " + item.getCaption() + " liberado com sucesso.");
			}
		
		}catch (Exception e) {
			Toast.makeText(this, "Erro de requisição com a Hydra",Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Erro de requisição com a Hydra no onItemClick: " + e.getMessage());
		}
	}

	/************************************************************
	 * PROTECTED METHODS
	 ************************************************************/
	
	/**
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.clearSelection();
		
		try{
			for (int i = 0; i < listView.getCount(); ++i) {
				boolean isDriverInUse = hydraConnection.isDriverInUse(data.get(i).getDriverData());
				listView.setItemChecked(i, isDriverInUse);
			}
		}catch (Exception e) {
			Toast.makeText(this, "Erro de requisição com a Hydra",Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Erro de requisição com a Hydra no onResume: " + e.getMessage());
		}
	}

	/************************************************************
	 * PRIVATE METHODS
	 ************************************************************/
	
	/**
	 * Desmarca todas as checkbox's.
	 */
	private void clearSelection() {
		final int itemCount = listView.getCount();
		for (int i = 0; i < itemCount; ++i) {
			listView.setItemChecked(i, false);
		}
	}


}