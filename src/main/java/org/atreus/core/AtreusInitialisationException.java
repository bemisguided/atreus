/**
 * The MIT License
 *
 * Copyright (c) 2014 Martin Crawford and contributors.
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
package org.atreus.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception thrown if an error occurred on intialisation of Atreus..
 *
 * @author Martin Crawford
 */
public class AtreusInitialisationException extends AtreusException {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusInitialisationException.class);

  public static int ERROR_CODE_TYPE_STRATEGY_INVALID = 100;
  public static int ERROR_CODE_TTL_STRATEGY_INVALID = 101;
  public static int ERROR_CODE_PRIMARY_KEY_STRATEGY_INVALID = 102;
  public static int ERROR_CODE_PRIMARY_KEY_MULTIPLE = 103;
  public static int ERROR_CODE_REGISTER_TYPE_STRATEGY = 110;
  public static int ERROR_CODE_REGISTER_PRIMARY_KEY_STRATEGY = 111;
  public static int ERROR_CODE_REGISTER_TTL_STRATEGY = 112;

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  public AtreusInitialisationException(int errorCode, Object... details) {
    super(errorCode, details);
  }

  public AtreusInitialisationException(int errorCode, Throwable cause, Object... details) {
    super(errorCode, cause, details);
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

} // end of class