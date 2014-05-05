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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class the handle reflection related functions.
 *
 * @author Martin Crawford
 */
public class ReflectionUtils {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);
  private static final Map<Class<?>, Class<?>> REGISTRY_PRIMITIVE_WRAPPERS = new HashMap<>();

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

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

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public static Class<?> findCollectionValueClass(Field field) {
    if (!Collection.class.isAssignableFrom(field.getType())) {
      return null;
    }

    Type[] genericTypes = findGenericTypes(field);
    if (genericTypes == null || genericTypes.length != 1) {
      return null;
    }
    return (Class) genericTypes[0];
  }

  public static Class<?> findMapKeyClass(Field field) {
    if (!Map.class.isAssignableFrom(field.getType())) {
      return null;
    }

    Type[] genericTypes = findGenericTypes(field);
    if (genericTypes == null || genericTypes.length != 2) {
      return null;
    }
    return (Class) genericTypes[0];
  }

  public static Class<?> findMapValueClass(Field field) {
    if (!Map.class.isAssignableFrom(field.getType())) {
      return null;
    }

    Type[] genericTypes = findGenericTypes(field);
    if (genericTypes == null || genericTypes.length != 2) {
      return null;
    }
    return (Class) genericTypes[1];
  }

  public static Type[] findGenericTypes(Field field) {
    if (!(field.getGenericType() instanceof ParameterizedType)) {
      return null;
    }
    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
    return parameterizedType.getActualTypeArguments();
  }

  public static Class<?> toPrimitiveWrapper(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      return REGISTRY_PRIMITIVE_WRAPPERS.get(clazz);
    }
    return clazz;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class