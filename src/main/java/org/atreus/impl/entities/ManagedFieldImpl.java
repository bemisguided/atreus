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

import org.atreus.core.ext.AtreusTypeAccessor;
import org.atreus.core.ext.entities.AtreusManagedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Managed Field bean.
 *
 * @author Martin Crawford
 */
public class ManagedFieldImpl implements AtreusManagedField, Comparable<ManagedFieldImpl> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ManagedFieldImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private String column;

  private Field javaField;

  private AtreusTypeAccessor typeAccessor;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods


  @Override
  public int compareTo(ManagedFieldImpl o) {
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

    ManagedFieldImpl that = (ManagedFieldImpl) o;

    if (column != null ? !column.equals(that.column) : that.column != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return column != null ? column.hashCode() : 0;
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
    return javaField;
  }

  public void setJavaField(Field javaField) {
    this.javaField = javaField;
  }

  @Override
  public AtreusTypeAccessor getTypeAccessor() {
    return typeAccessor;
  }

  @Override
  public void setTypeAccessor(AtreusTypeAccessor typeAccessor) {
    this.typeAccessor = typeAccessor;
  }

} // end of class