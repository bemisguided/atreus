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
package org.atreus.impl.entities.meta.associations.composite;

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.impl.entities.meta.fields.BaseMetaComplexFieldImpl;

import java.io.Serializable;

/**
 * Composite Child Primary Key meta field.
 *
 * @author Martin Crawford
 */
public class CompositeChildPrimaryKeyMetaFieldImpl extends BaseMetaComplexFieldImpl {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final String name;
  private final AtreusMetaSimpleField parentKeyField;
  private final AtreusMetaSimpleField childKeyField;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public CompositeChildPrimaryKeyMetaFieldImpl(AtreusMetaEntity ownerObject, String name, AtreusMetaSimpleField parentKeyField) {
    super(ownerObject);
    this.name = name;
    this.parentKeyField = parentKeyField;
    this.childKeyField = (AtreusMetaSimpleField) ownerObject.getPrimaryKeyField();
    addField(parentKeyField);
    addField(childKeyField);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Object getValue(Object entity) {
    if (entity instanceof AtreusManagedEntity) {
      Serializable parentKey = (Serializable) parentKeyField.getValue(entity);
      Serializable childKey = (Serializable) childKeyField.getValue(entity);
      return new CompositeChildPrimaryKey(parentKey, childKey);
    }
    return childKeyField.getValue(entity);
  }

  @Override
  public void setValue(Object entity, Object value) {
    if (value instanceof CompositeChildPrimaryKey && entity instanceof AtreusManagedEntity) {
      parentKeyField.setValue(entity, ((CompositeChildPrimaryKey) value).getParentKey());
      childKeyField.setValue(entity, ((CompositeChildPrimaryKey) value).getChildKey());
      return;
    }
    childKeyField.setValue(entity, value);
  }

  @Override
  public Class<?> getType() {
    return CompositeChildPrimaryKey.class;
  }

} // end of class