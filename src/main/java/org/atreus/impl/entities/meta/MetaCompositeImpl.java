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
package org.atreus.impl.entities.meta;

import org.atreus.core.ext.meta.AtreusMetaComposite;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;

/**
 * Meta Composite bean.
 *
 * @author Martin Crawford
 */
public class MetaCompositeImpl implements AtreusMetaComposite {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private AtreusMetaEntity parentEntity;
  private AtreusMetaField parentField;
  private AtreusMetaEntity childEntity;
  private AtreusMetaField childField;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public AtreusMetaEntity getParentEntity() {
    return parentEntity;
  }

  public void setParentEntity(AtreusMetaEntity parentEntity) {
    this.parentEntity = parentEntity;
  }

  @Override
  public AtreusMetaField getParentField() {
    return parentField;
  }

  public void setParentField(AtreusMetaField parentField) {
    this.parentField = parentField;
  }

  @Override
  public AtreusMetaEntity getChildEntity() {
    return childEntity;
  }

  public void setChildEntity(AtreusMetaEntity childEntity) {
    this.childEntity = childEntity;
  }

  @Override
  public AtreusMetaField getChildField() {
    return childField;
  }

  public void setChildField(AtreusMetaField childField) {
    this.childField = childField;
  }

} // end of class