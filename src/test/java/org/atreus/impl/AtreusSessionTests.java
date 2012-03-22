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

package org.atreus.impl;

import java.util.Calendar;
import java.util.UUID;

import org.cassandraunit.AbstractCassandraUnit4TestCase;
import org.cassandraunit.dataset.DataSet;
import org.cassandraunit.dataset.xml.ClassPathXmlDataSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AtreusSessionTests extends AbstractCassandraUnit4TestCase {

	private AtreusSessionFactoryImpl sessionFactory = new AtreusSessionFactoryImpl();

	private AtreusSessionImpl s;

	@Override
	public DataSet getDataSet() {
		return new ClassPathXmlDataSet("AtreusSessionTests.xml");
	}

	@Before
	public void setup() throws Exception {
		sessionFactory.connect("localhost", 9171, "AtreusSessionTests");
		s = (AtreusSessionImpl) sessionFactory.openSession();
	}

	@After
	public void teardown() throws Exception {
		s.close();
		sessionFactory.disconnect();
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
		s.flush();

		s.deleteRow();
		s.deleteRow("ColumnTest1", rowKey1);

		s.setFamilyAndKey("ColumnTest1", rowKey1);
		Assert.assertNull("Row key " + rowKey1 + " not null", s.readColumn("col1", String.class));
		s.setFamilyAndKey("ColumnTest1", rowKey2);
		Assert.assertNull("Row key " + rowKey2 + " not null", s.readColumn("col1", String.class));
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
		s.writeColumn("col1", "subCol1", col1);
		s.writeColumn("col1", "subCol2", col2);
		s.writeColumn("col1", "subCol3", col3);
		s.flush();

		s.writeColumn("col2", "subCol1", col1);
		s.writeColumn("col2", "subCol2", col2);
		s.writeColumn("col2", "subCol3", col3);
		s.flush();

		s.writeColumn("col3", "subCol1", col1);
		s.writeColumn("col3", "subCol2", col2);
		s.writeColumn("col3", "subCol3", col3);
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

	@Test
	public void testColumnWriteRead() throws Exception {
		// Setup test
		String rowKey = UUID.randomUUID().toString();
		AtreusSessionImpl s = (AtreusSessionImpl) sessionFactory.openSession();

		// Data for ColFamily
		String col1 = "value1";
		int col2 = 1234;
		Calendar col3 = Calendar.getInstance();

		// Write data to ColFamily
		s.setFamilyAndKey("ColumnTest1", rowKey);
		// s.writeColumn("emptyCol");
		s.writeColumn("col1", col1);
		s.writeColumn("col2", col2);
		s.writeColumn("col3", col3);
		s.flush();

		// Assert

		// Assert.assertTrue("Expect exists", r.existsColumn("emptyCol"));
		// Assert.assertFalse("Expect not exist", r.existsColumn("fakeCol"));
		// Assert.assertEquals("", r.columnValueAsString("emptyCol"));
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
		s.flush();

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
}