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

/**
 * <p>Gets the documentation for the object.</p>
 * <p>(documentation symbol type)</p>
 * <p>The type may be any symbol, but the most common ones are:</p>
 * <ul>
 * <li>variable (for defvar, defparameter, defconstant)</li>
 * <li>function (for defun, defmacro)</li>
 * <li>structure (for defstruct)</li>
 * <li>type (for deftype)</li>
 * <li>setf (for defsetf)</li>
 * </ul>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.4 $
 */
public class DocumentationPrimitive extends LispPrimitive {
    public DocumentationPrimitive(final Jatha lisp) {
        super(lisp, "DOCUMENTATION", 2, 2);
    }

    public void Execute(final SECDMachine machine) {
        final LispValue type = machine.S.pop();
        final LispValue sym = machine.S.pop();
        machine.S.push(sym.documentation(type));
        machine.C.pop();
    }
}// DocumentationPrimitive
