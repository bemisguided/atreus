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
package org.atreus.impl.core.mappings.entities.handlers;

import com.datastax.driver.core.RegularStatement;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.impl.core.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static org.atreus.impl.util.MetaFieldIteratorUtils.iterateMetaSimpleFields;

/**
 * Handles the update of an entity.
 *
 * @author Martin Crawford
 */
public class EntityUpdateHandler extends BaseEntityHandler {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(EntityUpdateHandler.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void update(AtreusSessionExt session, AtreusManagedEntity managedEntity) {
    // TODO enable lightweight transaction as configuration option to ensure entity already exists
    AtreusMetaEntity metaEntity = managedEntity.getMetaEntity();
    boolean hasTtl = hasTtl(managedEntity);

    // Check if there are any fields to be updated
    Collection<AtreusMetaSimpleField> updatedFields = managedEntity.getUpdatedFields();
    if (updatedFields.isEmpty()) {

      if (!hasTtl) {
        // Skip out if there are not fields to update and the Ttl is not set
        return;
      }

      // If ttl is set then all fields must be updated, including the primary key
      updatedFields.addAll(iterateMetaSimpleFields(metaEntity.getPrimaryKeyField()));
      updatedFields.addAll(iterateMetaSimpleFields(metaEntity.getFields()));
    }
    RegularStatement regularStatement = QueryHelper.updateEntity(metaEntity, updatedFields, hasTtl);
    bindAndExecute(session, managedEntity, regularStatement, updatedFields);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class