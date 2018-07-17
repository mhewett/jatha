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


import org.jatha.dynatype.*;
import org.jatha.machine.*;
import org.jatha.Jatha;

/**
 * <p>Adds new packages to use for a package.</p>
 * <p>(use-package packages-to-use &optional package)</p>
 * <p>packages-to-use may be either a list of packages or packages names, or package nicknames. It could also be
 * be a single package or package name. A package name/nickname may be either a symbol or a string<p>
 * <p>package defaults to the current package if not specified. Otherwise it is the package or package name
 * of the package that will use packages-to-use</p>
 * <p>use-package returns t</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
public class UsePackagePrimitive extends LispPrimitive {
    public UsePackagePrimitive(final Jatha lisp) {
        super(lisp, "USE-PACKAGE", 1, 2);
    }
    
    public void Execute(final SECDMachine machine) {
        final LispValue args    = machine.S.pop();
        final LispValue useDef = args.car();
        LispValue useList = null;
        final LispValue pkg = args.second();
        LispPackage thePack;
        
        if(pkg == f_lisp.NIL) {
            thePack = (LispPackage)machine.get_special_value(f_lisp.PACKAGE_SYMBOL);
        } else {
            thePack = (LispPackage)f_lisp.findPackage(pkg);
        }

        if(!(useDef instanceof LispCons)) {
            useList = f_lisp.makeList(useDef);
        } else {
            useList = useDef;
        }

        thePack.setUses(thePack.getUses().append(useList));

        machine.S.push(f_lisp.T);
        machine.C.pop();
    }
    
    // 1 or 2 evaluated args
    public LispValue CompileArgs(LispCompiler compiler, SECDMachine machine, LispValue args,LispValue valueList, LispValue code) throws CompilerException {
        return compiler.compileArgsLeftToRight(args, valueList,f_lisp.makeCons(machine.LIS,f_lisp.makeCons(args.length(), code)));
    }
}

