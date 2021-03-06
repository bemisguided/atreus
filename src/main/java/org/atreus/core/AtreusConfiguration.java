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

  private ConsistencyLevel defaultReadConsistencyLevel = ConsistencyLevel.ONE;
  private boolean defaultWriteAsync = false;
  private boolean defaultWriteBatch = true;
  private ConsistencyLevel defaultWriteConsistencyLevel = ConsistencyLevel.ONE;
  private String[] hosts;
  private String keySpace;
  private int port;
  private String[] scanPaths;
  private boolean sessionCache = true;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public ConsistencyLevel getDefaultReadConsistencyLevel() {
    return defaultReadConsistencyLevel;
  }

  public void setDefaultReadConsistencyLevel(ConsistencyLevel defaultReadConsistencyLevel) {
    this.defaultReadConsistencyLevel = defaultReadConsistencyLevel;
  }

  public boolean isDefaultWriteAsync() {
    return defaultWriteAsync;
  }

  public void setDefaultWriteAsync(boolean defaultWriteAsync) {
    this.defaultWriteAsync = defaultWriteAsync;
  }

  public boolean isDefaultWriteBatch() {
    return defaultWriteBatch;
  }

  public void setDefaultWriteBatch(boolean defaultWriteBatch) {
    this.defaultWriteBatch = defaultWriteBatch;
  }

  public ConsistencyLevel getDefaultWriteConsistencyLevel() {
    return defaultWriteConsistencyLevel;
  }

  public void setDefaultWriteConsistencyLevel(ConsistencyLevel defaultWriteConsistencyLevel) {
    this.defaultWriteConsistencyLevel = defaultWriteConsistencyLevel;
  }

  public String[] getHosts() {
    return hosts;
  }

  public void setHosts(String... hosts) {
    this.hosts = hosts;
  }

  public String getKeySpace() {
    return keySpace;
  }

  public void setKeySpace(String keySpace) {
    this.keySpace = keySpace;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String[] getScanPaths() {
    return scanPaths;
  }

  public void setScanPaths(String... scanPaths) {
    this.scanPaths = scanPaths;
  }

  public boolean isSessionCache() {
    return sessionCache;
  }

  public void setSessionCache(boolean sessionCache) {
    this.sessionCache = sessionCache;
  }

}