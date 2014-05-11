package org.atreus.core.tests.entities.functional;

import org.atreus.core.annotations.AtreusComposite;
import org.atreus.core.annotations.AtreusEntity;
import org.atreus.core.annotations.AtreusPrimaryKey;

/**
 * ParentCompositeTestEntity.
 *
 * @author Martin Crawford
 */
@AtreusEntity
public class ParentCompositeTestEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  @AtreusPrimaryKey
  private String id;

  @AtreusComposite(parent = true)
  private ChildCompositeTestEntity childEntity;

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

  public ChildCompositeTestEntity getChildEntity() {
    return childEntity;
  }

  public void setChildEntity(ChildCompositeTestEntity childEntity) {
    this.childEntity = childEntity;
  }

} // end of class