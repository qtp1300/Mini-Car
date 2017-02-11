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
	private Bitmap screenshot(Bitmap bitmap) {// 图片截取
		Bitmap  bip=Bitmap.createBitmap(bitmap, bitmap.getWidth()/7, bitmap.getHeight()/5, 5*bitmap.getWidth()/7, 3 * bitmap.getHeight()/5);//去边框
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
				if (r < 15 && g < 30 && b > 40) {// 蓝色(找边框可能是黑色需要改动）
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
				if (r < 15 && g < 30 && b > 40) {// 蓝色
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
				if (r < 15 && g < 30 && b > 40) {// 蓝色
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
				if (r < 15 && g < 30 && b > 40) {// 蓝色
					endX = x;
					break;
				}
			}
			if (endX != 0)
				break;
		}
		Log.e("坐标范围：", "左边坐标X：" + startX + "左边坐标Y：" + startY + "下边坐标X：" + endX
				+ "下边坐标Y：" + endY);
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
	 * 进行图片识别
	 * 
	 * @param bitmap
	 *            待识别图片
	 * @param language
	 *            识别语言
	 * @return 识别结果字符串
	 */
	public String doOcr(Bitmap bitmap) {
		TessBaseAPI baseApi = new TessBaseAPI();

		baseApi.init(getSDPath(), "eng");

		// 必须加此行，tess-two要求BMP必须为此配置
		// copy改为图的大小产生一个新位图，图像的大小为256位图。true表示产生的图片可以切割。
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);		
		new FileService().savePhoto(bitmap, Global.HUIDU+".png");
		baseApi.setImage(bitmap);
		
		String text = baseApi.getUTF8Text();
		baseApi.clear();
		baseApi.end();
        Log.e("查看内容", text);
		return text; // 加载到本地的字库
	}

	/**
	 * 获取sd卡的路径
	 * 
	 * @return 路径的字符串
	 */
	public  String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取外存目录
		}
		return sdDir.toString();
	}

	// .为了提高图像的识别率，首先要灰度化
	public Bitmap convertToGrayscale(Bitmap bitmap) {
        
	    Bitmap reBitmap = screenshot(bitmap);      //切割图形
	    
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
