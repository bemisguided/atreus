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

import org.atreus.core.annotations.*;
import org.atreus.core.ext.AtreusEntityStrategy;
import org.atreus.core.ext.entities.AtreusManagedEntity;
import org.atreus.core.ext.entities.AtreusManagedField;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;

import static org.atreus.impl.util.StringUtils.isNotNullOrEmpty;

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
  public boolean isTtlField(AtreusManagedField managedField) {
    Field javaField = managedField.getJavaField();
    return javaField.getAnnotation(AtreusTtl.class) != null;
  }

  @Override
  public void processEntity(AtreusManagedEntity managedEntity) {
    Class<?> entityType = managedEntity.getEntityType();
    AtreusEntity entityAnnotation = entityType.getAnnotation(AtreusEntity.class);
    if (isNotNullOrEmpty(entityAnnotation.value())) {
      managedEntity.setName(entityAnnotation.value());
    }
    if (isNotNullOrEmpty(entityAnnotation.table())) {
      managedEntity.setTable(entityAnnotation.table());
    }
    if (isNotNullOrEmpty(entityAnnotation.keySpace())) {
      managedEntity.setKeySpace(entityAnnotation.keySpace());
    }
  }

  @Override
  public void processField(AtreusManagedEntity managedEntity, AtreusManagedField managedField) {
    Field javaField = managedField.getJavaField();
    AtreusField fieldAnnotation = javaField.getAnnotation(AtreusField.class);

    if (fieldAnnotation != null && isNotNullOrEmpty(fieldAnnotation.value())) {
      managedField.setColumn(fieldAnnotation.value());
    }

    AtreusFieldType fieldTypeAnnotation = javaField.getAnnotation(AtreusFieldType.class);

    if (fieldTypeAnnotation != null) {
      try {
        managedField.setTypeAccessor(fieldTypeAnnotation.value().newInstance());
      }
      catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    managedEntity.getFields().add(managedField);
  }

  @Override
  public void processPrimaryKeyField(AtreusManagedEntity managedEntity, AtreusManagedField managedField) {
    Field javaField = managedField.getJavaField();
    AtreusPrimaryKey primaryKeyAnnotation = javaField.getAnnotation(AtreusPrimaryKey.class);

    if (primaryKeyAnnotation == null) {
      return;
    }

    if (isNotNullOrEmpty(primaryKeyAnnotation.value())) {
      managedField.setColumn(primaryKeyAnnotation.value());
    }

    AtreusFieldType fieldTypeAnnotation = javaField.getAnnotation(AtreusFieldType.class);

    if (fieldTypeAnnotation != null) {
      try {
        managedField.setTypeAccessor(fieldTypeAnnotation.value().newInstance());
      }
      catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}