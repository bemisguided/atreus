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

import org.atreus.core.AtreusConfiguration;
import org.atreus.core.AtreusInitialisationException;
import org.atreus.core.ext.*;
import org.atreus.impl.AtreusEnvironment;
import org.atreus.impl.types.TypeManager;
import org.atreus.impl.util.ReflectionUtils;
import org.atreus.impl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Registry of managed entities.
 *
 * @author Martin Crawford
 */
public class EntityManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(EntityManager.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusEnvironment environment;
  private Map<Class<?>, ManagedEntityImpl> classRegistry = new HashMap<>();
  private Map<String, ManagedEntityImpl> nameRegistry = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public EntityManager(AtreusEnvironment environment) {
    this.environment = environment;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addEntity(Class<?> entityType) {
    doProcessEntity(entityType);
  }

  public AtreusManagedEntity getEntity(String name) {
    return nameRegistry.get(name);
  }

  public AtreusManagedEntity getEntity(Class<?> entityType) {
    return classRegistry.get(entityType);
  }

  public void processEntities() {
    // Iterate and process fields for each manged entity
    for (ManagedEntityImpl managedEntity : classRegistry.values()) {
      Class<?> entityType = managedEntity.getEntityType();
      // Process the fields
      for (Field javaField : entityType.getDeclaredFields()) {

        // Ignore transient field
        if (Modifier.isTransient(javaField.getModifiers())) {
          continue;
        }

        doProcessField(managedEntity, javaField);
      }
    }
  }

  public void scanPaths(String[] paths) {
    for (String path : paths) {
      scanPath(path);
    }
  }

  public void scanPath(String path) {
    LOG.trace("Scanning classpath path={}", path);

    AtreusEntityStrategy[] entityStrategies = environment.getConfiguration().getEntityStrategies();

    // Resolve all candidate entity classes using the configured entity strategies
    Set<Class<?>> entityTypes = new HashSet<>();
    for (AtreusEntityStrategy entityStrategy : entityStrategies) {
      LOG.trace("Scanning with AtreusEntityStrategy={}", entityStrategy.getClass());
      entityTypes.addAll(entityStrategy.findEntities(path));
    }

    // Iterate and process each found entity class
    for (Class<?> entityType : entityTypes) {
      addEntity(entityType);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void addManagedEntity(ManagedEntityImpl managedEntity) {
    classRegistry.put(managedEntity.getEntityType(), managedEntity);
    nameRegistry.put(managedEntity.getName(), managedEntity);
  }

  private void assertSinglePrimaryKey(ManagedEntityImpl managedEntity, ManagedFieldImpl managedField) {
    if (managedEntity.getPrimaryKeyField() != null && !managedEntity.getPrimaryKeyField().equals(managedField)) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_MULTIPLE,
          managedEntity.getName());
    }
  }

  private ManagedEntityImpl buildManagedEntity(Class<?> entityType) {
    String className = entityType.getSimpleName();
    ManagedEntityImpl managedEntity = new ManagedEntityImpl();
    managedEntity.setEntityType(entityType);
    managedEntity.setName(className);
    managedEntity.setTable(className);
    managedEntity.setKeySpace(environment.getConfiguration().getKeySpace());
    return managedEntity;
  }

  private ManagedFieldImpl buildManagedField(ManagedEntityImpl managedEntity, Field javaField) {
    ManagedFieldImpl managedField = new ManagedFieldImpl();
    managedField.setManagedEntity(managedEntity);
    managedField.setColumn(javaField.getName());
    managedField.setJavaField(javaField);
    return managedField;
  }

  private void doProcessEntity(Class<?> entityType) {
    LOG.trace("Processing Entity entityType={}", entityType.getCanonicalName());

    AtreusConfiguration configuration = environment.getConfiguration();

    // Create the managed entity with defaults
    ManagedEntityImpl managedEntity = buildManagedEntity(entityType);

    // Iterate the entity strategies and update the managed entity
    for (AtreusEntityStrategy entityStrategy : configuration.getEntityStrategies()) {
      updateManagedEntity(managedEntity, entityStrategy);
    }

    LOG.debug("Registered Entity name={} {}", managedEntity.getName(), managedEntity.getEntityType());
    addManagedEntity(managedEntity);
  }

  @SuppressWarnings("unchecked")
  private void doProcessField(ManagedEntityImpl managedEntity, Field javaField) {
    String fieldName = javaField.getName();
    LOG.trace("Processing Field for Entity entityType={} javaField={}", managedEntity.getEntityType(), fieldName);

    AtreusConfiguration configuration = environment.getConfiguration();

    // Set the accessibility of the field to true
    javaField.setAccessible(true);

    // Build the Managed Field with common values
    ManagedFieldImpl managedField = buildManagedField(managedEntity, javaField);

    // Iterate the entity strategies and process
    for (AtreusEntityStrategy entityStrategy : configuration.getEntityStrategies()) {

      if (classRegistry.containsKey(javaField.getType())) {
        LOG.debug("Relationship detected {}", managedField.getJavaField());
        continue;
      }
      // Resolve the common Type Strategy (if available)
      resolveTypeStrategy(managedField, entityStrategy);

      // Special fields

      // Primary Key
      if (entityStrategy.isPrimaryKeyField(managedField)) {
        processPrimaryKeyField(managedEntity, managedField, entityStrategy);
        continue;
      }

      // Time-to-live
      if (entityStrategy.isTtlField(managedField)) {
        processTimeToLiveField(managedEntity, managedField, entityStrategy);
        continue;
      }

      // Managed Fields
      managedEntity.addField(managedField);

      // Collection
      if (Collection.class.isAssignableFrom(javaField.getType())) {
        processCollectionField(managedField, entityStrategy);
        continue;
      }

      // Map
      if (Map.class.isAssignableFrom(javaField.getType())) {
        processMapField(managedField, entityStrategy);
        continue;
      }

      updateField(managedField, entityStrategy);

    }


    // TODO validation: Type Strategy, primary keys & generator are serializable, validate ttl
  }

  @SuppressWarnings("unchecked")
  private void processCollectionField(ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    Class<?> valueClass = entityStrategy.getCollectionValue(managedField);
    if (valueClass == null) {
      valueClass = ReflectionUtils.findCollectionValueClass(managedField.getJavaField());
    }

    CQLDataType valueDataType = CQLDataType.mapClassToDataType(valueClass);

    if (valueClass == null || valueDataType == null) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_COLLECTION_VALUE_TYPE_NOT_RESOLVABLE,
          managedField);
    }

    if (!(managedField.getTypeStrategy() instanceof AtreusCollectionTypeStrategy)) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_COLLECTION_TYPE_STRATEGY_INVALID,
          managedField, managedField.getTypeStrategy());
    }

    AtreusCollectionTypeStrategy collectionTypeStrategy = (AtreusCollectionTypeStrategy) managedField.getTypeStrategy();
    collectionTypeStrategy.setValueDataType(valueDataType);
  }

  @SuppressWarnings("unchecked")
  private void processMapField(ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    Class<?> valueClass = entityStrategy.getCollectionValue(managedField);
    Class<?> keyClass = entityStrategy.getMapKey(managedField);

    if (valueClass == null) {
      valueClass = ReflectionUtils.findMapValueClass(managedField.getJavaField());
    }

    if (keyClass == null) {
      keyClass = ReflectionUtils.findMapKeyClass(managedField.getJavaField());
    }

    CQLDataType valueDataType = CQLDataType.mapClassToDataType(valueClass);
    CQLDataType keyDataType = CQLDataType.mapClassToDataType(keyClass);

    if (valueClass == null || valueDataType == null) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_COLLECTION_VALUE_TYPE_NOT_RESOLVABLE,
          managedField);
    }

    if (keyClass == null || keyDataType == null) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_MAP_KEY_TYPE_NOT_RESOLVABLE,
          managedField);
    }

    if (!(managedField.getTypeStrategy() instanceof AtreusMapTypeStrategy)) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_MAP_TYPE_STRATEGY_INVALID,
          managedField, managedField.getTypeStrategy());
    }

    AtreusMapTypeStrategy mapTypeStrategy = (AtreusMapTypeStrategy) managedField.getTypeStrategy();
    mapTypeStrategy.setValueDataType(valueDataType);
    mapTypeStrategy.setKeyDataType(keyDataType);
  }

  private void processPrimaryKeyField(ManagedEntityImpl managedEntity, ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    // Assert there is not already an existing primary key
    assertSinglePrimaryKey(managedEntity, managedField);

    // Update the Primary Key
    updatePrimaryKey(managedField, entityStrategy);

    // If primary key is generated resolve a generation strategy
    if (entityStrategy.isPrimaryKeyGenerated(managedField)) {
      resolvePrimaryKeyStrategy(managedEntity, managedField, entityStrategy);
    }

    // Mark as a Primary Key
    managedEntity.setPrimaryKeyField(managedField);
  }

  private void processTimeToLiveField(ManagedEntityImpl managedEntity, ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    resolveTtlStrategy(managedEntity, managedField, entityStrategy);
    managedEntity.setTtlField(managedField);
  }

  private void updateManagedEntity(ManagedEntityImpl managedEntity, AtreusEntityStrategy entityStrategy) {
    String name = entityStrategy.getEntityName(managedEntity);
    String keySpace = entityStrategy.getEntityKeySpace(managedEntity);
    String table = entityStrategy.getEntityTable(managedEntity);

    if (StringUtils.isNotNullOrEmpty(name)) {
      managedEntity.setName(name);
    }
    if (StringUtils.isNotNullOrEmpty(keySpace)) {
      managedEntity.setKeySpace(keySpace);
    }
    if (StringUtils.isNotNullOrEmpty(table)) {
      managedEntity.setTable(table);
    }
  }

  private void updateField(ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    String column = entityStrategy.getFieldColumn(managedField);

    if (StringUtils.isNotNullOrEmpty(column)) {
      managedField.setColumn(column);
    }
  }

  private void updatePrimaryKey(ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    String column = entityStrategy.getPrimaryKeyColumn(managedField);

    if (StringUtils.isNotNullOrEmpty(column)) {
      managedField.setColumn(column);
    }
  }

  private void resolvePrimaryKeyStrategy(ManagedEntityImpl managedEntity, ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    AtreusPrimaryKeyStrategy primaryKeyStrategy = entityStrategy.resolvePrimaryKeyStrategy(managedField);

    if (primaryKeyStrategy != null) {
      managedEntity.setPrimaryKeyGenerator(primaryKeyStrategy);
      return;
    }

    if (managedEntity.getPrimaryKeyStrategy() != null) {
      return;
    }

    TypeManager typeManager = environment.getTypeManager();

    Field javaField = managedField.getJavaField();
    primaryKeyStrategy = typeManager.findPrimaryKeyGenerator(javaField.getType());
    if (primaryKeyStrategy != null) {
      managedEntity.setPrimaryKeyGenerator(primaryKeyStrategy);
    }
  }

  private void resolveTtlStrategy(ManagedEntityImpl managedEntity, ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    AtreusTtlStrategy ttlStrategy = entityStrategy.resolveTtlStrategy(managedField);

    if (ttlStrategy != null) {
      managedEntity.setTtlStrategy(ttlStrategy);
      return;
    }

    if (managedEntity.getTtlStrategy() != null) {
      return;
    }

    TypeManager typeManager = environment.getTypeManager();

    Field javaField = managedField.getJavaField();
    ttlStrategy = typeManager.findTtlStrategy(javaField.getType());
    if (ttlStrategy != null) {
      managedEntity.setTtlStrategy(ttlStrategy);
    }
  }

  @SuppressWarnings("unchecked")
  private void resolveTypeStrategy(ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    Class<?> typeClass = managedField.getJavaField().getType();
    AtreusTypeStrategy typeStrategy = entityStrategy.resolveTypeStrategy(managedField);

    if (typeStrategy != null) {
      typeStrategy.setValueClass(typeClass);
      managedField.setTypeStrategy(typeStrategy);
      return;
    }

    if (managedField.getTypeStrategy() != null) {
      return;
    }

    TypeManager typeManager = environment.getTypeManager();

    typeStrategy = typeManager.findTypeStrategy(typeClass);
    if (typeStrategy != null) {
      typeStrategy.setValueClass(typeClass);
      managedField.setTypeStrategy(typeStrategy);
    }
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}