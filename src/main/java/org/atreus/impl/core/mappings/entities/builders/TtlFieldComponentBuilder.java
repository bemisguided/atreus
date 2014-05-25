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

import org.atreus.core.AtreusInitialisationException;
import org.atreus.core.annotations.AtreusTtl;
import org.atreus.core.annotations.AtreusTtlTranslator;
import org.atreus.core.ext.strategies.AtreusTtlStrategy;
import org.atreus.impl.core.Environment;
import org.atreus.impl.core.mappings.BaseFieldEntityMetaComponentBuilder;
import org.atreus.impl.core.mappings.entities.meta.MetaEntityImpl;
import org.atreus.impl.core.mappings.entities.meta.StaticMetaSimpleFieldImpl;
import org.atreus.impl.types.TypeManager;

import java.lang.reflect.Field;

/**
 * Time-to-live meta field builder.
 *
 * @author Martin Crawford
 */
public class TtlFieldComponentBuilder extends BaseFieldEntityMetaComponentBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public TtlFieldComponentBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean acceptsField(MetaEntityImpl metaEntity, Field field) {
    return field.getAnnotation(AtreusTtl.class) != null;
  }

  @Override
  public boolean handleField(MetaEntityImpl metaEntity, Field field) {

    // Create the time-to-live meta field
    StaticMetaSimpleFieldImpl ttlMetaField = createStaticMetaSimpleField(metaEntity, field, null);

    // Resolve the type strategy
    resolveTypeStrategy(metaEntity, ttlMetaField, field);

    // Add time-to-live field to the meta entity
    metaEntity.setTtlField(ttlMetaField);

    // Resolve the time-to-live strategy
    resolveTtlStrategy(metaEntity, field);

    return true;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void resolveTtlStrategy(MetaEntityImpl metaEntity, Field field) {

    // Query for @AtreusTtlTranslator
    AtreusTtlTranslator ttlStrategyAnnotation = field.getAnnotation(AtreusTtlTranslator.class);

    // Exists an annotation
    if (ttlStrategyAnnotation != null) {

      // Attempt to assign the Type Strategy
      Class<? extends AtreusTtlStrategy> ttlStrategyClass = ttlStrategyAnnotation.value();
      try {
        metaEntity.setTtlStrategy(ttlStrategyClass.newInstance());
        return;
      }
      catch (InstantiationException | IllegalAccessException e) {
        // Instantiation exception translate to Atreus Exception
        throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_STRATEGY_INVALID,
            metaEntity.toString(), ttlStrategyClass.getCanonicalName(), e);
      }
    }

    TypeManager typeManager = getEnvironment().getTypeManager();

    AtreusTtlStrategy ttlStrategy = typeManager.findTtlStrategy(field.getType());
    if (ttlStrategy != null) {
      metaEntity.setTtlStrategy(ttlStrategy);
    }
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class