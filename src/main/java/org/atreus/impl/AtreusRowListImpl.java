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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.atreus.AtreusColumnMap;
import org.atreus.AtreusRowList;

public class AtreusRowListImpl implements AtreusRowList {

	private final List<AtreusColumnMap> list;

	public AtreusRowListImpl(int size) {
		this.list = new ArrayList<AtreusColumnMap>(size);
	}

	@Override
	public Iterator<AtreusColumnMap> iterator() {
		return list.iterator();
	}

	@Override
	public boolean hasRows() {
		return size() > 0;
	}

	@Override
	public int size() {
		return list.size();
	}

	public void addColumnMap(AtreusColumnMap columnMap) {
		list.add(columnMap);
	}

}
