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

import org.atreus.core.AtreusInitialisationException;
import org.atreus.core.ext.AtreusPrimaryKeyStrategy;
import org.atreus.core.ext.AtreusTtlStrategy;
import org.atreus.core.ext.AtreusType;
import org.atreus.core.ext.AtreusTypeStrategy;
import org.atreus.impl.AtreusEnvironment;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry of Type to Strategy mappings.
 *
 * @author Martin Crawford
 */
public class TypeManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(TypeManager.class);
  private static final Map<Class<?>, Class<?>> REGISTRY_PRIMITIVE_WRAPPERS = new HashMap<>();

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusEnvironment environment;
  private Map<Class<?>, Class<? extends AtreusPrimaryKeyStrategy>> primaryKeyStrategyMap = new HashMap<>();
  private Map<Class<?>, Class<? extends AtreusTtlStrategy>> ttlStrategyMap = new HashMap<>();
  private Map<Class<?>, Class<? extends AtreusTypeStrategy>> typeStrategyMap = new HashMap<>();

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

  public void addPrimaryKeyStrategy(Class<?> typeClass, Class<? extends AtreusPrimaryKeyStrategy> primaryKeyStrategy) {
    primaryKeyStrategyMap.put(typeClass, primaryKeyStrategy);
  }

  public void addTtlStrategy(Class<?> typeClass, Class<? extends AtreusTtlStrategy> ttlStrategy) {
    ttlStrategyMap.put(typeClass, ttlStrategy);
  }

  public void addTypeStrategy(Class<?> typeClass, Class<? extends AtreusTypeStrategy> typeStrategy) {
    typeStrategyMap.put(typeClass, typeStrategy);
  }

  public AtreusPrimaryKeyStrategy<?> findPrimaryKeyGenerator(Class<?> typeClass) {
    // Handle primitive types
    if (typeClass.isPrimitive()) {
      typeClass = REGISTRY_PRIMITIVE_WRAPPERS.get(typeClass);
    }

    // Iterate the registry and find the first assignable class
    for (Class<?> key : primaryKeyStrategyMap.keySet()) {
      if (key.isAssignableFrom(typeClass)) {
        return createPrimaryKeyStrategy(primaryKeyStrategyMap.get(key));
      }
    }
    return null;
  }

  public AtreusTtlStrategy<?> findTtlStrategy(Class<?> typeClass) {
    // Handle primitive types
    if (typeClass.isPrimitive()) {
      typeClass = REGISTRY_PRIMITIVE_WRAPPERS.get(typeClass);
    }

    // Iterate the registry and find the first assignable class
    for (Class<?> key : ttlStrategyMap.keySet()) {
      if (key.isAssignableFrom(typeClass)) {
        return createTtlStrategyInstance(ttlStrategyMap.get(key));
      }
    }
    return null;
  }

  public AtreusTypeStrategy<?> findTypeStrategy(Class<?> typeClass) {
    // Handle primitive types
    if (typeClass.isPrimitive()) {
      typeClass = REGISTRY_PRIMITIVE_WRAPPERS.get(typeClass);
    }

    // Iterate the registry and find the first assignable class
    for (Class<?> key : typeStrategyMap.keySet()) {
      if (key.isAssignableFrom(typeClass)) {
        Class<? extends AtreusTypeStrategy> typeStrategyClass = typeStrategyMap.get(key);
        LOG.trace("Resolved typeStrategyClass={} for typeClass={}", typeStrategyClass, typeClass);
        return createTypeStrategyInstance(typeStrategyClass);
      }
    }
    return null;
  }

  public void scanPaths(String[] paths) {
    for (String path : paths) {
      scanPath(path);
    }
  }

  @SuppressWarnings("unchecked")
  public void scanPath(String path) {
    Reflections reflections = new Reflections(path);
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(AtreusType.class);
    for (Class<?> clazz : classes) {
      AtreusType annotation = clazz.getAnnotation(AtreusType.class);
      if (AtreusTypeStrategy.class.isAssignableFrom(clazz)) {
        registerTypeStrategy((Class<? extends AtreusTypeStrategy>) clazz, annotation);
      }
      if (AtreusPrimaryKeyStrategy.class.isAssignableFrom(clazz)) {
        registerPrimaryKeyStrategy((Class<? extends AtreusPrimaryKeyStrategy>) clazz, annotation);
      }
      if (AtreusTtlStrategy.class.isAssignableFrom(clazz)) {
        registerTtlStrategy((Class<? extends AtreusTtlStrategy>) clazz, annotation);
      }
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private AtreusPrimaryKeyStrategy createPrimaryKeyStrategy(Class<? extends AtreusPrimaryKeyStrategy> primaryKeyStrategyClass) {
    try {
      return primaryKeyStrategyClass.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_REGISTER_PRIMARY_KEY_STRATEGY,
          primaryKeyStrategyClass.getCanonicalName());
    }
  }

  private AtreusTtlStrategy createTtlStrategyInstance(Class<? extends AtreusTtlStrategy> ttlStrategyClass) {
    try {
      return ttlStrategyClass.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_REGISTER_TTL_STRATEGY,
          ttlStrategyClass.getCanonicalName());
    }
  }

  private AtreusTypeStrategy createTypeStrategyInstance(Class<? extends AtreusTypeStrategy> typeStrategyClass) {
    try {
      return typeStrategyClass.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_REGISTER_TYPE_STRATEGY,
          typeStrategyClass.getCanonicalName());
    }
  }

  private void registerPrimaryKeyStrategy(Class<? extends AtreusPrimaryKeyStrategy> primaryKeyStrategyClass, AtreusType annotation) {
    Class<?> typeClass = annotation.value();
    LOG.trace("Registered primaryKeyStrategyClass={} for typeClass={}", primaryKeyStrategyClass, typeClass);
    addPrimaryKeyStrategy(typeClass, primaryKeyStrategyClass);
  }

  private void registerTtlStrategy(Class<? extends AtreusTtlStrategy> ttlStrategyClass, AtreusType annotation) {
    Class<?> typeClass = annotation.value();
    LOG.trace("Registered ttlStrategyClass={} for typeClass={}", ttlStrategyClass, typeClass);
    addTtlStrategy(typeClass, ttlStrategyClass);
  }

  private void registerTypeStrategy(Class<? extends AtreusTypeStrategy> typeStrategyClass, AtreusType annotation) {
    Class<?> typeClass = annotation.value();
    LOG.trace("Registered typeStrategyClass={} for typeClass={}", typeStrategyClass, typeClass);
    addTypeStrategy(typeClass, typeStrategyClass);

  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class