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
package org.atreus.core.impl.entities;

import org.atreus.core.ext.entities.AtreusManagedEntity;
import org.atreus.core.ext.entities.AtreusManagedField;
import org.atreus.core.BaseAtreusTests;
import org.atreus.core.impl.entities.tests.TestEntity1;
import org.atreus.impl.entities.EntityManager;
import org.atreus.impl.types.cql.IntegerTypeAccessor;
import org.atreus.impl.types.cql.LongTypeAccessor;
import org.atreus.impl.types.cql.StringTypeAccessor;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for the Atreus Annotation entity strategy.
 *
 * @author Martin Crawford
 */
public class AtreusAnnotationEntityStrategyTests extends BaseAtreusTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusAnnotationEntityStrategyTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testScanPath() {
    EntityManager entityManager = getEnvironment().getEntityManager();
    entityManager.scanPath("org.atreus.core.impl.entities.tests");

    // Assert TestEntity1 registry
    AtreusManagedEntity managedEntity = entityManager.getEntity(TestEntity1.class);
    Assert.assertNotNull("Expected not null ManagedEntity", managedEntity);
    managedEntity = entityManager.getEntity("TestEntity1");
    Assert.assertNotNull("Expected not null ManagedEntity", managedEntity);

    // Assert TestEntity1 entity
    Assert.assertEquals(getEnvironment().getConfiguration().getKeySpace(), managedEntity.getKeySpace());
    Assert.assertEquals("TestEntity1", managedEntity.getName());
    Assert.assertEquals("TestEntity1", managedEntity.getTable());
    Assert.assertEquals(TestEntity1.class, managedEntity.getEntityType());

    // Assert TestEntity1 primary key
    Assert.assertEquals("primaryKey", managedEntity.getPrimaryKeyField().getColumn());
    Assert.assertEquals("primaryKey", managedEntity.getPrimaryKeyField().getJavaField().getName());
    Assert.assertTrue("accessibility should be true", managedEntity.getPrimaryKeyField().getJavaField().isAccessible());
    Assert.assertNotNull("Expected not null TypeAccessor", managedEntity.getPrimaryKeyField().getTypeAccessor());
    Assert.assertEquals(StringTypeAccessor.class, managedEntity.getPrimaryKeyField().getTypeAccessor().getClass());

    // Assert TestEntity1 fields
    Assert.assertEquals(3, managedEntity.getFields().size());
    for (AtreusManagedField managedField : managedEntity.getFields()) {
      if ("field1".equals(managedField.getColumn())) {
        Assert.assertNotNull("Expected not null TypeAccessor", managedField.getTypeAccessor());
        Assert.assertEquals(StringTypeAccessor.class, managedField.getTypeAccessor().getClass());
      }
      if ("field2".equals(managedField.getColumn())) {
        Assert.assertNotNull("Expected not null TypeAccessor", managedField.getTypeAccessor());
        Assert.assertEquals(IntegerTypeAccessor.class, managedField.getTypeAccessor().getClass());
      }
      if ("field4field".equals(managedField.getColumn())) {
        Assert.assertNotNull("Expected not null TypeAccessor", managedField.getTypeAccessor());
        Assert.assertEquals(LongTypeAccessor.class, managedField.getTypeAccessor().getClass());
      }
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class