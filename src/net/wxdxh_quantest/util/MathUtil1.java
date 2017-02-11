package net.wxdxh_quantest.util;

import java.util.Arrays;

import net.wxdxh_quantest.MainActivity;
import net.wxdxh_quantest.client.Client;


public class MathUtil1 {
	// f1=(M02/100)%4+1
	public int  f1Util(int MO2) {		
		int result=(MO2/100)%4+1;
		return result;
	}
	private int f1Util1(int MO2) {// *
		// F1=((short)(1+(int)M02/100*702))%4+1
		int result = (1 + (int) MO2 / 100 * 702) % 4 + 1;
		return result;
	}
	public String f2Util(String MO6) {
		// f2=CONNECT(SUBSTRING(RIGHT(M06,2),0,0),
		// NUMtoSTR(((SUBSTRING(RIGHT(M06,6),0,0)+ RIGHT(M06,1))%2)*4+3))
		String right_two=MO6.substring(4,5);
		int right_six=MO6.substring(0,1).toCharArray()[0];
		int right_one=Integer.parseInt(MO6.substring(5, 6));
		String result=right_two+((right_six+right_one)%2*4+3);
		return result;
	}
	private String f2Util1(String MO6) {// *
		// f2=CONNECT((char)(((MAX(RIGHT(M06,6))+MIN(RIGHT(M06,6)))%4)*2+67),
		// NUMtoSTR(((SUBSTRING(RIGHT(M06,6),0,0)+ RIGHT(M06,1))%2)*4+3))
		String src = MO6.substring(0, 6);
		char[] mo6 = src.toCharArray();
		Arrays.sort(mo6);
		char fisrt = (char) (((mo6[0] + mo6[5]) % 4) * 2 + 67);
		int second = ((src.toCharArray()[0] + src.toCharArray()[5]) % 2) * 4 + 3;
		String result = fisrt + "" + second;
		return result;
	}

	private String  f2Util2(String MO6) {// *
		// f2=CONNECT((char)(((MAX(RIGHT(M06,6))+MIN(RIGHT(M06,6)))%4)*2+67),
		// NUMtoSTR((INT (AVERAGE (RIGHT(M06,6)))%2)*4+3))
		String src = MO6.substring(0, 6);
		char[] mo6 = src.toCharArray();
		Arrays.sort(mo6);
		char fisrt = (char) (((mo6[0] + mo6[5]) % 4) * 2 + 67);
		int second =  Math.round((mo6[0] +mo6[1] + mo6[2] + mo6[3]) / 4) % 2*4 + 3;
		String result = fisrt + "" + second;
		return result;
	}
	public int f3Util(String MO6,int MO8) {
		// f3=(STRtoNUM(SUBSTRING(RIGHT(M06,5), 0, 2))*M08)%5+1
		String right_five=MO6.substring(1, 4);
		int result=Integer.parseInt(right_five)*MO8%5+1;
		if(Client.carports[result]){
			result=(result+1)%5+1;// f4=(M13+1)%5+1
		}
		return result;
	}
	private int f3Util1(String MO6,int MO8) {//*
		// f3=( INT (AVERAGE (RIGHT(M06,6)))*M08)%5+1
		char[] mo6 = MO6.substring(0, 6).toCharArray();
		int result =  Math.round((mo6[0] +mo6[1] + mo6[2] + mo6[3]+ + mo6[4] + mo6[5]) / 6) *MO8%5 + 1;
		if(Client.carports[result]){
			result=(result+1)%5+1;// f4=(M13+1)%5+1
		}
		return result;
	}

	private int f3Util2(String MO6,String MO9,int MO2,int MO8) {//*
		// f3= INT( AVERAGE (LEFT(NUMtoSTR(M02),1),
							// SUBSTRING(RIGHT(M06,5), 0, 0),M08,
							// RIGHT(M09,1)))%5+1
		int[] An = new int[4];
		An[0] = Integer.parseInt((MO2+"").substring(0, 1));
		An[1] = Integer.parseInt(MO6.substring(1, 2));
		An[2] = MO8;
		String src1 = MO9.substring(1, 2);
		int mo9 = Integer.parseInt(src1);
		An[3] = mo9;
		int result = Math.round((An[0] + An[1] + An[2] + An[3]) / 4) % 5 + 1;
		if(Client.carports[result]){
			result=(result+1)%5+1;// f4=(M13+1)%5+1
		}
		return result;
	}
}
