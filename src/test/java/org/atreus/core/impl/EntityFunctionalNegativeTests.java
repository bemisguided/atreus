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
package org.atreus.core.impl;

import org.atreus.core.AtreusInitialisationException;
import org.atreus.core.BaseAtreusCassandraTests;
import org.atreus.core.tests.entities.errors.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Negative functional tests for various bad entity mappings.
 *
 * @author Martin Crawford
 */
public class EntityFunctionalNegativeTests extends BaseAtreusCassandraTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(EntityFunctionalNegativeTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test(expected = AtreusInitialisationException.class)
  public void testCollectionValueNotResolvable() {
    LOG.info("Running testCollectionValueNotResolvable");
    try {
      addEntity(CollectionValueNotResolvableTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_COLLECTION_VALUE_TYPE_NOT_RESOLVABLE, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMapValueNotResolvable() {
    LOG.info("Running testMapValueNotResolvable");
    try {
      addEntity(MapValueNotResolvableTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_COLLECTION_VALUE_TYPE_NOT_RESOLVABLE, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMapKeyNotResolvable() {
    LOG.info("Running testMapKeyNotResolvable");
    try {
      addEntity(MapKeyNotResolvableTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_MAP_KEY_TYPE_NOT_RESOLVABLE, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testInvalidCollectionStrategy() {
    LOG.info("Running testInvalidCollectionStrategy");
    try {
      addEntity(InvalidCollectionStrategyTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_COLLECTION_TYPE_STRATEGY_INVALID, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testInvalidMapStrategy() {
    LOG.info("Running testInvalidMapStrategy");
    try {
      addEntity(InvalidMapStrategyTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_MAP_TYPE_STRATEGY_INVALID, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMultiplePrimaryKeys() {
    LOG.info("Running testMultiplePrimaryKeys");
    try {
      addEntity(MultiplePrimaryKeyTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_MULTIPLE, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testPrimaryKeysNotSerializable() {
    LOG.info("Running testPrimaryKeysNotSerializable");
    try {
      addEntity(PrimaryKeyNotSerializableTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_NOT_SERIALIZABLE, e.getErrorCode());
      throw e;
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class