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
import org.atreus.core.ext.meta.AtreusMetaSimpleField;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Interface applied to entities once managed by Atreus.
 *
 * @author Martin Crawford
 */
public interface AtreusManagedEntity {

  public void snapshot();

  public void fetchField(AtreusMetaField metaField);

  public Map<String, Object> getDynamicFields();

  public Object getEntity();

  public boolean isFetched(AtreusMetaField metaField);

  public Object getFieldValue(AtreusMetaField metaField);

  public void setFieldValue(AtreusMetaField metaField, Object value);

  public AtreusMetaEntity getMetaEntity();

  public Serializable getPrimaryKey();

  public Collection<AtreusMetaSimpleField> getUpdatedFields();

}