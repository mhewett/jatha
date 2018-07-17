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
/**
 * $Id: MakepackagePrimitive.java,v 1.6 2005/05/22 20:15:53 olagus Exp $
 */
package org.jatha.compile;

import java.util.Map;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;

import org.jatha.compile.args.*;

/**
 * <p>Creates a package with the associated information if no such package exists. Returns the package created.</p>
 * <p>(make-package package-name &key nicknames use)</p>
 * <p>package-name should be a symbol or string</p>
 * <p>nicknames should be a list of symbols or strings</p>
 * <p>use should also be a list of symbols or strings</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.6 $
 */
public class MakepackagePrimitive extends LispPrimitive {
    private LambdaList args;

    private LispValue pckSym;
    private LispValue nckKey;
    private LispValue useKey;
    private LispValue nckSym;
    private LispValue useSym;

    public MakepackagePrimitive(final Jatha lisp) {
        super(lisp, "MAKE-PACKAGE", 1, 5);
        pckSym = lisp.EVAL.intern("PACKAGE-NAME");
        nckSym = lisp.EVAL.intern("NICKNAMES");
        useSym = lisp.EVAL.intern("USE");
        nckKey = lisp.EVAL.intern(":NICKNAMES");
        useKey = lisp.EVAL.intern(":USE");
        args = new OrdinaryLambdaList(lisp);
        args.getNormalArguments().add(new NormalArgument(pckSym));
        args.getKeyArguments().put(nckKey,new KeyArgument(nckSym,nckKey));
        args.getKeyArguments().put(useKey,new KeyArgument(useSym,useKey,lisp.makeList(lisp.QUOTE,lisp.makeList(lisp.makeString("COMMON-LISP")))));
    }
    
    public void Execute(final SECDMachine machine) {
  	final LispValue argsList = machine.S.pop();
        final Map arguments = args.parse(argsList);
  	final LispValue name = (LispValue)arguments.get(pckSym);
  	final LispValue nick = (LispValue)arguments.get(nckSym);
  	final LispValue use = (LispValue)arguments.get(useSym);
        machine.S.push(f_lisp.makePackage(name,nick,use));
        machine.C.pop();
    }

  // One to two evaluated args.
  public LispValue CompileArgs(LispCompiler compiler, SECDMachine machine, LispValue args,LispValue valueList, LispValue code) throws CompilerException {
      return compiler.compileArgsLeftToRight(args, valueList, f_lisp.makeCons(machine.LIS,f_lisp.makeCons(args.length(), code))  );
  }
}

