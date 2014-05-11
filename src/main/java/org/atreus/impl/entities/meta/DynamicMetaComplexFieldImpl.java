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

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.meta.AtreusMetaObject;

/**
 * Dynamic meta complex field.
 *
 * @author Martin Crawford
 */
public class DynamicMetaComplexFieldImpl extends BaseMetaComplexFieldImpl {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final String name;
  private final Class<?> type;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public DynamicMetaComplexFieldImpl(AtreusMetaObject ownerObject, String name, Class<?> type) {
    super(ownerObject);
    this.name = name;
    this.type = type;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Object getValue(Object entity) {
    if (!(entity instanceof AtreusManagedEntity)) {
      throw new RuntimeException("Cannot retrieve a dynamic field from a non-managed entity");
    }
    return ((AtreusManagedEntity) entity).getDynamicFields().get(name);
  }

  @Override
  public void setValue(Object entity, Object value) {
    if (!(entity instanceof AtreusManagedEntity)) {
      throw new RuntimeException("Cannot retrieve a dynamic field from a non-managed entity");
    }
    ((AtreusManagedEntity) entity).getDynamicFields().put(name, value);
  }

  @Override
  public Class<?> getType() {
    return type;
  }

} // end of class