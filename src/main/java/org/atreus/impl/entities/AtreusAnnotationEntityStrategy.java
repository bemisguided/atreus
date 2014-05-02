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
package org.atreus.impl.entities;

import org.atreus.core.AtreusInitialisationException;
import org.atreus.core.annotations.*;
import org.atreus.core.ext.*;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Implements an entity strategy for the Atreus Annotations set.
 *
 * @author Martin Crawford
 */
public class AtreusAnnotationEntityStrategy implements AtreusEntityStrategy {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusAnnotationEntityStrategy.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public Class<?> getCollectionValue(AtreusManagedField managedField) {
    AtreusCollection collectionAnnotation = managedField.getJavaField().getAnnotation(AtreusCollection.class);
    if (collectionAnnotation != null && !NullType.class.isAssignableFrom(collectionAnnotation.type())) {
      return collectionAnnotation.type();
    }
    return null;
  }

  @Override
  public String getEntityName(AtreusManagedEntity managedEntity) {
    AtreusEntity entityAnnotation = managedEntity.getEntityType().getAnnotation(AtreusEntity.class);
    if (entityAnnotation == null) {
      return null;
    }
    return entityAnnotation.value();
  }

  @Override
  public String getEntityKeySpace(AtreusManagedEntity managedEntity) {
    AtreusEntity entityAnnotation = managedEntity.getEntityType().getAnnotation(AtreusEntity.class);
    if (entityAnnotation == null) {
      return null;
    }
    return entityAnnotation.keySpace();
  }

  @Override
  public String getEntityTable(AtreusManagedEntity managedEntity) {
    AtreusEntity entityAnnotation = managedEntity.getEntityType().getAnnotation(AtreusEntity.class);
    if (entityAnnotation == null) {
      return null;
    }
    return entityAnnotation.table();
  }

  @Override
  public String getFieldColumn(AtreusManagedField managedField) {
    AtreusField fieldAnnotation = managedField.getJavaField().getAnnotation(AtreusField.class);
    if (fieldAnnotation == null) {
      return null;
    }
    return fieldAnnotation.value();
  }

  @Override
  public Class<?> getMapKey(AtreusManagedField managedField) {
    AtreusMap mapAnnotation = managedField.getJavaField().getAnnotation(AtreusMap.class);
    if (mapAnnotation != null && !NullType.class.isAssignableFrom(mapAnnotation.key())) {
      return mapAnnotation.key();
    }
    return null;
  }

  @Override
  public String getPrimaryKeyColumn(AtreusManagedField managedField) {
    AtreusPrimaryKey primaryKeyAnnotation = managedField.getJavaField().getAnnotation(AtreusPrimaryKey.class);
    if (primaryKeyAnnotation == null) {
      return null;
    }
    return primaryKeyAnnotation.value();
  }

  @Override
  public Collection<Class<?>> findEntities(String path) {
    Reflections reflections = new Reflections(path);
    return reflections.getTypesAnnotatedWith(AtreusEntity.class);
  }

  @Override
  public boolean isPrimaryKeyField(AtreusManagedField managedField) {
    Field javaField = managedField.getJavaField();
    return javaField.getAnnotation(AtreusPrimaryKey.class) != null;
  }

  @Override
  public boolean isPrimaryKeyGenerated(AtreusManagedField managedField) {
    AtreusPrimaryKey primaryKeyAnnotation = managedField.getJavaField().getAnnotation(AtreusPrimaryKey.class);
    return primaryKeyAnnotation.generated();
  }

  @Override
  public boolean isTtlField(AtreusManagedField managedField) {
    Field javaField = managedField.getJavaField();
    return javaField.getAnnotation(AtreusTtl.class) != null;
  }

  @Override
  public AtreusPrimaryKeyStrategy resolvePrimaryKeyStrategy(AtreusManagedField managedField) {

    // Query for @AtreusPrimaryKeyGenerator
    AtreusPrimaryKeyGenerator primaryKeyGeneratorAnnotation = managedField.getJavaField().getAnnotation(AtreusPrimaryKeyGenerator.class);

    // None exists return
    if (primaryKeyGeneratorAnnotation == null) {
      return null;
    }

    // Attempt to assign the Type Strategy
    Class<? extends AtreusPrimaryKeyStrategy> primaryKeyStrategyClass = primaryKeyGeneratorAnnotation.value();
    try {
      return primaryKeyStrategyClass.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e) {
      // Instantiation exception translate to Atreus Exception
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_STRATEGY_INVALID,
          managedField.toString(), primaryKeyStrategyClass.getCanonicalName(), e);
    }
  }

  @Override
  public AtreusTtlStrategy resolveTtlStrategy(AtreusManagedField managedField) {

    // Query for @AtreusTtlTranslator
    AtreusTtlTranslator ttlTranslatorAnnotation = managedField.getJavaField().getAnnotation(AtreusTtlTranslator.class);

    // None exists return
    if (ttlTranslatorAnnotation == null) {
      return null;
    }

    // Attempt to assign the Type Strategy
    Class<? extends AtreusTtlStrategy> ttlStrategy = ttlTranslatorAnnotation.value();
    try {
      return ttlStrategy.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e) {
      // Instantiation exception translate to Atreus Exception
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_TTL_STRATEGY_INVALID,
          managedField.toString(), ttlStrategy.getCanonicalName(), e);
    }
  }

  @Override
  public AtreusTypeStrategy resolveTypeStrategy(AtreusManagedField managedField) {

    // Query for @AtreusFieldType
    AtreusFieldType fieldTypeAnnotation = managedField.getJavaField().getAnnotation(AtreusFieldType.class);

    // None exists return
    if (fieldTypeAnnotation == null) {
      return null;
    }

    // Attempt to assign the Type Strategy
    Class<? extends AtreusTypeStrategy> typeStrategyClass = fieldTypeAnnotation.value();
    try {
      return typeStrategyClass.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e) {

      // Instantiation exception translate to Atreus Exception
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_TYPE_STRATEGY_INVALID,
          managedField.toString(), typeStrategyClass.getCanonicalName(), e);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}