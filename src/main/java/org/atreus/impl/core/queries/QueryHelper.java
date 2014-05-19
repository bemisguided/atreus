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
package org.atreus.impl.core.queries;

import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import org.atreus.core.ext.meta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;
import static org.atreus.impl.util.MetaFieldIteratorUtils.iterateMetaSimpleFields;

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
    for (AtreusMetaSimpleField metaSimpleField : iterateMetaSimpleFields(metaEntity.getPrimaryKeyField())) {
      String columnName = metaSimpleField.getColumn();
      insert.value(columnName, bindMarker(columnName));
    }
    for (AtreusMetaSimpleField metaSimpleField : iterateMetaSimpleFields(metaEntity.getFields())) {
      String columnName = metaSimpleField.getColumn();
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
    for (AtreusMetaSimpleField metaSimpleField : iterateMetaSimpleFields(metaEntity.getPrimaryKeyField())) {
      String columnName = metaSimpleField.getColumn();
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
    for (AtreusMetaSimpleField metaSimpleField : iterateMetaSimpleFields(metaAssociation.getOwner().getAssociationKeyField())) {
      String columnName = metaSimpleField.getColumn();
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
    return updateEntity(metaEntity, iterateMetaSimpleFields(metaEntity.getFields()), withTtl);
  }

  public static RegularStatement updateEntity(AtreusMetaEntity metaEntity, Collection<AtreusMetaSimpleField> updatedFields, boolean withTtl) {
    AtreusMetaTable table = metaEntity.getTable();
    Update update = update(table.getKeySpace(), table.getName());
    Update.Where where = null;
    for (AtreusMetaSimpleField metaSimpleField : iterateMetaSimpleFields(metaEntity.getPrimaryKeyField())) {
      String columnName = metaSimpleField.getColumn();
      if (where == null) {
        where = update.where(eq(columnName, bindMarker(columnName)));
        continue;
      }
      where.and(eq(columnName, bindMarker(columnName)));
    }
    for (AtreusMetaSimpleField metaSimpleField : updatedFields) {
      String columnName = metaSimpleField.getColumn();
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
    for (AtreusMetaSimpleField metaSimpleField : iterateMetaSimpleFields(metaEntity.getPrimaryKeyField())) {
      String columnName = metaSimpleField.getColumn();
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
    AtreusMetaField ownerKeyField = metaAssociation.getOwner().getAssociationKeyField();
    Delete delete = delete().from(table.getKeySpace(), table.getName());
    Delete.Where where = null;
    for (AtreusMetaSimpleField metaSimpleField : iterateMetaSimpleFields(ownerKeyField)) {
      String columnName = metaSimpleField.getColumn();
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

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class