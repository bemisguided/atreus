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
package org.atreus.core.impl.queries;

import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.impl.entities.meta.DynamicMetaFieldImpl;
import org.atreus.impl.entities.meta.MetaEntityImpl;
import org.atreus.impl.queries.QueryHelper;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the Query Helper.
 *
 * @author Martin Crawford
 */
public class QueryHelperTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(QueryHelperTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testInsert() {
    LOG.info("Running testInsert");
    MetaEntityImpl metaEntity = buildEntity("QueryHelperTests", "testInsert");
    metaEntity.setPrimaryKeyField(buildField(metaEntity, "id"));
    metaEntity.addField(buildField(metaEntity, "col1"));
    metaEntity.addField(buildField(metaEntity, "col2"));

    assertEquals("INSERT INTO queryhelpertests.testInsert(id,col1,col2) VALUES (:id,:col1,:col2);", QueryHelper.insertEntity(metaEntity).getQueryString());
  }

  @Test
  public void testInsertWithTtl() {
    LOG.info("Running testInsertWithTtl");
    MetaEntityImpl metaEntity = buildEntity("QueryHelperTests", "testInsert");
    metaEntity.setPrimaryKeyField(buildField(metaEntity, "id"));
    metaEntity.addField(buildField(metaEntity, "col1"));
    metaEntity.addField(buildField(metaEntity, "col2"));
    metaEntity.setTtlField(buildField(metaEntity, "ttl1"));

    assertEquals("INSERT INTO queryhelpertests.testInsert(id,col1,col2) VALUES (:id,:col1,:col2) USING TTL :ttl1;", QueryHelper.insertEntity(metaEntity, true).getQueryString());
  }

  @Test
  @Ignore
  public void testInsertWithCompositeKey() {
    LOG.info("Running testInsertWithCompositeKey");
  }

  @Test
  public void testSelect() {
    LOG.info("Running testSelect");
    MetaEntityImpl metaEntity = buildEntity("QueryHelperTests", "testSelect");
    metaEntity.setPrimaryKeyField(buildField(metaEntity, "id"));
    metaEntity.addField(buildField(metaEntity, "col1"));
    metaEntity.addField(buildField(metaEntity, "col2"));

    assertEquals("SELECT * FROM queryhelpertests.testSelect WHERE id=:id;", QueryHelper.selectEntity(metaEntity).getQueryString());
  }

  @Test
  @Ignore
  public void testSelectWithCompositeKey() {
    LOG.info("Running testSelectWithCompositeKey");
  }

  @Test
  public void testUpdate() {
    LOG.info("Running testUpdate");
    MetaEntityImpl metaEntity = buildEntity("QueryHelperTests", "testUpdate");
    metaEntity.setPrimaryKeyField(buildField(metaEntity, "id"));
    metaEntity.addField(buildField(metaEntity, "col1"));
    metaEntity.addField(buildField(metaEntity, "col2"));

    assertEquals("UPDATE queryhelpertests.testUpdate SET col1=:col1,col2=:col2 WHERE id=:id;", QueryHelper.updateEntity(metaEntity).getQueryString());
  }

  @Test
  public void testUpdateWithTtl() {
    LOG.info("Running testUpdateWithTtl");
    MetaEntityImpl metaEntity = buildEntity("QueryHelperTests", "testUpdate");
    metaEntity.setPrimaryKeyField(buildField(metaEntity, "id"));
    metaEntity.addField(buildField(metaEntity, "col1"));
    metaEntity.addField(buildField(metaEntity, "col2"));
    metaEntity.setTtlField(buildField(metaEntity, "ttl1"));

    assertEquals("UPDATE queryhelpertests.testUpdate USING TTL :ttl1 SET col1=:col1,col2=:col2 WHERE id=:id;", QueryHelper.updateEntity(metaEntity, true).getQueryString());
  }

  @Test
  @Ignore
  public void testUpdateWithCompositeKey() {
    LOG.info("Running testUpdateWithCompositeKey");
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private MetaEntityImpl buildEntity(String keySpaceName, String tableName) {
    MetaEntityImpl managedEntity = new MetaEntityImpl();
    managedEntity.setKeySpace(keySpaceName);
    managedEntity.setTable(tableName);
    return managedEntity;
  }

  private AtreusMetaField buildField(MetaEntityImpl metaEntity, String columnName) {
    DynamicMetaFieldImpl managedField = new DynamicMetaFieldImpl(metaEntity, columnName, String.class);
    managedField.setColumn(columnName);
    return managedField;
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class