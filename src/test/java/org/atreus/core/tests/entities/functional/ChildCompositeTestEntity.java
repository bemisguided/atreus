package org.atreus.core.tests.entities.functional;

import org.atreus.core.annotations.AtreusEntity;
import org.atreus.core.annotations.AtreusPrimaryKey;

/**
 * ChildCompositeTestEntity.
 *
 * @author Martin Crawford
 */
@AtreusEntity
public class ChildCompositeTestEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  @AtreusPrimaryKey
  private String id;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

} // end of class