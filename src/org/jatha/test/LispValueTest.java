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
/* Copyright (c) 1996, 1997    Micheal Hewett   hewett@cs.stanford.edu
 *
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 *  This source code is copyrighted as shown above.  If this
 *  code was obtained as part of a freeware or shareware release,
 *  assume that the provisions of the Gnu "copyleft" agreement
 *  apply.
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
 *
 *        File:  LispValueTest.java
 *
 *      Author:  Micheal Hewett
 *     Created:  13 Jan 1997
 *
 *    Compiler:  javac 1.0.2
 *
 * Description:  Test program for the LispValue package
 *
 ****************************************************************************
 *
 *  Classes:
 *
 *     LispValueTest   (has a main() function).
 *
 *
 ****************************************************************************
 */

package org.jatha.test;

import java.applet.Applet;
import java.io.IOException;

import org.jatha.Jatha;
import org.jatha.dynatype.LispInteger;
import org.jatha.dynatype.LispReal;
import org.jatha.dynatype.LispString;
import org.jatha.dynatype.LispSymbol;
import org.jatha.dynatype.LispValue;


public class LispValueTest extends Applet
{
  public static void main(String argv[])
  {
    LispTester tester = new LispTester();

    Jatha lisp = new Jatha(false, false);

    lisp.init();
    lisp.start();

    tester.test(lisp);
  }

}

class LispTester
{

  public void show(String label, LispValue value)
  {
    System.out.print(label);
    value.print();    // System.out;
    System.out.println();
  }


  public void test(Jatha lisp)
  {


    // TEST SYMBOLS
    LispSymbol  x1 = lisp.makeSymbol("Mike");

    LispValue   l1 = lisp.makeCons(x1,  lisp.T);
    LispValue   l2 = lisp.makeCons(lisp.NIL, l1);

    show("L2: ", l2);

    LispValue   l3 = lisp.makeCons(x1, lisp.NIL);
    LispValue   l4 = lisp.makeCons(lisp.T,  l3);

    show("L4: ", l4);

    LispValue   l5 = lisp.makeCons(x1, lisp.NIL);
    LispValue   l6 = lisp.makeCons(l3, l1);

    show("L6: ", l6);


    // TEST Numbers

    LispInteger  i1 = lisp.makeInteger();
    LispInteger  i2 = lisp.makeInteger(5);
    LispInteger  i3 = lisp.makeInteger(273);

    LispValue   l7 = lisp.makeCons(i3, lisp.makeList(i2, i1));
    show("L7: ", l7);

    LispReal  r1 = lisp.makeReal();
    LispReal  r2 = lisp.makeReal(5.5);
    LispReal  r3 = lisp.makeReal(273.273273);

    LispValue   l8 = lisp.makeList(r3, r2, r1);
    show("L8: ", l8);

    // TEST Strings

    LispString  s1 = lisp.makeString("BAZ");
    LispString  s2 = lisp.makeString("bar");
    LispString  s3 = lisp.makeString("foo");

    LispValue   l9 = lisp.makeCons(s3,
				    lisp.makeCons(s2,
						   lisp.makeCons(s1, lisp.makeList(r3, r2, r1))));
    show("L9: ", l9);

    LispValue   l10 = lisp.makeList(s3, s2);
    show("L10: ", l10);

    // Added 23 Feb 2006 (mh)
//    try {
//      String expr = "(* 5 B)";
//      LispValue a = lisp.makeSymbol("A");
//      a.set_special(true);
//      a.setf_symbol_value(lisp.makeInteger(7));
//      System.out.println("A = " + a.symbol_value());
//
//      LispValue B       = lisp.eval("B");
//      LispValue globals = lisp.makeList(lisp.makeList(lisp.makeCons(lisp.makeSymbol("B"), lisp.makeInteger(7))));
//      LispValue input  = lisp.parse(expr);
//      LispValue result = lisp.eval(input, globals);
//      System.out.println(input + " = " + result);
//    } catch (IOException ioe) {
//      System.err.println("Exception during test ");
//    }

    // Test #1
    System.out.println(lisp.eval("(let ((x 7)) (* 5 x)))"));
    System.out.println(lisp.eval("(progn (setq x 7) (* 5 x))"));
    System.out.println(lisp.eval("(setq x 7)"));
    System.out.println(lisp.eval("(* 5 x)"));

    // Test #2
    try {
      String expr = "(let ((x 7)) (* 5 x)))";
      LispValue input = lisp.parse(expr);
      LispValue result = lisp.eval(input);
      System.out.println(input + " = " + result);
    } catch (IOException ioe) {
      System.err.println("Exception during test ");
    }

  }
}


/*

void
cSimbaApp::test_lisp_functions(void)
{
  // Test LISP functions

  LispString str1 = new LispString("testing");
  cout << (char )(str1) << endl;

  cout << "Mike = " << (long)(intern("Mike")) << endl;
  cout << "Mike = " << (long)(intern("Mike")) << endl;

  LispValue aList = list(3, lisp.makeCons(intern("Mike"),   makeInteger(38)),
  lisp.makeCons(intern("Allie"),  makeInteger(4)),
  lisp.makeCons(intern("Sowmya"), makeInteger(29)));
  cout << "ALIST = " << aList << endl;

  LispValue value = assoc(intern("Mike"), aList);
  cout << "Assoc returned: " << value << endl;

  LispValue list4 = list(5, intern("Alpha"), intern("Beta"), intern("Delta"), intern("Gamma"), intern("Zeta"));
  cout << "Member(Beta) returned: " << (member(intern("Beta"), list4)) << endl;
  cout << "Member(Tau) returned: "  << (member(intern("Tau"), list4)) << endl;

  cout << "remove(Beta) returned: " << (remove(intern("Beta"), list4)) << endl;
  cout << "remove(Tau) returned: "  << (remove(intern("Tau"), list4)) << endl;

  cout << "remove(Alpha) returned: " << (remove(intern("Alpha"), list4)) << endl;
  cout << "remove(Zeta) returned: "  << (remove(intern("Zeta"), list4)) << endl;

  cout << "subst(Delta, Mike)  returned: " << (subst(intern("Delta"), intern("Mike"), list4)) << endl;
  cout << "subst(Foo, Mike)    returned: " << (subst(intern("Foo"), intern("Mike"), list4)) << endl;
  cout << "subst(Gamma, Allie) returned: " << (subst(intern("Gamma"), intern("Allie"), list4)) << endl;

}

*/

