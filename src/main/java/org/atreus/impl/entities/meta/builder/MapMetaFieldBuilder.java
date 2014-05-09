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
package org.atreus.impl.entities.meta.builder;

import org.atreus.core.AtreusInitialisationException;
import org.atreus.core.annotations.AtreusCollection;
import org.atreus.core.annotations.AtreusField;
import org.atreus.core.annotations.AtreusMap;
import org.atreus.core.annotations.NullType;
import org.atreus.core.ext.CQLDataType;
import org.atreus.core.ext.strategies.AtreusMapTypeStrategy;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.meta.MetaEntityImpl;
import org.atreus.impl.entities.meta.StaticMetaFieldImpl;
import org.atreus.impl.util.ReflectionUtils;
import org.atreus.impl.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Map meta field builder.
 *
 * @author Martin Crawford
 */
public class MapMetaFieldBuilder extends BaseMetaFieldBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public MapMetaFieldBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean acceptField(MetaEntityImpl metaEntity, Field field) {

    // Check if this is a Collection class
    if (!Map.class.isAssignableFrom(field.getType())) {
      return false;
    }

    // Create the static field
    StaticMetaFieldImpl mapMetaField = createStaticMetaField(metaEntity, field);

    // Check for a field annotation
    AtreusField fieldAnnotation = field.getAnnotation(AtreusField.class);
    if (fieldAnnotation != null) {
      String fieldColumn = fieldAnnotation.value();
      if (StringUtils.isNotNullOrEmpty(fieldColumn)) {
        mapMetaField.setColumn(fieldColumn);
      }
    }

    // Resolve the type strategy
    resolveTypeStrategy(metaEntity, mapMetaField, field);

    // Resolve the key and value classes and corresponding CQL data types
    Class<?> valueClass = resolveMapValueClass(field);
    Class<?> keyClass = resolveMapKeyClass(field);
    CQLDataType valueDataType = CQLDataType.mapClassToDataType(valueClass);
    CQLDataType keyDataType = CQLDataType.mapClassToDataType(keyClass);

    if (valueClass == null || valueDataType == null) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_COLLECTION_VALUE_TYPE_NOT_RESOLVABLE,
          mapMetaField);
    }

    if (keyClass == null || keyDataType == null) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_MAP_KEY_TYPE_NOT_RESOLVABLE,
          mapMetaField);
    }

    if (!(mapMetaField.getTypeStrategy() instanceof AtreusMapTypeStrategy)) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_MAP_TYPE_STRATEGY_INVALID,
          mapMetaField, mapMetaField.getTypeStrategy());
    }

    AtreusMapTypeStrategy mapTypeStrategy = (AtreusMapTypeStrategy) mapMetaField.getTypeStrategy();
    mapTypeStrategy.setValueDataType(valueDataType);
    mapTypeStrategy.setKeyDataType(keyDataType);

    // Add to the meta entity
    metaEntity.addField(mapMetaField);

    return true;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected Class<?> resolveMapValueClass(Field field) {
    AtreusCollection collectionAnnotation = field.getAnnotation(AtreusCollection.class);
    if (collectionAnnotation != null && !NullType.class.isAssignableFrom(collectionAnnotation.type())) {
      return collectionAnnotation.type();
    }
    return ReflectionUtils.findMapValueClass(field);
  }

  protected Class<?> resolveMapKeyClass(Field field) {
    AtreusMap mapAnnotation = field.getAnnotation(AtreusMap.class);
    if (mapAnnotation != null && !NullType.class.isAssignableFrom(mapAnnotation.key())) {
      return mapAnnotation.key();
    }
    return ReflectionUtils.findMapKeyClass(field);
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class