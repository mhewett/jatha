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

package org.jatha.compile;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;


public class SetPrimitive extends LispPrimitive
{
  public SetPrimitive(Jatha lisp)
  {
    super(lisp, "SET", 2);
  }

  public void Execute(SECDMachine machine)
  {
    LispValue arg2 = machine.S.pop();
    LispValue arg1 = machine.S.pop();

    machine.S.push(arg1.setf_symbol_value(arg2));
    machine.C.pop();
  }

  // TODO: Fix SetPrimitive - doesn't handle local vars correctly
  // 13 Dec 2005 (mh)
  // SET is not compiling correctly (noticed by Ola Bini).
  // If the variable being set is a bound variable (i.e. in a LET)
  // the global value of the variable is set instead of the local value.
  // Fixed SETQ, but don't know how to fix SET.

}

