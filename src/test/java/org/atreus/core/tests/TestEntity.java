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
package org.atreus.core.tests;

import org.atreus.core.annotations.AtreusEntity;
import org.atreus.core.annotations.AtreusField;
import org.atreus.core.annotations.AtreusFieldType;
import org.atreus.core.annotations.AtreusPrimaryKey;
import org.atreus.impl.types.cql.LongTypeStrategy;

/**
 * Test Entity 1.
 *
 * @author Martin Crawford
 */
@AtreusEntity()
public class TestEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  @AtreusPrimaryKey
  private String primaryKey;

  @AtreusField
  private String field1;

  private int field2;

  private transient String field3;

  @AtreusField(value = "field4field")
  @AtreusFieldType(LongTypeStrategy.class)
  private Integer field4;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public String getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(String primaryKey) {
    this.primaryKey = primaryKey;
  }

  public String getField1() {
    return field1;
  }

  public void setField1(String field1) {
    this.field1 = field1;
  }

  public int getField2() {
    return field2;
  }

  public void setField2(int field2) {
    this.field2 = field2;
  }

  public String getField3() {
    return field3;
  }

  public void setField3(String field3) {
    this.field3 = field3;
  }

  public Integer getField4() {
    return field4;
  }

  public void setField4(Integer field4) {
    this.field4 = field4;
  }
}