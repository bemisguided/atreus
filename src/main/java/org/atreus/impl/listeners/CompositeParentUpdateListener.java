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
package org.atreus.impl.listeners;

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.listeners.AtreusAbstractEntityListener;
import org.atreus.core.ext.listeners.AtreusOnSaveListener;
import org.atreus.core.ext.listeners.AtreusOnUpdateListener;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.impl.entities.ManagedCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;

/**
 * Update Composite Association Parent listener.
 *
 * @author Martin Crawford
 */
public class CompositeParentUpdateListener extends AtreusAbstractEntityListener implements AtreusOnSaveListener, AtreusOnUpdateListener {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(CompositeParentUpdateListener.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void acceptAssociation(AtreusSessionExt session, AtreusManagedEntity parentEntity, AtreusMetaAssociation metaAssociation) {
    AtreusMetaField ownerField = metaAssociation.getOwner().getAssociationField();
    Class<?> parentFieldType = ownerField.getType();
    if (Collection.class.isAssignableFrom(parentFieldType)) {
      Collection collection = (Collection) ownerField.getValue(parentEntity);
      updateCollection(session, metaAssociation, parentEntity, collection);
    }
    else {
      Object childEntity = ownerField.getValue(parentEntity);
      updateEntity(session, metaAssociation, parentEntity, childEntity);
      parentEntity.setFieldValue(metaAssociation.getOwner().getAssociationField(), childEntity);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void updateCollection(AtreusSessionExt session, AtreusMetaAssociation metaAssociation, AtreusManagedEntity parentEntity, Collection collection) {
    if (collection instanceof ManagedCollection) {
      updateManagedCollection(session, metaAssociation, parentEntity, (ManagedCollection) collection);
      return;
    }
    // TODO handle case where collection isn't a Managed Collection (edge case but possible)
  }

  private void updateManagedCollection(AtreusSessionExt session, AtreusMetaAssociation metaAssociation, AtreusManagedEntity parentEntity, ManagedCollection managedCollection) {

    for (Object addedEntity : managedCollection.getAddedEntities()) {
      LOG.debug("addedEntity {}", addedEntity);
      updateEntity(session, metaAssociation, parentEntity, addedEntity);
    }

    for (Object removedEntity : managedCollection.getRemovedEntities()) {
      LOG.debug("removedEntity {}", removedEntity);
      session.delete(removedEntity);
    }

    for (Object updatedEntity : managedCollection.getUpdatedEntities()) {
      LOG.debug("updatedEntity {}", updatedEntity);
      updateEntity(session, metaAssociation, parentEntity, updatedEntity);
    }

  }

  private AtreusManagedEntity updateEntity(AtreusSessionExt session, AtreusMetaAssociation metaAssociation, AtreusManagedEntity parentEntity, Object entity) {
    AtreusManagedEntity childEntity = session.manageEntity(entity);
    if (!childEntity.isUpdated()) {
      return childEntity;
    }
    AtreusMetaSimpleField parentKeyField = (AtreusMetaSimpleField) metaAssociation.getOwner().getAssociationKeyField();
    Serializable childParentKeyValue = (Serializable) childEntity.getFieldValue(parentKeyField);
    Serializable parentPrimaryKey = parentEntity.getPrimaryKey();
    if (!parentPrimaryKey.equals(childParentKeyValue)) {
      childEntity.setFieldValue(parentKeyField, parentPrimaryKey);
    }
    return session.save(childEntity);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class