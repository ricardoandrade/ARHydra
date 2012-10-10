package br.unb.unbiquitous.marker.decoder;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.android.PlanarYUVLuminanceSource;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;

public class QRCodeDecoder {

	private final MultiFormatReader multiFormatReader;
	private boolean running = true;
	private CameraManager cameraManager;

	private String textDecoded;

	public QRCodeDecoder(CameraManager cameraManager) {
		multiFormatReader = new MultiFormatReader();
		
		Map<DecodeHintType,Collection<BarcodeFormat>>  hints = new EnumMap<DecodeHintType,Collection<BarcodeFormat>>(DecodeHintType.class);
		 Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
		 decodeFormats.add(BarcodeFormat.QR_CODE);
	        

		    // The prefs can't change while the thread is running, so pick them up once here.
//		    if (decodeFormats == null || decodeFormats.isEmpty()) {
//		      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
//		      decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
//		      if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D, false)) {
//		        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
//		      }
//		      
//		      
//		      if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_QR, false)) {
//		        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
//		      }
//		      
//		      
//		      if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX, false)) {
//		        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
//		      }
//		    }
		 
		    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

//		    if (characterSet != null) {
//		      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
//		    }
//		    
//		    
//		    hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
//		
		multiFormatReader.setHints(hints);
		
		this.cameraManager = cameraManager;
	}

	/**
	 * Decode the data within the viewfinder rectangle, and time how long it
	 * took. For efficiency, reuse the same reader objects from one decode to
	 * the next.
	 * 
	 * @param data
	 *            The YUV preview frame.
	 * @param width
	 *            The width of the preview frame.
	 * @param height
	 *            The height of the preview frame.
	 */
	public Result decode(byte[] data, int width, int height) {
		Result rawResult = null;
		PlanarYUVLuminanceSource source = cameraManager.buildLuminanceSource(
				data, width, height);
		if (source != null) {
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			try {
				rawResult = multiFormatReader.decodeWithState(bitmap);
				textDecoded = rawResult.getText();
				return rawResult;
			} catch (ReaderException re) {
				// continue
			} finally {
				multiFormatReader.reset();
			}
		}
		return null;
	}

	public String getTextDecoded() {
		return textDecoded;
	}

	
}
