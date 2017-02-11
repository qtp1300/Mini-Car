package net.wxdxh_quantest.util;

import java.util.ArrayList;
import net.wxdxh_quantest.model.Coordinates;
import net.wxdxh_quantest.saveUtil.FileService;

import android.graphics.Bitmap;

import android.util.Log;
import android.widget.Toast;

public class ShapeUtil {
	
		private int [] colorNum=new int[8];//红、绿、蓝、黄、品、青、黑色个数
		private int blackMax=80;//黑色最大RGB值和
		private int RGBMax=600;//红绿蓝最大RGB值和
		private int noiseMax=800;//黄品青最大RGB值和
		private Bitmap convertToBlack(Bitmap bip) {// 像素处理背景变为白色，红、绿、蓝、黄、品、青、黑色，白色不变
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
					if (rgb<blackMax) {//黑色
						pl[offset + x] = pixel;
						colorNum[6]++;//黑色
					} else if (rgb<RGBMax) {// 红绿蓝
						pl[offset + x] = pixel;
						if(r>g&&r>b)
							colorNum[0]++;//红色
						else if(g>b)
							colorNum[1]++;//绿色
						else
							colorNum[2]++;//蓝色
							
					} else if (rgb<noiseMax && rgb>RGBMax) {//黄、品和青
						pl[offset + x] = pixel;
						if(b<r&&b<g)
							colorNum[3]++;//黄色
						else if(g<r&&g<b)
						colorNum[4]++;//品色
						else
							colorNum[5]++;//青色
						
					} else {
						pl[offset + x] = 0xffffffff;// 白色
					}
				}
			}
			
			Bitmap result = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);//把颜色值重新赋给新建的图片 图片的宽高为以前图片的值
			result.setPixels(pl, 0, width, 0, 0, width, height);
			return result;
		}
		public int[] getMaxIndex(int[] arr){
			if(arr==null||arr.length==0){
			return null;//如果数组为空 或者是长度为0 就返回null
			}
			int maxIndex=0;//假设第一个元素为最大值 那么下标设为0
			int[] arrnew=new int[2];//设置一个 长度为2的数组 用作记录 规定第一个元素存储最大值 第二个元素存储下标
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
				result = "红色";
			}else if (a == 1) {
				result = "绿色";
			}else if (a == 2) {
				result = "蓝色";
			}else if (a == 3) {
				result = "黄色";
			}else if (a == 4) {
				result = "品色";
			}else if (a == 5) {
				result = "青色";
			}else if (a == 6) {
				result = "黑色";
			}
            else {
				result = "白色";
			}
            return result;
		}
		
		
}
