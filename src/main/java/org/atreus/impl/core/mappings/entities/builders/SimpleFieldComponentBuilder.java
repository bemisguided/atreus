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
package org.atreus.impl.core.mappings.entities.builders;

import org.atreus.core.annotations.AtreusField;
import org.atreus.impl.core.Environment;
import org.atreus.impl.core.mappings.BaseFieldEntityMetaComponentBuilder;
import org.atreus.impl.core.mappings.entities.meta.MetaEntityImpl;
import org.atreus.impl.core.mappings.entities.meta.StaticMetaSimpleFieldImpl;
import org.atreus.impl.util.ObjectUtils;

import java.lang.reflect.Field;

/**
 * Simple field meta field builder.
 *
 * @author Martin Crawford
 */
public class SimpleFieldComponentBuilder extends BaseFieldEntityMetaComponentBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public SimpleFieldComponentBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean handleField(MetaEntityImpl metaEntity, Field field) {
    // Assumption is this is the last field builder to be called and therefore a simple field

    // Create the static field
    StaticMetaSimpleFieldImpl metaField = createStaticMetaSimpleField(metaEntity, field, null);

    // Check for a field annotation
    AtreusField fieldAnnotation = field.getAnnotation(AtreusField.class);
    if (fieldAnnotation != null) {
      String fieldColumn = fieldAnnotation.value();
      if (ObjectUtils.isNotNullOrEmpty(fieldColumn)) {
        metaField.setColumn(fieldColumn);
      }
    }

    // Resolve the type strategy
    resolveTypeStrategy(metaEntity, metaField, field);

    // Add the field to the meta entity
    metaEntity.addField(metaField);
    return true;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class