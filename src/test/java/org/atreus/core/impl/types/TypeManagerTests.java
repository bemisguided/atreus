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
package org.atreus.core.impl.types;

import org.atreus.core.BaseAtreusTests;
import org.atreus.impl.types.TypeManager;
import org.atreus.impl.types.atreus.ShortTypeAccessor;
import org.atreus.impl.types.cql.IntegerTypeAccessor;
import org.atreus.impl.types.cql.LongTypeAccessor;
import org.atreus.impl.types.cql.StringTypeAccessor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Unit tests for the Type Manager.
 *
 * @author Martin Crawford
 */
public class TypeManagerTests extends BaseAtreusTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(TypeManagerTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testDefaultTypes() {
    TypeManager typeManager = getEnvironment().getTypeManager();

    Assert.notNull(typeManager.findTypeAccessor(String.class), "StringTypeAccessor expected");
    Assert.isInstanceOf(StringTypeAccessor.class, typeManager.findTypeAccessor(String.class));

    Assert.notNull(typeManager.findTypeAccessor(Integer.class), "IntegerTypeAccessor expected");
    Assert.isInstanceOf(IntegerTypeAccessor.class, typeManager.findTypeAccessor(Integer.class));

    Assert.notNull(typeManager.findTypeAccessor(Long.class), "LongTypeAccessor expected");
    Assert.isInstanceOf(LongTypeAccessor.class, typeManager.findTypeAccessor(Long.class));

    Assert.notNull(typeManager.findTypeAccessor(Short.class), "ShortTypeAccessor expected");
    Assert.isInstanceOf(ShortTypeAccessor.class, typeManager.findTypeAccessor(Short.class));

  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class