package org.atreus.impl.schema;

import org.atreus.core.AtreusManager;
import org.atreus.core.ext.plugins.AtreusPlugin;
import org.atreus.impl.schema.model.ColumnTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * CQL Schema Generator Plugin.
 *
 * @author Martin Crawford
 */
public class SchemaGeneratorPlugin implements AtreusPlugin {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(SchemaGeneratorPlugin.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void init() {

  }

  @Override
  public void preMapping(AtreusManager manager) {

  }

  @Override
  public void postMapping(AtreusManager manager) {
    ColumnTableBuilder builder = new ColumnTableBuilder();
    Map<String, ColumnTable> columnTableMap = builder.build(manager.getMetaManager());
    CreateQueryWriter queryWriter = new CreateQueryWriter();
    Set<String> createCQL = queryWriter.generate(columnTableMap.values());
    for (String cql : createCQL) {
      LOG.debug("CQL: {}", cql);
    }
  }

  @Override
  public void destroy() {

  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class