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
package org.atreus.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to map bean fields to methods. Maintains a cache of all mappings.
 *
 * @author Martin Crawford
 */
public class BeanUtils {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BeanUtils.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private static Set<Class<?>> cachedBeans = new HashSet<>();
  private static Map<CompositeMapKey, PropertyDescriptor> descriptorByMethod = new HashMap<>();
  private static Map<CompositeMapKey, PropertyDescriptor> descriptorByField = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public static String getField(Class<?> clazz, Method method) {
    PropertyDescriptor propertyDescriptor = descriptorByMethod.get(toKey(clazz, method));
    if (propertyDescriptor != null) {
      return propertyDescriptor.getName();
    }
    if (cachedBeans.contains(clazz)) {
      return null;
    }
    cacheBean(clazz);
    return getField(clazz, method);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private static void cacheBean(Class<?> clazz) {
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
      for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
        descriptorByMethod.put(toKey(clazz, propertyDescriptor.getReadMethod()), propertyDescriptor);
        descriptorByMethod.put(toKey(clazz, propertyDescriptor.getWriteMethod()), propertyDescriptor);
        descriptorByMethod.put(toKey(clazz, propertyDescriptor.getName()), propertyDescriptor);
      }
      cachedBeans.add(clazz);
    }
    catch (IntrospectionException e) {
      throw new RuntimeException("Exception caching bean for " + clazz, e);
    }
  }

  private static CompositeMapKey toKey(Class<?> clazz, Method method) {
    return new CompositeMapKey(clazz, method);
  }

  private static CompositeMapKey toKey(Class<?> clazz, String field) {
    return new CompositeMapKey(clazz, field);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class