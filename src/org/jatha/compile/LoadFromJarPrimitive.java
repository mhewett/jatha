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
 * Implements load-from-container.  Loads the given file
 * from the current jar, using getResource.
 * Normally takes one argument, for example "images/icon.gif".
 * Do not use a leading slash.
 * The two-argument version takes a filename as the first argument
 * and the URL of a jar file as the second argument.
 */
public class LoadFromJarPrimitive extends LispPrimitive
{
  public LoadFromJarPrimitive(Jatha lisp)
  {
    super(lisp, "LOAD-FROM-JAR", 1, 2);
  }

  public void Execute(SECDMachine machine)
  {
    LispValue args    = machine.S.pop();
    LispValue filename      = args.car();
    LispValue containerURL  = args.second();

    machine.C.pop();  // Remove the opcode.

    if (containerURL != f_lisp.NIL)   // Jar file URL specified
      machine.S.push(f_lisp.loadFromJar(filename, containerURL));

    else
      machine.S.push(f_lisp.loadFromJar(filename));
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
