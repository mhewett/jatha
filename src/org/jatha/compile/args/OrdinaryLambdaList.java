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
 * $Id: OrdinaryLambdaList.java,v 1.3 2008/03/10 03:26:14 mhewett Exp $
 */
package org.jatha.compile.args;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.jatha.Jatha;

import org.jatha.dynatype.LispValue;

/**
 * <p>Info about this class</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.3 $
 */
public class OrdinaryLambdaList implements LambdaList {
    private Jatha lisp;

    private List<LispValue> normalArguments;
    private List<LispValue> optionalArguments;
    private RestArgument restArgument;
    private Map<LispValue, LispValue> keyArguments;
    private boolean allowOtherKeys;
    private List<LispValue> auxArguments;

    private LispValue allowOtherKeysKey;

    public OrdinaryLambdaList(final Jatha lisp) {
        this.lisp = lisp;
        normalArguments = new LinkedList<LispValue>();
        optionalArguments = new LinkedList<LispValue>();
        restArgument = null;
        keyArguments = new HashMap<LispValue, LispValue>();
        allowOtherKeys = false;
        auxArguments = new LinkedList<LispValue>();
        allowOtherKeysKey = lisp.EVAL.intern(":ALLOW-OTHER-KEYS");
    }

    public Map<LispValue, LispValue> parse(final LispValue arguments) {
        final Map<LispValue, LispValue> ret = new HashMap<LispValue, LispValue>();
        int state = 0; // 0 = regular, 1 = optional, 2 = rest, 3 = key
        final Set<LispValue> keyArgsLeft = new HashSet<LispValue>(keyArguments.keySet());
        final Set<LispValue> optArgsLeft = new HashSet<LispValue>(optionalArguments);
        LispValue vals = lisp.NIL;
            
        Iterator<LispValue> next = normalArguments.iterator();
        LispValue rests = null;
        boolean tempAllow = allowOtherKeys;

        if(!tempAllow) {
            for(final Iterator<LispValue> iter = arguments.iterator();iter.hasNext();) {
                if ((iter.next().eql(allowOtherKeysKey) == lisp.T) && iter.next() != lisp.NIL) {
                    tempAllow = true;
                    break;
                }
            }
        }
        
        for(final Iterator<LispValue> iter = arguments.iterator();iter.hasNext();) {
            final LispValue val = iter.next();
            if(tempAllow && (val.eql(allowOtherKeysKey) == lisp.T)) {
                iter.next();
                continue;
            }

            while(next != null && !next.hasNext()) {
                state++;
                switch(state) {
                case 1:
                    next = optionalArguments.iterator();
                    break;
                case 2:
                    if(restArgument != null) {
                        rests = lisp.NIL;
                    }
                    next = null;
                    break;
                default:
                    next = null;
                    break;
                }
            }
            switch(state) {
            case 0:
                final NormalArgument arg = (NormalArgument)next.next();
                ret.put(arg.getVar(),val);
                vals = lisp.makeCons(lisp.makeCons(arg.getVar(),val),vals);
                break;
            case 1:
                final OptionalArgument oarg = (OptionalArgument)next.next();
                ret.put(oarg.getVar(),val);
                vals = lisp.makeCons(lisp.makeCons(oarg.getVar(),val),vals);
                optArgsLeft.remove(oarg);
                if(oarg.getSupplied() != null) {
                    ret.put(oarg.getSupplied(),lisp.T);
                    vals = lisp.makeCons(lisp.makeCons(oarg.getSupplied(),lisp.T),vals);
                }
                break;
            default:
                if(keyArguments.size() > 0 || tempAllow) {
                    final KeyArgument key = (KeyArgument)keyArguments.get(val);
                    if(null != key || tempAllow) {
                        keyArgsLeft.remove(val);
                        final LispValue theVal = iter.next();
                        if(null != rests) {
                            rests = rests.append(lisp.makeCons(val,lisp.makeCons(theVal,lisp.NIL)));
                        }
                        if(null != key) {
                            ret.put(key.getVar(),theVal);
                            vals = lisp.makeCons(lisp.makeCons(key.getVar(),theVal),vals);
                            if(key.getSupplied() != null) {
                                ret.put(key.getSupplied(),lisp.T);
                                vals = lisp.makeCons(lisp.makeCons(key.getSupplied(),lisp.T),vals);
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Bad key argument: " + val); //TODO: fix good exception here
                    }
                } else if(null != rests) {
                    rests = rests.append(lisp.makeCons(val,lisp.NIL));
                } else {
                    throw new IllegalArgumentException("Bad arguments"); //TODO: fix good exception here
                }



                break;
            }
        }
        if(state == 0 && !optionalArguments.isEmpty()) {
            next = optionalArguments.iterator();
        }
        for(;next != null && next.hasNext();) {
            final OptionalArgument arg = (OptionalArgument)next.next();
            if(arg.getInitForm() != null) {
                final LispValue val = lisp.eval(arg.getInitForm(),lisp.makeList(vals));
                ret.put(arg.getVar(),val);
                vals = lisp.makeCons(lisp.makeCons(arg.getVar(),val),vals);
            } else {
                ret.put(arg.getVar(),lisp.NIL);
                vals = lisp.makeCons(lisp.makeCons(arg.getVar(),lisp.NIL),vals);
            }
            if(arg.getSupplied() != null) {
                ret.put(arg.getSupplied(),lisp.NIL);
                vals = lisp.makeCons(lisp.makeCons(arg.getSupplied(),lisp.NIL),vals);
            }
        }
        for(final Iterator<LispValue> iter = keyArgsLeft.iterator();iter.hasNext();) {
            final LispValue sym = iter.next();
            final KeyArgument arg = (KeyArgument)keyArguments.get(sym);
            if(arg.getInitForm() != null) {
                final LispValue val = lisp.eval(arg.getInitForm(),lisp.makeList(vals));
                ret.put(arg.getVar(),val);
                vals = lisp.makeCons(lisp.makeCons(arg.getVar(),val),vals);
            } else {
                ret.put(arg.getVar(),lisp.NIL);
                vals = lisp.makeCons(lisp.makeCons(arg.getVar(),lisp.NIL),vals);
            }
            if(arg.getSupplied() != null) {
                ret.put(arg.getSupplied(),lisp.NIL);
                vals = lisp.makeCons(lisp.makeCons(arg.getSupplied(),lisp.NIL),vals);
            }
        }
        if(restArgument != null) {
            if(rests != null) {
                ret.put(restArgument.getVar(),rests);
                vals = lisp.makeCons(lisp.makeCons(restArgument.getVar(),rests),vals);
            } else {
                ret.put(restArgument.getVar(),lisp.NIL);
                vals = lisp.makeCons(lisp.makeCons(restArgument.getVar(),lisp.NIL),vals);
            }
        }

        for(final Iterator<LispValue> iter = auxArguments.iterator();iter.hasNext();) {
            final AuxArgument arg = (AuxArgument)iter.next();
            final LispValue val = lisp.eval(arg.getInitForm(),lisp.makeList(vals));
            ret.put(arg.getVar(),val);
            vals = lisp.makeCons(lisp.makeCons(arg.getVar(),val),vals);
        }
        return ret;
    } 

    public List<LispValue> getNormalArguments() {
        return normalArguments;
    }

    public List<LispValue> getOptionalArguments() {
        return optionalArguments;
    }

    public RestArgument getRestArgument() {
        return restArgument;
    }

    public void setRestArgument(final RestArgument restArgument) {
        this.restArgument = restArgument;
    }

    public boolean getAllowOtherKeys() {
        return allowOtherKeys;
    }

    public void setAllowOtherKeys(final boolean allowOtherKeys) {
        this.allowOtherKeys = allowOtherKeys;
    }

    public Map<LispValue, LispValue> getKeyArguments() {
        return keyArguments;
    }

    public List<LispValue> getAuxArguments() {
        return auxArguments;
    }
}// OrdinaryLambdaList
