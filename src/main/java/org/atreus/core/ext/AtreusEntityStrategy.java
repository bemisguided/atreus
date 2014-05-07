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

import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.ext.strategies.AtreusPrimaryKeyStrategy;
import org.atreus.core.ext.strategies.AtreusTtlStrategy;
import org.atreus.core.ext.strategies.AtreusTypeStrategy;

import java.util.Collection;

/**
 * Interface for a strategy to configure managed entities.
 *
 * @author Martin Crawford
 */
public interface AtreusEntityStrategy {

  public Class<?> getCollectionValue(AtreusMetaField managedField);

  public String getEntityName(AtreusMetaEntity managedEntity);

  public String getEntityKeySpace(AtreusMetaEntity managedEntity);

  public String getEntityTable(AtreusMetaEntity managedEntity);

  public String getFieldColumn(AtreusMetaField managedField);

  public Class<?> getMapKey(AtreusMetaField managedField);

  public String getPrimaryKeyColumn(AtreusMetaField managedField);

  public Collection<Class<?>> findEntities(String path);

  public boolean isPrimaryKeyField(AtreusMetaField managedField);

  public boolean isPrimaryKeyGenerated(AtreusMetaField managedField);

  public boolean isTtlField(AtreusMetaField managedField);

  public AtreusPrimaryKeyStrategy resolvePrimaryKeyStrategy(AtreusMetaField managedField);

  public AtreusTtlStrategy resolveTtlStrategy(AtreusMetaField managedField);

  public AtreusTypeStrategy resolveTypeStrategy(AtreusMetaField managedField);

}