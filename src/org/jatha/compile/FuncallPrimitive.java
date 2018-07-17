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


// Funcall creates a new expression and calls EVAL on it.
public class FuncallPrimitive extends LispPrimitive
{
  public FuncallPrimitive(Jatha lisp)
  {
    super(lisp, "FUNCALL", 1, Long.MAX_VALUE);
  }

  public void Execute(SECDMachine machine)
  {
    // The args list is an expression to be evaluated.
    // Need to quote the argument(s) because they have already been evaluated.
    // The EVAL will evaluate them again.
    LispValue args   = machine.S.pop();
    LispValue fn     = args.car();
    LispValue fnArgs = args.cdr();

    machine.S.push(f_lisp.makeCons(fn, f_lisp.COMPILER.quoteList(fnArgs)));

    // (mh) 4 Sep 2004
    // This seems like a kludge, but I don't know how to get around it.
    // if the fn is a user-defined function, we have to move the arguments to the E register.
    if ((fn instanceof LispFunction) && (! ((LispFunction)fn).isBuiltin()))
    {
      machine.S.pop();
      machine.S.push(f_lisp.makeList(fn));
      machine.E.push(fnArgs);
    }

    machine.C.pop();
    machine.C.push(new EvalPrimitive(f_lisp));
  }

  // Evaluate only the first arg.
  public LispValue CompileArgs (LispCompiler compiler, SECDMachine machine, LispValue args,
				LispValue valueList, LispValue code)
    throws CompilerException
  {
    return
      compiler.compileArgsLeftToRight(args, valueList,
                                      f_lisp.makeCons(machine.LIS,
                                                      f_lisp.makeCons( args.length(), code)));
      /*
      compiler.compileArgsLeftToRight(
        f_lisp.makeCons(args.car(), f_lisp.NIL),
                        valueList,
      compiler.compileConstantArgsLeftToRight(machine, args.cdr(), valueList,
                                              f_lisp.makeCons(machine.LIS,
                                                              f_lisp.makeCons( args.length(), code))));
                                                              */
   }
}


