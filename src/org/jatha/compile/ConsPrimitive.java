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




public class ConsPrimitive extends LispPrimitive
{
  public ConsPrimitive(Jatha lisp)
  {
    super(lisp, "CONS", 2);
  }

  public void Execute(SECDMachine machine)
  {
    // Common LISP requires L->R evaluation of args.
    // So the first arg is on the bottom of the stack. */
    LispValue arg2 = machine.S.pop();
    LispValue arg1 = machine.S.pop();

    machine.S.push(f_lisp.makeCons(arg1, arg2));
    machine.C.pop();
  }
}