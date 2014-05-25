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
package org.atreus.impl.core.mappings.entities.meta;

import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.ext.meta.AtreusMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base meta field.
 *
 * @author Martin Crawford
 */
public abstract class BaseMetaFieldImpl implements AtreusMetaField, Comparable<BaseMetaFieldImpl> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseMetaFieldImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusMetaObject ownerObject;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  protected BaseMetaFieldImpl(AtreusMetaObject ownerObject) {
    this.ownerObject = ownerObject;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public int compareTo(BaseMetaFieldImpl o) {
    if (getCanonicalName() == null && o.getCanonicalName() == null) {
      return 0;
    }
    if (getCanonicalName() == null) {
      return -1;
    }
    if (o.getCanonicalName() == null) {
      return 1;
    }
    return getCanonicalName().compareTo(o.getCanonicalName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BaseMetaFieldImpl that = (BaseMetaFieldImpl) o;

    if (ownerObject != null ? !ownerObject.equals(that.ownerObject) : that.ownerObject != null) {
      return false;
    }

    if (getCanonicalName() != null ? !getCanonicalName().equals(that.getCanonicalName()) : that.getCanonicalName() != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return getCanonicalName() != null ? getCanonicalName().hashCode() : 0;
  }

  @Override
  public String toString() {
    return ownerObject.getName() + "." + getCanonicalName();
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected abstract String getCanonicalName();

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public AtreusMetaObject getOwnerObject() {
    return ownerObject;
  }

} // end of class