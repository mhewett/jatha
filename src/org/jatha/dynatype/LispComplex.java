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
 */

package org.jatha.dynatype;


// @date    Thu Mar 27 13:29:37 1997
/**
 * LispComplex is an abstract class that implements
 * the Common LISP COMPLEX type.  It contains the
 * definitions of add, subtract, multiply, divide
 * and many other functions.
 *
 * @see StandardLispComplex
 * @see LispValue
 * @see LispNumber
 * @see LispAtom
 * @see LispInteger
 * @see LispReal
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
public interface LispComplex extends LispNumber
{

  /*
  public LispComplex();

  public LispComplex(double real, double imag);
  public LispComplex(double real)	  ;
  public LispComplex(LispComplex c)	  ;
  */

  // Accessors

  /**
   * Sets the real part of a complex number.
   * @param real the new value for the real part of the complex number
   * @see #setValue
   */
  public void setReal(LispValue real);

  /**
   * Sets the real part of a complex number.
   * @param imag the new value for the imaginary part of the complex number
   * @see #setValue
   */
  public void setImaginary(LispValue imag);

  /**
   * Sets both the real part and the imaginary part of a complex number.
   * @param real the new value for the real part of the complex number
   * @param imag the new value for the imaginary part of the complex number
   * @see #setValue
   */
  public void setValue(LispValue real, LispValue imag);

  /**
   * Sets the real part of this Complex number to length*cos(angle) and
   * sets the imaginary part to length*sin(angle) where lengthis the first argument
   * and angle is the argument (in radians)
   * @param length
   * @param angle
   */
  public void polar(double length, double angle);

  /**
   * Returns the real part of the complex number.
   */
  public LispValue getReal();

  /**
   * Returns the imaginary part of the complex number.
   */
  public LispValue getImaginary();


  // Input and Output


  // Truncate mantissae to n places
  /*
  public LispComplex truncate(int n);
  public LispComplex truncate(LispComplex x, int n);
  */

  // Conversions
  /*
  public LispComplex parseComplex(String ss);
  public LispComplex valueOf(String ss);
  */

  // Modulus
  public double abs(LispComplex a);
  public double squareAbs();
  public double squareAbs(LispComplex a);

  // Argument
  public double arg();
  public double arg(LispComplex a);
  // Conjugate
  public LispComplex conjugate();
  public LispComplex conjugate(LispComplex a);
  // Addition
  public LispComplex plus(LispComplex a);
  public LispComplex plus(LispComplex a, LispComplex b);
  public LispComplex plus(double a);
  public LispComplex plus(LispComplex a, double b);
  public LispComplex plus(double a, LispComplex b);
  public LispComplex plus(double a, double b);
  public void plusEquals(LispComplex a );

  public void plusEquals(double a );

  // Subtraction
  public LispComplex minus(LispComplex a);
  public LispComplex minus(LispComplex a, LispComplex b);
  public LispComplex minus(double a);
  public LispComplex minus(LispComplex a, double b);
  public LispComplex transposedMinus(double a);
  public LispComplex minus(double a, LispComplex b);
  public LispComplex minus(double a, double b);
  public void minusEquals(LispComplex a );

  public void minusEquals(double a );

  // Infinity Handling Option for multiplication and division
  // 	  	public void setInfOption(boolean opt);
  public void setInfOption(int opt);
  public boolean getInfOption();

  // Multiplication
  public LispComplex times(LispComplex a);
  public LispComplex times(LispComplex a, LispComplex b);
  public LispComplex times(double a);
  public LispComplex times(LispComplex a, double b);
  public LispComplex times(double a, LispComplex b);
  public LispComplex times(double a, double b);
  public void timesEquals(LispComplex a );

  public void timesEquals(double a );

  // Division
  public LispComplex over(LispComplex a);
  public LispComplex over(LispComplex a, LispComplex b);
  public LispComplex over(double a);
  public LispComplex over(LispComplex a, double b);
  public LispComplex transposedOver(double a);
  public LispComplex over(double a, LispComplex b);
  public LispComplex over(double a, double b);
  public void overEquals(LispComplex a );

  public void overEquals(double a );

  // Reciprocal
  public LispComplex inverse();
  public LispComplex inverse(LispComplex a);

  // Negation
  public LispComplex negate(LispComplex a);
  // Exponential
  public LispComplex exp(LispComplex aa);
  public LispComplex exp(double aa);
  public LispComplex expPlusJayArg(double arg);
  public LispComplex expMinusJayArg(double arg);
  // Logarithm
  public LispComplex log(LispComplex aa );
  public LispComplex sqrt(LispComplex aa );
  public LispComplex nthRoot(LispComplex aa, int n );
  // Power
  public LispComplex square(LispComplex aa);
  public LispComplex pow(LispComplex a, int b);
  public LispComplex pow(LispComplex a, double b);
  public LispComplex pow(LispComplex a, LispComplex b);
  public LispComplex pow(int a, LispComplex b);
  public LispComplex pow(double a, LispComplex b);
  // Trigonometric Functions
  // 	  	public LispComplex sin(LispComplex aa );
  public LispComplex asin(LispComplex aa );
  public LispComplex cos(LispComplex aa );
  public LispComplex acos(LispComplex aa );
  public LispComplex tan(LispComplex aa );
  public LispComplex atan(LispComplex aa );
  public LispComplex sinh(LispComplex aa );
  public LispComplex asinh(LispComplex aa );
  public LispComplex cosh(LispComplex aa );
  public LispComplex acosh(LispComplex aa );
  public LispComplex tanh(LispComplex aa );
  public LispComplex atanh(LispComplex aa );
  //Hypotenuse
  public double hypot(LispComplex aa, LispComplex bb);
  // Logical Tests
  // 	public boolean isEqual(LispComplex x);
  public boolean isEqual(LispComplex a, LispComplex b);
  public boolean isEqualWithinLimits(LispComplex x, double limit);
  public boolean isEqualWithinLimits(LispComplex a, LispComplex b, double limit);
  public boolean isReal();
  public boolean isReal(LispComplex a);
  public boolean isZero();
  public boolean isZero(LispComplex a);
  public boolean isInfinite();
  public boolean isInfinite(LispComplex a);
  public boolean isPlusInfinity();
  public boolean isPlusInfinity(LispComplex a);
  public boolean isMinusInfinity();
  public boolean isMinusInfinity(LispComplex a);
  public boolean isNaN();
  public boolean isNaN(LispComplex a);
  // Arrays
  public LispComplex[] oneDarray(int n);
  public LispComplex[] oneDarray(int n, double a, double b);
  public LispComplex[] oneDarray(int n, LispComplex xx);
  public LispComplex[][] twoDarray(int n, int m);
  public LispComplex[][] twoDarray(int n, int m, double a, double b);
  public LispComplex[][] twoDarray(int n, int m, LispComplex xx);
  // Deep Copy

  public LispComplex copy(LispComplex a);

  public LispComplex[] copy(LispComplex[] a);
  public LispComplex[][] copy(LispComplex[][] a);
  public LispComplex zero();
  public LispComplex plusOne();
  public LispComplex minusOne();
  public LispComplex plusJay();
  public LispComplex minusJay();
  public LispComplex pi();
  public LispComplex twoPiJay();

}

