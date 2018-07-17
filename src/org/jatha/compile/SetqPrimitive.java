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


public class SetqPrimitive extends LispPrimitive
{
  public SetqPrimitive(Jatha lisp)
  {
    super(lisp, "SETQ", 2);
  }

  public void Execute(SECDMachine machine)
  {
    LispValue val = machine.S.pop();
    LispValue sym = machine.S.pop();

    if (sym.basic_listp())   // local variable
      machine.LD.setComponentAt(sym, machine.E.value(), val);

    else if (sym.specialP())  // special variable
      machine.special_set(sym, val);

    else  // global variable
      sym.setf_symbol_value(val);

    machine.S.push(val);
    machine.C.pop();
  }


  public LispValue CompileArgs (LispCompiler compiler, SECDMachine machine, LispValue args,
                                LispValue valueList, LispValue code)
      throws CompilerException
  {

    // 13 Dec 2005 (mh)
    // SETQ is not compiling correctly (noticed by Ola Bini).
    // Also, SET is not compiling correctly.  They are always modifying the
    // global value of the symbol, not the local value.  Fixed it by passing in
    // the index instead of the symbol name if it is known to have a binding.

    LispValue lookupVal = compiler.indexAndAttribute(args.first(), valueList);

    if (lookupVal.second().basic_null())  // SETQ of a global var
      return
          f_lisp.makeCons(machine.LDC,
                          f_lisp.makeCons(args.first(),
                                          compiler.compile(args.second(), valueList, code)));

    else  // SETQ of a local var, inside a LET or something like that.
      return  f_lisp.makeCons(machine.LDC,
                              f_lisp.makeCons(lookupVal.second(),
                                              compiler.compile(args.second(), valueList, code)));
  }
}

