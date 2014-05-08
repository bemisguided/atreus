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
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.strategies.*;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.meta.MetaEntityImpl;
import org.atreus.impl.entities.meta.StaticMetaFieldImpl;
import org.atreus.impl.entities.proxy.ProxyManager;
import org.atreus.impl.types.TypeManager;
import org.atreus.impl.util.ReflectionUtils;
import org.atreus.impl.util.StringUtils;
import org.atreus.impl.visitors.PrimaryKeyGeneratorVisitorManaged;
import org.atreus.impl.visitors.UpdateManagedEntityVisitor;
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

  private final ProxyManager proxyManager = new ProxyManager();
  private final Environment environment;
  private Map<Class<?>, MetaEntityImpl> metaEntityByClass = new HashMap<>();
  private Map<String, MetaEntityImpl> metaEntityByName = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public EntityManager(Environment environment) {
    this.environment = environment;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addEntity(Class<?> entityType) {
    doProcessEntity(entityType);
  }

  public AtreusMetaEntity getMetaEntity(String name) {
    return metaEntityByName.get(name);
  }

  public AtreusMetaEntity getMetaEntity(Object entity) {
    return getMetaEntity(entity.getClass());
  }

  public AtreusMetaEntity getMetaEntity(Class<?> entityType) {
    return metaEntityByClass.get(entityType);
  }

  public AtreusManagedEntity toManagedEntity(Object entity) {
    AtreusMetaEntity metaEntity = getMetaEntity(entity);
    return proxyManager.createManagedEntity(metaEntity, entity);
  }

  public void processEntities() {
    // Iterate and process fields for each manged entity
    for (MetaEntityImpl managedEntity : metaEntityByClass.values()) {
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

  public void visitUpdate(AtreusSessionExt session, Object entity) {
    // TODO this does not belong here
    AtreusManagedEntity managedEntity = session.getManagedEntity(entity);
    MetaEntityImpl metaEntity = (MetaEntityImpl) environment.getEntityManager().getMetaEntity(entity.getClass());
    for (AtreusManagedEntityVisitor visitor : metaEntity.getUpdateVisitors()) {
      visitor.acceptEntity(session, managedEntity);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void addManagedEntity(MetaEntityImpl managedEntity) {
    metaEntityByClass.put(managedEntity.getEntityType(), managedEntity);
    metaEntityByName.put(managedEntity.getName(), managedEntity);
  }

  private void assertSinglePrimaryKey(MetaEntityImpl managedEntity, StaticMetaFieldImpl managedField) {
    if (managedEntity.getPrimaryKeyField() != null && !managedEntity.getPrimaryKeyField().equals(managedField)) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_MULTIPLE,
          managedEntity.getName());
    }
  }

  private MetaEntityImpl buildManagedEntity(Class<?> entityType) {
    String className = entityType.getSimpleName();
    MetaEntityImpl managedEntity = new MetaEntityImpl();
    managedEntity.setEntityType(entityType);
    managedEntity.setName(className);
    managedEntity.setTable(className);
    managedEntity.setKeySpace(environment.getConfiguration().getKeySpace());
    return managedEntity;
  }

  private StaticMetaFieldImpl buildJavaMetaField(MetaEntityImpl metaEntity, Field javaField) {
    StaticMetaFieldImpl managedField = new StaticMetaFieldImpl(metaEntity, javaField);
    managedField.setColumn(javaField.getName());
    return managedField;
  }

  private void doProcessEntity(Class<?> entityType) {
    LOG.trace("Processing Entity entityType={}", entityType.getCanonicalName());

    AtreusConfiguration configuration = environment.getConfiguration();

    // Create the managed entity with defaults
    MetaEntityImpl managedEntity = buildManagedEntity(entityType);

    // Iterate the entity strategies and update the managed entity
    for (AtreusEntityStrategy entityStrategy : configuration.getEntityStrategies()) {
      updateManagedEntity(managedEntity, entityStrategy);
    }

    // TODO optimize reuse of objectss
    managedEntity.getUpdateVisitors().add(new PrimaryKeyGeneratorVisitorManaged());
    managedEntity.getUpdateVisitors().add(new UpdateManagedEntityVisitor());

    // Build Proxy Class
    proxyManager.createProxyClass(entityType);

    LOG.debug("Registered Entity name={} {}", managedEntity.getName(), managedEntity.getEntityType());
    addManagedEntity(managedEntity);
  }

  @SuppressWarnings("unchecked")
  private void doProcessField(MetaEntityImpl managedEntity, Field javaField) {
    String fieldName = javaField.getName();
    LOG.trace("Processing Field for Entity entityType={} javaField={}", managedEntity.getEntityType(), fieldName);

    AtreusConfiguration configuration = environment.getConfiguration();

    // Set the accessibility of the field to true
    javaField.setAccessible(true);

    // Build the Managed Field with common values
    StaticMetaFieldImpl javaMetaField = buildJavaMetaField(managedEntity, javaField);

    // Iterate the entity strategies and process
    for (AtreusEntityStrategy entityStrategy : configuration.getEntityStrategies()) {

      if (metaEntityByClass.containsKey(javaField.getType())) {
        LOG.debug("Relationship detected {}", javaMetaField.getName());
        continue;
      }
      // Resolve the common Type Strategy (if available)
      resolveTypeStrategy(javaMetaField, entityStrategy);

      // Special fields

      // Primary Key
      if (entityStrategy.isPrimaryKeyField(javaMetaField)) {
        processPrimaryKeyField(managedEntity, javaMetaField, entityStrategy);
        continue;
      }

      // Time-to-live
      if (entityStrategy.isTtlField(javaMetaField)) {
        processTimeToLiveField(managedEntity, javaMetaField, entityStrategy);
        continue;
      }

      // Managed Fields
      managedEntity.addField(javaMetaField);

      // Collection
      if (Collection.class.isAssignableFrom(javaField.getType())) {
        processCollectionField(javaMetaField, entityStrategy);
        continue;
      }

      // Map
      if (Map.class.isAssignableFrom(javaField.getType())) {
        processMapField(javaMetaField, entityStrategy);
        continue;
      }

      updateField(javaMetaField, entityStrategy);

    }


    // TODO validation: Type Strategy, primary keys & generator are serializable, validate ttl
  }

  @SuppressWarnings("unchecked")
  private void processCollectionField(StaticMetaFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
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
  private void processMapField(StaticMetaFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
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

  private void processPrimaryKeyField(MetaEntityImpl managedEntity, StaticMetaFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
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

  private void processTimeToLiveField(MetaEntityImpl managedEntity, StaticMetaFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    resolveTtlStrategy(managedEntity, managedField, entityStrategy);
    managedEntity.setTtlField(managedField);
  }

  private void updateManagedEntity(MetaEntityImpl managedEntity, AtreusEntityStrategy entityStrategy) {
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

  private void updateField(StaticMetaFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    String column = entityStrategy.getFieldColumn(managedField);

    if (StringUtils.isNotNullOrEmpty(column)) {
      managedField.setColumn(column);
    }
  }

  private void updatePrimaryKey(StaticMetaFieldImpl managedField, AtreusEntityStrategy entityStrategy) {
    String column = entityStrategy.getPrimaryKeyColumn(managedField);

    if (StringUtils.isNotNullOrEmpty(column)) {
      managedField.setColumn(column);
    }
  }

  private void resolvePrimaryKeyStrategy(MetaEntityImpl metaEntity, StaticMetaFieldImpl metaField, AtreusEntityStrategy entityStrategy) {
    AtreusPrimaryKeyStrategy primaryKeyStrategy = entityStrategy.resolvePrimaryKeyStrategy(metaField);

    if (primaryKeyStrategy != null) {
      metaEntity.setPrimaryKeyGenerator(primaryKeyStrategy);
      return;
    }

    if (metaEntity.getPrimaryKeyStrategy() != null) {
      return;
    }

    TypeManager typeManager = environment.getTypeManager();

    primaryKeyStrategy = typeManager.findPrimaryKeyGenerator(metaField.getType());
    if (primaryKeyStrategy != null) {
      metaEntity.setPrimaryKeyGenerator(primaryKeyStrategy);
    }
  }

  private void resolveTtlStrategy(MetaEntityImpl metaEntity, StaticMetaFieldImpl metaField, AtreusEntityStrategy entityStrategy) {
    AtreusTtlStrategy ttlStrategy = entityStrategy.resolveTtlStrategy(metaField);

    if (ttlStrategy != null) {
      metaEntity.setTtlStrategy(ttlStrategy);
      return;
    }

    if (metaEntity.getTtlStrategy() != null) {
      return;
    }

    TypeManager typeManager = environment.getTypeManager();

    ttlStrategy = typeManager.findTtlStrategy(metaField.getType());
    if (ttlStrategy != null) {
      metaEntity.setTtlStrategy(ttlStrategy);
    }
  }

  @SuppressWarnings("unchecked")
  private void resolveTypeStrategy(StaticMetaFieldImpl metaFied, AtreusEntityStrategy entityStrategy) {
    Class<?> typeClass = metaFied.getType();
    AtreusTypeStrategy typeStrategy = entityStrategy.resolveTypeStrategy(metaFied);

    if (typeStrategy != null) {
      typeStrategy.setValueClass(typeClass);
      metaFied.setTypeStrategy(typeStrategy);
      return;
    }

    if (metaFied.getTypeStrategy() != null) {
      return;
    }

    TypeManager typeManager = environment.getTypeManager();

    typeStrategy = typeManager.findTypeStrategy(typeClass);
    if (typeStrategy != null) {
      typeStrategy.setValueClass(typeClass);
      metaFied.setTypeStrategy(typeStrategy);
    }
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}