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

import org.atreus.core.annotations.AtreusEntity;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.builder.MetaEntityBuilder;
import org.atreus.impl.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
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

  private final MetaEntityBuilder metaEntityBuilder;
  private final Environment environment;
  private Map<Class<?>, AtreusMetaEntity> metaEntityByClass = new HashMap<>();
  private Map<String, AtreusMetaEntity> metaEntityByName = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public EntityManager(Environment environment) {
    this.environment = environment;
    this.metaEntityBuilder = new MetaEntityBuilder(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addEntityType(Class<?> entityType) {
    metaEntityBuilder.addEntityType(entityType);
  }

  public void addManagedEntity(AtreusMetaEntity metaEntity) {
    metaEntityByClass.put(metaEntity.getEntityType(), metaEntity);
    metaEntityByName.put(metaEntity.getName(), metaEntity);
  }

  public AtreusMetaEntity getMetaEntity(String name) {
    return metaEntityByName.get(name);
  }

  public AtreusMetaEntity getMetaEntity(Object entity) {
    if (entity instanceof AtreusManagedEntity) {
      return ((AtreusManagedEntity) entity).getMetaEntity();
    }
    return getMetaEntity(entity.getClass());
  }

  public AtreusMetaEntity getMetaEntity(Class<?> entityType) {
    return metaEntityByClass.get(entityType);
  }

  public Collection<AtreusMetaEntity> getMetaEntities() {
    return metaEntityByName.values();
  }

  public void init() {
    metaEntityBuilder.build();
  }

  public AtreusManagedEntity manageEntity(AtreusSessionExt session, Object entity) {
    AtreusMetaEntity metaEntity = getMetaEntity(entity);
    return environment.getProxyManager().createManagedEntity(session, metaEntity, entity);
  }

  public void scanPaths(String[] paths) {
    for (String path : paths) {
      scanPath(path);
    }
  }

  public void scanPath(String path) {
    LOG.trace("Scanning classpath path={}", path);

    // Resolve all candidate entity classes using the configured entity strategies
    Set<Class<?>> entityTypes = ReflectionUtils.findClassesWithAnnotation(path, AtreusEntity.class);

    // Iterate and process each found entity class
    for (Class<?> entityType : entityTypes) {
      addEntityType(entityType);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}