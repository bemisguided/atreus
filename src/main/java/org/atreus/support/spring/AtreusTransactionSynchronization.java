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
package org.atreus.support.spring;

import org.atreus.AtreusSession;
import org.atreus.AtreusSessionFactory;
import org.atreus.impl.utils.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

final class AtreusTransactionSynchronization extends TransactionSynchronizationAdapter {

	private static final Logger logger = LoggerFactory.getLogger(AtreusTransactionSynchronization.class);

	private AtreusSession session;

	private AtreusSessionFactory sessionFactory;

	public AtreusTransactionSynchronization(AtreusSession session, AtreusSessionFactory sessionFactory) {
		AssertUtils.notNull(session, "Atreus Session object cannot be null");
		AssertUtils.notNull(sessionFactory, "Atreus Session Factory cannot be null");
		this.session = session;
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void afterCompletion(int status) {
		if (logger.isDebugEnabled()) {
			logger.debug("Called afterCompletion(" + status + ") - Thread [" + Thread.currentThread().getId() + "]");
		}

		session.close();

		if (logger.isDebugEnabled()) {
			logger.debug("After AtreusSession.close() - Thread [" + Thread.currentThread().getId() + "]");
		}

		TransactionSynchronizationManager.unbindResource(sessionFactory);
		if (logger.isDebugEnabled()) {
			logger.debug("After TransactionSynchronizationManager.unbindResource() - Thread [" + Thread.currentThread().getId() + "]");
		}
	}

	@Override
	public void beforeCommit(boolean readOnly) {
		flush();
	}

	public void bindSession() {
		if (logger.isDebugEnabled()) {
			logger.debug("Called bindSession() - Thread [" + Thread.currentThread().getId() + "]");
		}
		TransactionSynchronizationManager.registerSynchronization(this);
		if (logger.isDebugEnabled()) {
			logger.debug("After TransactionSynchronizationManager.registerSynchronization() - Thread [" + Thread.currentThread().getId() + "]");
		}
		TransactionSynchronizationManager.bindResource(sessionFactory, session);
		if (logger.isDebugEnabled()) {
			logger.debug("After TransactionSynchronizationManager.bindResource() - Thread [" + Thread.currentThread().getId() + "]");
		}
	}

	@Override
	public void flush() {
		if (logger.isDebugEnabled()) {
			logger.debug("Called flush() - Thread [" + Thread.currentThread().getId() + "]");
		}

		if (session.isBatchWriting()) {

			if (logger.isDebugEnabled()) {
				logger.debug("Session is set to batch write - Thread [" + Thread.currentThread().getId() + "]");
				logger.debug("Before AtreusSession.flush() - Thread [" + Thread.currentThread().getId() + "]");
			}

			session.flush();

			if (logger.isDebugEnabled()) {
				logger.debug("After AtreusSession.flush() - Thread [" + Thread.currentThread().getId() + "]");
			}
		}

	}

}