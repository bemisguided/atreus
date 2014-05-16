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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.listeners.AtreusAbstractEntityListener;
import org.atreus.core.ext.listeners.AtreusOnFetchListener;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.impl.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Update Composite Parent Association Entity fetch listener.
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
  public void acceptAssociation(AtreusSessionExt session, AtreusManagedEntity parentEntity, AtreusMetaAssociation metaAssociation) {
    // TODO rough and incomplete implementation
    BoundStatement boundStatement = session.prepareQuery(QueryHelper.selectAssociatedEntities(metaAssociation));
    metaAssociation.getOwner().getAssociationKeyField().bindValue(boundStatement, parentEntity.getPrimaryKey());
    ResultSet resultSet = session.execute(boundStatement);
    Row row = resultSet.one();
    try {
      Object entity = metaAssociation.getAssociation().getMetaEntity().getEntityType().newInstance();
      metaAssociation.getAssociation().getAssociationKeyField().unbindEntity(row, entity);
      for (AtreusMetaField metaField : metaAssociation.getAssociation().getMetaEntity().getFields()) {
        metaField.unbindEntity(row, entity);
      }
      AtreusManagedEntity childEntity = session.manageEntity(entity);
      metaAssociation.getOwner().getAssociationKeyField().setValue(parentEntity, childEntity);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class