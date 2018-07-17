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
public interface LispPackage extends LispValue
{
  /**
   * Returns the name of this package as a LispString.
   */
  public LispString getName();

  /**
   * Returns a list of the nicknames of this package.
   * Each element of the list is a LispString.
   */
  public LispValue getNicknames();

  /**
   * Returns the external symbol with the given name
   * @param str a LispString corresponding to the symbol.
   */
  LispValue getExternalSymbol(LispString str);

  /**
   * Returns the symbol table for this package.
   */
  SymbolTable getSymbolTable();

  /**
   * Returns the symbol with the given name.
   * @param str
   */
  LispValue getSymbol(LispString str);

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Sun May 11 16:25:14 1997
  /**
   * Declares the symbol or symbols as exported symbols.
   * The symbol can be a symbol or list of symbols.
   */
    public LispValue export(LispValue symbols);

  /**
   * Returns the symbol after it is added to the package.
   * @param name
   * @param symbol
   */
  LispValue addSymbol(LispString name, LispValue symbol);

    /**
     * Returns a list of package names that this package uses. Contains either symbols or strings.
     */
    LispValue getUses();

    /**
     * Sets the uses for this package.
     *
     * @param uses the uses of this package
     */
    void setUses(final LispValue uses);

    void setNicknames(final LispValue nicknames);
}
