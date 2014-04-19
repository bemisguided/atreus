package org.atreus.core.impl.queries;

import org.atreus.impl.entities.ManagedEntity;
import org.atreus.impl.entities.ManagedField;
import org.atreus.impl.queries.QueryHelper;
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
    ManagedEntity managedEntity = buildEntity("QueryHelperTests", "testInsert");
    managedEntity.setPrimaryKey(new ManagedField[]{buildField("id")});
    managedEntity.setFields(new ManagedField[]{buildField("col1"), buildField("col2")});

    assertEquals("INSERT INTO queryhelpertests.testInsert(id,col1,col2) VALUES (:id,:col1,:col2);", QueryHelper.insertEntity(managedEntity).getQueryString());
  }

  @Test
  public void testInsertWithCompositeKey() {
    ManagedEntity managedEntity = buildEntity("QueryHelperTests", "testInsertWithCompositeKey");
    managedEntity.setPrimaryKey(new ManagedField[]{buildField("id1"), buildField("id2")});
    managedEntity.setFields(new ManagedField[]{buildField("col1"), buildField("col2")});

    assertEquals("INSERT INTO queryhelpertests.testInsertWithCompositeKey(id1,id2,col1,col2) VALUES (:id1,:id2,:col1,:col2);", QueryHelper.insertEntity(managedEntity).getQueryString());
  }

  @Test
  public void testSelect() {
    ManagedEntity managedEntity = buildEntity("QueryHelperTests", "testSelect");
    managedEntity.setPrimaryKey(new ManagedField[]{buildField("id")});
    managedEntity.setFields(new ManagedField[]{buildField("col1"), buildField("col2")});

    assertEquals("SELECT * FROM queryhelpertests.testSelect WHERE id=:id;", QueryHelper.selectEntity(managedEntity).getQueryString());
  }

  @Test
  public void testSelectWithCompositeKey() {
    ManagedEntity managedEntity = buildEntity("QueryHelperTests", "testSelectWithCompositeKey");
    managedEntity.setPrimaryKey(new ManagedField[]{buildField("id1"), buildField("id2")});
    managedEntity.setFields(new ManagedField[]{buildField("col1"), buildField("col2")});

    assertEquals("SELECT * FROM queryhelpertests.testSelectWithCompositeKey WHERE id1=:id1 AND id2=:id2;", QueryHelper.selectEntity(managedEntity).getQueryString());
  }

  @Test
  public void testUpdate() {
    ManagedEntity managedEntity = buildEntity("QueryHelperTests", "testUpdate");
    managedEntity.setPrimaryKey(new ManagedField[]{buildField("id")});
    managedEntity.setFields(new ManagedField[]{buildField("col1"), buildField("col2")});

    assertEquals("UPDATE queryhelpertests.testUpdate SET col1=:col1,col2=:col2 WHERE id=:id;", QueryHelper.updateEntity(managedEntity).getQueryString());
  }

  @Test
  public void testUpdateWithCompositeKey() {
    ManagedEntity managedEntity = buildEntity("QueryHelperTests", "testUpdate");
    managedEntity.setPrimaryKey(new ManagedField[]{buildField("id1"), buildField("id2")});
    managedEntity.setFields(new ManagedField[]{buildField("col1"), buildField("col2")});

    assertEquals("UPDATE queryhelpertests.testUpdate SET col1=:col1,col2=:col2 WHERE id1=:id1 AND id2=:id2;", QueryHelper.updateEntity(managedEntity).getQueryString());
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private ManagedEntity buildEntity(String keySpaceName, String tableName) {
    ManagedEntity managedEntity = new ManagedEntity();
    managedEntity.setKeySpaceName(keySpaceName);
    managedEntity.setTableName(tableName);
    return managedEntity;
  }

  private ManagedField buildField(String columnName) {
    ManagedField managedField = new ManagedField();
    managedField.setColumnName(columnName);
    return managedField;
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class