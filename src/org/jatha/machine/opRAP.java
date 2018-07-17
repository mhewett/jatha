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
 * opRAP applies a recursive function in an evaluation environment.
 * It pushes the S, E and C registers on to the D register.
 * It pushes the car of the topmost S value onto the C register.
 * ((f.(nil.e)) v.s) (nil.e) (RAP.c) d -->
 *     NIL ((v.e).e) f (s e c.d)
 * Uses S, E, C and D registers.
 * Modifes S, E, C and D registers.
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opRAP extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  public opRAP(Jatha lisp)
  {
    super(lisp, "RAP");
  }


  public void Execute(SECDMachine machine)
  {
    LispValue recursiveClosure = machine.S.pop();  /* (f . (nil.e1)) */
    LispValue v                = machine.S.pop();  /* v = list of closures */

    // machine.E.pop();
    machine.C.pop();


    /*
      System.out.println("\nRAP:   closure = " + recursiveClosure);
      System.out.println("\nRAP:   v       = " + v);
      */



    /*
    machine.D.push(machine.C.pop());
    machine.D.push(machine.E.pop());
    machine.D.push(machine.S.pop());
    */
    /*
    LispValue Evalue = machine.E.value();

    machine.D.assign(f_lisp.makeCons(machine.S.value(),
                                     f_lisp.makeCons(Evalue.cdr(),
                                                     f_lisp.makeCons(machine.C.value(),
                                                                     machine.D.value()))));

                                                                     */
    LispValue e2 = machine.E.value();
    machine.D.assign(f_lisp.makeCons(machine.S.value(),
                                     f_lisp.makeCons(e2.cdr(),
                                                     f_lisp.makeCons(machine.C.value(),
                                                                     machine.D.value()))));


    machine.C.assign(recursiveClosure.car());  /* f */

    // The car of E should be rplaca'd with the list of closures
    //machine.E.assign(f_lisp.makeCons(v, recursiveClosure.cdr().cdr())); //  (v . e1)
    machine.E.value().rplaca(v);

    machine.S.assign(f_lisp.NIL);
  }
}
