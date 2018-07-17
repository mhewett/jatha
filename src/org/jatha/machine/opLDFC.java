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
import org.jatha.dynatype.LispFunction;
import org.jatha.dynatype.LispValue;



// @date    Sat Feb  1 22:18:53 1997
/**
 * LDFC is not in Kogge's book.  I use it to activate functions previously
 * defined by DEFUN.  It is exactly like LDF except that it gets the code
 * from the symbol rather than as the next arg in the C register.
 *
 * Uses C, E, S registers.
 * Modifes S register.
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opLDFC extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  //@author  Micheal S. Hewett    hewett@cs.stanford.edu
  public opLDFC(Jatha lisp)
  {
    super(lisp, "LDFC");
  }


  public void Execute(SECDMachine machine)
  {
    /* Make a closure and push it on the S Register. */
    machine.C.pop();   // pop the LDFC symbol.

    LispValue code = machine.C.pop().symbol_function();
    if (code instanceof LispFunction)
      code = ((LispFunction)code).getCode();

    machine.S.assign(f_lisp.makeCons(f_lisp.makeCons(code, machine.E.value()),
                                     machine.S.value()));
  }


  public LispValue grindef(LispValue code, int indentAmount)
  {
    indent(indentAmount);

    System.out.print(functionName);
    indent(4);
    code.second().internal_princ(System.out);

    f_lisp.NEWLINE.internal_princ(System.out);

    return code.cdr().cdr();
  }
}
