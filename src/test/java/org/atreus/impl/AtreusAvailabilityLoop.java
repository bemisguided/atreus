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

import java.util.UUID;

import org.atreus.AtreusConfiguration;
import org.atreus.AtreusSession;
import org.atreus.AtreusSessionFactory;
import org.atreus.AtreusSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtreusAvailabilityLoop {

	private static final Logger logger = LoggerFactory.getLogger(AtreusAvailabilityLoop.class);

	public static void main(String[] args) throws Exception {
		AtreusConfiguration config = new AtreusConfiguration(new String[] { "192.168.2.28", "192.168.2.33" }, 9160, "AtreusAvailability");
		config.setClusterPollFrequency(30 * 1000);
		// config.setDefaultWriteConsistencyLevel(ConsistencyLevel.QUORUM);
		final AtreusSessionFactory factory = AtreusSessionFactoryBuilder.buildFactory(config);
		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread(new Runnable() {

				private void doIt() {
					try {
						AtreusSession session = factory.openSession();

						try {
							session.setFamilyAndKey("Test", UUID.randomUUID().toString());
							session.writeColumn("Column1", 12345);
							session.writeColumn("Column2", 12345);
							session.writeColumn("Column3", 12345);
							session.flush();
							logger.info(Thread.currentThread().getName() + " - Write key - " + session.getRowKey());
						} catch (Exception e) {
							logger.error("Exception on execute: " + e.toString());
						} finally {
							session.close();
						}
					} catch (Exception e) {
						logger.error("Exception on open: " + e.toString());
					}
				}

				@Override
				public void run() {
					while (true) {
						doIt();
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			}, "Runner-" + i);
			thread.start();
		}
	}
}
