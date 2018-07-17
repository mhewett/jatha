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

package org.jatha.machine;

import org.jatha.Jatha;
import org.jatha.dynatype.*;



// @date    Sat Feb  1 22:18:53 1997
/**
 * opRTN returns from a function call.
 * Replaces the S, E and C registers with the first three
 * values in the D register.
 * Then pushs the topmost value on the old S register on the new S register.
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opRTN extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  public opRTN(Jatha lisp)
  {
    super(lisp, "RTN");
  }


  public void Execute(SECDMachine machine)
  {

    LispValue save = machine.S.pop();

    machine.S.assign(f_lisp.makeCons(save, machine.D.pop()));
    machine.E.assign(machine.D.pop());
    machine.C.assign(machine.D.pop());
  }
}
