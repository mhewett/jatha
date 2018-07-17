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
 * $Id: opTAG_B.java,v 1.2 2008/07/25 00:02:43 mhewett Exp $
 */
package org.jatha.machine;

import org.jatha.Jatha;
import org.jatha.dynatype.*;

/**
 * Helps to implement TAGBODY.
 * Pops itself from the C register.
 * Pushes onto to the X register the value: (E D new HashTable(B))
 *  
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
class opTAG_B extends SECDop {
    /**
     * It calls <tt>SECDop()</tt> with the machine argument
     * and the label of this instruction.
     * @see SECDMachine
     */
    public opTAG_B(final Jatha lisp) {
        super(lisp, "TAG_B");
    }
    
    public void Execute(final SECDMachine machine) {
        machine.C.pop();
        machine.X.assign(f_lisp.makeCons(f_lisp.makeList(machine.E.value(),machine.D.value(),new StandardLispHashTable((StandardLispHashTable)machine.B)),machine.X.value()));
    }
}
