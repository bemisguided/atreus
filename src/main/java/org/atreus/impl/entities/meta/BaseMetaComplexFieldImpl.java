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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.meta.AtreusMetaComplexField;
import org.atreus.core.ext.meta.AtreusMetaObject;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base meta complex field.
 *
 * @author Martin Crawford
 */
public abstract class BaseMetaComplexFieldImpl implements AtreusMetaComplexField {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseMetaComplexFieldImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusMetaObject ownerObject;
  private Map<String, AtreusMetaSimpleField> fields = new LinkedHashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  protected BaseMetaComplexFieldImpl(AtreusMetaObject ownerObject) {
    this.ownerObject = ownerObject;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addField(AtreusMetaSimpleField metaSimpleField) {
    fields.put(metaSimpleField.getName(), metaSimpleField);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public final AtreusMetaSimpleField[] getFields() {
    AtreusMetaSimpleField[] result = new AtreusMetaSimpleField[fields.size()];
    return fields.values().toArray(result);
  }

  @Override
  public final void bindEntity(BoundStatement boundStatement, Object entity) {
    for (AtreusMetaSimpleField metaSimpleField : fields.values()) {
      metaSimpleField.bindEntity(boundStatement, entity);
    }
  }

  @Override
  public final void bindValue(BoundStatement boundStatement, Object value) {
     // TODO figure this one out
  }

  @Override
  public final AtreusMetaObject getOwnerObject() {
    return ownerObject;
  }

  @Override
  public final void unbindEntity(Row row, Object entity) {
    for (AtreusMetaSimpleField metaSimpleField : fields.values()) {
      metaSimpleField.unbindEntity(row, entity);
    }
  }

} // end of class