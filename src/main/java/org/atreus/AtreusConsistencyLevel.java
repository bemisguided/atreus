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
package org.atreus;

public enum AtreusConsistencyLevel {
	ANY() {

		@Override
		public boolean isReadDegradable() {
			return false;
		}

		@Override
		public boolean isWriteDegradable() {
			return false;
		}

		@Override
		public AtreusConsistencyLevel readDegrade(boolean fast) {
			return null;
		}

		@Override
		public AtreusConsistencyLevel writeDegrade(boolean fast) {
			return null;
		}

	},
	ONE() {

		@Override
		public boolean isReadDegradable() {
			return false;
		}

		@Override
		public boolean isWriteDegradable() {
			return true;
		}

		@Override
		public AtreusConsistencyLevel readDegrade(boolean fast) {
			return null;
		}

		@Override
		public AtreusConsistencyLevel writeDegrade(boolean fast) {
			return ANY;
		}

	},
	TWO() {

		@Override
		public boolean isReadDegradable() {
			return true;
		}

		@Override
		public boolean isWriteDegradable() {
			return true;
		}

		@Override
		public AtreusConsistencyLevel readDegrade(boolean fast) {
			return ONE;
		}

		@Override
		public AtreusConsistencyLevel writeDegrade(boolean fast) {
			if (fast) {
				return ANY;
			}
			return ONE;
		}

	},
	THREE() {

		@Override
		public boolean isReadDegradable() {
			return true;
		}

		@Override
		public boolean isWriteDegradable() {
			return true;
		}

		@Override
		public AtreusConsistencyLevel readDegrade(boolean fast) {
			if (fast) {
				return ONE;
			}
			return TWO;
		}

		@Override
		public AtreusConsistencyLevel writeDegrade(boolean fast) {
			if (fast) {
				return ANY;
			}
			return TWO;
		}

	},
	QUORUM() {

		@Override
		public boolean isReadDegradable() {
			return true;
		}

		@Override
		public boolean isWriteDegradable() {
			return true;
		}

		@Override
		public AtreusConsistencyLevel readDegrade(boolean fast) {
			if (fast) {
				return ONE;
			}
			return TWO;
		}

		@Override
		public AtreusConsistencyLevel writeDegrade(boolean fast) {
			if (fast) {
				return ANY;
			}
			return TWO;
		}

	},
	LOCAL_QUORUM() {

		@Override
		public boolean isReadDegradable() {
			return true;
		}

		@Override
		public boolean isWriteDegradable() {
			return true;
		}

		@Override
		public AtreusConsistencyLevel readDegrade(boolean fast) {
			if (fast) {
				return ONE;
			}
			return QUORUM;
		}

		@Override
		public AtreusConsistencyLevel writeDegrade(boolean fast) {
			if (fast) {
				return ANY;
			}
			return QUORUM;
		}

	},
	EACH_QUORUM() {

		@Override
		public boolean isReadDegradable() {
			return true;
		}

		@Override
		public boolean isWriteDegradable() {
			return true;
		}

		@Override
		public AtreusConsistencyLevel readDegrade(boolean fast) {
			if (fast) {
				return ONE;
			}
			return LOCAL_QUORUM;
		}

		@Override
		public AtreusConsistencyLevel writeDegrade(boolean fast) {
			if (fast) {
				return ANY;
			}
			return LOCAL_QUORUM;
		}

	},
	ALL() {

		@Override
		public boolean isReadDegradable() {
			return true;
		}

		@Override
		public boolean isWriteDegradable() {
			return true;
		}

		@Override
		public AtreusConsistencyLevel readDegrade(boolean fast) {
			if (fast) {
				return ONE;
			}
			return EACH_QUORUM;
		}

		@Override
		public AtreusConsistencyLevel writeDegrade(boolean fast) {
			if (fast) {
				return ANY;
			}
			return EACH_QUORUM;
		}

	};

	public abstract boolean isReadDegradable();

	public abstract boolean isWriteDegradable();

	public abstract AtreusConsistencyLevel readDegrade(boolean fast);

	public abstract AtreusConsistencyLevel writeDegrade(boolean fast);
}
