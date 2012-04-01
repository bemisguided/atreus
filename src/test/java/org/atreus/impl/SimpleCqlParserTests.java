/**
 * The MIT License
 *
 * Copyright (c) 2012 Martin Crawford and contributors.
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
package org.atreus.impl;

import org.atreus.AtreusConsistencyLevel;
import org.atreus.impl.cql.CqlStatement;
import org.atreus.impl.cql.CqlStatementType;
import org.atreus.impl.cql.CqlSimpleParser;
import org.junit.Assert;
import org.junit.Test;

public class SimpleCqlParserTests {

	@Test
	public void batchConsistencyLevelTests() throws Exception {
		CqlSimpleParser parser = new CqlSimpleParser();

		CqlStatement statement;

		statement = parser.parse("BEGIN BATCH USING CONSISTENCY QUORUM AND TTL 8640000 INSERT INTO users "
				+ "(KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH; ");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("BEGIN BATCH USING CONSISTENCY ONE AND TTL 8640000 INSERT INTO users "
				+ "(KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH;", statement.toString());

		statement = parser.parse("BEGIN BATCH USING TTL 8640000 AND CONSISTENCY QUORUM AND TIMEOUT=123456 INSERT INTO users "
				+ "(KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH; ");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("BEGIN BATCH USING TTL 8640000 AND CONSISTENCY ONE AND TIMEOUT=123456 INSERT INTO users "
				+ "(KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH;", statement.toString());

		statement = parser.parse("BEGIN BATCH USING CONSISTENCY QUORUM AND TTL 8640000 AND TIMEOUT=123456 INSERT INTO users "
				+ "(KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH; ");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("BEGIN BATCH USING CONSISTENCY ONE AND TTL 8640000 AND TIMEOUT=123456 INSERT INTO users "
				+ "(KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH;", statement.toString());

		statement = parser.parse("BEGIN BATCH INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH; ");
		Assert.assertNull("No consistency level should be specified", statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("BEGIN BATCH USING CONSISTENCY ONE INSERT INTO users " + "(KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH;",
				statement.toString());

	}

	@Test
	public void deleteConsistencyLevelTests() throws Exception {
		CqlSimpleParser parser = new CqlSimpleParser();

		CqlStatement statement;

		statement = parser.parse("DELETE col1, col2 FROM users WHERE KEY = 'user1';");
		Assert.assertNull("No consistency level should be specified", statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("DELETE col1, col2 FROM users USING CONSISTENCY ONE WHERE KEY = 'user1';", statement.toString());

		statement = parser.parse("DELETE col1, col2 FROM users USING CONSISTENCY QUORUM WHERE KEY = 'user1';");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("DELETE col1, col2 FROM users USING CONSISTENCY ONE WHERE KEY = 'user1';", statement.toString());

		statement = parser.parse("DELETE col1, col2 FROM users USING TIMESTAMP=1234 AND CONSISTENCY QUORUM WHERE KEY = 'user1';");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("DELETE col1, col2 FROM users USING TIMESTAMP=1234 AND CONSISTENCY ONE WHERE KEY = 'user1';", statement.toString());

		statement = parser.parse("DELETE col1, col2 FROM users USING CONSISTENCY QUORUM AND TIMESTAMP=1234 WHERE KEY = 'user1';");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("DELETE col1, col2 FROM users USING CONSISTENCY ONE AND TIMESTAMP=1234 WHERE KEY = 'user1';", statement.toString());

		statement = parser.parse("DELETE col1, col2 FROM users USING CONSISTENCY QUORUM AND TIMESTAMP=1234 WHERE KEY = 'user1';");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("DELETE col1, col2 FROM users USING CONSISTENCY ONE AND TIMESTAMP=1234 WHERE KEY = 'user1';", statement.toString());

	}

	@Test
	public void insertConsistencyLevelTests() throws Exception {
		CqlSimpleParser parser = new CqlSimpleParser();

		CqlStatement statement;

		statement = parser.parse("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user');");
		Assert.assertNull("No consistency level should be specified", statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING CONSISTENCY ONE;", statement.toString());

		statement = parser.parse("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING CONSISTENCY QUORUM;");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING CONSISTENCY ONE;", statement.toString());

		statement = parser.parse("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING CONSISTENCY QUORUM AND TIMESTAMP=123456;");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING CONSISTENCY ONE AND TIMESTAMP=123456;",
				statement.toString());

		statement = parser.parse("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING TIMESTAMP=123456 AND CONSISTENCY QUORUM;");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING TIMESTAMP=123456 AND CONSISTENCY ONE;",
				statement.toString());

		statement = parser.parse("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING TTL 1234567 AND CONSISTENCY QUORUM;");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING TTL 1234567 AND CONSISTENCY ONE;", statement.toString());

		statement = parser.parse("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING TIMESTAMP=123456;");
		Assert.assertNull("No consistency level should be specified", statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') USING CONSISTENCY ONE AND TIMESTAMP=123456;",
				statement.toString());
	}

	@Test
	public void selectConsistencyLevelTests() throws Exception {
		CqlSimpleParser parser = new CqlSimpleParser();

		CqlStatement statement;

		statement = parser.parse("SELECT * FROM ColFamily1 USING CONSISTENCY QUORUM;");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("SELECT * FROM ColFamily1 USING CONSISTENCY ONE;", statement.toString());

		statement = parser.parse("SELECT * FROM ColFamily1 USING CONSISTENCY QUORUM WHERE col1 = 'value1';");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("SELECT * FROM ColFamily1 USING CONSISTENCY ONE WHERE col1 = 'value1';", statement.toString());

		statement = parser.parse("SELECT * FROM ColFamily1 USING CONSISTENCY QUORUM LIMIT 100;");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("SELECT * FROM ColFamily1 USING CONSISTENCY ONE LIMIT 100;", statement.toString());

		statement = parser.parse("SELECT * FROM ColFamily1 WHERE col1 = 'value1';");
		Assert.assertNull("No consistency level should be specified", statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("SELECT * FROM ColFamily1 USING CONSISTENCY ONE WHERE col1 = 'value1';", statement.toString());

		statement = parser.parse("SELECT * FROM ColFamily1 LIMIT 100;");
		Assert.assertNull("No consistency level should be specified", statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("SELECT * FROM ColFamily1 USING CONSISTENCY ONE LIMIT 100;", statement.toString());

		statement = parser.parse("SELECT * FROM ColFamily1;");
		Assert.assertNull("No consistency level should be specified", statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("SELECT * FROM ColFamily1 USING CONSISTENCY ONE;", statement.toString());

	}

	@Test
	public void statementIdentificationTests() throws Exception {
		CqlSimpleParser parser = new CqlSimpleParser();

		CqlStatement statement;

		statement = parser.parse("select * From ColFamily1 using consistency quorum;");
		Assert.assertEquals(CqlStatementType.SELECT, statement.getType());

		statement = parser.parse("    select * From ColFamily1 using consistency quorum;");
		Assert.assertEquals(CqlStatementType.SELECT, statement.getType());

		statement = parser.parse("DELETE email, phone\nFROM users\nUSING CONSISTENCY QUORUM AND TIMESTAMP 1318452291034 WHERE user_name = 'jsmith'; ");
		Assert.assertEquals(CqlStatementType.DELETE, statement.getType());

		statement = parser.parse(" UPdaTE users SET password = 'ps22dhds' WHERE KEY = 'user2'; ");
		Assert.assertEquals(CqlStatementType.UPDATE, statement.getType());

		statement = parser.parse("  INSERT INTO users (KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user'); ");
		Assert.assertEquals(CqlStatementType.INSERT, statement.getType());

		statement = parser.parse("BEGIN BATCH USING CONSISTENCY QUORUM AND TTL 8640000  INSERT INTO users "
				+ "(KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH; ");
		Assert.assertEquals(CqlStatementType.BATCH, statement.getType());

		// Invalid batch statement
		statement = parser.parse(" BATCH USING CONSISTENCY QUORUM AND TTL 8640000  INSERT INTO users "
				+ "(KEY, password, name) VALUES ('user2', 'ch@ngem3b', 'second user') APPLY BATCH; ");
		Assert.assertEquals(CqlStatementType.OTHER, statement.getType());
	}

	@Test
	public void updateConsistencyLevelTests() throws Exception {
		CqlSimpleParser parser = new CqlSimpleParser();

		CqlStatement statement;

		statement = parser.parse("UPDATE users SET password = 'ps22dhds' WHERE KEY = 'user2';");
		Assert.assertNull("No consistency level should be specified", statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("UPDATE users USING CONSISTENCY ONE SET password = 'ps22dhds' WHERE KEY = 'user2';", statement.toString());

		statement = parser.parse("UPDATE users USING CONSISTENCY QUORUM SET password = 'ps22dhds' WHERE KEY = 'user2';");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("UPDATE users USING CONSISTENCY ONE SET password = 'ps22dhds' WHERE KEY = 'user2';", statement.toString());

		statement = parser.parse("UPDATE users USING CONSISTENCY QUORUM AND TIMESTAMP=123456 SET password = 'ps22dhds' WHERE KEY = 'user2';");
		Assert.assertEquals(AtreusConsistencyLevel.QUORUM, statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("UPDATE users USING CONSISTENCY ONE AND TIMESTAMP=123456 SET password = 'ps22dhds' WHERE KEY = 'user2';", statement.toString());

		statement = parser.parse("UPDATE users USING TIMESTAMP=123456 SET password = 'ps22dhds' WHERE KEY = 'user2';");
		Assert.assertNull("No consistency level should be specified", statement.getConsistencyLevel());
		statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
		Assert.assertEquals("UPDATE users USING CONSISTENCY ONE AND TIMESTAMP=123456 SET password = 'ps22dhds' WHERE KEY = 'user2';", statement.toString());

	}
}
