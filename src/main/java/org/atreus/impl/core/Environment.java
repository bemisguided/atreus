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
package org.atreus.impl.core;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.DriverException;
import org.atreus.core.AtreusClusterConnectivityException;
import org.atreus.core.AtreusConfiguration;
import org.atreus.core.ext.plugins.AtreusPlugin;
import org.atreus.impl.core.mappings.MappingBuilder;
import org.atreus.impl.core.proxies.collections.CollectionProxyManager;
import org.atreus.impl.core.proxies.entities.EntityProxyManager;
import org.atreus.impl.core.queries.QueryManager;
import org.atreus.impl.types.TypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Atreus Environment.
 *
 * @author Martin Crawford
 */
public class Environment {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(Environment.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Cluster cassandraCluster;
  private Session cassandraSession;
  private ManagerImpl manager;
  private final AtreusConfiguration configuration;
  private final MetaManagerImpl metaManager;
  private final MappingBuilder mappingBuilder;
  private final QueryManager queryManager;
  private final EntityProxyManager entityProxyManager;
  private final CollectionProxyManager collectionProxyManager;
  private final TypeManager typeManager;
  private final List<AtreusPlugin> plugins = new ArrayList<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public Environment(AtreusConfiguration configuration) {
    this.configuration = configuration;
    this.metaManager = new MetaManagerImpl(this);
    this.queryManager = new QueryManager(this);
    this.typeManager = new TypeManager(this);
    this.mappingBuilder = new MappingBuilder(this);
    this.entityProxyManager = new EntityProxyManager();
    this.collectionProxyManager = new CollectionProxyManager();
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void addPlugin(AtreusPlugin plugin) {
    plugins.add(plugin);
  }

  public void connect() {
    try {
      Cluster cluster = Cluster.builder()
          .addContactPoints(configuration.getHosts())
          .withPort(configuration.getPort())
          .build();
      cluster.connect();
      setCassandraCluster(cluster);
      setCassandraSession(cluster.newSession());
    }
    catch (DriverException e) {
      throw new AtreusClusterConnectivityException(AtreusClusterConnectivityException.ERROR_CODE_CANNOT_CONNECT, e);
    }
  }

  public void init() {
    typeManagerInit();
    pluginsInit();
    pluginsPreMapping();
    metaBuilderInit();
    pluginsPostMapping();
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private void metaBuilderInit() {
    mappingBuilder.scanPaths(configuration.getScanPaths());
    mappingBuilder.build();
  }

  private void pluginsInit() {
    for (AtreusPlugin plugin : plugins) {
      plugin.init();
    }
  }

  private void pluginsPreMapping() {
    for (AtreusPlugin plugin : plugins) {
      plugin.preMapping(manager);
    }
  }

  private void pluginsPostMapping() {
    for (AtreusPlugin plugin : plugins) {
      plugin.postMapping(manager);
    }
  }

  private void typeManagerInit() {
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

  public MetaManagerImpl getMetaManager() {
    return metaManager;
  }

  public MappingBuilder getMappingBuilder() {
    return mappingBuilder;
  }

  public QueryManager getQueryManager() {
    return queryManager;
  }

  public EntityProxyManager getEntityProxyManager() {
    return entityProxyManager;
  }

  public CollectionProxyManager getCollectionProxyManager() {
    return collectionProxyManager;
  }

  public TypeManager getTypeManager() {
    return typeManager;
  }

  public ManagerImpl getManager() {
    return manager;
  }

  public void setManager(ManagerImpl manager) {
    this.manager = manager;
  }

}