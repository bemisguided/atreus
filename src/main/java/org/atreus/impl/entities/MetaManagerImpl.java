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

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.*;
import org.atreus.impl.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry of meta model.
 *
 * @author Martin Crawford
 */
public class MetaManagerImpl implements AtreusMetaManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(MetaManagerImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final Environment environment;
  private Map<Class<?>, AtreusMetaComponent> metaComponentByClass = new HashMap<>();
  private Map<String, AtreusMetaComponent> metaComponentByName = new HashMap<>();
  private Map<Class<?>, AtreusMetaEntity> metaEntityByClass = new HashMap<>();
  private Map<String, AtreusMetaEntity> metaEntityByName = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public MetaManagerImpl(Environment environment) {
    this.environment = environment;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addMetaComponent(AtreusMetaComponent metaComponent) {
    metaComponentByClass.put(metaComponent.getComponentType(), metaComponent);
    metaComponentByName.put(metaComponent.getName(), metaComponent);
  }

  public void addMetaEntity(AtreusMetaEntity metaEntity) {
    metaEntityByClass.put(metaEntity.getEntityType(), metaEntity);
    metaEntityByName.put(metaEntity.getName(), metaEntity);
  }

  @Override
  public AtreusMetaComponent[] getComponents() {
    AtreusMetaComponent[] result = new AtreusMetaComponent[metaComponentByName.size()];
    return metaComponentByName.values().toArray(result);
  }

  @Override
  public AtreusMetaComponent getComponent(String name) {
    return metaComponentByName.get(name);
  }

  @Override
  public AtreusMetaComponent getComponent(Class<?> componentType) {
    return metaComponentByClass.get(componentType);
  }

  @Override
  public AtreusMetaComponent getComponent(Object entity) {
    return getComponent(entity.getClass());
  }

  @Override
  public AtreusMetaEntity getEntity(String name) {
    return metaEntityByName.get(name);
  }

  @Override
  public AtreusMetaEntity getEntity(Object entity) {
    if (entity instanceof AtreusManagedEntity) {
      return ((AtreusManagedEntity) entity).getMetaEntity();
    }
    return getEntity(entity.getClass());
  }

  @Override
  public AtreusMetaEntity getEntity(Class<?> entityType) {
    return metaEntityByClass.get(entityType);
  }

  @Override
  public AtreusMetaEntity[] getEntities() {
    AtreusMetaEntity[] result = new AtreusMetaEntity[metaEntityByName.size()];
    return metaEntityByName.values().toArray(result);
  }

  @SuppressWarnings("unchecked")
  public AtreusManagedEntity manageEntity(AtreusSessionExt session, Object entity) {
    AtreusMetaEntity metaEntity = getEntity(entity);
    AtreusManagedEntity managedEntity = environment.getProxyManager().createManagedEntity(session, metaEntity, entity);
    for(AtreusMetaField metaField : metaEntity.getFields()){
      if (!(metaField instanceof AtreusMetaAssociationField)) {
        continue;
      }
      if (!Collection.class.isAssignableFrom(metaField.getType())) {
        continue;
      }
      ManagedCollection managedCollection = environment.getProxyManager().createManagedCollection((Class<? extends Collection>) metaField.getType());
      Collection collection = (Collection) managedEntity.getFieldValue(metaField);
      if (collection != null) {
        ((Collection) managedCollection).addAll(collection);
      }
      managedEntity.setFieldValue(metaField, managedCollection);
    }
    return managedEntity;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}