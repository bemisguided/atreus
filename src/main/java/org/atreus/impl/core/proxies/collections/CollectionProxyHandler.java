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
package org.atreus.impl.core.proxies.collections;

import javassist.util.proxy.MethodHandler;
import org.atreus.impl.core.entities.ManagedCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Proxy Handler for a Managed Collection.
 *
 * @author Martin Crawford
 */
public class CollectionProxyHandler implements MethodHandler {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(CollectionProxyHandler.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final ManagedCollection managedCollection;
  private final Collection collection;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public CollectionProxyHandler(Collection collection) {
    this.collection = collection;
    this.managedCollection = new ProxyManagedCollection(collection);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public Object invoke(Object self, Method overridden, Method forwarder, Object[] args) throws Throwable {
    try {
      if (forwarder == null) {
        return overridden.invoke(managedCollection, args);
      }

      return overridden.invoke(collection, args);
    }
    catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class