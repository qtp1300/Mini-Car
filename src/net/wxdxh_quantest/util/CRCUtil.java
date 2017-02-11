package net.wxdxh_quantest.util;

import java.io.IOException;

import net.wxdxh_quantest.client.Client;
import net.wxdxh_quantest.global.Global;
import net.wxdxh_quantest.saveUtil.FileService;

public class CRCUtil {
		// 0x67,0x34,0x78,0xA2,0xFD,0x27
		public void CRC16() {
			String content = "控制码为：0x13,0x14,0x05,0x20";
			String opens = "";
			short[] openbyte;
			try {
				String src= new FileService().read( Global.CRCNAME);
				if(src.length()>5){
					content=src;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] names = content.split("：");
			String[] mbyte1 = names[1].split(",");
			for (int i = 0; i < mbyte1.length; i++) {
				opens += mbyte1[i].split("x")[1];
			}
			openbyte = hexStringToBytes(opens);
			for (int i = 0; i < 4; i++) {
				Client.openbyte[i] = (byte) openbyte[i];
			}
			crc16Check(openbyte);
			Client.openbyte[4] = CRCL;
			Client.openbyte[5] = CRCH;
		}
		//从string中得到short数据数组
		private short[] hexStringToBytes(String hexString) {
			if (hexString == null || hexString.equals("")) {
				return null;
			}
			hexString = hexString.toUpperCase();
			int length = hexString.length() / 2;
			char[] hexChars = hexString.toCharArray();
			short[] d = new short[length];
			for (int i = 0; i < length; i++) {
				int pos = i * 2;
				d[i] = (short) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
			}
			return d;
		}
		//char字符转换为short值
		private short charToByte(char c) {
			return (short) "0123456789ABCDEF".indexOf(c);
		}
		private int CRC_16_POLYNOMIALS = 0xA001;
		//高八位和低八位
		private byte CRCH;
		private byte CRCL;
		//crc16校验方法
		private void crc16Check(short[] mbyte) {
			int CRC = 0xffff;
			for (int i = 0; i < mbyte.length; i++) {
				CRC = CRC ^ (mbyte[i]);
				for (int j = 0; j < 8; j++) {
					if ((CRC & 0x0001) == 1)
						CRC = (CRC >> 1) ^ CRC_16_POLYNOMIALS;
					else
						CRC = CRC >> 1;
				}
			}
			CRCL = (byte) CRC;
			CRCH = (byte) (CRC >> 8);
			try {
				new FileService().saveToSDCard(Global.CRCCONTENT, "算出的第一个校验码："
						+ toHexString(CRCL)
						+ "算出的第二个校验码："
						+ toHexString(CRCH));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		//byte变换为String的十六进制
		private String toHexString(byte b) {
			String hex = Integer.toHexString(b & 0xff);
			hex = hex.toUpperCase();
			if (hex.length() == 1)
				hex = '0' + hex;
			return "0x" + hex;
		}
}
