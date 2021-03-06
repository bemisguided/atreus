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
package org.atreus.impl.core.mappings.associations.composite.meta;

import java.io.Serializable;

/**
 * Composite Child Primary Key object.
 *
 * @author Martin Crawford
 */
public class CompositeChildPrimaryKey implements Serializable {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final Serializable parentKey;
  private final Serializable childKey;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public CompositeChildPrimaryKey(Serializable parentKey, Serializable childKey) {
    this.parentKey = parentKey;
    this.childKey = childKey;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CompositeChildPrimaryKey that = (CompositeChildPrimaryKey) o;

    if (childKey != null ? !childKey.equals(that.childKey) : that.childKey != null) {
      return false;
    }
    if (parentKey != null ? !parentKey.equals(that.parentKey) : that.parentKey != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = parentKey != null ? parentKey.hashCode() : 0;
    result = 31 * result + (childKey != null ? childKey.hashCode() : 0);
    return result;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public Serializable getParentKey() {
    return parentKey;
  }

  public Serializable getChildKey() {
    return childKey;
  }

} // end of class