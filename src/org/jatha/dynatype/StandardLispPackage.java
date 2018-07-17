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
import org.jatha.util.SymbolTable;


// date    Mon May  5 22:39:01 1997
/**
 * An implementation of ANSI Common LISP packages,
 * including defpackage, export, import, etc.
 *
 * @see org.jatha.Jatha
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 * @version 1.0
 *
 */
public class StandardLispPackage extends StandardLispValue implements LispPackage
{
/* ------------------  FIELDS   ------------------------------ */

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon May  5 22:46:11 1997
  /**
   * SYMTAB is the local symbol table for this package.
   */
  protected SymbolTable f_symbolTable;

  // author Ola Bini, ola.bini@itc.ki.se
  // date Sun May 22 20:27:00 2005
  /**
   * The shadowing symbols of this package. Read CLTL, chapter 11.5 for more information.
   */
  protected SymbolTable f_shadowingSymbols;

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon May  5 22:48:52 1997
  /**
   * The LISP string giving the name of the package.
   */
  protected LispValue f_name;       // A Lisp String
  protected LispValue f_nicknames;  // List of Lisp Strings

  protected LispValue f_uses;    // List of packages used by this one.

/* ------------------  CONSTRUCTORS   ------------------------------ */

  public StandardLispPackage()
  {
    super();
  }


  // return the package
  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon May  5 22:39:01 1997
  /**
   * Creates a new package.  Caller should verify
   * that none exists by the same name before calling
   * this function.
   * @param name - a string giving the name of the new package
   */
  public StandardLispPackage(Jatha lisp, String name)
  {
    this(lisp, new StandardLispString(lisp, name));
  }


  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon May  5 22:39:01 1997
  /**
   * Creates a new package.  Caller should verify
   * that none exists by the same name before calling
   * this function.Þ
   * @param name - a LISP string giving the name of the package
   *
   */
  public StandardLispPackage(Jatha lisp, LispValue name)
  {
    this(lisp, name, null, null, null);
  }

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon May  5 22:43:14 1997
  /**
   * Creates a package with a list of nicknames.
   * @param name - a LISP string giving the name.
   * @param nicknames - a LISP list of nicknames (all strings)
   *
   */
  public StandardLispPackage(Jatha lisp, LispValue name, LispValue nicknames)
  {
    this(lisp, name, nicknames, null, null);
  }

  public StandardLispPackage(Jatha lisp, LispValue name, LispValue nicknames,
                             LispValue usesList)
  {
    this(lisp, name, nicknames, usesList, null);
  }


  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon May  5 22:43:14 1997
  /**
   * Creates a package with a list of nicknames and
   * a list of exported symbols.
   * @param pname a LISP string giving the name.
   * @param pnicknames a LISP list of nicknames (all strings)
   * @param puses a LISP list of exported symbols (all strings)
   * @param symtab a symbol table to use for this package.
   */
  public StandardLispPackage(Jatha lisp, LispValue pname, LispValue pnicknames,
		                 LispValue puses, SymbolTable symtab)
  {
      this(lisp, pname, pnicknames, puses, symtab, null);
  }

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon May  5 22:43:14 1997
  /**
   * Creates a package with a list of nicknames and
   * a list of exported symbols.
   * @param pname a LISP string giving the name.
   * @param pnicknames a LISP list of nicknames (all strings)
   * @param puses a LISP list of exported symbols (all strings)
   * @param symtab a symbol table to use for this package.
   * @param shadows a shadowing symbol table to use for this package.
   *
   */
  public StandardLispPackage(Jatha lisp, LispValue pname, LispValue pnicknames,
		                 LispValue puses, SymbolTable symtab, final SymbolTable shadows)
  {
    super(lisp);

    if (pname == null) {
        f_name = f_lisp.NIL;
    } else if(pname instanceof LispSymbol) {
        f_name = pname.symbol_name();
    } else {
        f_name = pname;
    }



    if (pnicknames == null)
      f_nicknames = f_lisp.NIL;
    else
      f_nicknames  = transformToStrings(pnicknames);

    if (puses == null)
      f_uses = f_lisp.NIL;
    else
      f_uses       = puses;

    if (symtab instanceof SymbolTable)
      f_symbolTable = symtab;
    else
      f_symbolTable = new SymbolTable(f_lisp);

    if (shadows instanceof SymbolTable)
      f_shadowingSymbols = shadows;
    else
      f_shadowingSymbols = new SymbolTable(f_lisp);
  }


  public SymbolTable getSymbolTable()
  {
    return f_symbolTable;
  }

/* ------------------  NON-LISP methods   ------------------------------ */

  public void internal_princ(PrintStream os)
  { os.print("#<The " + ((LispString)(f_name)).getValue() + " package>"); }

  public void internal_prin1(PrintStream os)
  { os.print("#<The " + ((LispString)(f_name)).getValue() + " package>"); }

  public void internal_print(PrintStream os)
  { os.print("#<The " + ((LispString)(f_name)).getValue() + " package>"); }


  public String toString()
  {
      if (f_nicknames != f_lisp.NIL && f_nicknames != null)
      return getAsString(f_nicknames.car()).getValue();
    else
      return ((LispString)(f_name)).getValue();
  }

    private LispValue transformToStrings(final LispValue list) {
        LispValue internal = list;
        LispValue build = f_lisp.NIL;
        while(internal != f_lisp.NIL) {
            build = f_lisp.makeCons(getAsString(internal.car()),build);
            internal = internal.cdr();
        }
        return build.reverse();
    }

    private LispString getAsString(final LispValue inp) {
        return (LispString)((inp.basic_symbolp()) ? inp.symbol_name() : inp);
    }

  // Returns either NIL or the symbol.
  // Searches this package only for external symbols matching
  // the string.
  public LispValue getExternalSymbol(LispString str)
  {
    LispValue symbol = f_symbolTable.get(str);

    if ((symbol != f_lisp.NIL) && ((LispSymbol)symbol).externalP()) {
      return symbol;
    } else
      return f_lisp.NIL;
  }

  /**
   * Stores a new symbol in this package.
   * The lookup key is a LispString giving the print name.
   * The value is a LispSymbol - or LispNil.
   */
  public LispValue addSymbol(LispString name, LispValue symbol)
  {
    f_symbolTable.put(name, symbol);
    return symbol;
  }


  // Returns either NIL or the symbol.
  // Searches this package and recursively searches external
  // symbols of packages used by this package.

  public LispValue getSymbol(LispString str)
  {
    LispValue symbol = f_shadowingSymbols.get(str);

    if (symbol != f_lisp.NIL)
      return symbol;

    symbol = f_symbolTable.get(str);

    if (symbol != f_lisp.NIL)
      return symbol;

    // ELSE - search used packages
    LispValue p = f_uses;
    while (p != f_lisp.NIL)
    {
      symbol = ((LispPackage)(f_lisp.findPackage(p.car()))).getExternalSymbol(str);
      if (symbol != f_lisp.NIL)
	return symbol;
      else
	p = p.cdr();
    }

    // If all fails, return NIL.
    return f_lisp.NIL;
  }

/* ------------------  LISP methods   ------------------------------ */

  // A list might be all strings or all symbols.
  LispValue makeSymbolsFromList(LispValue symbolList)
  {
    if (symbolList == f_lisp.NIL)
      return symbolList;
    else if (symbolList.car().basic_symbolp())
      return f_lisp.makeCons(symbolList.car(),
			 makeSymbolsFromList(symbolList.cdr()));
    else // Assume it's a string
      return f_lisp.makeCons(f_lisp.EVAL.intern(getAsString(symbolList.car()), this),makeSymbolsFromList(symbolList.cdr()));
  }

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Sun May 11 16:25:14 1997
  /**
   * Declares the symbol or symbols as exported symbols.
   * The symbol can be a symbol or list of symbols.
   */
  public LispValue export(LispValue symbols)
  {
    if (!symbols.basic_consp())
      symbols = f_lisp.makeCons(symbols, f_lisp.NIL);

    // For every symbol, declare it external
    LispValue s = symbols;
    while (s != f_lisp.NIL)
    {
        if(s.car() instanceof LispSymbol) {
            ((LispSymbol)(s.car())).setExternal(true); // Should handle error here.
        }

      s = s.cdr();
    }

    // Return T
    return f_lisp.T;
  }


  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Sun May 11 16:25:14 1997
  /**
   * Imports the given symbols into the current package.
   * The symbol can be a symbol or list of symbols.
   * Note: this method name is altered because of a
   * conflict with a Java reserved word.
   */
  public LispValue lisp_import(LispValue symbols)
  {
    if (!symbols.basic_consp())
      symbols = f_lisp.makeCons(symbols, f_lisp.NIL);

    // For every symbol, declare it external
    LispValue  s = symbols;
    LispValue symb;

    while (s != f_lisp.NIL)
    {
      symb = s.car();
      if (getSymbol((LispString)(symb.symbol_name())) == f_lisp.NIL)
	f_symbolTable.put((LispString)(symb.symbol_name()), symb);
      else
	System.err.println(";; * WARNING: Attempt to import " + symb +
			   " conflicts with existing symbol " +
			   getSymbol((LispString)(symb.symbol_name())) + " in " + this.f_name);

      s = s.cdr();
    }

    // Return T
    return f_lisp.T;
  }

  // author  Ola Bini    ola.bini@itc.ki.se
  // date    Sun May 22 20:31:00 2005
  /**
   * Imports the given symbols into the current package shadowing list.
   */
  public LispValue shadowing_import(LispValue symbols) {
    if (!symbols.basic_consp())
      symbols = f_lisp.makeCons(symbols, f_lisp.NIL);

    // For every symbol, declare it external
    LispValue  s = symbols;
    LispValue symb;

    while (s != f_lisp.NIL) {
      symb = s.car();
      f_shadowingSymbols.put((LispString)(symb.symbol_name()), symb);
      s = s.cdr();
    }

    // Return T
    return f_lisp.T;
  }

  // author  Ola Bini    ola.bini@itc.ki.se
  // date    Sun May 22 20:31:00 2005
  public LispValue shadow(LispValue symbols) {
    if (!symbols.basic_consp())
      symbols = f_lisp.makeCons(symbols, f_lisp.NIL);

    LispValue  s = symbols;
    LispValue symb;

    while(s != f_lisp.NIL) {
      symb = s.car();
      final LispString symb_name = (symb.basic_stringp()?((LispString)symb):((LispString)symb.symbol_name()));
      if(getSymbol(symb_name) == f_lisp.NIL) {
          final LispValue symbi = f_lisp.EVAL.intern(symb_name,this);
          f_shadowingSymbols.put(symb_name,symbi);
      } else if(f_shadowingSymbols.get(symb_name) == f_lisp.NIL) {
          f_shadowingSymbols.put(symb_name,getSymbol(symb_name));
      }
      s = s.cdr();
    }

    // Return T
    return f_lisp.T;
  }

  public LispValue type_of() { return f_lisp.PACKAGE_TYPE; }
  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.PACKAGE_TYPE))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

  public LispString getName()
  {
    return (LispString)(f_name);
  }

  /**
   * Returns a list of the nicknames of this package.
   * Each element of the list is a LispString.
   */
  public LispValue getNicknames()
  {
    return f_nicknames;
  }

  /**
   * Sets the nicknames-list
   */
  public void setNicknames(final LispValue nicknames)
  {
    f_nicknames = nicknames;
  }

  public LispValue getUses() {
    return f_uses;
  }

  public void setUses(final LispValue uses) {
    this.f_uses = uses;
  }

  /**
   * Returns true if this package uses the given package
   */
  public boolean uses(final LispValue pkg)
  {
    LispValue p = f_uses;

    while (! p.basic_null())
    {
      if (pkg.eq(f_lisp.findPackage(p.car())) == f_lisp.T)
        return true;
      p = p.cdr();
    }
    return false;
  }

}
