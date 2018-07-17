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



public class ListStarPrimitive extends LispPrimitive
{
  LispValue CONS = null;

  public ListStarPrimitive(Jatha lisp)
  {
    super(lisp, "LIST*", 1, Long.MAX_VALUE);

    inlineP = true;
    CONS = new ConsPrimitive(lisp);
  }

  public void Execute(SECDMachine machine)
  {
    // do nothing.  The list is there.
    System.err.println("LIST* was compiled - shouldn't have been.");

    machine.C.pop();
  }

  // If there is only one arg, it is the result.
  // If there is more than one, they are consed
  // onto the front of the last one:
  //   (list* 'a 'b 'c '(d e)) ==> (a b c d e)
  public LispValue CompileArgs (LispCompiler compiler, SECDMachine machine, LispValue args,
				LispValue valueList, LispValue code)
    throws CompilerException
  {
    if (args.cdr() == f_lisp.NIL)
      return compiler.compileArgsLeftToRight(args, valueList, code);
    else
      return compiler.compile(args.car(), valueList,
			      CompileArgs(compiler, machine, args.cdr(),
					  valueList,
                        f_lisp.makeCons(CONS, code)));
   }
}
