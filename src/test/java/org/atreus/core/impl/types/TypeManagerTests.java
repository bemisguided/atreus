package org.atreus.core.impl.types;

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
public class TypeManagerTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(TypeManagerTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testDefaultTypes() {
    TypeManager typeManager = new TypeManager();

    Assert.notNull(typeManager.findType(String.class), "StringTypeAccessor expected");
    Assert.isInstanceOf(StringTypeAccessor.class, typeManager.findType(String.class));

    Assert.notNull(typeManager.findType(Integer.class), "IntegerTypeAccessor expected");
    Assert.isInstanceOf(IntegerTypeAccessor.class, typeManager.findType(Integer.class));

    Assert.notNull(typeManager.findType(Long.class), "LongTypeAccessor expected");
    Assert.isInstanceOf(LongTypeAccessor.class, typeManager.findType(Long.class));

    Assert.notNull(typeManager.findType(Short.class), "ShortTypeAccessor expected");
    Assert.isInstanceOf(ShortTypeAccessor.class, typeManager.findType(Short.class));

  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class