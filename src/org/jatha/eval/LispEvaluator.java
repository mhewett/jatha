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



package org.jatha.eval;

import org.jatha.Jatha;
import org.jatha.dynatype.LispPackage;
import org.jatha.dynatype.LispString;
import org.jatha.dynatype.LispValue;
import org.jatha.dynatype.StandardLispInteger;
import org.jatha.dynatype.StandardLispString;



/**
 * A LISP eval mechanism based on the SECD abstract
 * machine described in "The Architecture of Symbolic
 * Computers" by Peter Kogge.  ISBN 0-07-035596-7
 *
 *  25 Jan 1997 (mh)
 */
public class LispEvaluator
{
  private Jatha f_lisp = null;

  // --------  CONSTRUCTORS  -------------

  public LispEvaluator(Jatha lisp)
  {
    super();

    f_lisp = lisp;
  }

  // --------  non-LISP methods  -------------

  // init() should only be called once for each LISP session.
  // it creates the initial list of global variables, and
  // builds the

  public void init()
  {
    setf_symbol_value(intern("*"),                         f_lisp.NIL);
    setf_symbol_value(intern("**"),                        f_lisp.NIL);
    setf_symbol_value(intern("***"),                       f_lisp.NIL);
    setf_symbol_value(intern("*LISP-TRACE*"),              f_lisp.NIL);
    setf_symbol_value(intern("*COMP-NATIVE-FUNCTIONS*"),   f_lisp.NIL);
    setf_symbol_value(intern("*COMP-SPECIAL-FUNCTIONS*"),  f_lisp.NIL);

    // Declare *PACKAGE* as a global variable.
    setf_symbol_value(intern("*PACKAGE*"),                 f_lisp.PACKAGE);
    intern("*PACKAGE*").set_special(true);

    // ** obsolete
    // globalVars = f_lisp.NIL;
    // globalVarValues = cons(f_lisp.NIL, f_lisp.NIL);
  }


  // --------  LISP methods (alphabetical) -------------


  public LispValue cons(LispValue theCar, LispValue theCdr)
  {
    return f_lisp.makeCons(theCar, theCdr);
  }


  public LispValue intern(LispString symbolString)
  {
    if (f_lisp.COLON.eql(symbolString.basic_elt(0)) != f_lisp.NIL)
      return intern((LispString)(symbolString.substring(new StandardLispInteger(f_lisp, 1))),
                    (LispPackage)(f_lisp.findPackage("KEYWORD")));
    else
      return intern(symbolString, f_lisp.PACKAGE);
  }


  public LispValue intern(LispString symbolString, LispPackage pkg)
  {
    LispValue newSymbol;

    // First, check to see whether one exists already.
    newSymbol = pkg.getSymbol(symbolString);

    if (newSymbol != f_lisp.NIL)    // Already there, don't add it again.
    {
      // System.out.println("Package " + pkg + " already owns " + newSymbol);
      return newSymbol;
    }
    else
    {
      if (pkg == f_lisp.findPackage("KEYWORD"))
      {
        String newString = symbolString.toStringSimple().toUpperCase();
        // Symbols must be uppercase
        newSymbol = f_lisp.makeKeyword(new StandardLispString(f_lisp, newString));
      }
      else
        newSymbol = f_lisp.makeSymbol(symbolString);

      return intern(symbolString, newSymbol, pkg);
    }
  }

  // We need this for the startup when we create f_lisp.NIL and LispValue.T.
  // Actually, LispValue is always a LispSymbol, but because of NIL's strange
  // properties, we must make the type be LispValue.
  public LispValue intern(LispString symbolString, LispValue symbol)
  {
    return intern(symbolString, symbol, f_lisp.PACKAGE);
  }

  // We need this for the startup when we create f_lisp.NIL and LispValue.T.
  // Actually, LispValue is always a LispSymbol, but because of NIL's strange
  // properties, we must make the type be LispValue.
  public LispValue intern(LispString symbolString, LispValue symbol,
                          LispPackage pkg)
  {
    if (pkg == null)   // uninterned symbol
      return symbol;
    else
    {
      symbol.setPackage(pkg);
      return pkg.addSymbol(symbolString, symbol);
    }
  }

  public LispValue intern(String str)
  {
    return intern(f_lisp.makeString(str));
  }

  public LispValue intern(String str, LispPackage pkg)
  {
    return intern(f_lisp.makeString(str), pkg);
  }

/* ------------------     ------------------------------ */

  public LispValue nreverse(LispValue thing)
  {
    return thing.nreverse();
  }

  public LispValue reverse(LispValue thing)
  {
    return thing.reverse();
  }

  public LispValue setf_symbol_function(LispValue symbol, LispValue value)
  {
    return symbol.setf_symbol_function(value);
  }

  public LispValue setf_symbol_plist(LispValue symbol, LispValue value)
  {
    return symbol.setf_symbol_plist(value);
  }

  public LispValue setf_symbol_value(LispValue symbol, LispValue value)
  {
    return symbol.setf_symbol_value(value);
  }

}
