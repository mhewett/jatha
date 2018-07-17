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


public class DefconstantPrimitive extends LispPrimitive
{
  public DefconstantPrimitive(Jatha lisp)
  {
    super(lisp, "DEFCONSTANT", 2, 3);  // symbol, value, documentation
  }

  public void Execute(SECDMachine machine)
  {
    LispValue val  = machine.S.pop();
    LispValue sym  = machine.S.pop();

    // Assign the value.
    if (sym.boundp() == f_lisp.T)
    {
      System.err.println("Warning: Constant " + sym
			 + " being redefined from " + sym.symbol_value()
			 + " to " + val);
    }
    sym.setf_symbol_value(val);

    // Make it a constant
    LispValue newSymbol = new StandardLispConstant(f_lisp, sym);
    f_lisp.SYMTAB.replace((LispString)(sym.symbol_name()), newSymbol);

    // Declare the symbol as Special
    newSymbol.set_special(true);

    // Return the symbol
    machine.S.push(sym);

    machine.C.pop();  // Pop the DEFCONSTANT
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
