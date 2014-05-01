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
import org.atreus.impl.types.atreus.ShortTypeStrategy;
import org.atreus.impl.types.cql.IntegerTypeStrategy;
import org.atreus.impl.types.cql.LongTypeStrategy;
import org.atreus.impl.types.cql.StringTypeStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    Assert.assertNotNull("StringTypeAccessor expected", typeManager.findTypeStrategy(String.class));
    Assert.assertEquals(StringTypeStrategy.class, typeManager.findTypeStrategy(String.class).getClass());

    Assert.assertNotNull("IntegerTypeAccessor expected", typeManager.findTypeStrategy(Integer.class));
    Assert.assertEquals(IntegerTypeStrategy.class, typeManager.findTypeStrategy(Integer.class).getClass());

    Assert.assertNotNull("LongTypeAccessor expected", typeManager.findTypeStrategy(Long.class));
    Assert.assertEquals(LongTypeStrategy.class, typeManager.findTypeStrategy(Long.class).getClass());

    Assert.assertNotNull("ShortTypeAccessor expected", typeManager.findTypeStrategy(Short.class));
    Assert.assertEquals(ShortTypeStrategy.class, typeManager.findTypeStrategy(Short.class).getClass());

  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class