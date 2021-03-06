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
package org.atreus.core;

import com.datastax.driver.core.*;

import java.io.Serializable;

public interface AtreusSession {

  public boolean isBatchOpen();

  public boolean isClosed();

  public boolean isWriteAsync();

  public void setWriteAsync(boolean writeAsync);

  public boolean isWriteBatch();

  public void setWriteBatch(boolean batchWrite);

  public ConsistencyLevel getReadConsistencyLevel();

  public void setReadConsistencyLevel(ConsistencyLevel consistencyLevel);

  public ConsistencyLevel getWriteConsistencyLevel();

  public void setWriteConsistencyLevel(ConsistencyLevel consistencyLevel);

  public void close();

  public <T> void delete(T entity);

  public ResultSet execute(Statement statement);

  public ResultSet execute(String cql);

  public void executeOrBatch(Statement statement);

  public void executeOrBatch(String cql);

  public <T> T findOne(Class<T> entityType, Serializable primaryKey);

  public void flush();

  public void flush(boolean async);

  public <T> T save(T entity);

  public BoundStatement prepareQuery(String cql);

  public BoundStatement prepareQuery(RegularStatement regularStatement);

  public <T> T update(T entity);

}
