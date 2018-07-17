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
 * $Id: ShadowPrimitive.java,v 1.1 2005/05/22 20:15:53 olagus Exp $
 */
package org.jatha.compile;

import java.util.Map;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;

import org.jatha.compile.args.*;

/**
 * <p>The print name of each symbol in symbols will be extracted and put in as a new symbol in the package, with shadowing on</p>
 * <p>(shadow symbols &optional package)</p>
 * <p>symbols should be a list of symbols or a single symbol, a string, or a list of strings</p>
 * <p>package should be a symbol or string, or a package</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class ShadowPrimitive extends LispPrimitive {
    private LambdaList args;

    private LispValue symbolsSym;
    private LispValue packageSym;

    public ShadowPrimitive(final Jatha lisp) {
        super(lisp, "SHADOW", 1, 2);
        symbolsSym = lisp.EVAL.intern("SYMBOLS");
        packageSym = lisp.EVAL.intern("PACKAGE");
        args = new OrdinaryLambdaList(lisp);
        args.getNormalArguments().add(new NormalArgument(symbolsSym));
        args.getOptionalArguments().add(new OptionalArgument(packageSym,lisp.PACKAGE_SYMBOL));
    }
    
    public void Execute(final SECDMachine machine) {
  	final LispValue argsList = machine.S.pop();
        final Map arguments = args.parse(argsList);
  	final LispValue symb = (LispValue)arguments.get(symbolsSym);
  	final LispValue pkg = (LispValue)arguments.get(packageSym);
        final LispValue pack = f_lisp.findPackage(pkg);
        machine.S.push(((StandardLispPackage)pack).shadow(symb));
        machine.C.pop();
    }

  // One to two evaluated args.
  public LispValue CompileArgs(LispCompiler compiler, SECDMachine machine, LispValue args,LispValue valueList, LispValue code) throws CompilerException {
      return compiler.compileArgsLeftToRight(args, valueList, f_lisp.makeCons(machine.LIS,f_lisp.makeCons(args.length(), code))  );
  }
}// ShadowPrimitive
