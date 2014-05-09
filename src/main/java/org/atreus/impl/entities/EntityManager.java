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

import org.atreus.core.ext.AtreusEntityStrategy;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.meta.MetaEntityImpl;
import org.atreus.impl.entities.meta.builder.*;
import org.atreus.impl.entities.proxy.ProxyManager;
import org.atreus.impl.listeners.EntityUpdateListener;
import org.atreus.impl.listeners.PrimaryKeyGeneratorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
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
  private List<BaseMetaPropertyBuilder> propertyBuilders = new ArrayList<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public EntityManager(Environment environment) {
    this.environment = environment;
    initPropertyBuilders();
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addEntity(Class<?> entityType) {
    buildMetaEntity(entityType);
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

  public void initMetaEntities() {

    // Iterate and process fields for each manged entity
    for (MetaEntityImpl metaEntity : metaEntityByClass.values()) {
      Class<?> entityType = metaEntity.getEntityType();

      // Build the fields
      for (Field field : entityType.getDeclaredFields()) {

        // Execute the meta property builder rule set
        for (BaseMetaPropertyBuilder propertyBuilder : propertyBuilders) {
          if (propertyBuilder.acceptField(metaEntity, field)) {
            break;
          }
        }

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

  private void addManagedEntity(MetaEntityImpl metaEntity) {
    metaEntityByClass.put(metaEntity.getEntityType(), metaEntity);
    metaEntityByName.put(metaEntity.getName(), metaEntity);
  }

  private MetaEntityImpl createMetaEntity(Class<?> entityType) {
    String className = entityType.getSimpleName();
    MetaEntityImpl managedEntity = new MetaEntityImpl();
    managedEntity.setEntityType(entityType);
    managedEntity.setName(className);
    managedEntity.setTable(className);
    managedEntity.setKeySpace(environment.getConfiguration().getKeySpace());
    return managedEntity;
  }

  private void buildMetaEntity(Class<?> entityType) {
    LOG.trace("Processing Entity entityType={}", entityType.getCanonicalName());

    // Create the managed entity with defaults
    MetaEntityImpl metaEntity = createMetaEntity(entityType);

    // Execute the meta property builder rule set
    for (BaseMetaPropertyBuilder metaPropertyBuilder : propertyBuilders) {
      if (metaPropertyBuilder.acceptEntity(metaEntity, entityType)) {
        break;
      }
    }

    // TODO optimize reuse of objectss
    metaEntity.addListener(new PrimaryKeyGeneratorListener());
    metaEntity.addListener(new EntityUpdateListener());

    // Build Proxy Class
    proxyManager.createProxyClass(entityType);

    LOG.debug("Registered Entity name={} {}", metaEntity.getName(), metaEntity.getEntityType());
    addManagedEntity(metaEntity);
  }

  private void initPropertyBuilders() {
    propertyBuilders.add(new DefaultMetaEntityBuilder(environment));
    propertyBuilders.add(new FilterTransientMetaFieldBuilder(environment));
    propertyBuilders.add(new MakeAccessibleMetaFieldBuilder(environment));
    propertyBuilders.add(new PrimaryKeyMetaFieldBuilder(environment));
    propertyBuilders.add(new TtlMetaFieldBuilder(environment));
    propertyBuilders.add(new CollectionMetaFieldBuilder(environment));
    propertyBuilders.add(new MapMetaFieldBuilder(environment));
    propertyBuilders.add(new SimpleMetaFieldBuilder(environment));
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}