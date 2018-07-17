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
 * <p>Fetches the name of the specified package.</p>
 * <p>(package-name package)</p>
 * <p>package should be either a package, package-name or a package nickname</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
public class PackageNamePrimitive extends LispPrimitive {
    public PackageNamePrimitive(final Jatha lisp) {
        super(lisp, "PACKAGE-NAME", 1, 1);
    }
    
    public void Execute(final SECDMachine machine) {
        machine.S.push(((LispPackage)f_lisp.findPackage(machine.S.pop())).getName());
        machine.C.pop();
    }
}

