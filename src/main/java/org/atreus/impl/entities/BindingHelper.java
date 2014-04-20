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
import org.atreus.core.ext.entities.AtreusManagedEntity;
import org.atreus.core.ext.entities.AtreusManagedField;
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

  public static void bindToEntity(AtreusManagedEntity managedEntity, Object entity, Row row) {
    for (AtreusManagedField managedField : managedEntity.getPrimaryKey()) {
      bindToField(managedField, entity, row);
    }
    for (AtreusManagedField managedField : managedEntity.getFields()) {
      bindToField(managedField, entity, row);
    }
  }

  public static void bindToField(AtreusManagedField managedField, Object entity, Row row) {
    Object value = managedField.getTypeAccessor().get(row, managedField.getColumn());
    try {
      managedField.getJavaField().set(entity, value);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void bindFromPrimaryKeys(AtreusManagedEntity managedEntity, BoundStatement boundStatement, Serializable... primaryKeys) {
    int index = 0;
    for (AtreusManagedField managedField : managedEntity.getPrimaryKey()) {
      managedField.getTypeAccessor().set(boundStatement, managedField.getColumn(), primaryKeys[index]);
    }
  }

  public static void bindFromEntity(AtreusManagedEntity managedEntity, Object entity, BoundStatement boundStatement) {
    for (AtreusManagedField managedField : managedEntity.getPrimaryKey()) {
      bindFromField(managedField, entity, boundStatement);
    }
    for (AtreusManagedField managedField : managedEntity.getFields()) {
      bindFromField(managedField, entity, boundStatement);
    }
  }

  public static void bindFromField(AtreusManagedField managedField, Object object, BoundStatement boundStatement) {
    try {
      Object value = managedField.getJavaField().get(object);
      managedField.getTypeAccessor().set(boundStatement, managedField.getColumn(), value);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class