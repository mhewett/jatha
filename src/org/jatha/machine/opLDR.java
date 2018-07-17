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
 *
 */

package org.jatha.machine;

import org.jatha.Jatha;
import org.jatha.dynatype.*;

/**
 * JPG : Not an original jatha opcode 
 * opLDR (op LoaD Rest) has been added to handle user-defined functions with
 * a variable number of args (with &rest keyword in the list of parameters)  
 * 
 * opLDR pushes the value of a local variable onto
 * the S register and removes two values from the C register.
 * The values on the C register are the constant 'LDR'
 * and the pair (i, j), which are indices into the
 * value of the E register which is a list of lists.
 * The j index points the beginning of the list of parameters to push onto S register.
 * <p>
 * This class contributed by Jean-Pierre Gaillardon, April 2005
 * </p>
 * @see SECDMachine
 *
 */
class opLDR extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  public opLDR(Jatha lisp)
  {
    super(lisp, "LDR");
  }


  public void Execute(SECDMachine machine)
  {
    LispValue indexes;

    indexes = machine.C.value().second();

    machine.S.push(getRestListAt(indexes, machine.E.value()));

    machine.C.pop();
    machine.C.pop();
  }


  /**
   * @param ij_indexes a dot paired (i . j) i is the list index, j the index in this list
   * @param valueList the list of lists
   * @return the "rest parameter" whose index is superior to j in list number i
   */
  private LispValue getRestListAt(LispValue ij_indexes, LispValue valueList)
  {
    long i, j;

    i = ((LispInteger)(ij_indexes.car())).getLongValue();
    j = ((LispInteger)(ij_indexes.cdr())).getLongValue();

    LispValue subList = loc(i, valueList);
    for (int idx = 1 ; idx < j ; idx++)
    {
      subList = subList.cdr();
    }
    return subList;
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
