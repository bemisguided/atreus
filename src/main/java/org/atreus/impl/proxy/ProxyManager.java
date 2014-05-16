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
package org.atreus.impl.proxy;

import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.entities.ManagedEntityImpl;
import org.atreus.impl.entities.collections.ManagedCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Proxy Manager.
 *
 * @author Martin Crawford
 */
public class ProxyManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ProxyManager.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Map<Class<?>, Class<AtreusManagedEntity>> entityProxyClasses = new HashMap<>();
  private Map<Class<?>, Class<ManagedCollection>> collectionProxyClasses = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @SuppressWarnings("unchecked")
  public void defineEntityProxy(Class<?> entityType) {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setSuperclass(entityType);
    proxyFactory.setInterfaces(new Class[]{AtreusManagedEntity.class});
    Class<AtreusManagedEntity> proxyClass = proxyFactory.createClass();
    entityProxyClasses.put(entityType, proxyClass);
  }

  @SuppressWarnings("unchecked")
  public Class<ManagedCollection> defineCollectionProxy(Class<? extends Collection> collectionType) {
    Class<? extends Collection> implType = resolveCollectionClass(collectionType);
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setSuperclass(implType);
    proxyFactory.setInterfaces(new Class[]{ManagedCollection.class});
    Class<ManagedCollection> proxyClass = proxyFactory.createClass();
    collectionProxyClasses.put(collectionType, proxyClass);
    return proxyClass;
  }

  public ManagedCollection createManagedCollection(Class<? extends Collection> collectionType) {
    Class<ManagedCollection> proxyClass = collectionProxyClasses.get(collectionType);
    if (proxyClass == null) {
      proxyClass = defineCollectionProxy(collectionType);
    }
    try {
      ManagedCollection managedCollection = proxyClass.newInstance();
      Class<? extends  Collection> collectionClass = resolveCollectionClass(collectionType);
      Collection collection = collectionClass.newInstance();
      CollectionProxyHandler proxyHandler = new CollectionProxyHandler(collection);
      ((Proxy) managedCollection).setHandler(proxyHandler);
      return managedCollection;
    }
    catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Proxy class could not be created for " + collectionType);
    }
  }

  public AtreusManagedEntity createManagedEntity(AtreusSessionExt session, AtreusMetaEntity metaEntity, Object entity) {
    Class<AtreusManagedEntity> proxyClass = entityProxyClasses.get(metaEntity.getEntityType());
    if (proxyClass == null) {
      throw new RuntimeException("No proxy class available for " + metaEntity.getEntityType());
    }
    try {
      AtreusManagedEntity managedEntityProxy = proxyClass.newInstance();
      ManagedEntityImpl managedEntityImpl = new ManagedEntityImpl(session, metaEntity, entity);
      EntityProxyHandler proxyHandler = new EntityProxyHandler(managedEntityImpl, entity);
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
    if (Queue.class.isAssignableFrom(collectionType)) {
      return LinkedList.class;
    }
    if (List.class.isAssignableFrom(collectionType)) {
      return ArrayList.class;
    }
    throw new RuntimeException("Could not resolve collection type " + collectionType);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class