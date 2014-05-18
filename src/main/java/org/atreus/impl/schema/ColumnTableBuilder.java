/**
 * The MIT License
 *
 * Copyright (c) 2014 Martin Crawford and contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.atreus.impl.schema;

import org.atreus.core.ext.AtreusCQLDataType;
import org.atreus.core.ext.meta.*;
import org.atreus.core.ext.strategies.AtreusCollectionTypeStrategy;
import org.atreus.core.ext.strategies.AtreusMapTypeStrategy;
import org.atreus.core.ext.strategies.AtreusTypeStrategy;
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
    for (AtreusMetaEntity metaEntity : metaManager.getEntities()) {
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
      Column column = buildColumn(metaField);
      if (columnTable.getPartitionKeys().isEmpty()) {
        columnTable.getPartitionKeys().add(column);
      }
      else {
        columnTable.getClusterKeys().add(column);
      }
      columnTable.getColumns().add(column);
    }

    for (AtreusMetaSimpleField metaField : expandMetaFields(metaEntity.getFields())) {
      Column column = buildColumn(metaField);
      columnTable.getColumns().add(column);
    }
  }

  private Column buildColumn(AtreusMetaSimpleField metaSimpleField) {
    Column column = new Column();
    column.setName(metaSimpleField.getColumn());
    AtreusTypeStrategy typeStrategy = metaSimpleField.getTypeStrategy();
    column.setDataType(typeStrategy.getDataType());
    if (typeStrategy instanceof AtreusCollectionTypeStrategy) {
      AtreusCollectionTypeStrategy collectionTypeStrategy = (AtreusCollectionTypeStrategy) typeStrategy;
      column.setDataTypeParams(new AtreusCQLDataType[]{collectionTypeStrategy.getValueDataType()});
    }
    if (typeStrategy instanceof AtreusMapTypeStrategy) {
      AtreusMapTypeStrategy mapTypeStrategy = (AtreusMapTypeStrategy) typeStrategy;
      column.setDataTypeParams(new AtreusCQLDataType[]{mapTypeStrategy.getKeyDataType(), mapTypeStrategy.getValueDataType()});
    }
    return column;
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