package net.wxdxh_quantest.util;

import java.util.Arrays;

public class MathUtil {
	public int[] small_to_big(int[] numInt) {// 从小到达排序
		Arrays.sort(numInt);
		return numInt;
	}
	public int bigNum(int[] numInt) {// 最大值
		Arrays.sort(numInt);
		return numInt[numInt.length - 1];
	}

	public int smallNum(int[] numInt) {// 最小值
		Arrays.sort(numInt);
		return numInt[0];
	}

	public int midNum(int[] numInt) {// 中间值
		Arrays.sort(numInt);
		return numInt[(numInt.length + 1) / 2 - 1];
	}

	public int indexNum(int[] numInt, int index) {// 查找第i个数
		return numInt[index - 1];
	}

	public int totalNum(int[] numInt) {// 求和
		int total = 0;
		for (int i = 0; i < numInt.length; i++) {
			total += numInt[i];
		}
		return total;
	}

	public int[] chaiNum(int numInt) {// 拆分数据各位
		int[] chainum = new int[10];
		for (int i = 0; i < 100; i++) {
			chainum[i] = numInt % 10;// 余数,从各位数开始得到
			numInt = numInt / 10;
			if (numInt == 0)
				break;
		}
		// Math.abs(i);//求绝对值
		return chainum;
	}
	
	public int[] chuanNum(int[] firstInt, int[] secondInt) {//两个int数组叠加为一个int数组方法
		int[] chanNum = new int[firstInt.length + secondInt.length];
		for (int i = 0; i < chanNum.length; i++) {
			if (i < firstInt.length)
				chanNum[i] = firstInt[i];
			else
				chanNum[i] = secondInt[i - firstInt.length];
		}
		return chanNum;
	}
	public int maxIndex(int []chuanNum){//最大值在数组中的位置
		int [] oldNum=chuanNum;
		int index=0;
		Arrays.sort(chuanNum);
		int maxNum=chuanNum[chuanNum.length-1];
		for(int i=0;i<chuanNum.length;i++){
			if(maxNum==oldNum[i]){
				index=i;
				break;
			}	
		}
		return index;
	}
}
