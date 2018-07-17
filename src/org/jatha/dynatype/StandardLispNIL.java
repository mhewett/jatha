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
 *Þ
 * For further information, please contact Micheal Hewett at
 *   hewett@cs.stanford.edu
 *
 */
package org.jatha.dynatype;

import java.io.PrintStream;

import org.jatha.Jatha;


// See LispValue.java for documentation



//--------------------------------  LispNil  --------------------------------
//
// This is used only for NIL.  Since NIL is both a degenerate list and
// a symbol, it causes some programming difficulties.  Using this resolves
// those problems.

public class StandardLispNIL extends StandardLispConsOrNil implements LispNil
{
  private  LispString name;
  private  LispValue  value;
  private  LispValue  plist;
  private  LispValue  function = null;
  private  LispValue  pack = null;   // its package

  public StandardLispNIL()
  {
    super();
  }

  public StandardLispNIL(Jatha lisp, String symbolName)
  {
    super(lisp);
    name = new StandardLispString(lisp, symbolName);
    value = this;
    plist = this;
  }

  public StandardLispNIL(Jatha lisp, LispString symbolNameString)
  {
    super(lisp);
    name  = symbolNameString;
    value = this;
    plist = this;
  }


  public void internal_princ(PrintStream os)        { os.print("NIL"); }
  public void internal_princ_as_cdr(PrintStream os) { /* Do Nothing */ }

  public void internal_prin1(PrintStream os)        { os.print("NIL"); }
  public void internal_prin1_as_cdr(PrintStream os) { /* Do Nothing */ }

  public void internal_print(PrintStream os)        { os.print("NIL"); }
  public void internal_print_as_cdr(PrintStream os) { /* Do Nothing */ }

  // Prints information for the APROPOS function
  // Same implementation as for LispSymbol.
  public void apropos_print(PrintStream out)
  {
    internal_print(out);
    // Print at least one space;
    out.print(' ');
    // TAB over
    for (int i=0; i<((Jatha.APROPOS_TAB - name.getValue().length()) - 1); ++i)
      out.print(' ');

    // If symbol has a function, describe it:
    if (function != null)
    {
      if (function.basic_macrop())
        out.print("[macro] ");
      else
        out.print("[function] ");
    }

    // If symbol has a value, print it:
    if (value != null)
    {
      out.print("value: ");
      value.internal_prin1(out);
    }
    out.println();
  }

  // contributed by Jean-Pierre Gaillardon, April 2005
  public LispValue constantp()
  {
    return f_lisp.T;
  }

  public String toString()      { return "NIL"; }
  public String toStringAsCdr() { return ""; }


  public boolean basic_atom()      { return true;  }
  public boolean basic_consp()     { return false; }
  public boolean basic_constantp() { return true;  }
  public int     basic_length()    { return 0;     }
  public boolean basic_symbolp()   { return true;  }
  public boolean basic_null()      { return true;  }


  // Packages
  public void setPackage(LispPackage newPackage)
  {
    if (pack == null)    // Can only have one home package
      pack = newPackage;
  }

/* ------------------  LISP functions   ------------------------------ */


  public LispValue     append(LispValue otherList) { return otherList; }
  public LispValue     assoc(LispValue index)      { return f_lisp.NIL; }
  public LispValue     atom()                      { return f_lisp.T;   }
  public LispValue     boundp()                    { return f_lisp.T;   }
  public LispValue     butlast()                   { return f_lisp.NIL; }
  public LispValue     car()                       { return f_lisp.NIL; }
  public LispValue     cdr()                       { return f_lisp.NIL; }
  public LispValue     copy_list()                 { return f_lisp.NIL; }
  public LispValue     elt(LispValue index)        { return f_lisp.NIL; }

  public LispValue     first        ()  { return f_lisp.NIL; }
  public LispValue     second       ()  { return f_lisp.NIL; }
  public LispValue     third        ()  { return f_lisp.NIL; }
  public LispValue     fourth       ()  { return f_lisp.NIL; }
  public LispValue     fifth        ()  { return f_lisp.NIL; }
  public LispValue     sixth        ()  { return f_lisp.NIL; }
  public LispValue     seventh      ()  { return f_lisp.NIL; }
  public LispValue     eighth       ()  { return f_lisp.NIL; }
  public LispValue     ninth        ()  { return f_lisp.NIL; }
  public LispValue     tenth        ()  { return f_lisp.NIL; }

  public LispValue     last()                    { return f_lisp.NIL; }
  public LispValue     length()                  { return new StandardLispInteger(f_lisp, 0);}
  public LispValue     member(LispValue elt)     { return f_lisp.NIL; }
  public LispValue     nreverse(LispValue index) { return f_lisp.NIL; }

  public LispValue     lisp_null    ()           { return f_lisp.T;   }
  public LispValue     rassoc(LispValue index)   { return f_lisp.NIL; }
  public LispValue     remove(LispValue elt)     { return f_lisp.NIL; }
  public LispValue     rest()                    { return f_lisp.NIL; }
  public LispValue     reverse(LispValue elt)    { return f_lisp.NIL; }
  public LispValue     rplaca(LispValue  newCar) { return f_lisp.NIL; }
  public LispValue     rplacd(LispValue  newCdr) { return f_lisp.NIL; }

  public LispValue setf_symbol_function(LispValue newFunction)
  {
    throw new LispConstantRedefinedException(name.getValue());
  }

  public LispValue     setf_symbol_value(LispValue newValue)
  {
    if (value == null)
      return value = newValue;
    else
      throw new LispConstantRedefinedException(name.getValue());
  }

  public LispValue     symbol_name()    { return name;  }
  public LispValue     symbol_package() { return pack;  }
  public LispValue     symbol_plist()   { return plist; }
  public LispValue     symbol_value()   { return value; }

  public LispValue     symbolp()                 { return f_lisp.T;   }

  public LispValue     subst(LispValue oldValue, LispValue newValue)
  { return f_lisp.NIL; }

  public LispValue     type_of     ()  { return f_lisp.NULL_TYPE;   }
  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.NULL_TYPE))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }



};


