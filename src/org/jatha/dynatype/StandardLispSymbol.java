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

import java.util.Map;
import java.util.HashMap;

import java.io.*;

import org.jatha.read.*;
import org.jatha.Jatha;




//--------------------------------  LispSymbol  ------------------------------

// date    Wed Mar 26 11:09:38 1997
/**
 * LispSymbol implements LISP Symbols, including provisions
 * for special bindings.
 *
 * @see LispValue
 * @see LispAtom
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 * @version 1.0
 *
 */
public class StandardLispSymbol extends StandardLispAtom implements LispSymbol
{

/* ------------------  PRIVATE vars   ------------------------------ */

  // Every LISP symbol has these four components
  protected  LispValue    f_function;     // Function value
  protected  LispString   f_name;           // Print name
  protected  LispValue    f_value;          // Assigned value
  protected  LispValue    f_plist;          // Property list
  protected  LispPackage  f_package;           // The symbol's home package

  protected  boolean    f_isExternalInPackage = false;
  protected  boolean    f_isSpecial = false;  // Special? (dynamically-bound)
  protected  int        f_specialCount = 0;   // Number of special binding nestings

  protected  boolean    f_mixedCase;          // For mixed-case symbols

  protected Map f_documentation;

/* ------------------  CONSTRUCTORS   ------------------------------ */

  public StandardLispSymbol()
  {
    super();
  }

  public StandardLispSymbol(Jatha lisp, String symbolName)
  {
    this(lisp, new StandardLispString(lisp, symbolName));
  }

  // Only 'name' is required to create a symbol.
  public StandardLispSymbol(Jatha lisp, LispString symbolNameString)
  {
    super(lisp);
    f_name       = symbolNameString;
    f_value      = null;              // Default to UNBOUND
    f_function   = null;              // Default to UNBOUND
    f_plist      = lisp.NIL;        // Default to NIL
    f_package       = null;              // Default to no package.

    // If the symbol contains lower-case letters, or anything other than
    // the following set of letters, we need to print OR-bars around it.

    f_mixedCase  = (LispParser.firstCharNotInSet(0, symbolNameString.getValue(),
                                               "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ:*/+-?!.%$<>=_")
            < ((LispInteger)symbolNameString.length()).getLongValue());

    f_documentation = new HashMap();
  }


/* ------------------  BASIC (non-LISP) methods   ------------------------------ */


  public boolean basic_symbolp()   { return true; }

  // 'equals' is required for HashTable.
  public boolean equals(LispSymbol otherSymbol)
  {

    return (this == otherSymbol);   // Same symbols have same address
  }

  /**
   * Returns the Java String representing the symbol's name, without any quoting.
   */
  public String internal_getName()
  {
    return f_name.getValue();
  }

  public void internal_prin1(PrintStream os)
  {
    os.print(toString());
  }

  // PRINC doesn't print the package name.
  public void internal_princ(PrintStream os) { os.print(f_name.getValue()); }

  public void internal_print(PrintStream os)
  {
    os.print(toString());
  }


  // Prints information for the APROPOS function
  public void apropos_print(PrintWriter out)
  {
    String printRep = this.toString();

    out.print(printRep);
    out.print(' ');
    // TAB over
    for (int i=0; i<((Jatha.APROPOS_TAB - printRep.length()) - 1); ++i)
      out.print(' ');

    // If symbol has a function, describe it:
    if (f_function != null)
    {
      if (f_function.basic_macrop())
        out.print("[macro] ");
      else if (f_function.basic_functionp())
        out.print("[function] ");
      else
        out.print("[not macro or function!]");
   }
    else if (f_lisp.getCompiler().specialFormP(this))
      out.print("[special form] ");

    else if (f_lisp.isType(this))
      out.print("[type] ");

    // If symbol has a value, print it:
    if (f_value != null)
    {
      out.print("value: " + f_value);
    }
    out.println();
  }


  /**
   * Returns a Java String containing a printed representation of this symbol.
   */
  public Object toJava()
  {
    return toStringSimple();
  }

  /**
   * Returns this symbol as a string, including the package.
   */
  public String toString()
  {
    String pkg = "";

    // System.err.println("StandardLispSymbol.toString(): pkg = " + f_package + ", PACKAGE=" + f_lisp.PACKAGE);
    if (f_package == null)
      pkg = "#:";
    else if (f_package != f_lisp.PACKAGE)
    {
      if (f_lisp.PACKAGE.uses(f_package))
        pkg = "";
      else if (f_isExternalInPackage)
        pkg = f_package.toString() + ":";
      else
        pkg = f_package.toString() + "::";
    }

    /* Don't worry about OR bars for now.
    // TODO  need *print-case* and *read-case*.
    if (mixedCase)
      return pkg + "|" + name.getValue() + "|";
    else
    */
    return pkg + f_name.getValue();
  }


  /**
   * Strips or-bars from a LispSymbol name.
   * Leaves a colon on the front of keyword symbols.
   */
  public String toStringSimple()
  {
    // if (this instanceof LispKeyword)    // would this work?
    if (f_package == f_lisp.findPackage("KEYWORD"))
      return ":" + f_name.getValue();
    else
      return f_name.getValue();
  }

  //  ****  Handling special (dynamically-bound) variables   *********

  public void set_special(boolean value) { f_isSpecial = value; }

  public boolean specialP() { return f_isSpecial; }

  public void adjustSpecialCount(int amount) { f_specialCount = f_specialCount + amount; }

  public int get_specialCount() { return f_specialCount; }


  // ********   Packages  *********************************

  public void setPackage(LispPackage newPackage)
  {
    if (f_package == null)    // Can only have one home package
      f_package = newPackage;
  }

  public void setExternal(boolean value)
  {
    // We really should verify that the package is set, but
    // we probably can't set this unless it is so we'll assume it is.

    f_isExternalInPackage = value;
  }

  public boolean externalP() { return f_isExternalInPackage; }


/* ------------------  LISP methods   ------------------------------ */

  public LispValue  apply(LispValue args)
  {
    if (f_function == null)
      return super.apply(args);
    else
    {
      System.err.println("\nSorry, APPLY is not yet implemented.");

      return f_lisp.NIL;
    }
  }

  public LispValue  boundp()
  {
    if (f_value == null)
      return f_lisp.NIL;
    else
      return f_lisp.T;
  }

  public LispValue  fboundp()
  {
    if (f_function == null)
      return f_lisp.NIL;
    else
      return f_lisp.T;
  }

  public LispValue  funcall(LispValue args)
  {
    if (f_function == null)
      throw new LispValueNotAFunctionException("The first argument to FUNCALL");
    else
    {
      // push the args back on the stack
      for (LispValue v = args; v != f_lisp.NIL; v = v.cdr())
        f_lisp.MACHINE.S.push(v.car());

      // get the function, and push it on the code stack.
      // Note that we don't do error checking on the number
      // of arguments.  This is bad...
      f_lisp.MACHINE.C.pop();                     // Pop the funcall function off.
      f_lisp.MACHINE.C.push(((LispFunction)f_function).getCode());   // Push the new one on.
    }

    return f_lisp.NIL;
  }

  public LispValue     pop          ()
  {
    LispValue returnValue;

    if (f_value.basic_listp())
    {
      returnValue     = f_value.car();
      setf_symbol_value(f_value.cdr());
      return returnValue;
    }
    else
      throw new LispValueNotAListException("The value of "
                                           + f_name.toStringSimple());
  }

  public LispValue     push         (LispValue newValue)
  {
    if (f_value.basic_listp())
    {
      setf_symbol_value(new StandardLispCons(f_lisp, newValue, f_value));
      return newValue;
    }
    else
      throw new LispValueNotAListException("The value of "
                                           + f_name.toStringSimple());
  }

  // Modified by Jean-Pierre Gaillardon to handle macros, April 2005
  public LispValue setf_symbol_function(LispValue newCode)
  {
    // function or macro
    if (newCode.basic_functionp())
    {
      f_function = newCode;
      return f_function;
    }

    // A macro has the symbol :MACRO as the first element.
    if (f_lisp.getCompiler().isMacroCode(newCode))
    {
      f_function = new StandardLispMacro(f_lisp, this, newCode.cdr());
      return f_function;
    }

    // Else, create a new function.
    f_function = new StandardLispFunction(f_lisp, this, newCode);
    return f_function;
  }

  public LispValue setf_symbol_plist(LispValue newPlist)
  {
    f_plist = newPlist;
    return f_plist;
  }

  public LispValue setf_symbol_value(LispValue newValue)
  {
    f_value = newValue;
    return f_value;
  }

  public LispValue setq(LispValue newValue)
  {
    f_value = newValue;
    return f_value;
  }

  /**
   * Converts a String, Symbol or Character to a string.
   */
  public LispValue string()
  {
    return new StandardLispString(f_lisp, this.toString());
  }

  public LispValue     symbolp() { return f_lisp.T; }

  public LispValue symbol_function() throws LispException
  {
    if (f_function == null)
    {
      throw new LispUndefinedFunctionException(f_name.getValue());
    }
    return f_function;
  }

  public LispValue symbol_name()
  {
    return f_name;
  }

  public LispValue symbol_package()
  {
    if (f_package == null)
      return f_lisp.NIL;
    else
      return f_package;
  }

  public LispValue symbol_plist()
  {
    return f_plist;
  }

  public LispValue symbol_value() throws LispException
  {
    if (f_value == null)
    {
      throw new LispUnboundVariableException(f_name.getValue());
    }
    return f_value;
  }

  public LispValue     type_of     ()  { return f_lisp.SYMBOL_TYPE;   }
  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.SYMBOL_TYPE))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

    public LispValue documentation(final LispValue type) {
        if(!(type instanceof LispSymbol)) {
            throw new LispValueNotASymbolException("The second argument to DOCUMENTATION");
        }
        final LispValue val = (LispValue)f_documentation.get(type);
        return (val == null) ? f_lisp.NIL : val;
    }

    public LispValue setf_documentation(final LispValue type, final LispValue value) {
        if(!(type instanceof LispSymbol)) {
            throw new LispValueNotASymbolException("The second argument to SETF-DOCUMENTATION");
        }
        if(!(value instanceof LispString) && value != f_lisp.NIL) {
            throw new LispValueNotAStringException("The third argument to SETF-DOCUMENTATION");
        }
        f_documentation.put(type,value);
        return value;
    }

};

