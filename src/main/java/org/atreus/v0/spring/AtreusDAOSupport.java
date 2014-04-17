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
package org.atreus.v0.spring;

import org.atreus.v0.AtreusColumnMap;
import org.atreus.v0.AtreusRowList;
import org.atreus.v0.AtreusSession;
import org.springframework.dao.support.DaoSupport;

public class AtreusDAOSupport extends DaoSupport {

	private AtreusSpringSessionFactory sessionFactory;

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		// TODO Auto-generated method stub
	}

	protected void deleteColumn(Object colName) {
		getCurrentSession().deleteColumn(colName);
	}

	protected void deleteColumn(Object colName, Object subColName) {
		getCurrentSession().deleteColumn(colName, subColName);
	}

	protected void execute(String cql) {
		getCurrentSession().execute(cql);
	}

	protected boolean existsColumn(Object colName) {
		return getCurrentSession().existsColumn(colName);
	}

	protected boolean existsColumn(Object colName, Object subColName) {
		return getCurrentSession().existsColumn(colName, subColName);
	}

	protected boolean existsRow() {
		return getCurrentSession().existsRow();
	}

	protected boolean existsRow(Object rowKey) {
		return getCurrentSession().existsRow(rowKey);
	}

	protected void flush() {
		getCurrentSession().flush();
	}

	protected String getColumnFamily() {
		return getCurrentSession().getColumnFamily();
	}

	protected AtreusSession getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	protected Object getRowKey() {
		return getCurrentSession().getRowKey();
	}

	public AtreusSpringSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected AtreusColumnMap newColumnMap() {
		return getCurrentSession().newColumnMap();
	}

	protected AtreusColumnMap newColumnMap(boolean superColumns) {
		return getCurrentSession().newColumnMap(superColumns);
	}

	protected AtreusRowList query(String cql) {
		return getCurrentSession().query(cql);
	}

	protected <T> T readColumn(Object colName, Class<T> type) {
		return getCurrentSession().readColumn(colName, type);
	}

	protected <T> T readColumn(Object colName, Object subColName, Class<T> type) {
		return getCurrentSession().readColumn(colName, subColName, type);
	}

	protected byte[] readColumnAsBytes(Object colName) {
		return getCurrentSession().readColumnAsBytes(colName);
	}

	protected byte[] readColumnAsBytes(Object colName, Object subColName) {
		return getCurrentSession().readColumnAsBytes(colName, subColName);
	}

	protected AtreusColumnMap readColumns() {
		return getCurrentSession().readColumns();
	}

	protected AtreusColumnMap readColumns(String colFamily, Object rowKey) {
		return getCurrentSession().readColumns(colFamily, rowKey);
	}

	protected void setColumnFamily(String colFamily) {
		getCurrentSession().setColumnFamily(colFamily);
	}

	protected void setFamilyAndRow(String colFamily, Object rowKey) {
		getCurrentSession().setFamilyAndKey(colFamily, rowKey);
	}

	protected void setRowKey(Object rowKey) {
		getCurrentSession().setRowKey(rowKey);
	}

	public void setSessionFactory(AtreusSpringSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected void writeColumn(Object colName) {
		getCurrentSession().writeColumn(colName);
	}

	protected void writeColumn(Object colName, byte[] value) {
		getCurrentSession().writeColumn(colName, value);
	}

	protected void writeColumn(Object colName, Object value) {
		getCurrentSession().writeColumn(colName, value);
	}

	protected void writeSubColumn(Object colName, Object subColName) {
		getCurrentSession().writeSubColumn(colName, subColName);
	}

	protected void writeSubColumn(Object colName, Object subColName, byte[] value) {
		getCurrentSession().writeSubColumn(colName, subColName, value);
	}

	protected void writeSubColumn(Object colName, Object subColName, Object value) {
		getCurrentSession().writeSubColumn(colName, subColName, value);
	}

}
