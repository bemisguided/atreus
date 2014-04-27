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

import com.datastax.driver.core.ConsistencyLevel;
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

  private ConsistencyLevel writeConsistencyLevel;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public AtreusSessionImpl(AtreusEnvironment environment) {
    this.environment = environment;
    readConsistencyLevel = environment.getConfiguration().getDefaultConsistencyLevelRead();
    writeConsistencyLevel = environment.getConfiguration().getDefaultConsistencyLevelWrite();
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public <T> T findByPrimaryKey(Class<T> entityType, Serializable primaryKey) {
    // Assert input params
    AssertUtils.notNull(entityType, "entityType is a required parameter");
    AtreusManagedEntity managedEntity = environment.getEntityManager().getEntity(entityType);
    if (managedEntity == null) {
      throw new RuntimeException(entityType.getCanonicalName() + " is not managed by Atreus");
    }

    // Build command
    FindByPrimaryKeyCommand command = new FindByPrimaryKeyCommand(environment, this);
    command.setManagedEntity(managedEntity);
    command.setPrimaryKey(primaryKey);

    // Execute (not a batchable command)
    return doExecute(command, entityType);
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
    SaveCommand command = new SaveCommand(environment, this);
    command.setManagedEntity(managedEntity);
    command.setEntity(entity);

    // Execute or batch
    doExecuteOrBatch(command);
  }

  @Override
  public void close() {
    // TODO Internal closing of the Atreus Session
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  @SuppressWarnings("unchecked")
  protected <T> T doExecute(BaseCommand command, Class<T> type) {
    command.prepare();
    return (T) command.execute();
  }

  @SuppressWarnings("unchecked")
  protected void doExecuteOrBatch(BaseCommand command) {
    // TODO batch
    doExecute(command, null);
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

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
  public ConsistencyLevel getWriteConsistencyLevel() {
    return writeConsistencyLevel;
  }

  @Override
  public void setWriteConsistencyLevel(ConsistencyLevel writeConsistencyLevel) {
    this.writeConsistencyLevel = writeConsistencyLevel;
  }

}