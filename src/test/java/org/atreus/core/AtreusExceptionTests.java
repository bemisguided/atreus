package org.atreus.core;

import org.atreus.impl.types.generators.UuidPrimaryKeyStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for Atreus Exceptions.
 *
 * @author Martin Crawford
 */
public class AtreusExceptionTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusExceptionTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testMessage() {
    AtreusException e = new AtreusInitialisationException(AtreusInitialisationException.ERROR_CODE_REGISTER_PRIMARY_KEY_STRATEGY, UuidPrimaryKeyStrategy.class);
    Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_REGISTER_PRIMARY_KEY_STRATEGY, e.getErrorCode());
    Assert.assertEquals("Unable to register primary key strategy [" + UuidPrimaryKeyStrategy.class + "]", e.getMessage());
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class