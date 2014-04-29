package org.atreus.impl.types.cql;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.atreus.core.ext.AtreusCollectionTypeStrategy;
import org.atreus.core.ext.AtreusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Set Type Strategy.
 *
 * @author Martin Crawford
 */
@AtreusType(Set.class)
public class SetTypeStrategy extends BaseCollectionTypeStrategy implements AtreusCollectionTypeStrategy<Set> {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(SetTypeStrategy.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public Set get(Row row, String colName) {
    return row.getSet(colName, getValueClass());
  }

  @Override
  public void set(BoundStatement boundStatement, String colName, Set value) {
    boundStatement.setSet(colName, value);
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class