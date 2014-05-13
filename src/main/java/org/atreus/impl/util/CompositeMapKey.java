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
package org.atreus.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Utility composite key for use with java.util.Map.
 *
 * @author Martin Crawford
 */
public class CompositeMapKey {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(CompositeMapKey.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Object[] keys;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public CompositeMapKey(Object... keys) {
    this.keys = keys;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public int hashCode() {
    int result = 0;
    for (Object keyValue : keys) {
      if (keyValue != null) {
        result += keyValue.hashCode();
      }
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CompositeMapKey)) {
      return false;
    }
    CompositeMapKey that = (CompositeMapKey) obj;
    return Arrays.equals(keys, that.keys);
  }

  @Override
  public String toString() {
    return "CompositeKey{" + Arrays.toString(keys) + '}';
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class