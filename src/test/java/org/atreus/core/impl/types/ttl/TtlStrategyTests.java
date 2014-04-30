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
package org.atreus.core.impl.types.ttl;

import junit.framework.Assert;
import org.atreus.impl.types.ttl.DateTtlStrategy;
import org.atreus.impl.types.ttl.LongTtlStrategy;
import org.atreus.impl.types.ttl.ShortTtlStrategy;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Unit tests for time-to-live strategy implementations.
 *
 * @author Martin Crawford
 */
public class TtlStrategyTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(TtlStrategyTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testLongTtlStrategy() {
    LongTtlStrategy ttlStrategy = new LongTtlStrategy();
    Assert.assertEquals(1234, (int) ttlStrategy.translate(new Date(), (long) 1234));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLongTtlStrategyWithOutOfRangeMax() {
    LongTtlStrategy ttlStrategy = new LongTtlStrategy();
    ttlStrategy.translate(new Date(), Long.MAX_VALUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLongTtlStrategyWithOutOfRangeMin() {
    LongTtlStrategy ttlStrategy = new LongTtlStrategy();
    ttlStrategy.translate(new Date(), Long.MIN_VALUE);
  }

  @Test
  public void testShortTtlStrategy() {
    ShortTtlStrategy ttlStrategy = new ShortTtlStrategy();
    Assert.assertEquals(1234, (int) ttlStrategy.translate(new Date(), (short) 1234));
  }

  @Test
  public void testDateTtlStrategy() {
    DateTtlStrategy ttlStrategy = new DateTtlStrategy();
    Calendar now = Calendar.getInstance();
    Calendar future = Calendar.getInstance();
    future.setTimeInMillis(now.getTimeInMillis());
    future.add(Calendar.HOUR, 1);
    Assert.assertEquals(60 * 60, (int) ttlStrategy.translate(now.getTime(), future.getTime()));
  }


  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class