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
package org.atreus.v0.impl.cache;

import java.util.Collections;

public class CachedColumn extends CachedReference {

	private final byte[] name;

	private byte[] value;

	public CachedColumn(CachedRow parent, byte[] name) {
		super(parent);
		this.name = name;
	}

	public CachedColumn(CachedSuperColumn parent, byte[] name) {
		super(parent);
		this.name = name;
	}

	@Override
	protected Iterable<? extends CachedReference> getChildren() {
		return Collections.emptySet();
	}

	public byte[] getName() {
		return name;
	}

	public byte[] getValue() {
		return value;
	}

	@Override
	public void markResultDelete() {
		value = null;
		super.markResultDelete();
	}

	public void setValue(byte[] value) {
		markResultFull();
		this.value = value;
	}

}
