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
 * $Id: KeyArgument.java,v 1.1 2005/05/16 21:51:23 olagus Exp $
 */
package org.jatha.compile.args;

import org.jatha.dynatype.LispValue;

/**
 * <p>Info about this class</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class KeyArgument extends OptionalArgument {
    private LispValue keyword;
    
    public KeyArgument(final LispValue var) {
        this(var,var,null,null);
    }
    
    public KeyArgument(final LispValue var,final LispValue keyword) {
        this(var, keyword, null, null);
    }
    
    public KeyArgument(final LispValue var,final LispValue keyword, final LispValue initForm) {
        this(var, keyword, initForm, null);
    }
    
    public KeyArgument(final LispValue var,final LispValue keyword, final LispValue initForm, final LispValue supplied) {
        super(var, initForm, supplied);
        this.keyword = keyword;
    }
    
    public LispValue getKeyword() {
        return this.keyword;
    }
    
    public void setKeyword(final LispValue keyword) {
        this.keyword = keyword;
    }
}// KeyArgument
