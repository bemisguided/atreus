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
import org.atreus.core.annotations.AtreusPrimaryKey;
import org.atreus.core.annotations.AtreusPrimaryKeyGenerator;
import org.atreus.core.ext.strategies.AtreusPrimaryKeyStrategy;
import org.atreus.impl.core.Environment;
import org.atreus.impl.core.mappings.BaseFieldEntityMetaComponentBuilder;
import org.atreus.impl.core.mappings.entities.meta.MetaEntityImpl;
import org.atreus.impl.core.mappings.entities.meta.StaticMetaSimpleFieldImpl;
import org.atreus.impl.types.TypeManager;
import org.atreus.impl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Primary Key meta field builder.
 *
 * @author Martin Crawford
 */
public class PrimaryKeyComponentBuilder extends BaseFieldEntityMetaComponentBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(PrimaryKeyComponentBuilder.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public PrimaryKeyComponentBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean acceptsField(MetaEntityImpl metaEntity, Field field) {
    return field.getAnnotation(AtreusPrimaryKey.class) != null;
  }


  @Override
  public boolean handleField(MetaEntityImpl metaEntity, Field field) {

    // Check if this is annotated as a primary key
    AtreusPrimaryKey primaryKeyAnnotation = field.getAnnotation(AtreusPrimaryKey.class);

    // Create the Primary Key meta field
    StaticMetaSimpleFieldImpl primaryKeyMetaField = createStaticMetaSimpleField(metaEntity, field);
    String primaryKeyColumn = primaryKeyAnnotation.value();
    if (StringUtils.isNotNullOrEmpty(primaryKeyColumn)) {
      primaryKeyMetaField.setColumn(primaryKeyColumn);
    }

    // Assert this is the only primary key
    assertSinglePrimaryKey(metaEntity, primaryKeyMetaField);

    // Resolve the type strategy
    resolveTypeStrategy(metaEntity, primaryKeyMetaField, field);

    // Add the primary key to the meta entity
    metaEntity.setPrimaryKeyField(primaryKeyMetaField);

    if (primaryKeyAnnotation.generated()) {
      resolvePrimaryKeyStrategy(metaEntity, field);
    }
    return true;
  }

  @Override
  public void validateField(MetaEntityImpl metaEntity, Field field) {
    if (!Serializable.class.isAssignableFrom(field.getType())) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_NOT_SERIALIZABLE,
          metaEntity.getName());
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void assertSinglePrimaryKey(MetaEntityImpl metaEntity, StaticMetaSimpleFieldImpl managedField) {
    if (metaEntity.getPrimaryKeyField() != null && !metaEntity.getPrimaryKeyField().equals(managedField)) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_MULTIPLE,
          metaEntity.getName());
    }
  }

  private void resolvePrimaryKeyStrategy(MetaEntityImpl metaEntity, Field field) {

    // Query for @AtreusPrimaryKeyGenerator
    AtreusPrimaryKeyGenerator primaryKeyGeneratorAnnotation = field.getAnnotation(AtreusPrimaryKeyGenerator.class);

    // Exists annotation
    if (primaryKeyGeneratorAnnotation != null) {

      // Attempt to assign the Type Strategy
      Class<? extends AtreusPrimaryKeyStrategy> primaryKeyStrategyClass = primaryKeyGeneratorAnnotation.value();
      try {
        metaEntity.setPrimaryKeyGenerator(primaryKeyStrategyClass.newInstance());
        return;
      }
      catch (InstantiationException | IllegalAccessException e) {
        // Instantiation exception translate to Atreus Exception
        throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_STRATEGY_INVALID,
            metaEntity.toString(), primaryKeyStrategyClass.getCanonicalName(), e);
      }
    }

    TypeManager typeManager = getEnvironment().getTypeManager();

    AtreusPrimaryKeyStrategy primaryKeyStrategy = typeManager.findPrimaryKeyGenerator(field.getType());
    if (primaryKeyStrategy != null) {
      metaEntity.setPrimaryKeyGenerator(primaryKeyStrategy);
    }
  }


  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class