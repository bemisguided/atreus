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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.RegularStatement;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.core.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the saving of an entity.
 *
 * @author Martin Crawford
 */
public class EntitySaveHandler extends BaseEntityHandler {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(EntitySaveHandler.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void save(AtreusSessionExt session, AtreusManagedEntity managedEntity) {
    // TODO enable lightweight transaction as configuration option to ensure entity does not already exists
    AtreusMetaEntity metaEntity = managedEntity.getMetaEntity();
    boolean hasTtl = hasTtl(managedEntity);
    RegularStatement regularStatement = QueryHelper.insertEntity(metaEntity, hasTtl);
    BoundStatement boundStatement = session.prepareQuery(regularStatement);
    bindEntityAll(boundStatement, managedEntity);
    session.executeOrBatch(boundStatement);
    managedEntity.snapshot();
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class