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
package org.atreus.impl.cql;

import org.atreus.AtreusConsistencyLevel;

public class CqlSimpleParser {

	private static final String CL_ALL = AtreusConsistencyLevel.ALL.toString();

	private static final String CL_ANY = AtreusConsistencyLevel.ANY.toString();

	private static final String CL_EACH_QUORUM = AtreusConsistencyLevel.EACH_QUORUM.toString();

	private static final String CL_LOCAL_QUORUM = AtreusConsistencyLevel.LOCAL_QUORUM.toString();

	private static final String CL_ONE = AtreusConsistencyLevel.ONE.toString();

	private static final String CL_QUORUM = AtreusConsistencyLevel.QUORUM.toString();

	private static final String CL_THREE = AtreusConsistencyLevel.THREE.toString();

	private static final String CL_TWO = AtreusConsistencyLevel.TWO.toString();

	private static final String KEYWORD_BATCH = "BATCH";

	private static final String KEYWORD_BEGIN = "BEGIN";

	private static final String KEYWORD_CONSISTENCY = "CONSISTENCY";

	private static final String KEYWORD_DELETE = "DELETE";

	private static final String KEYWORD_INSERT = "INSERT";

	private static final String KEYWORD_LIMIT = "LIMIT";

	private static final String KEYWORD_SELECT = "SELECT";

	private static final String KEYWORD_SET = "SET";

	private static final String KEYWORD_TIMESTAMP = "TIMESTAMP";

	private static final String KEYWORD_TTL = "TTL";

	private static final String KEYWORD_UPDATE = "UPDATE";

	private static final String KEYWORD_USING = "USING";

	private static final String KEYWORD_WHERE = "WHERE";

	private void identifyType(String token, String lastToken, CqlStatement statement) {
		if (isSelectStatement(token)) {
			statement.setType(CqlStatementType.SELECT);
			return;
		}
		if (isUpdateStatement(token)) {
			statement.setType(CqlStatementType.UPDATE);
			return;
		}
		if (isInsertStatement(token)) {
			statement.setType(CqlStatementType.INSERT);
			return;
		}
		if (isDeleteStatement(token)) {
			statement.setType(CqlStatementType.DELETE);
			return;
		}
		if (isBatchStatement(token) && isBeginStatement(lastToken)) {
			statement.setType(CqlStatementType.BATCH);
			return;
		}
	}

	private boolean isBatchStatement(String token) {
		return KEYWORD_BATCH.equalsIgnoreCase(token);
	}

	private boolean isBeginStatement(String token) {
		return KEYWORD_BEGIN.equalsIgnoreCase(token);
	}

	private boolean isConsistencyClause(String token) {
		return KEYWORD_CONSISTENCY.equalsIgnoreCase(token);
	}

	private boolean isDeleteStatement(String token) {
		return KEYWORD_DELETE.equalsIgnoreCase(token);
	}

	private boolean isInsertStatement(String token) {
		return KEYWORD_INSERT.equalsIgnoreCase(token);
	}

	private boolean isLimitClause(String token) {
		return KEYWORD_LIMIT.equalsIgnoreCase(token);
	}

	private boolean isSelectStatement(String token) {
		return KEYWORD_SELECT.equalsIgnoreCase(token);
	}

	private boolean isSetClause(String token) {
		return KEYWORD_SET.equalsIgnoreCase(token);
	}

	private boolean isTimestameParam(String token) {
		return token.toUpperCase().startsWith(KEYWORD_TIMESTAMP);
	}

	private boolean isTtlParam(String token) {
		return KEYWORD_TTL.equalsIgnoreCase(token);
	}

	private boolean isUpdateStatement(String token) {
		return KEYWORD_UPDATE.equalsIgnoreCase(token);
	}

	private boolean isUsingClause(String token) {
		return KEYWORD_USING.equalsIgnoreCase(token);
	}

	private boolean isWhereClause(String token) {
		return KEYWORD_WHERE.equalsIgnoreCase(token);
	}

	public CqlStatement parse(String cqlStatement) {
		String[] tokens = cqlStatement.split("[\\s|;]+");

		CqlStatement statement = new CqlStatement(tokens);
		String lastToken = null;
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];

			if (CqlStatementType.OTHER.equals(statement.getType())) {
				if (i < 3) {
					identifyType(token, lastToken, statement);
				} else {
					break;
				}
			} else {
				switch (statement.getType()) {
				case SELECT:
					parseSelect(i, tokens, statement);
					break;

				case INSERT:
					parseInsert(i, tokens, statement);
					break;

				case UPDATE:
					parseUpdate(i, tokens, statement);
					break;

				case DELETE:
					parseDelete(i, tokens, statement);
					break;

				case BATCH:
					parseBatch(i, tokens, statement);
					break;
				}
				break;
			}
			lastToken = token;
		}
		return statement;
	}

	private void parseBatch(int pos, String[] tokens, CqlStatement statement) {
		for (int i = pos; i < tokens.length; i++) {
			String token = tokens[i];

			// Start USING clause
			if (isUsingClause(token)) {
				statement.setExistsUsingClause(true);
				statement.setConsistencyClausePosition(i + 1);
			}

			// Inside USING clause
			if (statement.isExistsUsingClause()) {
				if (isConsistencyClause(token)) {
					statement.setExistsUsingConsistencyClause(true);
					statement.setConsistencyClausePosition(i + 1);
				}
				if (isConsistencyClause(tokens[i - 1])) {
					parseConsistencyLevel(token, statement);
				}
				if (isTimestameParam(token) || isTtlParam(token)) {
					statement.setExistsUsingClauseOther(true);
				}
			}

			// End USING clause
			if (statement.isExistsUsingClause()) {
				if (isInsertStatement(token) || isUpdateStatement(token) || isDeleteStatement(token)) {
					break;
				}
			}

			// Case where there is no USING clause
			if (isInsertStatement(token) || isUpdateStatement(token) || isDeleteStatement(token)) {
				statement.setConsistencyClausePosition(i);
			}
		}
	}

	private void parseConsistencyLevel(String token, CqlStatement statement) {
		if (CL_ALL.equalsIgnoreCase(token)) {
			statement.setConsistencyLevel(AtreusConsistencyLevel.ALL);
			return;
		}
		if (CL_ANY.equalsIgnoreCase(token)) {
			statement.setConsistencyLevel(AtreusConsistencyLevel.ANY);
			return;
		}
		if (CL_EACH_QUORUM.equalsIgnoreCase(token)) {
			statement.setConsistencyLevel(AtreusConsistencyLevel.EACH_QUORUM);
			return;
		}
		if (CL_LOCAL_QUORUM.equalsIgnoreCase(token)) {
			statement.setConsistencyLevel(AtreusConsistencyLevel.LOCAL_QUORUM);
			return;
		}
		if (CL_ONE.equalsIgnoreCase(token)) {
			statement.setConsistencyLevel(AtreusConsistencyLevel.ONE);
			return;
		}
		if (CL_QUORUM.equalsIgnoreCase(token)) {
			statement.setConsistencyLevel(AtreusConsistencyLevel.QUORUM);
			return;
		}
		if (CL_THREE.equalsIgnoreCase(token)) {
			statement.setConsistencyLevel(AtreusConsistencyLevel.THREE);
			return;
		}
		if (CL_TWO.equalsIgnoreCase(token)) {
			statement.setConsistencyLevel(AtreusConsistencyLevel.TWO);
			return;
		}
	}

	private void parseDelete(int pos, String[] tokens, CqlStatement statement) {
		for (int i = pos; i < tokens.length; i++) {
			String token = tokens[i];

			// Start USING clause
			if (isUsingClause(token)) {
				statement.setExistsUsingClause(true);
				statement.setConsistencyClausePosition(i + 1);
			}

			// Inside USING clause
			if (statement.isExistsUsingClause()) {
				if (isConsistencyClause(token)) {
					statement.setExistsUsingConsistencyClause(true);
					statement.setConsistencyClausePosition(i + 1);
				}
				if (isConsistencyClause(tokens[i - 1])) {
					parseConsistencyLevel(token, statement);
				}
				if (isTimestameParam(token)) {
					statement.setExistsUsingClauseOther(true);
				}
			}

			// End USING clause
			if (statement.isExistsUsingClause()) {
				if (isWhereClause(token)) {
					break;
				}
			}

			// Case where there is no USING clause
			if (isWhereClause(token)) {
				statement.setConsistencyClausePosition(i);
			}
		}
	}

	private void parseInsert(int pos, String[] tokens, CqlStatement statement) {
		for (int i = pos; i < tokens.length; i++) {
			String token = tokens[i];

			// Start USING clause
			if (isUsingClause(token)) {
				statement.setExistsUsingClause(true);
				statement.setConsistencyClausePosition(i + 1);
			}

			// Inside USING clause
			if (statement.isExistsUsingClause()) {
				if (isConsistencyClause(token)) {
					statement.setExistsUsingConsistencyClause(true);
					statement.setConsistencyClausePosition(i + 1);
				}
				if (isConsistencyClause(tokens[i - 1])) {
					parseConsistencyLevel(token, statement);
				}
				if (isTimestameParam(token) || isTtlParam(token)) {
					statement.setExistsUsingClauseOther(true);
				}
			}

		}

		// Case where there is no WHERE or LIMIT clause
		if (statement.getConsistencyClausePosition() < 0) {
			statement.setConsistencyClausePosition(tokens.length);
		}

	}

	private void parseSelect(int pos, String[] tokens, CqlStatement statement) {
		for (int i = pos; i < tokens.length; i++) {
			String token = tokens[i];

			// Start USING clause
			if (isUsingClause(token)) {
				statement.setExistsUsingClause(true);
				statement.setConsistencyClausePosition(i + 1);
			}

			// Inside USING clause
			if (statement.isExistsUsingClause()) {
				if (isConsistencyClause(token)) {
					statement.setExistsUsingConsistencyClause(true);
					statement.setConsistencyClausePosition(i + 1);
				}
				if (isConsistencyClause(tokens[i - 1])) {
					parseConsistencyLevel(token, statement);
				}
			}

			// End USING clause
			if (statement.isExistsUsingClause()) {
				if (isWhereClause(token) || isLimitClause(token)) {
					break;
				}
			}

			// Case where there is no USING clause
			if (isWhereClause(token) || isLimitClause(token)) {
				statement.setConsistencyClausePosition(i);
			}
		}

		// Case where there is no WHERE or LIMIT clause
		if (statement.getConsistencyClausePosition() < 0) {
			statement.setConsistencyClausePosition(tokens.length);
		}

	}

	private void parseUpdate(int pos, String[] tokens, CqlStatement statement) {
		for (int i = pos; i < tokens.length; i++) {
			String token = tokens[i];

			// Start USING clause
			if (isUsingClause(token)) {
				statement.setExistsUsingClause(true);
				statement.setConsistencyClausePosition(i + 1);
			}

			// Inside USING clause
			if (statement.isExistsUsingClause()) {
				if (isConsistencyClause(token)) {
					statement.setExistsUsingConsistencyClause(true);
					statement.setConsistencyClausePosition(i + 1);
				}
				if (isConsistencyClause(tokens[i - 1])) {
					parseConsistencyLevel(token, statement);
				}
				if (isTimestameParam(token) || isTtlParam(token)) {
					statement.setExistsUsingClauseOther(true);
				}
			}

			// End USING clause
			if (statement.isExistsUsingClause()) {
				if (isSetClause(token)) {
					break;
				}
			}

			// Case where there is no USING clause
			if (isSetClause(token)) {
				statement.setConsistencyClausePosition(i);
			}
		}
	}

}
