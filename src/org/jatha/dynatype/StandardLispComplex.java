/*
 * Jatha - a Common LISP-compatible LISP library in Java.
 * Copyright (C) 1997-2005 Micheal Scott Hewett
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * For further information, please contact Micheal Hewett at
 *   hewett@cs.stanford.edu
 *
 * ==================================================================
 *
 *  Complex.java  - Holds a complex number
 *
 * -------------------------------------------------------------------
 * User: hewett
 * Date: Oct 25, 2003
 * Time: 5:36:06 PM
 * -------------------------------------------------------------------
 */

package org.jatha.dynatype;

import org.jatha.Jatha;

import java.math.BigInteger;

/**
 * StandardLispComplex represents a Complex number.
 *
 */
public class StandardLispComplex extends StandardLispNumber implements LispComplex
{
  protected LispValue f_realPart      = null;
  protected LispValue f_imaginaryPart = null;

  /**
   * Creates the complex number (0, 0).
   */
  public StandardLispComplex(Jatha lisp)
  {
    this(lisp, lisp.makeReal(0.0), lisp.makeReal(0.0));
  }

  /**
   * Creates realPart new complex number with the given
   * real part and imaginary part.
   * @param realPart
   * @param imaginaryPart
   */
  public StandardLispComplex(Jatha lisp, LispValue realPart, LispValue imaginaryPart)
  {
    super(lisp);

    f_realPart = realPart;
    f_imaginaryPart = imaginaryPart;
  }


  public String toString()
  {
    return "(" + f_realPart + " + " + f_imaginaryPart + "i)";
  }

  public boolean equals(Object o)
   {
     if (this == o) return true;
     if (!(o instanceof LispComplex)) return false;

     final LispComplex complex = (LispComplex) o;

     if (! (f_imaginaryPart.equals(complex.getImaginary()))) return false;
     if (! (f_realPart.equals(complex.getReal()))) return false;

     return true;
   }

   public int hashCode()
   {
     int result;
     long temp;
     temp = Double.doubleToLongBits(((LispNumber)f_realPart).getDoubleValue());
     result = (int) (temp ^ (temp >>> 32));
     temp = Double.doubleToLongBits(((LispNumber)f_imaginaryPart).getDoubleValue());
     result = 29 * result + (int) (temp ^ (temp >>> 32));
     return result;
   }


  public void setImaginary(LispValue imaginaryPart)
  {
    f_imaginaryPart = imaginaryPart;
  }

  public LispValue getImaginary()
  {
    return f_imaginaryPart;
  }

  public LispValue getReal()
  {
    return f_realPart;
  }

  public void setReal(LispValue realPart)
  {
    f_realPart = realPart;
  }


  // ----- Functions prescribed by the LispComplex interface  -------

  // todo: Need to implement all of these, and change some of the method names, for example plus, to match the Jatha conventions.



  /**
   *  Modulus
   */
  public double abs(LispComplex a)
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex acos(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex acosh(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Argument
  public double arg()
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public double arg(LispComplex a)
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Trigonometric Functions
  // 	  	public LispComplex sin(LispComplex aa );
  public LispComplex asin(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex asinh(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex atan(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex atanh(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Conjugate
  public LispComplex conjugate()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex conjugate(LispComplex a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex copy(LispComplex a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex[] copy(LispComplex[] a)
  {
    return new LispComplex[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex[][] copy(LispComplex[][] a)
  {
    return new LispComplex[0][];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex cos(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex cosh(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex exp(double aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Exponential
  public LispComplex exp(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex expMinusJayArg(double arg)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex expPlusJayArg(double arg)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public BigInteger getBigIntegerValue()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public double getDoubleValue()
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean getInfOption()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public long getLongValue()
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  //Hypotenuse
  public double hypot(LispComplex aa, LispComplex bb)
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Reciprocal
  public LispComplex inverse()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex inverse(LispComplex a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Logical Tests
  // 	public boolean isEqual(LispComplex x);
  public boolean isEqual(LispComplex a, LispComplex b)
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isEqualWithinLimits(LispComplex a, LispComplex b, double limit)
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isEqualWithinLimits(LispComplex x, double limit)
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isInfinite()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isInfinite(LispComplex a)
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isMinusInfinity()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isMinusInfinity(LispComplex a)
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isNaN()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isNaN(LispComplex a)
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isPlusInfinity()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isPlusInfinity(LispComplex a)
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isReal()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isReal(LispComplex a)
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isZero()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isZero(LispComplex a)
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Logarithm
  public LispComplex log(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex minus(double a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex minus(double a, double b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex minus(double a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Subtraction
  public LispComplex minus(LispComplex a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex minus(LispComplex a, double b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex minus(LispComplex a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void minusEquals(double a)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void minusEquals(LispComplex a)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex minusJay()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex minusOne()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Negation
  public LispComplex negate(LispComplex a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex nthRoot(LispComplex aa, int n)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Arrays
  public LispComplex[] oneDarray(int n)
  {
    return new LispComplex[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex[] oneDarray(int n, double a, double b)
  {
    return new LispComplex[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex[] oneDarray(int n, LispComplex xx)
  {
    return new LispComplex[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex over(double a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex over(double a, double b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex over(double a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Division
  public LispComplex over(LispComplex a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex over(LispComplex a, double b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex over(LispComplex a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void overEquals(double a)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void overEquals(LispComplex a)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex pi()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex plus(double a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex plus(double a, double b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex plus(double a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Addition
  public LispComplex plus(LispComplex a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex plus(LispComplex a, double b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex plus(LispComplex a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void plusEquals(double a)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void plusEquals(LispComplex a)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex plusJay()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex plusOne()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Sets the real part of this Complex number to length*cos(angle) and
   * sets the imaginary part to length*sin(angle) where lengthis the first argument
   * and angle is the argument (in radians)
   *
   * @param length
   * @param angle
   */
  public void polar(double length, double angle)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex pow(double a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex pow(int a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex pow(LispComplex a, double b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex pow(LispComplex a, int b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex pow(LispComplex a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Infinity Handling Option for multiplication and division
  // 	  	public void setInfOption(boolean opt);
  public void setInfOption(int opt)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Sets both the real part and the imaginary part of a complex number.
   *
   * @param real the new value for the real part of the complex number
   * @param imag the new value for the imaginary part of the complex number
   * @see #setValue
   */
  public void setValue(LispValue real, LispValue imag)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex sinh(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex sqrt(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Power
  public LispComplex square(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public double squareAbs()
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public double squareAbs(LispComplex a)
  {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex tan(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex tanh(LispComplex aa)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex times(double a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex times(double a, double b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex times(double a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  // Multiplication
  public LispComplex times(LispComplex a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex times(LispComplex a, double b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex times(LispComplex a, LispComplex b)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void timesEquals(double a)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void timesEquals(LispComplex a)
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex transposedMinus(double a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex transposedOver(double a)
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex[][] twoDarray(int n, int m)
  {
    return new LispComplex[0][];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex[][] twoDarray(int n, int m, double a, double b)
  {
    return new LispComplex[0][];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex[][] twoDarray(int n, int m, LispComplex xx)
  {
    return new LispComplex[0][];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex twoPiJay()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public LispComplex zero()
  {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
