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
package org.atreus.impl.core.mappings.associations.composite.listeners;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.listeners.AtreusAbstractEntityListener;
import org.atreus.core.ext.listeners.AtreusOnFetchListener;
import org.atreus.core.ext.meta.AtreusAssociationType;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.impl.core.entities.ManagedCollection;
import org.atreus.impl.core.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fetch Composite Association Parent listener.
 *
 * @author Martin Crawford
 */
public class CompositeParentFetchListener extends AtreusAbstractEntityListener implements AtreusOnFetchListener {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(CompositeParentFetchListener.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void acceptAssociation(AtreusSessionExt session, AtreusManagedEntity ownerEntity, AtreusMetaAssociation metaAssociation) {

    // Fetch the associations to this owner
    List<AtreusManagedEntity> associatedEntities = fetchAssociations(session, ownerEntity, metaAssociation, true);

    // Handle populating owner's collection
    if (Collection.class.isAssignableFrom(metaAssociation.getOwner().getAssociationField().getType())) {
      setCollection(ownerEntity, associatedEntities, metaAssociation);
      return;
    }

    // Handle populating owner's individual entity
    setEntity(ownerEntity, associatedEntities, metaAssociation);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  public List<AtreusManagedEntity> fetchAssociations(AtreusSessionExt session, AtreusManagedEntity ownerEntity, AtreusMetaAssociation metaAssociation, boolean outbound) {

    // Execute select against the outbound association table
    AtreusMetaEntity metaAssociationEntity = metaAssociation.getAssociation().getMetaEntity();
    BoundStatement boundStatement = session.prepareQuery(QueryHelper.selectAssociatedEntities(metaAssociation));
    metaAssociation.getOwner().getAssociationKeyField().bindValue(boundStatement, ownerEntity.getPrimaryKey());
    ResultSet resultSet = session.execute(boundStatement);

    // Iterate the results and create managed entities
    List<AtreusManagedEntity> associations = new ArrayList<>(resultSet.all().size());
    for (Row row : resultSet.all()) {

      // Create a new entity instance
      AtreusManagedEntity associatedEntity = session.entityInstance(metaAssociationEntity, null);

      // Assign the primary key
      metaAssociationEntity.getPrimaryKeyField().unbindEntity(row, associatedEntity);

      // For composite associations populate the entity
      if (AtreusAssociationType.COMPOSITE.equals(metaAssociation.getType())) {
        for (AtreusMetaField metaField : metaAssociationEntity.getFields()) {
          metaField.unbindEntity(row, associatedEntity);
        }
      }
      associations.add(associatedEntity);
    }
    return associations;
  }

  @SuppressWarnings("unchecked")
  public void setCollection(AtreusManagedEntity managedEntity, List<AtreusManagedEntity> associatedEntities, AtreusMetaAssociation metaAssociation) {
    AtreusMetaField associationField = metaAssociation.getOwner().getAssociationField();
    ManagedCollection managedCollection = (ManagedCollection) managedEntity.getFieldValue(associationField);
    managedCollection.getUpdatedEntities().addAll(associatedEntities);
    managedCollection.snapshot();
  }

  public void setEntity(AtreusManagedEntity managedEntity, List<AtreusManagedEntity> associatedEntities, AtreusMetaAssociation metaAssociation) {
    AtreusMetaField associationField = metaAssociation.getOwner().getAssociationField();

    // No entities to set
    if (associatedEntities.size() < 1) {
      return;
    }

    // Expect only one entity so this is a data bind exception
    if (associatedEntities.size() > 1) {
      throw new RuntimeException("More entities than expected for this association " + associationField);
    }

    // Set the first list item as the value
    managedEntity.setFieldValue(associationField, associatedEntities.get(0));
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class