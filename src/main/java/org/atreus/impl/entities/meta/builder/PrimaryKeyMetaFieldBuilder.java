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
import org.atreus.core.annotations.AtreusPrimaryKey;
import org.atreus.core.annotations.AtreusPrimaryKeyGenerator;
import org.atreus.core.ext.strategies.AtreusPrimaryKeyStrategy;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.meta.MetaEntityImpl;
import org.atreus.impl.entities.meta.StaticMetaFieldImpl;
import org.atreus.impl.types.TypeManager;
import org.atreus.impl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Primary Key meta field builder.
 *
 * @author Martin Crawford
 */
public class PrimaryKeyMetaFieldBuilder extends BaseMetaFieldBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(PrimaryKeyMetaFieldBuilder.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public PrimaryKeyMetaFieldBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean acceptField(MetaEntityImpl metaEntity, Field field) {

    // Check if this is annotated as a primary key
    AtreusPrimaryKey primaryKeyAnnotation = field.getAnnotation(AtreusPrimaryKey.class);
    if (primaryKeyAnnotation == null) {
      return false;
    }

    // Create the Primary Key meta field
    StaticMetaFieldImpl primaryKeyMetaField = createStaticMetaField(metaEntity, field);
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

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void assertSinglePrimaryKey(MetaEntityImpl metaEntity, StaticMetaFieldImpl managedField) {
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