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

import org.atreus.core.AtreusDataBindingException;
import org.atreus.core.AtreusInitialisationException;
import org.atreus.core.BaseAtreusCassandraTests;
import org.atreus.core.ext.AtreusCQLDataType;
import org.atreus.core.ext.meta.AtreusMetaEntity;
import org.atreus.core.ext.meta.AtreusMetaSimpleField;
import org.atreus.core.tests.entities.errors.*;
import org.atreus.core.tests.entities.functional.*;
import org.atreus.impl.queries.QueryHelper;
import org.atreus.impl.types.generators.StringPrimaryKeyStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.*;

/**
 * Functional Tests for various entity mappings.
 *
 * @author Martin Crawford
 */
public class EntityFunctionalTests extends BaseAtreusCassandraTests {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(EntityFunctionalTests.class);

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Test
  public void testAtreusTypes() throws Exception {
    LOG.info("Running testAtreusTypes");
    addEntity(AtreusTypesTestEntity.class);
    initEnvironment();

    executeCQL("CREATE TABLE default.AtreusTypesTestEntity (" +
        "id text, " +
        "aShort int, " +
        "anEnum text, " +
        "PRIMARY KEY(id))");

    AtreusTypesTestEntity testEntity = new AtreusTypesTestEntity();
    testEntity.setaShort((short) 1234);
    testEntity.setAnEnum(AtreusCQLDataType.CQL_ASCII);

    getSession().save(testEntity);
    getSession().flush();
    String primaryKey = testEntity.getId();

    AtreusTypesTestEntity otherEntity = getSession().findOne(AtreusTypesTestEntity.class, primaryKey);

    Assert.assertNotNull("Expect a value", otherEntity);
    Assert.assertEquals(primaryKey, otherEntity.getId());
    Assert.assertEquals(1234, (short) otherEntity.getaShort());
    Assert.assertEquals(AtreusCQLDataType.CQL_ASCII, otherEntity.getAnEnum());

    // Null values
    testEntity = new AtreusTypesTestEntity();
    getSession().save(testEntity);
    getSession().flush();
    primaryKey = testEntity.getId();

    otherEntity = getSession().findOne(AtreusTypesTestEntity.class, primaryKey);

    Assert.assertNotNull("Expect a value", otherEntity);
    Assert.assertNull("Expect null short", otherEntity.getaShort());
    Assert.assertNull("Expect null enum", otherEntity.getAnEnum());
  }

  @Test
  public void testCQLPrimitiveTypes() throws Exception {
    LOG.info("Running testCQLPrimitiveTypes");
    addEntity(CQLPrimitiveTypesTestEntity.class);
    initEnvironment();

    executeCQL("CREATE TABLE default.CQLPrimitiveTypesTestEntity (" +
        "id text, " +
        "aBigDecimal decimal, " +
        "aBigInteger varint, " +
        "aBoolean boolean, " +
        "aDate timestamp, " +
        "aDouble double, " +
        "aFloat float, " +
        "anInetAddress inet, " +
        "aInteger int, " +
        "aLong bigint, " +
        "aString text, " +
        "aUuid uuid, " +
        "PRIMARY KEY(id))");


    CQLPrimitiveTypesTestEntity testEntity = new CQLPrimitiveTypesTestEntity();
    testEntity.setaBigDecimal(new BigDecimal("1.23"));
    testEntity.setaBigInteger(new BigInteger("123"));
    testEntity.setaBoolean(true);
    testEntity.setaDate(new Date(12000000));
    testEntity.setaDouble(0.50);
    testEntity.setaFloat(0.25f);
    testEntity.setAnInetAddress(InetAddress.getByName("127.0.0.1"));
    testEntity.setaInteger(1234);
    testEntity.setaLong(4321);
    testEntity.setaString("value");
    testEntity.setaUuid(UUID.fromString("4f7a8b70-d002-11e3-9c1a-0800200c9a66"));

    getSession().save(testEntity);
    getSession().flush();
    String primaryKey = testEntity.getId();

    CQLPrimitiveTypesTestEntity otherEntity = getSession().findOne(CQLPrimitiveTypesTestEntity.class, primaryKey);

    Assert.assertNotNull("Expect a value", otherEntity);
    Assert.assertEquals(primaryKey, otherEntity.getId());
    Assert.assertEquals(new BigDecimal("1.23"), otherEntity.getaBigDecimal());
    Assert.assertEquals(new BigInteger("123"), otherEntity.getaBigInteger());
    Assert.assertEquals(true, otherEntity.isaBoolean());
    Assert.assertEquals(new Date(12000000), otherEntity.getaDate());
    Assert.assertEquals(0.50, otherEntity.getaDouble(), 0);
    Assert.assertEquals(0.25f, otherEntity.getaFloat(), 0);
    Assert.assertEquals(InetAddress.getByName("127.0.0.1"), otherEntity.getAnInetAddress());
    Assert.assertEquals(1234, otherEntity.getaInteger());
    Assert.assertEquals(4321, otherEntity.getaLong());
    Assert.assertEquals("value", otherEntity.getaString());
    Assert.assertEquals(UUID.fromString("4f7a8b70-d002-11e3-9c1a-0800200c9a66"), otherEntity.getaUuid());
  }


  @Test
  public void testTimeToLiveEntity() throws Exception {
    LOG.info("Running testTimeToLiveEntity");
    addEntity(TtlTestEntity.class);
    initEnvironment();

    try {
      executeCQL("CREATE TABLE default.TtlTestEntity (" +
          "id text, " +
          "value text, " +
          "PRIMARY KEY(id))");

      // Test with a time-to-live bindValue
      TtlTestEntity testEntity = new TtlTestEntity();
      testEntity.setValue("I am a text value");
      testEntity.setTtl(2); // 2 seconds

      getSession().save(testEntity);
      getSession().flush();
      String primaryKey = testEntity.getId();

      TtlTestEntity otherEntity = getSession().findOne(TtlTestEntity.class, primaryKey);
      Assert.assertNotNull("Expect to be not null", otherEntity);
      Assert.assertEquals(primaryKey, otherEntity.getId());
      Assert.assertEquals("I am a text value", otherEntity.getValue());
      Assert.assertNull("Time-to-live value should be null", otherEntity.getTtl());

      // Wait until expired
      sleepSeconds(3); // Sleep for 3 secs

      otherEntity = getSession().findOne(TtlTestEntity.class, primaryKey);
      Assert.assertNull("Expect to be null", otherEntity);

      // Test w/o a time-to-live bindValue
      testEntity = new TtlTestEntity();
      testEntity.setValue("I am another text value");

      getSession().save(testEntity);
      getSession().flush();
      primaryKey = testEntity.getId();

      otherEntity = getSession().findOne(TtlTestEntity.class, primaryKey);
      Assert.assertNotNull("Expect to be not null", otherEntity);
    }
    finally {
      executeCQL("DROP TABLE default.TtlTestEntity");
    }
  }

  @Test(expected = AtreusDataBindingException.class)
  public void testTimeToLiveEntityBadValue() throws Exception {
    LOG.info("Running testTimeToLiveEntityBadValue");
    addEntity(TtlTestEntity.class);
    initEnvironment();

    try {
      executeCQL("CREATE TABLE default.TtlTestEntity (" +
          "id text, " +
          "value text, " +
          "PRIMARY KEY(id))");


      // Test with a time-to-live bindValue
      TtlTestEntity testEntity = new TtlTestEntity();
      testEntity.setValue("I am a text value");
      testEntity.setTtl(0);

      getSession().save(testEntity);
    }
    finally {
      executeCQL("DROP TABLE default.TtlTestEntity");
    }
  }

  @Test
  public void testCollectionsEntity() throws Exception {
    LOG.info("Running testCollectionsEntity");
    addEntity(CollectionTestEntity.class);
    initEnvironment();

    executeCQL("CREATE TABLE default.CollectionTestEntity (" +
        "id text, " +
        "setField set<bigint>, " +
        "listField list<text>, " +
        "mapField1 map<text,bigint>, " +
        "mapField2 map<text,bigint>, " +
        "PRIMARY KEY(id))");

    CollectionTestEntity testEntity = new CollectionTestEntity();

    Set<Long> setValue = new HashSet<>();
    testEntity.setSetField(setValue);
    setValue.add((long) 123456);
    setValue.add((long) 654321);

    List<String> listValue = new ArrayList<>();
    testEntity.setListField(listValue);
    listValue.add("value1");
    listValue.add("value2");

    Map<String, Long> mapValue = new HashMap<>();
    testEntity.setMapField1(mapValue);
    testEntity.setMapField2(mapValue);
    mapValue.put("value1", (long) 123456);
    mapValue.put("value2", (long) 654321);

    getSession().save(testEntity);
    getSession().flush();
    String primaryKey = testEntity.getId();

    CollectionTestEntity otherEntity = getSession().findOne(CollectionTestEntity.class, primaryKey);

    Assert.assertNotNull("Expect to be not null", otherEntity);
    Assert.assertThat(otherEntity.getListField(), JUnitMatchers.hasItems("value1", "value2"));
    Assert.assertThat(otherEntity.getSetField(), JUnitMatchers.hasItems((long) 123456, (long) 654321));
    Assert.assertNotNull("Expect value for 'value1'", otherEntity.getMapField1().get("value1"));
    Assert.assertNotNull("Expect value for 'value2'", otherEntity.getMapField1().get("value2"));
    Assert.assertNotNull("Expect value for 'value1'", otherEntity.getMapField2().get("value1"));
    Assert.assertNotNull("Expect value for 'value2'", otherEntity.getMapField2().get("value2"));

    executeCQL("DROP TABLE default.CollectionTestEntity");
  }

  @Test
  public void testSimpleCompositeEntityAssociation() throws Exception {
    LOG.info("Running testSimpleCompositeEntityAssociation");
    addEntity(ParentCompositeTestEntity.class);
    addEntity(ChildCompositeTestEntity.class);
    initEnvironment();

    AtreusMetaEntity parentMetaEntity = getEnvironment().getEntityManager().getMetaEntity(ParentCompositeTestEntity.class);
    AtreusMetaEntity childMetaEntity = getEnvironment().getEntityManager().getMetaEntity(ChildCompositeTestEntity.class);
    LOG.info("parentMetaEntity: {}", QueryHelper.selectEntity(parentMetaEntity));
    LOG.info("childMetaEntity: {}", QueryHelper.selectEntity(childMetaEntity));
  }

  @Test
  public void testNameOverrideEntity() throws Exception {
    LOG.info("Running testNameOverrideEntity");
    addEntity(NameOverrideTestEntity.class);
    initEnvironment();

    AtreusMetaEntity metaEntity = getEnvironment().getEntityManager().getMetaEntity(NameOverrideTestEntity.class);
    Assert.assertNotNull("Expect an entity", metaEntity);
    Assert.assertEquals("EntityName", metaEntity.getName());
    Assert.assertEquals("KeySpaceName", metaEntity.getKeySpace());
    Assert.assertEquals("TableName", metaEntity.getTable());

    Assert.assertEquals("primaryKey", ((AtreusMetaSimpleField) metaEntity.getPrimaryKeyField()).getColumn());
    Assert.assertEquals(StringPrimaryKeyStrategy.class, metaEntity.getPrimaryKeyStrategy().getClass());

    Assert.assertEquals("columnName", ((AtreusMetaSimpleField) metaEntity.getFieldByName("field1")).getColumn());

  }

  @Test(expected = AtreusInitialisationException.class)
  public void testCollectionValueNotResolvable() {
    LOG.info("Running testCollectionValueNotResolvable");
    try {
      addEntity(CollectionValueNotResolvableTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_COLLECTION_VALUE_TYPE_NOT_RESOLVABLE, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMapValueNotResolvable() {
    LOG.info("Running testMapValueNotResolvable");
    try {
      addEntity(MapValueNotResolvableTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_COLLECTION_VALUE_TYPE_NOT_RESOLVABLE, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMapKeyNotResolvable() {
    LOG.info("Running testMapKeyNotResolvable");
    try {
      addEntity(MapKeyNotResolvableTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_MAP_KEY_TYPE_NOT_RESOLVABLE, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testInvalidCollectionStrategy() {
    LOG.info("Running testInvalidCollectionStrategy");
    try {
      addEntity(InvalidCollectionStrategyTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_COLLECTION_TYPE_STRATEGY_INVALID, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testInvalidMapStrategy() {
    LOG.info("Running testInvalidMapStrategy");
    try {
      addEntity(InvalidMapStrategyTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_MAP_TYPE_STRATEGY_INVALID, e.getErrorCode());
      throw e;
    }
  }

  @Test(expected = AtreusInitialisationException.class)
  public void testMultiplePrimaryKeys() {
    LOG.info("Running testMultiplePrimaryKeys");
    try {
      addEntity(MultiplePrimaryKeyTestEntity.class);
      initEnvironment();
    }
    catch (AtreusInitialisationException e) {
      Assert.assertEquals(AtreusInitialisationException.ERROR_CODE_PRIMARY_KEY_MULTIPLE, e.getErrorCode());
      throw e;
    }
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class