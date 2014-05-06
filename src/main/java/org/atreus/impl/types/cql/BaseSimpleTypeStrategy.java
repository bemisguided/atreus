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
package org.atreus.impl.types.cql;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusTypeStrategy;
import org.atreus.core.ext.CQLDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for Collection Type Strategies.
 *
 * @author Martin Crawford
 */
public abstract class BaseSimpleTypeStrategy<T> implements AtreusTypeStrategy<T> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseSimpleTypeStrategy.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Class<?> valueClass;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public final T get(Row row, String colName) {
    if (row.isNull(colName)){
      return null;
    }
    return doGet(row, colName);
  }

  @Override
  public Class<?> getValueClass() {
    return valueClass;
  }

  @Override
  public final void set(BoundStatement boundStatement, String colName, T value) {
    if (value == null) {
      return;
    }
    doSet(boundStatement, colName, value);
  }

  @Override
  public void setValueClass(Class<?> valueClass) {
    this.valueClass = valueClass;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected abstract T doGet(Row row, String colName);

  protected abstract void doSet(BoundStatement boundStatement, String colName, T value);

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class