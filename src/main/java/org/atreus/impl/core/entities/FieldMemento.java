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
package org.atreus.impl.core.entities;

import org.atreus.core.ext.meta.AtreusMetaField;

/**
 * Field Memento bean.
 *
 * @author Martin Crawford
 */
public class FieldMemento {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusMetaField metaField;
  private FieldFetchState fetchState;
  private Object snapshot;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public FieldMemento(AtreusMetaField metaField) {
    this.metaField = metaField;
    this.fetchState = FieldFetchState.UNITIALIZED;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void snapshot(Object value) {
    fetchState = FieldFetchState.INITIALIZED;
    snapshot = value;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public AtreusMetaField getMetaField() {
    return metaField;
  }

  public FieldFetchState getFetchState() {
    return fetchState;
  }

  public Object getSnapshot() {
    return snapshot;
  }

} // end of class