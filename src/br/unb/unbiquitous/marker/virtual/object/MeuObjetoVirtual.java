package br.unb.unbiquitous.marker.virtual.object;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import br.unb.unbiquitous.activity.MainUOSActivity;
import br.unb.unbiquitous.hydra.HydraConnection;
import br.unb.unbiquitous.marker.command.VirtualObjectCommand;
import br.unb.unbiquitous.ubiquitos.uos.driverManager.DriverData;

import com.google.droidar.de.rwth.BasicMarker;
import com.google.droidar.gl.Color;
import com.google.droidar.gl.GLCamera;
import com.google.droidar.gl.GLFactory;
import com.google.droidar.gl.animations.AnimationFaceToCamera;
import com.google.droidar.gl.scenegraph.MeshComponent;
import com.google.droidar.gl.scenegraph.Shape;
import com.google.droidar.util.IO;
import com.google.droidar.util.Vec;
import com.google.droidar.worldData.Obj;
import com.google.droidar.worldData.World;

/**
 * 
 * Representação do objeto virtual contendo o nome da aplicação e seus drivers.
 * O nome da aplicação será o identificador do marcador.
 * 
 * @author ricardoandrade
 *
 */
public class MeuObjetoVirtual extends BasicMarker {

	/************************************************
	 * VARIABLES
	 ************************************************/
	
	private static int TAMANHO_MAXIMO_NOME_DRIVER = 15;
	private static int LIMITE_NOMES = 5;
	
	private Obj objetoTexto;
	private World world;
	private Activity activity;
	private GLCamera glCamera;
	private String appName;
	private MeshComponent textoMeshComponent;
	private MeshComponent shapeMeshComponent;
	private VirtualObjectCommand virtualObjectCommand;
	private AnimationFaceToCamera animationFaceToCamera;
	boolean firstTime = true;
	
	private List<String> nomeDrivers;
	
	private HydraConnection hydraConnection;

	/************************************************
	 * CONSTRUCTORS
	 ************************************************/
	
	/**
	 * 
	 */
	public MeuObjetoVirtual(String appName, GLCamera camera, World world, Activity activity) {
		super(appName, camera);
		this.world = world;
		this.activity = activity;
		this.glCamera = camera;
		this.appName = appName;
		
		virtualObjectCommand = new VirtualObjectCommand();
		animationFaceToCamera = new AnimationFaceToCamera(glCamera);
		
		hydraConnection = ((MainUOSActivity) activity).getHydraConnection();
	}

	/**
	 * 
	 * @param id
	 * @param camera
	 */
	public MeuObjetoVirtual(String id, GLCamera camera) {
		super(id, camera);
	}
	
	/************************************************
	 * PUBLIC METHODS
	 ************************************************/
	
	/**
	 * Método chamado para atualização da posição do objeto virtual na tela.
	 */
	@Override
	public void setObjectPos(Vec positionVec) {
		
		// Verifica se é a primeira vez que terá suas posições setadas.
		if(firstTime){
			
			// Criando o objeto virtual.
			// Essa parte não foi implementada no construtor porque o mesmo necessita das posicões
			// do objeto para criar o texto.
			criarObjetoVirtual(appName, positionVec, activity, glCamera);
			
			firstTime = false;
		}
		
		shapeMeshComponent.setPosition(positionVec);
		textoMeshComponent.setPosition(positionVec);
	}

	/**
	 * A rotação não foi implementada pois a rotação é calculada com os sensores
	 * na classe ArActivity e repassada para a thread de detecção dos marcadores. 
	 */
	@Override
	public void setObjRotation(float[] rotMatrix) {	}
	
	
	/************************************************
	 * PRIVATE METHODS
	 ************************************************/
	
	/**
	 * Método responsável por criar o objeto virtual na tela e adicionar os eventos de onclick.
	 */
	private void criarObjetoVirtual(String appName, Vec textPosition, Context context, GLCamera glCamera){

		world.add(desenharFundoDoTexto(Color.blackTransparent()));
		world.add(desenharTexto(appName,textPosition, activity, glCamera));
		adicionarEventosOnClick();
		
	}
	
	/**
	 * Método responsável por criar o texto contendo o nome da aplicação e alguns drivers 
	 * disponibilizados pela aplicação. A fim de não "poluir" a tela, nem todos os drivers 
	 * serão apresentados. Os drivers que não forem apresentados nesse momento, poderão ser 
	 * visualizados ao clicar no objeto virtual e redirecionados para uma nova tela. 
	 * 
	 * @param appName
	 * @param textPosition
	 * @param context
	 * @param glCamera
	 * @return
	 */
	private Obj desenharTexto(String appName, Vec textPosition, Context context, GLCamera glCamera){
		
		TextView v = new TextView(context);
		v.setTypeface(null, Typeface.BOLD);
		v.setTextColor(Color.white().toIntRGB());
		v.setText(getTextoFormatado(appName));
		
		this.objetoTexto = new Obj();
	    textoMeshComponent = GLFactory.getInstance().newTexturedSquare("textBitmap"	+ appName, IO.loadBitmapFromView(v), controlarTamanhoDoTexto(appName));
		textoMeshComponent.setPosition(textPosition);
		textoMeshComponent.addAnimation(animationFaceToCamera);
		this.objetoTexto.setComp(this.textoMeshComponent);
		return objetoTexto;
	}

	/**
	 * Método responsável por calcular o tamanho do texto. Apresenta o texto nem muito grande 
	 * e nem muito pequeno, provê também um controle para que o texto não estrapole o tamanho 
	 * do fundo do objeto.
	 *  
	 * @param texto
	 * @return
	 */
	private float controlarTamanhoDoTexto(String appName){
		
		List<String> nomes = buscarNomeDosDrivers(appName);
		
		return  ( validarTamanho(appName) && validarTamanho(nomes) && nomes.size()  < LIMITE_NOMES ) ? 2.75f : 1.65f ;
	}
	
	/**
	 * Método responsável por validar se não há estrapolação no tamanho de todos os nomes.  
	 * @param nomes
	 * @return
	 */
	private boolean validarTamanho(List<String> nomes){

		for (String driverName : nomes) {
			if(driverName.length() > TAMANHO_MAXIMO_NOME_DRIVER){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Método responsável por validar se não há estrapolação no tamanho do nome.
	 * @param nome
	 * @return
	 */
	private boolean validarTamanho(String nome){
		return nome.length() <= TAMANHO_MAXIMO_NOME_DRIVER;
	}
	
	/**
	 * Método responsável por buscar e retornar o nome de todos os drivers 
	 * disponíveis pela aplicação.
	 * 
	 * @param appName
	 * @return
	 */
	private List<String> buscarNomeDosDrivers(String appName){
		
		List<DriverData> drivers = hydraConnection.getDriversByDevice(appName);
		
		nomeDrivers = new ArrayList<String>();
		
		for (DriverData driverData : drivers) {
			String[] split = driverData.getInstanceID().split("\\.");
			
			if(split.length == 0){
				nomeDrivers.add(driverData.getInstanceID());
			}else{
				nomeDrivers.add(split[split.length - 1]);
			}
		}
		
		return nomeDrivers;
	}
	
	/**
	 * Método responsável por retornar o nome dos drivers disponíveis disponívei pela 
	 * aplicação formatado. Com essa formatação será possível apresentar os nomes de 
	 * alguns ou todos os drivers da aplicação. Caso não seja possível apresentar todos
	 * os nomes, os caracteres "..." seá apresentado no final. 
	 * 
	 * @param appName
	 * @return
	 */
	private String getTextoFormatado(String appName){
		
		StringBuilder stringBuilder = new StringBuilder();
		
		List<String> nomeDrivers = buscarNomeDosDrivers(appName);
		int quantidadeNomes = 0;
		
		quantidadeNomes = formata(appName, stringBuilder, true);
		
		stringBuilder.append("\n");
		
		for (int i = 0; i < nomeDrivers.size(); i++) {
				stringBuilder.append("\n");
				// Insere novos nomes dos drivers até o limite estabelecido.
				if ( quantidadeNomes < LIMITE_NOMES ){
					
					// Concatena o nome do driver.
					stringBuilder.append("- ");
					// Valida se o nome não ultrapassou o limite. 
					// Os drivers com nomes muito grande não serão apresentados 
					// compondo o objeto virtual.
					if(validarTamanho(nomeDrivers.get(i))){
					
						stringBuilder.append(nomeDrivers.get(i));
						// Verifica se é o último elemento. Se não for, insere uma nova linha.
						if( (i + 1) < nomeDrivers.size() ){
							stringBuilder.append("\n");
						}
						quantidadeNomes++;
					}else{
						
						quantidadeNomes += formata(nomeDrivers.get(i), stringBuilder, false);
						
					}
					
				}else{
					
					// Será apresentado os "..." caso a quantidade de nomes 
					// tenha alcançado o limite ou tenha algum nome que tenha 
					//estrapolado o tamanho máximo.
					if( ((i + 1) < nomeDrivers.size()) || !validarTamanho(nomeDrivers) ){
						stringBuilder.append("...");
					}
				}
		}			
		return stringBuilder.toString();
	}
	
	/**
	 * Método responsável por criar o fundo do texto apresentado.
	 * @param cor
	 * @return
	 */
	private MeshComponent desenharFundoDoTexto(Color cor) {
		
		shapeMeshComponent = new Shape();
		shapeMeshComponent.addAnimation(animationFaceToCamera);
		
		Shape fundo = new Shape(cor);
		fundo.add(new Vec(-1.5f, 1.75f, 0));
		fundo.add(new Vec(-1.5f, -1.75f, 0));
		fundo.add(new Vec(1.5f, -1.75f, 0));

		fundo.add(new Vec(1.5f, -1.75f, 0));
		fundo.add(new Vec(1.5f, 1.75f, 0));
		fundo.add(new Vec(-1.5f, 1.75f, 0));
		
		fundo.setPosition(new Vec(0, -1f, -0.5f));
		fundo.setRotation(new Vec(90, 0, 0));
		
		shapeMeshComponent.addChild(fundo);

		return shapeMeshComponent;
	}
	
	/**
	 * Método responsável por setar os eventos de onclick nos objetos texto e fundo do texto. 
	 * A ideia aqui é fazer o redirecionamento para uma nova tela quando o objeto for clicado.
	 * Essa nova tela apresentará os drivers disponíveis pela aplicação selecionada, bem como
	 * apresentará opções de redirecionamento ou cancelamento do uso dos recursos pela Hydra.
	 */
	private void adicionarEventosOnClick(){
		virtualObjectCommand.setObjetoVirtual(this);
		textoMeshComponent.setOnClickCommand(virtualObjectCommand);
		textoMeshComponent.setOnLongClickCommand(virtualObjectCommand);
		shapeMeshComponent.setOnClickCommand(virtualObjectCommand);
		shapeMeshComponent.setOnLongClickCommand(virtualObjectCommand);
	}
	
	private int formata(String palavra, StringBuilder stringBuilder, boolean isTitle){
		int tamanho;
		int quantidadeNomes = 0;
		if ( (palavra.length() % TAMANHO_MAXIMO_NOME_DRIVER) == 0){
			tamanho = palavra.length() / TAMANHO_MAXIMO_NOME_DRIVER;
		}else{
			tamanho = (palavra.length() / TAMANHO_MAXIMO_NOME_DRIVER) + 1;
		}
		
		if(isTitle && tamanho == 1){
			
			int qntEspacos = TAMANHO_MAXIMO_NOME_DRIVER - palavra.length();
			
			for(int i=0;i< qntEspacos; i++){
				stringBuilder.append(" ");
			}
			
			stringBuilder.append(palavra);
			
			for(int i=0;i< qntEspacos; i++){
				stringBuilder.append(" ");
			}
			
			return 1;
		}
		
		for ( int j = 0; j < tamanho; j++){
			
			int indiceFinal = 0;
			
			if( ((j + 1) * TAMANHO_MAXIMO_NOME_DRIVER) > palavra.length()){
				indiceFinal = palavra.length(); 
			}else{
				indiceFinal = (j+1) * TAMANHO_MAXIMO_NOME_DRIVER;
			}
			
			stringBuilder.append(palavra.substring(j * TAMANHO_MAXIMO_NOME_DRIVER, indiceFinal));
			
			if( (j + 1) < tamanho ){
				stringBuilder.append("\n  ");
			}
			quantidadeNomes++;
		}
		return quantidadeNomes;
	}

	
	/************************************************
	 * GETTERS AND SETTERS
	 ************************************************/
	
	public Obj getObjetoTexto() {
		return objetoTexto;
	}

	public void setObjetoTexto(Obj objetoTexto) {
		this.objetoTexto = objetoTexto;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public GLCamera getGlCamera() {
		return glCamera;
	}

	public void setGlCamera(GLCamera glCamera) {
		this.glCamera = glCamera;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public List<String> getNomeDrivers() {
		return nomeDrivers;
	}

	public void setNomeDrivers(List<String> nomeDrivers) {
		this.nomeDrivers = nomeDrivers;
	}

	public HydraConnection getHydraConnection() {
		return hydraConnection;
	}

	public void setHydraConnection(HydraConnection hydraConnection) {
		this.hydraConnection = hydraConnection;
	}

	public MeshComponent getTextoMeshComponent() {
		return textoMeshComponent;
	}

	public void setTextoMeshComponent(MeshComponent textoMeshComponent) {
		this.textoMeshComponent = textoMeshComponent;
	}

	public MeshComponent getShapeMeshComponent() {
		return shapeMeshComponent;
	}

	public void setShapeMeshComponent(MeshComponent shapeMeshComponent) {
		this.shapeMeshComponent = shapeMeshComponent;
	}
	
}
