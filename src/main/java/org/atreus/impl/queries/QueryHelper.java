package org.atreus.impl.queries;

import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import org.atreus.impl.entities.ManagedEntity;
import org.atreus.impl.entities.ManagedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

/**
 * Helper class to build CQL queries.
 *
 * @author Martin Crawford
 */
public class QueryHelper {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(QueryHelper.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public static RegularStatement insertEntity(ManagedEntity managedEntity) {
    Insert insert = insertInto(managedEntity.getKeySpaceName(), managedEntity.getTableName());
    for (ManagedField managedField : managedEntity.getPrimaryKey()) {
      String columnName = managedField.getColumnName();
      insert.value(columnName, bindMarker(columnName));
    }
    for (ManagedField managedField : managedEntity.getFields()) {
      String columnName = managedField.getColumnName();
      insert.value(columnName, bindMarker(columnName));
    }
    return insert;
  }

  public static RegularStatement selectEntity(ManagedEntity managedEntity) {
    Select select = select().all().from(managedEntity.getKeySpaceName(), managedEntity.getTableName());
    Select.Where where = null;
    for (ManagedField managedField : managedEntity.getPrimaryKey()) {
      String columnName = managedField.getColumnName();
      if (where == null) {
        where = select.where(eq(columnName, bindMarker(columnName)));
        continue;
      }
      where.and(eq(columnName, bindMarker(columnName)));
    }
    return select;
  }

  public static RegularStatement updateEntity(ManagedEntity managedEntity) {
    Update update = update(managedEntity.getKeySpaceName(), managedEntity.getTableName());
    Update.Where where = null;
    for (ManagedField managedField : managedEntity.getPrimaryKey()) {
      String columnName = managedField.getColumnName();
      if (where == null) {
        where = update.where(eq(columnName, bindMarker(columnName)));
        continue;
      }
      where.and(eq(columnName, bindMarker(columnName)));
    }
    for (ManagedField managedField : managedEntity.getFields()) {
      String columnName = managedField.getColumnName();
      where.with(set(columnName, bindMarker(columnName)));
    }
    return update;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class