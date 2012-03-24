package org.atreus.impl;

import java.util.Calendar;
import java.util.Date;

import org.atreus.AtreusTypeConverter;
import org.atreus.impl.converters.BooleanTypeConverter;
import org.atreus.impl.converters.ByteTypeConverter;
import org.atreus.impl.converters.CalendarTypeConverter;
import org.atreus.impl.converters.CharacterTypeConverter;
import org.atreus.impl.converters.DateTypeConverter;
import org.atreus.impl.converters.IntegerTypeConverter;
import org.atreus.impl.converters.LongTypeConverter;
import org.atreus.impl.converters.ShortTypeConverter;
import org.atreus.impl.converters.StringTypeConverter;
import org.atreus.impl.utils.ByteUtils;
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
		Boolean result = (Boolean) converter.fromBytes(bytes);

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
		Byte result = (Byte) converter.fromBytes(bytes);

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
		Calendar result = (Calendar) converter.fromBytes(bytes);

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
		Character result = (Character) converter.fromBytes(bytes);

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
		Date result = (Date) converter.fromBytes(bytes);

		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(Date.class));
		Assert.assertFalse("Expect support false", converter.isSupported(String.class));
	}

	@Test
	public void testIntegerConverter() throws Exception {
		AtreusTypeConverter converter = new IntegerTypeConverter();
		Integer value = Integer.valueOf(1234);
		String hex = "000004d2";
		byte[] bytes = converter.toBytes(value);
		Integer result = (Integer) converter.fromBytes(bytes);
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
		Long result = (Long) converter.fromBytes(bytes);
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
		Short result = (Short) converter.fromBytes(bytes);
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
		String result = (String) converter.fromBytes(bytes);
		Assert.assertArrayEquals(fromHex(hex), bytes);
		Assert.assertEquals(value, result);

		Assert.assertTrue("Expect support true", converter.isSupported(String.class));
		Assert.assertFalse("Expect support false", converter.isSupported(Short.class));
	}

	private String toHex(byte[] bytes) {
		return ByteUtils.toHex(bytes);
	}
}
