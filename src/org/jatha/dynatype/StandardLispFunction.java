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

import java.io.*;

import org.jatha.Jatha;
import org.jatha.compile.LispCompiler;


// date    Mon Feb 24 22:40:45 1997
/**
 * Implements a Common LISP 'function' type which represents
 * built-in or user-defined functions.
 *
 * @see LispValue
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 * @version 1.0
 *
 */
public class StandardLispFunction extends StandardLispValue implements LispFunction
{

  private boolean   f_isBuiltin = false;
  private LispValue f_symbol    = null;
  private LispValue f_code      = null;


/* ------------------  Constructors   ------------------------------ */

  public StandardLispFunction()
  {
    super();
  }


  /**
   * Creates a function with no content - not recommended.
   */
  public StandardLispFunction(Jatha lisp)
  {
    super(lisp);
  }


  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  /**
   * Send in the instance of Jatha and the code for this function.
   */
  public StandardLispFunction(Jatha lisp, LispValue symbol, LispValue code)
  {
    super(lisp);

    f_code   = code;
    f_symbol = symbol;
    f_isBuiltin = LispCompiler.isBuiltinFunction(code);
  }


  public String toString()
  {
    LispValue aPackage = f_lisp.findPackage("LISP");
    String aSymbol     = (f_symbol == null) ? "anonymous" : f_symbol.toStringSimple();

    if (f_symbol != null)
      aPackage = f_symbol.symbol_package();

    return "#<standardFunction " + aPackage.toString() + " " + aSymbol + ">";
  }


/* ------------------  PUBLIC non-LISP functions   ------------------------------ */

  public void    internal_princ(PrintStream os) { os.print(toString()); }
  public void    internal_prin1(PrintStream os) { os.print(toString()); }
  public void    internal_print(PrintStream os) { os.print(toString()); }

  /**
   * Returns a Java HashMap containing the contents of this HashTable.
   */
  public Object toJava()
  {
    if (f_code != null)
      return ((LispCons)f_code.toJava());
    else
      return null;
  }


  public boolean basic_functionp()
  {
    return true;
  }

  /**
   * Gets the code of the function.
   */
  public LispValue getCode()
  {
    return f_code;
  }

  /**
   * Returns the symbol that owns this function.
   */
  public LispValue getSymbol()
  {
    return f_symbol;
  }

  /**
   * Returns true if this is a builtin function.
   */
  public boolean isBuiltin()
  {
    return f_isBuiltin;
  }


/* ------------------  LISP functions   ------------------------------ */

}



