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
package org.atreus.impl.core.mappings.entities.listeners;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.RegularStatement;
import org.atreus.core.AtreusDataBindingException;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.listeners.AtreusAbstractEntityListener;
import org.atreus.core.ext.listeners.AtreusOnSaveListener;
import org.atreus.core.ext.listeners.AtreusOnUpdateListener;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.impl.core.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Update Entity visitor.
 *
 * @author Martin Crawford
 */
public class EntityUpdateListener extends AtreusAbstractEntityListener implements AtreusOnSaveListener, AtreusOnUpdateListener {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(EntityUpdateListener.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void acceptEntity(AtreusSessionExt session, AtreusManagedEntity managedEntity) {
    AtreusMetaEntity metaEntity = managedEntity.getMetaEntity();
    AtreusMetaField primaryKeyMetaField = metaEntity.getPrimaryKeyField();

    boolean hasTtl = false;
    AtreusMetaField ttlMetaField = metaEntity.getTtlField();
    if (ttlMetaField != null && managedEntity.getFieldValue(ttlMetaField) != null) {
      hasTtl = true;
    }
    RegularStatement regularStatement = QueryHelper.insertEntity(metaEntity, hasTtl);
    BoundStatement boundStatement = session.prepareQuery(regularStatement);
    bindFromField(primaryKeyMetaField, managedEntity, boundStatement);
    for (AtreusMetaField managedField : metaEntity.getFields()) {
      bindFromField(managedField, managedEntity, boundStatement);
    }

    bindFromEntityTtl(metaEntity, managedEntity, boundStatement);
    session.executeOrBatch(boundStatement);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  @SuppressWarnings("unchecked")
  private void bindFromField(AtreusMetaField metaField, AtreusManagedEntity managedEntity, BoundStatement boundStatement) {
    metaField.bindEntity(boundStatement, managedEntity);
  }

  @SuppressWarnings("unchecked")
  private void bindFromEntityTtl(AtreusMetaEntity metaEntity, AtreusManagedEntity managedEntity, BoundStatement boundStatement) {
    AtreusMetaField ttlMetaField = metaEntity.getTtlField();
    if (ttlMetaField == null) {
      return;
    }
    Object value = managedEntity.getFieldValue(ttlMetaField);
    if (value == null) {
      return;
    }

    Integer ttlValue = metaEntity.getTtlStrategy().translate(new Date(), value);
    if (ttlValue == null || ttlValue < 1) {
      throw new AtreusDataBindingException(AtreusDataBindingException.ERROR_CODE_INVALID_TIME_TO_LIVE_VALUE, ttlMetaField, ttlValue);
    }
    ttlMetaField.bindValue(boundStatement, ttlValue);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class