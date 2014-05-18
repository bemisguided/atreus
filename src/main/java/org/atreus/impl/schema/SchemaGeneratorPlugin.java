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

import org.atreus.core.AtreusManager;
import org.atreus.core.AtreusSession;
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

  private Map<String, ColumnTable> columnTableMap;

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
    columnTableMap = builder.build(manager.getMetaManager());
    CreateQueryWriter queryWriter = new CreateQueryWriter();
    Set<String> createCQL = queryWriter.generate(columnTableMap.values());
    AtreusSession session = manager.openSession();
    for (String cql : createCQL) {
      LOG.debug("SCHEMA CQL: {}", cql);
      session.execute(cql);
    }
    session.flush();
    session.close();
  }

  @Override
  public void destroy() {
  }

  public void dropSchema(AtreusManager manager) {
    DropQueryWriter queryWriter = new DropQueryWriter();
    if (columnTableMap == null) {
      return;
    }
    Set<String> createCQL = queryWriter.generate(columnTableMap.values());
    AtreusSession session = manager.openSession();
    for (String cql : createCQL) {
      LOG.debug("SCHEMA CQL: {}", cql);
      session.execute(cql);
    }
    session.flush();
    session.close();
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class