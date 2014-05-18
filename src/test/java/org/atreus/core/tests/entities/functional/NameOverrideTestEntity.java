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
package org.atreus.core.tests.entities.functional;

import org.atreus.core.annotations.AtreusEntity;
import org.atreus.core.annotations.AtreusField;
import org.atreus.core.annotations.AtreusPrimaryKey;
import org.atreus.core.annotations.AtreusPrimaryKeyGenerator;
import org.atreus.impl.types.generators.StringPrimaryKeyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NameOverrideTestEntity
 *
 * @author Martin Crawford
 */
@AtreusEntity(value = "EntityName", keySpace = "NonDefault", table = "TableName")
public class NameOverrideTestEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(NameOverrideTestEntity.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  @AtreusPrimaryKey("primaryKey")
  @AtreusPrimaryKeyGenerator(StringPrimaryKeyStrategy.class)
  private String id;

  @AtreusField("columnName")
  private String field1;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getField1() {
    return field1;
  }

  public void setField1(String field1) {
    this.field1 = field1;
  }

} // end of class