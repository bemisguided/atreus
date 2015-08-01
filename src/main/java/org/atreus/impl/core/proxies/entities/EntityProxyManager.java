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
package org.atreus.impl.core.proxies.entities;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.core.entities.EntityAccessor;
import org.atreus.impl.core.entities.ManagedCollection;
import org.atreus.impl.core.entities.ManagedEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Entity Proxy Manager.
 *
 * @author Martin Crawford
 */
public class EntityProxyManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(EntityProxyManager.class);

  private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
    public boolean isHandled(Method m) {
      if (m.getParameterTypes().length == 0 && m.getName().equals("finalize")) {
        return false;
      }
      return true;
    }
  };

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Map<Class<?>, Class<AtreusManagedEntity>> entityProxyClasses = new HashMap<>();
  private Map<Class<?>, Class<ManagedCollection>> collectionProxyClasses = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @SuppressWarnings("unchecked")
  public void defineEntityProxy(Class<?> entityType) {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setSuperclass(entityType);
    proxyFactory.setInterfaces(new Class[]{AtreusManagedEntity.class, EntityAccessor.class});
    proxyFactory.setFilter(FINALIZE_FILTER);
    Class<AtreusManagedEntity> proxyClass = proxyFactory.createClass();
    entityProxyClasses.put(entityType, proxyClass);
  }

  public AtreusManagedEntity wrapEntity(AtreusSessionExt session, AtreusMetaEntity metaEntity, Object entity) {
    Class<AtreusManagedEntity> proxyClass = entityProxyClasses.get(metaEntity.getEntityType());
    if (proxyClass == null) {
      throw new RuntimeException("No proxy class available for " + metaEntity.getEntityType());
    }
    try {
      AtreusManagedEntity managedEntityProxy = proxyClass.newInstance();
      ManagedEntityImpl managedEntityImpl = new ManagedEntityImpl(session, metaEntity, entity);
      MethodHandler proxyHandler = new EntityProxyHandler(managedEntityImpl, entity);
      ((Proxy) managedEntityProxy).setHandler(proxyHandler);
      return managedEntityProxy;
    }
    catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Proxy class could not be created for " + metaEntity.getEntityType());
    }
  }

  public AtreusManagedEntity createEntity(AtreusSessionExt session, AtreusMetaEntity metaEntity) {
    Class<AtreusManagedEntity> proxyClass = entityProxyClasses.get(metaEntity.getEntityType());
    if (proxyClass == null) {
      throw new RuntimeException("No proxy class available for " + metaEntity.getEntityType());
    }
    try {
      AtreusManagedEntity managedEntityProxy = proxyClass.newInstance();
      ManagedEntityImpl managedEntityImpl = new ManagedEntityImpl(session, metaEntity, managedEntityProxy);
      MethodHandler proxyHandler = new EntityProxyHandler(managedEntityImpl, managedEntityProxy);
      ((Proxy) managedEntityProxy).setHandler(proxyHandler);
      return managedEntityProxy;
    }
    catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Proxy class could not be created for " + metaEntity.getEntityType());
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private Class<? extends Collection> resolveCollectionClass(Class<? extends Collection> collectionType) {
    if (!collectionType.isInterface()) {
      return collectionType;
    }
    if (SortedSet.class.isAssignableFrom(collectionType)) {
      return TreeSet.class;
    }
    if (Set.class.isAssignableFrom(collectionType)) {
      return HashSet.class;
    }
    if (List.class.isAssignableFrom(collectionType)) {
      return ArrayList.class;
    }
    throw new RuntimeException("Could not resolve collection type " + collectionType);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class