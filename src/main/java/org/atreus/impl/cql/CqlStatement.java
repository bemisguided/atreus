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

import java.nio.ByteBuffer;

import org.atreus.AtreusConsistencyLevel;
import org.atreus.impl.utils.ByteUtils;

public class CqlStatement {

	private String[] tokens;

	private AtreusConsistencyLevel consistencyLevel;

	private int consistencyClausePosition = -1;

	private boolean existsUsingClause;

	private boolean existsUsingConsistencyClause;

	private boolean existsUsingClauseOther;

	private CqlStatementType type = CqlStatementType.OTHER;

	public CqlStatement(String[] tokens) {
		this.tokens = tokens;
	}

	public int getConsistencyClausePosition() {
		return consistencyClausePosition;
	}

	public AtreusConsistencyLevel getConsistencyLevel() {
		return consistencyLevel;
	}

	public CqlStatementType getType() {
		return type;
	}

	public boolean isExistsUsingClause() {
		return existsUsingClause;
	}

	public boolean isExistsUsingClauseOther() {
		return existsUsingClauseOther;
	}

	public boolean isExistsUsingConsistencyClause() {
		return existsUsingConsistencyClause;
	}

	public void setConsistencyClausePosition(int consistencyLevelPos) {
		this.consistencyClausePosition = consistencyLevelPos;
	}

	public void setConsistencyLevel(AtreusConsistencyLevel consistencyLevel) {
		this.consistencyLevel = consistencyLevel;
	}

	public void setExistsUsingClause(boolean existsUsingClause) {
		this.existsUsingClause = existsUsingClause;
	}

	public void setExistsUsingClauseOther(boolean hasOtherUsingClauses) {
		this.existsUsingClauseOther = hasOtherUsingClauses;
	}

	public void setExistsUsingConsistencyClause(boolean hasExistingConsistencyLevel) {
		this.existsUsingConsistencyClause = hasExistingConsistencyLevel;
	}

	public void setType(CqlStatementType type) {
		this.type = type;
	}

	public ByteBuffer toByteBuffer() {
		return ByteBuffer.wrap(toBytes());
	}

	public byte[] toBytes() {
		return ByteUtils.toBytes(toString());
	}

	public String toString() {
		if (CqlStatementType.OTHER.equals(type)) {
			StringBuilder builder = new StringBuilder(tokens.length * 2);
			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i];
				if (i > 0) {
					builder.append(' ');
				}
				builder.append(token);
			}
			return builder.toString();
		}
		StringBuilder builder = new StringBuilder(tokens.length * 2 + 6);
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if (i > 0) {
				builder.append(' ');
			}
			if (i == consistencyClausePosition) {
				if (existsUsingConsistencyClause) {
					builder.append(consistencyLevel.toString());
					continue;
				}
				if (existsUsingClauseOther) {
					builder.append("CONSISTENCY ");
					builder.append(consistencyLevel.toString());
					builder.append(" AND ");
				} else {
					builder.append("USING CONSISTENCY ");
					builder.append(consistencyLevel.toString());
					builder.append(' ');
				}
			}
			builder.append(token);
		}
		if (!existsUsingConsistencyClause && consistencyClausePosition == tokens.length) {
			builder.append(" USING CONSISTENCY ");
			builder.append(consistencyLevel.toString());
		}
		builder.append(';');
		return builder.toString();
	}
}
