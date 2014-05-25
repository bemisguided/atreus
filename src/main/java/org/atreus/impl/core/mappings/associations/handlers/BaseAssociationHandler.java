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
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusAssociationType;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.impl.core.entities.ManagedCollection;
import org.atreus.impl.core.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A base association handler.
 *
 * @author Martin Crawford
 */
public class BaseAssociationHandler {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseAssociationHandler.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected List<AtreusManagedEntity> fetchAssociations(AtreusSessionExt session, AtreusManagedEntity ownerEntity, AtreusMetaAssociation metaAssociation, boolean outbound) {

    // Execute select against the outbound association table
    AtreusMetaEntity metaAssociationEntity = metaAssociation.getAssociation().getMetaEntity();
    BoundStatement boundStatement = session.prepareQuery(QueryHelper.selectAssociatedEntities(metaAssociation));
    Serializable primaryKey = ownerEntity.getPrimaryKey();
    metaAssociation.getOwner().getAssociationKeyField().bindValue(boundStatement, primaryKey);
    ResultSet resultSet = session.execute(boundStatement);

    // Iterate the results and create managed entities
    List<Row> rows = resultSet.all();
    List<AtreusManagedEntity> associations = new ArrayList<>(rows.size());
    for (Row row : rows) {

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

      // TODO add fetch for aggregate associations here
      associations.add(associatedEntity);
    }
    return associations;
  }

  @SuppressWarnings("unchecked")
  protected void setCollection(AtreusManagedEntity managedEntity, List<AtreusManagedEntity> associatedEntities, AtreusMetaAssociation metaAssociation) {
    AtreusMetaField associationField = metaAssociation.getOwner().getAssociationField();
    Collection collection = (Collection) managedEntity.getFieldValue(associationField);
    if (collection instanceof ManagedCollection) {
      ManagedCollection managedCollection = (ManagedCollection) collection;
      managedCollection.snapshot(associatedEntities);
      managedEntity.setFieldValue(associationField, managedCollection);
      return;
    }
    collection.addAll(associatedEntities);
  }

  protected void setEntity(AtreusManagedEntity managedEntity, List<AtreusManagedEntity> associatedEntities, AtreusMetaAssociation metaAssociation) {
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

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class