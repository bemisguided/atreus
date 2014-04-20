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

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.atreus.core.impl.BaseAtreusTests;
import org.atreus.impl.AtreusEnvironment;
import org.atreus.impl.AtreusSessionFactoryImpl;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for Cassandra required unit tests.
 *
 * @author Martin Crawford
 */
public abstract class BaseCassandraTests extends BaseAtreusTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseCassandraTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private AtreusSession session;

  private static Cluster cluster;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @BeforeClass
  public static void beforeClass() throws Exception {
    EmbeddedCassandraServerHelper.startEmbeddedCassandra();
    cluster = Cluster.builder().addContactPoint(CLUSTER_HOST_NAME).withPort(CLUSTER_PORT).build();
    executeCQL("CREATE KEYSPACE " + DEFAULT_KEY_SPACE + " WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
  }

  @Before
  public void before() throws Exception {
    AtreusConfiguration configuration = new AtreusConfiguration(CLUSTER_HOST_NAME, CLUSTER_PORT, DEFAULT_KEY_SPACE);
    setEnvironment(new AtreusEnvironment(configuration));
    AtreusSessionFactoryImpl sessionFactory = new AtreusSessionFactoryImpl(getEnvironment());
    sessionFactory.connect();
    session = sessionFactory.openSession();
  }

  @After
  public void after() throws Exception {
    session.close();
    session = null;
  }

  @AfterClass
  public static void afterClass() throws Exception {
    cluster.close();
    EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected static void executeCQL(String cql) {
    Session cassandraSession = cluster.newSession();
    cassandraSession.execute(cql);
    cassandraSession.close();
  }

  protected AtreusSession getSession() {
    return session;
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class