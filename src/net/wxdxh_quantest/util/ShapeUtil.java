package net.wxdxh_quantest.util;

import java.util.ArrayList;
import net.wxdxh_quantest.model.Coordinates;
import net.wxdxh_quantest.saveUtil.FileService;

import android.graphics.Bitmap;

import android.util.Log;
import android.widget.Toast;

public class ShapeUtil {
	
		private int [] colorNum=new int[8];//�졢�̡������ơ�Ʒ���ࡢ��ɫ����
		private int blackMax=80;//��ɫ���RGBֵ��
		private int RGBMax=600;//���������RGBֵ��
		private int noiseMax=800;//��Ʒ�����RGBֵ��
		private Bitmap convertToBlack(Bitmap bip) {// ���ش�������Ϊ��ɫ���졢�̡������ơ�Ʒ���ࡢ��ɫ����ɫ����
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
					int rgb=r+g+b;
					if (rgb<blackMax) {//��ɫ
						pl[offset + x] = pixel;
						colorNum[6]++;//��ɫ
					} else if (rgb<RGBMax) {// ������
						pl[offset + x] = pixel;
						if(r>g&&r>b)
							colorNum[0]++;//��ɫ
						else if(g>b)
							colorNum[1]++;//��ɫ
						else
							colorNum[2]++;//��ɫ
							
					} else if (rgb<noiseMax && rgb>RGBMax) {//�ơ�Ʒ����
						pl[offset + x] = pixel;
						if(b<r&&b<g)
							colorNum[3]++;//��ɫ
						else if(g<r&&g<b)
						colorNum[4]++;//Ʒɫ
						else
							colorNum[5]++;//��ɫ
						
					} else {
						pl[offset + x] = 0xffffffff;// ��ɫ
					}
				}
			}
			
			Bitmap result = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);//����ɫֵ���¸����½���ͼƬ ͼƬ�Ŀ��Ϊ��ǰͼƬ��ֵ
			result.setPixels(pl, 0, width, 0, 0, width, height);
			return result;
		}
		public int[] getMaxIndex(int[] arr){
			if(arr==null||arr.length==0){
			return null;//�������Ϊ�� �����ǳ���Ϊ0 �ͷ���null
			}
			int maxIndex=0;//�����һ��Ԫ��Ϊ���ֵ ��ô�±���Ϊ0
			int[] arrnew=new int[2];//����һ�� ����Ϊ2������ ������¼ �涨��һ��Ԫ�ش洢���ֵ �ڶ���Ԫ�ش洢�±�
			for(int i =0;i<arr.length-1;i++){
			if(arr[maxIndex]<arr[i+1]){
			maxIndex=i+1;
			arrnew[0]=arr[maxIndex];
			arrnew[1]=maxIndex;
			}
			}
			return arrnew;
			}
		
		public String colour(Bitmap bp){
			String result;
			convertToBlack(bp);
			int [] abc = getMaxIndex(colorNum);
//			colorNum = null;
			int a = abc[1];
			colorNum = null;
            if (a == 0) {
				result = "��ɫ";
			}else if (a == 1) {
				result = "��ɫ";
			}else if (a == 2) {
				result = "��ɫ";
			}else if (a == 3) {
				result = "��ɫ";
			}else if (a == 4) {
				result = "Ʒɫ";
			}else if (a == 5) {
				result = "��ɫ";
			}else if (a == 6) {
				result = "��ɫ";
			}
            else {
				result = "��ɫ";
			}
            return result;
		}
		
		
}
