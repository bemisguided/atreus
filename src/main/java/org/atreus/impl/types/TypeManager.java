package org.atreus.impl.types;

import org.atreus.core.types.AtreusType;
import org.atreus.core.types.AtreusTypeAccessor;
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

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Map<Class<?>, AtreusTypeAccessor<?>> registry = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public TypeManager() {
    scanPath("org.atreus.impl.types");
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addType(Class<?> typeClass, AtreusTypeAccessor<?> typeAccessor) {
    registry.put(typeClass, typeAccessor);
  }

  public AtreusTypeAccessor<?> findType(Class<?> typeClass) {
    return registry.get(typeClass);
  }

  public void scanPath(String path) {
    Reflections reflections = new Reflections(path);
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(AtreusType.class);
    for(Class<?> clazz : classes) {
      if (!AtreusTypeAccessor.class.isAssignableFrom(clazz)) {
        continue;
      }
      try {
        AtreusTypeAccessor<?> typeAccessor = (AtreusTypeAccessor) clazz.newInstance();
        AtreusType annotation = clazz.getAnnotation(AtreusType.class);
        Class<?> typeClass = annotation.value();
        LOG.debug("Registered typeAccessor={} for typeClass={}", typeAccessor.getClass(), typeClass);
        addType(typeClass, typeAccessor);
      }catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class