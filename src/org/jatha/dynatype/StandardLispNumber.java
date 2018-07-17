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

import org.jatha.Jatha;

import java.util.Iterator;


// @date    Thu Mar 27 13:29:37 1997
/**
 * LispNumber is an abstract class that implements
 * the Common LISP NUMBER type.  It contains the
 * definitions of add, subtract, multiply and divide.
 *
 *
 * @see LispValue
 * @see LispAtom
 * @see LispInteger
 * @see LispReal
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
abstract public class StandardLispNumber extends StandardLispAtom implements LispNumber
{

  public StandardLispNumber()
  {
    super();
  }

  public StandardLispNumber(Jatha lisp)
  {
    super(lisp);
  }


  public boolean    basic_constantp()  { return true; }
  public boolean    basic_numberp()    { return true; }


  // ---  non-LISP methods  ---

  public abstract double getDoubleValue();


  // ---- LISP functions -------------
  
  // contributed by Jean-Pierre Gaillardon, April 2005
  public LispValue constantp()
  {
    return f_lisp.T;
  }

  /**
   * Default implementation of abs.
   */
  public LispValue abs()
  {
    if (this.getDoubleValue() > 0.0)
      return this;
    else
      return new StandardLispReal(f_lisp, this.getDoubleValue() * -1.0);
  }

  /**
   * Converts a numeric value from degrees to radians.
   * @return The value in radians.
   */
  public LispValue degreesToRadians()
  {
    return new StandardLispReal(f_lisp, this.getDoubleValue() * StrictMath.PI / 180.0);
  }

  /**
   * Converts a numeric value from radians to degrees.
   * @return The value in degrees.
   */
  public LispValue radiansToDegrees()
  {
    return new StandardLispReal(f_lisp, this.getDoubleValue() * 180.0 / StrictMath.PI);
  }

  /**
   * Calculate the object raised to the power of n.
   */
  public LispValue expt(LispValue n)
  {
    boolean allIntegers = (this.basic_integerp() && n.basic_integerp());
    if (n instanceof LispNumber)
      if (allIntegers)
      {
        return new StandardLispBignum(f_lisp, getBigIntegerValue().pow((int) ((LispNumber)n).getLongValue()));
      }
      else
      {
        return new StandardLispReal(f_lisp, StrictMath.pow(getDoubleValue(), ((LispNumber)n).getDoubleValue()));
      }
    else
      throw new LispValueNotANumberException("The second argument to expt (" + n + ")");
  }
  
  /**
   * Compute the factorial of a non-negative integer.
   * Reals are truncated to the nearest integer.
   */
  public LispValue factorial()
  {
    LispNumber value = this;

    // Truncate real numbers, if passed in.
    if (! (value instanceof LispInteger))
      value = new StandardLispInteger(f_lisp, value.getLongValue());

    // Need to handle this through the multiply function
    // in order to convert to BigNums as necessary.
    if (value.getLongValue() <= 1)
      return f_lisp.ONE;
    else {
        // changed algorithm, due to stack overflow when factorizing big numbers. (n > 1000 ca)
        LispValue total = f_lisp.ONE;
        long index = 2L;
        while(index <= value.getLongValue()) {
            total = total.multiply(new StandardLispInteger(f_lisp,index++));
        }
        return total;
    }
    //      return value.multiply(value.subtract(f_lisp.ONE).factorial());
  }


  /**
   * Returns the max of this number and its arguments, which may
   * be a list of numbers or a single number.
   * @param args a number or a list of numbers
   * @return the maximum numeric value.
   */
  public LispValue max(LispValue args)
  {
    // The list of numbers has already been evaluated.
    // Terminate if we hit any non-numbers.
    // Keep the max in a double until the value
    // either overflows, in which case we turn it into
    // a bignum

    double    maxValue    = this.getDoubleValue();
    double    otherValue  = 0;
    boolean   allIntegers = (this instanceof LispInteger);
    LispValue arglist;

    //System.err.println("Checking max of " + this + " and " + args);
    // Make sure the argument is a list of numbers.
    arglist = args;
    if (! (arglist instanceof LispConsOrNil))
      arglist = f_lisp.makeList(arglist);

    while (arglist != f_lisp.NIL)
    {
      LispValue arg = arglist.car();

      if (! arg.basic_numberp())
      {
        super.max(arglist.car());  // generates an error.
        return(f_lisp.NIL);
      }

      // Keep a flag to see whether the final value
      // should be an integer or not.
      if (allIntegers)
        if (! arg.basic_integerp())
          allIntegers = false;

      // Do the MAX function.
      otherValue = ((LispNumber)arg).getDoubleValue();
      if (otherValue > maxValue)
        maxValue = otherValue;

      arglist = arglist.cdr();
      //System.err.println("  max value is " + maxValue);
    }

    if (allIntegers)
      return(f_lisp.makeInteger((long)maxValue));
    else
      return(f_lisp.makeReal(maxValue));
  }

  /**
   * Returns the min of this number and its arguments, which may
   * be a list of numbers or a single number.
   * @param args a number or a list of numbers
   * @return the minimum numeric value.
   */
  public LispValue min(LispValue args)
  {
    // The list of numbers has already been evaluated.
    // Terminate if we hit any non-numbers.
    // Keep the min in a double until the value
    // either overflows, in which case we turn it into
    // a bignum

    double    minValue    = this.getDoubleValue();
    double    otherValue  = 0;
    boolean   allIntegers = (this instanceof LispInteger);
    LispValue arglist;

    // Make sure the argument is a list of numbers.
    arglist = args;
    if (! (arglist instanceof LispConsOrNil))
      arglist = f_lisp.makeList(arglist);

    while (arglist != f_lisp.NIL)
    {
      LispValue arg = arglist.car();

      if (! arg.basic_numberp())
      {
        super.max(arglist.car());  // generates an error.
        return(f_lisp.NIL);
      }

      // Keep a flag to see whether the final value
      // should be an integer or not.
      if (allIntegers)
        if (! arg.basic_integerp())
          allIntegers = false;

      // Do the Min function.
      otherValue = ((LispNumber)arg).getDoubleValue();
      if (otherValue < minValue)
        minValue = otherValue;

      arglist = arglist.cdr();
    }

    if (allIntegers)
      return(f_lisp.makeInteger((long)minValue));
    else
      return(f_lisp.makeReal(minValue));
  }


  /**
   * Returns the negative of the given number.
   */
  public LispValue    negate()
  {
    return this.subtract(f_lisp.NIL);  // Returns the negative.
  }

  public LispValue    numberp()  { return f_lisp.T; }

  /**
   * Computes 1/x of the given number.  Only valid for numbers.
   * @return a LispReal
   */
  public LispValue reciprocal()
  {
    return f_lisp.makeReal(1.0 / getDoubleValue());
  }


/* ------------------  Arithmetic functions   ------------------------------ */

  // Added bignums:  20 May 1997 (mh)
  // detecting bignums:
  //   HI = java.lang.Long.MAX_VALUE
  //   LO = java.lang.Long.MIN_VALUE
  //   a + b:
  //     a>0, b>0  if (HI - a) < b  ==> use Bignum
  //     a>0, b<0  or a<0, b>0: won't overflow
  //     a<0, b<0  if (LO - a) > b  ==> use Bignum
  //
  //   a - b:
  //     a>0, b<0  if (HI + b) < a  ==> use Bignum
  //     a<0, b>0  if (LO + b) > a  ==> use Bignum
  //     a>0, b>0 : won't overflow.
  //     a<0, b<0 : won't overflow.
  //
  //   a * b:
  //     let (a * b) => X.  If (X / a) == b, okay, else overflow.  Hmmm, could be expensive.
  //


  /**
   * ADD adds any combination of real or integer numbers.
   * May create a Bignum or signal floating-point overflow
   * if necessary.
   *
   * @see LispReal
   * @see LispInteger
   */
  public LispValue     add(LispValue  args)
  {
    // The list of numbers has already been evaluated.
    // Terminate if we hit any non-numbers.
    // Keep the sum in a Long value until the value
    // either overflows, in which case we turn it into
    // a bignum, or else a real value is added, in which
    // case the result is a double.

    double    d_sum  = this.getDoubleValue();
    long      l_sum  = 0;
    long      l_addend = 0;
    LispValue addend, arglist;
    boolean   allIntegers = this.basic_integerp();

    if (allIntegers)
      l_sum = this.getLongValue();

    // Make sure the argument is a list of numbers.
    arglist = args;
    if (! (arglist instanceof LispConsOrNil))
      arglist = f_lisp.makeList(arglist);

    while (arglist != f_lisp.NIL)
    {
      addend = arglist.car();
      if (! addend.basic_numberp())
      {
        super.add(arglist.car());  // generates an error.
        return(f_lisp.NIL);
      }

      // Might need to convert to a double
      if (allIntegers && (addend.basic_floatp()))
      {
        allIntegers = false;
        d_sum = l_sum;
      }

      // Might need to convert to a Bignum
      // a>0, b>0 : (MAX - a) < b
      // a<0, b<0 : (MIN - a) > b

      if (allIntegers)
      {
        if (addend instanceof LispBignum)
        {
          // System.out.println("Bignum arg...converting " + l_sum + " to bignum.");
          LispBignum bn_val = f_lisp.makeBignum(l_sum);
          return bn_val.add(arglist);
        }

        l_addend = ((LispInteger)addend).getLongValue();

        if ((l_sum > 0) && (l_addend > 0))
        {
          // System.out.println("Comparing " + l_sum + " to " + Long.MAX_VALUE);

          if ((Long.MAX_VALUE - l_sum) < l_addend)
          // Need to convert to bignum
          {
            // System.out.println("Converting " + l_sum + " to bignum.");
            LispBignum bn_val = f_lisp.makeBignum(l_sum);
            return bn_val.add(arglist);
          }
        }
        else if ((l_sum < 0) && (l_addend < 0))
        {
          // System.out.println("Comparing " + l_sum + " to " + Long.MIN_VALUE);
          if ((Long.MIN_VALUE - l_sum) > l_addend)
          {
            // Need to convert to bignum
            // System.out.println("Converting " + l_sum + " to bignum.");
            LispBignum bn_val = f_lisp.makeBignum(l_sum);
            return bn_val.add(arglist);
          }
        }
      }

      // If not allIntegers, result is a double.

      if (allIntegers)
        l_sum += l_addend;
      else
        if (addend.basic_floatp())
          d_sum += ((LispReal)addend).getDoubleValue();
        else if (addend.bignump() == f_lisp.T)
          d_sum += ((LispBignum)addend).getDoubleValue();
        else
          d_sum += ((LispInteger)addend).getLongValue();

      arglist = arglist.cdr();
    };

    if (allIntegers)
      return(f_lisp.makeInteger(l_sum));
    else
      return(f_lisp.makeReal(d_sum));
  }


  /**
   * DIVIDE adds any combination of real or integer numbers.
   *
   * @see LispReal
   * @see LispInteger
   * @see LispBignum
   */
  public LispValue     divide      (LispValue   args)
  {
    double    d_quotient = 0.0;
    long      l_quotient = 0, term_value;
    boolean   allIntegers  = this.basic_integerp();
    LispValue term;
    int       argCount     = 1;
    LispValue arglist      = null;


    // This object is either a Real or an Integer.
    // If dividing by a Bignum, the result will be
    // less than one, so it will necessarily be a double.
    // So we don't have to worry about converting the result
    // to bignums unless the first argument is a bignum.

    if (!allIntegers)
      d_quotient = this.getDoubleValue();
    else
      l_quotient = this.getLongValue();

    // Make sure the argument is a list of numbers.
    arglist = args;
    if (! (arglist instanceof LispConsOrNil))
      arglist = f_lisp.makeList(arglist);

    while (arglist != f_lisp.NIL)
    {
      term = arglist.car();             /* Arglist is already evaluated. */
      if (! term.basic_numberp())
      {
        super.divide(arglist.car());  // Generate an error
        return(f_lisp.NIL);
      }


      ++argCount;

      if (allIntegers && (term.basic_floatp()
        || (term.bignump() == f_lisp.T)))
      {
        allIntegers = false;
        d_quotient  = l_quotient;
      }

      if (term.zerop() == f_lisp.NIL)
      {
        if (!allIntegers)
        {
          if (term.bignump() == f_lisp.T)
            d_quotient = d_quotient
              / ((LispBignum)term).getDoubleValue();
          else if (term.basic_floatp())
            d_quotient = d_quotient / ((LispReal)term).getDoubleValue();
          else
            d_quotient = d_quotient / ((LispInteger)term).getLongValue();
        }
        else // do integer divide and check result.
        {
          term_value = ((LispInteger)term).getLongValue();
          d_quotient = (double)l_quotient / (double)term_value;
          if (StrictMath.round(d_quotient) == d_quotient)
            l_quotient = l_quotient / term_value;
          else
            allIntegers = false;
        }
      }
      else
      {
        System.out.print("\n;; *** ERROR: Attempt to divide by 0.\n");
        return(f_lisp.NIL);
      }

      arglist = arglist.cdr();
    }

    if (argCount == 1)        /* Have to handle n-arg differently from 1-arg */
      if (allIntegers && (l_quotient != 0))
      {
        allIntegers = false;
        d_quotient = 1.0 / l_quotient;
      }
      else if (!allIntegers && (d_quotient != 0))
        d_quotient = 1.0 / d_quotient;
      else
      {
        System.out.print("\n;; *** ERROR: Attempt to divide by 0.\n");
        return(f_lisp.NIL);
      }

    if (allIntegers)
      return(f_lisp.makeInteger(l_quotient));
    else if (d_quotient == (long)d_quotient)
      return(f_lisp.makeInteger((long)d_quotient));
    else
      return(f_lisp.makeReal(d_quotient));
  }


  /**
   * MULTIPLY adds any combination of real or integer numbers.
   *
   * @see LispReal
   * @see LispInteger
   */
  public LispValue     multiply    (LispValue  args)
  {
    double     d_product     = this.getDoubleValue();
    long       l_product     = 0;
    long       l_term, l_result = 0;
    boolean    allIntegers = this.basic_integerp();
    LispValue  term, arglist;

    // Is this number zero?
    if (this.zerop() == f_lisp.T)
      return this;

    if (allIntegers)
      l_product = this.getLongValue();

    // Make sure the argument is a list of numbers.
    arglist = args;
    if (! (arglist instanceof LispConsOrNil))
      arglist = f_lisp.makeList(arglist);

    // Keep a pointer into the arglist because we may
    // have to send the remainder to BigNum to process
    LispValue ptr = arglist;

    for (Iterator iterator = arglist.iterator(); iterator.hasNext();)
    {
      term = (LispValue) iterator.next();

      // Check for a non-numeric argument.
      if (! term.basic_numberp())    // generates an error
      {
        super.multiply(term);
        return(f_lisp.NIL);
      }

      // Multiplying by one?
      if (term.equals(f_lisp.ONE))
      {
        ptr = ptr.cdr();
        continue;
      }

      // Multiplying by zero?
      if (term.zerop() == f_lisp.T)
        return f_lisp.ZERO;


      // Convert to a double if the next term is a floating-point number.
      if (allIntegers && term.basic_floatp())
      {
        allIntegers = false;
        d_product = l_product;
      }

      // Might need to convert to a Bignum if the
      // next term is a BigNum, or if the product will
      // overflow the Long value.
      //
      //   a * b:
      //     let (a * b) => X.  If (X / a) == b, okay, else overflow.

      if (allIntegers)
      {
        if (term instanceof LispBignum)
        {
          LispBignum bn_val = f_lisp.makeBignum(l_product);
          return bn_val.multiply(ptr);
        }

        l_term = ((LispInteger)term).getLongValue();

        // May need to convert to a bignum.
        // if ((l_result != 0) && ((l_result / l_term) != l_product))
        if ((Long.MAX_VALUE / l_product) < l_term)
        {
          // Need to convert to bignum
          //System.out.println("Converting " + l_product + " to bignum and multiplying by " + ptr);
          LispBignum bn_val = f_lisp.makeBignum(l_product);
          return bn_val.multiply(ptr);
        }
        else
          l_result = l_product * l_term;
      }

      if (allIntegers)
        l_product =  l_result;
      else
        if (term.basic_floatp())
          d_product *=  ((LispReal)term).getDoubleValue();
        else if (term.bignump() == f_lisp.T)
          d_product *= ((LispBignum)term).getDoubleValue();
        else
          d_product *= ((LispInteger)term).getLongValue();

      ptr = ptr.cdr();
    }

    if (allIntegers)
      return(f_lisp.makeInteger(l_product));
    else if (d_product == (long)d_product)
      return(f_lisp.makeInteger((long)d_product));
    else
      return(f_lisp.makeReal(d_product));
  }


  /**
   * SUBTRACT adds any combination of real or integer numbers.
   *
   * @see LispReal
   * @see LispInteger
   */
  public LispValue     subtract    (LispValue  args)
  {
    // The list of numbers has already been evaluated.
    // Terminate if we hit any non-numbers.
    // Keep the sum in a Long value until the value
    // either overflows, in which case we turn it into
    // a bignum, or else a real value is added, in which
    // case the result is a double.

    double    d_sum  = - this.getDoubleValue();
    long      l_sum  = 0;
    long      l_addend = 0;
    LispValue addend, arglist;
    boolean   allIntegers = this.basic_integerp();
    int       argCount = 1;

    //System.out.println("LispNumber.subtract: this = " + this + ", args = " + args);
    //todo: StandardLispNumber.subtract: this won't work with BigNums
    if (allIntegers)
      l_sum = - this.getLongValue();

    // Make sure the argument is a list of numbers.
    arglist = args;
    if (! (arglist instanceof LispConsOrNil))
      arglist = f_lisp.makeList(arglist);

    while (arglist != f_lisp.NIL)
    {
      addend = arglist.car();
      if (! addend.basic_numberp())
      {
        super.subtract(arglist.car());  // generates an error.
        return(f_lisp.NIL);
      }

      // Might need to convert to a double
      if (allIntegers && addend.basic_floatp())
      {
        allIntegers = false;
        d_sum = l_sum;
      }

      ++argCount;

      if (argCount == 2)
      {
        l_sum = - l_sum;
        d_sum = - d_sum;
      }

      // Might need to convert to a Bignum
      //
      //   a - b:
      //     a>0, b<0  if (HI + b) < a  ==> use Bignum
      //     a<0, b>0  if (LO + b) > a  ==> use Bignum

      if (allIntegers)
      {
        if (addend instanceof LispBignum)
        {
          // System.out.println("Bignum arg (sub)...converting " + l_sum + " to bignum.");
          LispBignum bn_val = f_lisp.makeBignum(l_sum);
          return bn_val.subtract(arglist);
        }

        l_addend = ((LispInteger)addend).getLongValue();

        if ((l_sum > 0) && (l_addend < 0))
        {
          if ((Long.MAX_VALUE + l_addend) < l_sum)
          // Need to convert to bignum
          {
            // System.out.println("Converting " + l_sum + " to bignum.");
            LispBignum bn_val = f_lisp.makeBignum(l_sum);
            return bn_val.subtract(arglist);
          }
        }
        else if ((l_sum < 0) && (l_addend > 0))
        {
          if ((Long.MIN_VALUE + l_addend) > l_sum)
          {
            // Need to convert to bignum
            // System.out.println("Converting " + l_sum + " to bignum.");
            LispBignum bn_val = f_lisp.makeBignum(l_sum);
            return bn_val.subtract(arglist);
          }
        }
      }

      // If not allIntegers, result is a double.

      if (allIntegers)
        l_sum -= l_addend;
      else
        if (addend.basic_floatp())
          d_sum -= ((LispReal)addend).getDoubleValue();
        else if (addend.bignump() == f_lisp.T)
          d_sum -= ((LispBignum)addend).getDoubleValue();
        else
          d_sum -= ((LispInteger)addend).getLongValue();

      arglist = arglist.cdr();
    };

    if (allIntegers)
      return(f_lisp.makeInteger(l_sum));
    else
      return(f_lisp.makeReal(d_sum));
  }


  /**
   * Arccos function with result in radians.
   * Also called Inverse Cosine, this is the
   * angle whose cosine is the argument.
   */
  public LispValue acos()
  {
    return new StandardLispReal(f_lisp, StrictMath.acos(getDoubleValue()));
  }


  /**
   * Arcsin function with result in radians.
   * Also called Inverse Sine, this is the
   * angle whose sine is the argument.
   */
  public LispValue asin()
  {
    return new StandardLispReal(f_lisp, StrictMath.asin(getDoubleValue()));
  }


  /**
   * Arctan function with result in radians.
   * Also called Inverse Tangent, this is the
   * angle whose tangent is the argument.
   */
  public LispValue atan()
  {
    return new StandardLispReal(f_lisp, StrictMath.atan(getDoubleValue()));
  }


  /**
   * Two-argument Arctan function with result in radians
   * Also called Inverse Tangent, this is the
   * angle whose tangent is y/x, where y is
   * the first argument and x is the second argument.
   */
  public LispValue atan2(LispValue x)
  {
    if (x instanceof LispNumber)
      return new StandardLispReal(f_lisp, StrictMath.atan2(getDoubleValue(), ((LispNumber)x).getDoubleValue()));
    else
      throw new LispValueNotANumberException("The second argument to atan2 (" + x + ")");
  }

  /**
   * Returns the smallest integer greater than or equal to the input value.
   */
  public LispValue ceiling()
  {
    if (this instanceof LispInteger)
      return this;
    else if (this.getDoubleValue() > Long.MAX_VALUE)
      return f_lisp.makeBignum(StrictMath.floor(getDoubleValue()));
    else
      return f_lisp.makeInteger((long)StrictMath.ceil(getDoubleValue()));
  }


  /**
   * Cosine function, argument in radians.
   */
  public LispValue cos()
  {
    return new StandardLispReal(f_lisp, StrictMath.cos(getDoubleValue()));
  }

  /**
   * Cotangent function, 1/tan(x), argument in radians.
   */
  public LispValue cot()
  {
    return new StandardLispReal(f_lisp, 1.0 / StrictMath.tan(getDoubleValue()));
  }

  /**
   * Cosecant function, 1/sin(x), argument in radians.
   */
  public LispValue csc()
  {
    return new StandardLispReal(f_lisp, 1.0 / StrictMath.sin(getDoubleValue()));
  }

  /**
   * Returns the largest integer less than or equal to the input value.
   */
  public LispValue floor()
  {
    if (this instanceof LispInteger)
      return this;
    else if (this.getDoubleValue() > Long.MAX_VALUE)
      return f_lisp.makeBignum(StrictMath.floor(getDoubleValue()));
    else
      return f_lisp.makeInteger((long)Math.floor(getDoubleValue()));
  }

  /**
   * Secant function, 1/cos(x), argument in radians.
   */
  public LispValue sec()
  {
    return new StandardLispReal(f_lisp, 1.0 / StrictMath.cos(getDoubleValue()));
  }

  /**
   * Sine trigonometric function, argument is in radians.
   */
  public LispValue sin()
  {
    return new StandardLispReal(f_lisp, StrictMath.sin(getDoubleValue()));
  }

  public LispValue sqrt()
  {
    return new StandardLispReal(f_lisp, StrictMath.sqrt(getDoubleValue()));
  }

  /**
   * Tangent trigonometric function, argument is in radians.
   */
  public LispValue tan()
  {
    return new StandardLispReal(f_lisp, StrictMath.tan(getDoubleValue()));
  }

  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.NUMBER_TYPE))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

  public LispValue greaterThan(LispValue arg)
  {
    if (arg instanceof LispNumber)
      if (this.getDoubleValue() > ((LispNumber)arg).getDoubleValue())
        return f_lisp.T;
      else
        return f_lisp.NIL;
    else
      throw new LispValueNotANumberException("> " + arg);
  }

  public LispValue greaterThanOrEqual(LispValue arg)
  {
    if (arg instanceof LispNumber)
      if (this.getDoubleValue() >= ((LispNumber)arg).getDoubleValue())
        return f_lisp.T;
      else
        return f_lisp.NIL;
    else
      throw new LispValueNotANumberException(">= " + arg);
  }

  public LispValue lessThan(LispValue arg)
  {
    if (arg instanceof LispNumber)
      if (this.getDoubleValue() < ((LispNumber)arg).getDoubleValue())
        return f_lisp.T;
      else
        return f_lisp.NIL;
    else
      throw new LispValueNotANumberException("< " + arg);
  }

  public LispValue lessThanOrEqual(LispValue arg)
  {
    if (arg instanceof LispNumber)
      if (this.getDoubleValue() <= ((LispNumber)arg).getDoubleValue())
        return f_lisp.T;
      else
        return f_lisp.NIL;
    else
      throw new LispValueNotANumberException("<= " + arg);
  }

  public LispValue equalNumeric(LispValue arg)
  {
    //System.err.println("StandardLispNumber.eql: comparing " + this.getDoubleValue() + " to " +
    //                   ((LispNumber)arg).getDoubleValue());

    if (arg instanceof LispNumber)
      if (this.getDoubleValue() == ((LispNumber)arg).getDoubleValue())
        return f_lisp.T;
      else
        return f_lisp.NIL;
    else
      throw new LispValueNotANumberException("= " + arg);
  }

  public LispValue eql(LispValue arg)
  {
    if (arg instanceof LispNumber)
      return equalNumeric(arg);
    else
      return f_lisp.NIL;
  }


}

