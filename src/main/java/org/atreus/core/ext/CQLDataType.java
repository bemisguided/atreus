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
package org.atreus.core.ext;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * CQL Data Types.
 *
 * @author Martin Crawford
 */
public enum CQLDataType {

  CQL_ASCII("ascii", String.class),
  CQL_BIGINT("bigint", Long.class),
  CQL_BLOB("blob", ByteBuffer.class),
  CQL_BOOLEAN("boolean", Boolean.class),
  CQL_COUNTER("counter", Integer.class),
  CQL_DECIMAL("decimal", BigDecimal.class),
  CQL_DOUBLE("double", Double.class),
  CQL_FLOAT("float", Float.class),
  CQL_INET("inet", InetAddress.class),
  INT("int", Integer.class),
  CQL_LIST("list", List.class),
  CQL_MAP("map", Map.class),
  CQL_SET("set", Set.class),
  CQL_TEXT("text", String.class),
  CQL_TIMESTAMP("timestamp", Date.class),
  CQL_UUID("uuid", UUID.class),
  CQL_TIMEUUID("timeuuid", UUID.class),
  CQL_VARCHAR("varchar", String.class),
  CQL_VARINT("varint", BigInteger.class);

  private final String text;
  private final Class<?> defaultType;

  CQLDataType(String text, Class<?> defaultType) {
    this.text = text;
    this.defaultType = defaultType;
  }

  public String getText() {
    return text;
  }

  public Class<?> getDefaultType() {
    return defaultType;
  }

}
