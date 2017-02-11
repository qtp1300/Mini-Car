package net.wxdxh_quantest.util;

import java.io.File;

import net.wxdxh_quantest.client.Client;
import net.wxdxh_quantest.global.Global;
import net.wxdxh_quantest.saveUtil.FileService;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class LicenseUtil {
	private Bitmap screenshot(Bitmap bitmap) {// ͼƬ��ȡ
		Bitmap  bip=Bitmap.createBitmap(bitmap, bitmap.getWidth()/7, bitmap.getHeight()/5, 5*bitmap.getWidth()/7, 3 * bitmap.getHeight()/5);//ȥ�߿�
		new FileService().savePhoto(bip, Global.QUBIANKUNG+".png");
		int startX = 0, startY = 0, endX = 0, endY = 0;
		int width = bip.getWidth();
		int height = bip.getHeight();
		int[] pixels = new int[width * height];
		bip.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int y = height / 7; y < height; y++) {
			int offset = y * width;
			for (int x = width / 8; x < width; x++) {
				int pixel = pixels[offset + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				if (r < 15 && g < 30 && b > 40) {// ��ɫ(�ұ߿�����Ǻ�ɫ��Ҫ�Ķ���
					startY = y;
					break;
				}
			}
			if (startY != 0)
				break;
		}
		for (int y = 6 * height / 7; y > 0; y--) {
			int offest = y * width;
			for (int x = 7 * width / 8; x > 0; x--) {
				int pixel = pixels[offest + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				if (r < 15 && g < 30 && b > 40) {// ��ɫ
					endY = y;
					break;
				}
			}
			if (endY != 0)
				break;
		}
		for (int x = 0; x < width; x++) {
			for (int y = height / 7; y < height; y++) {
				int pixel = pixels[y * width + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				if (r < 15 && g < 30 && b > 40) {// ��ɫ
					startX = x;
					break;
				}
			}
			if (startX != 0)
				break;
		}
		for (int x =  width  ; x > 0; x--) {
			for (int y = 6 * height / 7; y > 0; y--) {
				int pixel = pixels[y * width + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				if (r < 15 && g < 30 && b > 40) {// ��ɫ
					endX = x;
					break;
				}
			}
			if (endX != 0)
				break;
		}
		Log.e("���귶Χ��", "�������X��" + startX + "�������Y��" + startY + "�±�����X��" + endX
				+ "�±�����Y��" + endY);
        if (startX > 80 && startY > 0 && endX > 200 && endY > 0) {					
		Bitmap result = Bitmap.createBitmap(bip, startX+43, startY+8, endX - (startX+44) ,
				endY - (startY+14));
		    return result;
        }else {
        	Client.lice_flag = true;
			return bitmap;
		}
	}
	/**
	 * ����ͼƬʶ��
	 * 
	 * @param bitmap
	 *            ��ʶ��ͼƬ
	 * @param language
	 *            ʶ������
	 * @return ʶ�����ַ���
	 */
	public String doOcr(Bitmap bitmap) {
		TessBaseAPI baseApi = new TessBaseAPI();

		baseApi.init(getSDPath(), "eng");

		// ����Ӵ��У�tess-twoҪ��BMP����Ϊ������
		// copy��Ϊͼ�Ĵ�С����һ����λͼ��ͼ��Ĵ�СΪ256λͼ��true��ʾ������ͼƬ�����и
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);		
		new FileService().savePhoto(bitmap, Global.HUIDU+".png");
		baseApi.setImage(bitmap);
		
		String text = baseApi.getUTF8Text();
		baseApi.clear();
		baseApi.end();
        Log.e("�鿴����", text);
		return text; // ���ص����ص��ֿ�
	}

	/**
	 * ��ȡsd����·��
	 * 
	 * @return ·�����ַ���
	 */
	public  String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// ��ȡ���Ŀ¼
		}
		return sdDir.toString();
	}

	// .Ϊ�����ͼ���ʶ���ʣ�����Ҫ�ҶȻ�
	public Bitmap convertToGrayscale(Bitmap bitmap) {
        
	    Bitmap reBitmap = screenshot(bitmap);      //�и�ͼ��
	    
	    new FileService().savePhoto(reBitmap, Global.QIEGE+".png");
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		Paint paint = new Paint();
		ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(cmcf);

		Bitmap result = Bitmap.createBitmap(reBitmap.getWidth(),
				reBitmap.getHeight(), Bitmap.Config.RGB_565);     
		Canvas drawingCanvas = new Canvas(result);
		Rect src = new Rect(0, 0, reBitmap.getWidth(), reBitmap.getHeight());
		Rect dst = new Rect(src);
		drawingCanvas.drawBitmap(reBitmap, src, dst, paint);
		return result;
	}
}
