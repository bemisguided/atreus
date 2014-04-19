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

import org.atreus.core.entities.AtreusEntity;
import org.atreus.core.entities.AtreusField;
import org.atreus.core.entities.AtreusPrimaryKey;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.atreus.impl.util.StringUtils.getValue;

/**
 * Registry of managed entities.
 *
 * @author Martin Crawford
 */
public class EntityManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(EntityManager.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Map<Class<?>, ManagedEntity> registry = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addEntity(ManagedEntity managedEntity) {
    registry.put(managedEntity.getEntityClass(), managedEntity);
  }

  public void scanPath(String path) {
    Reflections reflections = new Reflections(path);
    Set<Class<?>> entityTypes = reflections.getTypesAnnotatedWith(AtreusEntity.class);
    for(Class<?> entityType : entityTypes) {
      LOG.trace("Found Entity with @AtreusEntity classType={}", entityType.getCanonicalName());

      AtreusEntity entityAnnotation = entityType.getAnnotation(AtreusEntity.class);
      ManagedEntity managedEntity = new ManagedEntity();
      String className = entityType.getSimpleName();
      managedEntity.setName(getValue(entityAnnotation.value(), className));
      managedEntity.setTableName(getValue(entityAnnotation.table(), className));
      managedEntity.setKeySpaceName(getValue(entityAnnotation.keySpace(), className));

      List<ManagedField> primaryKeysList = new ArrayList<>();
      List<ManagedField> managedFieldsList = new ArrayList<>();
      for(Field field : entityType.getDeclaredFields()) {
        String fieldName = field.getName();
        LOG.trace("Found declared field name={}", fieldName);
        // Skip transient field
        if (Modifier.isTransient(field.getModifiers())) {
          LOG.trace("Field is transient and being ignored name={}", fieldName);
          continue;
        }
        ManagedField managedField = new ManagedField();
        managedField.setColumnName(fieldName);
        managedField.setJavaField(field);
        AtreusPrimaryKey primaryKeyAnnotation = field.getAnnotation(AtreusPrimaryKey.class);
        if (primaryKeyAnnotation != null) {
          managedField.setColumnName(getValue(primaryKeyAnnotation.value(), fieldName));
          // TODO type manager
          primaryKeysList.add(primaryKeyAnnotation.order(), managedField);
          continue;
        }
        AtreusField fieldAnnotation = field.getAnnotation(AtreusField.class);
        if (fieldAnnotation != null) {
          managedField.setColumnName(getValue(fieldAnnotation.value(), fieldName));
          // TODO type manager
        }
        managedFieldsList.add(managedField);
        ManagedField[] primaryKeys = new ManagedField[primaryKeysList.size()];
        primaryKeysList.toArray(primaryKeys);
        ManagedField[] managedFields = new ManagedField[managedFieldsList.size()];
        managedFieldsList.toArray(managedFields);
        managedEntity.setPrimaryKey(primaryKeys);
        managedEntity.setFields(managedFields);
      }
      addEntity(managedEntity);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}