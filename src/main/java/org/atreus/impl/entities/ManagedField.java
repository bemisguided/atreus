package org.atreus.impl.entities;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.types.AtreusTypeAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Managed Field bean.s
 *
 * @author Martin Crawford
 */
public class ManagedField {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ManagedField.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private String columnName;

  private Field javaField;

  private AtreusTypeAccessor typeAccessor;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  public void toEntity(Object entity, Row row) {
    Object value = typeAccessor.get(row, columnName);
    try {
      javaField.set(entity, value);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void fromEntity(Object object, BoundStatement boundStatement) {
    try {
      Object value = javaField.get(object);
      typeAccessor.set(boundStatement, columnName, value);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public Field getJavaField() {
    return javaField;
  }

  public void setJavaField(Field javaField) {
    this.javaField = javaField;
  }

  public AtreusTypeAccessor getTypeAccessor() {
    return typeAccessor;
  }

  public void setTypeAccessor(AtreusTypeAccessor typeAccessor) {
    this.typeAccessor = typeAccessor;
  }

} // end of class