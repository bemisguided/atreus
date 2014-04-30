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
package org.atreus.core.ext;

import java.util.Collection;

/**
 * Interface for a strategy to configure managed entities.
 *
 * @author Martin Crawford
 */
public interface AtreusEntityStrategy {

  public Class<?> getCollectionValue(AtreusManagedField managedField);

  public String getEntityName(AtreusManagedEntity managedEntity);

  public String getEntityKeySpace(AtreusManagedEntity managedEntity);

  public String getEntityTable(AtreusManagedEntity managedEntity);

  public String getFieldColumn( AtreusManagedField managedField);

  public Class<?> getMapKey(AtreusManagedField managedField);

  public String getPrimaryKeyColumn( AtreusManagedField managedField);

  public Collection<Class<?>> findEntities(String path);

  public boolean isPrimaryKeyField(AtreusManagedField managedField);

  public boolean isPrimaryKeyGenerated(AtreusManagedField managedField);

  public boolean isTtlField(AtreusManagedField managedField);

  public AtreusPrimaryKeyStrategy resolvePrimaryKeyStrategy(AtreusManagedField managedField);

  public AtreusTtlStrategy resolveTtlStrategy(AtreusManagedField managedField);

  public AtreusTypeStrategy resolveTypeStrategy(AtreusManagedField managedField);

}