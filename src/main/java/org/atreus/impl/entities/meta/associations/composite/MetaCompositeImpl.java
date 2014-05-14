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
package org.atreus.impl.entities.meta.associations.composite;

import org.atreus.core.ext.meta.AtreusMetaComposite;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.impl.entities.meta.associations.BaseMetaAssociationImpl;

/**
 * Meta Composite bean.
 *
 * @author Martin Crawford
 */
public class MetaCompositeImpl extends BaseMetaAssociationImpl implements AtreusMetaComposite {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private AtreusMetaEntity ownerEntity;
  private AtreusMetaField ownerField;
  private AtreusMetaEntity associatedEntity;
  private AtreusMetaSimpleField associatedEntityChildKeyField;
  private AtreusMetaField associatedEntityField;
  private AtreusMetaSimpleField associatedEntityParentKeyField;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public AtreusMetaEntity getOwnerEntity() {
    return ownerEntity;
  }

  public void setOwnerEntity(AtreusMetaEntity ownerEntity) {
    this.ownerEntity = ownerEntity;
  }

  @Override
  public AtreusMetaField getOwnerField() {
    return ownerField;
  }

  public void setOwnerField(AtreusMetaField ownerField) {
    this.ownerField = ownerField;
  }

  @Override
  public AtreusMetaEntity getAssociatedEntity() {
    return associatedEntity;
  }

  public void setAssociatedEntity(AtreusMetaEntity associatedEntity) {
    this.associatedEntity = associatedEntity;
  }

  @Override
  public AtreusMetaField getAssociatedEntityField() {
    return associatedEntityField;
  }

  public void setAssociatedEntityField(AtreusMetaField associatedEntityField) {
    this.associatedEntityField = associatedEntityField;
  }

  @Override
  public AtreusMetaSimpleField getAssociatedEntityChildKeyField() {
    return associatedEntityChildKeyField;
  }

  public void setAssociatedEntityChildKeyField(AtreusMetaSimpleField associatedEntityChildKeyField) {
    this.associatedEntityChildKeyField = associatedEntityChildKeyField;
  }

  @Override
  public AtreusMetaSimpleField getAssociatedEntityParentKeyField() {
    return associatedEntityParentKeyField;
  }

  public void setAssociatedEntityParentKeyField(AtreusMetaSimpleField associatedEntityParentKeyField) {
    this.associatedEntityParentKeyField = associatedEntityParentKeyField;
  }

} // end of class