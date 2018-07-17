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



//-------------------------------  LispReal  ---------------------------------

public class StandardLispReal extends StandardLispNumber implements LispReal
{
  private  double f_value;

  public void internal_print(PrintStream os) { os.print(f_value); }
  public void internal_prin1(PrintStream os) { os.print(f_value); }
  public void internal_princ(PrintStream os) { os.print(f_value); }

  public StandardLispReal()
  {
    super();
    f_value = 0.0;
  }

  public StandardLispReal(Jatha lisp, double theValue)
  {
    super(lisp);
    f_value = theValue;
  }

  public StandardLispReal(Jatha lisp)
  {
    super(lisp);
    f_value = 0.0;
  }

  public double getDoubleValue()  { return f_value; }

  public BigInteger getBigIntegerValue()
  {
    return BigInteger.valueOf((long)f_value);
  }

  public long getLongValue()
  {
    return (long)f_value;
  }


  /**
   * Returns a Java Double object with the value of this number.
   */
  public Object toJava()
  {
    return new Double(f_value);
  }


  /**
   * Returns a Java Double, Float or Integer object,
   * depending on the typeHint.
   */
  public Object toJava(String typeHint)
  {
    if (typeHint == null)
      return toJava();

    else if (typeHint.equalsIgnoreCase("Double"))
       return toJava();

    else if (typeHint.equalsIgnoreCase("Float"))
      return new Float(f_value);

    else if (typeHint.equalsIgnoreCase("Integer"))
      return new Integer((int)f_value);

    else if (typeHint.equalsIgnoreCase("Long"))
      return new Long((long)f_value);

    else
      return toJava();
  }


  public String toString() { return String.valueOf(f_value); }

  public boolean basic_floatp() { return true; }

  public LispValue eql(LispValue val)
  {
    if (val instanceof LispReal)
    {
      //System.err.println("StandardLispReal.eql: comparing " + this.f_value + " to " + ((LispReal)val).getDoubleValue());
      if (this.f_value == ((LispReal)val).getDoubleValue())
        return f_lisp.T;
      else
        return f_lisp.NIL;
    }
    else
      return super.eql(val);
  }

  public LispValue equal(LispValue val)
  {
    return eql(val);
  }

  public LispValue floatp   ()  { return f_lisp.T; }

  public LispValue type_of  ()  { return f_lisp.DOUBLE_FLOAT_TYPE;   }
  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.REAL_TYPE) || (type == f_lisp.FLOAT_TYPE) || (type == f_lisp.DOUBLE_FLOAT_TYPE))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

  public LispValue zerop    ()
  {
    if (f_value == 0.0)
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

};

