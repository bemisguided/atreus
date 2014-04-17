package org.atreus.impl.types.atreus;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.types.AtreusTypeAccessor;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Base class for Type Accessors mapped by a byte array.
 *
 * @author Martin Crawford
 */
public abstract class BaseByteBufferTypeAccessor<T extends Serializable> implements AtreusTypeAccessor<T> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public T get(Row row, String colName) {
    ByteBuffer byteBuffer = row.getBytes(colName);
    return toValue(byteBuffer.array());
  }

  @Override
  public void set(BoundStatement boundStatement, String colName, T value) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(fromValue(value));
    boundStatement.setBytes(colName, byteBuffer);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected abstract T toValue(byte[] bytes);

  protected abstract byte[] fromValue(T value);

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class