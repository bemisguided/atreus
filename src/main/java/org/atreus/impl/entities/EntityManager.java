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
import org.atreus.core.ext.AtreusEntityStrategy;
import org.atreus.core.ext.AtreusPrimaryKeyGenerator;
import org.atreus.core.ext.AtreusTypeAccessor;
import org.atreus.core.ext.entities.AtreusManagedEntity;
import org.atreus.impl.AtreusEnvironment;
import org.atreus.impl.types.TypeManager;
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

  private void doProcessEntity(Class<?> entityType) {
    LOG.trace("Processing Entity entityType={}", entityType.getCanonicalName());

    AtreusConfiguration configuration = environment.getConfiguration();

    // Create the managed entity with defaults
    ManagedEntityImpl managedEntity = new ManagedEntityImpl();
    String className = entityType.getSimpleName();
    managedEntity.setEntityType(entityType);
    managedEntity.setName(className);
    managedEntity.setTable(className);
    managedEntity.setKeySpace(configuration.getKeySpace());

    // Iterate the entity strategies and process
    for (AtreusEntityStrategy entityStrategy : configuration.getEntityStrategies()) {
      entityStrategy.processEntity(managedEntity);
    }

    for (Field javaField : entityType.getDeclaredFields()) {
      doProcessField(managedEntity, javaField);
    }

    LOG.debug("Registered Entity name={} entityClass={}", managedEntity.getName(), managedEntity.getEntityType());
    addEntity(managedEntity);
  }


  private void doProcessField(ManagedEntityImpl managedEntity, Field javaField) {
    String fieldName = javaField.getName();
    LOG.trace("Processing Field for Entity entityType={} javaField={}", managedEntity.getEntityType(), fieldName);

    AtreusConfiguration configuration = environment.getConfiguration();
    TypeManager typeManager = environment.getTypeManager();

    // Ignore transient field
    if (Modifier.isTransient(javaField.getModifiers())) {
      LOG.trace("Ignored as it is transient entityType={} javaField={}", managedEntity.getEntityType(), fieldName);
      return;
    }

    // Set the accessibility of the field to true
    javaField.setAccessible(true);

    // Create the managed field and set the defaults
    ManagedFieldImpl managedField = new ManagedFieldImpl();
    managedField.setColumn(fieldName);
    managedField.setJavaField(javaField);
    AtreusTypeAccessor typeAccessor = typeManager.findTypeAccessor(javaField.getType());
    LOG.trace("{}", javaField.getType());
    if (typeAccessor != null) {
      LOG.trace("Resolved typeAccessor={}", typeAccessor.getClass());
      managedField.setTypeAccessor(typeAccessor);
    }

    // Iterate the entity strategies and process
    for (AtreusEntityStrategy entityStrategy : configuration.getEntityStrategies()) {
      if (entityStrategy.isPrimaryKeyField(managedField)) {
        LOG.trace("Identified a primary key field entityType={} javaField={}", managedEntity.getEntityType(), fieldName);
        AtreusPrimaryKeyGenerator primaryKeyGenerator = typeManager.findPrimaryKeyGenerator(javaField.getType());
        if (primaryKeyGenerator != null) {
          managedEntity.setPrimaryKeyGenerator(primaryKeyGenerator);
        }
        entityStrategy.processPrimaryKeyField(managedEntity, managedField);
        managedEntity.setPrimaryKeyField(managedField);
      }
      else if (entityStrategy.isTtlField(managedField)) {
        LOG.trace("Identified a ttl key field entityType={} javaField={}", managedEntity.getEntityType(), fieldName);
        managedEntity.setTtlField(managedField);
      }
      else {
        entityStrategy.processField(managedEntity, managedField);
      }
    }

    // TODO validation: type accessor, primary keys & generator are serializable, validate ttl
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}