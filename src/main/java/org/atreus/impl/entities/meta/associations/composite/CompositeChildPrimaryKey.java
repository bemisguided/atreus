package org.atreus.impl.entities.meta.associations.composite;

import java.io.Serializable;

/**
 * Composite Child Primary Key object.
 *
 * @author Martin Crawford
 */
public class CompositeChildPrimaryKey implements Serializable {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final Serializable parentKey;
  private final Serializable childKey;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public CompositeChildPrimaryKey(Serializable parentKey, Serializable childKey) {
    this.parentKey = parentKey;
    this.childKey = childKey;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CompositeChildPrimaryKey that = (CompositeChildPrimaryKey) o;

    if (childKey != null ? !childKey.equals(that.childKey) : that.childKey != null) {
      return false;
    }
    if (parentKey != null ? !parentKey.equals(that.parentKey) : that.parentKey != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = parentKey != null ? parentKey.hashCode() : 0;
    result = 31 * result + (childKey != null ? childKey.hashCode() : 0);
    return result;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public Serializable getParentKey() {
    return parentKey;
  }

  public Serializable getChildKey() {
    return childKey;
  }

} // end of class