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
package org.atreus.impl.types.cql;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusCQLDataType;
import org.atreus.core.ext.strategies.AtreusMapTypeStrategy;
import org.atreus.core.ext.strategies.AtreusType;
import org.atreus.impl.types.BaseCollectionTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * List Type Strategy.
 *
 * @author Martin Crawford
 */
@AtreusType(Map.class)
public class MapTypeStrategy extends BaseCollectionTypeStrategy<Map> implements AtreusMapTypeStrategy<Map> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(MapTypeStrategy.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private AtreusCQLDataType keyDataType;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public AtreusCQLDataType getKeyDataType() {
    return keyDataType;
  }

  @Override
  public void setKeyDataType(AtreusCQLDataType keyDataType) {
    this.keyDataType = keyDataType;
  }

  @Override
  public AtreusCQLDataType getDataType() {
    return AtreusCQLDataType.CQL_MAP;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  @Override
  protected Map doGet(Row row, String colName) {
    return row.getMap(colName, getKeyDataType().getDefaultClass(), getValueDataType().getDefaultClass());
  }

  @Override
  public void doSet(BoundStatement boundStatement, String colName, Map value) {
    boundStatement.setMap(colName, value);
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class