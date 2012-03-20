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

package org.atreus.converters;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarTypeConverter extends ObjectStreamTypeConverter {

	public boolean isSupported(Class<?> type) {
		return Calendar.class.isAssignableFrom(type);
	}

	public void write(Object value, ObjectOutputStream output) throws IOException {
		Calendar cal = (Calendar) value;
		output.writeLong(cal.getTimeInMillis());
		output.writeUTF(cal.getTimeZone().getID());
	}

	public Object read(ObjectInputStream input) throws IOException {
		long timestamp = input.readLong();
		String tzId = input.readUTF();
		TimeZone tz = TimeZone.getTimeZone(tzId);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(timestamp);
		return cal;
	}
}
