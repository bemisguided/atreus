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

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Base class for all Atreus exceptions.
 *
 * @author Martin Crawford
 */
public abstract class AtreusException extends RuntimeException {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  private static final transient Logger LOG = LoggerFactory.getLogger(AtreusException.class);
  private static final ResourceBundle ERROR_CODE_MESSAGES = ResourceBundle.getBundle("messages.errorCodes");

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  private final int errorCode;
  private final Object[] details;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  protected AtreusException(int errorCode, Object... details) {
    this.errorCode = errorCode;
    this.details = details;
  }

  protected AtreusException(int errorCode, Throwable cause, Object... details) {
    super(cause);
    this.errorCode = errorCode;
    this.details = details;
  }

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  @Override
  public final String getMessage() {
    String message = MessageFormat.format(ERROR_CODE_MESSAGES.getString("errorCode." + errorCode), details);
    StringBuilder sb = new StringBuilder(message);
    Throwable cause = super.getCause();
    if (cause != null) {
      sb.append(": ");
      sb.append(cause.getMessage());
    }
    return sb.toString();
  }

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters

  public int getErrorCode() {
    return errorCode;
  }

} // end of class