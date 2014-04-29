package org.atreus.impl.types.cql;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusMapTypeStrategy;
import org.atreus.core.ext.AtreusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * List Type Strategy.
 *
 * @author Martin Crawford
 */
@AtreusType(Map.class)
public class MapTypeStrategy extends BaseCollectionTypeStrategy implements AtreusMapTypeStrategy<Map> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(MapTypeStrategy.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private Class<?> keyClass;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public Class<?> getKeyClass() {
    return keyClass;
  }

  @Override
  public void setKeyClass(Class<?> keyClass) {
    this.keyClass = keyClass;
  }

  @Override
  public Map get(Row row, String colName) {
    return row.getMap(colName, getValueClass(), getKeyClass());
  }

  @Override
  public void set(BoundStatement boundStatement, String colName, Map value) {
    boundStatement.setMap(colName, value);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class