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
package org.atreus.impl;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.DriverException;
import org.atreus.core.AtreusClusterConnectivityException;
import org.atreus.core.AtreusConfiguration;
import org.atreus.core.AtreusInitialisationException;
import org.atreus.impl.entities.EntityManager;
import org.atreus.impl.queries.QueryManager;
import org.atreus.impl.types.TypeManager;
import org.atreus.impl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Atreus Environment setup.
 *
 * @author Martin Crawford
 */
public class AtreusEnvironment {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusEnvironment.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Cluster cassandraCluster;
  private Session cassandraSession;
  private final AtreusConfiguration configuration;
  private final EntityManager entityManager;
  private final QueryManager queryManager;
  private final TypeManager typeManager;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public AtreusEnvironment(AtreusConfiguration configuration) {
    this.configuration = configuration;
    this.entityManager = new EntityManager(this);
    this.queryManager = new QueryManager(this);
    this.typeManager = new TypeManager(this);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void connect() {
    try {
      Cluster cluster = Cluster.builder()
          .addContactPoints(configuration.getHosts())
          .withPort(configuration.getPort())
          .build();
      cluster.connect();
      setCassandraCluster(cluster);
      setCassandraSession(cluster.newSession());
    }catch (DriverException e) {
      throw new AtreusClusterConnectivityException(AtreusClusterConnectivityException.ERROR_CODE_CANNOT_CONNECT, e);
    }
  }

  public void init() {
    initTypeManager();
    initEntityManager();
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void initEntityManager() {
    getEntityManager().scanPaths(getConfiguration().getScanPaths());
    getEntityManager().processEntities();
  }

  private void initTypeManager() {
    getTypeManager().scanPaths(getConfiguration().getScanPaths());
  }
  
  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public Cluster getCassandraCluster() {
    return cassandraCluster;
  }

  public void setCassandraCluster(Cluster cassandraCluster) {
    this.cassandraCluster = cassandraCluster;
  }

  public Session getCassandraSession() {
    return cassandraSession;
  }

  public void setCassandraSession(Session cassandraSession) {
    this.cassandraSession = cassandraSession;
  }

  public AtreusConfiguration getConfiguration() {
    return configuration;
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public QueryManager getQueryManager() {
    return queryManager;
  }

  public TypeManager getTypeManager() {
    return typeManager;
  }

}