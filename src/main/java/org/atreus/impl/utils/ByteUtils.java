/*
 * The MIT License
 *
 * Copyright (c) 2012 Martin Crawford and contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.atreus.impl.utils;

import java.nio.charset.Charset;

public class ByteUtils {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	public static final byte[] toBytes(String value) {
		if (value == null) {
			return null;
		}
		return value.getBytes(UTF_8);
	}

	public static final String toString(byte[] value) {
		return new String(value, UTF_8);
	}

	public static byte[] toBytes(byte value) {
		return new byte[] { value };
	}

	public static byte[] toBytes(byte[] value) {
		return value;
	}

	public static byte[] toBytes(short value) {
		return new byte[] { (byte) ((value >> 8) & 0xff), (byte) ((value >> 0) & 0xff), };
	}

	public static byte[] toBytes(short[] value) {
		if (value == null) {
			return null;
		}
		byte[] byts = new byte[value.length * 2];
		for (int i = 0; i < value.length; i++) {
			System.arraycopy(toBytes(value[i]), 0, byts, i * 2, 2);
		}
		return byts;
	}

	public static byte[] toBytes(char value) {
		return new byte[] { (byte) ((value >> 8) & 0xff), (byte) ((value >> 0) & 0xff), };
	}

	public static byte[] toBytes(char[] value) {
		if (value == null) {
			return null;
		}
		byte[] byts = new byte[value.length * 2];
		for (int i = 0; i < value.length; i++) {
			System.arraycopy(toBytes(value[i]), 0, byts, i * 2, 2);
		}
		return byts;
	}

	public static byte[] toBytes(int value) {
		return new byte[] { (byte) ((value >> 24) & 0xff), (byte) ((value >> 16) & 0xff), (byte) ((value >> 8) & 0xff), (byte) ((value >> 0) & 0xff), };
	}

	public static byte[] toBytes(int[] value) {
		if (value == null) {
			return null;
		}
		byte[] byts = new byte[value.length * 4];
		for (int i = 0; i < value.length; i++) {
			System.arraycopy(toBytes(value[i]), 0, byts, i * 4, 4);
		}
		return byts;
	}

	public static byte[] toBytes(long value) {
		return new byte[] { (byte) ((value >> 56) & 0xff), (byte) ((value >> 48) & 0xff), (byte) ((value >> 40) & 0xff), (byte) ((value >> 32) & 0xff), (byte) ((value >> 24) & 0xff),
				(byte) ((value >> 16) & 0xff), (byte) ((value >> 8) & 0xff), (byte) ((value >> 0) & 0xff), };
	}

	public static byte[] toBytes(long[] value) {
		if (value == null) {
			return null;
		}
		byte[] byts = new byte[value.length * 8];
		for (int i = 0; i < value.length; i++) {
			System.arraycopy(toBytes(value[i]), 0, byts, i * 8, 8);
		}
		return byts;
	}

	public static byte[] toBytes(float value) {
		return toBytes(Float.floatToRawIntBits(value));
	}

	public static byte[] toBytes(float[] value) {
		if (value == null) {
			return null;
		}
		byte[] byts = new byte[value.length * 4];
		for (int i = 0; i < value.length; i++) {
			System.arraycopy(toBytes(value[i]), 0, byts, i * 4, 4);
		}
		return byts;
	}

	public static byte[] toBytes(double value) {
		return toBytes(Double.doubleToRawLongBits(value));
	}

	public static byte[] toBytes(double[] value) {
		if (value == null) {
			return null;
		}
		byte[] byts = new byte[value.length * 8];
		for (int i = 0; i < value.length; i++) {
			System.arraycopy(toBytes(value[i]), 0, byts, i * 8, 8);
		}
		return byts;
	}

	public static byte[] toBytes(boolean value) {
		return new byte[] { (byte) (value ? 0x01 : 0x00) }; // bool -> {1 byte}
	}

	public static byte[] toBytes(boolean[] data) {
		if (data == null) {
			return null;
		}
		int len = data.length;
		byte[] lena = toBytes(len);
		byte[] byts = new byte[lena.length + (len / 8) + (len % 8 != 0 ? 1 : 0)];
		System.arraycopy(lena, 0, byts, 0, lena.length);
		for (int i = 0, j = lena.length, k = 7; i < data.length; i++) {
			byts[j] |= (data[i] ? 1 : 0) << k--;
			if (k < 0) {
				j++;
				k = 7;
			}
		}
		return byts;
	}

	public static byte[] toBytes(String[] value) {
		if (value == null) {
			return null;
		}
		int totalLength = 0;
		int bytesPos = 0;
		byte[] dLen = toBytes(value.length);
		totalLength += dLen.length;
		int[] sLens = new int[value.length];
		totalLength += (sLens.length * 4);
		byte[][] strs = new byte[value.length][];

		for (int i = 0; i < value.length; i++) {
			if (value[i] != null) {
				strs[i] = toBytes(value[i]);
				sLens[i] = strs[i].length;
				totalLength += strs[i].length;
			} else {
				sLens[i] = 0;
				strs[i] = new byte[0];
			}
		}
		byte[] bytes = new byte[totalLength];
		System.arraycopy(dLen, 0, bytes, 0, 4);
		byte[] bsLens = toBytes(sLens);
		System.arraycopy(bsLens, 0, bytes, 4, bsLens.length);
		bytesPos += 4 + bsLens.length;
		for (byte[] sba : strs) {
			System.arraycopy(sba, 0, bytes, bytesPos, sba.length);
			bytesPos += sba.length;
		}
		return bytes;
	}

	public static byte toByte(byte[] bytes) {
		return (bytes == null || bytes.length == 0) ? 0x0 : bytes[0];
	}

	public static short toShort(byte[] bytes) {
		if (bytes == null || bytes.length != 2) {
			return 0x0;
		}
		return (short) ((0xff & bytes[0]) << 8 | (0xff & bytes[1]) << 0);
	}

	public static short[] toShortArray(byte[] bytes) {
		if (bytes == null || bytes.length % 2 != 0) {
			return null;
		}
		short[] shts = new short[bytes.length / 2];
		for (int i = 0; i < shts.length; i++) {
			shts[i] = toShort(new byte[] { bytes[(i * 2)], bytes[(i * 2) + 1] });
		}
		return shts;
	}

	public static char toChar(byte[] bytes) {
		if (bytes == null || bytes.length != 2) {
			return 0x0;
		}
		return (char) ((0xff & bytes[0]) << 8 | (0xff & bytes[1]) << 0);
	}

	public static char[] toCharArray(byte[] bytes) {
		if (bytes == null || bytes.length % 2 != 0) {
			return null;
		}
		char[] chrs = new char[bytes.length / 2];
		for (int i = 0; i < chrs.length; i++) {
			chrs[i] = toChar(new byte[] { bytes[(i * 2)], bytes[(i * 2) + 1], });
		}
		return chrs;
	}

	public static int toInt(byte[] bytes) {
		if (bytes == null || bytes.length != 4) {
			return 0x0;
		}
		return (int) ((0xff & bytes[0]) << 24 | (0xff & bytes[1]) << 16 | (0xff & bytes[2]) << 8 | (0xff & bytes[3]) << 0);
	}

	public static int[] toIntArray(byte[] bytes) {
		if (bytes == null || bytes.length % 4 != 0) {
			return null;
		}
		int[] ints = new int[bytes.length / 4];
		for (int i = 0; i < ints.length; i++)
			ints[i] = toInt(new byte[] { bytes[(i * 4)], bytes[(i * 4) + 1], bytes[(i * 4) + 2], bytes[(i * 4) + 3], });
		return ints;
	}

	public static long toLong(byte[] bytes) {
		if (bytes == null || bytes.length != 8) {
			return 0x0;
		}
		return (long) ((long) (0xff & bytes[0]) << 56 | (long) (0xff & bytes[1]) << 48 | (long) (0xff & bytes[2]) << 40 | (long) (0xff & bytes[3]) << 32
				| (long) (0xff & bytes[4]) << 24 | (long) (0xff & bytes[5]) << 16 | (long) (0xff & bytes[6]) << 8 | (long) (0xff & bytes[7]) << 0);
	}

	public static long[] toLongArray(byte[] bytes) {
		if (bytes == null || bytes.length % 8 != 0) {
			return null;
		}
		long[] lngs = new long[bytes.length / 8];
		for (int i = 0; i < lngs.length; i++) {
			lngs[i] = toLong(new byte[] { bytes[(i * 8)], bytes[(i * 8) + 1], bytes[(i * 8) + 2], bytes[(i * 8) + 3], bytes[(i * 8) + 4], bytes[(i * 8) + 5], bytes[(i * 8) + 6],
					bytes[(i * 8) + 7], });
		}
		return lngs;
	}

	public static float toFloat(byte[] bytes) {
		if (bytes == null || bytes.length != 4) {
			return 0x0;
		}
		return Float.intBitsToFloat(toInt(bytes));
	}

	public static float[] toFloatAarray(byte[] bytes) {
		if (bytes == null || bytes.length % 4 != 0) {
			return null;
		}
		float[] flts = new float[bytes.length / 4];
		for (int i = 0; i < flts.length; i++) {
			flts[i] = toFloat(new byte[] { bytes[(i * 4)], bytes[(i * 4) + 1], bytes[(i * 4) + 2], bytes[(i * 4) + 3], });
		}
		return flts;
	}

	public static double toDouble(byte[] bytes) {
		if (bytes == null || bytes.length != 8) {
			return 0x0;
		}
		return Double.longBitsToDouble(toLong(bytes));
	}

	public static double[] toDoubleArray(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		if (bytes.length % 8 != 0) {
			return null;
		}
		double[] dbls = new double[bytes.length / 8];
		for (int i = 0; i < dbls.length; i++) {
			dbls[i] = toDouble(new byte[] { bytes[(i * 8)], bytes[(i * 8) + 1], bytes[(i * 8) + 2], bytes[(i * 8) + 3], bytes[(i * 8) + 4], bytes[(i * 8) + 5], bytes[(i * 8) + 6],
					bytes[(i * 8) + 7], });
		}
		return dbls;
	}

	public static boolean toBoolean(byte[] bytes) {
		return (bytes == null || bytes.length == 0) ? false : bytes[0] != 0x00;
	}

	public static boolean[] toBooleanArray(byte[] bytes) {
		if (bytes == null || bytes.length < 4) {
			return null;
		}
		int len = toInt(new byte[] { bytes[0], bytes[1], bytes[2], bytes[3] });
		boolean[] bools = new boolean[len];
		for (int i = 0, j = 4, k = 7; i < bools.length; i++) {
			bools[i] = ((bytes[j] >> k--) & 0x01) == 1;
			if (k < 0) {
				j++;
				k = 7;
			}
		}
		return bools;
	}

	public static String[] toStringArray(byte[] bytes) {
		if (bytes == null || bytes.length < 4) {
			return null;
		}
		byte[] bBuff = new byte[4];
		System.arraycopy(bytes, 0, bBuff, 0, 4);
		int saLen = toInt(bBuff);
		if (bytes.length < (4 + (saLen * 4))) {
			return null;
		}
		bBuff = new byte[saLen * 4];
		System.arraycopy(bytes, 4, bBuff, 0, bBuff.length);
		int[] sLens = toIntArray(bBuff);
		if (sLens == null) {
			return null;
		}
		String[] strs = new String[saLen];
		for (int i = 0, dataPos = 4 + (saLen * 4); i < saLen; i++) {
			if (sLens[i] > 0) {
				if (bytes.length >= (dataPos + sLens[i])) {
					bBuff = new byte[sLens[i]];
					System.arraycopy(bytes, dataPos, bBuff, 0, sLens[i]);
					dataPos += sLens[i];
					strs[i] = toString(bBuff);
				} else
					return null;
			}
		}
		return strs;
	}

}
