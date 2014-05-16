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
package org.atreus.core.impl;

import com.datastax.driver.core.ResultSet;
import org.atreus.core.BaseAtreusCassandraTests;
import org.atreus.core.tests.entities.functional.ChildCompositeTestEntity;
import org.atreus.core.tests.entities.functional.ParentCompositeSetTestEntity;
import org.atreus.core.tests.entities.functional.ParentCompositeTestEntity;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Functional Tests for various composite association entity mappings.
 *
 * @author Martin Crawford
 */
public class CompositeAssociationFunctionalTests extends BaseAtreusCassandraTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(CompositeAssociationFunctionalTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testSimpleCompositeEntityAssociation() throws Exception {
    LOG.info("Running testSimpleCompositeEntityAssociation");
    addEntity(ParentCompositeTestEntity.class);
    addEntity(ChildCompositeTestEntity.class);
    initEnvironment();

    executeCQL("CREATE TABLE default.ParentCompositeTestEntity (" +
        "id text, " +
        "PRIMARY KEY(id))");

    executeCQL("CREATE TABLE default.ChildCompositeTestEntity (" +
        "ParentCompositeTestEntity_id text, " +
        "id text, " +
        "PRIMARY KEY(ParentCompositeTestEntity_id, id))");

    ParentCompositeTestEntity parentEntity = new ParentCompositeTestEntity();
    ChildCompositeTestEntity childEntity = new ChildCompositeTestEntity();
    parentEntity.setChildEntity(childEntity);
    getSession().save(parentEntity);
    getSession().flush();

    ParentCompositeTestEntity otherEntity = getSession().findOne(ParentCompositeTestEntity.class, parentEntity.getId());

    Assert.assertNotNull("child entity should not be null", otherEntity.getChildEntity());
    Assert.assertEquals(childEntity.getId(), otherEntity.getChildEntity().getId());

    executeCQL("DROP TABLE default.ParentCompositeTestEntity");
    executeCQL("DROP TABLE default.ChildCompositeTestEntity");
  }

  @Test
  public void testCollectionCompositeEntityAssociation() throws Exception {
    LOG.info("Running testCollectionCompositeEntityAssociation");
    addEntity(ParentCompositeSetTestEntity.class);
    addEntity(ChildCompositeTestEntity.class);
    initEnvironment();

    executeCQL("CREATE TABLE default.ParentCompositeSetTestEntity (" +
        "id text, " +
        "PRIMARY KEY(id))");

    executeCQL("CREATE TABLE default.ChildCompositeTestEntity (" +
        "ParentCompositeSetTestEntity_id text, " +
        "id text, " +
        "PRIMARY KEY(ParentCompositeSetTestEntity_id, id))");

    // Save parent entity
    LOG.debug("Save parent entity");
    ParentCompositeSetTestEntity parentEntity = new ParentCompositeSetTestEntity();
    ChildCompositeTestEntity childEntity1 = new ChildCompositeTestEntity();
    ChildCompositeTestEntity childEntity2 = new ChildCompositeTestEntity();
    parentEntity.getChildEntities().add(childEntity1);
    parentEntity.getChildEntities().add(childEntity2);
    getSession().save(parentEntity);
    getSession().flush();

    String primaryKey = parentEntity.getId();
    ParentCompositeSetTestEntity otherEntity = getSession().findOne(ParentCompositeSetTestEntity.class, primaryKey);

    Assert.assertNotNull("child entities should not be null", otherEntity.getChildEntities());
    Assert.assertTrue(otherEntity.getChildEntities().contains(childEntity1));
    Assert.assertTrue(otherEntity.getChildEntities().contains(childEntity2));

    // Add a new child to the collection and remove another
    LOG.debug("Update parent entity with added child and removed child");
    ChildCompositeTestEntity childEntity3 = new ChildCompositeTestEntity();
    otherEntity.getChildEntities().add(childEntity3);
    otherEntity.getChildEntities().remove(childEntity2);
    getSession().save(parentEntity);
    getSession().flush();

    otherEntity = getSession().findOne(ParentCompositeSetTestEntity.class, primaryKey);

    Assert.assertNotNull("child entities should not be null", otherEntity.getChildEntities());
    Assert.assertTrue(otherEntity.getChildEntities().contains(childEntity1));
    Assert.assertFalse(otherEntity.getChildEntities().contains(childEntity2));
    Assert.assertTrue(otherEntity.getChildEntities().contains(childEntity3));

    // Delete the parent entity
    LOG.debug("Delete parent entity");
    getSession().delete(otherEntity);
    getSession().flush();

    otherEntity = getSession().findOne(ParentCompositeSetTestEntity.class, primaryKey);
    Assert.assertNull("Expect not a value", otherEntity);

    ResultSet resultSet = getSession().execute("SELECT * FROM default.ChildCompositeTestEntity");
    Assert.assertTrue(resultSet.isExhausted());

    executeCQL("DROP TABLE default.ParentCompositeSetTestEntity");
    executeCQL("DROP TABLE default.ChildCompositeTestEntity");
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class