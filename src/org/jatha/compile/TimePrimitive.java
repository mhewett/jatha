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

package org.jatha.compile;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;


public class TimePrimitive extends LispPrimitive
{
  public TimePrimitive(Jatha lisp)
  {
    super(lisp, "TIME", 1);
  }

  public void Execute(SECDMachine machine)
    throws CompilerException
  {
    LispValue expr = machine.S.pop();
    LispValue code = f_lisp.COMPILER.compile(f_lisp.MACHINE, expr, f_lisp.NIL);
    SECDMachine newMachine = new SECDMachine(f_lisp);

    long startMemory  = f_lisp.SYSTEM_INFO.freeMemory();
    long startTime    = System.currentTimeMillis();
    LispValue value   = newMachine.Execute(code, f_lisp.NIL);
    long endTime      = System.currentTimeMillis();
    long endMemory    = f_lisp.SYSTEM_INFO.freeMemory();

    System.out.println("\n; real time: " + (endTime - startTime)     + " ms");
    System.out.println(  ";     space: " + (startMemory - endMemory) + " bytes");

    machine.S.push(value);
    machine.C.pop();
  }
}

