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

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.listeners.AtreusAbstractEntityListener;
import org.atreus.core.ext.listeners.AtreusOnSaveListener;
import org.atreus.core.ext.listeners.AtreusOnUpdateListener;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.impl.core.mappings.associations.composite.handlers.CompositeParentUpdateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Update Composite Association Parent listener.
 *
 * @author Martin Crawford
 */
public class CompositeParentUpdateListener extends AtreusAbstractEntityListener implements AtreusOnSaveListener, AtreusOnUpdateListener {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(CompositeParentUpdateListener.class);

  private static final CompositeParentUpdateHandler COMPOSITE_PARENT_UPDATE_HANDLER = new CompositeParentUpdateHandler();

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void acceptAssociation(AtreusSessionExt session, AtreusManagedEntity parentEntity, AtreusMetaAssociation metaAssociation) {
    COMPOSITE_PARENT_UPDATE_HANDLER.update(session, metaAssociation, parentEntity);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class