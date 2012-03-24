package org.atreus.impl;

import java.util.Set;

import junit.framework.Assert;

import org.atreus.impl.converters.TypeConverterRegistry;
import org.junit.Before;
import org.junit.Test;

public class AtreusColumnMapTests {

	private AtreusColumnMapImpl map;
	private TypeConverterRegistry typeRegistry;

	@Before
	public void startup() throws Exception {
		typeRegistry = new TypeConverterRegistry();
		typeRegistry.addDefaultConverters();
		map = new AtreusColumnMapImpl(typeRegistry);
	}

	@Test
	public void testByteArrays() throws Exception {
		byte[] col1 = new byte[] { 1, 2, 3, 4 };
		byte[] col2 = new byte[] { 4, 3, 2, 1 };

		byte[] value1 = new byte[] { 1, 1, 1, 1 };
		String value2 = "value2";

		map.put(col1, value1);
		map.put(col2, value2);

		Set<byte[]> cols = map.columnNameSet();
		Assert.assertTrue("col1 exists in set", cols.contains(col1));

		Assert.assertTrue("col1 existsColumn", map.existsColumn(col1));
		Assert.assertTrue("col2 existsColumn", map.existsColumn(col2));
		Assert.assertFalse("non-existent existsColumn", map.existsColumn(new byte[] { 2, 2, 2, 2 }));

		Assert.assertTrue("value1 existsValue", map.existsValue(value1));
		Assert.assertTrue("value2 existsValue", map.existsValue(value2));

		Assert.assertEquals(value1, map.getAsBytes(col1));
		Assert.assertEquals(value2, map.get(col2, value2.getClass()));

		Assert.assertNull("non-existent getAsBytes", map.getAsBytes("no-exist"));
	}

	@Test
	public void testImmutable() throws Exception {
		byte[] col1 = new byte[] { 1, 2, 3, 4 };
		String col2 = "col1";

		byte[] value1 = new byte[] { 1, 1, 1, 1 };
		String value2 = "value2";

		map.setImmutable(true);

		try {
			map.put(col1, value1);
			Assert.fail("Immutable map should fail on put");
		} catch (Exception e) {

		}
		try {
			map.put(col1, value2);
			Assert.fail("Immutable map should fail on put");
		} catch (Exception e) {

		}
		try {
			map.put(col2, value1);
			Assert.fail("Immutable map should fail on put");
		} catch (Exception e) {

		}
		try {
			map.put(col2, value2);
			Assert.fail("Immutable map should fail on put");
		} catch (Exception e) {

		}
	}

	@Test
	public void testObjectsAndPrimitives() throws Exception {
		String col1 = "col1";
		boolean col2 = false;
		int col3 = 123;
		byte[] col4 = new byte[] { 1, 2, 3, 4 };

		String value1 = "value1";
		int value2 = 1234;
		boolean value3 = true;
		String value4 = "value4";

		map.put(col1, value1);
		map.put(col2, value2);
		map.put(col3, value3);
		map.put(col4, value4);

		Set<String> cols = map.columnNameSet(String.class);
		Assert.assertTrue("col1 exists in set", cols.contains(col1));

		Assert.assertEquals(4, cols.size());
		Assert.assertEquals(4, map.size());

		Assert.assertTrue("col1 existsColumn", map.existsColumn(col1));
		Assert.assertTrue("col2 existsColumn", map.existsColumn(col2));
		Assert.assertTrue("col3 existsColumn", map.existsColumn(col3));
		Assert.assertTrue("col4 existsColumn", map.existsColumn(col4));
		Assert.assertFalse("non-existent existsColumn", map.existsColumn("no-exist"));

		Assert.assertTrue("value1 existsValue", map.existsValue(value1));
		Assert.assertTrue("value2 existsValue", map.existsValue(value2));
		Assert.assertTrue("value3 existsValue", map.existsValue(value3));
		Assert.assertTrue("value4 existsValue", map.existsValue(value4));

		Assert.assertEquals(value1, map.get(col1, value1.getClass()));
		Assert.assertEquals(Integer.valueOf(value2), map.get(col2, Integer.class));
		Assert.assertEquals(Boolean.valueOf(value3), map.get(col3, Boolean.class));
		Assert.assertEquals(value4, map.get(col4, value4.getClass()));

		Assert.assertNull("non-existent getAsBytes", map.getAsBytes("no-exist"));
	}
}
