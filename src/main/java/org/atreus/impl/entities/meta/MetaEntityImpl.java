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
package org.atreus.impl.entities.meta;

import org.atreus.core.ext.AtreusManagedEntityVisitor;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.ext.strategies.AtreusPrimaryKeyStrategy;
import org.atreus.core.ext.strategies.AtreusTtlStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

;

/**
 * Managed Entity bean.
 *
 * @author Martin Crawford
 */
public class MetaEntityImpl implements AtreusMetaEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(MetaEntityImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Class<?> entityType;

  private Map<String, AtreusMetaField> fieldsByName = new TreeMap<>();

  private Map<String, AtreusMetaField> fieldsByColumnName = new TreeMap<>();

  private String keySpace;

  private String name;

  private AtreusMetaField primaryKeyField;

  private AtreusPrimaryKeyStrategy primaryKeyGenerator;

  private String table;

  private AtreusMetaField ttlField;

  private AtreusTtlStrategy ttlStrategy;

  private List<AtreusManagedEntityVisitor> saveVisitors = new ArrayList<>();

  private List<AtreusManagedEntityVisitor> updateVisitors = new ArrayList<>();

  private List<AtreusManagedEntityVisitor> deleteVisitors = new ArrayList<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void addField(AtreusMetaField managedField) {
    fieldsByName.put(managedField.getName(), managedField);
    fieldsByColumnName.put(managedField.getColumn(), managedField);
  }

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
  public AtreusMetaField getFieldByName(String name) {
    return fieldsByName.get(name);
  }

  @Override
  public AtreusMetaField getFieldByColumnName(String columnName) {
    return fieldsByColumnName.get(columnName);
  }

  @Override
  public AtreusMetaField[] getFields() {
    AtreusMetaField[] result = new AtreusMetaField[fieldsByName.size()];
    return fieldsByName.values().toArray(result);
  }

  @Override
  public Object getFieldValue(String name, Object entity) {
    AtreusMetaField metaField = getFieldByName(name);
    return metaField.getValue(entity);
  }

  @Override
  public void setFieldValue(String name, Object entity, Object value) {
    AtreusMetaField metaField = getFieldByName(name);
    metaField.setValue(entity, value);
  }

  public AtreusMetaField getPrimaryKeyField() {
    return primaryKeyField;
  }

  public void setPrimaryKeyField(AtreusMetaField primaryKeyField) {
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
  public AtreusMetaField getTtlField() {
    return ttlField;
  }

  public void setTtlField(AtreusMetaField ttlField) {
    this.ttlField = ttlField;
  }

  @Override
  public AtreusTtlStrategy getTtlStrategy() {
    return ttlStrategy;
  }

  public void setTtlStrategy(AtreusTtlStrategy ttlStrategy) {
    this.ttlStrategy = ttlStrategy;
  }

  public List<AtreusManagedEntityVisitor> getSaveVisitors() {
    return saveVisitors;
  }

  public List<AtreusManagedEntityVisitor> getUpdateVisitors() {
    return updateVisitors;
  }

  public List<AtreusManagedEntityVisitor> getDeleteVisitors() {
    return deleteVisitors;
  }
}