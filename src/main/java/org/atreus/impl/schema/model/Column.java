package org.atreus.impl.schema.model;

import org.atreus.core.ext.AtreusCQLDataType;

/**
 * CQL Column model.
 *
 * @author Martin Crawford
 */
public class Column {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private String name;
  private AtreusCQLDataType dataType;
  private AtreusCQLDataType[] dataTypeParams;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AtreusCQLDataType getDataType() {
    return dataType;
  }

  public void setDataType(AtreusCQLDataType dataType) {
    this.dataType = dataType;
  }

  public AtreusCQLDataType[] getDataTypeParams() {
    return dataTypeParams;
  }

  public void setDataTypeParams(AtreusCQLDataType[] dataTypeParams) {
    this.dataTypeParams = dataTypeParams;
  }

} // end of class