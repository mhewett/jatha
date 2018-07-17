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

/**
 * Implements a Common LISP 'macro' type which represents
 * user-defined macros.
 * <p>
 * This class was contributed by Jean-Pierre Gaillardon, April 2005
 * </p>
 */
public class StandardLispMacro extends StandardLispFunction implements LispMacro
{

  public StandardLispMacro(Jatha f_lisp, LispValue symbol, LispValue value)
  {
    super(f_lisp, symbol, value);
  }

  public boolean basic_functionp() { return false; }
  public boolean basic_macrop()    { return true; }

  public String toString()
  {
    LispValue aPackage = f_lisp.findPackage("LISP");
    String aSymbol     = (getSymbol() == null) ? "anonymous" : getSymbol().toStringSimple();

    if (getSymbol() != null)
      aPackage = getSymbol().symbol_package();

    return "#<standardMacro " + aPackage.toString() + " " + aSymbol + ">";
  }


}
