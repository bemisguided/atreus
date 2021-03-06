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
package org.atreus.core.ext.meta;

import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusSessionExt;
import org.atreus.core.ext.listeners.AtreusEntityListener;
import org.atreus.core.ext.strategies.AtreusPrimaryKeyStrategy;
import org.atreus.core.ext.strategies.AtreusTtlStrategy;

/**
 * Atreus meta property interface defining a managed entity.
 *
 * @author Martin Crawford
 */
public interface AtreusMetaEntity extends AtreusMetaObject {

  public void addListener(AtreusEntityListener listener);

  public void broadcastListeners(AtreusSessionExt session, AtreusManagedEntity managedEntity, Class<? extends AtreusEntityListener> event);

  public AtreusMetaAssociation[] getAssociations();

  public Class<?> getEntityType();

  public AtreusMetaField getPrimaryKeyField();

  public AtreusPrimaryKeyStrategy getPrimaryKeyStrategy();

  public AtreusMetaTable getTable();

  public AtreusMetaSimpleField getTtlField();

  public AtreusTtlStrategy getTtlStrategy();

}