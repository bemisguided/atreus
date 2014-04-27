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
package org.atreus.impl.queries;

import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusManagedField;
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

  public static RegularStatement insertEntity(AtreusManagedEntity managedEntity) {
    return insertEntity(managedEntity, false);
  }

  public static RegularStatement insertEntity(AtreusManagedEntity managedEntity, boolean withTtl) {
    Insert insert = insertInto(managedEntity.getKeySpace(), managedEntity.getTable());
    String columnName = managedEntity.getPrimaryKeyField().getColumn();
    insert.value(columnName, bindMarker(columnName));
    for (AtreusManagedField managedField : managedEntity.getFields()) {
      columnName = managedField.getColumn();
      insert.value(columnName, bindMarker(columnName));
    }
    AtreusManagedField ttlField = managedEntity.getTtlField();
    if (ttlField != null && withTtl) {
      insert.using(ttl(bindMarker(ttlField.getColumn())));
    }
    return insert;
  }

  public static RegularStatement selectEntity(AtreusManagedEntity managedEntity) {
    Select select = select().all().from(managedEntity.getKeySpace(), managedEntity.getTable());
    String columnName = managedEntity.getPrimaryKeyField().getColumn();
    select.where(eq(columnName, bindMarker(columnName)));
    return select;
  }

  public static RegularStatement updateEntity(AtreusManagedEntity managedEntity) {
    return updateEntity(managedEntity, false);
  }

  public static RegularStatement updateEntity(AtreusManagedEntity managedEntity, boolean withTtl) {
    Update update = update(managedEntity.getKeySpace(), managedEntity.getTable());
    String columnName = managedEntity.getPrimaryKeyField().getColumn();
    Update.Where where = update.where(eq(columnName, bindMarker(columnName)));
    for (AtreusManagedField managedField : managedEntity.getFields()) {
      columnName = managedField.getColumn();
      where.with(set(columnName, bindMarker(columnName)));
    }
    AtreusManagedField ttlField = managedEntity.getTtlField();
    if (ttlField != null && withTtl) {
      update.using(ttl(bindMarker(ttlField.getColumn())));
    }
    return update;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class