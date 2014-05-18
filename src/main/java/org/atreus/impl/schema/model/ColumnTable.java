package org.atreus.impl.schema.model;

import java.util.ArrayList;
import java.util.List;

/**
 * CQL Column Table model.
 *
 * @author Martin Crawford
 */
public class ColumnTable {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private String keySpace;
  private String name;
  private List<Column> columns = new ArrayList<>();
  private List<Column> partionKeys = new ArrayList<>();
  private List<Column> clusterKeys = new ArrayList<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public String getKeySpace() {
    return keySpace;
  }

  public void setKeySpace(String keySpace) {
    this.keySpace = keySpace;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Column> getColumns() {
    return columns;
  }

  public List<Column> getPartionKeys() {
    return partionKeys;
  }

  public List<Column> getClusterKeys() {
    return clusterKeys;
  }

} // end of class