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


// According the the CL spec, the value argument of DEFVAR
// is only supposed to be evaluated if the argument is going
// to be assigned to the variable.  This means we have to
// evaluate it at runtime, but only if necessary.

public class DefvarPrimitive extends LispPrimitive
{
  public DefvarPrimitive(Jatha lisp)
  {
    super(lisp, "DEFVAR", 1, 3);
  }

  public void Execute(SECDMachine machine)
  {
    LispValue args = machine.S.pop();
    LispValue sym  = args.first();
    LispValue val  = args.second();

    // Declare the symbol as Special
    sym.set_special(true);

    // If there is a value and the symbol is not bound,
    // we need to evaluate the argument and assign it to the symbol.
    if ((args.basic_length() == 2)
	      && (sym.boundp() == f_lisp.NIL))
    {
      machine.C.pop();  // Pop the DEFVAR

      machine.C.push(new SetfSymbolValuePrimitive(f_lisp));
      machine.C.push(new EvalPrimitive(f_lisp));
      machine.S.push(sym);
      machine.S.push(val);
    }
    else
    {
      // Return the symbol
      machine.S.push(sym);

      machine.C.pop();  // Pop the DEFVAR
    }

  }

  // Evaluate no args.  DEFVAR is a special form.
  public LispValue CompileArgs (LispCompiler compiler, SECDMachine machine, LispValue args,
				LispValue valueList, LispValue code)
  {
    return
      compiler.compileConstantArgsLeftToRight(machine, args, valueList,
			                                        f_lisp.makeCons(machine.LIS,
				                                         f_lisp.makeCons(args.length(), code)));
   }
}
