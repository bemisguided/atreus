/*
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

package org.atreus.support.spring;

import org.atreus.AtreusConfiguration;
import org.atreus.AtreusSession;
import org.atreus.AtreusSessionFactory;
import org.atreus.AtreusSessionFactoryBuilder;
import org.atreus.impl.utils.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public final class SpringAtreusSessionFactory implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(SpringAtreusSessionFactory.class);

	private AtreusConfiguration config;

	private AtreusSessionFactory sessionFactory;

	public SpringAtreusSessionFactory() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		AssertUtils.notNull(config, "Atreus Configuration property is not set");
		sessionFactory = AtreusSessionFactoryBuilder.buildFactory(config);
	}

	private void attachSessionToTransaction(AtreusSession session) {
		if (logger.isDebugEnabled()) {
			logger.debug("Attaching session to transaction - Thread [" + Thread.currentThread().getId() + "]");
		}
		AtreusTransactionSynchronization transactionSynchronization = new AtreusTransactionSynchronization(session, sessionFactory);
		transactionSynchronization.bindSession();
	}

	private AtreusSession createSession() {
		AtreusSession session = sessionFactory.openSession();
		attachSessionToTransaction(session);
		return session;
	}

	@Override
	public void destroy() throws Exception {
		sessionFactory.disconnect();
	}

	public AtreusConfiguration getConfiguration() {
		return config;
	}

	public AtreusSession getCurrentSession() {
		if (logger.isDebugEnabled()) {
			logger.debug("Call getSession() - Thread [" + Thread.currentThread().getId() + "]");
		}
		if (TransactionSynchronizationManager.hasResource(this)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Session exists for the current thread/transaction context - Thread [" + Thread.currentThread().getId() + "]");
			}
			return (AtreusSession) TransactionSynchronizationManager.getResource(this);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Session does not exist for the current thread/transaction context - Thread [" + Thread.currentThread().getId() + "]");
		}
		return createSession();
	}

	public void setConfiguration(AtreusConfiguration config) {
		this.config = config;
	}
}
