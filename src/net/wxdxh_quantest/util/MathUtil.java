package net.wxdxh_quantest.util;

import java.util.Arrays;

public class MathUtil {
	public int[] small_to_big(int[] numInt) {// ��С��������
		Arrays.sort(numInt);
		return numInt;
	}
	public int bigNum(int[] numInt) {// ���ֵ
		Arrays.sort(numInt);
		return numInt[numInt.length - 1];
	}

	public int smallNum(int[] numInt) {// ��Сֵ
		Arrays.sort(numInt);
		return numInt[0];
	}

	public int midNum(int[] numInt) {// �м�ֵ
		Arrays.sort(numInt);
		return numInt[(numInt.length + 1) / 2 - 1];
	}

	public int indexNum(int[] numInt, int index) {// ���ҵ�i����
		return numInt[index - 1];
	}

	public int totalNum(int[] numInt) {// ���
		int total = 0;
		for (int i = 0; i < numInt.length; i++) {
			total += numInt[i];
		}
		return total;
	}

	public int[] chaiNum(int numInt) {// ������ݸ�λ
		int[] chainum = new int[10];
		for (int i = 0; i < 100; i++) {
			chainum[i] = numInt % 10;// ����,�Ӹ�λ����ʼ�õ�
			numInt = numInt / 10;
			if (numInt == 0)
				break;
		}
		// Math.abs(i);//�����ֵ
		return chainum;
	}
	
	public int[] chuanNum(int[] firstInt, int[] secondInt) {//����int�������Ϊһ��int���鷽��
		int[] chanNum = new int[firstInt.length + secondInt.length];
		for (int i = 0; i < chanNum.length; i++) {
			if (i < firstInt.length)
				chanNum[i] = firstInt[i];
			else
				chanNum[i] = secondInt[i - firstInt.length];
		}
		return chanNum;
	}
	public int maxIndex(int []chuanNum){//���ֵ�������е�λ��
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
