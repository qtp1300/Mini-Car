package net.wxdxh_quantest.util;

import java.text.DecimalFormat;

public class NumUtil {
	public int[] first_int;// �õ���һ����ά��������
	public int[] second_int;// �õ��ڶ�����ά������

	public void numUtil(String codeName, int index) {// �洢���ݵĶ�ά�����ƣ���ά�����
		String content = "0x11,0x12,0x13,0x14,0x21,0x22,0x23,0x24";
		String numContent = "";

		// ������д洢�ĸ�ʽΪ��content = "0x11,0x12,0x13,0x14,0x21,0x22,0x23,0x24";
		String[] mbyte = content.split(",");
		for (int i = 0; i < mbyte.length; i++) {
			numContent += mbyte[i].split("x")[1];
		}
		if (index == 1)
			first_int = hexStringToBytes(numContent);
		else
			second_int = hexStringToBytes(numContent);
		for (int i = 0; i < first_int.length; i++) {
		}
		// Log.e("fffffffffffffffff",re );
	}

	// ��ʮ�����Ƶ�string�еõ�int��������
	private int[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();// ���ַ����е�Сд��ĸת��Ϊ��д��ĸ
		int length = hexString.length() / 2;// �ַ��������ݸ���
		char[] hexChars = hexString.toCharArray();
		int[] d = new int[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	// char�ַ�ת��Ϊintֵ
	private int charToByte(char c) {
		return "0123456789ABCDEF".indexOf(c);
	}

	// intת��ΪString
	public String intToString(int num) {
		return Integer.toString(num);

	}

	// Stringת��Ϊint
	public int stringToInt(String str) {
		return Integer.parseInt(str);

	}

	// ��������ȡ��
	public long mathRounded(double num) {
		return Math.round(num);
	}

	// ��ȡ��
	public long mathCeil(double num) {
		return (long) Math.ceil(num);
	}

	// ��ȡ��
	public long mathFloor(double num) {
		return (long) Math.floor(num);
	}

	/**
	 * ����������ָ��С�����λ����������
	 * 
	 * @param sourceData
	 *            �����Ҫ��ȡ��Ԫ����
	 * @param str
	 *            ȡ��ĸ�ʽ����Ҫ�õ�"#.0"�ĸ�ʽ����ΪС�����1λ��"#.00"ΪС�����2λ���Դ����ƣ�
	 * @return ��ȡ��� ����
	 */
	public static double getDouble(double sourceData, String sf) {
		DecimalFormat df = new DecimalFormat(sf);
		String str = df.format(sourceData);
		return Double.parseDouble(str);
	}

	/**
	 * ����������ָ��С����� a λ����������
	 * 
	 * @param sourceData
	 *            Ҫȡ���ԭ����
	 * @param a
	 *            С���� ��� λ�����磺10��С�����1λ��100��С���ݺ�2λ�Դ����ƣ�
	 * @return ��ȡ��� ����
	 */
	public static float getFloatRound(double sourceData, int a) {
		int i = (int) Math.round(sourceData * a); // С����� a λǰ�ƣ�����������
		float f2 = (float) (i / (float) a); // ��ԭС����� a λ
		return f2;
	}

	/**
	 * С�� ��������
	 * 
	 * @param val
	 * @param precision
	 * @return
	 */

	public static Double roundDouble(double val, int precision) {
		Double ret = null;
		try {
			double factor = Math.pow(10, precision);
			ret = Math.floor(val * factor + 0.5) / factor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	// ʮ������ת������
	public String HToB(String a) {
		String b = Integer.toBinaryString(Integer.valueOf(toD(a, 16)));
		return b;
	}

	// ������תʮ������
	public String BToH(String a) {
		// ��������תΪʮ�����ٴ�ʮ����תΪʮ������
		String b = Integer.toHexString(Integer.valueOf(toD(a, 2)));
		return b;
	}

	// ���������תΪʮ������
	public String toD(String a, int b) {
		int r = 0;
		for (int i = 0; i < a.length(); i++) {
			r = (int) (r + formatting(a.substring(i, i + 1))
					* Math.pow(b, a.length() - i - 1));
		}
		return String.valueOf(r);
	}

	// ��ʮ�������е���ĸתΪ��Ӧ������
	public int formatting(String a) {
		int i = 0;
		for (int u = 0; u < 10; u++) {
			if (a.equals(String.valueOf(u))) {
				i = u;
			}
		}
		if (a.equals("a")) {
			i = 10;
		}
		if (a.equals("b")) {
			i = 11;
		}
		if (a.equals("c")) {
			i = 12;
		}
		if (a.equals("d")) {
			i = 13;
		}
		if (a.equals("e")) {
			i = 14;
		}
		if (a.equals("f")) {
			i = 15;
		}
		return i;
	}
	
	

	// ʮ����ת��Ϊ�˽���
	public void to8(int i) {
		String str = Integer.toOctalString(i);
		System.out.println(str);
	}
	
	

	// ��ʮ�����е�����תΪʮ�����ƶ�Ӧ����ĸ
	public String formattingH(int a) {
		String i = String.valueOf(a);
		switch (a) {
		case 10:
			i = "a";
			break;
		case 11:
			i = "b";
			break;
		case 12:
			i = "c";
			break;
		case 13:
			i = "d";
			break;
		case 14:
			i = "e";
			break;
		case 15:
			i = "f";
			break;
		}
		return i;
	}

	// //////////////////////////////////////
	
	
//����ַ������������int����
	public void numUtil1(String codeName, int index) {      
		String content = "11,12,13,14,21,22,23,24";
		// try {
		// content = new FileService().read(codeName + ".txt");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		String[] contentString = content.split(",");
		int[] result_int = new int[contentString.length];
		for (int i = 0; i < contentString.length; i++) {
			// Log.e("fffffffffffffffff",Integer.parseInt(contentString[i])+""
			// );
			result_int[i] = Integer.parseInt(contentString[i]);
		}
		if (index == 1)
			first_int = result_int;
		else
			second_int = result_int;
	}

}
