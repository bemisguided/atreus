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
import com.datastax.driver.core.utils.Bytes;
import org.atreus.core.ext.AtreusTypeAccessor;
import org.atreus.impl.util.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Base class for Type Accessors mapped by a byte array.
 *
 * @author Martin Crawford
 */
public abstract class BaseByteBufferTypeAccessor<T> implements AtreusTypeAccessor<T> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseByteBufferTypeAccessor.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public T get(Row row, String colName) {
    ByteBuffer byteBuffer = row.getBytes(colName);
    if (byteBuffer == null) {
      return null;
    }
    byte[] bytes = Bytes.getArray(byteBuffer);
    return toValue(bytes);
  }

  @Override
  public void set(BoundStatement boundStatement, String colName, T value) {
    if (value == null) {
      boundStatement.setBytes(colName, null);
      return;
    }
    byte[] bytes = fromValue(value);
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    boundStatement.setBytes(colName, byteBuffer);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected abstract T toValue(byte[] bytes);

  protected abstract byte[] fromValue(T value);

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class