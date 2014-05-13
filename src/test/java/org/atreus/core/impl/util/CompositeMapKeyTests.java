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
package org.atreus.core.impl.util;

import junit.framework.Assert;
import org.atreus.impl.util.CompositeMapKey;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for the Composite Key.
 *
 * @author Martin Crawford
 */
public class CompositeMapKeyTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(CompositeMapKeyTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testEquals() {
    String key1 = "1234";
    Integer key2 = 1234;
    Assert.assertEquals(new CompositeMapKey(key1, key2), new CompositeMapKey(key1, key2));
    Assert.assertEquals(new CompositeMapKey(key1, null), new CompositeMapKey(key1, null));
    Assert.assertNotSame(new CompositeMapKey(key1, key2), new CompositeMapKey(key1, key1));
    Assert.assertNotSame(new CompositeMapKey(key1, key2), new CompositeMapKey(key1));
    Assert.assertNotSame(new CompositeMapKey(key1, key2), new CompositeMapKey(key1, null));
  }

  @Test
  public void testHashCode() {
    String key1 = "1234";
    Integer key2 = 1234;
    Assert.assertEquals(new CompositeMapKey(key1, key2).hashCode(), new CompositeMapKey(key1, key2).hashCode());
    Assert.assertEquals(new CompositeMapKey(key1, null).hashCode(), new CompositeMapKey(key1, null).hashCode());
    Assert.assertNotSame(new CompositeMapKey(key1, key2).hashCode(), new CompositeMapKey(key1, key1).hashCode());
    Assert.assertNotSame(new CompositeMapKey(key1, key2).hashCode(), new CompositeMapKey(key1).hashCode());
    Assert.assertNotSame(new CompositeMapKey(key1, key2).hashCode(), new CompositeMapKey(key1, null).hashCode());

  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class