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

package org.atreus.impl;

import java.util.Calendar;
import java.util.UUID;

import org.atreus.AtreusColumnMap;
import org.atreus.AtreusCommandException;
import org.atreus.AtreusConfiguration;
import org.atreus.AtreusSessionFactory;
import org.atreus.AtreusSessionFactoryBuilder;
import org.cassandraunit.AbstractCassandraUnit4TestCase;
import org.cassandraunit.dataset.DataSet;
import org.cassandraunit.dataset.xml.ClassPathXmlDataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AtreusSessionTests extends AbstractCassandraUnit4TestCase {

	private AtreusSessionFactory sessionFactory;

	private AtreusSessionImpl s;

	@Override
	public DataSet getDataSet() {
		return new ClassPathXmlDataSet("AtreusSessionTests.xml");
	}

	@Before
	public void setup() throws Exception {
		AtreusConfiguration config = new AtreusConfiguration("localhost", 9171, "AtreusSessionTests");
		sessionFactory = AtreusSessionFactoryBuilder.buildFactory(config);
		s = (AtreusSessionImpl) sessionFactory.openSession();
		s.setBatchWriting(false);
	}

	@After
	public void teardown() throws Exception {
		s.close();
		sessionFactory.disconnect();
	}

	@Test
	public void testColumnDeleteColumnNegativeMissingFamilyKey() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		try {
			// Attempt to delete a row without a column family and row key set
			s.deleteColumn("col1");
			Assert.fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {

		}

		try {
			// Attempt to write without a column family set
			s.setRowKey(rowKey);
			s.deleteColumn("col1");
			Assert.fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testColumnDeleteColumnNegativeNonExistent() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Attempt to delete a non-existent column
		s.setFamilyAndKey("ColumnTest1", rowKey);
		s.deleteColumn("col1");

		// Attempt to delete a non-existent sub column
		s.setFamilyAndKey("SuperColumnTest1", rowKey);
		s.deleteColumn("col1", "subcol1");
	}

	@Test
	public void testColumnDeleteRowNegativeMissingFamilyKey() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		try {
			// Attempt to delete a row without a column family and row key set
			s.deleteRow();
			Assert.fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {

		}

		try {
			// Attempt to write without a column family set
			s.setRowKey(rowKey);
			s.deleteRow();
			Assert.fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testColumnDeleteRowNegativeNonExistent() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Attempt to delete a non-existent row
		s.deleteRow("ColumnTest1", rowKey);
	}

	@Test
	public void testColumnWriteRead() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Data for ColFamily
		String col1 = "value1";
		int col2 = 1234;
		Calendar col3 = Calendar.getInstance();

		// Write data to ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		s.writeColumn("col1", col1);
		s.writeColumn("col2", col2);
		s.writeColumn("col3", col3);

		// Assert
		Assert.assertEquals(col1, s.readColumn("col1", col1.getClass()));
		Assert.assertEquals(Integer.valueOf(col2), s.readColumn("col2", Integer.class));
		Assert.assertEquals(col3, s.readColumn("col3", col3.getClass()));
	}

	@Test
	public void testColumnWriteReadNegativeColumnTypes() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();
		try {
			// Attempt to write a standard column to a super column
			s.setFamilyAndKey("SuperColumnTest1", rowKey);
			s.writeColumn("col1", "value1");
			Assert.fail("Expect Atreus Command Exception");
		} catch (AtreusCommandException e) {

		}

		try {
			// Attempt to write a super column to a standard column
			s.setFamilyAndKey("ColumnTest1", rowKey);
			s.writeSubColumn("col1", "subcol1", "value1");
			Assert.fail("Expect Atreus Command Exception");
		} catch (AtreusCommandException e) {

		}
	}

	@Test
	public void testColumnWriteReadNegativeMissingFamilyKey() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		try {
			// Attempt to write without a column family and row key set
			s.writeColumn("col1", "value1");
			Assert.fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {

		}

		try {
			// Attempt to write without a column family set
			s.setRowKey(rowKey);
			s.writeColumn("col1", "value1");
			Assert.fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void testColumnWriteReadTwoColFamilies() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Data for first ColFamily
		String fam1col1 = "value1";
		int fam1col2 = 1234;
		Calendar fam1col3 = Calendar.getInstance();

		// Write to first ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		s.writeColumn("emptyCol");
		s.writeColumn("col1", fam1col1);
		s.writeColumn("col2", fam1col2);
		s.writeColumn("col3", fam1col3);

		// Data for second ColFamily
		String fam2col1 = "value2";
		int fam2col2 = 4312;
		Calendar fam2col3 = Calendar.getInstance();
		fam2col3.add(Calendar.DAY_OF_MONTH, 1);

		// Write to second ColFamily
		s.setFamilyAndKey("ColumnTest2", rowKey);
		s.writeColumn("emptyCol");
		s.writeColumn("col1", fam2col1);
		s.writeColumn("col2", fam2col2);
		s.writeColumn("col3", fam2col3);

		// Assert

		// Assert First ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		Assert.assertEquals("", s.readColumn("emptyCol", String.class));
		Assert.assertEquals(fam1col1, s.readColumn("col1", fam1col1.getClass()));
		Assert.assertEquals(Integer.valueOf(fam1col2), s.readColumn("col2", Integer.class));
		Assert.assertEquals(fam1col3, s.readColumn("col3", fam1col3.getClass()));

		// Assert Second ColFamily
		s.setFamilyAndKey("ColumnTest2", rowKey);
		Assert.assertEquals("", s.readColumn("emptyCol", String.class));
		Assert.assertEquals(fam2col1, s.readColumn("col1", fam2col1.getClass()));
		Assert.assertEquals(Integer.valueOf(fam2col2), s.readColumn("col2", Integer.class));
		Assert.assertEquals(fam2col3, s.readColumn("col3", fam2col3.getClass()));

	}

	@Test
	public void testDeleteColumns() throws Exception {
		// Setup test
		String rowKey1 = UUID.randomUUID().toString();
		String rowKey2 = UUID.randomUUID().toString();

		s.setFamilyAndKey("ColumnTest1", rowKey1);
		s.writeColumn("col1", "val1");
		s.writeColumn("col2", "val2");

		s.setFamilyAndKey("SuperColumnTest1", rowKey2);
		s.writeSubColumn("col1", "subCol1", "val1");
		s.writeSubColumn("col2", "subCol2", "val2");

		s.setFamilyAndKey("ColumnTest1", rowKey1);
		s.deleteColumn("col1");

		s.setFamilyAndKey("SuperColumnTest1", rowKey2);
		s.deleteColumn("col1", "subCol1");

		s.setFamilyAndKey("ColumnTest1", rowKey1);
		Assert.assertNull("Row key " + rowKey1 + " null", s.readColumn("col1", String.class));
		Assert.assertNotNull("Row key " + rowKey1 + " not null", s.readColumn("col2", String.class));

		s.setFamilyAndKey("SuperColumnTest1", rowKey2);
		Assert.assertNull("Row key " + rowKey2 + " null", s.readColumn("col1", "subCol1", String.class));
		Assert.assertNotNull("Row key " + rowKey2 + " not null", s.readColumn("col2", "subCol2", String.class));
	}

	@Test
	public void testDeleteRows() throws Exception {
		// Setup test
		String rowKey1 = UUID.randomUUID().toString();
		String rowKey2 = UUID.randomUUID().toString();

		s.setFamilyAndKey("ColumnTest1", rowKey1);
		s.writeColumn("col1", "val1");
		s.writeColumn("col2", "val2");
		s.setFamilyAndKey("ColumnTest2", rowKey2);
		s.writeColumn("col1", "val1");
		s.writeColumn("col2", "val2");

		s.deleteRow();
		s.deleteRow("ColumnTest1", rowKey1);

		s.setFamilyAndKey("ColumnTest1", rowKey1);
		Assert.assertNull("Row key " + rowKey1 + " null", s.readColumn("col1", String.class));
		s.setFamilyAndKey("ColumnTest1", rowKey2);
		Assert.assertNull("Row key " + rowKey2 + " null", s.readColumn("col1", String.class));
	}

	@Test
	public void testExistsColumn() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Write data to ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		s.writeColumn("col1", "value1");
		s.writeColumn("col2");

		// Write data to SubColFamily
		s.setFamilyAndKey("SuperColumnTest1", rowKey);
		s.writeSubColumn("col1", "subcol1", "value1");
		s.writeSubColumn("col1", "subcol2");

		// Assert
		s.setFamilyAndKey("ColumnTest1", rowKey);
		Assert.assertTrue("col1 should exist", s.existsColumn("col1"));
		Assert.assertTrue("col2 should exist", s.existsColumn("col2"));
		Assert.assertFalse("col3 should not exist", s.existsColumn("col3"));

		s.setFamilyAndKey("SuperColumnTest1", rowKey);
		Assert.assertTrue("subcol1 should exist", s.existsColumn("col1", "subcol1"));
		Assert.assertTrue("subcol2 should exist", s.existsColumn("col1", "subcol2"));
		Assert.assertFalse("subcol3 should not exist", s.existsColumn("col1", "subcol3"));
	}

	@Test
	public void testReadMultipleColumns() throws Exception {

		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Data for ColFamily
		String col1 = "value1";
		int col2 = 1234;
		Calendar col3 = Calendar.getInstance();

		// Write data to ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		s.writeColumn("col1", col1);
		s.writeColumn("col2", col2);
		s.writeColumn("col3", col3);

		// Write data to SuperColFamily
		s.setFamilyAndKey("SuperColumnTest1", rowKey);
		s.writeSubColumn("col1", "subCol1", col1);
		s.writeSubColumn("col1", "subCol2", col2);
		s.writeSubColumn("col1", "subCol3", col3);
		s.writeSubColumn("col2", "subCol1", col1);
		s.writeSubColumn("col2", "subCol2", col2);
		s.writeSubColumn("col2", "subCol3", col3);

		// Assert ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		AtreusColumnMap map = s.readColumns();

		Assert.assertEquals(rowKey, map.getRowKey(String.class));
		Assert.assertEquals(col1, map.get("col1", String.class));
		Assert.assertEquals(Integer.valueOf(col2), map.get("col2", Integer.class));
		Assert.assertEquals(col3, map.get("col3", Calendar.class));
		Assert.assertNull(map.get("no-exist", String.class));

		// Assert SuperColFamily
		s.setFamilyAndKey("SuperColumnTest1", rowKey);
		map = s.readColumns();

		Assert.assertEquals(col1, map.get("col1").get("subCol1", String.class));
		Assert.assertEquals(Integer.valueOf(col2), map.get("col1").get("subCol2", Integer.class));
		Assert.assertEquals(col3, map.get("col1").get("subCol3", Calendar.class));
		Assert.assertEquals(col1, map.get("col2").get("subCol1", String.class));
		Assert.assertEquals(Integer.valueOf(col2), map.get("col2").get("subCol2", Integer.class));
		Assert.assertEquals(col3, map.get("col2").get("subCol3", Calendar.class));

	}

	@Test
	public void testReadMultipleColumnsNegative() throws Exception {

		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Write data to ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		AtreusColumnMap map = s.readColumns();

		Assert.assertEquals(0, map.size());
	}

	@Test
	public void testReadMultipleColumnsNegativeNonExistent() throws Exception {

		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Read non-existent multi columns
		s.setFamilyAndKey("ColumnTest2", rowKey);
		AtreusColumnMap map = s.readColumns();
		Assert.assertEquals(0, map.size());
	}

	@Test
	public void testSubColumnWriteRead() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Data for ColFamily
		String col1 = "value1";
		int col2 = 1234;
		Calendar col3 = Calendar.getInstance();

		s.setFamilyAndKey("SuperColumnTest1", rowKey);

		// Write data to ColFamily
		s.writeSubColumn("col1", "subCol1", col1);
		s.writeSubColumn("col1", "subCol2", col2);
		s.writeSubColumn("col1", "subCol3", col3);
		s.writeSubColumn("col1", "subCol4");

		s.writeSubColumn("col2", "subCol1", col1);
		s.writeSubColumn("col2", "subCol2", col2);
		s.writeSubColumn("col2", "subCol3", col3);

		s.writeSubColumn("col3", "subCol1", col1);
		s.writeSubColumn("col3", "subCol2", col2);
		s.writeSubColumn("col3", "subCol3", col3);

		// Assert
		Assert.assertEquals(col1, s.readColumn("col1", "subCol1", String.class));
		Assert.assertEquals(Integer.valueOf(col2), s.readColumn("col1", "subCol2", Integer.class));
		Assert.assertEquals(col3, s.readColumn("col1", "subCol3", Calendar.class));
		Assert.assertEquals("", s.readColumn("col1", "subCol4", String.class));
		Assert.assertEquals(col1, s.readColumn("col2", "subCol1", String.class));
		Assert.assertEquals(Integer.valueOf(col2), s.readColumn("col2", "subCol2", Integer.class));
		Assert.assertEquals(col3, s.readColumn("col2", "subCol3", Calendar.class));
		Assert.assertEquals(col1, s.readColumn("col3", "subCol1", String.class));
		Assert.assertEquals(Integer.valueOf(col2), s.readColumn("col3", "subCol2", Integer.class));
		Assert.assertEquals(col3, s.readColumn("col3", "subCol3", Calendar.class));
	}
}
