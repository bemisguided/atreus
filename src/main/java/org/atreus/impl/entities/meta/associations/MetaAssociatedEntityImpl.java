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

import org.atreus.core.ext.meta.AtreusMetaAssociatedEntity;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo document me
 *
 * @author Martin Crawford
 */
public class MetaAssociatedEntityImpl implements AtreusMetaAssociatedEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(MetaAssociatedEntityImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private AtreusMetaEntity metaEntity;
  private AtreusMetaField associationField;
  private AtreusMetaField associationKeyField;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public AtreusMetaEntity getMetaEntity() {
    return metaEntity;
  }

  public void setMetaEntity(AtreusMetaEntity metaEntity) {
    this.metaEntity = metaEntity;
  }

  @Override
  public AtreusMetaField getAssociationField() {
    return associationField;
  }

  public void setAssociationField(AtreusMetaField associationField) {
    this.associationField = associationField;
  }

  @Override
  public AtreusMetaField getAssociationKeyField() {
    return associationKeyField;
  }

  public void setAssociationKeyField(AtreusMetaField associationKeyField) {
    this.associationKeyField = associationKeyField;
  }

} // end of class