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
import org.jatha.dynatype.LispFunction;
import org.jatha.dynatype.LispValue;
import org.jatha.machine.SECDMachine;



// @date    Tue Feb  4 13:30:53 1997
/**
 * (APPLY fn args...)
 * @see org.jatha.machine.SECDMachine
 * @see LispCompiler
 * @see LispPrimitive
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
public class ApplyPrimitive extends LispPrimitive
{
  public ApplyPrimitive(Jatha lisp)
  {
    super(lisp, "APPLY", 2, Long.MAX_VALUE);
  }

  // Apply is routed through EVAL after the args are parsed.
  public void Execute(SECDMachine machine)
    throws CompilerException
  {
    LispValue args   = machine.S.pop();
    LispValue fn     = args.car();
    LispValue fnArgs = args.cdr();

    // The last arg must be a list.
    if (!validArgumentList(args))
      throw new WrongArgumentTypeException("APPLY", "a CONS in the last argument",
					   "a " + fnArgs.last().car().type_of().toString());
    machine.S.push(f_lisp.makeCons(fn, f_lisp.COMPILER.quoteList(constructArgList(fnArgs))));

    // (mh) 4 Sep 2004
    // This seems like a kludge, but I don't know how to get around it.
    // if the fn is a user-defined function, we have to move the arguments to the E register.
    if ((fn instanceof LispFunction) && (! ((LispFunction)fn).isBuiltin()))
    {
      machine.S.pop();
      machine.S.push(f_lisp.makeList(fn));
      machine.E.push(fnArgs);
    }

    // The args list is an expression to be evaluated.
    machine.C.pop();
    machine.C.push(new EvalPrimitive(f_lisp));

    //System.out.println("APPLY: fn = " + fn + ", args = " + fnArgs);
    //System.out.println("S: " + machine.S.value());
    //System.out.println("E: " + machine.E.value());
    //System.out.println("C: " + machine.C.value());
  }

  // Unlimited number of evaluated args.
  public LispValue CompileArgs (LispCompiler compiler, SECDMachine machine, LispValue args,
				LispValue valueList, LispValue code)
    throws CompilerException
  {
    return
      compiler.compileArgsLeftToRight(args, valueList,
                                      f_lisp.makeCons(machine.LIS,
                                                      f_lisp.makeCons(args.length(), code)));
   }


  // The last arg is a list.  We need to cons the
  // rest onto the front of the list.
  LispValue constructArgList(LispValue args)
  {
    // The last argument is a list, and we need to quote
    // the values in that list.
    if (args.cdr() == f_lisp.NIL)
      return args.car();
    else
      return f_lisp.makeCons(args.car(), constructArgList(args.cdr()));
  }

  public boolean validArgumentList(LispValue args)
  {
    // The last argument must be a CONS
    if (args.last().car().basic_consp() || args.last().car() == f_lisp.NIL)
      return super.validArgumentList(args);
    else
    {
      System.err.println(";; *ERROR*: Last argument to APPLY must be a CONS.");
      return false;
    }
  }
}
