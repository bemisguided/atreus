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
package org.atreus.impl.core.mappings.associations.handlers;

import com.datastax.driver.core.BoundStatement;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.impl.core.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Handles the deletion of an association of the owner entity.
 *
 * @author Martin Crawford
 */
public class AssociationOwnerDeleteHandler {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AssociationOwnerDeleteHandler.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void delete(AtreusSessionExt session, AtreusMetaAssociation metaAssociation, AtreusManagedEntity ownerEntity) {

    // Delete all associations
    deleteAssociations(session, ownerEntity, metaAssociation);

    // Handle populating owner's collection
    AtreusMetaField associationField = metaAssociation.getOwner().getAssociationField();
    if (Collection.class.isAssignableFrom(associationField.getType())) {
      Collection collection = (Collection) associationField.getValue(ownerEntity);
      unmanageCollection(session, collection);
      return;
    }

    // Handle populating owner's individual entity
    Object entity = associationField.getValue(ownerEntity);
    unmanageEntity(session, entity);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void deleteAssociations(AtreusSessionExt session, AtreusManagedEntity ownerEntity, AtreusMetaAssociation metaAssociation) {
    BoundStatement boundStatement = session.prepareQuery(QueryHelper.deleteAllAssociatedEntities(metaAssociation));
    metaAssociation.getOwner().getAssociationKeyField().bindValue(boundStatement, ownerEntity.getPrimaryKey());
    session.executeOrBatch(boundStatement);
  }

  private void unmanageCollection(AtreusSessionExt session, Collection collection) {
    for (Object entity : collection) {
      unmanageEntity(session, entity);
    }
  }

  private void unmanageEntity(AtreusSessionExt session, Object entity) {
    if (entity instanceof AtreusManagedEntity) {
      session.unmanageEntity((AtreusManagedEntity) entity);
    }
  }
  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class