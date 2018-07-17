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


// @date    Sat Feb  1 22:16:03 1997
/**
 * opLD_GLOBAL pushes onto the stack the value of a global variable.
 * Handles special (dynamically-bound) variables correctly.
 * Uses C register (2 values).
 * Modifes register S
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opLD_GLOBAL extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  //@author  Micheal S. Hewett    hewett@cs.stanford.edu
  public opLD_GLOBAL(Jatha lisp)
  {
    super(lisp, "LD_GLOBAL");
  }


  public void Execute(SECDMachine machine)
  {
    LispValue symb = machine.C.value().second();

    machine.S.push(machine.get_special_value(symb));

    machine.C.pop(); // opcode
    machine.C.pop(); // symbol
  }


  public LispValue grindef(LispValue code, int indentAmount)
  {
    indent(indentAmount);

    System.out.print(functionName);
    indent(2);
    code.second().internal_princ(System.out);

    f_lisp.NEWLINE.internal_princ(System.out);

    return code.cdr().cdr();
  }
}
