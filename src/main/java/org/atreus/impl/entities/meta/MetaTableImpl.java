package org.atreus.impl.entities.meta;

import org.atreus.core.ext.meta.AtreusMetaTable;

/**
 * Meta Table bean.
 *
 * @author Martin Crawford
 */
public class MetaTableImpl implements AtreusMetaTable {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private String keySpace;
  private String name;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public String getKeySpace() {
    return keySpace;
  }

  public void setKeySpace(String keySpace) {
    this.keySpace = keySpace;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

} // end of class