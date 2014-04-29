package org.atreus.impl.types.cql;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusCollectionTypeStrategy;
import org.atreus.core.ext.AtreusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * List Type Strategy.
 *
 * @author Martin Crawford
 */
@AtreusType(List.class)
public class ListTypeStrategy extends BaseCollectionTypeStrategy implements AtreusCollectionTypeStrategy<List> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(ListTypeStrategy.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public List get(Row row, String colName) {
    return row.getList(colName, getValueClass());
  }

  @Override
  public void set(BoundStatement boundStatement, String colName, List value) {
    boundStatement.setList(colName, value);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class