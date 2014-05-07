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
package org.atreus.impl.types.atreus;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.CQLDataType;
import org.atreus.core.ext.strategies.AtreusType;
import org.atreus.impl.types.cql.BaseSimpleTypeStrategy;

/**
 * Integer Type Strategy.
 *
 * @author Martin Crawford
 */
@AtreusType(Short.class)
public class ShortTypeStrategy extends BaseSimpleTypeStrategy<Short> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public CQLDataType getDataType() {
    return CQLDataType.CQL_INT;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  @Override
  protected Short doGet(Row row, String colName) {
    Integer integer = row.getInt(colName);
    if (integer > Short.MAX_VALUE || integer < Short.MIN_VALUE) {
      throw new IllegalArgumentException(integer + " is out of range for short");
    }
    return integer.shortValue();
  }

  @Override
  protected void doSet(BoundStatement boundStatement, String colName, Short value) {
    boundStatement.setInt(colName, value);
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}