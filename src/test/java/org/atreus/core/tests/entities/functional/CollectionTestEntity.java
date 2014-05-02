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
package org.atreus.core.tests.entities.functional;

import org.atreus.core.annotations.AtreusCollection;
import org.atreus.core.annotations.AtreusEntity;
import org.atreus.core.annotations.AtreusMap;
import org.atreus.core.annotations.AtreusPrimaryKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CollectionTestEntity.
 *
 * @author Martin Crawford
 */
@AtreusEntity()
public class CollectionTestEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  @AtreusPrimaryKey
  private String id;

  @AtreusCollection(type = Long.class)
  private Set<Long> setField;

  private List<String> listField;

  @AtreusCollection(type = Long.class)
  @AtreusMap(key = String.class)
  private Map<String, Long> mapField1;

  @AtreusCollection
  @AtreusMap
  private Map<String, Long> mapField2;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Set<Long> getSetField() {
    return setField;
  }

  public void setSetField(Set<Long> setField) {
    this.setField = setField;
  }

  public List<String> getListField() {
    return listField;
  }

  public void setListField(List<String> listField) {
    this.listField = listField;
  }

  public Map<String, Long> getMapField1() {
    return mapField1;
  }

  public void setMapField1(Map<String, Long> mapField1) {
    this.mapField1 = mapField1;
  }

  public Map<String, Long> getMapField2() {
    return mapField2;
  }

  public void setMapField2(Map<String, Long> mapField2) {
    this.mapField2 = mapField2;
  }

}