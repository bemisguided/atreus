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
package org.atreus.impl.entities.collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Base managed collection.
 *
 * @author Martin Crawford
 */
public abstract class BaseManagedCollection implements Collection, ManagedCollection {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseManagedCollection.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final Collection addedEntities = new HashSet<>();
  private final Collection delegate;
  private final Collection removedEntities = new HashSet<>();

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  protected BaseManagedCollection(Collection delegate) {
    this.delegate = delegate;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return delegate.contains(o);
  }

  @Override
  public Iterator iterator() {
    return delegate.iterator();
  }

  @Override
  public Object[] toArray() {
    return delegate.toArray();
  }

  @Override
  public Object[] toArray(Object[] a) {
    return delegate.toArray(a);
  }

  @SuppressWarnings("unchecked")
  public boolean add(Object o) {
    addedEntities.add(o);
    return delegate.add(o);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean remove(Object o) {
    removedEntities.add(o);
    return delegate.remove(o);
  }

  @SuppressWarnings("unchecked")
  public boolean containsAll(Collection c) {
    return delegate.containsAll(c);
  }

  @SuppressWarnings("unchecked")
  public boolean addAll(Collection c) {
    addedEntities.addAll(c);
    return delegate.addAll(c);
  }

  @SuppressWarnings("unchecked")
  public boolean removeAll(Collection c) {
    removedEntities.addAll(c);
    return delegate.removeAll(c);
  }

  @SuppressWarnings("unchecked")
  public boolean retainAll(Collection c) {
    // TODO implement retainAll
    return delegate.retainAll(c);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void clear() {
    removedEntities.addAll(delegate);
    delegate.clear();
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  @Override
  public Collection getAddedEntities() {
    return addedEntities;
  }

  @Override
  public Collection getCollection() {
    return delegate;
  }

  @Override
  public Collection getRemovedEntities() {
    return removedEntities;
  }

} // end of class