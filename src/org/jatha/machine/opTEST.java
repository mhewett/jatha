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
 * opTEST is a variation of SEL that optimizes for recursion
 * in the else part.  It eliminates pushing anything on the Dump.
 * Modifes S register.
 * @see SECDMachine
 * @see opSEL
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 *
 */
class opTEST extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  public opTEST(Jatha lisp)
  {
    super(lisp, "TEST");
  }


  public void Execute(SECDMachine machine)
  {
    LispValue selector    = machine.S.pop();
    LispValue trueValue;

    machine.C.pop();               /* Pop the TEST command. */
    trueValue   = machine.C.pop();

    if (selector != f_lisp.NIL)
       machine.C.assign(trueValue);
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
