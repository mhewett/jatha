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
 * $Id: BlockPrimitive.java,v 1.1 2005/06/01 13:08:02 olagus Exp $
 */
package org.jatha.compile;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;

/**
 * <p>The Block primitive</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class BlockPrimitive extends LispPrimitive {
    public BlockPrimitive(final Jatha lisp) {
        super(lisp,"BLOCK",1,Long.MAX_VALUE);
        inlineP = true;
    }

    public void Execute(final SECDMachine machine) {
        System.err.println("BLOCK was compiled - shouldn't have been.");
    }
    
    public LispValue CompileArgs(final LispCompiler compiler, final SECDMachine machine, final LispValue args, final LispValue valueList, final LispValue code) throws CompilerException {
        final LispValue tag = args.car();
        compiler.getLegalBlocks().push(tag);
        final LispValue fullCode = f_lisp.makeList(f_lisp.makeCons(f_lisp.getEval().intern("PROGN"),args.cdr()));
        final LispValue compiledCode = compiler.compileArgsLeftToRight(fullCode, valueList,f_lisp.makeCons(machine.BLK,f_lisp.makeCons(tag,code)));
        compiler.getLegalBlocks().pop();
        return compiledCode;
    }
}// BlockPrimitive
