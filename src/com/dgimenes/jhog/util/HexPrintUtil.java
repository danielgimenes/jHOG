package com.dgimenes.jhog.util;

public class HexPrintUtil {
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String byteArrayToStringHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 3];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 3] = hexArray[v >>> 4];
			hexChars[j * 3 + 1] = hexArray[v & 0x0F];
			hexChars[j * 3 + 2] = ' ';
		}
		return new String(hexChars).trim();
	}
}
