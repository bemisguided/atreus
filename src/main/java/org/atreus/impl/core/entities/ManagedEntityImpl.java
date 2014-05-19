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

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusMetaAssociationField;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.impl.core.mappings.entities.meta.DynamicMetaSimpleFieldImpl;
import org.atreus.impl.util.MetaFieldIteratorUtils;
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
  private boolean fetched;
  private final AtreusMetaEntity metaEntity;
  private final AtreusSessionExt session;
  private final Map<AtreusMetaField, FieldMemento> mementoByField = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public ManagedEntityImpl(AtreusSessionExt session, AtreusMetaEntity metaEntity, Object entity) {
    this.session = session;
    this.entity = entity;
    this.metaEntity = metaEntity;
    init();
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void baseline() {
    for(AtreusMetaField metaField : mementoByField.keySet()) {
      FieldMemento fieldMemento = mementoByField.get(metaField);
      fieldMemento.baseline(getFieldValue(metaField));
    }
  }

  @Override
  public void fetchField(AtreusMetaField metaField) {
    if (metaField instanceof AtreusMetaAssociationField) {
      // TODO something else
      return;
    }
    session.fetch(this);
  }

  @Override
  public boolean isFetched(AtreusMetaField metaField) {
    FieldMemento fieldMemento = mementoByField.get(metaField);
    return FieldFetchState.INITIALIZED.equals(fieldMemento.getFetchState());
  }

  @Override
  public Object getFieldValue(AtreusMetaField metaField) {
    if (metaField instanceof DynamicMetaSimpleFieldImpl) {
      return metaField.getValue(this);
    }
    return metaField.getValue(entity);
  }

  @Override
  public void setFieldValue(AtreusMetaField metaField, Object value) {
    FieldMemento fieldMemento = mementoByField.get(metaField);
    if (fieldMemento != null) {
      fieldMemento.baseline(value);
    }
    if (metaField instanceof DynamicMetaSimpleFieldImpl) {
      metaField.setValue(this, value);
      return;
    }
    metaField.setValue(entity, value);
  }

  @Override
  public boolean isUpdated() {
    return true;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void init() {
    for (AtreusMetaField metaField : MetaFieldIteratorUtils.iterateMetaFields(metaEntity.getPrimaryKeyField())) {
      createFieldMemento(metaField);
    }
    for (AtreusMetaField metaField : MetaFieldIteratorUtils.iterateMetaFields(metaEntity.getFields())) {
      createFieldMemento(metaField);
    }
  }

  private void createFieldMemento(AtreusMetaField metaField) {
    FieldMemento fieldMemento = new FieldMemento(metaField);
    mementoByField.put(metaField, fieldMemento);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public Map<String, Object> getDynamicFields() {
    return dynamicFields;
  }

  @Override
  public Object getEntity() {
    return entity;
  }

  @Override
  public AtreusMetaEntity getMetaEntity() {
    return metaEntity;
  }

  @Override
  public Serializable getPrimaryKey() {
    return (Serializable) getMetaEntity().getPrimaryKeyField().getValue(entity);
  }

} // end of class