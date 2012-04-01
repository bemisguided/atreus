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
import org.atreus.AtreusConfiguration;
import org.atreus.AtreusIllegalStateException;
import org.atreus.AtreusSessionFactory;
import org.atreus.AtreusSessionFactoryBuilder;
import org.cassandraunit.AbstractCassandraUnit4TestCase;
import org.cassandraunit.dataset.DataSet;
import org.cassandraunit.dataset.xml.ClassPathXmlDataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AtreusSessionBatchTests extends AbstractCassandraUnit4TestCase {

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
	}

	@After
	public void teardown() throws Exception {
		s.close();
		sessionFactory.disconnect();
	}

	@Test
	public void testBatchModeSwitching() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();

		// Turn off batch mode
		s.setBatchWriting(false);

		// Write and assert data to ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		s.writeColumn("col1", "value1");
		Assert.assertEquals("value1", s.readColumn("col1", String.class));

		// Turn on batch mode
		s.setBatchWriting(true);

		// Write and assert data to ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		s.writeColumn("col2", "value2");
		Assert.assertFalse("col2 should not exist", s.existsColumn("col2"));
		s.flush();
		Assert.assertEquals("value2", s.readColumn("col2", String.class));

		// Attempt to turn off batch mode mid-batch
		try {
			s.setFamilyAndKey("ColumnTest1", rowKey);
			s.writeColumn("col2", "value2");
			s.setBatchWriting(false);
			Assert.fail("Expect AtreusIllegalStateException");
		} catch (AtreusIllegalStateException e) {

		}
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

		// Flush the batch
		s.flush();

		// Assert
		Assert.assertEquals(col1, s.readColumn("col1", col1.getClass()));
		Assert.assertEquals(Integer.valueOf(col2), s.readColumn("col2", Integer.class));
		Assert.assertEquals(col3, s.readColumn("col3", col3.getClass()));
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

		// Flush the batch
		s.flush();

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

		// Flush the batch
		s.flush();

		s.setFamilyAndKey("ColumnTest1", rowKey1);
		s.deleteColumn("col1");

		s.setFamilyAndKey("SuperColumnTest1", rowKey2);
		s.deleteColumn("col1", "subCol1");

		// Flush the batch
		s.flush();

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
		s.setFamilyAndKey("ColumnTest1", rowKey2);
		s.writeColumn("col1", "val1");
		s.writeColumn("col2", "val2");

		// Flush the batch
		s.flush();

		s.deleteRow();
		s.deleteRow("ColumnTest1", rowKey1);

		// Flush the batch
		s.flush();

		s.setFamilyAndKey("ColumnTest1", rowKey1);
		Assert.assertNull("Row key " + rowKey1 + " null", s.readColumn("col1", String.class));
		s.setFamilyAndKey("ColumnTest1", rowKey2);
		Assert.assertNull("Row key " + rowKey2 + " null", s.readColumn("col1", String.class));
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

		// Flush the batch
		s.flush();

		// Assert ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		AtreusColumnMap map = s.readColumns();

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

		s.writeSubColumn("col2", "subCol1", col1);
		s.writeSubColumn("col2", "subCol2", col2);
		s.writeSubColumn("col2", "subCol3", col3);

		s.writeSubColumn("col3", "subCol1", col1);
		s.writeSubColumn("col3", "subCol2", col2);
		s.writeSubColumn("col3", "subCol3", col3);

		// Flush the batch
		s.flush();

		// Assert
		Assert.assertEquals(col1, s.readColumn("col1", "subCol1", String.class));
		Assert.assertEquals(Integer.valueOf(col2), s.readColumn("col1", "subCol2", Integer.class));
		Assert.assertEquals(col3, s.readColumn("col1", "subCol3", Calendar.class));
		Assert.assertEquals(col1, s.readColumn("col2", "subCol1", String.class));
		Assert.assertEquals(Integer.valueOf(col2), s.readColumn("col2", "subCol2", Integer.class));
		Assert.assertEquals(col3, s.readColumn("col2", "subCol3", Calendar.class));
		Assert.assertEquals(col1, s.readColumn("col3", "subCol1", String.class));
		Assert.assertEquals(Integer.valueOf(col2), s.readColumn("col3", "subCol2", Integer.class));
		Assert.assertEquals(col3, s.readColumn("col3", "subCol3", Calendar.class));
	}
}