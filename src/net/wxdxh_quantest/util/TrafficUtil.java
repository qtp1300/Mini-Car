package net.wxdxh_quantest.util;

import java.util.ArrayList;
import net.wxdxh_quantest.global.Global;
import net.wxdxh_quantest.model.Coordinates;
import net.wxdxh_quantest.saveUtil.FileService;

import android.graphics.Bitmap;
import android.util.Log;


public class TrafficUtil {
	// // 储存图片左边的像素坐标
		ArrayList<Coordinates> rlistl = new ArrayList<Coordinates>();
		ArrayList<Coordinates> glistl = new ArrayList<Coordinates>();
		// // 储存图片最右边的像素坐标
		ArrayList<Coordinates> rlistr = new ArrayList<Coordinates>();
		ArrayList<Coordinates> glistr = new ArrayList<Coordinates>();
		
		private Bitmap screenshot(Bitmap bitmap) {// 图片截取			
			int startX = 0, startY = 0, endX = 0, endY = 0;
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int[] pixels = new int[width * height];
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			for (int y = height / 11; y < height; y++) {
				int offset = y * width;
				for (int x = width / 8; x < width; x++) {
					int pixel = pixels[offset + x];
					int r = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int b = pixel & 0xff;
					if (r > 150 && g < 120 && b < 120) {    //红色
						startY = y;
						break;
					}
					else if (r > 160 && g > 160 && b < 100) {    //绿色
						startY = y;
						break;
					}
				}
				if (startY != 0)
					break;
			}
			for (int y = 10 * height / 11; y > 0; y--) {
				int offest = y * width;
				for (int x = 7 * width / 8; x > 0; x--) {
					int pixel = pixels[offest + x];
					int r = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int b = pixel & 0xff;
					if (r > 150 && g < 120 && b < 120) {     //红色
						endY = y;
						break;
					}
					else if (r > 160 && g > 160 && b < 100) {     //绿色
						endY = y;
						break;
					}					
				}
				if (endY != 0)
					break;
			}
			for (int x = 1 * width / 8; x < width; x++) {
				for (int y = height / 7; y < height; y++) {
					int pixel = pixels[y * width + x];
					int r = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int b = pixel & 0xff;
					if (r > 150 && g < 120 && b < 120) {       //红色
						startX = x;
						break;
					}else if (r > 160 && g > 160 && b < 100) {    //绿色
						startX = x;
						break;
					}
				}
				if (startX != 0)
					break;
			}
			for (int x = 7 * width / 8 ; x > 0; x--) {
				for (int y = 6 * height / 7; y > 0; y--) {
					int pixel = pixels[y * width + x];
					int r = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int b = pixel & 0xff;
					if (r > 150 && g < 120 && b < 120) {      //红色
						endX = x;
						break;
					}else if (r > 160 && g > 160 && b < 100) {     //绿色
						endX = x;
						break;
					}
				}
				if (endX != 0)
					break;
			}
			Bitmap result = Bitmap.createBitmap(bitmap, startX, startY, endX - startX ,
					endY - startY);
			new FileService().savePhoto(result, Global.QIE+".png");
			return result;
		}
		
		private Bitmap convertToBlack(Bitmap bip) {// 像素处理背景变为黑色，红绿黄不变
			int width = bip.getWidth();
			int height = bip.getHeight();
			int[] pixels = new int[width * height];
			bip.getPixels(pixels, 0, width, 0, 0, width, height);
			int[] pl = new int[bip.getWidth() * bip.getHeight()];
			for (int y = 0; y < height; y++) {
				int offset = y * width;
				for (int x = 0; x < width; x++) {
					int pixel = pixels[offset + x];
					int r = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int b = pixel & 0xff;
							if (r > 180 && g < 200 && b < 200) // 红色
								pl[offset + x] = pixel;
							else if(r > 160 && g > 160 && b < 100) // 绿色
								pl[offset + x] = pixel;
					else
						pl[offset + x] = 0xff000000;// 黑色
//						pl[offset + x] = 0xffffffff;// 白色
				}
			}
			Bitmap result = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			result.setPixels(pl, 0, width, 0, 0, width, height);
			new FileService().savePhoto(result, Global.BIANHEI+".png");
			return result;

		}
		
		public int  shapeIdentification(Bitmap bp) {						
			rlistl.clear();
			glistl.clear();
			rlistr.clear();
			glistr.clear();
			int search_index = 0;
			int rindex=0;
			Bitmap bitmap = screenshot(convertToBlack(bp));
			new FileService().savePhoto(bitmap, Global.TRAFFICHANDLE+".png");
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int[] pixels = new int[width * height];
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			for (int y = 0; y < height; y++) {// 得到左边所有的坐标
				int offset = y * width;
				for (int x = 0; x < width; x++) {
					int pixel = pixels[offset + x];
					if (pixel != 0xff000000) {
						int r = (pixel >> 16) & 0xff;
						int g = (pixel >> 8) & 0xff;
						int b = pixel & 0xff;
						if (r > 180 && g < 200 && b < 200) {// 红色
							rlistl.add(new Coordinates(x, y));
							break;
						} else if (r > 160 && g > 160 && b < 100) {// 绿色
							glistl.add(new Coordinates(x, y));
							break;
						}
					}
				}
			}
			for (int x = width - 1; x > 0; x--) {// 得到最右边的坐标
				for (int y = 0; y < height; y++) {
					int pixel = pixels[y * width + x];
					if (pixel != 0xff000000) {
						int r = (pixel >> 16) & 0xff;
						int g = (pixel >> 8) & 0xff;
						int b = pixel & 0xff;
						if (r > 180 && g < 200 && b < 200) {// 红色							
							if(rindex==9)
							rlistr.add(new Coordinates(x, y));
							search_index++;
						} else if (r > 160 && g > 160 && b < 100) {// 绿色
							if(rindex==9)
							glistr.add(new Coordinates(x, y));
							search_index++;
						}
					}
				}
				if (search_index > 3){// 寻找到最右边像素点
					rindex++;
					if(rindex>9)
					break;
				}
			}
			if (rlistl.size() > glistl.size()) {
				shapeResult=shape(rlistl, rlistr, 1);
				Log.e("红色个数右", rlistr.size() + "");
				Log.e("红色个数左", rlistl.size() + "");
			} else if (glistl.size() > rlistl.size()) {
				shapeResult=shape(glistl, glistr, 2);
				Log.e("绿色个数右", glistr.size() + "");
				Log.e("绿色个数左", glistl.size() + "");
			}
			return shapeResult;
		}
		int shapeResult;
		double minNum = 3.0 / 13;
		double midNum = 5.0 / 12;
		
		private int shape(ArrayList<Coordinates> listl,
				ArrayList<Coordinates> listr, int sort) {
			int state=0;//箭头方向状态
			int index = listl.size();// 像素点总高度
			int index_r=listr.size();
			Log.e("像素点高度：", index+"");
			if (index > 8&& index_r>2) {
				double midderNum = listr.get(listr.size() - 1).getY()
						- listr.get(0).getY();
				Log.e("右边像素个数：", midderNum+""+1.0*midderNum / index );
				Log.e("像素差",midderNum+"");
				Log.e("hahhahahha：", 1.0*midderNum / index+"" );
				if (1.0*midderNum / index < minNum) {// 箭头右边
					if (sort == 1) {// 红色
						state=1;//"交通灯为：红色向右箭头"
					} else if (sort == 2) {// 绿色
						state=2;//"交通灯为：绿色向右箭头"
					}
				} 
				else if (1.0*midderNum / index < midNum) {// //箭头左边
					if (sort == 1) {// 红色
						state=3;//"交通灯为：红色向左箭头"
					} else if (sort == 2) {// 绿色
						state=4;// "交通灯为：绿色向左箭头"
					}
				}
				else {// 箭头拐弯
						state=5;//"交通灯为：掉头"
				}
			} else {
				state=0;//"交通灯颜色识别失败"
			}
			return state;
		}
}
