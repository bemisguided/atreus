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
package org.atreus.impl.entities.proxy;

import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.entities.ManagedEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Proxy Manager.
 *
 * @author Martin Crawford
 */
public class ProxyManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ProxyManager.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Map<Class<?>, Class<AtreusManagedEntity>> proxyClasses = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @SuppressWarnings("unchecked")
  public void createProxyClass(Class<?> entityType) {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setSuperclass(entityType);
    proxyFactory.setInterfaces(new Class[]{AtreusManagedEntity.class});
    Class<AtreusManagedEntity> proxyClass = proxyFactory.createClass();
    proxyClasses.put(entityType, proxyClass);
  }

  public AtreusManagedEntity createManagedEntity(AtreusMetaEntity metaEntity, Object entity) {
    Class<AtreusManagedEntity> proxyClass = proxyClasses.get(metaEntity.getEntityType());
    if (proxyClass == null) {
      throw new RuntimeException("No proxy class available for " + metaEntity.getEntityType());
    }
    try {
      AtreusManagedEntity managedEntityProxy = proxyClass.newInstance();
      ManagedEntityImpl managedEntityImpl = new ManagedEntityImpl(metaEntity, entity);
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

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class