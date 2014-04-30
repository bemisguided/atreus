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
import org.atreus.core.ext.AtreusEntityStrategy;
import org.atreus.core.ext.AtreusPrimaryKeyStrategy;
import org.atreus.core.ext.AtreusTtlStrategy;
import org.atreus.core.ext.AtreusTypeStrategy;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.impl.AtreusEnvironment;
import org.atreus.impl.types.TypeManager;
import org.atreus.impl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
  private Map<Class<?>, AtreusManagedEntity> classRegistry = new HashMap<>();
  private Map<String, AtreusManagedEntity> nameRegistry = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public EntityManager(AtreusEnvironment environment) {
    this.environment = environment;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addEntity(AtreusManagedEntity managedEntity) {
    classRegistry.put(managedEntity.getEntityType(), managedEntity);
    nameRegistry.put(managedEntity.getName(), managedEntity);
  }

  public AtreusManagedEntity getEntity(String name) {
    return nameRegistry.get(name);
  }

  public AtreusManagedEntity getEntity(Class<?> entityType) {
    return classRegistry.get(entityType);
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
      doProcessEntity(entityType);
    }

  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

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

    // Process the fields
    for (Field javaField : entityType.getDeclaredFields()) {

      // Ignore transient field
      if (Modifier.isTransient(javaField.getModifiers())) {
        continue;
      }

      doProcessField(managedEntity, javaField);
    }

    LOG.debug("Registered Entity name={} entityClass={}", managedEntity.getName(), managedEntity.getEntityType());
    addEntity(managedEntity);
  }

  private void doProcessField(ManagedEntityImpl managedEntity, Field javaField) {
    String fieldName = javaField.getName();
    LOG.trace("Processing Field for Entity entityType={} javaField={}", managedEntity.getEntityType(), fieldName);

    AtreusConfiguration configuration = environment.getConfiguration();

    // Set the accessibility of the field to true
    javaField.setAccessible(true);

    // Build the Managed Field with default values
    ManagedFieldImpl managedField = buildManagedField(managedEntity, javaField);

    // Iterate the entity strategies and process
    for (AtreusEntityStrategy entityStrategy : configuration.getEntityStrategies()) {

      // Resolve the default Type Strategy (if available)
      resolveTypeStrategy(managedField, entityStrategy);

      // Primary Key
      if (entityStrategy.isPrimaryKeyField(managedField)) {

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
      // Time-to-live
      else if (entityStrategy.isTtlField(managedField)) {
        resolveTtlStrategy(managedEntity, managedField,entityStrategy);
        managedEntity.setTtlField(managedField);
      }
      else {
        updateField(managedField, entityStrategy);
        managedEntity.getFields().add(managedField);
      }
    }

    // TODO validation: Type Strategy, primary keys & generator are serializable, validate ttl
  }

  private void updateManagedEntity(ManagedEntityImpl managedEntity, AtreusEntityStrategy entityStrategy) {
    String name = entityStrategy.getEntityName(managedEntity);
    String keySpace = entityStrategy.getEntityKeySpace(managedEntity);
    String table = entityStrategy.getEntityName(managedEntity);

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

  private void resolveTypeStrategy(ManagedFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    AtreusTypeStrategy typeStrategy = entityStrategy.resolveTypeStrategy(managedField);

    if (typeStrategy != null) {
      managedField.setTypeStrategy(typeStrategy);
      return;
    }

    if (managedField.getTypeStrategy() != null) {
      return;
    }

    TypeManager typeManager = environment.getTypeManager();

    Field javaField = managedField.getJavaField();
    typeStrategy = typeManager.findTypeStrategy(javaField.getType());
    if (typeStrategy != null) {
      managedField.setTypeStrategy(typeStrategy);
    }
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}