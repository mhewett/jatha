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
public interface LispSymbol extends LispAtom
{
  /**
   * Returns true if the symbol is external in its package.
   */
  boolean externalP();

  /**
   * Sets whether the symbol is external in its package.
   * @param b
   */
  void setExternal(boolean b);


}

