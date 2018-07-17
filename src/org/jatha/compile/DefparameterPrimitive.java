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



public class DefparameterPrimitive extends LispPrimitive
{
  public DefparameterPrimitive(Jatha lisp)
  {
    super(lisp, "DEFPARAMETER", 2, 3);  // symbol, value, documentation
  }

  public void Execute(SECDMachine machine)
  {
    LispValue val  = machine.S.pop();
    LispValue sym  = machine.S.pop();

    // Declare the symbol as Special
    sym.set_special(true);

    // Assign the value.
    machine.special_set(sym, val);

    // Return the symbol
    machine.S.push(sym);

    machine.C.pop();  // Pop the DEFPARAMETER
  }

  public LispValue CompileArgs (LispCompiler compiler, SECDMachine machine, LispValue args,
				LispValue valueList, LispValue code)
    throws CompilerException
  {
    // Don't evaluate the first arg. (load it as a constant)
    return
      f_lisp.makeCons(machine.LDC,
	                    f_lisp.makeCons(args.first(),
		                                  compiler.compile(args.second(), valueList, code)));
  }
}

