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
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import org.atreus.core.ext.meta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

  public static RegularStatement insertEntity(AtreusMetaEntity metaEntity) {
    return insertEntity(metaEntity, false);
  }

  public static RegularStatement insertEntity(AtreusMetaEntity metaEntity, boolean withTtl) {
    AtreusMetaTable table = metaEntity.getTable();
    Insert insert = insertInto(table.getKeySpace(), table.getName());
    for (String columnName : listAllColumnNames(metaEntity)) {
      insert.value(columnName, bindMarker(columnName));
    }
    AtreusMetaSimpleField ttlField = metaEntity.getTtlField();
    if (ttlField != null && withTtl) {
      insert.using(ttl(bindMarker(ttlField.getColumn())));
    }
    return insert;
  }

  public static RegularStatement selectEntity(AtreusMetaEntity metaEntity) {
    AtreusMetaTable table = metaEntity.getTable();
    Select select = select().all().from(table.getKeySpace(), table.getName());
    Select.Where where = null;
    for (String columnName : listPrimaryKeyColumnNames(metaEntity)) {
      if (where == null) {
        where = select.where(eq(columnName, bindMarker(columnName)));
        continue;
      }
      where.and(eq(columnName, bindMarker(columnName)));
    }
    return select;
  }

  public static RegularStatement selectAssociatedEntities(AtreusMetaAssociation metaAssociation) {
    AtreusMetaTable table = metaAssociation.getOutboundTable();
    Select select = select().all().from(table.getKeySpace(), table.getName());
    Select.Where where = null;
    for (String columnName : listColumnNames(metaAssociation.getOwner().getAssociationKeyField(), null)) {
      if (where == null) {
        where = select.where(eq(columnName, bindMarker(columnName)));
        continue;
      }
      where.and(eq(columnName, bindMarker(columnName)));
    }
    return select;
  }

  public static RegularStatement updateEntity(AtreusMetaEntity metaEntity) {
    return updateEntity(metaEntity, false);
  }

  public static RegularStatement updateEntity(AtreusMetaEntity metaEntity, boolean withTtl) {
    AtreusMetaTable table = metaEntity.getTable();
    Update update = update(table.getKeySpace(), table.getName());
    Update.Where where = null;
    for (String columnName : listPrimaryKeyColumnNames(metaEntity)) {
      if (where == null) {
        where = update.where(eq(columnName, bindMarker(columnName)));
        continue;
      }
      where.and(eq(columnName, bindMarker(columnName)));
    }
    for (String columnName : listFieldColumnNames(metaEntity)) {
      update.with(set(columnName, bindMarker(columnName)));
    }
    AtreusMetaSimpleField ttlField = metaEntity.getTtlField();
    if (ttlField != null && withTtl) {
      update.using(ttl(bindMarker(ttlField.getColumn())));
    }
    return update;
  }

  public static RegularStatement deleteEntity(AtreusMetaEntity metaEntity) {
    AtreusMetaTable table = metaEntity.getTable();
    Delete delete = delete().from(table.getKeySpace(), table.getName());
    Delete.Where where = null;
    for (String columnName : listPrimaryKeyColumnNames(metaEntity)) {
      if (where == null) {
        where = delete.where(eq(columnName, bindMarker(columnName)));
        continue;
      }
      where.and(eq(columnName, bindMarker(columnName)));
    }
    return delete;
  }

  public static RegularStatement deleteAllAssociatedEntities(AtreusMetaAssociation metaAssociation) {
    AtreusMetaTable table = metaAssociation.getOutboundTable();
    AtreusMetaEntity ownerEntity = metaAssociation.getOwner().getMetaEntity();
    AtreusMetaField ownerKeyField = metaAssociation.getOwner().getAssociationKeyField();
    Delete delete = delete().from(table.getKeySpace(), table.getName());
    Delete.Where where = null;
    for (String columnName : listColumnNames(ownerKeyField, null)) {
      if (where == null) {
        where = delete.where(eq(columnName, bindMarker(columnName)));
        continue;
      }
      where.and(eq(columnName, bindMarker(columnName)));
    }
    return delete;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private static List<String> listAllColumnNames(AtreusMetaEntity metaEntity) {
    List<String> columnNames = new ArrayList<>();
    listPrimaryKeyColumnNames(metaEntity, columnNames);
    listFieldColumnNames(metaEntity, columnNames);
    return columnNames;
  }

  private static List<String> listFieldColumnNames(AtreusMetaEntity metaEntity) {
    return listFieldColumnNames(metaEntity, null);
  }

  private static List<String> listFieldColumnNames(AtreusMetaEntity metaEntity, List<String> columnNames) {
    if (columnNames == null) {
      columnNames = new ArrayList<>();
    }
    for (AtreusMetaField metaField : metaEntity.getFields()) {
      listColumnNames(metaField, columnNames);
    }
    return columnNames;
  }

  private static List<String> listPrimaryKeyColumnNames(AtreusMetaEntity metaEntity) {
    return listPrimaryKeyColumnNames(metaEntity, null);
  }

  private static List<String> listPrimaryKeyColumnNames(AtreusMetaEntity metaEntity, List<String> columnNames) {
    if (columnNames == null) {
      columnNames = new ArrayList<>();
    }
    listColumnNames(metaEntity.getPrimaryKeyField(), columnNames);
    return columnNames;
  }

  private static List<String> listColumnNames(AtreusMetaField metaField, List<String> columnNames) {
    if (columnNames == null) {
      columnNames = new ArrayList<>();
    }
    if (metaField instanceof AtreusMetaSimpleField) {
      columnNames.add(((AtreusMetaSimpleField) metaField).getColumn());
      return columnNames;
    }
    if (metaField instanceof AtreusMetaComplexField) {
      for (AtreusMetaSimpleField metaSimpleField : ((AtreusMetaComplexField) metaField).getFields()) {
        columnNames.add(metaSimpleField.getColumn());
      }
    }
    return columnNames;
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class