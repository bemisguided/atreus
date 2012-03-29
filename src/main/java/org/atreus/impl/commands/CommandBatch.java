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
package org.atreus.impl.commands;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class CommandBatch {

	private List<BatchableCommand> batchList = new LinkedList<BatchableCommand>();

	private boolean open = false;

	public void addCommand(BatchableCommand command) {
		batchList.add(command);
		open = true;
	}

	public List<BatchableCommand> getBatchList() {
		Collections.sort(batchList, new Comparator<BatchableCommand>() {
			public int compare(BatchableCommand o1, BatchableCommand o2) {
				int result = o1.getColumnFamily().compareTo(o2.getColumnFamily());
				if (result == 0) {
					result = o1.getRowKey().compareTo(o2.getRowKey());
				}
				return result;
			}
		});
		return batchList;
	}

	public boolean isOpen() {
		return open;
	}
}
