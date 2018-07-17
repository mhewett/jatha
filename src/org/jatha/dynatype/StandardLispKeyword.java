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

// See LispValue.java for documentation



//------------------------------  LispKeyword  ------------------------------

/**
 * A keyword is a constant (symbol) which evaluates to itself.
 */
public class StandardLispKeyword extends StandardLispConstant implements LispKeyword
{
  public StandardLispKeyword()
  {
    super();
  }

  public StandardLispKeyword(Jatha lisp, String symbolName)
  {
    super(lisp, symbolName);
    f_value = this;
    setExternal(true);   // All keywords are external in the keyword package
  }

  public StandardLispKeyword(Jatha lisp, LispString symbolNameString)
  {
    super(lisp, symbolNameString);
    f_value = this;
    setExternal(true);   // All keywords are external in the keyword package
  }


  // ------  BASIC (non-LISP) methods  --------

  public boolean basic_constantp()  { return true; }
  public boolean basic_keywordp()   { return true; }


  /**
   * Returns a String containing the printed equivalent of this keyword.
   */
  public Object toJava()
  {
    return ":" + f_name.getValue();
  }

  // Keywords need special handling over regular symbols.
  public String toString()
  {
    String pkg = ":";

    if (f_mixedCase)
      return pkg + "|" + f_name.getValue() + "|";
    else
      return pkg + f_name.getValue();
  }

  public String toStringSimple()
  {
    return f_name.toStringSimple();
  }


  // ------  LISP methods  --------

  public LispValue     keywordp     ()  { return f_lisp.T; }

  public LispValue setf_symbol_value(LispValue newValue)
  {
    throw new LispConstantRedefinedException(f_name.getValue());
  }

};

