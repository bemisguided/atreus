package org.atreus.impl.schema;

import org.atreus.impl.schema.model.Column;
import org.atreus.impl.schema.model.ColumnTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Generates a set of CQL CREATE TABLE scripts.
 *
 * @author Martin Crawford
 */
public class CreateQueryWriter {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(CreateQueryWriter.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public Set<String> generate(Collection<ColumnTable> columnTables) {
    Set<String> results = new HashSet<>();
    for(ColumnTable columnTable : columnTables) {
      results.add(generateQuery(columnTable));
    }
    return results;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private String generateQuery(ColumnTable columnTable) {
    StringBuilder query = new StringBuilder("CREATE TABLE ");
    query.append(columnTable.getKeySpace());
    query.append('.');
    query.append(columnTable.getName());
    query.append(" (");
    for (Column column : columnTable.getColumns()) {
      query.append(column.getName());
      query.append(' ');
      query.append(column.getDataType().getText());
      // TODO param types
      query.append(", ");
    }
    query.append("PRIMARY KEY (");
    boolean hasPartitionKey = false;
    for (Column column : columnTable.getPartionKeys()) {
      if (hasPartitionKey) {
        query.append(", ");
      }
      query.append(column.getName());
      hasPartitionKey = true;
    }
    for (Column column : columnTable.getClusterKeys()) {
      query.append(", ");
      query.append(column.getName());
    }
    query.append("));");
    return query.toString();
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class