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
package org.atreus.impl.core.mappings.associations.composite.builders;

import org.atreus.core.annotations.AtreusCompositeParent;
import org.atreus.core.annotations.NullType;
import org.atreus.core.ext.listeners.AtreusEntityListener;
import org.atreus.core.ext.meta.AtreusAssociationType;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.impl.core.Environment;
import org.atreus.impl.core.mappings.BaseFieldEntityMetaComponentBuilder;
import org.atreus.impl.core.mappings.associations.composite.listeners.CompositeChildUpdateListener;
import org.atreus.impl.core.mappings.associations.composite.listeners.CompositeParentDeleteListener;
import org.atreus.impl.core.mappings.associations.composite.listeners.CompositeParentFetchListener;
import org.atreus.impl.core.mappings.associations.composite.listeners.CompositeParentUpdateListener;
import org.atreus.impl.core.mappings.associations.composite.meta.CompositeChildPrimaryKeyMetaFieldImpl;
import org.atreus.impl.core.mappings.associations.composite.meta.MetaAssociationFieldImpl;
import org.atreus.impl.core.mappings.associations.meta.MetaAssociatedEntityImpl;
import org.atreus.impl.core.mappings.associations.meta.MetaAssociationImpl;
import org.atreus.impl.core.mappings.entities.meta.MetaEntityImpl;
import org.atreus.impl.core.mappings.entities.meta.MetaTableImpl;
import org.atreus.impl.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Default meta composite builder.
 *
 * @author Martin Crawford
 */
public class CompositeParentComponentBuilder extends BaseFieldEntityMetaComponentBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final AtreusEntityListener COMPOSITE_PARENT_UPDATE_LISTENER = new CompositeParentUpdateListener();
  private static final AtreusEntityListener COMPOSITE_PARENT_FETCH_LISTENER = new CompositeParentFetchListener();
  private static final AtreusEntityListener COMPOSITE_PARENT_DELETE_LISTENER = new CompositeParentDeleteListener();
  private static final AtreusEntityListener COMPOSITE_CHILD_UPDATE_LISTENER = new CompositeChildUpdateListener();

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public CompositeParentComponentBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean acceptsField(MetaEntityImpl metaEntity, Field field) {
    return field.getAnnotation(AtreusCompositeParent.class) != null;
  }

  @Override
  public boolean handleField(MetaEntityImpl parentMetaEntity, Field field) {
    AtreusCompositeParent compositeAnnotation = field.getAnnotation(AtreusCompositeParent.class);

    // Resolve the child entity type
    Class<?> childEntityType = resolveEntityType(field, compositeAnnotation.type());
    MetaEntityImpl childMetaEntity = (MetaEntityImpl) getEnvironment().getMetaManager().getEntity(childEntityType);

    // Create a meta composite
    MetaAssociationImpl metaAssociation = createMetaAssociation(parentMetaEntity, childMetaEntity);
    metaAssociation.setType(AtreusAssociationType.COMPOSITE);
    parentMetaEntity.addAssociation(metaAssociation);

    // Build Composite Parent meta entity
    buildCompositeParentEntity(parentMetaEntity, field, metaAssociation);

    // Build Composite Child meta entity
    buildCompositeChildEntity(parentMetaEntity, childMetaEntity, metaAssociation);

    // Add the appropriate listeners
    parentMetaEntity.addListener(COMPOSITE_PARENT_UPDATE_LISTENER);
    parentMetaEntity.addListener(COMPOSITE_PARENT_FETCH_LISTENER);
    parentMetaEntity.addListener(COMPOSITE_PARENT_DELETE_LISTENER);
    childMetaEntity.addListener(COMPOSITE_CHILD_UPDATE_LISTENER);
    return true;
  }

  @Override
  public void validateField(MetaEntityImpl metaEntity, Field field) {
    AtreusCompositeParent compositeAnnotation = field.getAnnotation(AtreusCompositeParent.class);

    // Resolve the child entity type
    Class<?> childEntityType = resolveEntityType(field, compositeAnnotation.type());

    // Resolve the child meta entity
    AtreusMetaEntity childMetaEntity = getEnvironment().getMetaManager().getEntity(childEntityType);
    if (childMetaEntity == null) {
      throw new RuntimeException("Child entity type not managed " + childEntityType);
    }

    // Ensure that the primary key is a simple field type
    if (!(childMetaEntity.getPrimaryKeyField() instanceof AtreusMetaSimpleField)) {
      throw new RuntimeException("Primary key of a child must be a simple field type " + childEntityType);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected Class<?> resolveEntityType(Field field, Class<?> providedType) {
    if (providedType != null && !providedType.equals(NullType.class)) {
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

  private void buildCompositeChildEntity(MetaEntityImpl parentMetaEntity, MetaEntityImpl childMetaEntity, MetaAssociationImpl metaAssociation) {

    // Create the primary key reference on the child entity
    String parentKeyName = parentMetaEntity.getTable().getName() + "_" + ((AtreusMetaSimpleField) parentMetaEntity.getPrimaryKeyField()).getColumn();
    AtreusMetaSimpleField parentKeyField = createDynamicMetaSimpleField(childMetaEntity, parentKeyName, parentMetaEntity.getPrimaryKeyField().getType());
    parentKeyField.setTypeStrategy(((AtreusMetaSimpleField) parentMetaEntity.getPrimaryKeyField()).getTypeStrategy());

    // Update the meta composite
    ((MetaAssociatedEntityImpl) metaAssociation.getOwner()).setAssociationKeyField(parentKeyField);
    ((MetaAssociatedEntityImpl) metaAssociation.getAssociation()).setAssociationKeyField(childMetaEntity.getPrimaryKeyField());
    ((MetaTableImpl) metaAssociation.getOutboundTable()).setKeySpace(childMetaEntity.getTable().getKeySpace());
    ((MetaTableImpl) metaAssociation.getOutboundTable()).setName(childMetaEntity.getTable().getName());

    // Create the composite child primary key using the existing primary key with the parent reference
    CompositeChildPrimaryKeyMetaFieldImpl childKeyField = new CompositeChildPrimaryKeyMetaFieldImpl(childMetaEntity, parentKeyName, parentKeyField);
    childMetaEntity.setPrimaryKeyField(childKeyField);
  }

  private void buildCompositeParentEntity(MetaEntityImpl parentMetaEntity, Field field, MetaAssociationImpl metaAssociation) {
    MetaAssociationFieldImpl parentMetaField = new MetaAssociationFieldImpl(parentMetaEntity, field, metaAssociation);
    ((MetaAssociatedEntityImpl) metaAssociation.getOwner()).setAssociationField(parentMetaField);
    parentMetaEntity.addField(parentMetaField);
  }

  private MetaAssociationImpl createMetaAssociation(MetaEntityImpl ownerEntity, MetaEntityImpl associationEntity) {
    MetaAssociationImpl metaAssociation = new MetaAssociationImpl();
    ((MetaAssociatedEntityImpl) metaAssociation.getOwner()).setMetaEntity(ownerEntity);
    ((MetaAssociatedEntityImpl) metaAssociation.getAssociation()).setMetaEntity(associationEntity);
    return metaAssociation;
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class