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

import java.io.PrintStream;
import java.math.BigInteger;

import org.jatha.Jatha;



//------------------------------  LispBignum  -------------------------------

/**
 * Implements BigNums - large integers.
 */
public class StandardLispBignum extends StandardLispInteger implements LispBignum
{

  private  BigInteger value;

  // ---  static initializer  ---

  // ---  Constructors  ---

  public StandardLispBignum()
  {
    super();
  }

  public StandardLispBignum(Jatha lisp, BigInteger  theValue)
  {
    super(lisp);
    value = theValue;
  }

  public StandardLispBignum(Jatha lisp, long   theValue)
  {
    super(lisp);
    value = BigInteger.valueOf(theValue);
  }


  public StandardLispBignum(Jatha lisp, double theValue)
  {
    super(lisp);
    value = BigInteger.valueOf((long)theValue);
  }


  public StandardLispBignum(Jatha lisp)
  {
    super(lisp);
    value = ZERO;
  }

  public double  getDoubleValue()
  {
    return value.doubleValue();
  }

  public BigInteger getBigIntegerValue()
  {
    return value;
  }

  public long getLongValue()
  {
    return value.longValue();
  }

  public void    internal_princ(PrintStream os) { os.print(value); }
  public void    internal_prin1(PrintStream os) { os.print(value); }
  public void    internal_print(PrintStream os) { os.print(value); }
  public boolean basic_bignump()   { return true; }
  public boolean basic_integerp()  { return true; }


  /**
   * Returns a Java Double, Float or Integer object,
   * depending on the typeHint.
   */
  public Object toJava(String typeHint)
  {
    if (typeHint == null)
      return toJava();

    else if (typeHint.equalsIgnoreCase("Double"))
      return new Double(getDoubleValue());

    else if (typeHint.equalsIgnoreCase("Float"))
      return new Float(getDoubleValue());

    else if (typeHint.equalsIgnoreCase("Integer"))
      return new Integer((int)(getLongValue()));

    else if (typeHint.equalsIgnoreCase("Long"))
      return new Long(getLongValue());

    else
      return value;
  }


  public String  toString() { return value.toString(); }


  // ---  LISP methods  ---

  /**
   * Bignum implementation of abs.
   */
  public LispValue abs()
  {
    if (this.value.signum() > 0)
      return this;
    else
      return new StandardLispBignum(f_lisp, this.value.negate());
  }


  public LispValue eql(LispValue val)
  {
    if (val instanceof LispBignum)
      if (value.equals(((LispBignum)val).getBigIntegerValue()))
        return f_lisp.T;
      else
        return f_lisp.NIL;

    if (val instanceof LispInteger)
    {
      LispBignum n = new StandardLispBignum(f_lisp, ((LispInteger)val).getLongValue());
      if (value.equals(n.getBigIntegerValue()))
        return f_lisp.T;
      else
        return f_lisp.NIL;
    }

    if (val instanceof LispReal)
    {
      LispReal r = ((LispReal)val);

      if (StrictMath.round(r.getDoubleValue()) != r.getDoubleValue())  // If not integral
        return f_lisp.NIL;

      LispBignum n = new StandardLispBignum(f_lisp, r.getDoubleValue());
      if (value.equals(n.getBigIntegerValue()))
        return f_lisp.T;
      else
        return f_lisp.NIL;
    }


    return super.eql(val);
  }


  public LispValue equal(LispValue val)
  {
    return eql(val);
  }


  public LispValue     add         (LispValue  args)
  {
    // Args is a list of numbers that has already been evaluated.
    // Terminate if we hit any non-numbers.
    BigInteger sum = this.value;
    LispValue addend;

    while (args != f_lisp.NIL)
    {
      // System.out.println("LispBignum.add: " + sum + " and " + args);
      addend = args.car();
      if (addend.numberp() != f_lisp.T)
      {
        this.add(args.car());
        return(f_lisp.NIL);
      }

      // If an addend is a float, we need to convert the
      // pending result to a LispReal and add the rest of
      // the numbers as reals.
      if (addend.floatp() == f_lisp.T)
      {
        // Do we print a warning?
        LispReal realValue = f_lisp.makeReal(this.getDoubleValue());
        return realValue.add(args);
      }

      if (addend instanceof LispBignum)
        sum = sum.add(((LispBignum)addend).getBigIntegerValue());
      else // must be an integer
        sum = sum.add(BigInteger.valueOf(((LispInteger)addend).getLongValue()));

      args = args.cdr();
    };

    if ((sum.compareTo(MAXINT) <= 0)    // If LispInteger size...
            && (sum.compareTo(MININT) >= 0))
      return (f_lisp.makeInteger(sum.longValue()));
    else
      return (f_lisp.makeBignum(sum));
  }


  // @author  Micheal S. Hewett    hewett@cs.stanford.edu
  /**
   * DIVIDE adds any combination of real or integer numbers.
   *
   * @see LispReal
   * @see LispInteger
   *
   */
  public LispValue     divide      (LispValue   args)
  {
    BigInteger quotient    = this.getBigIntegerValue();
    BigInteger quotientAndRem[];
    BigInteger term_value;
    LispValue  term;
    int        argCount     = 1;

    while (args != f_lisp.NIL)
    {
      term = args.car();             /* Arglist is already evaluated. */
      if (term.numberp() != f_lisp.T)
      {
        this.divide(args.car());  // generate error
        return(f_lisp.NIL);
      }

      ++argCount;

      // If a term is a float, we need to convert the
      // pending result to a LispReal and divide the rest of
      // the numbers as reals.
      if (term.floatp() == f_lisp.T)
      {
        // Do we print a warning?
        LispReal realValue = f_lisp.makeReal(quotient.doubleValue());
        return realValue.divide(args);
      }


      // Do integer divide and check result.
      if (! (term instanceof LispBignum))
        term = new StandardLispBignum(f_lisp, ((LispInteger)term).getLongValue());

      term_value = ((LispBignum)term).getBigIntegerValue();

      if (term_value.compareTo(ZERO) != 0)
      {
        quotientAndRem = quotient.divideAndRemainder(term_value);
        if (quotientAndRem[1].compareTo(ZERO) != 0)
        {
          // Won't divide evenly, so convert to a real.
          return f_lisp.makeReal(quotient.doubleValue()).divide(args);
        }
        else
          quotient = quotientAndRem[0];
      }
      else
      {
        System.out.print("\n;; *** ERROR: Attempt to divide by 0.\n");
        return(f_lisp.NIL);
      }

      args = args.cdr();
    }

    if (argCount == 1)           /* Have to handle n-arg differently from 1-arg */
      if (quotient.compareTo(ZERO) != 0)
        return (f_lisp.makeReal(1.0 / quotient.doubleValue()));
      else
      {
        System.out.print("\n;; *** ERROR: Attempt to divide by 0.\n");
        return(f_lisp.NIL);
      }


    if ((quotient.compareTo(MAXINT) <= 0)    // If LispInteger size...
            && (quotient.compareTo(MININT) >= 0))
      return (f_lisp.makeInteger(quotient.longValue()));
    else
      return (f_lisp.makeBignum(quotient));
  }


  // @author  Micheal S. Hewett    hewett@cs.stanford.edu
    // 20050520 - fixed an error if args is not a list, but a single value instead.
  /**
   * MULTIPLY adds any combination of real or integer numbers.
   *
   * @see LispReal
   * @see LispInteger
   */
  public LispValue     multiply    (LispValue  args)
  {
    BigInteger product     = this.getBigIntegerValue();
    LispValue  term,arglist;

    // Make sure the argument is a list of numbers.
    arglist = args;
    if (! (arglist instanceof LispConsOrNil))
      arglist = f_lisp.makeList(arglist);

    while (arglist != f_lisp.NIL)
    {
      term = arglist.car();

      // Generate an error if the multiplicand is not a number.
      if (term.numberp() != f_lisp.T)
      {
        super.multiply(arglist.car());  // generates an error
        return(f_lisp.NIL);
      }

      // If a term is a float, we need to convert the
      // pending result to a LispReal and multiply the rest of
      // the numbers as reals.
      if (term.floatp() == f_lisp.T)
      {
        // Do we print a warning?
        LispReal realValue = f_lisp.makeReal(this.getDoubleValue());
        return realValue.multiply(arglist);
      }

      if (term instanceof LispBignum)
      {
        //System.out.print("Bignum: multiplying " + this + " by Bignum " + ((LispBignum)term).getBigIntegerValue());
        product = product.multiply(((LispBignum)term).getBigIntegerValue());
        //System.out.println(", producing " + product);
      }
      else // (term instanceof LispInteger)
      {
        //System.out.print("Bignum: multiplying " + this + " by Integer " + ((LispInteger)term).getLongValue());
        product = product.multiply(BigInteger.valueOf(((LispInteger)term).getLongValue()));
        //System.out.println(", producing " + product);
      }

      arglist = arglist.cdr();
    };

    if ((product.compareTo(MAXINT) <= 0)    // If LispInteger size...
            && (product.compareTo(MININT) >= 0))
      return (f_lisp.makeInteger(product.longValue()));
    else
      return (f_lisp.makeBignum(product));
  }


  // @author  Micheal S. Hewett    hewett@cs.stanford.edu
  /**
   * SUBTRACT adds any combination of real or integer numbers.
   *
   * @see LispReal
   * @see LispInteger
   */
  public LispValue     subtract         (LispValue  args)
  {
    // Args is a list of numbers that has already been evaluated.
    // Terminate if we hit any non-numbers.
    BigInteger sum = this.value.negate();
    LispValue term;
    int       argCount = 1;

    while (args != f_lisp.NIL)
    {
      // System.out.println("LispBignum.subtract: " + sum + " and " + args);
      term = args.car();
      if (term.numberp() != f_lisp.T)
      {
        this.subtract(args.car());  // generate error
        return(f_lisp.NIL);
      }

      // If a term is a float, we need to convert the
      // pending result to a LispReal and add the rest of
      // the numbers as reals.
      if (term.floatp() == f_lisp.T)
      {
        // Do we print a warning?
        LispReal realValue = f_lisp.makeReal(this.getDoubleValue());
        return realValue.subtract(args);
      }

      ++argCount;

      if (argCount == 2)
        sum = sum.negate();

      if (term instanceof LispBignum)
        sum = sum.subtract(((LispBignum)term).getBigIntegerValue());
      else // (term instanceof LispInteger)
        sum = sum.subtract(BigInteger.valueOf(((LispInteger)term).getLongValue()));

      args = args.cdr();
    };


    if ((sum.compareTo(MAXINT) <= 0)    // If LispInteger size...
            && (sum.compareTo(MININT) >= 0))
      return (f_lisp.makeInteger(sum.longValue()));
    else
      return (f_lisp.makeBignum(sum));
  }



  public LispValue bignump   ()  { return f_lisp.T; }
  public LispValue integerp  ()  { return f_lisp.T; }

  public LispValue negate()
  {
    return new StandardLispBignum(f_lisp, value.negate());
  }
  

  public LispValue type_of   ()  { return f_lisp.BIGNUM_TYPE;   }
  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.BIGNUM_TYPE))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

  public LispValue zerop     ()
  {
    if (value.equals(ZERO))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

}
