/**
 * The MIT License
 *
 * Copyright (c) 2014 Martin Crawford and contributors.
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
package org.atreus.core.impl.util;

import org.atreus.impl.util.ByteUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ByteUtilsTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ByteUtilsTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testToFromBoolean() throws Exception {
    boolean value = Boolean.TRUE;
    String hex = "01";
    byte[] bytes = ByteUtils.toBytes(value);
    boolean result = ByteUtils.toBoolean(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testToFromBooleanArray() throws Exception {
    boolean[] value = new boolean[]{Boolean.TRUE, Boolean.FALSE};
    String hex = "0000000280";
    byte[] bytes = ByteUtils.toBytes(value);
    boolean[] result = ByteUtils.toBooleanArray(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertTrue(Arrays.equals(value, result));
  }

  @Test
  public void testToFromByte() throws Exception {
    Byte value = (byte) 1;
    String hex = "01";
    byte[] bytes = ByteUtils.toBytes(value);
    Byte result = ByteUtils.toByte(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testToFromByteArray() throws Exception {
    byte[] value = new byte[]{1, 15};
    String hex = "010f";
    byte[] bytes = ByteUtils.toBytes(value);
    byte[] result = ByteUtils.toBytes(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertTrue(Arrays.equals(value, result));
  }

  @Test
  public void testToFromChar() throws Exception {
    Character value = 'c';
    String hex = "0063";
    byte[] bytes = ByteUtils.toBytes(value);
    Character result = ByteUtils.toChar(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testToFromCharArray() throws Exception {
    char[] value = new char[]{'c', 'c'};
    String hex = "00630063";
    byte[] bytes = ByteUtils.toBytes(value);
    char[] result = ByteUtils.toCharArray(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertTrue(Arrays.equals(value, result));
  }

  @Test
  public void testToFromInteger() throws Exception {
    Integer value = 1234;
    String hex = "000004d2";
    byte[] bytes = ByteUtils.toBytes(value);
    Integer result = ByteUtils.toInt(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testToFromIntegerArray() throws Exception {
    int[] value = new int[]{1234, 1234};
    String hex = "000004d2000004d2";
    byte[] bytes = ByteUtils.toBytes(value);
    int[] result = ByteUtils.toIntArray(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertTrue(Arrays.equals(value, result));
  }

  @Test
  public void testToFromLong() throws Exception {
    Long value = (long) 341312312;
    String hex = "0000000014580338";
    byte[] bytes = ByteUtils.toBytes(value);
    Long result = ByteUtils.toLong(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testToFromLongArray() throws Exception {
    long[] value = new long[]{341312312, 341312312};
    String hex = "00000000145803380000000014580338";
    byte[] bytes = ByteUtils.toBytes(value);
    long[] result = ByteUtils.toLongArray(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertTrue(Arrays.equals(value, result));
  }

  @Test
  public void testToFromShort() throws Exception {
    Short value = 12312;
    String hex = "3018";
    byte[] bytes = ByteUtils.toBytes(value);
    Short result = ByteUtils.toShort(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testToFromShortArray() throws Exception {
    short[] value = new short[]{12312, 12312};
    String hex = "30183018";
    byte[] bytes = ByteUtils.toBytes(value);
    short[] result = ByteUtils.toShortArray(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertTrue(Arrays.equals(value, result));
  }

  @Test
  public void testToFromString() throws Exception {
    String value = "test";
    String hex = "74657374";
    byte[] bytes = ByteUtils.toBytes(value);
    String result = ByteUtils.toString(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testToFromStringArray() throws Exception {
    String[] value = new String[]{"test", "test"};
    String hex = "0000000200000004000000047465737474657374";
    byte[] bytes = ByteUtils.toBytes(value);
    String[] result = ByteUtils.toStringArray(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertTrue(Arrays.equals(value, result));
  }

  @Test
  public void testToFromFloat() throws Exception {
    Float value = 0.6f;
    String hex = "3f19999a";
    byte[] bytes = ByteUtils.toBytes(value);
    Float result = ByteUtils.toFloat(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testToFromFloatArray() throws Exception {
    float[] value = new float[]{0.6f, 0.6f};
    String hex = "3f19999a3f19999a";
    byte[] bytes = ByteUtils.toBytes(value);
    float[] result = ByteUtils.toFloatAarray(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertTrue(Arrays.equals(value, result));
  }

  @Test
  public void testToFromDouble() throws Exception {
    Double value = 0.6;
    String hex = "3fe3333333333333";
    byte[] bytes = ByteUtils.toBytes(value);
    Double result = ByteUtils.toDouble(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertEquals(value, result);
  }

  @Test
  public void testToFromDoubleArray() throws Exception {
    double[] value = new double[]{0.6, 0.6};
    String hex = "3fe33333333333333fe3333333333333";
    byte[] bytes = ByteUtils.toBytes(value);
    double[] result = ByteUtils.toDoubleArray(bytes);

    Assert.assertArrayEquals(fromHex(hex), bytes);
    Assert.assertTrue(Arrays.equals(value, result));
  }

  @Test
  public void testToHex() throws Exception {
    Assert.assertEquals("417472657573", toHex("Atreus".getBytes()));
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private byte[] fromHex(String hex) {
    return ByteUtils.fromHex(hex);
  }

  private String toHex(byte[] bytes) {
    return ByteUtils.toHex(bytes);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}
