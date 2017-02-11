package net.wxdxh_quantest.util;

import java.util.ArrayList;


import net.wxdxh_quantest.global.Global;
import net.wxdxh_quantest.model.Coordinates;
import net.wxdxh_quantest.saveUtil.FileService;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;


public class ShapeUtil1 {
	private int rectNum=0;//����
	private int triaNum=0;//������
	private int circNum=0;//Բ��
	private int[] location_X = new int[512];
	private int[] location_Y = new int[512];
	
	private int[] red = {120 , 30 , 30};
	private int[] green = {160 , 180 , 120};      //��LCD 160  210  80
	private int[] yellow = {180 , 180 , 100};
		
	private Bitmap convertToBlack(Bitmap bip, int index) {// ���ش�������Ϊ��ɫ�����̻Ʋ���
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
						if (index==1&&r > red[0] && g < red[1] && b < red[2]) // ��ɫ
							pl[offset + x] = pixel;
						else if(index==2&&r < green[0] && g > green[1] && b < green[2]) // ��ɫ
							pl[offset + x] = pixel;
						else if (index==3&&r > yellow[0] && g > yellow[1] && b < yellow[2])// ��ɫ
							pl[offset + x] = pixel;
				else
					pl[offset + x] = 0xff000000;// ��ɫ
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		result.setPixels(pl, 0, width, 0, 0, width, height);
		return result;

	}
	
	public int shapeNum(Bitmap bp,int index,int shape){//�������ҷָ��ʶ��ͼƬ��״
				rectNum = 0;    //����
				triaNum = 0;    //����
				circNum = 0;    //Բ��
				int result=0;
				shapeDivision(convertToBlack(bp, index));//���ҷָ�
			    for (int i = 0; i < index1; i++) {
				     Bitmap btmap = new FileService().readPhoto("ͼƬ" + ( i + 1 )+ ".png");
				     shapeDivision1(btmap);//���·ָ�
			    }
					for (int i = 0; i < index11; i++) {
						Bitmap bitmap = new FileService().readPhoto("1ͼƬ" + (i + 1)+ ".png");
						int width = bitmap.getWidth();
						int height = bitmap.getHeight();
						int result1 = 0;
						int result_id=shapeIdentification(bitmap);//ʶ������ͼƬ
						if(result_id == -1){
							for (int j = 1; j < 36; j++) {
								 Matrix matrix = new Matrix();
								 matrix.postRotate(j*10);
								 Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
									        width, height, matrix, true);
								 result1=shapeIdentification(resizedBitmap);//ʶ������ͼƬ
								 if (result1 != -1) {
									break;
								}
							}
							if (result1 != -1) {
								
							}else if(rlistl.size()>20&&rlistr.size()>20||
									 glistl.size()>20&&glistr.size()>20||
									 blistl.size()>20&&blistr.size()>20){
								Log.e("�鿴", rlistl.size()+"");
								circNum++;
							}
						}
					}
			    Log.e("����ͼ������","���θ�����" + rectNum + "" + "�����θ�����" + triaNum+ ""
							+ "Բ�θ�����" + circNum);
				switch (shape) {
				case 1:
					result= triaNum;     //���Ǹ���
					break;
				case 2:
					result= circNum;    //Բ�θ���
					break;
				case 3:
					result= rectNum;   //���θ���
					break;
				default:
					break;
				}
				return result;
			}
	// // ����ͼƬ�����������
	ArrayList<Coordinates> rlistl = new ArrayList<Coordinates>();
	ArrayList<Coordinates> glistl = new ArrayList<Coordinates>();
	ArrayList<Coordinates> blistl = new ArrayList<Coordinates>();
	// // ����ͼƬ�ұ���������
	ArrayList<Coordinates> rlistr = new ArrayList<Coordinates>();
	ArrayList<Coordinates> glistr = new ArrayList<Coordinates>();
	ArrayList<Coordinates> blistr = new ArrayList<Coordinates>();
	int index1 = 0;
	// // �洢ͼƬ�Ϸ�����ֵ
	ArrayList<Coordinates> list = new ArrayList<Coordinates>();	
	private void shapeDivision(Bitmap bp) {//���ҷָ�
		list.clear();
		int width = bp.getWidth();
		int height = bp.getHeight();
		int[] pixels = new int[width * height];
		bp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = pixels[y*width + x];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
					if (r > red[0] && g < red[1] && b < red[2]) { // ��ɫ
						list.add(new Coordinates(x, y));
						break;
					}
					else if ( r < green[0] && g > green[1] && b < green[2]) { // ��ɫ
						list.add(new Coordinates(x, y));
						break;
					}
					else if (r > yellow[0] && g > yellow[1] && b < yellow[2]) {// ��ɫ
						list.add(new Coordinates(x, y));
						break;
					}
			}
		}
		for (int i = 0; i < list.size(); i++) {
			if(i>0){
				if (list.get(i).getX() - list.get(i-1).getX()>2) {
					index1++;
					location_X[index1] = list.get(i).getX();
					Bitmap bitmap = Bitmap.createBitmap(bp, location_X[index1 - 1],
							0, location_X[index1]-location_X[index1 - 1], height);
					new FileService().savePhoto(bitmap, "ͼƬ" + index1 + ".png");
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(bp, location_X[index1],0, width-location_X[index1], height);
		new FileService().savePhoto(bitmap, "ͼƬ"+(++index1)+".png");
	}
	int index11 = 0;
	// �洢ͼƬ�Ϸ�����ֵ
	ArrayList<Coordinates> Rlist = new ArrayList<Coordinates>();
	// �õ������Ҫ����ɫ���е����ص�����
		public void shapeDivision1(Bitmap bp) {//���·ָ�
			Rlist.clear();
			int width = bp.getWidth();
			int height = bp.getHeight();
			int[] pixels = new int[width * height];
			bp.getPixels(pixels, 0, width, 0, 0, width, height);
			for (int y = 0; y < height; y++) {
				int offset = y * width;
				for (int x = 0; x < width; x++) {
					int pixel = pixels[offset + x];
					if (pixel != 0xff000000) {
						int r = (pixel >> 16) & 0xff;
						int g = (pixel >> 8) & 0xff;
						int b = pixel & 0xff;
						if (r > red[0] && g < red[1] && b < red[2]) {// ��ɫ
							Rlist.add(new Coordinates(x, y));
							break;
						} else if (r < green[0] && g > green[1] && b < green[2]) {// ��ɫ
							Rlist.add(new Coordinates(x, y));
							break;
						} else if (r > yellow[0] && g > yellow[1] && b < yellow[2]) {// ��ɫ
							Rlist.add(new Coordinates(x, y));
							break;
						}
					}
				}
			}
			for (int i = 0; i < Rlist.size(); i++) {
				if (i > 0) {
					if (Rlist.get(i).getY() - Rlist.get(i - 1).getY() > 2) {      
						index11++;
						location_Y[index11] = Rlist.get(i).getY();
						Bitmap bitmap = Bitmap.createBitmap(bp, 0,location_Y[index11 - 1], width, location_Y[index11]- location_Y[index11 - 1]);
						new FileService().savePhoto(bitmap, "1ͼƬ" + index11+ ".png");
					}          
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(bp, 0, location_Y[index11], width,height - location_Y[index11]);			
			new FileService().savePhoto(bitmap, "1ͼƬ" + (++index11) + ".png");
		}
	//ʶ�������������
	private int shapeIdentification(Bitmap bp) {
		rlistl.clear();
		glistl.clear();
		blistl.clear();
		rlistr.clear();
		glistr.clear();
		blistr.clear();
		int up_index=0;
		int down_index=0;
		int num_index=0;
		int width = bp.getWidth();
		int height = bp.getHeight();
		int[] pixels = new int[width * height];
		bp.getPixels(pixels, 0, width, 0, 0, width, height);
		// �õ������5����������
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				int pixel = pixels[offset + x];
					int r = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int b = pixel & 0xff;
						if ( r > red[0] && g < red[1] && b < red[2] ) {// ��ɫ
							if(up_index==5)
							rlistl.add(new Coordinates(x, y));
							num_index++;
						} else if (r < green[0] && g > green[1] && b < green[2]) {// ��ɫ
							if(up_index==5)
							glistl.add(new Coordinates(x, y));
							num_index++;
						}else if (r > yellow[0] && g > yellow[1] && b < yellow[2]) {// ��ɫ
							if(up_index==5)
							blistl.add(new Coordinates(x, y));
							num_index++;
						}
					} 
				if(num_index>0){
					up_index++;
					if(up_index>5)
						break;
			}
		}
		num_index=0;
			// �õ������5������
		for (int y = height-1; y > 0; y--) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
					int pixel = pixels[offset + x];
					int r = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int b = pixel & 0xff;
						if (r > red[0] && g < red[1] && b < red[2]) {// ��ɫ
							if(down_index==5)
							rlistr.add(new Coordinates(x, y));
							num_index++;
						} else if (r < green[0] && g > green[1] && b < green[2]) {// ��ɫ
							if(down_index==5)
							glistr.add(new Coordinates(x, y));
							num_index++;
						}else if (r > yellow[0] && g > yellow[1] && b < yellow[2]) {// ��ɫ
							if(down_index==5)
							blistr.add(new Coordinates(x, y));
							num_index++;
						}
					} 
				if(num_index>0){
					down_index++;
					if(down_index>5)
						break;
			}
		}
		if (rlistl.size() > glistl.size()&&rlistl.size() > blistl.size()) {
			shape(rlistl, rlistr);
			Log.e("��ɫ����", rlistl.size() + "");
		}
		else if (glistl.size() > blistl.size()) {
			shape(glistl, glistr);
			Log.e("��ɫ����", glistl.size() + "");
		}
		else if (blistl.size() > glistl.size()) {
			shape(blistl, blistr);
			Log.e("��ɫ����", blistl.size() + "");
		}
		else {
		}
		return shapeResult;
	}	
	int shapeResult = 0;
	int minNum = 45;
	// ��״ʶ��
	private void shape(ArrayList<Coordinates> listl,
			ArrayList<Coordinates> listr) {
		int indexl = listl.size();
		int indexr=listr.size();
		if (indexl > 0&&indexr>0) {// �ж���ɫʶ��ɹ�
			int differNum_up = indexl;
			int differNum_down = indexr;
			Log.e("��������ֵ��", differNum_up + "��������ֵ��" + differNum_down);
			// ������
			if (Math.abs(differNum_down-differNum_up)>minNum) {
				triaNum++;
				shapeResult=0;
			}
			// ����
			else  if(differNum_down>minNum&&differNum_up>minNum){
				rectNum++;
				shapeResult=0;
			}
			// Բ�Ρ������Ρ�����
			else{
				shapeResult=-1;
			}
		} else{
		}
		
	}
}
