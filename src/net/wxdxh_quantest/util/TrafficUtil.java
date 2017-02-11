package net.wxdxh_quantest.util;

import java.util.ArrayList;
import net.wxdxh_quantest.global.Global;
import net.wxdxh_quantest.model.Coordinates;
import net.wxdxh_quantest.saveUtil.FileService;

import android.graphics.Bitmap;
import android.util.Log;


public class TrafficUtil {
	// // ����ͼƬ��ߵ���������
		ArrayList<Coordinates> rlistl = new ArrayList<Coordinates>();
		ArrayList<Coordinates> glistl = new ArrayList<Coordinates>();
		// // ����ͼƬ���ұߵ���������
		ArrayList<Coordinates> rlistr = new ArrayList<Coordinates>();
		ArrayList<Coordinates> glistr = new ArrayList<Coordinates>();
		
		private Bitmap screenshot(Bitmap bitmap) {// ͼƬ��ȡ			
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
					if (r > 150 && g < 120 && b < 120) {    //��ɫ
						startY = y;
						break;
					}
					else if (r > 160 && g > 160 && b < 100) {    //��ɫ
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
					if (r > 150 && g < 120 && b < 120) {     //��ɫ
						endY = y;
						break;
					}
					else if (r > 160 && g > 160 && b < 100) {     //��ɫ
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
					if (r > 150 && g < 120 && b < 120) {       //��ɫ
						startX = x;
						break;
					}else if (r > 160 && g > 160 && b < 100) {    //��ɫ
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
					if (r > 150 && g < 120 && b < 120) {      //��ɫ
						endX = x;
						break;
					}else if (r > 160 && g > 160 && b < 100) {     //��ɫ
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
		
		private Bitmap convertToBlack(Bitmap bip) {// ���ش�������Ϊ��ɫ�����̻Ʋ���
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
							if (r > 180 && g < 200 && b < 200) // ��ɫ
								pl[offset + x] = pixel;
							else if(r > 160 && g > 160 && b < 100) // ��ɫ
								pl[offset + x] = pixel;
					else
						pl[offset + x] = 0xff000000;// ��ɫ
//						pl[offset + x] = 0xffffffff;// ��ɫ
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
			for (int y = 0; y < height; y++) {// �õ�������е�����
				int offset = y * width;
				for (int x = 0; x < width; x++) {
					int pixel = pixels[offset + x];
					if (pixel != 0xff000000) {
						int r = (pixel >> 16) & 0xff;
						int g = (pixel >> 8) & 0xff;
						int b = pixel & 0xff;
						if (r > 180 && g < 200 && b < 200) {// ��ɫ
							rlistl.add(new Coordinates(x, y));
							break;
						} else if (r > 160 && g > 160 && b < 100) {// ��ɫ
							glistl.add(new Coordinates(x, y));
							break;
						}
					}
				}
			}
			for (int x = width - 1; x > 0; x--) {// �õ����ұߵ�����
				for (int y = 0; y < height; y++) {
					int pixel = pixels[y * width + x];
					if (pixel != 0xff000000) {
						int r = (pixel >> 16) & 0xff;
						int g = (pixel >> 8) & 0xff;
						int b = pixel & 0xff;
						if (r > 180 && g < 200 && b < 200) {// ��ɫ							
							if(rindex==9)
							rlistr.add(new Coordinates(x, y));
							search_index++;
						} else if (r > 160 && g > 160 && b < 100) {// ��ɫ
							if(rindex==9)
							glistr.add(new Coordinates(x, y));
							search_index++;
						}
					}
				}
				if (search_index > 3){// Ѱ�ҵ����ұ����ص�
					rindex++;
					if(rindex>9)
					break;
				}
			}
			if (rlistl.size() > glistl.size()) {
				shapeResult=shape(rlistl, rlistr, 1);
				Log.e("��ɫ������", rlistr.size() + "");
				Log.e("��ɫ������", rlistl.size() + "");
			} else if (glistl.size() > rlistl.size()) {
				shapeResult=shape(glistl, glistr, 2);
				Log.e("��ɫ������", glistr.size() + "");
				Log.e("��ɫ������", glistl.size() + "");
			}
			return shapeResult;
		}
		int shapeResult;
		double minNum = 3.0 / 13;
		double midNum = 5.0 / 12;
		
		private int shape(ArrayList<Coordinates> listl,
				ArrayList<Coordinates> listr, int sort) {
			int state=0;//��ͷ����״̬
			int index = listl.size();// ���ص��ܸ߶�
			int index_r=listr.size();
			Log.e("���ص�߶ȣ�", index+"");
			if (index > 8&& index_r>2) {
				double midderNum = listr.get(listr.size() - 1).getY()
						- listr.get(0).getY();
				Log.e("�ұ����ظ�����", midderNum+""+1.0*midderNum / index );
				Log.e("���ز�",midderNum+"");
				Log.e("hahhahahha��", 1.0*midderNum / index+"" );
				if (1.0*midderNum / index < minNum) {// ��ͷ�ұ�
					if (sort == 1) {// ��ɫ
						state=1;//"��ͨ��Ϊ����ɫ���Ҽ�ͷ"
					} else if (sort == 2) {// ��ɫ
						state=2;//"��ͨ��Ϊ����ɫ���Ҽ�ͷ"
					}
				} 
				else if (1.0*midderNum / index < midNum) {// //��ͷ���
					if (sort == 1) {// ��ɫ
						state=3;//"��ͨ��Ϊ����ɫ�����ͷ"
					} else if (sort == 2) {// ��ɫ
						state=4;// "��ͨ��Ϊ����ɫ�����ͷ"
					}
				}
				else {// ��ͷ����
						state=5;//"��ͨ��Ϊ����ͷ"
				}
			} else {
				state=0;//"��ͨ����ɫʶ��ʧ��"
			}
			return state;
		}
}
