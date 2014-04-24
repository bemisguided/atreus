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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.atreus.core.AtreusSession;
import org.atreus.core.ext.entities.AtreusManagedEntity;
import org.atreus.impl.entities.BindingHelper;
import org.atreus.impl.queries.QueryHelper;
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

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public AtreusSessionImpl(AtreusEnvironment environment) {
    this.environment = environment;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public <T> T findByKey(Class<T> entityType, Serializable primaryKey) {
    AssertUtils.notNull(entityType, "entityType is a required parameter");
    AtreusManagedEntity managedEntity = environment.getEntityManager().getEntity(entityType);
    if (managedEntity == null) {
      throw new RuntimeException(entityType.getCanonicalName() + " is not managed by Atreus");
    }
    RegularStatement statement = QueryHelper.selectEntity(managedEntity);
    LOG.debug("CQL Statement: {}", statement.getQueryString());
    BoundStatement boundStatement = environment.getQueryManager().generate(statement);
    BindingHelper.bindFromPrimaryKeys(managedEntity, boundStatement, primaryKey);
    ResultSet resultSet = environment.getCassandraSession().execute(boundStatement);
    Row row = resultSet.one();
    if (row == null) {
      return null;
    }
    try {
      T entity = entityType.newInstance();
      BindingHelper.bindToEntity(managedEntity, entity, row);
      return entity;
    }
    catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void save(Object entity) {
    AssertUtils.notNull(entity, "entity is a required parameter");
    AtreusManagedEntity managedEntity = environment.getEntityManager().getEntity(entity.getClass());
    if (managedEntity == null) {
      throw new RuntimeException(entity.getClass().getCanonicalName() + " is not managed by Atreus");
    }
    RegularStatement statement = QueryHelper.insertEntity(managedEntity);
    LOG.debug("CQL Statement: {}", statement.getQueryString());
    BoundStatement boundStatement = environment.getQueryManager().generate(statement);
    BindingHelper.bindFromEntity(managedEntity, entity, boundStatement);
    environment.getCassandraSession().execute(boundStatement);
  }

  @Override
  public void close() {
    // TODO Internal closing of the Atreus Session
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

}