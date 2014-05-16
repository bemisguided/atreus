package org.atreus.impl.entities.meta.associations;

import org.atreus.core.ext.meta.AtreusMetaAssociatedEntity;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo document me
 *
 * @author Martin Crawford
 */
public class MetaAssociatedEntityImpl implements AtreusMetaAssociatedEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(MetaAssociatedEntityImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private AtreusMetaEntity metaEntity;
  private AtreusMetaField associationField;
  private AtreusMetaField associationKeyField;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public AtreusMetaEntity getMetaEntity() {
    return metaEntity;
  }

  public void setMetaEntity(AtreusMetaEntity metaEntity) {
    this.metaEntity = metaEntity;
  }

  @Override
  public AtreusMetaField getAssociationField() {
    return associationField;
  }

  public void setAssociationField(AtreusMetaField associationField) {
    this.associationField = associationField;
  }

  @Override
  public AtreusMetaField getAssociationKeyField() {
    return associationKeyField;
  }

  public void setAssociationKeyField(AtreusMetaField associationKeyField) {
    this.associationKeyField = associationKeyField;
  }

} // end of class