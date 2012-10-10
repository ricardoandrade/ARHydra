package br.unb.unbiquitous.marker.virtual.object;

import javax.microedition.khronos.opengles.GL10;

import com.google.droidar.gl.Color;
import com.google.droidar.gl.Renderable;
import com.google.droidar.gl.scenegraph.MeshComponent;
import com.google.droidar.worldData.Visitor;

/**
 * Classe responsável por extender as características
 * da classe MeshComponent, tendo como objetivo a criação
 * do objeto virtual. 
 * 
 * @author ricardoandrade
 *
 */
public class MeuMeshComponent extends MeshComponent {
	
	/************************************************
	 * CONSTRUCTOR
	 ************************************************/
	public MeuMeshComponent(){
		this(null);
	}
	
	/************************************************
	 * PROTECTED METHODS
	 ************************************************/
	protected MeuMeshComponent(Color color) {
		super(color);
	}

	/************************************************
	 * PUBLIC METHODS
	 ************************************************/
	
	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

	@Override
	public void draw(GL10 gl, Renderable parent) {
		
	}

}
