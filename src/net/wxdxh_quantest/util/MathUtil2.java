package net.wxdxh_quantest.util;

import java.util.Arrays;

import net.wxdxh_quantest.client.Client;


import android.util.Log;

public class MathUtil2 {

	// Year、Month、Day为比赛当天对应的日期
	private int year=2016;
	private int month=5;
	private int day=8;
	private int f1Util(int MO2) {
		// f1=(Month-INT((M02-Year*10)/50.0))%4+1
		int result = (month-(int) Math.round((MO2-year*10)/50.0))%4+1;
		return result;
	}

	private String f2Util(int MO8,String MO6) {
		// f2=CONNECT(NUMtoSTR(HEX(12+(M08%2)*2)),NUMtoSTR(3+(STRtoNUM(DEL(RIGHT(M06,5),3)%2)*4))
		int ft = 12+MO8%2*2;
		String first=formattingH(ft);
		String src = MO6.substring(1, 4)+MO6.substring(5, 6);
		int second = (Integer.parseInt(src) % 2) * 4 + 3;
		String result = first + "" + second;
		return result;
	}

	private int f3Util(int MO2,int MO8,String MO6,String MO9) {
		// f3=INT(STRtoNUM( INSERT(
		// CONNECT(LEFT(NUMtoSTR(M02),1),RIGHT(↓(CONNECT(M06,M09)),5)),”.”,M08+1)))%5+1
		String src=MO6+MO9;
		char [] right=src.toCharArray();
		Arrays.sort(right);
		
		String first=(MO2+"").substring(0, 1);
		for(int i=0;i<5;i++){
			first+=right[4-i];
		}
		String src2="";
				src2+=first.substring(0, MO8+1)+":"+first.substring(MO8+1, first.length());
		Log.e("fffffffff", src2+"fff"+src2.split(":").length+"");
		String ft=src2.split(":")[0];
		String to=src2.split(":")[1].substring(0, 1);
		int result = (Integer.parseInt(ft)+Integer.parseInt(to)/5)%5+1;
		if(Client.carports[result]){
			result = result % 5 + 1; // f4=M13%5+1
		}
			return result;
	}
	// 将十进制中的数字转为十六进制对应的字母
		public String formattingH(int a) {
			String i = String.valueOf(a);
			switch (a) {
			case 10:
				i = "A";
				break;
			case 11:
				i = "B";
				break;
			case 12:
				i = "C";
				break;
			case 13:
				i = "D";
				break;
			case 14:
				i = "E";
				break;
			case 15:
				i = "F";
				break;
			}
			return i;
		}
}
