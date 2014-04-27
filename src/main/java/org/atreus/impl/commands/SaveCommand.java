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

import com.datastax.driver.core.RegularStatement;
import org.atreus.core.AtreusSession;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.impl.AtreusEnvironment;
import org.atreus.impl.entities.BindingHelper;
import org.atreus.impl.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Save command.
 *
 * @author Martin Crawford
 */
public class SaveCommand extends BaseCommand {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(SaveCommand.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Object entity;

  private AtreusManagedEntity managedEntity;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public SaveCommand(AtreusEnvironment environment, AtreusSession session) {
    super(environment, session);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void prepare() {
    RegularStatement statement = QueryHelper.insertEntity(managedEntity);
    setBoundStatement(getQueryManager().generate(statement));
    BindingHelper.bindFromEntity(managedEntity, entity, getBoundStatement());
  }

  @Override
  public Object execute() {
    getBoundStatement().setConsistencyLevel(getSession().getWriteConsistencyLevel());
    getEnvironment().getCassandraSession().execute(getBoundStatement());
    return null;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public Object getEntity() {
    return entity;
  }

  public void setEntity(Object entity) {
    this.entity = entity;
  }

  public AtreusManagedEntity getManagedEntity() {
    return managedEntity;
  }

  public void setManagedEntity(AtreusManagedEntity managedEntity) {
    this.managedEntity = managedEntity;
  }

} // end of class