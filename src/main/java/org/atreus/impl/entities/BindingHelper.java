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
package org.atreus.impl.entities;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Helper class to bind data to and from Cassandra rows and statements.
 *
 * @author Martin Crawford
 */
public class BindingHelper {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BindingHelper.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public static void bindToEntity(AtreusMetaEntity managedEntity, Object entity, Row row) {
    bindToField(managedEntity.getPrimaryKeyField(), entity, row);
    for (AtreusMetaField managedField : managedEntity.getFields()) {
      bindToField(managedField, entity, row);
    }
  }

  public static void bindToField(AtreusMetaField metaField, Object entity, Row row) {
    Object value = metaField.getTypeStrategy().get(row, metaField.getColumn());
    metaField.setValue(entity, value);
  }

  public static boolean isNull(AtreusMetaField metaField, Object entity) {
    return metaField.getValue(entity) == null;
  }

  public static void bindFromPrimaryKeys(AtreusMetaEntity managedEntity, BoundStatement boundStatement, Serializable primaryKey) {
    AtreusMetaField managedField = managedEntity.getPrimaryKeyField();
    managedField.getTypeStrategy().set(boundStatement, managedField.getColumn(), primaryKey);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class