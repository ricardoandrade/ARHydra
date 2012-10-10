package br.unb.unbiquitous.marker.decoder;

import java.util.ArrayList;
import java.util.List;

import com.google.zxing.client.android.camera.CameraManager;

import br.unb.unbiquitous.jni.MarkerDetectionJni;
import br.unb.unbiquitous.jni.ZbarJni;
import br.unb.unbiquitous.util.Medicao;
import android.app.Activity;

/**
 * Essa classe é responsável por disponibilizar objetos
 * usados na decodificação de marcadores e QRCodes.
 * 
 * @author ricardoandrade
 *
 */
//TODO [Ricardo] Fazer o refactor para ver se essa classe dá para sumir.
public class DecoderObject {

	/************************************************
	 * VARIABLES
	 ************************************************/
	
	private Integer orientation;
	private ZbarJni zbar ;
	private MarkerDetectionJni markerDetection;
	private QRCodeDecoder qrCodeDecoder;
	
	private List<Medicao> medicoes;
	
	/************************************************
	 * GETTERS AND SETTERS
	 ************************************************/
	
	public DecoderObject(Activity activity) {
		qrCodeDecoder = new QRCodeDecoder(new CameraManager(activity.getApplication()));
		orientation = new Integer(0);
		markerDetection = new MarkerDetectionJni();
		zbar  = new ZbarJni();
		medicoes = new ArrayList<Medicao>();
	}

	public Integer getOrientation() {
		return orientation;
	}

	public void setOrientation(Integer rotation) {
		this.orientation = rotation;
	}

	public ZbarJni getZbar() {
		return zbar;
	}

	public void setZbar(ZbarJni zbar) {
		this.zbar = zbar;
	}

	public MarkerDetectionJni getMarkerDetection() {
		return markerDetection;
	}

	public void setMarkerDetection(MarkerDetectionJni markerDetection) {
		this.markerDetection = markerDetection;
	}

	public QRCodeDecoder getQrCodeDecoder() {
		return qrCodeDecoder;
	}

	public void setQrCodeDecoder(QRCodeDecoder qrCodeDecoder) {
		this.qrCodeDecoder = qrCodeDecoder;
	}

	public List<Medicao> getMedicoes() {
		return medicoes;
	}

	public void setMedicoes(List<Medicao> medicoes) {
		this.medicoes = medicoes;
	}
	
	
	
}
