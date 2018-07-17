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
 * opAP applies a non-recursive function in an evaluation environment.
 * ((f.e') v.s) e (AP.c) d --> NIL (v.e') f (s e c.d)
 * Replaces the C register with the CAR of the topmost value on the S register.
 * Pushes the C, E and S registers onto the D register, in that order.
 * Replaces the E register with a cons of the second value on the S
 * register and the cdr of the first value.
 * Places NIL in the S register.
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opAP extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  public opAP(Jatha lisp)
  {
    super(lisp, "AP");
  }


  public void Execute(SECDMachine machine)
  {

    LispValue fe = machine.S.pop();   /* (f . e) */
    LispValue v  = machine.S.pop();

    /*
      printf("\nAP:   fe = "); print(fe);
      printf("\nAP:   v  = "); print(v);
      */

    LispValue code = fe.car();
    if (code instanceof LispFunction)
      code = ((LispFunction)code).getCode();

    machine.C.pop();   // Get rid of 'AP' opcode.

    machine.D.assign(f_lisp.makeCons(machine.S.value(),
                                     f_lisp.makeCons(machine.E.value(),
                                                     f_lisp.makeCons(machine.C.value(), machine.D.value()))));
    machine.C.assign(code);
    machine.E.assign(f_lisp.makeCons(v, fe.cdr()));
    machine.S.assign(f_lisp.NIL);
  }
}


