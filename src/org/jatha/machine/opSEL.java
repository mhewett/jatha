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
 * opSEL takes a branch in an IF-ELSE expression.
 * It selects code from the C register and, depending on
 * whether the topmost value in the S register is true
 * or not, places that code on the C register and pushes
 * the remaining C register code onto the D register.
 * Pops the S register.
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opSEL extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  public opSEL(Jatha lisp)
  {
    super(lisp, "SEL");
  }


  public void Execute(SECDMachine machine)
  {
    LispValue selector    = machine.S.pop();
    LispValue trueCodeBranch;
    LispValue falseCodeBranch;

    machine.C.pop();                    // Pop the SEL command.
    trueCodeBranch   = machine.C.pop();
    falseCodeBranch  = machine.C.pop();

    machine.D.push(machine.C.value());  // push remaining code.

    if (selector == f_lisp.NIL)
       machine.C.assign(falseCodeBranch);
    else
       machine.C.assign(trueCodeBranch);
  }


  public LispValue grindef(LispValue code, int indentAmount)
  {
    indent(indentAmount);

    System.out.print(functionName);
    f_lisp.NEWLINE.internal_princ(System.out);

    printCode(code.second(), indentAmount + 8);
    f_lisp.NEWLINE.internal_princ(System.out);

    printCode(code.third(), indentAmount + 8);
    f_lisp.NEWLINE.internal_princ(System.out);

    return code.cdr().cdr().cdr();
  }
}
