package net.wxdxh_quantest.util;

import java.text.DecimalFormat;

public class NumUtil {
	public int[] first_int;// 得到第一个二维码中数据
	public int[] second_int;// 得到第二个二维码数据

	public void numUtil(String codeName, int index) {// 存储数据的二维码名称，二维码序号
		String content = "0x11,0x12,0x13,0x14,0x21,0x22,0x23,0x24";
		String numContent = "";

		// 如果其中存储的格式为：content = "0x11,0x12,0x13,0x14,0x21,0x22,0x23,0x24";
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

	// 从十六进制的string中得到int数据数组
	private int[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();// 把字符串中的小写字母转换为大写字母
		int length = hexString.length() / 2;// 字符串中数据个数
		char[] hexChars = hexString.toCharArray();
		int[] d = new int[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	// char字符转换为int值
	private int charToByte(char c) {
		return "0123456789ABCDEF".indexOf(c);
	}

	// int转换为String
	public String intToString(int num) {
		return Integer.toString(num);

	}

	// String转换为int
	public int stringToInt(String str) {
		return Integer.parseInt(str);

	}

	// 四舍五入取整
	public long mathRounded(double num) {
		return Math.round(num);
	}

	// 上取整
	public long mathCeil(double num) {
		return (long) Math.ceil(num);
	}

	// 下取整
	public long mathFloor(double num) {
		return (long) Math.floor(num);
	}

	/**
	 * 给参数返回指定小数点后几位的四舍五入
	 * 
	 * @param sourceData
	 *            传入的要舍取的元数据
	 * @param str
	 *            取舍的格式（主要用到"#.0"的格式，此为小数点后1位；"#.00"为小数点后2位，以此类推）
	 * @return 舍取后的 数据
	 */
	public static double getDouble(double sourceData, String sf) {
		DecimalFormat df = new DecimalFormat(sf);
		String str = df.format(sourceData);
		return Double.parseDouble(str);
	}

	/**
	 * 给参数返回指定小数点后 a 位的四舍五入
	 * 
	 * @param sourceData
	 *            要取舍的原数据
	 * @param a
	 *            小数点 后的 位数（如：10：小数点后1位；100：小数据后2位以此类推）
	 * @return 舍取后的 数据
	 */
	public static float getFloatRound(double sourceData, int a) {
		int i = (int) Math.round(sourceData * a); // 小数点后 a 位前移，并四舍五入
		float f2 = (float) (i / (float) a); // 还原小数点后 a 位
		return f2;
	}

	/**
	 * 小数 四舍五入
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

	// 十六进制转二进制
	public String HToB(String a) {
		String b = Integer.toBinaryString(Integer.valueOf(toD(a, 16)));
		return b;
	}

	// 二进制转十六进制
	public String BToH(String a) {
		// 将二进制转为十进制再从十进制转为十六进制
		String b = Integer.toHexString(Integer.valueOf(toD(a, 2)));
		return b;
	}

	// 任意进制数转为十进制数
	public String toD(String a, int b) {
		int r = 0;
		for (int i = 0; i < a.length(); i++) {
			r = (int) (r + formatting(a.substring(i, i + 1))
					* Math.pow(b, a.length() - i - 1));
		}
		return String.valueOf(r);
	}

	// 将十六进制中的字母转为对应的数字
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
	
	

	// 十进制转换为八进制
	public void to8(int i) {
		String str = Integer.toOctalString(i);
		System.out.println(str);
	}
	
	

	// 将十进制中的数字转为十六进制对应的字母
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
	
	
//拆分字符串并把它变成int数组
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
