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
 * $Id: opBLK.java,v 1.2 2008/07/25 00:02:09 mhewett Exp $
 */
package org.jatha.machine;

import org.jatha.Jatha;
import org.jatha.dynatype.*;

/**
 * <p>The BLK-operation is a marker, which pops itself and its argument from the C register. 
 * Its purpose is to be a marker for the
 * non-local exit special forms. 
 * It is used to implement the <i>block</i> special form.</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
class opBLK extends SECDop{
    /**
     * It calls <tt>SECDop()</tt> with the machine argument
     * and the label of this instruction.
     * @see SECDMachine
     */
    public opBLK(final Jatha lisp) {
        super(lisp, "BLK");
    }


    public void Execute(final SECDMachine machine) {
        machine.C.pop(); /* Pop the BLK command. */
        machine.C.pop(); /* Pop the tag */     
    }
    
    public LispValue grindef(final LispValue code, final int indentAmount) {
        indent(indentAmount);
        System.out.print(functionName);
        indent(5);
        code.second().internal_princ(System.out);
        f_lisp.NEWLINE.internal_princ(System.out);
        return code.cdr().cdr();
    }
}// opBLK
