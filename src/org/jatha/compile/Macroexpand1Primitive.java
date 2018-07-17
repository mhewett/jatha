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
 * $Id: Macroexpand1Primitive.java,v 1.3 2007/03/22 02:12:50 mhewett Exp $
 */
package org.jatha.compile;

import org.jatha.Jatha;
import org.jatha.dynatype.LispPackage;
import org.jatha.dynatype.LispValue;
import org.jatha.machine.SECDMachine;

/**
 * <p>Returns the expansion of the form named. Otherwise the form will be returned as is.</p>
 * <p>(macroexpand-1 form)</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.3 $
 */
public class Macroexpand1Primitive extends LispPrimitive {
    public Macroexpand1Primitive(final Jatha lisp) {
        super(lisp, "MACROEXPAND-1", 1);
    }
    
    public void Execute(final SECDMachine machine) {
        final LispValue form = machine.S.pop();
        final LispValue carForm = form.car();
        if(carForm.fboundp() == f_lisp.T && carForm.symbol_function() != null && carForm.symbol_function().basic_macrop()) {
            machine.S.push(f_lisp.eval(f_lisp.makeCons(f_lisp.EVAL.intern("%%%" + carForm.symbol_name().toStringSimple(),(LispPackage)f_lisp.findPackage("SYSTEM")),quoteList(form.cdr()))));
        } else {
            machine.S.push(form);
        }
        machine.C.pop();
    }

    private LispValue quoteList(final LispValue intern) {
        LispValue ret = f_lisp.NIL;
        for(final java.util.Iterator iter = intern.iterator();iter.hasNext();) {
            final LispValue curr = (LispValue)iter.next();
            ret = f_lisp.makeCons(f_lisp.makeList(f_lisp.QUOTE,curr),ret);
        }
        return ret.nreverse();
    }
}// Macroexpand1Primitive
