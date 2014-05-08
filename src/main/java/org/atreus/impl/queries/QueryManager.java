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
package org.atreus.impl.queries;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import org.atreus.impl.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages queries and the caching of prepared statements.
 *
 * @author Martin Crawford
 */
public class QueryManager {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(QueryManager.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final Environment environment;

  private Map<String, PreparedStatement> preparedStatementMap = new HashMap<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public QueryManager(Environment environment) {
    this.environment = environment;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public BoundStatement generate(RegularStatement cassandraStatement) {
    return generate(cassandraStatement.getQueryString());
  }

  public BoundStatement generate(String cql) {
    LOG.debug("CQL Statement: {}", cql);
    PreparedStatement preparedStatement = preparedStatementMap.get(cql);

    if (preparedStatement == null) {
      preparedStatement = environment.getCassandraSession().prepare(cql);
      preparedStatementMap.put(cql, preparedStatement);
    }
    return new BoundStatement(preparedStatement);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class