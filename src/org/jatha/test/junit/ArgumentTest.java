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
 * $Id: ArgumentTest.java,v 1.1 2005/05/21 16:28:45 olagus Exp $
 */
package org.jatha.test.junit;

import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jatha.Jatha;
import org.jatha.compile.args.LambdaList;
import org.jatha.compile.args.OrdinaryLambdaList;
import org.jatha.compile.args.NormalArgument;
import org.jatha.compile.args.OptionalArgument;
import org.jatha.compile.args.RestArgument;
import org.jatha.compile.args.KeyArgument;
import org.jatha.compile.args.AuxArgument;
import org.jatha.dynatype.LispValue;

/**
 * <p>Tests the handling of arguments.</p>
 *
 * @author <a href="mailto:Ola.Bini@itc.ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class ArgumentTest extends TestCase {
    private static Jatha lisp;

    static {
        lisp = new Jatha(false, false, false);  // no Jatha gui
        lisp.init();
        lisp.start();
    }

    private LambdaList list1; // (a b)                                               //tested
    private LambdaList list2; // (a &optional (b 2))                                 //tested
    private LambdaList list2_x; // (a &optional (b a))                               //tested
    private LambdaList list2_y; // (a &optional (b (+ a 3)))                         //tested
    private LambdaList list3; // (&optional (a 2 b) (c 3 d))                         //tested
    private LambdaList list4; // (&optional (a 2 b) (c 3 d) &rest x)                 //tested
    private LambdaList list5; // (a b &key c d)                                      //tested
    private LambdaList list6; // (a &optional (b 3) &rest x &key c (d a))            //tested
    private LambdaList list7; // (a &optional (b 3) &rest x &key c (d a) &allow-other-keys) //tested
    private LambdaList list8; // (a &key ((:c int) 13 supp))                         //tested
    private LambdaList list9; // (&aux (a 13))                                       //tested

    private LispValue list2_b_default;
    private LispValue list3_a_default;
    private LispValue list3_c_default;
    private LispValue list4_a_default;
    private LispValue list4_c_default;
    private LispValue list6_b_default;
    private LispValue list7_b_default;
    private LispValue list8_c_default;

    private LispValue a_key;
    private LispValue a_sym;
    private LispValue b_key;
    private LispValue b_sym;
    private LispValue c_key;
    private LispValue c_sym;
    private LispValue d_key;
    private LispValue d_sym;
    private LispValue x_key;
    private LispValue x_sym;
    private LispValue supp_sym;
    private LispValue int_sym;

    public ArgumentTest(final String name) {
        super(name);        
    }

    /**
     * Use -gui to enable the gui.
     * @param args command-line arguments.
     */
    public static void main (String[] args) {
        boolean useGui = false;  // default behavior
        
        for (int i=0,j=args.length; i<j; i++) {
            if(args[i].equalsIgnoreCase("-gui")) {
                useGui = true;
                break;
            }
        }

        if(useGui) {
            junit.swingui.TestRunner.run(ArgumentTest.class);
        } else {
            junit.textui.TestRunner.run(suite());
        }
    }

    public static Test suite() {
        return new TestSuite(ArgumentTest.class);
    }

    
    protected void setUp() throws Exception {
        list1 = new OrdinaryLambdaList(lisp);
        list2 = new OrdinaryLambdaList(lisp);
        list2_x = new OrdinaryLambdaList(lisp);
        list2_y = new OrdinaryLambdaList(lisp);
        list3 = new OrdinaryLambdaList(lisp);
        list4 = new OrdinaryLambdaList(lisp);
        list5 = new OrdinaryLambdaList(lisp);
        list6 = new OrdinaryLambdaList(lisp);
        list7 = new OrdinaryLambdaList(lisp);
        list8 = new OrdinaryLambdaList(lisp);
        list9 = new OrdinaryLambdaList(lisp);

        a_sym = lisp.EVAL.intern("A");
        a_key = lisp.EVAL.intern(":A");
        b_sym = lisp.EVAL.intern("B");
        b_key = lisp.EVAL.intern(":B");
        c_sym = lisp.EVAL.intern("C");
        c_key = lisp.EVAL.intern(":C");
        d_sym = lisp.EVAL.intern("D");
        d_key = lisp.EVAL.intern(":D");
        x_sym = lisp.EVAL.intern("X");
        x_key = lisp.EVAL.intern(":X");
        int_sym = lisp.EVAL.intern("INT");
        supp_sym = lisp.EVAL.intern("SUPP");

        list1.getNormalArguments().add(new NormalArgument(a_sym));
        list1.getNormalArguments().add(new NormalArgument(b_sym));

        list2.getNormalArguments().add(new NormalArgument(a_sym));
        list2_b_default = lisp.makeInteger(2);
        list2.getOptionalArguments().add(new OptionalArgument(b_sym,list2_b_default));

        list2_x.getNormalArguments().add(new NormalArgument(a_sym));
        list2_x.getOptionalArguments().add(new OptionalArgument(b_sym,a_sym));

        list2_y.getNormalArguments().add(new NormalArgument(a_sym));
        list2_y.getOptionalArguments().add(new OptionalArgument(b_sym,lisp.makeList(lisp.EVAL.intern("+"),a_sym,lisp.makeInteger(3))));

        list3_a_default = lisp.makeInteger(2);
        list3.getOptionalArguments().add(new OptionalArgument(a_sym,list3_a_default,b_sym));
        list3_c_default = lisp.makeInteger(3);
        list3.getOptionalArguments().add(new OptionalArgument(c_sym,list3_c_default,d_sym));

        list4_a_default = lisp.makeInteger(2);
        list4.getOptionalArguments().add(new OptionalArgument(a_sym,list4_a_default,b_sym));
        list4_c_default = lisp.makeInteger(3);
        list4.getOptionalArguments().add(new OptionalArgument(c_sym,list4_c_default,d_sym));
        list4.setRestArgument(new RestArgument(x_sym));

        list5.getNormalArguments().add(new NormalArgument(a_sym));
        list5.getNormalArguments().add(new NormalArgument(b_sym));
        list5.getKeyArguments().put(c_key,new KeyArgument(c_sym,c_key));
        list5.getKeyArguments().put(d_key,new KeyArgument(d_sym,d_key));

        list6.getNormalArguments().add(new NormalArgument(a_sym));
        list6_b_default = lisp.makeInteger(3);
        list6.getOptionalArguments().add(new OptionalArgument(b_sym,list6_b_default));
        list6.setRestArgument(new RestArgument(x_sym));
        list6.getKeyArguments().put(c_key,new KeyArgument(c_sym,c_key));
        list6.getKeyArguments().put(d_key,new KeyArgument(d_sym,d_key,a_sym));

        list7.getNormalArguments().add(new NormalArgument(a_sym));
        list7_b_default = lisp.makeInteger(3);
        list7.getOptionalArguments().add(new OptionalArgument(b_sym,list7_b_default));
        list7.setRestArgument(new RestArgument(x_sym));
        list7.getKeyArguments().put(c_key,new KeyArgument(c_sym,c_key));
        list7.getKeyArguments().put(d_key,new KeyArgument(d_sym,d_key,a_sym));
        list7.setAllowOtherKeys(true);

        list8.getNormalArguments().add(new NormalArgument(a_sym));
        list8_c_default = lisp.makeInteger(13);
        list8.getKeyArguments().put(c_key,new KeyArgument(int_sym,c_key,list8_c_default,supp_sym));

        list9.getAuxArguments().add(new AuxArgument(a_sym,lisp.makeInteger(13)));
    }

    protected void tearDown() {
        list1 = null;
        list2 = null;
        list2_x = null;
        list2_y = null;
        list3 = null;
        list4 = null;
        list5 = null;
        list6 = null;
        list7 = null;
        list8 = null;
        list9 = null;

        a_sym = null;
        a_key = null;
        b_sym = null;
        b_key = null;
        c_sym = null;
        c_key = null;
        d_sym = null;
        d_key = null;
        x_sym = null;
        x_key = null;
        supp_sym = null;

        list2_b_default = null;
        list3_a_default = null;
        list3_c_default = null;
        list4_a_default = null;
        list4_c_default = null;
        list6_b_default = null;
        list7_b_default = null;
    }

    public void testNormalArguments() {
        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue arg2 = lisp.makeString("Ojsan sa");
        
        final LispValue argList1 = lisp.makeList(arg1,arg2);
        final Map outp = list1.parse(argList1);
        assertEquals("Argument 1 should be correct",arg1,outp.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp.get(b_sym));
    } 

    public void testOptionalArguments() {
        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue arg2 = lisp.makeString("Ojsan sa");
        final LispValue argList1 = lisp.makeList(arg1,arg2);
        final LispValue argList2 = lisp.makeList(arg1);
        final LispValue argList3 = lisp.makeList(arg1,list2_b_default);
        final Map outp1 = list2.parse(argList1);
        final Map outp2 = list2.parse(argList2);
        final Map outp3 = list2.parse(argList3);
        assertEquals("Argument 1 should be correct",arg1,outp1.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp1.get(b_sym));

        assertEquals("Argument 1 should be correct",arg1,outp2.get(a_sym));
        assertEquals("Defaultargument 2 should be correct",list2_b_default,outp2.get(b_sym));

        assertEquals("Argument 1 should be correct",arg1,outp3.get(a_sym));
        assertEquals("Argument 2 should be correct",list2_b_default,outp3.get(b_sym));
    } 

    public void testOptionalArgumentsWithInternalDefaults() {
        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue argList = lisp.makeList(arg1);
        final Map outp = list2_x.parse(argList);
        assertEquals("Argument 1 should be correct",arg1,outp.get(a_sym));
        assertEquals("Argument 2 should be correct",arg1,outp.get(b_sym));
    }    

    public void testOptionalArgumentsWithInternalDefaultForm() {
        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue expected = lisp.makeInteger(20);
        final LispValue argList = lisp.makeList(arg1);
        final Map outp = list2_y.parse(argList);
        assertEquals("Argument 1 should be correct",arg1,outp.get(a_sym));
        assertEquals("Argument 2 should be correct",expected.toStringSimple(),((LispValue)outp.get(b_sym)).toStringSimple());
    }    

    public void testOptionalArgumentsWithSupplied() {
        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue arg2 = lisp.makeString("Ojsan sa");
        final LispValue argList1 = lisp.makeList(arg1,arg2);
        final LispValue argList2 = lisp.makeList(arg1);
        final LispValue argList3 = lisp.NIL;
        final Map outp1 = list3.parse(argList1);
        final Map outp2 = list3.parse(argList2);
        final Map outp3 = list3.parse(argList3);

        assertEquals("Argument 1 should be correct",arg1,outp1.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp1.get(c_sym));
        assertEquals("Argument 1 supplied should be correct",lisp.T,outp1.get(b_sym));
        assertEquals("Argument 2 supplied should be correct",lisp.T,outp1.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp2.get(a_sym));
        assertEquals("Argument 2 should be correct",list3_c_default,outp2.get(c_sym));
        assertEquals("Argument 1 supplied should be correct",lisp.T,outp2.get(b_sym));
        assertEquals("Argument 2 supplied should be correct",lisp.NIL,outp2.get(d_sym));

        assertEquals("Argument 1 should be correct",list3_a_default,outp3.get(a_sym));
        assertEquals("Argument 2 should be correct",list3_c_default,outp3.get(c_sym));
        assertEquals("Argument 1 supplied should be correct",lisp.NIL,outp3.get(b_sym));
        assertEquals("Argument 2 supplied should be correct",lisp.NIL,outp3.get(d_sym));
    }

    public void testOptionalArgumentsWithSuppliedAndRest() {
        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue arg2 = lisp.makeString("Ojsan sa");
        final LispValue argList1 = lisp.makeList(arg1,arg2);
        final LispValue argList2 = lisp.makeList(arg1);
        final LispValue argList3 = lisp.NIL;
        final Map outp1 = list4.parse(argList1);
        final Map outp2 = list4.parse(argList2);
        final Map outp3 = list4.parse(argList3);

        assertEquals("Argument 1 should be correct",arg1,outp1.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp1.get(c_sym));
        assertEquals("Argument 1 supplied should be correct",lisp.T,outp1.get(b_sym));
        assertEquals("Argument 2 supplied should be correct",lisp.T,outp1.get(d_sym));
        assertEquals("Rest argument should be correct",lisp.NIL,outp1.get(x_sym));

        assertEquals("Argument 1 should be correct",arg1,outp2.get(a_sym));
        assertEquals("Argument 2 should be correct",list4_c_default,outp2.get(c_sym));
        assertEquals("Argument 1 supplied should be correct",lisp.T,outp2.get(b_sym));
        assertEquals("Argument 2 supplied should be correct",lisp.NIL,outp2.get(d_sym));
        assertEquals("Rest argument should be correct",lisp.NIL,outp2.get(x_sym));

        assertEquals("Argument 1 should be correct",list4_a_default,outp3.get(a_sym));
        assertEquals("Argument 2 should be correct",list4_c_default,outp3.get(c_sym));
        assertEquals("Argument 1 supplied should be correct",lisp.NIL,outp3.get(b_sym));
        assertEquals("Argument 2 supplied should be correct",lisp.NIL,outp3.get(d_sym));
        assertEquals("Rest argument should be correct",lisp.NIL,outp3.get(x_sym));

        final LispValue argList4 = lisp.makeList(arg1,arg2,arg1);
        final LispValue argList5 = lisp.makeList(arg1,arg2,arg1,arg2);
        final LispValue argList6 = lisp.makeList(arg1,arg2,arg1).append(lisp.makeList(arg2,arg1));
        final LispValue expected4 = lisp.makeList(arg1);
        final LispValue expected5 = lisp.makeList(arg1,arg2);
        final LispValue expected6 = lisp.makeList(arg1,arg2,arg1);

        final Map outp4 = list4.parse(argList4);
        final Map outp5 = list4.parse(argList5);
        final Map outp6 = list4.parse(argList6);

        assertEquals("Argument 1 should be correct",arg1,outp4.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp4.get(c_sym));
        assertEquals("Argument 1 supplied should be correct",lisp.T,outp4.get(b_sym));
        assertEquals("Argument 2 supplied should be correct",lisp.T,outp4.get(d_sym));
        assertTrue("Rest argument should be correct",expected4.equal((LispValue)outp4.get(x_sym)) == lisp.T);

        assertEquals("Argument 1 should be correct",arg1,outp5.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp5.get(c_sym));
        assertEquals("Argument 1 supplied should be correct",lisp.T,outp5.get(b_sym));
        assertEquals("Argument 2 supplied should be correct",lisp.T,outp5.get(d_sym));
        assertTrue("Rest argument should be correct",expected5.equal((LispValue)outp5.get(x_sym)) == lisp.T);

        assertEquals("Argument 1 should be correct",arg1,outp6.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp6.get(c_sym));
        assertEquals("Argument 1 supplied should be correct",lisp.T,outp6.get(b_sym));
        assertEquals("Argument 2 supplied should be correct",lisp.T,outp6.get(d_sym));
        assertTrue("Rest argument should be correct",expected6.equal((LispValue)outp6.get(x_sym)) == lisp.T);
    }

    public void testNormalKeyParameters() {
        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue arg2 = lisp.makeString("Ojsan sa");
        final LispValue arg3 = lisp.makeInteger(42);
        final LispValue arg4 = lisp.makeString("well well well");
        
        final LispValue argList1 = lisp.makeList(arg1,arg2);
        final LispValue argList2 = lisp.makeList(arg1,arg2,c_key,arg3);
        final LispValue argList3 = lisp.makeList(arg1,arg2,c_key).append(lisp.makeList(arg3,d_key,arg4));
        final LispValue argList4 = lisp.makeList(arg1,arg2,d_key).append(lisp.makeList(arg4,c_key,arg3));

        final Map outp1 = list5.parse(argList1);
        final Map outp2 = list5.parse(argList2);
        final Map outp3 = list5.parse(argList3);
        final Map outp4 = list5.parse(argList4);

        assertEquals("Argument 1 should be correct",arg1,outp1.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp1.get(b_sym));
        assertEquals("Argument c should be correct",lisp.NIL,outp1.get(c_sym));
        assertEquals("Argument d should be correct",lisp.NIL,outp1.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp2.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp2.get(b_sym));
        assertEquals("Argument c should be correct",arg3,outp2.get(c_sym));
        assertEquals("Argument d should be correct",lisp.NIL,outp2.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp3.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp3.get(b_sym));
        assertEquals("Argument c should be correct",arg3,outp3.get(c_sym));
        assertEquals("Argument d should be correct",arg4,outp3.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp4.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp4.get(b_sym));
        assertEquals("Argument c should be correct",arg3,outp4.get(c_sym));
        assertEquals("Argument d should be correct",arg4,outp4.get(d_sym));
    }

    public void testCombinedComplicated() {
        // (a &optional (b 3) &rest x &key c (d a))
        // arguments to test:
        // (17)
        // (17 'z)
        // (17 'z :c "ojsan sa")
        // (17 'z :c "ojsan sa" :d 42)
        // (17 'z :d 42 :c "ojsan sa")

        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue arg2 = lisp.EVAL.intern("z");
        final LispValue arg3 = lisp.makeString("ojsan sa");
        final LispValue arg4 = lisp.makeInteger(42);

        final LispValue argList1 = lisp.makeList(arg1);
        final LispValue argList2 = lisp.makeList(arg1,arg2);
        final LispValue argList3 = lisp.makeList(arg1,arg2,c_key,arg3);
        final LispValue argList4 = lisp.makeList(arg1,arg2,c_key,arg3).append(lisp.makeList(d_key,arg4));
        final LispValue argList5 = lisp.makeList(arg1,arg2,d_key,arg4).append(lisp.makeList(c_key,arg3));

        final Map outp1 = list6.parse(argList1);
        final Map outp2 = list6.parse(argList2);
        final Map outp3 = list6.parse(argList3);
        final Map outp4 = list6.parse(argList4);
        final Map outp5 = list6.parse(argList5);

        final LispValue expected3 = lisp.makeList(c_key,arg3);
        final LispValue expected4 = lisp.makeList(c_key,arg3,d_key,arg4);
        final LispValue expected5 = lisp.makeList(d_key,arg4,c_key,arg3);

        assertEquals("Argument 1 should be correct",arg1,outp1.get(a_sym));
        assertEquals("Argument 2 should be correct",list6_b_default,outp1.get(b_sym));
        assertEquals("Argument x should be correct",lisp.NIL,outp1.get(x_sym));
        assertEquals("Argument c should be correct",lisp.NIL,outp1.get(c_sym));
        assertEquals("Argument d should be correct",arg1,outp1.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp2.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp2.get(b_sym));
        assertEquals("Argument x should be correct",lisp.NIL,outp2.get(x_sym));
        assertEquals("Argument c should be correct",lisp.NIL,outp2.get(c_sym));
        assertEquals("Argument d should be correct",arg1,outp2.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp3.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp3.get(b_sym));
        assertTrue("Argument x should be correct",expected3.equal((LispValue)outp3.get(x_sym)) == lisp.T);
        assertEquals("Argument c should be correct",arg3,outp3.get(c_sym));
        assertEquals("Argument d should be correct",arg1,outp3.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp4.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp4.get(b_sym));
        assertTrue("Argument x should be correct",expected4.equal((LispValue)outp4.get(x_sym)) == lisp.T);
        assertEquals("Argument c should be correct",arg3,outp4.get(c_sym));
        assertEquals("Argument d should be correct",arg4,outp4.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp5.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp5.get(b_sym));
        assertTrue("Argument x should be correct",expected5.equal((LispValue)outp5.get(x_sym)) == lisp.T);
        assertEquals("Argument c should be correct",arg3,outp5.get(c_sym));
        assertEquals("Argument d should be correct",arg4,outp5.get(d_sym));
    }

    public void testCombinedComplicatedAllowOtherKeys() {
        // (a &optional (b 3) &rest x &key c (d a) &allow-other-keys)
        // arguments to test:
        // (17)
        // (17 'z)
        // (17 'z :c "ojsan sa")
        // (17 'z :c "ojsan sa" :d 42)
        // (17 'z :d 42 :c "ojsan sa")
        // (17 'z :d 42 :c "ojsan sa" :testing t)

        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue arg2 = lisp.EVAL.intern("z");
        final LispValue arg3 = lisp.makeString("ojsan sa");
        final LispValue arg4 = lisp.makeInteger(42);
        final LispValue testing_key = lisp.EVAL.intern(":TESTING");

        final LispValue argList1 = lisp.makeList(arg1);
        final LispValue argList2 = lisp.makeList(arg1,arg2);
        final LispValue argList3 = lisp.makeList(arg1,arg2,c_key,arg3);
        final LispValue argList4 = lisp.makeList(arg1,arg2,c_key,arg3).append(lisp.makeList(d_key,arg4));
        final LispValue argList5 = lisp.makeList(arg1,arg2,d_key,arg4).append(lisp.makeList(c_key,arg3));
        final LispValue argList6 = lisp.makeList(arg1,arg2,d_key,arg4).append(lisp.makeList(c_key,arg3,testing_key,lisp.T));

        final Map outp1 = list7.parse(argList1);
        final Map outp2 = list7.parse(argList2);
        final Map outp3 = list7.parse(argList3);
        final Map outp4 = list7.parse(argList4);
        final Map outp5 = list7.parse(argList5);
        final Map outp6 = list7.parse(argList6);

        final LispValue expected3 = lisp.makeList(c_key,arg3);
        final LispValue expected4 = lisp.makeList(c_key,arg3,d_key,arg4);
        final LispValue expected5 = lisp.makeList(d_key,arg4,c_key,arg3);
        final LispValue expected6 = lisp.makeList(d_key,arg4,c_key,arg3).append(lisp.makeList(testing_key,lisp.T));

        assertEquals("Argument 1 should be correct",arg1,outp1.get(a_sym));
        assertEquals("Argument 2 should be correct",list7_b_default,outp1.get(b_sym));
        assertEquals("Argument x should be correct",lisp.NIL,outp1.get(x_sym));
        assertEquals("Argument c should be correct",lisp.NIL,outp1.get(c_sym));
        assertEquals("Argument d should be correct",arg1,outp1.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp2.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp2.get(b_sym));
        assertEquals("Argument x should be correct",lisp.NIL,outp2.get(x_sym));
        assertEquals("Argument c should be correct",lisp.NIL,outp2.get(c_sym));
        assertEquals("Argument d should be correct",arg1,outp2.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp3.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp3.get(b_sym));
        assertTrue("Argument x should be correct",expected3.equal((LispValue)outp3.get(x_sym)) == lisp.T);
        assertEquals("Argument c should be correct",arg3,outp3.get(c_sym));
        assertEquals("Argument d should be correct",arg1,outp3.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp4.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp4.get(b_sym));
        assertTrue("Argument x should be correct",expected4.equal((LispValue)outp4.get(x_sym)) == lisp.T);
        assertEquals("Argument c should be correct",arg3,outp4.get(c_sym));
        assertEquals("Argument d should be correct",arg4,outp4.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp5.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp5.get(b_sym));
        assertTrue("Argument x should be correct",expected5.equal((LispValue)outp5.get(x_sym)) == lisp.T);
        assertEquals("Argument c should be correct",arg3,outp5.get(c_sym));
        assertEquals("Argument d should be correct",arg4,outp5.get(d_sym));

        assertEquals("Argument 1 should be correct",arg1,outp6.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp6.get(b_sym));
        assertTrue("Argument x should be correct",expected6.equal((LispValue)outp6.get(x_sym)) == lisp.T);
        assertEquals("Argument c should be correct",arg3,outp6.get(c_sym));
        assertEquals("Argument d should be correct",arg4,outp6.get(d_sym));
    }

    public void testFullKey() {
        // (a &key ((:c int) 13 supp))
        // arguments to test:
        // (17)
        // (17 :c 'z)
        // (17 :c 13)
        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue arg2 = lisp.EVAL.intern("z");

        final LispValue argList1 = lisp.makeList(arg1);
        final LispValue argList2 = lisp.makeList(arg1,c_key,arg2);
        final LispValue argList3 = lisp.makeList(arg1,c_key,list8_c_default);

        final Map outp1 = list8.parse(argList1);
        final Map outp2 = list8.parse(argList2);
        final Map outp3 = list8.parse(argList3);

        assertEquals("Argument 1 should be correct",arg1,outp1.get(a_sym));
        assertEquals("Argument 2 should be correct",list8_c_default,outp1.get(int_sym));
        assertEquals("Supplied2 should be correct",lisp.NIL,outp1.get(supp_sym));

        assertEquals("Argument 1 should be correct",arg1,outp2.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp2.get(int_sym));
        assertEquals("Supplied2 should be correct",lisp.T,outp2.get(supp_sym));

        assertEquals("Argument 1 should be correct",arg1,outp3.get(a_sym));
        assertEquals("Argument 2 should be correct",list8_c_default,outp3.get(int_sym));
        assertEquals("Supplied2 should be correct",lisp.T,outp3.get(supp_sym));
    }

    public void testSupplyAllowOtherKeys() {
        // (a &key ((:c int) 13 supp))
        // arguments to test:
        // (17 :c 3 :allow-other-keys t :start 14)
        // (17 :c 3 :start 14 :allow-other-keys t)
        final LispValue arg1 = lisp.makeInteger(17);
        final LispValue arg2 = lisp.makeInteger(3);
        final LispValue all_ = lisp.EVAL.intern(":ALLOW-OTHER-KEYS");
        final LispValue start_ = lisp.EVAL.intern(":START");
        final LispValue start_arg = lisp.makeInteger(14);

        final LispValue argList1 = lisp.makeList(arg1,c_key,arg2,all_).append(lisp.makeList(lisp.T,start_,start_arg));
        final LispValue argList2 = lisp.makeList(arg1,c_key,arg2,start_).append(lisp.makeList(start_arg,all_,lisp.T));

        final Map outp1 = list8.parse(argList1);
        final Map outp2 = list8.parse(argList2);

        assertEquals("Argument 1 should be correct",arg1,outp1.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp1.get(int_sym));
        assertEquals("Supplied2 should be correct",lisp.T,outp1.get(supp_sym));

        assertEquals("Argument 1 should be correct",arg1,outp2.get(a_sym));
        assertEquals("Argument 2 should be correct",arg2,outp2.get(int_sym));
        assertEquals("Supplied2 should be correct",lisp.T,outp2.get(supp_sym));
    }

    public void testAuxArgument() {
        final Map outp = list9.parse(lisp.NIL);
        assertEquals("Aux argument should be correct",lisp.makeInteger(13).toStringSimple(),((LispValue)outp.get(a_sym)).toStringSimple());
    }
}// ArgumentTest
