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
import org.atreus.core.ext.meta.AtreusMetaComposite;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.impl.entities.collections.ManagedCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Update Composite Parent Association Entity listener.
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
  public void acceptCompositeAssociation(AtreusSessionExt session, AtreusManagedEntity parentEntity, AtreusMetaComposite metaComposite) {
    Class<?> parentFieldType = metaComposite.getOwnerField().getType();
    if (Collection.class.isAssignableFrom(parentFieldType)) {
      Collection collection = (Collection) metaComposite.getOwnerField().getValue(parentEntity);
      updateCollection(session, metaComposite, parentEntity, collection);
    }
    else if (Map.class.isAssignableFrom(parentFieldType)) {
      // do maps
    }
    else {
      Object childEntity = metaComposite.getOwnerField().getValue(parentEntity);
      updateEntity(session, metaComposite, parentEntity, childEntity);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void updateCollection(AtreusSessionExt session, AtreusMetaComposite metaComposite, AtreusManagedEntity parentEntity, Collection collection) {
    if (collection instanceof ManagedCollection) {
      updateManagedCollection(session, metaComposite, parentEntity, (ManagedCollection) collection);
      return;
    }
    // TODO delete and update all
  }

  private void updateManagedCollection(AtreusSessionExt session, AtreusMetaComposite metaComposite, AtreusManagedEntity parentEntity, ManagedCollection managedCollection) {

    for (Object addedEntity : managedCollection.getAddedEntities()) {
      session.save(addedEntity);
    }

    for (Object removedEntity : managedCollection.getRemovedEntities()) {
      // TODO do delete
    }

    for (Object entity : managedCollection.getCollection()) {
      updateEntity(session, metaComposite, parentEntity, entity);
    }

  }

  private void updateEntity(AtreusSessionExt session, AtreusMetaComposite metaComposite, AtreusManagedEntity parentEntity, Object entity) {
    AtreusManagedEntity childEntity = session.manageEntity(entity);
    if (!childEntity.isUpdated()) {
      return;
    }
    AtreusMetaSimpleField parentKeyField = metaComposite.getAssociatedEntityParentKeyField();
    Serializable childParentKeyValue = (Serializable) childEntity.getFieldValue(parentKeyField);
    Serializable parentPrimaryKey = parentEntity.getPrimaryKey();
    if (!parentPrimaryKey.equals(childParentKeyValue)) {
      childEntity.setFieldValue(parentKeyField, parentPrimaryKey);
    }
    session.manageEntity(childEntity);
    parentEntity.setFieldValue(metaComposite.getOwnerField(), childEntity);
    session.save(childEntity);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class