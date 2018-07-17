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



//------------------------------  LispInteger  -------------------------------

public class StandardLispInteger extends StandardLispNumber implements LispInteger
{
  private  long f_value;

  // ---  static initializer  ---

  // ---  Constructors  ---

  public StandardLispInteger()
  {
    super();
  }

  public StandardLispInteger(Jatha lisp, long theValue)
  {
    super(lisp);
    f_value = theValue;
  }


  public StandardLispInteger(Jatha lisp)
  {
    super(lisp);
    f_value = 0L;
  }

  // ---  non-LISP methods  ---
  public long    getValue()            { return f_value; }
  public double  getDoubleValue()      { return f_value; }

  public BigInteger getBigIntegerValue()
  {
    return BigInteger.valueOf(f_value);
  }

  public long getLongValue()
  {
    return f_value;
  }

  public void    internal_princ(PrintStream os) { os.print(f_value); }
  public void    internal_prin1(PrintStream os) { os.print(f_value); }
  public void    internal_print(PrintStream os) { os.print(f_value); }
  public boolean basic_integerp()      { return true; }

  /**
   * Returns a Java Long object with the value of this integer.
   */
  public Object toJava()
  {
    return new Long(f_value);
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
       return new Double(getDoubleValue());

    else if (typeHint.equalsIgnoreCase("Float"))
      return new Float(f_value);

    else if (typeHint.equalsIgnoreCase("Integer"))
      return new Integer((int)f_value);

    else
      return toJava();
  }

  public String  toString() { return String.valueOf(f_value); }


  // ---  LISP methods  ---
 /**
   * Integer implementation of abs.
   */
  public LispValue abs()
  {
    if (this.getLongValue() > 0)
      return this;
    else
      return new StandardLispInteger(f_lisp, this.getLongValue() * -1);
  }


  public LispValue eql(LispValue val)
  {
    if (val instanceof LispInteger)
    {
      //System.err.println("StandardLispInteger.eql: comparing " + this.f_value + " to " +
      //                   ((LispInteger)val).getLongValue());
      if (this.f_value == ((LispInteger)val).getLongValue())
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

  public LispValue integerp  ()  { return f_lisp.T; }

  public LispValue type_of   ()  { return f_lisp.INTEGER_TYPE;   }
  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.INTEGER_TYPE))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

  public LispValue zerop     ()
  {
    if (f_value == 0)
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

};
