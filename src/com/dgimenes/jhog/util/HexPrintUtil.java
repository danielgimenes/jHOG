/*
 * The MIT License (MIT)
 * Copyright (c) 2014 Daniel Costa Gimenes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */
package com.dgimenes.jhog.util;

/**
 * Creates a string of hexadecimal representation of bytes from an array of
 * bytes, separated by white spaces
 * 
 * @author danielgimenes
 * @version 1.0
 */
public final class HexPrintUtil {
	private static final int FIRST_4BITS_MASK = 0x0F;
	private static final int ONE_BYTE_MASK = 0xFF;
	private static final char SEPARATOR = ' ';
	private static final char[] hexChars = "0123456789ABCDEF".toCharArray();

	/**
	 * Creates a string of hexadecimal representation of bytes from an array of
	 * bytes, separated by white spaces
	 * 
	 * @param bytes
	 *            array of bytes
	 * @return string representation
	 */
	public static String bytesToHexString(byte[] bytes) {
		if (bytes.length == 0) {
			return "";
		}
		char[] resultString = new char[bytes.length * 3];
		int byteValue;
		for (int i = 0; i < bytes.length; i++) {
			byteValue = bytes[i] & ONE_BYTE_MASK;
			resultString[i * 3] = hexChars[byteValue >>> 4]; // division by 16
			resultString[i * 3 + 1] = hexChars[byteValue & FIRST_4BITS_MASK];
			resultString[i * 3 + 2] = SEPARATOR;
		}
		return new String(resultString).trim();
	}
}
