package br.unb.unbiquitous.activity;

import java.util.PropertyResourceBundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import br.unb.unbiquitous.application.UOSDroidApp;
import br.unb.unbiquitous.configuration.ConfigApp;
import br.unb.unbiquitous.configuration.ConfigLog4j;
import br.unb.unbiquitous.hydra.HydraConnection;
import br.unb.unbiquitous.marker.decoder.DecoderObject;
import br.unb.unbiquitous.marker.setup.SingleMarkerSetup;
import br.unb.unbiquitous.util.DecodeProgram;
import br.unb.unbiquitous.util.Relatorio;

import com.google.droidar.system.ArActivity;

/**
 * 
 * @author Ricardo Andrade
 * 
 */
public class MainUOSActivity extends Activity {

	/************************************************************
	 * CONSTANTS
	 ************************************************************/
	private static final String TAG = MainUOSActivity.class.getSimpleName();
	private static final boolean DEBUG = true;

	/************************************************************
	 * VARIABLES
	 ************************************************************/

	private UOSDroidApp droidobiquitousApp;
	private HydraConnection hydraConnection;

	private DecoderObject decoderObject;
	private SingleMarkerSetup markerSetup;

	private Button button;

	/************************************************************
	 * PUBLIC METHODS
	 ************************************************************/

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (DEBUG)
			Log.e(TAG, "++ ON CREATE ++");

		super.onCreate(savedInstanceState);

		// Configuring the Log4J
		ConfigLog4j.configure();
		
		verificarWifi();
			
		new StartMiddlewareTask().execute();
		
		// Start the augmented reality
		startAR();
		
		setContentView(button);
			
	}

	/**
	 * Creating the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (DEBUG)
			Log.e(TAG, "++ ON CREATE OPTIONS MENU ++");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.exibir_relatorio:
			Relatorio.getInstance().exibir(this);
			break;
		case R.id.resetar_relatorio:
			Relatorio.getInstance().resetar(this);
			break;
		case R.id.enviar_relatorio:
			Relatorio.getInstance().enviarParaEmail(this);
			break;
		case R.id.configuracoes:
			exibirPainelConfiguracoes();
			break;
			
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		confirmarSaida();
	}
	
	@Override
	protected void onDestroy() {
		 super.onDestroy();
	}
	
	/**
	 * Método responsável por exibir o painel de configurações.
	 */
	private void exibirPainelConfiguracoes(){
		
		String [] items = new String[DecodeProgram.values().length];   
		int index = -1;
		
		for (int i = 0 ; i < items.length; i++){
			items[i] = DecodeProgram.values()[i].getDescicao();
			if(DecodeProgram.values()[i].equals(ConfigApp.getInstance().getDecodeProgram()) ){
				index = i;
			}
			
		}
		
		final CharSequence[] itemsPrograma = items;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Decodificação do QRCode");
		builder.setSingleChoiceItems(items, index, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        Toast.makeText(getApplicationContext(),itemsPrograma[item] + " selecionado.", Toast.LENGTH_SHORT).show();
		        ConfigApp.getInstance().setDecodeProgram(DecodeProgram.getDecodeProgram(itemsPrograma[item].toString()));
		        dialog.dismiss();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
		
	}
	
	/**
	 * Método responsável por apresentar uma caixa de diálogo com o usuário
	 * para que ele confirme ou não a saída da aplicação.
	 */
	private void confirmarSaida(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage("Confirma saída?")
		       .setCancelable(false)
		       .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   stopMiddleware();
		       		   hydraConnection.getScheduler().cancel();
		       		   finish();
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

	/************************************************************
	 * PRIVATE METHODS
	 ************************************************************/

	/**
	 * 
	 */
	private void startAR() {
		decoderObject = new DecoderObject(this);

		markerSetup = new SingleMarkerSetup();
		markerSetup.setActivity(this);
		markerSetup.setDecoderObject(decoderObject);

		button = new Button(this);
		button.setText("Iniciar");
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ArActivity.startWithSetup(MainUOSActivity.this, markerSetup);
			}
		});

	}

	/**
	 * Start the middleware with the configs of the bundle and start the hydra
	 * connection.
	 */
	private void startMiddleware() {
		try {

			PropertyResourceBundle bundle;

			droidobiquitousApp = new UOSDroidApp();

			bundle = new PropertyResourceBundle(getResources().openRawResource(R.raw.arhydra));

			droidobiquitousApp.start(bundle);

			hydraConnection = new HydraConnection(droidobiquitousApp.getApplicationContext().getGateway());
			hydraConnection.setActivity(this);

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}	
	
	/**
	 * Método responsável por finalizar a execução do middlewware.
	 */
	private void stopMiddleware(){
		try {
			droidobiquitousApp.tearDown(null);
		} catch (Exception e) {
			Log.e(TAG,"Erro ao parar o middleware:" +  e.getMessage());
		}
	}
	
	/**
	 * Método responsável por aguardar o handshake com a Hydra.
	 */
	private void waitHydraHandshake(){
		while(hydraConnection.getHydraDevice() == null){}
	}
	
	/**
	 * Método responsável por verificar se foi feito o estabelecimento
	 * da conexão Wifi para que a ARHydra possa buscar as informações
	 * necessárias na Hydra dentro da mesma rede. 
	 */
	private void verificarWifi(){
		boolean firstTime = true;
		
		ConnectivityManager manager = (ConnectivityManager)getSystemService(MainUOSActivity.CONNECTIVITY_SERVICE);
       	Boolean isWifiEnable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

       	while(!isWifiEnable){
       		if(firstTime){
       			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
       			firstTime = false;
       		}
       		isWifiEnable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        }
	}

	/************************************************************
	 * INNER CLASSES
	 ************************************************************/
	
	/**
	 * Tarefa assíncrona que inicia o middleware uOS e fica esperando
	 * que o handshake com a Hydra seja efetuada para que o usuário 
	 * possa inicializar a aplicação ARHydra.
	 *  
	 */
	private class StartMiddlewareTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progressDialog;

		protected void onPreExecute() {
			progressDialog = new ProgressDialog(MainUOSActivity.this);
			progressDialog.setMessage("Aguardando o handshake com a Hydra...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.i(TAG, "++ Inicializando o middleware... ++");
			startMiddleware();
			Log.i(TAG, "++ Middleware inicializado. ++");
			
			Log.i(TAG, "++ Esperando pelo handshake com a Hydra... ++");
			waitHydraHandshake();
			Log.i(TAG, "++ Handshake efetuado com sucesso ... ++");
			
			Log.i(TAG, "++ Agendando busca de drivers na Hydra ... ++");
			hydraConnection.agendarBuscaDriverHydra();
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
		}
	}
	
	
	/************************************************************
	 * GETTERS AND SETTERS
	 ************************************************************/

	public HydraConnection getHydraConnection() {
		return hydraConnection;
	}

	public void setHydraConnection(HydraConnection hydraConnection) {
		this.hydraConnection = hydraConnection;
	}

}