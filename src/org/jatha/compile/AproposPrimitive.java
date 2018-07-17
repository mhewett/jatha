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



public class AproposPrimitive extends LispPrimitive
{
  public AproposPrimitive(Jatha lisp)
  {
    super(lisp, "APROPOS", 1, 2);
  }

  public void Execute(SECDMachine machine)
  {
    LispValue args    = machine.S.pop();
    LispValue str     = args.car();
    LispValue pkgArg  = args.second();
    LispValue pkg;

    if (str instanceof LispString)
    {
      if (pkgArg != f_lisp.NIL)   // Package specified
      {
        pkg = f_lisp.findPackage(pkgArg);
        if (pkg == f_lisp.NIL)   // Non-existent package
          System.err.println("\n;; * Warning: package '" + pkgArg +
                             "' does not exist.  Searching all packages.");
      }
      else
        pkg = f_lisp.NIL;

      machine.S.push(f_lisp.apropos(str, pkg));
      machine.C.pop();
    }
    else
    {
      System.err.println("\n;; *** The first argument to APROPOS is not a string");
      machine.S.push(f_lisp.NIL);
      machine.C.pop();
    }
  }

  // 1 or 2 evaluated args
  public LispValue CompileArgs (LispCompiler compiler, SECDMachine machine, LispValue args,
                                LispValue valueList, LispValue code)
    throws CompilerException
  {
    return
      compiler.compileArgsLeftToRight(args, valueList,
                                      f_lisp.makeCons(machine.LIS,
                                                      f_lisp.makeCons(args.length(), code)));
  }
}
