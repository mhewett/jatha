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


// @date    Thu Mar 27 13:26:07 1997
/**
 * LispAtom defines the ATOM type in Common LISP.
 * Functionally it only defines the <tt>atom()</tt> predicate.
 * But hierarchically it is the super class of all the
 * atom types.
 *
 * @see LispValue
 * @see LispSymbol
 * @see LispInteger
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
abstract public class StandardLispAtom extends StandardLispValue implements LispAtom
{

  public StandardLispAtom()
  {
    super();
  }

  public StandardLispAtom(Jatha lisp)
  {
    super(lisp);
  }

  public boolean basic_atom()  { return true; }

  public LispValue atom()      { return f_lisp.T; }

  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.ATOM_TYPE))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }
}
