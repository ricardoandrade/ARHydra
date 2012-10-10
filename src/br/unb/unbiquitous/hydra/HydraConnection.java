package br.unb.unbiquitous.hydra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.util.Log;
import br.unb.unbiquitous.activity.MainUOSActivity;
import br.unb.unbiquitous.application.UOSDroidApp;
import br.unb.unbiquitous.json.JSONArray;
import br.unb.unbiquitous.json.JSONObject;
import br.unb.unbiquitous.ubiquitos.uos.adaptabitilyEngine.Gateway;
import br.unb.unbiquitous.ubiquitos.uos.adaptabitilyEngine.SmartSpaceGateway;
import br.unb.unbiquitous.ubiquitos.uos.driverManager.DriverData;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.dataType.UpDevice;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.dataType.json.JSONDevice;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.dataType.json.JSONDriver;
import br.unb.unbiquitous.ubiquitos.uos.messageEngine.messages.ServiceResponse;
import br.unb.unbiquitous.util.DriverType;

/**
 * Class responsible by connect the Android to the Hydra 
 * application and improve the redirection of the resources.
 * 
 * @author Ricardo Andrade
 */
public class HydraConnection {

	/************************************************************
	 * CONSTANTS
	 ************************************************************/
	
	private static final String TAG = "HydraConnection";
	private static final String DRIVER_INSTANCE_ID_PARAMETER = "driverInstanceID";
	private static final String HYDRA_REDIRECT_SERVICE = "redirectResource";
	private static final String HYDRA_RELEASE_SERVICE = "releaseResource";
	private static final String HYDRA_DRIVER_IN_USE_SERVICE = "isDriverInUse";
	private static final String HYDRA_REGISTER_LIST_DRIVERS = "listDrivers";
	private static final String HYDRA_DEVICE_NAME = "HydraApp";
	

	/************************************************************
	 * VARIABLES
	 ************************************************************/

	private UOSDroidApp app;
	private Gateway gateway;
	private MainUOSActivity activity;
	private Timer scheduler = new Timer();
	
	private Set<DriverData> driversList;
	
	/************************************************************
	 * CONSTRUCTOR
	 ************************************************************/
	
	public HydraConnection() {
		gateway = app.getApplicationContext().getGateway();
		this.driversList = new HashSet<DriverData>();
	}

	public HydraConnection(Gateway gateway) {
		this.gateway = gateway;
		this.driversList = new HashSet<DriverData>();
	}
	
	/************************************************************
	 * PUBLIC METHODS
	 ************************************************************/
	
	/**
	 * Método responsável por obter a lista de drivers que foram
	 * reconhecidos na Hydra.
	 */
	public void getListDriversInHydra(){
		
			try{
				Map<String, String> parameterMap = new HashMap<String, String>();
				
				// Retira a busca de drivers da Hydra.
				parameterMap.put("device", (new JSONDevice(getHydraDevice())).toString());
				
				ServiceResponse response = gateway.callService(this.getHydraDevice(), 
						HYDRA_REGISTER_LIST_DRIVERS,
						DriverType.REGISTER.getPath(),
						null, //instanceID
						null, //security
						parameterMap);

				JSONArray jsonList = new JSONArray(response.getResponseData().get("driverList"));
				
				driversList = new HashSet<DriverData>();
				
				for(int i=0 ; i < jsonList.length(); i++){
					
					JSONObject object = jsonList.getJSONObject(i);
					
					JSONDriver jsonDriver = new JSONDriver(object.getString("driver"));
					JSONDevice jsonDevice = new JSONDevice(object.getString("device"));
					String instanceID = object.getString("instanceID");
					
					driversList.add(new DriverData(jsonDriver.getAsObject(), jsonDevice.getAsObject(), instanceID));
					
				}
				
				Log.i(TAG, "Drivers recebidos.");
				
			}catch (Exception e) {
				Log.i(TAG, "Erro ao atualizar a lista de drivers: " + e.getMessage());
			}
	}
	
	/**
	 * Método responsável por enviar a requisição para redirecionar o recurso informado.
	 * 
	 * @param driverData
	 * @throws Exception
	 */
	public void redirectResource(final DriverData driverData) throws Exception{
		
		if(driverData == null) throw new RuntimeException("Driver data nulo no método redirectResource.");
		
		RedirectResourceTask redirectResourceTask = new RedirectResourceTask(driverData, getHydraDevice());
		redirectResourceTask.execute();
		redirectResourceTask.get();
			
	}
	
	/**
	 * Método responsável por enviar a requisição para liberação do recurso informado.
	 * @param driverData
	 * @throws Exception
	 */
	public void releaseResource(final DriverData driverData) throws Exception{
		if(driverData == null) throw new RuntimeException("Driver data nulo no método releaseResource.");
		
		ReleaseResourceTask releaseResourceTask = new ReleaseResourceTask(driverData, getHydraDevice());
		releaseResourceTask.execute();
		releaseResourceTask.get();
	}
	/**
	 * Método responsável por enviar a requisição para saber se o recurso informado
	 * está sendo utilizado no momento.
	 * 
	 * @param driverData
	 * @return
	 * @throws Exception
	 */
	public boolean isDriverInUse(DriverData driverData) throws Exception{
		
		if(driverData == null) throw new RuntimeException("Driver data nulo no método isDriverInUse.");

		DriverInUseTask driverInUseTask = new DriverInUseTask(driverData, getHydraDevice());
		driverInUseTask.execute();
		return driverInUseTask.get();
	}
	
	/**
	 * Método responsável por retornar o dispositivo que esteja executando a Hydra.
	 * @return
	 */
	public UpDevice getHydraDevice(){
		return ((SmartSpaceGateway) gateway).getDeviceManager().retrieveDevice(HYDRA_DEVICE_NAME);
	}
	
	
	/**
	 * Returns a list with devices implementing any drivers.
	 * 
	 * @return A list with all the devices and instances implementing any drivers.
	 * @since 2011.0930
	 */
	public List<DriverData> getDriversList() {
		List<DriverData> lista = new ArrayList<DriverData>();
		lista.addAll(driversList);
		return lista;
	}

	/**
	 * Returns the driver data by the instanceID.
	 * @param instanceID
	 * @return
	 */
	public DriverData getDriverData(String instanceID){
		
		for (DriverData driverData : this.driversList) {
			
			if(driverData.getInstanceID().equals(instanceID) || driverData.getInstanceID().endsWith(instanceID)){
				return driverData;
			}
		}
		return null;
	}
	
	/**
	 * Returns a list with the drivers of the device. This list will contain only 
	 * mouse, keyboard, screen and camera drivers.
	 * 
	 * @param deviceName
	 * @return
	 */
	public List<DriverData> getDriversByDevice(String deviceName){
		

		List<DriverData> drivers = new ArrayList<DriverData>();
		
		for (DriverData driverData : this.getDriversList()) {
			if(	 driverData.getDevice().getName().equalsIgnoreCase(deviceName) &&	
				( driverData.getDriver().getName().equals(DriverType.CAMERA.getPath()) ||
				  driverData.getDriver().getName().equals(DriverType.KEYBOARD.getPath()) ||
				  driverData.getDriver().getName().equals(DriverType.MOUSE.getPath()) ||
				  driverData.getDriver().getName().equals(DriverType.SCREEN.getPath())
			    )){

				drivers.add(driverData);
			}
		}
		
		return drivers;
	}
	
	/**
	 * Método responsável por verificar se a aplicação que ativa no momento.
	 * @param deviceName
	 * @return
	 */
	public boolean isDeviceValid(String deviceName){
		for (DriverData driverData : this.getDriversList()) {
			if(driverData.getDevice().getName().equals(deviceName)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Método responsável por obter a lista de dispositivos que estão
	 * inseridos dentro do ambiente inteligente a cada minuto.
	 */
	public void agendarBuscaDriverHydra(){
		
		scheduler.schedule(new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG, "Atualizando a lista de drivers com a hydra");
				getListDriversInHydra();
			}

		}, 2000, 60000); // 1 minuto
		
	}
	
	/************************************************************
	 * INNER CLASS
	 ************************************************************/
	
	
	class DriverInUseTask extends AsyncTask<Void, Void, Boolean> {
		
		private DriverData driverData;
		private UpDevice hydraDevice;
		
		public DriverInUseTask(DriverData driverData, UpDevice hydraDevice) {
			this.driverData = driverData;
			this.hydraDevice = hydraDevice;
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			try{
				
				Map<String, String> parameterMap = new HashMap<String, String>();

				parameterMap.put(DRIVER_INSTANCE_ID_PARAMETER, driverData.getInstanceID());
				
				ServiceResponse response = gateway.callService(hydraDevice, 
						HYDRA_DRIVER_IN_USE_SERVICE,
						DriverType.HYDRA.getPath(),
						null,
						null, //security
						parameterMap);
			
				return Boolean.parseBoolean(response.getResponseData().get("valor"));
			}catch (Exception e) {
				Log.i(TAG, "Erro no na requisição de uso do recurso." + e.getMessage());
			}
			return false;
		}
	}
	
	class ReleaseResourceTask extends AsyncTask<Void, Void, Void>{
		
		private DriverData driverData;
		private UpDevice hydraDevice;
		
		public ReleaseResourceTask(DriverData driverData, UpDevice hydraDevice) {
			this.driverData = driverData;
			this.hydraDevice = hydraDevice;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				Map<String, String> parameterMap = new HashMap<String, String>();

				parameterMap.put(DRIVER_INSTANCE_ID_PARAMETER, driverData.getInstanceID());
				
				gateway.callService(hydraDevice, 
						HYDRA_RELEASE_SERVICE,
						DriverType.HYDRA.getPath(),
						null,
						null, //security
						parameterMap);
				
				Log.i(TAG, "Recurso " + driverData.getInstanceID() + " liberado.");

			}catch (Exception e) {
				Log.i(TAG, "Erro no na requisição de liberação do recurso." + e.getMessage());
			}
			
			return null;
		}
	}
	
	private class RedirectResourceTask extends AsyncTask<Void, Void, Void>{

		private DriverData driverData;
		private UpDevice hydraDevice;
		
		public RedirectResourceTask(DriverData driverData, UpDevice hydraDevice) {
			this.driverData = driverData;
			this.hydraDevice = hydraDevice;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			try{
				
				Map<String, String> parameterMap = new HashMap<String, String>();

				parameterMap.put(DRIVER_INSTANCE_ID_PARAMETER, driverData.getInstanceID());
				
				gateway.callService(hydraDevice, 
						HYDRA_REDIRECT_SERVICE,
						DriverType.HYDRA.getPath(),
						null,
						null, //security
						parameterMap);
				
				Log.i(TAG, "Recurso" + driverData.getInstanceID() + " redirecionado.");
				
			}catch (Exception e) {
				Log.i(TAG, "Erro no na requisição de redirecionamento do recurso." + e.getMessage());
			}
			return null;
		}
		
	}
	

	/************************************************************
	 * GETTERS AND SETTERS
	 ************************************************************/
	
	public MainUOSActivity getActivity() {
		return activity;
	}
	
	public void setActivity(MainUOSActivity activity) {
		this.activity = activity;
	}

	public Timer getScheduler() {
		return scheduler;
	}

	public void setScheduler(Timer scheduler) {
		this.scheduler = scheduler;
	}
	
	
	
}
