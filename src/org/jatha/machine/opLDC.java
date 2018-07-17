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



// date    Sat Feb  1 22:18:53 1997
/**
 * opLDC pops two values from the C register and
 * pushes the second of those onto the S register.
 * The two values on the C register should be
 * the constant 'LDC' and a value.
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opLDC extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  public opLDC(Jatha lisp)
  {
    super(lisp, "LDC");
  }


  public void Execute(SECDMachine machine)
  {
    machine.S.push(machine.C.value().second());

    machine.C.pop();
    machine.C.pop();
  }


  public LispValue grindef(LispValue code, int indentAmount)
  {
    indent(indentAmount);

    System.out.print(functionName);
    indent(5);
    code.second().internal_princ(System.out);

    f_lisp.NEWLINE.internal_princ(System.out);

    return code.cdr().cdr();
  }
}
