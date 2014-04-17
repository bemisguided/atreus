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

public abstract class CachedReference {

	private final CachedReference parent;

	private CacheResult result = CacheResult.UNKNOWN;

	public CachedReference() {
		this.parent = null;
	}

	public CachedReference(CachedReference parent) {
		this.parent = parent;
	}

	protected abstract Iterable<? extends CachedReference> getChildren();

	protected CachedReference getParent() {
		return parent;
	}

	public CacheResult getResult() {
		return result;
	}

	public void markResultDelete() {
		result = CacheResult.DELETED;
		for (CachedReference cachedReference : getChildren()) {
			cachedReference.markResultDelete();
		}
	}

	public void markResultFull() {
		result = CacheResult.FULL;
		if (parent != null) {
			parent.markResultPartial();
		}
	}

	public void markResultPartial() {
		result = CacheResult.PARTIAL;
		if (parent != null) {
			parent.markResultPartial();
		}
	}

	public void setResult(CacheResult result) {
		this.result = result;
	}

}