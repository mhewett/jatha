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



// @date    Sat Feb  1 22:18:53 1997
/**
 * opJOIN terminates a branch of an IF-ELSE statement
 * by taking the topmost entry from the D register and
 * storing it in the C register.
 * @see SECDMachine
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
class opJOIN extends SECDop
{
  /**
   * It calls <tt>SECDop()</tt> with the machine argument
   * and the label of this instruction.
   * @see SECDMachine
   */
  public opJOIN(Jatha lisp)
  {
    super(lisp, "JOIN");
  }


  public void Execute(SECDMachine machine)
  {

    machine.C.assign(machine.D.pop());
    /*
      // having trouble printing circular lists.
      printf("\n JOIN: C is now: "); machine.C.print(System.out);
      System.out.print("\n       D is now: "); machine.D.print(System.out);
      System.out.flush();
      */
  }
}
