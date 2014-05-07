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

import com.datastax.driver.core.*;
import org.atreus.core.AtreusSession;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.impl.commands.BaseCommand;
import org.atreus.impl.commands.FindByPrimaryKeyCommand;
import org.atreus.impl.commands.SaveCommand;
import org.atreus.impl.util.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Implements an Atreus Session.
 *
 * @author Martin Crawford
 */
public class AtreusSessionImpl implements AtreusSession {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusSessionImpl.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusEnvironment environment;

  private ConsistencyLevel readConsistencyLevel;

  private boolean writeAsync;

  private boolean writeBatch;

  private ConsistencyLevel writeConsistencyLevel;

  private BatchStatement batchStatement;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public AtreusSessionImpl(AtreusEnvironment environment) {
    this.environment = environment;
    writeAsync = environment.getConfiguration().isDefaultWriteAsync();
    writeBatch = environment.getConfiguration().isDefaultWriteBatch();
    readConsistencyLevel = environment.getConfiguration().getDefaultReadConsistencyLevel();
    writeConsistencyLevel = environment.getConfiguration().getDefaultWriteConsistencyLevel();
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public ResultSet execute(Statement statement) {
    return executeRead(statement);
  }

  @Override
  public ResultSet execute(String cql) {
    return execute(new SimpleStatement(cql));
  }

  @Override
  public void executeOrBatch(Statement statement) {
    if (isWriteBatch()) {
      if (batchStatement == null) {
        batchStatement = new BatchStatement();
      }
      batchStatement.add(statement);
      return;
    }
    environment.getCassandraSession().execute(statement);
  }

  @Override
  public void executeOrBatch(String cql) {
    executeOrBatch(new SimpleStatement(cql));
  }

  @Override
  public void flush() {
    if (batchStatement == null) {
      return;
    }
    executeWrite(batchStatement, isWriteAsync());
  }

  @Override
  public void flush(boolean async) {
    if (batchStatement == null) {
      return;
    }
    executeWrite(batchStatement, async);
  }

  @Override
  public <T> T findOne(Class<T> entityType, Serializable primaryKey) {
    // Assert input params
    AssertUtils.notNull(entityType, "entityType is a required parameter");
    AtreusManagedEntity managedEntity = environment.getEntityManager().getEntity(entityType);
    if (managedEntity == null) {
      throw new RuntimeException(entityType.getCanonicalName() + " is not managed by Atreus");
    }

    // Build command
    FindByPrimaryKeyCommand command = new FindByPrimaryKeyCommand();
    command.setManagedEntity(managedEntity);
    command.setPrimaryKey(primaryKey);

    // Execute (not a batchable command)
    return doExecute(command, entityType);
  }

  @Override
  public BoundStatement prepareQuery(String cql) {
    return environment.getQueryManager().generate(cql);
  }

  @Override
  public BoundStatement prepareQuery(RegularStatement regularStatement) {
    return environment.getQueryManager().generate(regularStatement);
  }

  @Override
  public void save(Object entity) {
    // Assert input params
    AssertUtils.notNull(entity, "entity is a required parameter");
    AtreusManagedEntity managedEntity = environment.getEntityManager().getEntity(entity.getClass());
    if (managedEntity == null) {
      throw new RuntimeException(entity.getClass().getCanonicalName() + " is not managed by Atreus");
    }

    // Build command
    SaveCommand command = new SaveCommand();
    command.setEntity(entity);

    // Execute or batch
    doExecute(command, null);
  }

  @Override
  public void close() {
    // TODO Internal closing of the Atreus Session
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  @SuppressWarnings("unchecked")
  protected <T> T doExecute(BaseCommand command, Class<T> type) {
    return (T) command.execute(environment, this);
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private ResultSet executeRead(Statement statement) {
    statement.setConsistencyLevel(getReadConsistencyLevel());
    return environment.getCassandraSession().execute(statement);
  }

  private void executeWrite(Statement statement, boolean async) {
    statement.setConsistencyLevel(getWriteConsistencyLevel());
    if (async) {
      // TODO implement callback listener architecture
      environment.getCassandraSession().executeAsync(statement);
      return;
    }
    environment.getCassandraSession().execute(statement);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters


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