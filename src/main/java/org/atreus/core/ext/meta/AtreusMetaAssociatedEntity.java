package org.atreus.core.ext.meta;

/**
 * Interface for a managed association entity.
 *
 * @author Martin Crawford
 */
public interface AtreusMetaAssociatedEntity {

  public AtreusMetaEntity getMetaEntity();

  public AtreusMetaField getAssociationField();

  public AtreusMetaField getAssociationKeyField();

}