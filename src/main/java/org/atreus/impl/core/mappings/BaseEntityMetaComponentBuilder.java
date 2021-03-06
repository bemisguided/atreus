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
package org.atreus.impl.core.mappings;

import org.atreus.impl.core.Environment;
import org.atreus.impl.core.mappings.entities.meta.MetaEntityImpl;

import java.lang.reflect.Field;

/**
 * Base Meta Property Builder.
 *
 * @author Martin Crawford
 */
public abstract class BaseEntityMetaComponentBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final Environment environment;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  protected BaseEntityMetaComponentBuilder(Environment environment) {
    this.environment = environment;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public boolean acceptsEntity(MetaEntityImpl metaEntity, Class<?> entityType) {
    return true;
  }

  public boolean acceptsField(MetaEntityImpl metaEntity, Field field) {
    return true;
  }

  public void validateField(MetaEntityImpl metaEntity, Field field) {

  }

  public boolean handleEntity(MetaEntityImpl metaEntity, Class<?> entityType) {
    return false;
  }

  public boolean handleField(MetaEntityImpl metaEntity, Field field) {
    return false;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  protected Environment getEnvironment() {
    return environment;
  }

} // end of class