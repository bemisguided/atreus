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

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusAssociationType;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.core.mappings.associations.composite.handlers.CompositeParentFetchHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the fetching of a generic association.
 *
 * @author Martin Crawford
 */
public class AssociationFetchHandler {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AssociationFetchHandler.class);

  private static final CompositeParentFetchHandler COMPOSITE_PARENT_FETCH_HANDLER = new CompositeParentFetchHandler();

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void fetch(AtreusSessionExt session, AtreusMetaAssociation metaAssociation, AtreusManagedEntity managedEntity) {
    AtreusMetaEntity metaEntity = managedEntity.getMetaEntity();
    if (AtreusAssociationType.COMPOSITE.equals(metaAssociation.getType())) {
      if (metaEntity.equals(metaAssociation.getOwner().getMetaEntity())) {
        COMPOSITE_PARENT_FETCH_HANDLER.fetch(session, metaAssociation, managedEntity);
        return;
      }
      // TODO Child fetch
    }

    // TODO aggregate and aggregate reference fetch
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class