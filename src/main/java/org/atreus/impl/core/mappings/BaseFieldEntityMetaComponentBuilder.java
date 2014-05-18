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

import org.atreus.core.AtreusInitialisationException;
import org.atreus.core.annotations.AtreusFieldType;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.core.ext.strategies.AtreusTypeStrategy;
import org.atreus.impl.core.Environment;
import org.atreus.impl.core.mappings.entities.meta.DynamicMetaSimpleFieldImpl;
import org.atreus.impl.core.mappings.entities.meta.MetaEntityImpl;
import org.atreus.impl.core.mappings.entities.meta.StaticMetaSimpleFieldImpl;
import org.atreus.impl.types.TypeManager;

import java.lang.reflect.Field;

/**
 * Base meta field builder.
 *
 * @author Martin Crawford
 */
public abstract class BaseFieldEntityMetaComponentBuilder extends BaseEntityMetaComponentBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  protected BaseFieldEntityMetaComponentBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected DynamicMetaSimpleFieldImpl createDynamicMetaSimpleField(MetaEntityImpl metaEntity, String name, Class<?> type) {
    DynamicMetaSimpleFieldImpl metaField = new DynamicMetaSimpleFieldImpl(metaEntity, name, type);
    metaField.setColumn(name);
    return metaField;
  }


  protected StaticMetaSimpleFieldImpl createStaticMetaSimpleField(MetaEntityImpl metaEntity, Field field) {
    StaticMetaSimpleFieldImpl metaField = new StaticMetaSimpleFieldImpl(metaEntity, field);
    metaField.setColumn(field.getName());
    return metaField;
  }

  @SuppressWarnings("unchecked")
  protected void resolveTypeStrategy(MetaEntityImpl metaEntity, AtreusMetaSimpleField metaField, Field field) {

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