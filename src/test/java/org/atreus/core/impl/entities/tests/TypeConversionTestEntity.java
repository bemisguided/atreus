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
package org.atreus.core.impl.entities.tests;

import org.atreus.core.annotations.AtreusEntity;
import org.atreus.core.annotations.AtreusPrimaryKey;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

/**
 * TypeConversionTestEntity.
 *
 * @author Martin Crawford
 */
@AtreusEntity()
public class TypeConversionTestEntity {

  // Constants ---------------------------------------------------------------------------------------------- Constants

  // Instance Variables ---------------------------------------------------------------------------- Instance Variables

  @AtreusPrimaryKey
  private String id;

  private BigDecimal aBigDecimal;

  private BigInteger aBigInteger;

  private boolean aBoolean;

  private Date aDate;

  private double aDouble;

  private float aFloat;

  private InetAddress anInetAddress;

  private int aInteger;

  private long aLong;

  private String aString;

  private UUID aUuid;

  // Constructors ---------------------------------------------------------------------------------------- Constructors

  // Public Methods ------------------------------------------------------------------------------------ Public Methods

  // Protected Methods ------------------------------------------------------------------------------ Protected Methods

  // Private Methods ---------------------------------------------------------------------------------- Private Methods

  // Getters & Setters ------------------------------------------------------------------------------ Getters & Setters


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BigDecimal getaBigDecimal() {
    return aBigDecimal;
  }

  public void setaBigDecimal(BigDecimal aBigDecimal) {
    this.aBigDecimal = aBigDecimal;
  }

  public BigInteger getaBigInteger() {
    return aBigInteger;
  }

  public void setaBigInteger(BigInteger aBigInteger) {
    this.aBigInteger = aBigInteger;
  }

  public boolean isaBoolean() {
    return aBoolean;
  }

  public void setaBoolean(boolean aBoolean) {
    this.aBoolean = aBoolean;
  }

  public Date getaDate() {
    return aDate;
  }

  public void setaDate(Date aDate) {
    this.aDate = aDate;
  }

  public double getaDouble() {
    return aDouble;
  }

  public void setaDouble(double aDouble) {
    this.aDouble = aDouble;
  }

  public float getaFloat() {
    return aFloat;
  }

  public void setaFloat(float aFloat) {
    this.aFloat = aFloat;
  }

  public InetAddress getAnInetAddress() {
    return anInetAddress;
  }

  public void setAnInetAddress(InetAddress anInetAddress) {
    this.anInetAddress = anInetAddress;
  }

  public int getaInteger() {
    return aInteger;
  }

  public void setaInteger(int aInteger) {
    this.aInteger = aInteger;
  }

  public long getaLong() {
    return aLong;
  }

  public void setaLong(long aLong) {
    this.aLong = aLong;
  }

  public String getaString() {
    return aString;
  }

  public void setaString(String aString) {
    this.aString = aString;
  }

  public UUID getaUuid() {
    return aUuid;
  }

  public void setaUuid(UUID aUuid) {
    this.aUuid = aUuid;
  }

}