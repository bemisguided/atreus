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
package org.atreus.impl.entities.meta;

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.ext.strategies.AtreusTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Implements a meta field instance for dynamically defined fields.
 *
 * @author Martin Crawford
 */
public class DynamicMetaFieldImpl implements AtreusMetaField, Comparable<DynamicMetaFieldImpl> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(DynamicMetaFieldImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private String column;

  private final String name;

  private final MetaEntityImpl ownerEntity;

  private final Class<?> type;

  private AtreusTypeStrategy typeStrategy;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public DynamicMetaFieldImpl(MetaEntityImpl ownerEntity, String name, Class<?> type) {
    this.ownerEntity = ownerEntity;
    this.name = name;
    this.type = type;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public int compareTo(DynamicMetaFieldImpl o) {
    if (column == null && o.column == null) {
      return 0;
    }
    if (column == null) {
      return -1;
    }
    if (o.column == null) {
      return 1;
    }
    return column.compareTo(o.column);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DynamicMetaFieldImpl that = (DynamicMetaFieldImpl) o;

    if (column != null ? !column.equals(that.column) : that.column != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return column != null ? column.hashCode() : 0;
  }

  @Override
  public String toString() {
    return ownerEntity.getName() + "." + name;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public String getColumn() {
    return column;
  }

  @Override
  public void setColumn(String column) {
    this.column = column;
  }

  @Override
  public Field getJavaField() {
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Object getValue(Object entity) {
    if (!(entity instanceof AtreusManagedEntity)) {
      throw new RuntimeException("Cannot retrieve a dynamic field from a non-managed entity");
    }

    return ((AtreusManagedEntity) entity).getDyanmicFields().get(name);
  }

  @Override
  public void setValue(Object entity, Object value) {
    if (!(entity instanceof AtreusManagedEntity)) {
      throw new RuntimeException("Cannot retrieve a dynamic field from a non-managed entity");
    }
    ((AtreusManagedEntity) entity).getDyanmicFields().put(name, value);
  }

  @Override
  public AtreusMetaEntity getOwnerEntity() {
    return ownerEntity;
  }

  @Override
  public Class<?> getType() {
    return type;
  }

  @Override
  public AtreusTypeStrategy getTypeStrategy() {
    return typeStrategy;
  }

  @Override
  public void setTypeStrategy(AtreusTypeStrategy typeStrategy) {
    this.typeStrategy = typeStrategy;
  }

} // end of class