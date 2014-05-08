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
package org.atreus.impl.commands;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.impl.Environment;
import org.atreus.impl.entities.BindingHelper;
import org.atreus.impl.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Find by primary key command.
 *
 * @author Martin Crawford
 */
public class FindByPrimaryKeyCommand extends BaseCommand {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(FindByPrimaryKeyCommand.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Serializable primaryKey;

  private AtreusMetaEntity managedEntity;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public Object execute(Environment environment, AtreusSessionExt session) {
    RegularStatement regularStatement = QueryHelper.selectEntity(managedEntity);
    BoundStatement boundStatement = environment.getQueryManager().generate(regularStatement);
    BindingHelper.bindFromPrimaryKeys(managedEntity, boundStatement, primaryKey);
    ResultSet resultSet = session.execute(boundStatement);
    Row row = resultSet.one();
    if (row == null) {
      return null;
    }
    try {
      Object entity = managedEntity.getEntityType().newInstance();
      BindingHelper.bindToEntity(managedEntity, entity, row);
      return entity;
    }
    catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public Serializable getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(Serializable primaryKey) {
    this.primaryKey = primaryKey;
  }

  public AtreusMetaEntity getManagedEntity() {
    return managedEntity;
  }

  public void setManagedEntity(AtreusMetaEntity managedEntity) {
    this.managedEntity = managedEntity;
  }

} // end of class