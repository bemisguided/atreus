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

import java.util.List;

import org.atreus.v0.AtreusConfiguration;
import org.atreus.v0.AtreusSessionFactory;
import org.atreus.v0.AtreusSessionFactoryBuilder;
import org.atreus.v0.AtreusTypeConverter;
import org.atreus.impl.util.AssertUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AtreusSpringSessionFactoryBase implements InitializingBean, DisposableBean, AtreusSpringSessionFactory {

	private AtreusConfiguration config;

	private AtreusSessionFactory sessionFactory;

	private List<AtreusTypeConverter> typeConverters;

	@Override
	public void afterPropertiesSet() throws Exception {
		AssertUtils.notNull(config, "Atreus Configuration property is not set");
		sessionFactory = AtreusSessionFactoryBuilder.buildFactory(config);
		for (AtreusTypeConverter converter : typeConverters) {
			sessionFactory.addConverter(converter);
		}
	}

	@Override
	public void destroy() throws Exception {
		sessionFactory.disconnect();
	}

	public AtreusConfiguration getConfiguration() {
		return config;
	}

	protected AtreusSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public List<AtreusTypeConverter> getTypeConverters() {
		return typeConverters;
	}

	public void setConfiguration(AtreusConfiguration config) {
		this.config = config;
	}

	protected void setTypeConverters(List<AtreusTypeConverter> typeConverters) {
		this.typeConverters = typeConverters;
	}

}
