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

import org.atreus.core.annotations.AtreusComposite;
import org.atreus.core.ext.meta.AtreusMetaComposite;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.meta.MetaCompositeImpl;
import org.atreus.impl.entities.meta.MetaEntityImpl;
import org.atreus.impl.entities.meta.StaticMetaSimpleFieldImpl;
import org.atreus.impl.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Default meta composite builder.
 *
 * @author Martin Crawford
 */
public class ParentMetaCompositeBuilder extends BaseMetaFieldBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public ParentMetaCompositeBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean acceptField(MetaEntityImpl parentMetaEntity, Field field) {
    AtreusComposite compositeAnnotation = field.getAnnotation(AtreusComposite.class);
    if (compositeAnnotation == null || !compositeAnnotation.parent()) {
      return false;
    }

    Class<?> entityType = resolveEntityType(field, compositeAnnotation.type());
    MetaEntityImpl childMetaEntity = (MetaEntityImpl) getEnvironment().getEntityManager().getMetaEntity(entityType);
    if (childMetaEntity == null) {
      throw new RuntimeException("Child entity type not managed " + entityType);
    }
    MetaCompositeImpl metaComposite = findOrCreateMetaComposite(parentMetaEntity, childMetaEntity);
    StaticMetaSimpleFieldImpl parentMetaField = createStaticMetaField(parentMetaEntity, field);
    metaComposite.setParentField(parentMetaField);

    // Create the parent primary key on the child
    createDynamicMetaField(childMetaEntity, parentMetaEntity.getTable() + "_" + ((AtreusMetaSimpleField) parentMetaEntity.getPrimaryKeyField()).getColumn(), field);
    return true;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected Class<?> resolveEntityType(Field field, Class<?> providedType) {
    if (providedType != null) {
      return providedType;
    }
    if (Collection.class.isAssignableFrom(field.getType())) {
      return ReflectionUtils.findCollectionValueClass(field);
    }
    if (Map.class.isAssignableFrom(field.getType())) {
      throw new RuntimeException("Map currently not supported for associations");
    }
    return field.getType();
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private MetaCompositeImpl findOrCreateMetaComposite(MetaEntityImpl parentMetaEntity, MetaEntityImpl childMetaEntity) {
    for (AtreusMetaComposite metaComposite : childMetaEntity.getCompositeAssociations()) {
      if (metaComposite.getParentEntity().equals(parentMetaEntity)) {
        return (MetaCompositeImpl) metaComposite;
      }
    }
    for (AtreusMetaComposite metaComposite : parentMetaEntity.getCompositeAssociations()) {
      if (metaComposite.getParentEntity().equals(childMetaEntity)) {
        return (MetaCompositeImpl) metaComposite;
      }
    }
    MetaCompositeImpl metaComposite = new MetaCompositeImpl();
    metaComposite.setParentEntity(parentMetaEntity);
    metaComposite.setChildEntity(childMetaEntity);
    return metaComposite;
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class