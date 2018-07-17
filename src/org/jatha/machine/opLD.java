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
 * opLD pushes the value of a local variable onto
 * the S register and removes two values from the C register.
 * The values on the C register are the constant 'LD'
 * and the pair (i, j), which are indices into the
 * value of the E register which is a list of lists.
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opLD extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  //@author  Micheal S. Hewett    hewett@cs.stanford.edu
  public opLD(Jatha lisp)
  {
    super(lisp, "LD");
  }


  public void Execute(SECDMachine machine)
  {
    LispValue indexes;

    indexes = machine.C.value().second();
    /*
    System.err.println("----DEBUG----");
    System.err.println(machine.E.value());
    System.err.println(indexes);
    System.err.println(getComponentAt(indexes,machine.E.value()));
    System.err.println("-------------");
    */
    /*
       print(CREATE_SYMBOL("---DEBUG----"));
       print(indexes);
       print(symbol_value(E));
       push(print(getComponentAt(indexes, symbol_value(E))), S);
       print(CREATE_SYMBOL("-----------"));
       */

    machine.S.push(getComponentAt(indexes, machine.E.value()));

    machine.C.pop();
    machine.C.pop();
  }


  public LispValue grindef(LispValue code, int indentAmount)
  {
    indent(indentAmount);

    System.out.print(functionName);
    indent(6);
    code.second().internal_princ(System.out);

    f_lisp.NEWLINE.internal_princ(System.out);

    return code.cdr().cdr();
  }
}
