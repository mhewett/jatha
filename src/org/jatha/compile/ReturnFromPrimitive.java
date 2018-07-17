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
 * $Id: ReturnFromPrimitive.java,v 1.2 2009/07/26 05:23:12 mhewett Exp $
 */
package org.jatha.compile;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;

/**
 * <p>The RETURN-FROM special form. Takes two parameters, the tag of the block to return from, and the value to return.</p>
 * <p>(return-from name result)</p>
 * <p>the tag name is not evaluated and must be a symbol or nil.</p>
 * <p>the result is optional, and defaults to nil if not specified. it is evaluated.</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
public class ReturnFromPrimitive extends LispPrimitive {
  public ReturnFromPrimitive(final Jatha lisp) {
    super(lisp,"RETURN-FROM",1,2);
  }

  public void Execute(final SECDMachine machine) throws CompilerException 
  {
    final LispValue tag = machine.S.pop();
    final LispValue args = machine.S.pop();
    final LispValue retVal = (args.basic_length()==0)?f_lisp.NIL:args.car();
    machine.S.push(retVal);
    findBlock(tag,machine);
  }

  private void findBlock(final LispValue tag, final SECDMachine machine) throws CompilerException 
  {
    LispValue currVal = null;
    while(true) {
      currVal = machine.C.pop();
      while (currVal != f_lisp.NIL && currVal != machine.RTN && currVal != machine.RTN_IF && currVal != machine.RTN_IT && currVal != machine.JOIN && currVal != machine.BLK && !(currVal instanceof TagbodyPrimitive)) {
        currVal = machine.C.pop();
      }
      if (currVal == machine.BLK) 
      {
        currVal = machine.C.pop();
        if (tag == currVal) 
        {
          return; // We found the place!
        }
      } else if(currVal == machine.RTN || currVal == machine.RTN_IF || currVal == machine.RTN_IT || currVal == machine.JOIN) {
        ((SECDop)currVal).Execute(machine);
      } else if (currVal instanceof TagbodyPrimitive) {
        machine.C.push(currVal);
        ((LispPrimitive)currVal).Execute(machine);
      } else {
        throw new IllegalArgumentException("RETURN-FROM called with in bad form, no matching block outside");
      }
    }
  }

  public LispValue CompileArgs(final LispCompiler compiler, final SECDMachine machine, final LispValue args, final LispValue valueList, final LispValue code) throws CompilerException {
    final LispValue tag = args.car();
    if(!compiler.getLegalBlocks().contains(tag)) {
      throw new IllegalReturnStatement("No enclosing lexical block with tag " + tag);
    }
    final LispValue fullCode = args.cdr();
    final LispValue compiledCode = compiler.compileArgsLeftToRight(fullCode, valueList,f_lisp.makeCons(machine.LIS,f_lisp.makeCons(fullCode.length(),f_lisp.makeCons(machine.LDC,f_lisp.makeCons(tag,code)))));
    return compiledCode;
  }
  public static class IllegalReturnStatement extends CompilerException {
    IllegalReturnStatement()         { super();  }
    IllegalReturnStatement(final String s) { super(s); }
  }
}// ReturnFromPrimitive
