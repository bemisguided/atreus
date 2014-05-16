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
package org.atreus.impl.entities.meta.associations;

import org.atreus.core.ext.meta.AtreusAssociationType;
import org.atreus.core.ext.meta.AtreusMetaAssociatedEntity;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.core.ext.meta.AtreusMetaTable;
import org.atreus.impl.entities.meta.MetaTableImpl;

/**
 * Meta association bean.
 *
 * @author Martin Crawford
 */
public class MetaAssociationImpl implements AtreusMetaAssociation {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private AtreusMetaAssociatedEntity association = new MetaAssociatedEntityImpl();
  private AtreusMetaTable inboundTable;
  private AtreusMetaTable outboundTable = new MetaTableImpl();
  private AtreusMetaAssociatedEntity owner = new MetaAssociatedEntityImpl();
  private AtreusAssociationType type;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters


  @Override
  public AtreusMetaAssociatedEntity getAssociation() {
    return association;
  }

  @Override
  public boolean isNavigable() {
    return inboundTable != null;
  }

  @Override
  public AtreusMetaTable getInboundTable() {
    return inboundTable;
  }

  public void setInboundTable(AtreusMetaTable inboundTable) {
    this.inboundTable = inboundTable;
  }

  @Override
  public AtreusMetaTable getOutboundTable() {
    return outboundTable;
  }

  @Override
  public AtreusMetaAssociatedEntity getOwner() {
    return owner;
  }

  @Override
  public AtreusAssociationType getType() {
    return type;
  }

  public void setType(AtreusAssociationType type) {
    this.type = type;
  }

} // end of class