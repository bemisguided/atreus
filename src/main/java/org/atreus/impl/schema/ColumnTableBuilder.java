package org.atreus.impl.schema;

import org.atreus.core.ext.meta.*;
import org.atreus.impl.schema.model.Column;
import org.atreus.impl.schema.model.ColumnTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the Column Table model from an Atreus Meta Manager.
 *
 * @author Martin Crawford
 */
public class ColumnTableBuilder {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ColumnTableBuilder.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public Map<String, ColumnTable> build(AtreusMetaManager metaManager) {
    Map<String, ColumnTable> results = new HashMap<>();
    for(AtreusMetaEntity metaEntity : metaManager.getEntities()) {
      buildColumnTables(metaEntity, results);
    }
    return results;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void buildColumnTables(AtreusMetaEntity metaEntity, Map<String, ColumnTable> results) {
    ColumnTable columnTable = new ColumnTable();
    columnTable.setKeySpace(metaEntity.getTable().getKeySpace());
    columnTable.setName(metaEntity.getTable().getName());
    results.put(columnTable.getName(), columnTable);
    for (AtreusMetaSimpleField metaField : expandMetaFields(metaEntity.getPrimaryKeyField())) {
      Column column = new Column();
      column.setName(metaField.getColumn());
      column.setDataType(metaField.getTypeStrategy().getDataType());
      // TODO param types Set, List & Map
      // TODO better control of the partition key vs the cluster key
      if ( columnTable.getPartionKeys().isEmpty()) {
        columnTable.getPartionKeys().add(column);
      } else {
        columnTable.getClusterKeys().add(column);
      }
      columnTable.getColumns().add(column);
    }

    for(AtreusMetaSimpleField metaField : expandMetaFields(metaEntity.getFields())) {
      Column column = new Column();
      column.setName(metaField.getColumn());
      column.setDataType(metaField.getTypeStrategy().getDataType());
      // TODO param types Set, List & Map
      columnTable.getColumns().add(column);
    }
  }

  private List<AtreusMetaSimpleField> expandMetaFields(AtreusMetaField... metaFields) {
    List<AtreusMetaSimpleField> results = new ArrayList<>();
    for (AtreusMetaField metaField : metaFields) {
      if (metaField instanceof AtreusMetaComplexField) {
        for (AtreusMetaSimpleField metaSimpleField : ((AtreusMetaComplexField) metaField).getFields()) {
          results.add(metaSimpleField);
        }
      }
      if (metaField instanceof AtreusMetaSimpleField) {
        results.add((AtreusMetaSimpleField) metaField);
      }
    }
    return results;
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class