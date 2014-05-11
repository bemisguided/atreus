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
import org.atreus.core.annotations.NullType;
import org.atreus.core.ext.AtreusCQLDataType;
import org.atreus.core.ext.strategies.AtreusCollectionTypeStrategy;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.meta.MetaEntityImpl;
import org.atreus.impl.entities.meta.StaticMetaSimpleFieldImpl;
import org.atreus.impl.util.ReflectionUtils;
import org.atreus.impl.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Collections meta field builder.
 *
 * @author Martin Crawford
 */
public class CollectionMetaFieldBuilder extends BaseMetaFieldBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public CollectionMetaFieldBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean acceptField(MetaEntityImpl metaEntity, Field field) {

    // Check if this is a Collection class
    if (!Collection.class.isAssignableFrom(field.getType())) {
      return false;
    }

    // Create the static field
    StaticMetaSimpleFieldImpl collectionMetaField = createStaticMetaField(metaEntity, field);

    // Check for a field annotation
    AtreusField fieldAnnotation = field.getAnnotation(AtreusField.class);
    if (fieldAnnotation != null) {
      String fieldColumn = fieldAnnotation.value();
      if (StringUtils.isNotNullOrEmpty(fieldColumn)) {
        collectionMetaField.setColumn(fieldColumn);
      }
    }

    // Resolve the type strategy
    resolveTypeStrategy(metaEntity, collectionMetaField, field);

    // Resolve the value class and corresponding CQL data type
    Class<?> valueClass = resolveCollectionValueClass(field);
    AtreusCQLDataType valueDataType = AtreusCQLDataType.mapClassToDataType(valueClass);

    if (valueClass == null || valueDataType == null) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_COLLECTION_VALUE_TYPE_NOT_RESOLVABLE,
          collectionMetaField);
    }

    if (!(collectionMetaField.getTypeStrategy() instanceof AtreusCollectionTypeStrategy)) {
      throw new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_COLLECTION_TYPE_STRATEGY_INVALID,
          collectionMetaField, collectionMetaField.getTypeStrategy());
    }

    AtreusCollectionTypeStrategy collectionTypeStrategy = (AtreusCollectionTypeStrategy) collectionMetaField.getTypeStrategy();
    collectionTypeStrategy.setValueDataType(valueDataType);

    // Add to the meta entity
    metaEntity.addField(collectionMetaField);

    return true;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected Class<?> resolveCollectionValueClass(Field field) {
    AtreusCollection collectionAnnotation = field.getAnnotation(AtreusCollection.class);
    if (collectionAnnotation != null && !NullType.class.isAssignableFrom(collectionAnnotation.type())) {
      return collectionAnnotation.type();
    }
    return ReflectionUtils.findCollectionValueClass(field);
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class