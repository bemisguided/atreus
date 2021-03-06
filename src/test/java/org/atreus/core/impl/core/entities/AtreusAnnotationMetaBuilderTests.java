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
package org.atreus.core.impl.core.entities;

import org.atreus.core.BaseAtreusTests;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.ext.meta.AtreusMetaManager;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.core.tests.entities.common.TestEntity;
import org.atreus.impl.core.mappings.MappingBuilder;
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
public class AtreusAnnotationMetaBuilderTests extends BaseAtreusTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusAnnotationMetaBuilderTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testScanPath() {
    LOG.info("Running testScanPath");
    MappingBuilder mappingBuilder = getEnvironment().getMappingBuilder();
    mappingBuilder.scanPath(DEFAULT_SCAN_PATH);
    mappingBuilder.build();

    AtreusMetaManager metaManager = getEnvironment().getMetaManager();

    // Assert TestEntity registry
    AtreusMetaEntity managedEntity = metaManager.getEntity(TestEntity.class);
    Assert.assertNotNull("Expected not null ManagedEntity", managedEntity);
    managedEntity = metaManager.getEntity("TestEntity");
    Assert.assertNotNull("Expected not null ManagedEntity", managedEntity);

    // Assert TestEntity entity
    Assert.assertEquals(getEnvironment().getConfiguration().getKeySpace(), managedEntity.getTable().getKeySpace());
    Assert.assertEquals("TestEntity", managedEntity.getName());
    Assert.assertEquals("TestEntity", managedEntity.getTable().getName());
    Assert.assertEquals(TestEntity.class, managedEntity.getEntityType());

    // Assert TestEntity primary key
    AtreusMetaSimpleField primaryKeyField = (AtreusMetaSimpleField) managedEntity.getPrimaryKeyField();
    Assert.assertEquals("primaryKey", primaryKeyField.getColumn());
    Assert.assertEquals("primaryKey", primaryKeyField.getName());
    Assert.assertNotNull("Expected not null TypeAccessor", primaryKeyField.getTypeStrategy());
    Assert.assertEquals(StringTypeStrategy.class, primaryKeyField.getTypeStrategy().getClass());

    // Assert TestEntity fields
    Assert.assertEquals(3, managedEntity.getFields().length);
    for (AtreusMetaField metaField : managedEntity.getFields()) {
      AtreusMetaSimpleField metaSimpleField = (AtreusMetaSimpleField) metaField;
      if ("field1".equals(metaSimpleField.getColumn())) {
        Assert.assertNotNull("Expected not null TypeAccessor", metaSimpleField.getTypeStrategy());
        Assert.assertEquals(StringTypeStrategy.class, metaSimpleField.getTypeStrategy().getClass());
      }
      if ("field2".equals(metaSimpleField.getColumn())) {
        Assert.assertNotNull("Expected not null TypeAccessor", metaSimpleField.getTypeStrategy());
        Assert.assertEquals(IntegerTypeStrategy.class, metaSimpleField.getTypeStrategy().getClass());
      }
      if ("field4field".equals(metaSimpleField.getColumn())) {
        Assert.assertNotNull("Expected not null TypeAccessor", metaSimpleField.getTypeStrategy());
        Assert.assertEquals(LongTypeStrategy.class, metaSimpleField.getTypeStrategy().getClass());
      }
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class