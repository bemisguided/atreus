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
package org.atreus.core.impl;

import org.atreus.core.BaseCassandraTests;
import org.atreus.core.impl.entities.tests.TestEntity1;
import org.atreus.core.impl.entities.tests.TestEntity2;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for the Atreus Session impl.
 *
 * @author Martin Crawford
 */
public class AtreusSessionImplTests extends BaseCassandraTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusSessionImplTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testSaveFind() {
    executeCQL("CREATE TABLE default.TestEntity2 (id text, field1 text, field2 int, PRIMARY KEY(id))");
    getEnvironment().getEntityManager().scanPath("org.atreus.core.impl.entities.tests");

    TestEntity2 testEntity = new TestEntity2();
    testEntity.setId("1234567");
    testEntity.setField1("field1Value");
    testEntity.setField2(24321);

    getSession().save(testEntity);

    TestEntity2 otherEntity = getSession().findByKey(TestEntity2.class, "1234567");

    Assert.assertNotNull("Expect a value", otherEntity);
    Assert.assertEquals("1234567", otherEntity.getId());
    Assert.assertEquals("field1Value", otherEntity.getField1());
    Assert.assertEquals(24321, (int) otherEntity.getField2());
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class