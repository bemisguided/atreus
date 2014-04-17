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

import org.atreus.v0.AtreusSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public final class AtreusSpringTransactionSessionFactory extends AtreusSpringSessionFactoryBase {

	private static final Logger logger = LoggerFactory.getLogger(AtreusSpringTransactionSessionFactory.class);

	public AtreusSpringTransactionSessionFactory() {
	}

	private void attachSessionToTransaction(AtreusSession session) {
		if (logger.isDebugEnabled()) {
			logger.debug("Attaching session to transaction - Thread [" + Thread.currentThread().getId() + "]");
		}
		AtreusTransactionSynchronization transactionSynchronization = new AtreusTransactionSynchronization(session, getSessionFactory());
		transactionSynchronization.bindSession();
	}

	private AtreusSpringSession createSession() {
		AtreusSession session = getSessionFactory().openSession();
		AtreusSpringSession springSession = new AtreusSpringSessionImpl(session);
		attachSessionToTransaction(session);
		return springSession;
	}

	@Override
	public AtreusSpringSession getCurrentSession() {
		if (logger.isDebugEnabled()) {
			logger.debug("Call getSession() - Thread [" + Thread.currentThread().getId() + "]");
		}
		if (TransactionSynchronizationManager.hasResource(this)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Session exists for the current thread/transaction context - Thread [" + Thread.currentThread().getId() + "]");
			}
			return (AtreusSpringSession) TransactionSynchronizationManager.getResource(this);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Session does not exist for the current thread/transaction context - Thread [" + Thread.currentThread().getId() + "]");
		}
		return createSession();
	}

}
