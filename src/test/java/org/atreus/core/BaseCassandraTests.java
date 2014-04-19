package org.atreus.core;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.cassandraunit.DataLoader;
import org.cassandraunit.dataset.xml.ClassPathXmlDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for Cassandra required unit tests.
 *
 * @author Martin Crawford
 */
public abstract class BaseCassandraTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(BaseCassandraTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables
  private Cluster cluster;
  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Before
  public void startCassandra() throws Exception {
    EmbeddedCassandraServerHelper.startEmbeddedCassandra();
    cluster = Cluster.builder().addContactPoint("localhost").withPort(9142).build();

  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  protected void loadData(String resourceName) {
    DataLoader dataLoader = new DataLoader("TestCluster", "localhost:9171");
    dataLoader.load(new ClassPathXmlDataSet(resourceName));
  }

  protected Session newSession() {
    return cluster.newSession();
  }

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class