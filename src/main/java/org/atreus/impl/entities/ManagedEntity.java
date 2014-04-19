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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;

/**
 * Managed Entity bean.
 *
 * @author Martin Crawford
 */
public class ManagedEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ManagedEntity.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Class<?> typeClass;

  private String keySpaceName;

  private String tableName;

  private ManagedField[] primaryKey;

  private ManagedField[] fields;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public Class<?> getTypeClass() {
    return typeClass;
  }

  public void setTypeClass(Class<?> typeClass) {
    this.typeClass = typeClass;
  }

  public String getKeySpaceName() {
    return keySpaceName;
  }

  public void setKeySpaceName(String keySpaceName) {
    this.keySpaceName = keySpaceName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public ManagedField[] getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(ManagedField[] primaryKey) {
    this.primaryKey = primaryKey;
  }

  public ManagedField[] getFields() {
    return fields;
  }

  public void setFields(ManagedField[] fields) {
    this.fields = fields;
  }

}