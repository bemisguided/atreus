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
import com.datastax.driver.core.Session;
import org.atreus.core.AtreusSession;
import org.atreus.impl.AtreusEnvironment;
import org.atreus.impl.queries.QueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all Commands.
 *
 * @author Martin Crawford
 */
public abstract class BaseCommand {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseCommand.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final AtreusEnvironment environment;
  private final AtreusSession session;
  private BoundStatement boundStatement;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  protected BaseCommand(AtreusEnvironment environment, AtreusSession session) {
    this.environment = environment;
    this.session = session;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public abstract void prepare();

  public abstract Object execute();

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public Session getCassandraSession() {
    return environment.getCassandraSession();
  }

  public AtreusEnvironment getEnvironment() {
    return environment;
  }

  public AtreusSession getSession() {
    return session;
  }

  protected BoundStatement getBoundStatement() {
    return boundStatement;
  }

  protected void setBoundStatement(BoundStatement boundStatement) {
    this.boundStatement = boundStatement;
  }

  protected QueryManager getQueryManager() {
    return environment.getQueryManager();
  }

} // end of class