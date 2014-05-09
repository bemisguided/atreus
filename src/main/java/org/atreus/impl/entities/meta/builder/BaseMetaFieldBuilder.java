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
package org.atreus.impl.entities.meta.builder;

import org.atreus.core.AtreusInitialisationException;
import org.atreus.core.annotations.AtreusFieldType;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.ext.strategies.AtreusTypeStrategy;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.meta.MetaEntityImpl;
import org.atreus.impl.entities.meta.StaticMetaFieldImpl;
import org.atreus.impl.types.TypeManager;

import java.lang.reflect.Field;

/**
 * Base meta field builder.
 *
 * @author Martin Crawford
 */
public abstract class BaseMetaFieldBuilder extends BaseMetaPropertyBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  protected BaseMetaFieldBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected StaticMetaFieldImpl createStaticMetaField(MetaEntityImpl metaEntity, Field field) {
    StaticMetaFieldImpl managedField = new StaticMetaFieldImpl(metaEntity, field);
    managedField.setColumn(field.getName());
    return managedField;
  }

  @SuppressWarnings("unchecked")
  protected void resolveTypeStrategy(MetaEntityImpl metaEntity, AtreusMetaField metaField, Field field) {

    // Query for @AtreusFieldType
    AtreusFieldType fieldTypeAnnotation = field.getAnnotation(AtreusFieldType.class);

    // Annotation exists so resolve
    Class<?> typeClass = field.getType();
    if (fieldTypeAnnotation != null) {

      // Attempt to assign the Type Strategy
      Class<? extends AtreusTypeStrategy> typeStrategyClass = fieldTypeAnnotation.value();
      try {
        AtreusTypeStrategy typeStrategy = typeStrategyClass.newInstance();
        typeStrategy.setValueClass(typeClass);
        metaField.setTypeStrategy(typeStrategy);
        return;
      }
      catch (InstantiationException | IllegalAccessException e) {

        // Instantiation exception translate to Atreus Exception
        throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_TYPE_STRATEGY_INVALID,
            metaEntity.toString(), typeStrategyClass.getCanonicalName(), e);
      }
    }

    TypeManager typeManager = getEnvironment().getTypeManager();

    AtreusTypeStrategy typeStrategy = typeManager.findTypeStrategy(typeClass);
    if (typeStrategy != null) {
      typeStrategy.setValueClass(typeClass);
      metaField.setTypeStrategy(typeStrategy);
    }
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class