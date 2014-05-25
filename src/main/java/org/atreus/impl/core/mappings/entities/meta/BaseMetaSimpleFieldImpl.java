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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.meta.AtreusMetaObject;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.core.ext.strategies.AtreusTypeStrategy;
import org.atreus.impl.util.AssertUtils;

/**
 * Base implementation of a meta field.
 *
 * @author Martin Crawford
 */
public abstract class BaseMetaSimpleFieldImpl extends BaseMetaFieldImpl implements AtreusMetaSimpleField {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private String column;

  private AtreusTypeStrategy typeStrategy;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  protected BaseMetaSimpleFieldImpl(AtreusMetaObject ownerObject) {
    super(ownerObject);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  @SuppressWarnings("unchecked")
  public void bindEntity(BoundStatement boundStatement, Object entity) {
    AssertUtils.notNull(getTypeStrategy(), "typeStrategy not set");
    Object value = getValue(entity);
    getTypeStrategy().bindValue(boundStatement, column, value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void bindValue(BoundStatement boundStatement, Object value) {
    AssertUtils.notNull(getTypeStrategy(), "typeStrategy not set");
    getTypeStrategy().bindValue(boundStatement, column, value);
  }

  @Override
  public void unbindEntity(Row row, Object entity) {
    AssertUtils.notNull(getTypeStrategy(), "typeStrategy not set");
    Object value = getTypeStrategy().unbindValue(row, column);
    setValue(entity, value);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public final String getColumn() {
    return column;
  }

  public final void setColumn(String column) {
    this.column = column;
  }

  @Override
  public final AtreusTypeStrategy getTypeStrategy() {
    return typeStrategy;
  }

  @Override
  public final void setTypeStrategy(AtreusTypeStrategy typeStrategy) {
    this.typeStrategy = typeStrategy;
  }

} // end of class