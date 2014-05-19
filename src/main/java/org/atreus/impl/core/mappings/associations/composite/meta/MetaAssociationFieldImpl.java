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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.core.ext.meta.AtreusMetaAssociationField;
import org.atreus.core.ext.meta.AtreusMetaObject;
import org.atreus.impl.core.mappings.entities.meta.MetaEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Implements a meta association field instance for a statically defined field.
 *
 * @author Martin Crawford
 */
public class MetaAssociationFieldImpl implements AtreusMetaAssociationField {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(MetaAssociationFieldImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusMetaObject ownerObject;
  private final Field javaField;
  private final AtreusMetaAssociation metaAssociation;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public MetaAssociationFieldImpl(MetaEntityImpl ownerObject, Field javaField, AtreusMetaAssociation metaAssociation) {
    this.ownerObject = ownerObject;
    this.javaField = javaField;
    this.metaAssociation = metaAssociation;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public AtreusMetaAssociation getAssociation() {
    return metaAssociation;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public void bindEntity(BoundStatement boundStatement, Object entity) {

  }

  @Override
  public void bindValue(BoundStatement boundStatement, Object value) {

  }

  @Override
  public String getName() {
    return javaField.getName();
  }

  @Override
  public Object getValue(Object entity) {
    // First check if the entity is a managed entity and if so get the underlining object entity
    if (entity instanceof AtreusManagedEntity) {
      entity = ((AtreusManagedEntity) entity).getEntity();
    }
    try {
      // Otherwise extract the value using java reflection directly
      return javaField.get(entity);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setValue(Object entity, Object value) {
    // First check if the entity is a managed entity and if so get the underlining object entity
    if (entity instanceof AtreusManagedEntity) {
      entity = ((AtreusManagedEntity) entity).getEntity();
    }
    try {
      // Otherwise bindValue the value using java reflection directly
      javaField.set(entity, value);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public AtreusMetaObject getOwnerObject() {
    return ownerObject;
  }

  @Override
  public Class<?> getType() {
    return javaField.getType();
  }

  @Override
  public void unbindEntity(Row row, Object entity) {

  }

} // end of class