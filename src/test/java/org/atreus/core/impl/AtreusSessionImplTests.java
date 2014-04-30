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

import org.atreus.core.BaseAtreusCassandraTests;
import org.atreus.core.impl.entities.tests.TypeConversionTestEntity;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for the Atreus Session impl.
 *
 * @author Martin Crawford
 */
public class AtreusSessionImplTests extends BaseAtreusCassandraTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusSessionImplTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testSaveFind() {
    executeCQL("CREATE TABLE default.TypeConversionTestEntity (" +
        "id text, " +
        "aBigDecimal decimal, " +
        "aBigInteger varint, " +
        "aBoolean boolean, " +
        "aDate timestamp, " +
        "aDouble double, " +
        "aFloat float, " +
        "anInetAddress inet, " +
        "aInteger int, " +
        "aLong bigint, " +
        "aString text, " +
        "aUuid uuid, " +
        "PRIMARY KEY(id))");
    getEnvironment().getEntityManager().scanPath("org.atreus.core.impl.entities.tests");

    TypeConversionTestEntity testEntity = new TypeConversionTestEntity();
    testEntity.setaString("field1Value");
    testEntity.setaInteger(321);

    getSession().save(testEntity);
    String primaryKey = testEntity.getId();

    TypeConversionTestEntity otherEntity = getSession().findByPrimaryKey(TypeConversionTestEntity.class, primaryKey);

    Assert.assertNotNull("Expect a value", otherEntity);
    Assert.assertEquals(primaryKey, otherEntity.getId());
    Assert.assertEquals("field1Value", otherEntity.getaString());
    Assert.assertEquals(321, otherEntity.getaInteger());
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class