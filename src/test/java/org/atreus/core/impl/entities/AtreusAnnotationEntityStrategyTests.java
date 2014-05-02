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

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusManagedField;
import org.atreus.core.BaseAtreusTests;
import org.atreus.core.tests.entities.common.TestEntity;
import org.atreus.impl.entities.EntityManager;
import org.atreus.impl.types.cql.IntegerTypeStrategy;
import org.atreus.impl.types.cql.LongTypeStrategy;
import org.atreus.impl.types.cql.StringTypeStrategy;
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
    LOG.info("Running testScanPath");
    EntityManager entityManager = getEnvironment().getEntityManager();
    entityManager.scanPath(DEFAULT_SCAN_PATH);
    entityManager.processEntities();

    // Assert TestEntity registry
    AtreusManagedEntity managedEntity = entityManager.getEntity(TestEntity.class);
    Assert.assertNotNull("Expected not null ManagedEntity", managedEntity);
    managedEntity = entityManager.getEntity("TestEntity");
    Assert.assertNotNull("Expected not null ManagedEntity", managedEntity);

    // Assert TestEntity entity
    Assert.assertEquals(getEnvironment().getConfiguration().getKeySpace(), managedEntity.getKeySpace());
    Assert.assertEquals("TestEntity", managedEntity.getName());
    Assert.assertEquals("TestEntity", managedEntity.getTable());
    Assert.assertEquals(TestEntity.class, managedEntity.getEntityType());

    // Assert TestEntity primary key
    Assert.assertEquals("primaryKey", managedEntity.getPrimaryKeyField().getColumn());
    Assert.assertEquals("primaryKey", managedEntity.getPrimaryKeyField().getJavaField().getName());
    Assert.assertTrue("accessibility should be true", managedEntity.getPrimaryKeyField().getJavaField().isAccessible());
    Assert.assertNotNull("Expected not null TypeAccessor", managedEntity.getPrimaryKeyField().getTypeStrategy());
    Assert.assertEquals(StringTypeStrategy.class, managedEntity.getPrimaryKeyField().getTypeStrategy().getClass());

    // Assert TestEntity fields
    Assert.assertEquals(3, managedEntity.getFields().length);
    for (AtreusManagedField managedField : managedEntity.getFields()) {
      if ("field1".equals(managedField.getColumn())) {
        Assert.assertNotNull("Expected not null TypeAccessor", managedField.getTypeStrategy());
        Assert.assertEquals(StringTypeStrategy.class, managedField.getTypeStrategy().getClass());
      }
      if ("field2".equals(managedField.getColumn())) {
        Assert.assertNotNull("Expected not null TypeAccessor", managedField.getTypeStrategy());
        Assert.assertEquals(IntegerTypeStrategy.class, managedField.getTypeStrategy().getClass());
      }
      if ("field4field".equals(managedField.getColumn())) {
        Assert.assertNotNull("Expected not null TypeAccessor", managedField.getTypeStrategy());
        Assert.assertEquals(LongTypeStrategy.class, managedField.getTypeStrategy().getClass());
      }
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class