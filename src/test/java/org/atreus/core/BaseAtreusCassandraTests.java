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
package org.atreus.core;

import org.atreus.impl.core.Environment;
import org.atreus.impl.core.ManagerImpl;
import org.atreus.impl.core.SessionImpl;
import org.atreus.impl.schema.SchemaGeneratorPlugin;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for Atreus with Cassandra required unit tests.
 *
 * @author Martin Crawford
 */
public class BaseAtreusCassandraTests extends BaseCassandraTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseAtreusCassandraTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private AtreusSession session;
  private SchemaGeneratorPlugin schemaGeneratorPlugin;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Before
  public void before() throws Exception {
    AtreusConfiguration configuration = new AtreusConfiguration();
    configuration.setHosts(CLUSTER_HOST_NAME);
    configuration.setPort(CLUSTER_PORT);
    configuration.setKeySpace(DEFAULT_KEY_SPACE);
    schemaGeneratorPlugin = new SchemaGeneratorPlugin();
    setEnvironment(new Environment(configuration));
    getEnvironment().setCassandraCluster(getCassandraCluster());
    getEnvironment().setCassandraSession(getCassandraCluster().newSession());
    getEnvironment().addPlugin(schemaGeneratorPlugin);
    getEnvironment().setManager(new ManagerImpl(getEnvironment()));
    session = new SessionImpl(getEnvironment());
  }

  @After
  public void after() throws Exception {
    session.close();
    schemaGeneratorPlugin.dropSchema(getEnvironment().getManager());
    session = null;
    setEnvironment(null);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected void addEntity(Class<?> entityType) {
    getEnvironment().getMappingBuilder().addEntityType(entityType);
  }

  protected AtreusSession getSession() {
    return session;
  }

  protected void setScanPaths(String... scanPaths) {
    getEnvironment().getConfiguration().setScanPaths(scanPaths);
  }

  protected void initEnvironment() {
    getEnvironment().init();
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class