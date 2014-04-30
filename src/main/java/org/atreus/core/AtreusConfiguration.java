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

import com.datastax.driver.core.ConsistencyLevel;
import org.atreus.core.ext.AtreusEntityStrategy;
import org.atreus.impl.entities.AtreusAnnotationEntityStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Atreus Configuration.
 *
 * @author Martin Crawford
 */
public class AtreusConfiguration {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusConfiguration.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private boolean defaultBatchWriting = true;
  private ConsistencyLevel defaultConsistencyLevelRead = ConsistencyLevel.ONE;
  private ConsistencyLevel defaultConsistencyLevelWrite = ConsistencyLevel.ONE;
  private String[] hosts;
  private String keySpace;
  private int port;
  private String[] scanPaths;
  private AtreusEntityStrategy[] entityStrategies = new AtreusEntityStrategy[]{new AtreusAnnotationEntityStrategy()};

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public AtreusConfiguration(String host, int port, String keySpace, String... scanPaths) {
    this.hosts = new String[]{host};
    this.keySpace = keySpace;
    this.port = port;
    this.scanPaths = scanPaths;
  }

  public AtreusConfiguration(String[] hosts, int port, String keySpace, String... scanPaths) {
    this.hosts = hosts;
    this.keySpace = keySpace;
    this.port = port;
    this.scanPaths = scanPaths;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public boolean isDefaultBatchWriting() {
    return defaultBatchWriting;
  }

  public void setDefaultBatchWriting(boolean defaultBatchWriting) {
    this.defaultBatchWriting = defaultBatchWriting;
  }

  public ConsistencyLevel getDefaultConsistencyLevelRead() {
    return defaultConsistencyLevelRead;
  }

  public void setDefaultConsistencyLevelRead(ConsistencyLevel defaultConsistencyLevelRead) {
    this.defaultConsistencyLevelRead = defaultConsistencyLevelRead;
  }

  public ConsistencyLevel getDefaultConsistencyLevelWrite() {
    return defaultConsistencyLevelWrite;
  }

  public void setDefaultConsistencyLevelWrite(ConsistencyLevel defaultConsistencyLevelWrite) {
    this.defaultConsistencyLevelWrite = defaultConsistencyLevelWrite;
  }

  public AtreusEntityStrategy[] getEntityStrategies() {
    return entityStrategies;
  }

  public void setEntityStrategies(AtreusEntityStrategy[] entityStrategies) {
    this.entityStrategies = entityStrategies;
  }

  public String[] getHosts() {
    return hosts;
  }

  public String getKeySpace() {
    return keySpace;
  }

  public int getPort() {
    return port;
  }

  public String[] getScanPaths() {
    return scanPaths;
  }

  public void setScanPaths(String... scanPaths) {
    this.scanPaths = scanPaths;
  }

}