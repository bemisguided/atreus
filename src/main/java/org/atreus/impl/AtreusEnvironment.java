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

import com.datastax.driver.core.Cluster;
import org.atreus.core.AtreusConfiguration;
import org.atreus.impl.entities.EntityManager;
import org.atreus.impl.types.TypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Atreus Environment setup.
 *
 * @author Martin Crawford
 */
public class AtreusEnvironment {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusEnvironment.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Cluster cluster;
  private final AtreusConfiguration configuration;
  private final EntityManager entityManager;
  private final TypeManager typeManager;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public AtreusEnvironment(AtreusConfiguration configuration) {
    this.configuration = configuration;
    this.entityManager = new EntityManager(this);
    this.typeManager = new TypeManager(this);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public Cluster getCluster() {
    return cluster;
  }

  public void setCluster(Cluster cluster) {
    this.cluster = cluster;
  }

  public AtreusConfiguration getConfiguration() {
    return configuration;
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public TypeManager getTypeManager() {
    return typeManager;
  }

}