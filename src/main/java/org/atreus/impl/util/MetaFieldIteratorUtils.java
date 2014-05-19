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
package org.atreus.impl.util;

import org.atreus.core.ext.meta.AtreusMetaAssociationField;
import org.atreus.core.ext.meta.AtreusMetaComplexField;
import org.atreus.core.ext.meta.AtreusMetaField;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class to iterate Meta Fields.
 *
 * @author Martin Crawford
 */
public class MetaFieldIteratorUtils {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(MetaFieldIteratorUtils.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  private MetaFieldIteratorUtils() {
  }


  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public static Iterable<AtreusMetaSimpleField> iterateMetaSimpleFields(Collection<? extends AtreusMetaField> metaFields) {
    List<AtreusMetaSimpleField> results = new ArrayList<>();
    for (AtreusMetaField metaField : metaFields) {
      addMetaSimpleFields(metaField, results);
    }
    return results;
  }

  public static Iterable<AtreusMetaSimpleField> iterateMetaSimpleFields(AtreusMetaField... metaFields) {
    List<AtreusMetaSimpleField> results = new ArrayList<>();
    for (AtreusMetaField metaField : metaFields) {
      addMetaSimpleFields(metaField, results);
    }
    return results;
  }

  public static Iterable<AtreusMetaField> iterateMetaFields(Collection<? extends AtreusMetaField> metaFields) {
    List<AtreusMetaField> results = new ArrayList<>();
    for (AtreusMetaField metaField : metaFields) {
      addMetaFields(metaField, results);
    }
    return results;
  }

  public static Iterable<AtreusMetaField> iterateMetaFields(AtreusMetaField... metaFields) {
    List<AtreusMetaField> results = new ArrayList<>();
    for (AtreusMetaField metaField : metaFields) {
      addMetaFields(metaField, results);
    }
    return results;
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  private static void addMetaSimpleFields(AtreusMetaField metaField, List<AtreusMetaSimpleField> results) {
    if (metaField instanceof AtreusMetaComplexField) {
      for (AtreusMetaSimpleField metaSimpleField : ((AtreusMetaComplexField) metaField).getFields()) {
        results.add(metaSimpleField);
      }
    }
    if (metaField instanceof AtreusMetaSimpleField) {
      results.add((AtreusMetaSimpleField) metaField);
    }
  }

  private static void addMetaFields(AtreusMetaField metaField, List<AtreusMetaField> results) {
    if (metaField instanceof AtreusMetaComplexField) {
      for (AtreusMetaField metaSubField : ((AtreusMetaComplexField) metaField).getFields()) {
        results.add(metaSubField);
      }
    }
    if (metaField instanceof AtreusMetaSimpleField) {
      results.add(metaField);
    }
    if (metaField instanceof AtreusMetaAssociationField) {
      results.add(metaField);
    }
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class