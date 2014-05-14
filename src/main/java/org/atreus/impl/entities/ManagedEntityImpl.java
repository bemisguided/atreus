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

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a managed entity.
 *
 * @author Martin Crawford
 */
public class ManagedEntityImpl implements AtreusManagedEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ManagedEntityImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final Map<String, Object> dynamicFields = new HashMap<>();
  private final Object entity;
  private final AtreusMetaEntity metaEntity;
  private final AtreusSessionExt session;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public ManagedEntityImpl(AtreusSessionExt session, AtreusMetaEntity metaEntity, Object entity) {
    this.session = session;
    this.entity = entity;
    this.metaEntity = metaEntity;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public Map<String, Object> getDynamicFields() {
    return dynamicFields;
  }

  @Override
  public Object getEntity() {
    return entity;
  }

  @Override
  public Object getFieldValue(AtreusMetaField metaField) {
    return metaField.getValue(this);
  }

  @Override
  public void setFieldValue(AtreusMetaField metaField, Object value) {
    metaField.setValue(this, value);
  }

  @Override
  public AtreusMetaEntity getMetaEntity() {
    return metaEntity;
  }

  @Override
  public Serializable getPrimaryKey() {
    return (Serializable) getMetaEntity().getPrimaryKeyField().getValue(entity);
  }

  @Override
  public boolean isUpdated() {
    return true;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class