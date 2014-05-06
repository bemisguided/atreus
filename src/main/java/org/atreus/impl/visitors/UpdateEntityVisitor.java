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
package org.atreus.impl.visitors;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.RegularStatement;
import org.atreus.core.AtreusDataBindingException;
import org.atreus.core.AtreusSession;
import org.atreus.core.ext.AtreusEntityVisitor;
import org.atreus.core.ext.AtreusManagedEntity;
import org.atreus.core.ext.AtreusManagedField;
import org.atreus.impl.entities.BindingHelper;
import org.atreus.impl.queries.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Update Entity visitor.
 *
 * @author Martin Crawford
 */
public class UpdateEntityVisitor extends AtreusEntityVisitor {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(UpdateEntityVisitor.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public void acceptEntity(AtreusSession session, AtreusManagedEntity managedEntity, Object entity) {
    AtreusManagedField managedPrimaryKey = managedEntity.getPrimaryKeyField();

    boolean hasTtl = false;
    AtreusManagedField ttlField = managedEntity.getTtlField();
    if (ttlField != null && !BindingHelper.isNull(ttlField, entity)) {
      hasTtl = true;
    }
    RegularStatement regularStatement = QueryHelper.insertEntity(managedEntity, hasTtl);
    BoundStatement boundStatement = session.prepareQuery(regularStatement);
    bindFromField(managedPrimaryKey, entity, boundStatement);
    for (AtreusManagedField managedField : managedEntity.getFields()) {
      bindFromField(managedField, entity, boundStatement);
    }

    bindFromEntityTtl(managedEntity, entity, boundStatement);
    session.executeOrBatch(boundStatement);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  @SuppressWarnings("unchecked")
  private void bindFromField(AtreusManagedField managedField, Object entity, BoundStatement boundStatement) {
    Object value = getField(managedField, entity);
    managedField.getTypeStrategy().set(boundStatement, managedField.getColumn(), value);
  }

  @SuppressWarnings("unchecked")
  private void bindFromEntityTtl(AtreusManagedEntity managedEntity, Object entity, BoundStatement boundStatement) {
    AtreusManagedField ttlField = managedEntity.getTtlField();
    if (ttlField == null) {
      return;
    }
    Object value = getField(ttlField, entity);
    if (value == null) {
      return;
    }

    Integer ttlValue = managedEntity.getTtlStrategy().translate(new Date(), value);
    if (ttlValue == null || ttlValue < 1) {
      throw new AtreusDataBindingException(AtreusDataBindingException.ERROR_CODE_INVALID_TIME_TO_LIVE_VALUE, ttlField, ttlValue);
    }
    boundStatement.setInt(ttlField.getColumn(), ttlValue);
  }

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class