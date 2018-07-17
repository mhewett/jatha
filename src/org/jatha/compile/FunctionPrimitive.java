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

/**
 * This is the class that encapsulates the action and
 * compilation of the FUNCTION function.
 *
 */
public class FunctionPrimitive extends LispPrimitive
{
  public static boolean DEBUG = false;


  public FunctionPrimitive(Jatha lisp)
  {
    super(lisp, "FUNCTION", 1);
  }

  public void Execute(SECDMachine machine)
  {
    LispValue arg = machine.S.pop();

    if (DEBUG)
      System.err.println("FunctionPrimitive.Execute: arg = " + arg.toString() + ", type = " + arg.type_of());

    // arg may be a symbol or a lambda expression
    if (arg.basic_symbolp())
      machine.S.push(arg.symbol_function());
    else
      machine.S.push(arg);

    machine.C.pop();
  }

  /**
   * The argument is not evaluated.
   * @param compiler
   * @param machine
   * @param args
   * @param valueList
   * @param code
   * @throws CompilerException
   */
  public LispValue CompileArgs (LispCompiler compiler, SECDMachine machine, LispValue args,
                                LispValue valueList, LispValue code)
    throws CompilerException
  {
    return  compiler.compileConstantArgsLeftToRight(machine, args, valueList, code);
  }
}

