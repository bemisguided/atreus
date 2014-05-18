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
package org.atreus.impl.entities.builder;

import org.atreus.core.annotations.AtreusEntity;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.meta.MetaEntityImpl;
import org.atreus.impl.entities.meta.MetaTableImpl;
import org.atreus.impl.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Meta Builder.
 *
 * @author Martin Crawford
 */
public class MetaBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(MetaBuilder.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final Environment environment;
  private final Set<Class<?>> entityTypes = new HashSet<>();
  private final List<BaseEntityMetaComponentBuilder> entityPropertyBuilders = new ArrayList<>();
  private final List<BaseEntityMetaComponentBuilder> fieldPropertyBuilders = new ArrayList<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public MetaBuilder(Environment environment) {
    this.environment = environment;
    init();
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addEntityType(Class<?> entityType) {
    entityTypes.add(entityType);
  }

  public void build() {
    buildMetaEntities();
    buildMetaFields();
  }

  public void init() {
    // Entity meta property builders
    entityPropertyBuilders.add(new DefaultEntityComponentBuilder(environment));
    entityPropertyBuilders.add(new FilterTransientFieldComponentBuilder(environment));
    entityPropertyBuilders.add(new MakeAccessibleComponentBuilder(environment));
    entityPropertyBuilders.add(new PrimaryKeyComponentBuilder(environment));

    // Field meta property builders
    fieldPropertyBuilders.add(new FilterPrimaryKeyComponentBuilder(environment));
    fieldPropertyBuilders.add(new FilterTransientFieldComponentBuilder(environment));
    fieldPropertyBuilders.add(new MakeAccessibleComponentBuilder(environment));
    fieldPropertyBuilders.add(new TtlFieldComponentBuilder(environment));
    fieldPropertyBuilders.add(new CompositeParentComponentBuilder(environment));
    fieldPropertyBuilders.add(new CollectionFieldComponentBuilder(environment));
    fieldPropertyBuilders.add(new MapFieldComponentBuilder(environment));
    fieldPropertyBuilders.add(new SimpleFieldComponentBuilder(environment));
  }

  public void scanPaths(String[] paths) {
    for (String path : paths) {
      scanPath(path);
    }
  }

  public void scanPath(String path) {
    LOG.trace("Scanning classpath path={}", path);

    // Resolve all candidate entity classes using the configured entity strategies
    Set<Class<?>> entityTypes = ReflectionUtils.findClassesWithAnnotation(path, AtreusEntity.class);

    // Iterate and process each found entity class
    for (Class<?> entityType : entityTypes) {
      addEntityType(entityType);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private MetaEntityImpl createMetaEntity(Class<?> entityType) {
    String className = entityType.getSimpleName();
    MetaEntityImpl managedEntity = new MetaEntityImpl();
    managedEntity.setEntityType(entityType);
    managedEntity.setName(className);
    ((MetaTableImpl) managedEntity.getTable()).setName(className);
    ((MetaTableImpl) managedEntity.getTable()).setKeySpace(environment.getConfiguration().getKeySpace());
    return managedEntity;
  }

  private void buildMetaEntities() {
    for (Class<?> entityType : entityTypes) {
      buildMetaEntity(entityType);
    }
  }

  private void buildMetaEntity(Class<?> entityType) {
    LOG.info("Processing Entity entityType={}", entityType.getCanonicalName());

    // Create the managed entity with defaults
    MetaEntityImpl metaEntity = createMetaEntity(entityType);

    // Execute the meta property builder rule bindValue on entity
    for (BaseEntityMetaComponentBuilder propertyBuilder : entityPropertyBuilders) {
      if (!propertyBuilder.acceptsEntity(metaEntity, entityType)) {
        continue;
      }
      if (propertyBuilder.handleEntity(metaEntity, entityType)) {
        break;
      }
    }

    // Execute the meta property rule bindValue on fields (first pass)
    for (Field field : entityType.getDeclaredFields()) {
      for (BaseEntityMetaComponentBuilder propertyBuilder : entityPropertyBuilders) {
        if (!propertyBuilder.acceptsField(metaEntity, field)) {
          continue;
        }
        propertyBuilder.validateField(metaEntity, field);
        if (propertyBuilder.handleField(metaEntity, field)) {
          break;
        }
      }
    }

    // Build Proxy Class
    environment.getProxyManager().defineEntityProxy(entityType);

    LOG.debug("Registered Entity name={} {}", metaEntity.getName(), metaEntity.getEntityType());
    environment.getMetaManager().addMetaEntity(metaEntity);
  }

  public void buildMetaFields() {

    // Iterate and process fields for each manged entity
    for (AtreusMetaEntity metaEntity : environment.getMetaManager().getEntities()) {
      Class<?> entityType = metaEntity.getEntityType();

      // Build the fields
      for (Field field : entityType.getDeclaredFields()) {

        // Execute the meta property builder rule bindValue
        for (BaseEntityMetaComponentBuilder propertyBuilder : fieldPropertyBuilders) {
          if (!propertyBuilder.acceptsField((MetaEntityImpl) metaEntity, field)) {
            continue;
          }
          propertyBuilder.validateField((MetaEntityImpl) metaEntity, field);
          if (propertyBuilder.handleField((MetaEntityImpl) metaEntity, field)) {
            break;
          }
        }

      }
    }
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class