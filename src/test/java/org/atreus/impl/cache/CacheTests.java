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
package org.atreus.impl.cache;

import junit.framework.Assert;

import org.junit.Test;

public class CacheTests {
	
	@Test
	public void testSingleColumnReadScenario() throws Exception {
		SessionCache cache = new SessionCache();
		
		String columnFamily = "colFam";
		byte[] rowKey = new byte[]{1,2,3,4};
		byte[] col1 = new byte[]{0,0,0,1};
		byte[] col2 = new byte[]{0,0,0,2};
		byte[] col3 = new byte[]{0,0,0,3};
		byte[] val1 = new byte[]{1,0,0,1};
		byte[] val2 = new byte[]{1,0,0,2};
		byte[] val3 = new byte[]{1,0,0,3};
		
		CachedColumnFamily cachedColFam = cache.getColumnFamily(columnFamily);
		CachedRow cachedRow = cachedColFam.getRow(rowKey);
		Assert.assertEquals(CacheResult.UNKNOWN, cachedColFam.getResult());
		Assert.assertEquals(CacheResult.UNKNOWN, cachedRow.getResult());
		
		// Logic: If CachedRow is UNKNOWN or PARTIAL fetch eagerly
		cachedRow.getColumn(col1).setValue(val1);
		cachedRow.getColumn(col2).setValue(val2);
		cachedRow.getColumn(col3).setValue(val3);
		Assert.assertEquals(CacheResult.FULL, cachedRow.getColumn(col1).getResult());
		Assert.assertEquals(CacheResult.FULL, cachedRow.getColumn(col2).getResult());
		Assert.assertEquals(CacheResult.FULL, cachedRow.getColumn(col3).getResult());
		Assert.assertEquals(CacheResult.PARTIAL, cachedRow.getResult());
		Assert.assertEquals(CacheResult.PARTIAL, cachedColFam.getResult());
		
		// Mark's the CachedRow as FULL
		cachedRow.markResultFull();
		Assert.assertEquals(CacheResult.FULL, cachedRow.getResult());
		
	}
	
	@Test
	public void testSingleColumnWriteScenario() throws Exception {
		SessionCache cache = new SessionCache();
		
		String columnFamily = "colFam";
		byte[] rowKey = new byte[]{1,2,3,4};
		byte[] col1 = new byte[]{0,0,0,1};
		byte[] val1 = new byte[]{1,0,0,1};
		
		CachedColumnFamily cachedColFam = cache.getColumnFamily(columnFamily);
		CachedRow cachedRow = cachedColFam.getRow(rowKey);
		
		cachedRow.getColumn(col1).setValue(val1);
		Assert.assertEquals(CacheResult.FULL, cachedRow.getColumn(col1).getResult());
		Assert.assertEquals(val1, cache.getColumnFamily(columnFamily).getRow(rowKey).getColumn(col1).getValue());
		Assert.assertEquals(CacheResult.PARTIAL, cachedRow.getResult());
		Assert.assertEquals(CacheResult.PARTIAL, cachedColFam.getResult());
		
	}
	
	@Test
	public void testSingleColumnDeleteScenario() throws Exception {
		SessionCache cache = new SessionCache();
		
		String columnFamily = "colFam";
		byte[] rowKey = new byte[]{1,2,3,4};
		byte[] col1 = new byte[]{0,0,0,1};
		byte[] col2 = new byte[]{0,0,0,2};
		byte[] col3 = new byte[]{0,0,0,3};
		byte[] val1 = new byte[]{1,0,0,1};
		byte[] val2 = new byte[]{1,0,0,2};
		byte[] val3 = new byte[]{1,0,0,3};
		
		CachedColumnFamily cachedColFam = cache.getColumnFamily(columnFamily);
		CachedRow cachedRow = cachedColFam.getRow(rowKey);
		cachedRow.getColumn(col1).setValue(val1);
		cachedRow.getColumn(col2).setValue(val2);
		cachedRow.getColumn(col3).setValue(val3);
		cachedRow.markResultFull();
		Assert.assertEquals(CacheResult.FULL, cachedRow.getColumn(col1).getResult());
		Assert.assertEquals(CacheResult.FULL, cachedRow.getColumn(col2).getResult());
		Assert.assertEquals(CacheResult.FULL, cachedRow.getColumn(col3).getResult());
		Assert.assertEquals(CacheResult.PARTIAL, cachedColFam.getResult());
		Assert.assertEquals(CacheResult.FULL, cachedRow.getResult());
		
		cachedRow.getColumn(col2).markResultDelete();
		Assert.assertEquals(CacheResult.DELETED, cachedRow.getColumn(col2).getResult());
		Assert.assertNull("Expect a null value", cachedRow.getColumn(col2).getValue());
		Assert.assertEquals(CacheResult.FULL, cachedRow.getResult());
		
		cachedRow.markResultDelete();
		Assert.assertEquals(CacheResult.DELETED, cachedRow.getColumn(col1).getResult());
		Assert.assertNull("Expect a null value", cachedRow.getColumn(col1).getValue());
		Assert.assertEquals(CacheResult.DELETED, cachedRow.getColumn(col2).getResult());
		Assert.assertNull("Expect a null value", cachedRow.getColumn(col2).getValue());
		Assert.assertEquals(CacheResult.DELETED, cachedRow.getColumn(col3).getResult());
		Assert.assertNull("Expect a null value", cachedRow.getColumn(col3).getValue());
		Assert.assertEquals(CacheResult.DELETED, cachedRow.getResult());
		
	}
}
