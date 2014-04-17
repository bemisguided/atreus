/**
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
package org.atreus.v0.impl;

import java.util.Calendar;
import java.util.Date;

import org.atreus.v0.AtreusConsistencyLevel;
import org.atreus.v0.AtreusTypeConverter;
import org.atreus.v0.impl.converters.BooleanTypeConverter;
import org.atreus.v0.impl.converters.ByteTypeConverter;
import org.atreus.v0.impl.converters.CalendarTypeConverter;
import org.atreus.v0.impl.converters.CharacterTypeConverter;
import org.atreus.v0.impl.converters.DateTypeConverter;
import org.atreus.v0.impl.converters.EnumTypeConverter;
import org.atreus.v0.impl.converters.IntegerTypeConverter;
import org.atreus.v0.impl.converters.LongTypeConverter;
import org.atreus.v0.impl.converters.ShortTypeConverter;
import org.atreus.v0.impl.converters.StringTypeConverter;
import org.atreus.impl.util.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

public class TypeConverterTests {

	private byte[] fromHex(String hex) {
		return ByteUtils.fromHex(hex);
	}

	@Test
	public void testBooleanConverter() throws Exception {
		AtreusTypeConverter converter = new BooleanTypeConverter();
		Boolean value = Boolean.TRUE;
		String hex = "01";
		byte[] bytes = converter.toBytes(value);
		Boolean result = (Boolean) converter.fromBytes(Boolean.class, bytes);

		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(Boolean.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testByteConverter() throws Exception {
		AtreusTypeConverter converter = new ByteTypeConverter();
		Byte value = Byte.valueOf((byte) 1);
		String hex = "01";
		byte[] bytes = converter.toBytes(value);
		Byte result = (Byte) converter.fromBytes(Byte.class, bytes);

		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(Byte.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testCalendarConverter() throws Exception {
		AtreusTypeConverter converter = new CalendarTypeConverter();
		Calendar value = Calendar.getInstance();
		value.setTimeInMillis(1000000);
		String hex = "aced0005771900000000000f4240000f416d65726963612f546f726f6e746f";
		byte[] bytes = converter.toBytes(value);
		Calendar result = (Calendar) converter.fromBytes(Calendar.class, bytes);

		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(Calendar.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testCharacterConverter() throws Exception {
		AtreusTypeConverter converter = new CharacterTypeConverter();
		Character value = Character.valueOf('c');
		String hex = "0063";
		byte[] bytes = converter.toBytes(value);
		Character result = (Character) converter.fromBytes(Character.class, bytes);

		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(Character.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testDateConverter() throws Exception {
		AtreusTypeConverter converter = new DateTypeConverter();
		Date value = new Date(1000000);
		String hex = "00000000000f4240";
		byte[] bytes = converter.toBytes(value);
		Date result = (Date) converter.fromBytes(Date.class, bytes);

		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(Date.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testEnumConverter() throws Exception {
		AtreusTypeConverter converter = new EnumTypeConverter();
		AtreusConsistencyLevel value = AtreusConsistencyLevel.EACH_QUORUM;
		String hex = ByteUtils.toHex(ByteUtils.toBytes("EACH_QUORUM"));
		byte[] bytes = converter.toBytes(value);
		AtreusConsistencyLevel result = (AtreusConsistencyLevel) converter.fromBytes(AtreusConsistencyLevel.class, bytes);
		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(AtreusConsistencyLevel.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testIntegerConverter() throws Exception {
		AtreusTypeConverter converter = new IntegerTypeConverter();
		Integer value = Integer.valueOf(1234);
		String hex = "000004d2";
		byte[] bytes = converter.toBytes(value);
		Integer result = (Integer) converter.fromBytes(Integer.class, bytes);
		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(Integer.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testLongConverter() throws Exception {
		AtreusTypeConverter converter = new LongTypeConverter();
		Long value = Long.valueOf((long) 341312312);
		String hex = "0000000014580338";
		byte[] bytes = converter.toBytes(value);
		Long result = (Long) converter.fromBytes(Long.class, bytes);
		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(Long.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testShortConverter() throws Exception {
		AtreusTypeConverter converter = new ShortTypeConverter();
		Short value = Short.valueOf((short) 12312);
		String hex = "3018";
		byte[] bytes = converter.toBytes(value);
		Short result = (Short) converter.fromBytes(Short.class, bytes);
		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(Short.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testStringConverter() throws Exception {
		AtreusTypeConverter converter = new StringTypeConverter();
		String value = "test";
		String hex = "74657374";
		byte[] bytes = converter.toBytes(value);
		String result = (String) converter.fromBytes(String.class, bytes);
		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(String.class));
		Assert.assertFalse("Expect support false", converter.isSupported(Short.class));
	}

}
