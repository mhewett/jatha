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
 * $Id: GoPrimitive.java,v 1.3 2009/07/26 05:23:12 mhewett Exp $
 */
package org.jatha.compile;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;

/**
 * <p>Implements the GO primitive</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.3 $
 */
public class GoPrimitive extends LispPrimitive
{
  private long counter = 0L;

  public GoPrimitive(final Jatha lisp) {
    super(lisp,"GO",1);
  }

  public void Execute(final SECDMachine machine) 
  {
    final LispValue tag = machine.S.pop().car();
    machine.S.assign(f_lisp.NIL);
    final LispValue code = machine.B.gethash(tag).car();

    machine.E.assign(machine.X.value().first().first());
    machine.D.assign(machine.X.value().first().second());
    ((StandardLispHashTable)machine.B).assign((StandardLispHashTable)machine.X.value().first().third());

    machine.C.assign(code);
  }

  public LispValue CompileArgs(final LispCompiler compiler, final SECDMachine machine, final LispValue args, final LispValue valueList, final LispValue code) throws CompilerException 
  {
    final LispValue tag = args.car();
    long nextVal = 0L;

    if(!compiler.isLegalTag(tag)) {
      throw new IllegalArgumentException("Tag " + tag + " is not legal in this lexical context");
    }

    // Added (mh) 12 Mar 2008 because duplicate tags were being entered, causing extra code to be generated.
    // This is related to a Jatha bug for nested dotimes.
    if (! compiler.getRegisteredGos().containsValue(tag))
    {
      synchronized(this) {
        nextVal = counter++;
      }
      compiler.getRegisteredGos().put(new Long(nextVal),tag);
    }

    return compiler.compileArgsLeftToRight(f_lisp.makeList(f_lisp.makeList(f_lisp.QUOTE,f_lisp.makeList(f_lisp.getEval().intern("#:T"+nextVal)))),valueList,code);
  }
  
}  // GoPrimitive
