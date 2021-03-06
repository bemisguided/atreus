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

import com.datastax.driver.core.*;
import org.atreus.core.AtreusConfiguration;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.listeners.AtreusOnDeleteListener;
import org.atreus.core.ext.listeners.AtreusOnFetchListener;
import org.atreus.core.ext.listeners.AtreusOnSaveListener;
import org.atreus.core.ext.listeners.AtreusOnUpdateListener;
import org.atreus.core.ext.meta.AtreusMetaAssociation;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.core.mappings.associations.handlers.AssociationFetchHandler;
import org.atreus.impl.core.mappings.entities.handlers.EntityFetchHandler;
import org.atreus.impl.core.queries.QueryManager;
import org.atreus.impl.util.AssertUtils;
import org.atreus.impl.util.CompositeMapKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements an Atreus Session.
 *
 * @author Martin Crawford
 */
public class SessionImpl implements AtreusSessionExt {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(SessionImpl.class);

  private static final AssociationFetchHandler ASSOCIATION_FETCH_HANDLER = new AssociationFetchHandler();

  private static final EntityFetchHandler ENTITY_FETCH_HANDLER = new EntityFetchHandler();

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private boolean closed;

  private final Environment environment;

  private Map<CompositeMapKey, AtreusManagedEntity> cachedEntities = new HashMap<>();

  private boolean sessionCache;

  private ConsistencyLevel readConsistencyLevel;

  private boolean writeAsync;

  private boolean writeBatch;

  private ConsistencyLevel writeConsistencyLevel;

  private BatchStatement currentBatchStatement;

  private int nestedBatchCount;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public SessionImpl(Environment environment) {
    this.environment = environment;
    writeAsync = getConfiguration().isDefaultWriteAsync();
    writeBatch = getConfiguration().isDefaultWriteBatch();
    readConsistencyLevel = getConfiguration().getDefaultReadConsistencyLevel();
    writeConsistencyLevel = getConfiguration().getDefaultWriteConsistencyLevel();
    sessionCache = getConfiguration().isSessionCache();
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods


  @Override
  public <T> void delete(T entity) {
    assertSessionNotClosed();

    // Assert input params
    AssertUtils.notNull(entity, "entity is a required parameter");

    // Retrieve managed entity
    AtreusManagedEntity managedEntity = manageEntity(entity);

    // Open a batch if necessary
    batchOpen();

    // Broadcast to the on delete listeners
    managedEntity.getMetaEntity().broadcastListeners(this, managedEntity, AtreusOnDeleteListener.class);

    // Finish the batch if necessary
    batchFinish();

    // Remove the managed entity from the cache
    uncacheEntry(managedEntity);
  }

  @Override
  public AtreusManagedEntity entityInstance(AtreusMetaEntity metaEntity, Serializable primaryKey) {
    assertSessionNotClosed();

    Object entity = environment.getEntityProxyManager().createEntity(this, metaEntity);
    metaEntity.getPrimaryKeyField().setValue(entity, primaryKey);
    return manageEntity(entity);
  }

  @Override
  public ResultSet execute(Statement statement) {
    assertSessionNotClosed();

    return executeRead(statement);
  }

  @Override
  public ResultSet execute(String cql) {
    assertSessionNotClosed();

    return execute(new SimpleStatement(cql));
  }

  @Override
  public void executeOrBatch(Statement statement) {
    assertSessionNotClosed();

    // Write batch enabled
    if (isWriteBatch()) {

      // No existing batch so open one
      if (!isBatchOpen()) {
        batchOpen();
      }
    }

    // Batch is currently open so add this statement to it
    if (isBatchOpen()) {
      currentBatchStatement.add(statement);
      return;
    }
    getCassandraSession().execute(statement);
  }

  @Override
  public void executeOrBatch(String cql) {
    assertSessionNotClosed();

    executeOrBatch(new SimpleStatement(cql));
  }

  @Override
  public void fetch(AtreusManagedEntity managedEntity) {
    assertSessionNotClosed();

    // Assert input params
    AssertUtils.notNull(managedEntity, "managedEntity is a required parameter");

    Serializable primaryKey = managedEntity.getPrimaryKey();
    AtreusMetaEntity metaEntity = managedEntity.getMetaEntity();

    managedEntity = fetchEntity(metaEntity, primaryKey, managedEntity);
    if (managedEntity == null) {
      throw new RuntimeException("Managed entity could not be fetched as it does not exist " + primaryKey + " " + metaEntity.getEntityType());
    }
    managedEntity.getMetaEntity().broadcastListeners(this, managedEntity, AtreusOnFetchListener.class);
  }

  @Override
  public void fetchAssociation(AtreusMetaAssociation metaAssociation, AtreusManagedEntity managedEntity) {
    assertSessionNotClosed();

    // Assert input params
    AssertUtils.notNull(metaAssociation, "metaAssociation is a required parameter");
    AssertUtils.notNull(managedEntity, "managedEntity is a required parameter");

    ASSOCIATION_FETCH_HANDLER.fetch(this, metaAssociation, managedEntity);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T findOne(Class<T> entityType, Serializable primaryKey) {
    assertSessionNotClosed();

    // Assert input params
    AssertUtils.notNull(entityType, "entityType is a required parameter");
    AssertUtils.notNull(primaryKey, "primaryKey is a required parameter");
    AtreusMetaEntity metaEntity = assertGetMetaEntity(entityType);

    AtreusManagedEntity managedEntity = fetchEntity(metaEntity, primaryKey, null);
    if (managedEntity == null) {
      return null;
    }
    managedEntity.getMetaEntity().broadcastListeners(this, managedEntity, AtreusOnFetchListener.class);
    return (T) managedEntity;
  }

  @Override
  public void flush() {
    assertSessionNotClosed();

    if (currentBatchStatement == null) {
      return;
    }
    executeWrite(currentBatchStatement, isWriteAsync());
    currentBatchStatement = null;
    nestedBatchCount = 0;
  }

  @Override
  public void flush(boolean async) {
    assertSessionNotClosed();

    if (currentBatchStatement == null) {
      return;
    }
    executeWrite(currentBatchStatement, async);
    currentBatchStatement = null;
    nestedBatchCount = 0;
  }

  @Override
  public AtreusManagedEntity manageEntity(Object entity) {
    assertSessionNotClosed();

    // Assert input params
    AssertUtils.notNull(entity, "entity is a required parameter");

    // Check if the entity is already a managed entity
    if (entity instanceof AtreusManagedEntity) {

      // Make sure it is saved to the session
      AtreusManagedEntity managedEntity = (AtreusManagedEntity) entity;
      cacheEntity(managedEntity);
      return managedEntity;
    }

    // Check the session for a matching managed entity
    AtreusMetaEntity metaEntity = assertGetMetaEntity(entity);

    Class<?> entityType = metaEntity.getEntityType();
    Serializable primaryKey = (Serializable) metaEntity.getPrimaryKeyField().getValue(entity);

    // A primary key has not been bindValue so create a managed entity and return unsaved to the session
    if (primaryKey == null) {
      return getEntityManager().wrapEntity(this, entity);
    }

    // Look up with in the cache
    AtreusManagedEntity managedEntity = getCachedEntity(entityType, primaryKey);
    if (managedEntity != null) {
      return managedEntity;
    }

    // Create a new managed entity and save to the session
    managedEntity = getEntityManager().wrapEntity(this, entity);
    cacheEntity(managedEntity);
    return managedEntity;
  }

  @Override
  public AtreusManagedEntity getCachedEntity(Class<?> entityType, Serializable primaryKey) {
    assertSessionNotClosed();

    if (!sessionCache) {
      return null;
    }
    CompositeMapKey managedEntityKey = new CompositeMapKey(entityType, primaryKey);
    return cachedEntities.get(managedEntityKey);
  }

  @Override
  public Object unmanageEntity(AtreusManagedEntity managedEntity) {
    assertSessionNotClosed();

    uncacheEntry(managedEntity);
    return managedEntity.getEntity();
  }

  @Override
  public BoundStatement prepareQuery(String cql) {
    assertSessionNotClosed();

    LOG.debug("CQL: {}", cql);
    return getQueryManager().generate(cql);
  }

  @Override
  public BoundStatement prepareQuery(RegularStatement regularStatement) {
    assertSessionNotClosed();

    LOG.debug("CQL: {}", regularStatement.getQueryString());
    return getQueryManager().generate(regularStatement);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T save(T entity) {
    assertSessionNotClosed();

    // Assert input params
    AssertUtils.notNull(entity, "entity is a required parameter");

    // Retrieve managed entity
    AtreusManagedEntity managedEntity = manageEntity(entity);

    // Open a batch if necessary
    batchOpen();

    // Broadcast to the on save listeners
    managedEntity.getMetaEntity().broadcastListeners(this, managedEntity, AtreusOnSaveListener.class);

    // Finish the batch if necessary
    batchFinish();

    // Cache the managed entity
    cacheEntity(managedEntity);
    return (T) managedEntity;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T update(T entity) {
    assertSessionNotClosed();

    // Assert input params
    AssertUtils.notNull(entity, "entity is a required parameter");

    // Retrieve managed entity
    AtreusManagedEntity managedEntity = manageEntity(entity);

    // Open a batch if necessary
    batchOpen();

    // Broadcast to the on save listeners
    managedEntity.getMetaEntity().broadcastListeners(this, managedEntity, AtreusOnUpdateListener.class);

    // Finish the batch if necessary
    batchFinish();

    // Cache the managed entity
    cacheEntity(managedEntity);
    return (T) managedEntity;
  }

  @Override
  public void close() {
    flush();
    cacheClear();
    closed = true;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected Session getCassandraSession() {
    return getEnvironment().getCassandraSession();
  }

  protected AtreusConfiguration getConfiguration() {
    return getEnvironment().getConfiguration();
  }

  protected MetaManagerImpl getEntityManager() {
    return getEnvironment().getMetaManager();
  }

  protected Environment getEnvironment() {
    return environment;
  }

  protected QueryManager getQueryManager() {
    return getEnvironment().getQueryManager();
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private AtreusMetaEntity assertGetMetaEntity(Object entity) {
    Class<?> entityType = entity.getClass();
    return assertGetMetaEntity(entityType);
  }

  private AtreusMetaEntity assertGetMetaEntity(Class<?> entityType) {
    AtreusMetaEntity metaEntity = getEntityManager().getEntity(entityType);
    if (metaEntity == null) {
      throw new RuntimeException(entityType.getCanonicalName() + " is not managed by Atreus");
    }
    return metaEntity;
  }

  private void assertSessionNotClosed() {
    if (isClosed()) {
      throw new RuntimeException("Session has been closed");
    }
    if (getCassandraSession().isClosed()) {
      throw new RuntimeException("Cassandra cluster session has been closed");
    }
  }

  private void batchFinish() {
    nestedBatchCount--;
    if (nestedBatchCount < 1) {
      LOG.trace("Batch Finished");
      flush();
    }
  }

  private void batchOpen() {
    nestedBatchCount++;
    if (isBatchOpen()) {
      return;
    }
    LOG.trace("Batch Opened");
    currentBatchStatement = new BatchStatement();
  }

  private void cacheClear() {
    if (!sessionCache) {
      // Cache is not enabled for this session
      return;
    }
    cachedEntities.clear();
  }

  private void cacheEntity(AtreusManagedEntity managedEntity) {
    if (!sessionCache) {
      // Cache is not enabled for this session
      return;
    }

    Class<?> entityType = managedEntity.getMetaEntity().getEntityType();
    Serializable primaryKey = managedEntity.getPrimaryKey();

    // Cannot cache with null primary ket
    if (primaryKey == null) {
      return;
    }

    // Build composite key and put in the map
    CompositeMapKey managedEntityKey = new CompositeMapKey(entityType, primaryKey);
    cachedEntities.put(managedEntityKey, managedEntity);
  }

  private ResultSet executeRead(Statement statement) {
    statement.setConsistencyLevel(getReadConsistencyLevel());
    return getCassandraSession().execute(statement);
  }

  private void executeWrite(Statement statement, boolean async) {
    statement.setConsistencyLevel(getWriteConsistencyLevel());
    if (async) {
      // TODO implement callback listener architecture
      getCassandraSession().executeAsync(statement);
      return;
    }
    getCassandraSession().execute(statement);
  }

  private AtreusManagedEntity fetchEntity(AtreusMetaEntity metaEntity, Serializable primaryKey, AtreusManagedEntity managedEntity) {
    return ENTITY_FETCH_HANDLER.fetch(this, metaEntity, primaryKey, managedEntity);
  }

  private void uncacheEntry(AtreusManagedEntity managedEntity) {
    if (!sessionCache) {
      // Cache is not enabled for this session
      return;
    }

    Class<?> entityType = managedEntity.getMetaEntity().getEntityType();
    Serializable primaryKey = managedEntity.getPrimaryKey();

    // Build composite key and remove from map
    CompositeMapKey managedEntityKey = new CompositeMapKey(entityType, primaryKey);
    cachedEntities.remove(managedEntityKey);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public boolean isBatchOpen() {
    return currentBatchStatement != null;
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public ConsistencyLevel getReadConsistencyLevel() {
    return readConsistencyLevel;
  }

  @Override
  public void setReadConsistencyLevel(ConsistencyLevel readConsistencyLevel) {
    this.readConsistencyLevel = readConsistencyLevel;
  }

  @Override
  public boolean isWriteAsync() {
    return writeAsync;
  }

  @Override
  public void setWriteAsync(boolean writeAsync) {
    if (isBatchOpen()) {
      throw new RuntimeException("Cannot change write batch mode while a batch is currently open");
    }
    this.writeAsync = writeAsync;
  }

  @Override
  public boolean isWriteBatch() {
    return writeBatch;
  }

  @Override
  public void setWriteBatch(boolean writeBatch) {
    this.writeBatch = writeBatch;
  }

  @Override
  public ConsistencyLevel getWriteConsistencyLevel() {
    return writeConsistencyLevel;
  }

  @Override
  public void setWriteConsistencyLevel(ConsistencyLevel writeConsistencyLevel) {
    this.writeConsistencyLevel = writeConsistencyLevel;
  }

}