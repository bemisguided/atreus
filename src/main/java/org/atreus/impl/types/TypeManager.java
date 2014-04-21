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
package org.atreus.impl.types;

import org.atreus.core.annotations.AtreusType;
import org.atreus.core.ext.AtreusTypeAccessor;
import org.atreus.impl.AtreusEnvironment;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry of Type Accessors.
 *
 * @author Martin Crawford
 */
public class TypeManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(TypeManager.class);
  private static final Map<Class<?>, Class<?>> REGISTRY_PRIMITIVE_WRAPPERS = new HashMap<>();

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusEnvironment environment;
  private Map<Class<?>, AtreusTypeAccessor<?>> registry = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  static {
    REGISTRY_PRIMITIVE_WRAPPERS.put(boolean.class, Boolean.class);
    REGISTRY_PRIMITIVE_WRAPPERS.put(byte.class, Byte.class);
    REGISTRY_PRIMITIVE_WRAPPERS.put(char.class, Character.class);
    REGISTRY_PRIMITIVE_WRAPPERS.put(double.class, Double.class);
    REGISTRY_PRIMITIVE_WRAPPERS.put(float.class, Float.class);
    REGISTRY_PRIMITIVE_WRAPPERS.put(int.class, Integer.class);
    REGISTRY_PRIMITIVE_WRAPPERS.put(long.class, Long.class);
    REGISTRY_PRIMITIVE_WRAPPERS.put(short.class, Short.class);
    REGISTRY_PRIMITIVE_WRAPPERS.put(void.class, Void.class);
  }

  public TypeManager(AtreusEnvironment environment) {
    this.environment = environment;
    scanPath("org.atreus.impl.types");
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addType(Class<?> typeClass, AtreusTypeAccessor<?> typeAccessor) {
    registry.put(typeClass, typeAccessor);
  }

  public AtreusTypeAccessor<?> findType(Class<?> typeClass) {
    // Handle primitive types
    if (typeClass.isPrimitive()) {
      typeClass = REGISTRY_PRIMITIVE_WRAPPERS.get(typeClass);
    }

    // Iterate the registry and find the first assignable class
    for (Class<?> key : registry.keySet()) {
      if (key.isAssignableFrom(typeClass)) {
        return registry.get(key);
      }
    }
    return null;
  }

  public void scanPath(String path) {
    Reflections reflections = new Reflections(path);
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(AtreusType.class);
    for (Class<?> clazz : classes) {
      if (!AtreusTypeAccessor.class.isAssignableFrom(clazz)) {
        continue;
      }
      try {
        AtreusTypeAccessor<?> typeAccessor = (AtreusTypeAccessor) clazz.newInstance();
        AtreusType annotation = clazz.getAnnotation(AtreusType.class);
        Class<?> typeClass = annotation.value();
        LOG.debug("Registered typeAccessor={} for typeClass={}", typeAccessor.getClass(), typeClass);
        addType(typeClass, typeAccessor);
      }
      catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class