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

import org.atreus.core.ext.AtreusManagedField;
import org.atreus.impl.entities.ManagedEntityImpl;
import org.atreus.impl.entities.ManagedFieldImpl;
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
    ManagedEntityImpl managedEntity = buildEntity("QueryHelperTests", "testInsert");
    managedEntity.setPrimaryKeyField(buildField("id"));
    managedEntity.getFields().add(buildField("col1"));
    managedEntity.getFields().add(buildField("col2"));

    assertEquals("INSERT INTO queryhelpertests.testInsert(id,col1,col2) VALUES (:id,:col1,:col2);", QueryHelper.insertEntity(managedEntity).getQueryString());
  }

  @Test
  public void testInsertWithTtl() {
    ManagedEntityImpl managedEntity = buildEntity("QueryHelperTests", "testInsert");
    managedEntity.setPrimaryKeyField(buildField("id"));
    managedEntity.getFields().add(buildField("col1"));
    managedEntity.getFields().add(buildField("col2"));
    managedEntity.setTtlField(buildField("ttl1"));

    assertEquals("INSERT INTO queryhelpertests.testInsert(id,col1,col2) VALUES (:id,:col1,:col2) USING TTL :ttl1;", QueryHelper.insertEntity(managedEntity, true).getQueryString());
  }

  @Test
  @Ignore
  public void testInsertWithCompositeKey() {
  }

  @Test
  public void testSelect() {
    ManagedEntityImpl managedEntity = buildEntity("QueryHelperTests", "testSelect");
    managedEntity.setPrimaryKeyField(buildField("id"));
    managedEntity.getFields().add(buildField("col1"));
    managedEntity.getFields().add(buildField("col2"));

    assertEquals("SELECT * FROM queryhelpertests.testSelect WHERE id=:id;", QueryHelper.selectEntity(managedEntity).getQueryString());
  }

  @Test
  @Ignore
  public void testSelectWithCompositeKey() {
  }

  @Test
  public void testUpdate() {
    ManagedEntityImpl managedEntity = buildEntity("QueryHelperTests", "testUpdate");
    managedEntity.setPrimaryKeyField(buildField("id"));
    managedEntity.getFields().add(buildField("col1"));
    managedEntity.getFields().add(buildField("col2"));

    assertEquals("UPDATE queryhelpertests.testUpdate SET col1=:col1,col2=:col2 WHERE id=:id;", QueryHelper.updateEntity(managedEntity).getQueryString());
  }

  @Test
  public void testUpdateWithTtl() {
    ManagedEntityImpl managedEntity = buildEntity("QueryHelperTests", "testUpdate");
    managedEntity.setPrimaryKeyField(buildField("id"));
    managedEntity.getFields().add(buildField("col1"));
    managedEntity.getFields().add(buildField("col2"));
    managedEntity.setTtlField(buildField("ttl1"));

    assertEquals("UPDATE queryhelpertests.testUpdate USING TTL :ttl1 SET col1=:col1,col2=:col2 WHERE id=:id;", QueryHelper.updateEntity(managedEntity, true).getQueryString());
  }

  @Test
  @Ignore
  public void testUpdateWithCompositeKey() {
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private ManagedEntityImpl buildEntity(String keySpaceName, String tableName) {
    ManagedEntityImpl managedEntity = new ManagedEntityImpl();
    managedEntity.setKeySpace(keySpaceName);
    managedEntity.setTable(tableName);
    return managedEntity;
  }

  private AtreusManagedField buildField(String columnName) {
    AtreusManagedField managedField = new ManagedFieldImpl();
    managedField.setColumn(columnName);
    return managedField;
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class