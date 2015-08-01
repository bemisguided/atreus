package org.atreus.impl.core.entities;

import java.lang.reflect.Field;

/**
 * todo document me
 *
 * @author Martin Crawford
 */
public interface EntityAccessor {

  public Object getFieldValue(Field javaField);

  public void setFieldValue(Field javaField, Object value);

}