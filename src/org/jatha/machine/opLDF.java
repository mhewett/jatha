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
import org.jatha.dynatype.LispValue;



// @date    Sat Feb  1 22:18:53 1997
/**
 * opLDF prepares to execute a non-recursive function.
 * It makes a closure and pushes it on the S register.
 * Uses C register (2 values).
 * Closure is cons(C.pop(), E.value()). 
 * Modifes C, D, E, and S registers.
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opLDF extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  //@author  Micheal S. Hewett    hewett@cs.stanford.edu
  public opLDF(Jatha lisp)
  {
    super(lisp, "LDF");
  }


  public void Execute(SECDMachine machine)
  {
    /* Make a closure and push it on the S Register. */
    machine.C.pop();      // Get rid of 'LDF'
    LispValue code = machine.C.pop();  // Get the new code.

    // todo: should we copy the value of E here?  My notes in book say to.
    //
    machine.S.assign(f_lisp.makeCons(f_lisp.makeCons(code,
                                                     machine.E.value()),
                                     machine.S.value())); 
    // machine.S.push(f_lisp.makeCons(code, machine.E.value()));
  }


  public LispValue grindef(LispValue code, int indentAmount)
  {
    indent(indentAmount);

    System.out.print(functionName);
    f_lisp.NEWLINE.internal_princ(System.out);

    printCode(code.second(), indentAmount + 8);
    f_lisp.NEWLINE.internal_princ(System.out);

    return code.cdr().cdr();
  }
}
