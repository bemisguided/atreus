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

import junit.framework.Assert;
import org.atreus.core.BaseAtreusCassandraTests;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.tests.entities.common.SimpleTestEntity;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Functional Tests for the Manged Entity.
 *
 * @author Martin Crawford
 */
public class ManagedEntityTests extends BaseAtreusCassandraTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ManagedEntityTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testFieldState() throws Exception {
    LOG.info("Running testFieldState");
    addEntity(SimpleTestEntity.class);
    initEnvironment();

    SimpleTestEntity simpleTestEntity = new SimpleTestEntity();
    AtreusManagedEntity managedEntity = getEnvironment().getMetaManager().wrapEntity(getSession(), simpleTestEntity);

    AtreusMetaEntity metaEntity = managedEntity.getMetaEntity();
    AtreusMetaField primaryKeyField = metaEntity.getPrimaryKeyField();
    AtreusMetaField field1Field = metaEntity.getFieldByName("field1");

    // Initial state should be un-fetched
    Assert.assertFalse("Field1 should be unfetched", managedEntity.isFetched(field1Field));

    // Now set some values
    managedEntity.setFieldValue(primaryKeyField, "1234567");
    managedEntity.setFieldValue(field1Field, "field1");

    // Both fields should now be initialized
    Assert.assertTrue("Field1 Key should be fetched", managedEntity.isFetched(field1Field));

  }

  @Test
  public void testFetching() throws Exception {
    LOG.info("Running testFetching");
    addEntity(SimpleTestEntity.class);
    initEnvironment();

    SimpleTestEntity simpleTestEntity = new SimpleTestEntity();

    SimpleTestEntity testEntity = new SimpleTestEntity();
    testEntity.setField1("field1");

    AtreusManagedEntity managedEntity = (AtreusManagedEntity) getSession().save(simpleTestEntity);
    getSession().flush();

    AtreusMetaEntity metaEntity = managedEntity.getMetaEntity();
    AtreusMetaField field1Field = metaEntity.getFieldByName("field1");

    // Both fields should now be initialized
    Assert.assertTrue("Field1 Key should be fetched", managedEntity.isFetched(field1Field));

    managedEntity = (AtreusManagedEntity) getSession().findOne(SimpleTestEntity.class, managedEntity.getPrimaryKey());

    // Both fields should be initialized immediately
    Assert.assertTrue("Field1 Key should be fetched", managedEntity.isFetched(field1Field));

  }

  @Test
  public void testFetchingWithNull() throws Exception {
    LOG.info("Running testFetchingWithNull");
    addEntity(SimpleTestEntity.class);
    initEnvironment();

    SimpleTestEntity simpleTestEntity = new SimpleTestEntity();

    SimpleTestEntity testEntity = new SimpleTestEntity();
    testEntity.setField1(null);

    AtreusManagedEntity managedEntity = (AtreusManagedEntity) getSession().save(simpleTestEntity);
    getSession().flush();

    AtreusMetaEntity metaEntity = managedEntity.getMetaEntity();
    AtreusMetaField field1Field = metaEntity.getFieldByName("field1");

    // Both fields should now be initialized
    Assert.assertTrue("Field1 Key should be fetched", managedEntity.isFetched(field1Field));

    managedEntity = (AtreusManagedEntity) getSession().findOne(SimpleTestEntity.class, managedEntity.getPrimaryKey());

    // Both fields should be initialized immediately
    Assert.assertTrue("Field1 Key should be fetched", managedEntity.isFetched(field1Field));

  }


  @Test
  public void testLazyFetchSimpleField() throws Exception {
    LOG.info("Running testFetching");
    addEntity(SimpleTestEntity.class);
    initEnvironment();

    SimpleTestEntity testEntity = new SimpleTestEntity();
    testEntity.setField1("field1");

    getSession().save(testEntity);
    getSession().flush();
    Serializable primaryKey = testEntity.getId();

    testEntity = new SimpleTestEntity();
    AtreusManagedEntity managedEntity = getEnvironment().getMetaManager().wrapEntity(getSession(), testEntity);
    testEntity = (SimpleTestEntity) managedEntity;
    AtreusMetaEntity metaEntity = managedEntity.getMetaEntity();
    AtreusMetaField primaryKeyField = metaEntity.getPrimaryKeyField();
    managedEntity.setFieldValue(primaryKeyField, primaryKey);

    LOG.debug("Lazy load begin");
    Assert.assertEquals("field1", testEntity.getField1());
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class