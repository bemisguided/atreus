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
package org.atreus.impl.entities;

import org.atreus.core.ext.AtreusPrimaryKeyStrategy;
import org.atreus.core.ext.AtreusTtlStrategy;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusManagedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

;

/**
 * Managed Entity bean.
 *
 * @author Martin Crawford
 */
public class ManagedEntityImpl implements AtreusManagedEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ManagedEntityImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Class<?> entityType;

  private Set<AtreusManagedField> fields = new TreeSet<>();

  private String keySpace;

  private String name;

  private AtreusManagedField primaryKeyField;

  private AtreusPrimaryKeyStrategy primaryKeyGenerator;

  private String table;

  private AtreusManagedField ttlField;

  private AtreusTtlStrategy ttlStrategy;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Class<?> getEntityType() {
    return entityType;
  }

  public void setEntityType(Class<?> entityType) {
    this.entityType = entityType;
  }

  @Override
  public String getKeySpace() {
    return keySpace;
  }

  public void setKeySpace(String keySpace) {
    this.keySpace = keySpace;
  }

  @Override
  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  @Override
  public Set<AtreusManagedField> getFields() {
    return fields;
  }

  public AtreusManagedField getPrimaryKeyField() {
    return primaryKeyField;
  }

  public void setPrimaryKeyField(AtreusManagedField primaryKeyField) {
    this.primaryKeyField = primaryKeyField;
  }

  @Override
  public AtreusPrimaryKeyStrategy getPrimaryKeyStrategy() {
    return primaryKeyGenerator;
  }

  public void setPrimaryKeyGenerator(AtreusPrimaryKeyStrategy primaryKeyGenerator) {
    this.primaryKeyGenerator = primaryKeyGenerator;
  }

  @Override
  public AtreusManagedField getTtlField() {
    return ttlField;
  }

  public void setTtlField(AtreusManagedField ttlField) {
    this.ttlField = ttlField;
  }

  @Override
  public AtreusTtlStrategy getTtlStrategy() {
    return ttlStrategy;
  }

  public void setTtlStrategy(AtreusTtlStrategy ttlStrategy) {
    this.ttlStrategy = ttlStrategy;
  }

}