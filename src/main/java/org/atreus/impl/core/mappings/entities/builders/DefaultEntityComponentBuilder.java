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
package org.atreus.impl.core.mappings.entities.builders;

import org.atreus.core.annotations.AtreusEntity;
import org.atreus.core.ext.listeners.AtreusEntityListener;
import org.atreus.impl.core.Environment;
import org.atreus.impl.core.mappings.BaseEntityMetaComponentBuilder;
import org.atreus.impl.core.mappings.entities.listeners.EntityDeleteListener;
import org.atreus.impl.core.mappings.entities.listeners.EntityUpdateListener;
import org.atreus.impl.core.mappings.entities.listeners.PrimaryKeyGeneratorListener;
import org.atreus.impl.core.mappings.entities.meta.MetaEntityImpl;
import org.atreus.impl.core.mappings.entities.meta.MetaTableImpl;
import org.atreus.impl.util.StringUtils;

/**
 * Default meta entity property builder.
 *
 * @author Martin Crawford
 */
public class DefaultEntityComponentBuilder extends BaseEntityMetaComponentBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final AtreusEntityListener PRIMARY_KEY_GENERATOR_LISTENER = new PrimaryKeyGeneratorListener();
  private static final AtreusEntityListener ENTITY_UPDATE_LISTENER = new EntityUpdateListener();
  private static final AtreusEntityListener ENTITY_DELETE_LISTENER = new EntityDeleteListener();

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public DefaultEntityComponentBuilder(Environment environment) {
    super(environment);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean handleEntity(MetaEntityImpl metaEntity, Class<?> entityType) {

    AtreusEntity entityAnnotation = entityType.getAnnotation(AtreusEntity.class);
    if (entityAnnotation == null) {
      return false;
    }

    String name = entityAnnotation.value();
    String keySpace = entityAnnotation.keySpace();
    String table = entityAnnotation.table();

    if (StringUtils.isNotNullOrEmpty(name)) {
      metaEntity.setName(name);
    }
    if (StringUtils.isNotNullOrEmpty(keySpace)) {
      ((MetaTableImpl) metaEntity.getTable()).setKeySpace(keySpace);
    }
    if (StringUtils.isNotNullOrEmpty(table)) {
      ((MetaTableImpl) metaEntity.getTable()).setName(table);
    }

    metaEntity.addListener(PRIMARY_KEY_GENERATOR_LISTENER);
    metaEntity.addListener(ENTITY_UPDATE_LISTENER);
    metaEntity.addListener(ENTITY_DELETE_LISTENER);
    return false;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class