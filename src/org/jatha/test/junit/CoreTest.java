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
package org.jatha.test.junit;

import java.io.*;

import junit.framework.*;

import org.jatha.Jatha;
import org.jatha.read.LispParser;
import org.jatha.dynatype.*;

/**
 * Core functionality tests for Jatha.
 * Note: This needs JUnit to compile, from http://www.junit.org/.
 * User: hewett
 * Date: Dec 12, 2002
 * Time: 8:47:18 AM
 * To change this template use Options | File Templates.
 */
public class CoreTest extends TestCase
{
  protected static Jatha     f_lisp;

  protected LispValue f_NIL;
  protected LispValue f_T;
  protected LispValue f_Bignum;
  protected LispValue f_Cons;
  protected LispValue f_Constant;
  protected LispValue f_Function;
  protected LispValue f_HashTable;
  protected LispValue f_Integer;
  protected LispValue f_Keyword;
  protected LispValue f_List;
  protected LispValue f_Number;
  protected LispValue f_QuotedList;
  protected LispValue f_Package;
  protected LispValue f_Real;
  protected LispValue f_String;
  protected LispValue f_Symbol;

  protected LispValue A;
  protected LispValue B;
  protected LispValue C;
  protected LispValue D;
  protected LispValue E;
  protected LispValue F;
  protected LispValue G;

  protected LispValue ONE;
  protected LispValue TWO;
  protected LispValue THREE;
  protected LispValue FOUR;
  protected LispValue FIVE;


  /**
   * Use -gui to enable the gui.
   * @param args command-line arguments.
   */
  public static void main (String[] args)
  {
    boolean useGui = false;  // default behavior

    if (args.length > 0)
      for (int i=0; i<args.length; ++i)
        if (args[i].equalsIgnoreCase("-gui"))
          useGui = true;

    if (useGui)
      junit.swingui.TestRunner.run(CoreTest.class);
    else
      junit.textui.TestRunner.run(suite());
  }


  protected void setUp()
      throws Exception
  {
    f_lisp = new Jatha(false, false);  // no Jatha gui
    f_lisp.init();
    f_lisp.start();

    try {
      A = f_lisp.parse("A");
      B = f_lisp.parse("B");
      C = f_lisp.parse("C");
      D = f_lisp.parse("D");
      E = f_lisp.parse("E");
    } catch (EOFException eof) {
      throw new Exception("Can't parse symbols.");
    }

    ONE    = new StandardLispInteger(f_lisp, 1);
    TWO    = new StandardLispInteger(f_lisp, 2);
    THREE  = new StandardLispInteger(f_lisp, 3);
    FOUR   = new StandardLispInteger(f_lisp, 4);
    FIVE   = new StandardLispInteger(f_lisp, 5);

    f_NIL = f_lisp.NIL;
    f_T   = f_lisp.T;
    f_Bignum     = new StandardLispBignum(f_lisp, Long.MAX_VALUE);
    f_Cons       = new StandardLispCons(f_lisp, A, B);
    f_Constant   = new StandardLispConstant(f_lisp, "CONSTANT");
    f_Function   = f_lisp.parse("CAR").symbol_function();
    f_HashTable  = new StandardLispHashTable(f_lisp);
    f_Integer    = new StandardLispInteger(f_lisp, 614);
    f_Keyword    = f_lisp.parse(":HELLO");
    f_List       = f_lisp.makeList(A, B, C);
    f_Package    = new StandardLispPackage(f_lisp, "TEST-PACKAGE");
    f_QuotedList = f_lisp.makeList(f_lisp.QUOTE,
                                   f_lisp.makeList(A, B, C));
    f_Real       = new StandardLispReal(f_lisp, 6.14);
    f_String     = new StandardLispString(f_lisp, "a test string");
    f_Symbol     = new StandardLispSymbol(f_lisp, "TEST-SYMBOL");
  }

  public static Test suite()
  {
    return new TestSuite(CoreTest.class);
  }

  public void testBasicAtom()
  {
    assertTrue(A.basic_atom());
    assertTrue(ONE.basic_atom());
    assertTrue(f_NIL.basic_atom());
    assertTrue(f_T.basic_atom());
    assertTrue(f_Bignum.basic_atom());
    assertFalse(f_Cons.basic_atom());
    assertTrue(f_Constant.basic_atom());
    assertFalse(f_Function.basic_atom());
    assertFalse(f_HashTable.basic_atom());
    assertTrue(f_Integer.basic_atom());
    assertTrue(f_Keyword.basic_atom());
    assertFalse(f_List.basic_atom());
    assertFalse(f_Package.basic_atom());
    assertTrue(f_Real.basic_atom());
    assertTrue(f_String.basic_atom());
    assertTrue(f_Symbol.basic_atom());
  }

  public void testBasicBignump()
  {
    assertFalse(A.basic_bignump());
    assertFalse(ONE.basic_bignump());
    assertFalse(f_NIL.basic_bignump());
    assertFalse(f_T.basic_bignump());
    assertTrue(f_Bignum.basic_bignump());
    assertFalse(f_Cons.basic_bignump());
    assertFalse(f_Constant.basic_bignump());
    assertFalse(f_Function.basic_bignump());
    assertFalse(f_HashTable.basic_bignump());
    assertFalse(f_Integer.basic_bignump());
    assertFalse(f_Keyword.basic_bignump());
    assertFalse(f_List.basic_bignump());
    assertFalse(f_Package.basic_bignump());
    assertFalse(f_Real.basic_bignump());
    assertFalse(f_String.basic_bignump());
    assertFalse(f_Symbol.basic_bignump());
  }

  public void testBasicConsp()
  {
    assertFalse(A.basic_consp());
    assertFalse(ONE.basic_consp());
    assertFalse(f_NIL.basic_consp());
    assertFalse(f_T.basic_consp());
    assertFalse(f_Bignum.basic_consp());
    assertTrue(f_Cons.basic_consp());
    assertFalse(f_Constant.basic_consp());
    assertTrue(((LispFunction)f_Function).getCode().basic_consp());
    assertFalse(f_HashTable.basic_consp());
    assertFalse(f_Integer.basic_consp());
    assertFalse(f_Keyword.basic_consp());
    assertTrue(f_List.basic_consp());
    assertFalse(f_Package.basic_consp());
    assertTrue(f_QuotedList.basic_consp());
    assertFalse(f_Real.basic_consp());
    assertFalse(f_String.basic_consp());
    assertFalse(f_Symbol.basic_consp());
  }

  public void testBasicConstantp()
  {
    assertFalse(A.basic_constantp());
    assertTrue(ONE.basic_constantp());
    assertTrue(f_NIL.basic_constantp());
    assertTrue(f_T.basic_constantp());
    assertTrue(f_Bignum.basic_constantp());
    assertFalse(f_Cons.basic_constantp());
    assertTrue(f_Constant.basic_constantp());
    assertFalse(f_Function.basic_constantp());
    assertFalse(f_HashTable.basic_constantp());
    assertTrue(f_Integer.basic_constantp());
    assertTrue(f_Keyword.basic_constantp());
    assertFalse(f_List.basic_constantp());
    assertFalse(f_Package.basic_constantp());
    assertTrue(f_QuotedList.basic_constantp());
    assertTrue(f_Real.basic_constantp());
    assertTrue(f_String.basic_constantp());
    assertFalse(f_Symbol.basic_constantp());
  }

  public void testBasicForeignp()
  {
    assertFalse(A.basic_foreignp());
    assertFalse(ONE.basic_foreignp());
    assertFalse(f_NIL.basic_foreignp());
    assertFalse(f_T.basic_foreignp());
    assertFalse(f_Bignum.basic_foreignp());
    assertFalse(f_Cons.basic_foreignp());
    assertFalse(f_Constant.basic_foreignp());
    assertFalse(f_Function.basic_foreignp());
    assertFalse(f_HashTable.basic_foreignp());
    assertFalse(f_Integer.basic_foreignp());
    assertFalse(f_Keyword.basic_foreignp());
    assertFalse(f_List.basic_foreignp());
    assertFalse(f_Package.basic_foreignp());
    assertFalse(f_Real.basic_foreignp());
    assertFalse(f_String.basic_foreignp());
    assertFalse(f_Symbol.basic_foreignp());
  }

  public void testBasicIntegerp()
  {
    assertFalse(A.basic_integerp());
    assertTrue(ONE.basic_integerp());
    assertFalse(f_NIL.basic_integerp());
    assertFalse(f_T.basic_integerp());
    assertTrue(f_Bignum.basic_integerp());
    assertFalse(f_Cons.basic_integerp());
    assertFalse(f_Constant.basic_integerp());
    assertFalse(f_Function.basic_integerp());
    assertFalse(f_HashTable.basic_integerp());
    assertTrue(f_Integer.basic_integerp());
    assertFalse(f_Keyword.basic_integerp());
    assertFalse(f_List.basic_integerp());
    assertFalse(f_Package.basic_integerp());
    assertFalse(f_Real.basic_integerp());
    assertFalse(f_String.basic_integerp());
    assertFalse(f_Symbol.basic_integerp());
  }

  public void testBasicFunctionp()
  {
    assertFalse(A.basic_functionp());
    assertFalse(ONE.basic_functionp());
    assertFalse(f_NIL.basic_functionp());
    assertFalse(f_T.basic_functionp());
    assertFalse(f_Bignum.basic_functionp());
    assertFalse(f_Cons.basic_functionp());
    assertFalse(f_Constant.basic_functionp());
    assertTrue(f_Function.basic_functionp());
    assertFalse(f_HashTable.basic_functionp());
    assertFalse(f_Integer.basic_functionp());
    assertFalse(f_Keyword.basic_functionp());
    assertFalse(f_List.basic_functionp());
    assertFalse(f_Package.basic_functionp());
    assertFalse(f_Real.basic_functionp());
    assertFalse(f_String.basic_functionp());
    assertFalse(f_Symbol.basic_functionp());
  }

  public void testBasicKeywordp()
  {
    assertFalse(A.basic_keywordp());
    assertFalse(ONE.basic_keywordp());
    assertFalse(f_NIL.basic_keywordp());
    assertFalse(f_T.basic_keywordp());
    assertFalse(f_Bignum.basic_keywordp());
    assertFalse(f_Cons.basic_keywordp());
    assertFalse(f_Constant.basic_keywordp());
    assertFalse(f_Function.basic_keywordp());
    assertFalse(f_HashTable.basic_keywordp());
    assertFalse(f_Integer.basic_keywordp());
    assertTrue(f_Keyword.basic_keywordp());
    assertFalse(f_List.basic_keywordp());
    assertFalse(f_Package.basic_keywordp());
    assertFalse(f_Real.basic_keywordp());
    assertFalse(f_String.basic_keywordp());
    assertFalse(f_Symbol.basic_keywordp());
  }

  @SuppressWarnings("unused")
  public void testBasicLength()
  {
    int l = 0;

    try {
      l = A.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    try { l = ONE.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    assertTrue(0 == f_NIL.basic_length());

    try { l = f_T.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    try { l = f_Bignum.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    try { l = f_Cons.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    try { l = f_Constant.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    assertTrue(1 < ((LispFunction)f_Function).getCode().basic_length());

    try { l = f_HashTable.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    try { l = f_Integer.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    try { l = f_Keyword.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}


    assertTrue(3 == f_List.basic_length());

    try { l = f_Package.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    assertTrue(2 == f_QuotedList.basic_length());

    try { l = f_Real.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

    assertTrue(13 == f_String.basic_length());

    try { l = f_Symbol.basic_length();
      fail("Should have thrown a LispValueNotAListException.");
    } catch (LispValueNotAListException e) {}

  }

  public void testBasicListp()
  {
    assertFalse(A.basic_listp());
    assertFalse(ONE.basic_listp());
    assertTrue(f_NIL.basic_listp());
    assertFalse(f_T.basic_listp());
    assertFalse(f_Bignum.basic_listp());
    assertTrue(f_Cons.basic_listp());
    assertFalse(f_Constant.basic_listp());
    assertFalse(f_Function.basic_listp());
    assertFalse(f_HashTable.basic_listp());
    assertFalse(f_Integer.basic_listp());
    assertFalse(f_Keyword.basic_listp());
    assertTrue(f_List.basic_listp());
    assertFalse(f_Package.basic_listp());
    assertFalse(f_Real.basic_listp());
    assertFalse(f_String.basic_listp());
    assertFalse(f_Symbol.basic_listp());
  }

  public void testBasicMacrop()
  {
    assertFalse(A.basic_macrop());
    assertFalse(ONE.basic_macrop());
    assertFalse(f_NIL.basic_macrop());
    assertFalse(f_T.basic_macrop());
    assertFalse(f_Bignum.basic_macrop());
    assertFalse(f_Cons.basic_macrop());
    assertFalse(f_Constant.basic_macrop());
    assertFalse(f_Function.basic_macrop());
    assertFalse(f_HashTable.basic_macrop());
    assertFalse(f_Integer.basic_macrop());
    assertFalse(f_Keyword.basic_macrop());
    assertFalse(f_List.basic_macrop());
    assertFalse(f_Package.basic_macrop());
    assertFalse(f_Real.basic_macrop());
    assertFalse(f_String.basic_macrop());
    assertFalse(f_Symbol.basic_macrop());

    LispParser parser = new LispParser(f_lisp, "(defmacro aaa (x) `(+ ,x 15)) ");
    LispValue macro = f_lisp.NIL;
    LispValue value = f_lisp.NIL;
    try {
      macro = parser.parse();
      assertFalse(macro.basic_null());
      value = f_lisp.eval(macro);
      assertFalse(value.basic_null());
    } catch (Exception e) {
      fail("Can't create macro: " + e.getMessage());
    }

    parser = new LispParser(f_lisp, "(aaa 42)");
    try {
      macro = parser.parse();
      assertFalse(macro.basic_null());
      value = f_lisp.eval(macro);
      assertTrue(value.equalNumeric(f_lisp.makeInteger(57)) == f_lisp.T);
    } catch (Exception e2) {
      fail("Can't evaluate macro: " + e2.getMessage());
    }
  }

  public void testBasicNull()
  {
    assertFalse(A.basic_null());
    assertFalse(ONE.basic_null());
    assertTrue(f_NIL.basic_null());
    assertFalse(f_T.basic_null());
    assertFalse(f_Bignum.basic_null());
    assertFalse(f_Cons.basic_null());
    assertFalse(f_Constant.basic_null());
    assertFalse(f_Function.basic_null());
    assertFalse(f_HashTable.basic_null());
    assertFalse(f_Integer.basic_null());
    assertFalse(f_Keyword.basic_null());
    assertFalse(f_List.basic_null());
    assertFalse(f_Package.basic_null());
    assertFalse(f_Real.basic_null());
    assertFalse(f_String.basic_null());
    assertFalse(f_Symbol.basic_null());
  }

  public void testBasicNumberp()
  {
    assertFalse(A.basic_numberp());
    assertTrue(ONE.basic_numberp());
    assertFalse(f_NIL.basic_numberp());
    assertFalse(f_T.basic_numberp());
    assertTrue(f_Bignum.basic_numberp());
    assertFalse(f_Cons.basic_numberp());
    assertFalse(f_Constant.basic_numberp());
    assertFalse(f_Function.basic_numberp());
    assertFalse(f_HashTable.basic_numberp());
    assertTrue(f_Integer.basic_numberp());
    assertFalse(f_Keyword.basic_numberp());
    assertFalse(f_List.basic_numberp());
    assertFalse(f_Package.basic_numberp());
    assertTrue(f_Real.basic_numberp());
    assertFalse(f_String.basic_numberp());
    assertFalse(f_Symbol.basic_numberp());
  }

  public void testBasicStringp()
  {
    assertFalse(A.basic_stringp());
    assertFalse(ONE.basic_stringp());
    assertFalse(f_NIL.basic_stringp());
    assertFalse(f_T.basic_stringp());
    assertFalse(f_Bignum.basic_stringp());
    assertFalse(f_Cons.basic_stringp());
    assertFalse(f_Constant.basic_stringp());
    assertFalse(f_Function.basic_stringp());
    assertFalse(f_HashTable.basic_stringp());
    assertFalse(f_Integer.basic_stringp());
    assertFalse(f_Keyword.basic_stringp());
    assertFalse(f_List.basic_stringp());
    assertFalse(f_Package.basic_stringp());
    assertFalse(f_Real.basic_stringp());
    assertTrue(f_String.basic_stringp());
    assertFalse(f_Symbol.basic_stringp());
  }

  public void testBasicSymbolp()
  {
    assertTrue(A.basic_symbolp());
    assertFalse(ONE.basic_symbolp());
    assertTrue(f_NIL.basic_symbolp());
    assertTrue(f_T.basic_symbolp());
    assertFalse(f_Bignum.basic_symbolp());
    assertFalse(f_Cons.basic_symbolp());
    assertTrue(f_Constant.basic_symbolp());
    assertFalse(f_Function.basic_symbolp());
    assertFalse(f_HashTable.basic_symbolp());
    assertFalse(f_Integer.basic_symbolp());
    assertTrue(f_Keyword.basic_symbolp());
    assertFalse(f_List.basic_symbolp());
    assertFalse(f_Package.basic_symbolp());
    assertFalse(f_Real.basic_symbolp());
    assertFalse(f_String.basic_symbolp());
    assertTrue(f_Symbol.basic_symbolp());
  }

  /* Tests for all LISP functions */
  public void testAppend()
  {
    LispValue l1 = f_lisp.makeList(A, B, C, D);
    LispValue l2 = f_lisp.makeList(D, C, B, A);
    LispValue l3 = l1.append(l2);

    assertTrue(8 == l3.basic_length());
    assertTrue(C.equal(l3.sixth()) == f_lisp.T);
    assertTrue(l3.first().equal(l3.last().car()) == f_lisp.T);
    assertTrue(l3.eq(l1).basic_null());
    assertTrue(l2.eq(l3.cdr().cdr().cdr().cdr()) == f_lisp.T);
    assertTrue(l3.reverse().equal(l3) != f_lisp.NIL);
  }

  public void testapply()
  {}
  public void testassoc()
  {  }

  public void testatom() {}

  public void testbignump() {}

  public void testboundp()
  {}
  public void testbutlast()
  {}
  public void testcar()
  {}
  public void testcdr()
  {}
  public void testcharacterp() {}

  public void testclrhash()
  {}

  public void testConcatenate()
  {

    try {
      LispValue in = f_lisp.parse("(concatenate 'STRING \"foo\" \" \" \"bar\")");
      LispValue out = f_lisp.eval(in);
      //System.err.println("out = " + out);
      assertTrue(out.toStringSimple().equals("foo bar"));

      in = f_lisp.parse("(concatenate 'STRING \"foo\" \" \" \"bar\" \" \" 'Mike)");
      out = f_lisp.eval(in);
      //System.err.println("out = " + out);
      assertTrue(out.toStringSimple().equals("foo bar MIKE"));


      in = f_lisp.parse("(concatenate 'STRING 'Mike \" foo\" \" \" \"bar\")");
      out = f_lisp.eval(in);
      //System.err.println("out = " + out);
      assertTrue(out.toStringSimple().equals("MIKE foo bar"));

      in = f_lisp.parse("(concatenate 'STRING)");
      out = f_lisp.eval(in);
      //System.err.println("out = " + out);
      assertTrue(out.toStringSimple().equals(""));

    } catch (Exception e) {
      System.err.println("in testConcatenate: " + e.getMessage());
      assertTrue(false);
    }
  }

  public void testconsp() {}

  public void testcopy_list()
  {}

  public void testeighth()
  {}

  public void testelt()
  {}
  public void testeq()
  {
  }

  public void testeql()
  {
  }

  public void testequal()
  {
  }

  public void testfboundp()
  {}
  public void testfloatp()
  {}
  public void testfifth()
  {}
  public void testfirst()
  {}
  public void testfourth()
  {}
  public void testfuncall()
  {}
  public void testgethash()
  {}
  public void testsetf_gethash()
  {}
  public void testhashtablep() {}

  public void testhash_table_count()
  {}
  public void testhash_table_size()
  {}
  public void testhash_table_rehash_size()
  {}
  public void testhash_table_rehash_threshold()
  {}
  public void testhash_table_test()
  {}
  public void testintegerp()  {  }

  public void testkeywordp()  {  }

  public void testlast()
  {}
  public void testlength()
  {}
  public void testlisp_null() {  }

  public void testlist()  {  }

  public void testlistp()
  {}

  public void testmax1()
  {
    assertTrue(ONE.max(f_lisp.makeList(ONE, TWO, THREE, FOUR)).eql(FOUR) == f_lisp.T);
  }

  public void testmax2()
  {
    assertFalse(ONE.max(TWO).eql(ONE) == f_lisp.T);
  }

  public void testmax3()
  {
    assertTrue(ONE.max(TWO).eql(TWO) == f_lisp.T);
  }

  public void testmax4()
  {
    assertTrue(TWO.max(ONE).eql(TWO) == f_lisp.T);
  }

  public void testmax5()
  {
    assertFalse(TWO.max(ONE).eql(ONE) == f_lisp.T);
  }

  public void testmax6()
  {
    LispValue A = f_lisp.makeReal(1.4);
    LispValue B = f_lisp.makeReal(14.6);
    LispValue C = f_lisp.makeReal(17.7);

    assertTrue(B.max(f_lisp.makeList(A, ONE, C)).eql(C) == f_lisp.T);
  }


  public void testmin1()
  {
    assertTrue(ONE.min(f_lisp.makeList(ONE, TWO, THREE, FOUR)).eql(ONE) == f_lisp.T);
  }

  public void testmin2()
  {
    assertFalse(ONE.min(TWO).eql(TWO) == f_lisp.T);
  }

  public void testmin3()
  {
    assertTrue(ONE.min(TWO).eql(ONE) == f_lisp.T);
  }

  public void testmin4()
  {
    assertTrue(TWO.min(ONE).eql(ONE) == f_lisp.T);
  }

  public void testmin5()
  {
    assertFalse(TWO.min(ONE).eql(TWO) == f_lisp.T);
  }

  public void testmin6()
  {
    LispValue A = f_lisp.makeReal(1.4);
    LispValue B = f_lisp.makeReal(14.6);
    LispValue C = f_lisp.makeReal(17.7);

    assertTrue(B.min(f_lisp.makeList(A, TWO, C)).eql(A) == f_lisp.T);
  }

  public void testmember()
  {}
  public void testnconc()
  {}
  public void testninth()
  {}
  public void testnreverse()
  {}
  public void testnumberp() { }

  public void testpop()
  {
    LispValue l1 = f_lisp.makeList(A, B, C);
    LispValue l2 = f_lisp.makeList(A, B, C);

    assertTrue(f_lisp.T == A.eq(l1.pop()));
    assertTrue(2 == l1.basic_length());
    assertTrue(3 == l1.push(D).basic_length());
    assertTrue(D == l1.pop());
    assertTrue(2 == l1.basic_length());
    assertTrue(B == l1.pop());
    assertTrue(1 == l1.basic_length());
    assertTrue(C == l1.pop());
    assertTrue(f_lisp.T == l1.equal(new StandardLispCons(f_lisp, f_lisp.NIL, f_lisp.NIL)));
    // assertTrue(LispValue.NIL == l1);  // Jatha can't do this correctly.

    // Nowever, this works.
    l1 = new StandardLispSymbol(f_lisp, "L1");
    l1.setq(f_lisp.makeList(A, B, C));
    assertTrue(f_lisp.T == A.eq(l1.pop()));
    assertTrue(2 == l1.symbol_value().basic_length());

    assertTrue(f_lisp.T == B.eq(l1.pop()));
    assertTrue(1 == l1.symbol_value().basic_length());

    assertTrue(f_lisp.T == C.eq(l1.pop()));
    assertTrue(0 == l1.symbol_value().basic_length());
    assertTrue(l1.symbol_value().basic_null());

    l1.push(C);
    assertTrue(1 == l1.symbol_value().basic_length());
    l1.push(B);
    assertTrue(2 == l1.symbol_value().basic_length());
    l1.push(A);
    assertTrue(3 == l1.symbol_value().basic_length());
    assertTrue(f_lisp.T == l1.symbol_value().equal(f_lisp.makeList(A, B, C)));
    assertTrue(f_lisp.T == l1.symbol_value().equal(l2));
  }

  public void testprin1()
  {}
  public void testprinc()
  {}
  public void testprint()
  {}


  public void testpush()
  {
    LispValue l1 = new StandardLispSymbol(f_lisp, "L1");
    LispValue l2 = f_lisp.makeList(A, B);

    l1.setq(f_lisp.NIL);
    l1.push(B); // works correctly.  The value of L1 is now (B).
    l1.push(A); // works correctly.  The value of L1 is now (A B).
    assertTrue(l1.symbol_value().equal(l2) == f_lisp.T);
  }


  public void testrassoc()
  {}
  public void testremhash()
  {}
  public void testremove()
  {}
  public void testreverse()
  {}
  public void testrplaca()
  {}
  public void testrplacd()
  {}
  public void testsecond()
  {}
  public void testsetf_symbol_function()
  {}
  public void testsetf_symbol_plist()
  {}
  public void testsetf_symbol_value()
  {}
  public void testseventh()
  {}

  public void teststringp()
  {
    A = f_lisp.makeString("test");
    B = f_lisp.makeInteger(5);

    assertTrue(A.stringp() == f_lisp.T);
    assertTrue(B.stringp() == f_lisp.NIL);
  }

  public void teststring()
  {
    A = f_lisp.makeString("test");
    B = f_lisp.makeInteger(5);
    C = f_lisp.makeList(A, B);

    assertTrue(A.string().stringp() == f_lisp.T);
    try {
      assertTrue(B.string().stringp() == f_lisp.T);
      fail("Should have thrown a LispValueNotConvertableToAStringException.");
    } catch (LispValueNotConvertableToAStringException e) {}
    try {
      assertTrue(C.string().stringp() == f_lisp.T);
      fail("Should have thrown a LispValueNotConvertableToAStringException.");
    } catch (LispValueNotConvertableToAStringException e) {}
  }

  public void teststringUpcase()
  {
    A = f_lisp.makeString("test");
    B = f_lisp.makeString("Test");
    C = f_lisp.makeString("");
    D = f_lisp.makeString("x");
    E = f_lisp.makeInteger(5);

    assertTrue(((LispString)(A.stringUpcase())).getValue().equals("TEST"));
    assertTrue(((LispString)(B.stringUpcase())).getValue().equals("TEST"));
    assertTrue(((LispString)(C.stringUpcase())).getValue().equals(""));
    assertTrue(((LispString)(D.stringUpcase())).getValue().equals("X"));
    try {
      assertTrue(((LispString)(E.stringUpcase())).getValue().equals("5"));
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringDowncase()
  {
    A = f_lisp.makeString("test");
    B = f_lisp.makeString("Test");
    C = f_lisp.makeString("");
    D = f_lisp.makeString("X");
    E = f_lisp.makeInteger(5);

    assertTrue(((LispString)(A.stringDowncase())).getValue().equals("test"));
    assertTrue(((LispString)(B.stringDowncase())).getValue().equals("test"));
    assertTrue(((LispString)(C.stringDowncase())).getValue().equals(""));
    assertTrue(((LispString)(D.stringDowncase())).getValue().equals("x"));
    try {
      assertTrue(((LispString)(E.stringDowncase())).getValue().equals("5"));
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringCapitalize()
  {
    A = f_lisp.makeString("test");
    B = f_lisp.makeString("eTest");
    C = f_lisp.makeString("");
    D = f_lisp.makeString("x");
    E = f_lisp.makeInteger(5);

    assertTrue(((LispString)(A.stringCapitalize())).getValue().equals("Test"));
    assertTrue(((LispString)(B.stringCapitalize())).getValue().equals("Etest"));
    assertTrue(((LispString)(C.stringCapitalize())).getValue().equals(""));
    assertTrue(((LispString)(D.stringCapitalize())).getValue().equals("X"));
    try {
      assertTrue(((LispString)(E.stringCapitalize())).getValue().equals("5"));
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringEqual()
  {
    A = f_lisp.makeString("MiKeY");
    B = f_lisp.makeString("MiKeY");
    C = f_lisp.makeString("mikey");
    D = f_lisp.makeString("5");
    E = f_lisp.makeInteger(5);
    F = f_lisp.makeString("");
    G = f_lisp.makeString("");

    assertTrue(A.stringEqual(B) == f_lisp.T);
    assertTrue(B.stringEqual(A) == f_lisp.T);
    assertTrue(A.stringEqual(C) == f_lisp.T);
    assertTrue(C.stringEqual(A) == f_lisp.T);
    assertTrue(D.stringEqual(C) == f_lisp.NIL);
    assertTrue(F.stringEqual(G) == f_lisp.T);
    assertTrue(F.stringEqual(D) == f_lisp.NIL);
    assertTrue(D.stringEqual(F) == f_lisp.NIL);

    try {
      assertTrue(E.stringEqual(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringEqual(E) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringEq()
  {
    A = f_lisp.makeString("MiKeY");
    B = f_lisp.makeString("MiKeY");
    C = f_lisp.makeString("mikey");
    D = f_lisp.makeString("5");
    E = f_lisp.makeInteger(5);
    F = f_lisp.makeString("");
    G = f_lisp.makeString("");

    assertTrue(A.stringEq(B) == f_lisp.T);
    assertTrue(B.stringEq(A) == f_lisp.T);
    assertTrue(A.stringEq(C) == f_lisp.NIL);
    assertTrue(C.stringEq(A) == f_lisp.NIL);
    assertTrue(D.stringEq(C) == f_lisp.NIL);
    assertTrue(F.stringEq(G) == f_lisp.T);
    assertTrue(F.stringEq(D) == f_lisp.NIL);
    assertTrue(D.stringEq(F) == f_lisp.NIL);

    try {
      assertTrue(E.stringEq(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringEq(E) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringNeq()
  {
    A = f_lisp.makeString("MiKeY");
    B = f_lisp.makeString("MiKeY");
    C = f_lisp.makeString("mikey");
    D = f_lisp.makeString("5");
    E = f_lisp.makeInteger(5);
    F = f_lisp.makeString("");
    G = f_lisp.makeString("");

    assertTrue(A.stringNeq(B) == f_lisp.NIL);
    assertTrue(B.stringNeq(A) == f_lisp.NIL);
    assertTrue(A.stringNeq(C) == f_lisp.T);
    assertTrue(C.stringNeq(A) == f_lisp.T);
    assertTrue(D.stringNeq(C) == f_lisp.T);
    assertTrue(F.stringNeq(G) == f_lisp.NIL);
    assertTrue(F.stringNeq(D) == f_lisp.T);
    assertTrue(D.stringNeq(F) == f_lisp.T);

    try {
      assertTrue(E.stringNeq(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringNeq(E) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringLessThan()
  {
    A = f_lisp.makeString("");
    B = f_lisp.makeString("a");
    C = f_lisp.makeString("ab");
    D = f_lisp.makeString("ba");
    E = f_lisp.makeString("bb");
    F = f_lisp.makeString("bbbbbbbz");
    G = f_lisp.makeInteger(5);

    LispValue B1 = f_lisp.makeString("A");
    LispValue E1 = f_lisp.makeString("BB");
    LispValue F1 = f_lisp.makeString("BBBBBBBZ");

    assertTrue(A.stringLessThan(B) == f_lisp.T);
    assertTrue(B.stringLessThan(A) == f_lisp.NIL);
    assertTrue(B.stringLessThan(C) == f_lisp.T);
    assertTrue(C.stringLessThan(D) == f_lisp.T);
    assertTrue(E.stringLessThan(F) == f_lisp.T);
    assertTrue(B.stringLessThan(B1) == f_lisp.NIL);
    assertTrue(B1.stringLessThan(B) == f_lisp.T);
    assertTrue(E1.stringLessThan(F1) == f_lisp.T);
    assertTrue(F.stringLessThan(F1) == f_lisp.NIL);

    try {
      assertTrue(G.stringLessThan(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringLessThan(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

  }

  public void teststringLessP()
  {
    A = f_lisp.makeString("");
    B = f_lisp.makeString("a");
    C = f_lisp.makeString("ab");
    D = f_lisp.makeString("ba");
    E = f_lisp.makeString("bb");
    F = f_lisp.makeString("bbbbbbbz");
    G = f_lisp.makeInteger(5);

    LispValue C1 = f_lisp.makeString("AB");
    LispValue E1 = f_lisp.makeString("BB");
    LispValue F1 = f_lisp.makeString("BBBBBBBZ");

    assertTrue(A.stringLessP(B) == f_lisp.T);
    assertTrue(B.stringLessP(A) == f_lisp.NIL);
    assertTrue(B.stringLessP(C) == f_lisp.T);
    assertTrue(C.stringLessP(D) == f_lisp.T);
    assertTrue(E.stringLessP(F) == f_lisp.T);
    assertTrue(C.stringLessP(C1) == f_lisp.NIL);
    assertTrue(C1.stringLessP(C) == f_lisp.NIL);
    assertTrue(E1.stringLessP(F1) == f_lisp.T);
    assertTrue(F.stringLessP(F1) == f_lisp.NIL);

    try {
      assertTrue(G.stringLessP(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringLessP(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringGreaterThan()
  {
    A = f_lisp.makeString("");
    B = f_lisp.makeString("a");
    C = f_lisp.makeString("ab");
    D = f_lisp.makeString("ba");
    E = f_lisp.makeString("bb");
    F = f_lisp.makeString("bbbbbbbz");
    G = f_lisp.makeInteger(5);

    LispValue B1 = f_lisp.makeString("A");
    LispValue E1 = f_lisp.makeString("BB");
    LispValue F1 = f_lisp.makeString("BBBBBBBZ");

    assertTrue(A.stringGreaterThan(B) == f_lisp.NIL);
    assertTrue(B.stringGreaterThan(A) == f_lisp.T);
    assertTrue(B.stringGreaterThan(C) == f_lisp.NIL);
    assertTrue(C.stringGreaterThan(D) == f_lisp.NIL);
    assertTrue(E.stringGreaterThan(F) == f_lisp.NIL);
    assertTrue(B.stringGreaterThan(B1) == f_lisp.T);
    assertTrue(B1.stringGreaterThan(B) == f_lisp.NIL);
    assertTrue(E1.stringGreaterThan(F1) == f_lisp.NIL);
    assertTrue(F.stringGreaterThan(F1) == f_lisp.T);

    try {
      assertTrue(G.stringGreaterThan(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringGreaterThan(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringGreaterP()
  {
    A = f_lisp.makeString("");
    B = f_lisp.makeString("a");
    C = f_lisp.makeString("ab");
    D = f_lisp.makeString("ba");
    E = f_lisp.makeString("bb");
    F = f_lisp.makeString("bbbbbbbz");
    G = f_lisp.makeInteger(5);

    LispValue C1 = f_lisp.makeString("AB");
    LispValue E1 = f_lisp.makeString("BB");
    LispValue F1 = f_lisp.makeString("BBBBBBBZ");

    assertTrue(A.stringGreaterP(B) == f_lisp.NIL);
    assertTrue(B.stringGreaterP(A) == f_lisp.T);
    assertTrue(B.stringGreaterP(C) == f_lisp.NIL);
    assertTrue(C.stringGreaterP(D) == f_lisp.NIL);
    assertTrue(E.stringGreaterP(F) == f_lisp.NIL);
    assertTrue(C.stringGreaterP(C1) == f_lisp.NIL);
    assertTrue(C1.stringGreaterP(C) == f_lisp.NIL);
    assertTrue(E1.stringGreaterP(F1) == f_lisp.NIL);
    assertTrue(F.stringGreaterP(F1) == f_lisp.NIL);

    try {
      assertTrue(G.stringGreaterP(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringGreaterP(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringLessThanOrEqual()
  {
    A = f_lisp.makeString("");
    B = f_lisp.makeString("a");
    C = f_lisp.makeString("ab");
    D = f_lisp.makeString("ba");
    E = f_lisp.makeString("bb");
    F = f_lisp.makeString("bbbbbbbz");
    G = f_lisp.makeInteger(5);

    LispValue B1 = f_lisp.makeString("A");
    LispValue E1 = f_lisp.makeString("BB");
    LispValue F1 = f_lisp.makeString("BBBBBBBZ");

    assertTrue(A.stringLessThanOrEqual(A) == f_lisp.T);
    assertTrue(A.stringLessThanOrEqual(B) == f_lisp.T);
    assertTrue(B.stringLessThanOrEqual(B) == f_lisp.T);
    assertTrue(B.stringLessThanOrEqual(A) == f_lisp.NIL);
    assertTrue(B.stringLessThanOrEqual(C) == f_lisp.T);
    assertTrue(C.stringLessThanOrEqual(D) == f_lisp.T);
    assertTrue(E.stringLessThanOrEqual(F) == f_lisp.T);
    assertTrue(B.stringLessThanOrEqual(B1) == f_lisp.NIL);
    assertTrue(B1.stringLessThanOrEqual(B) == f_lisp.T);
    assertTrue(E1.stringLessThanOrEqual(F1) == f_lisp.T);
    assertTrue(F.stringLessThanOrEqual(F1) == f_lisp.NIL);

    try {
      assertTrue(G.stringLessThanOrEqual(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringLessThanOrEqual(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringGreaterThanOrEqual()
  {
    A = f_lisp.makeString("");
    B = f_lisp.makeString("a");
    C = f_lisp.makeString("ab");
    D = f_lisp.makeString("ba");
    E = f_lisp.makeString("bb");
    F = f_lisp.makeString("bbbbbbbz");
    G = f_lisp.makeInteger(5);

    LispValue B1 = f_lisp.makeString("A");
    LispValue E1 = f_lisp.makeString("BB");
    LispValue F1 = f_lisp.makeString("BBBBBBBZ");

    assertTrue(A.stringGreaterThanOrEqual(A) == f_lisp.T);
    assertTrue(A.stringGreaterThanOrEqual(B) == f_lisp.NIL);
    assertTrue(B.stringGreaterThanOrEqual(B) == f_lisp.T);
    assertTrue(B.stringGreaterThanOrEqual(A) == f_lisp.T);
    assertTrue(B.stringGreaterThanOrEqual(C) == f_lisp.NIL);
    assertTrue(C.stringGreaterThanOrEqual(D) == f_lisp.NIL);
    assertTrue(E.stringGreaterThanOrEqual(F) == f_lisp.NIL);
    assertTrue(B.stringGreaterThanOrEqual(B1) == f_lisp.T);
    assertTrue(B1.stringGreaterThanOrEqual(B) == f_lisp.NIL);
    assertTrue(E1.stringGreaterThanOrEqual(F1) == f_lisp.NIL);
    assertTrue(F.stringGreaterThanOrEqual(F1) == f_lisp.T);

    try {
      assertTrue(G.stringGreaterThanOrEqual(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringGreaterThanOrEqual(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringNotLessP()
  {
    A = f_lisp.makeString("");
    B = f_lisp.makeString("a");
    C = f_lisp.makeString("ab");
    D = f_lisp.makeString("ba");
    E = f_lisp.makeString("bb");
    F = f_lisp.makeString("bbbbbbbz");
    G = f_lisp.makeInteger(5);

    LispValue B1 = f_lisp.makeString("A");
    LispValue E1 = f_lisp.makeString("BB");
    LispValue F1 = f_lisp.makeString("BBBBBBBZ");

    assertTrue(A.stringNotLessP(A) == f_lisp.T);
    assertTrue(A.stringNotLessP(B) == f_lisp.NIL);
    assertTrue(B.stringNotLessP(B) == f_lisp.T);
    assertTrue(B.stringNotLessP(A) == f_lisp.T);
    assertTrue(B.stringNotLessP(C) == f_lisp.NIL);
    assertTrue(C.stringNotLessP(D) == f_lisp.NIL);
    assertTrue(E.stringNotLessP(F) == f_lisp.NIL);
    assertTrue(B.stringNotLessP(B1) == f_lisp.T);
    assertTrue(B1.stringNotLessP(B) == f_lisp.T);
    assertTrue(E1.stringNotLessP(F1) == f_lisp.NIL);
    assertTrue(F1.stringNotLessP(F) == f_lisp.T);

    try {
      assertTrue(G.stringNotLessP(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringNotLessP(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringNotGreaterP()
  {
    A = f_lisp.makeString("");
    B = f_lisp.makeString("a");
    C = f_lisp.makeString("ab");
    D = f_lisp.makeString("ba");
    E = f_lisp.makeString("bb");
    F = f_lisp.makeString("bbbbbbbz");
    G = f_lisp.makeInteger(5);

    LispValue B1 = f_lisp.makeString("A");
    LispValue E1 = f_lisp.makeString("BB");
    LispValue F1 = f_lisp.makeString("BBBBBBBZ");

    assertTrue(A.stringNotGreaterP(A) == f_lisp.T);
    assertTrue(A.stringNotGreaterP(B) == f_lisp.T);
    assertTrue(B.stringNotGreaterP(B) == f_lisp.T);
    assertTrue(B.stringNotGreaterP(A) == f_lisp.NIL);
    assertTrue(B.stringNotGreaterP(C) == f_lisp.T);
    assertTrue(C.stringNotGreaterP(D) == f_lisp.T);
    assertTrue(E.stringNotGreaterP(F) == f_lisp.T);
    assertTrue(B.stringNotGreaterP(B1) == f_lisp.T);
    assertTrue(B1.stringNotGreaterP(B) == f_lisp.T);
    assertTrue(E1.stringNotGreaterP(F1) == f_lisp.T);
    assertTrue(F.stringNotGreaterP(F1) == f_lisp.T);

    try {
      assertTrue(G.stringNotGreaterP(D) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(D.stringNotGreaterP(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringEndsWith()
  {
    A = f_lisp.makeString("mikey");
    B = f_lisp.makeString("key");
    C = f_lisp.makeString("MIKEY");
    D = f_lisp.makeString("MIkey");
    E = f_lisp.makeString("mikeys");
    F = f_lisp.makeString("");
    G = f_lisp.makeInteger(5);

    assertTrue(A.stringEndsWith(B) == f_lisp.T);
    assertTrue(B.stringEndsWith(B) == f_lisp.T);
    assertTrue(C.stringEndsWith(B) == f_lisp.NIL);
    assertTrue(D.stringEndsWith(B) == f_lisp.T);
    assertTrue(E.stringEndsWith(B) == f_lisp.NIL);
    assertTrue(F.stringEndsWith(B) == f_lisp.NIL);
    assertTrue(B.stringEndsWith(F) == f_lisp.T);

    try {
      assertTrue(G.stringEndsWith(B) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(B.stringEndsWith(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringStartsWith()
  {
    A = f_lisp.makeString("mikey");
    B = f_lisp.makeString("mi");
    C = f_lisp.makeString("MIKEY");
    D = f_lisp.makeString("miKEY");
    E = f_lisp.makeString("smikey");
    F = f_lisp.makeString("");
    G = f_lisp.makeInteger(5);

    assertTrue(A.stringStartsWith(B) == f_lisp.T);
    assertTrue(B.stringStartsWith(B) == f_lisp.T);
    assertTrue(C.stringStartsWith(B) == f_lisp.NIL);
    assertTrue(D.stringStartsWith(B) == f_lisp.T);
    assertTrue(E.stringStartsWith(B) == f_lisp.NIL);
    assertTrue(F.stringStartsWith(B) == f_lisp.NIL);
    assertTrue(B.stringStartsWith(F) == f_lisp.T);

    try {
      assertTrue(G.stringStartsWith(B) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}

    try {
      assertTrue(B.stringStartsWith(G) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringTrim()
  {
    A = f_lisp.makeString("   mikey   ");
    B = f_lisp.makeString("mikey   ");
    C = f_lisp.makeString("   mikey");
    D = f_lisp.makeString("  malikey");
    E = f_lisp.makeString("mikey");
    F = f_lisp.makeString("");
    G = f_lisp.makeInteger(5);

    assertTrue(A.stringTrim().stringEq(E) == f_lisp.T);
    assertTrue(B.stringTrim().stringEq(E) == f_lisp.T);
    assertTrue(C.stringTrim().stringEq(E) == f_lisp.T);
    assertTrue(D.stringTrim().stringEq(E) == f_lisp.NIL);
    assertTrue(E.stringTrim().stringEq(E) == f_lisp.T);
    assertTrue(F.stringTrim().stringEq(E) == f_lisp.NIL);
    assertTrue(E.stringTrim().stringEq(F) == f_lisp.NIL);

    try {
      assertTrue(G.stringTrim() == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringTrim2()
  {
    A = f_lisp.makeString("aabbcmikeyabcdef");
    B = f_lisp.makeString("mikeycabcab");
    C = f_lisp.makeString("amikeyc");
    D = f_lisp.makeString("aamalikey");
    E = f_lisp.makeString("mikey");
    F = f_lisp.makeString("abcdef");
    G = f_lisp.makeString("");
    LispValue H = f_lisp.makeInteger(5);

    assertTrue(A.stringTrim(F).stringEq(E) == f_lisp.T);
    assertTrue(B.stringTrim(F).stringEq(E) == f_lisp.T);
    assertTrue(C.stringTrim(F).stringEq(E) == f_lisp.T);
    assertTrue(D.stringTrim(F).stringEq(E) == f_lisp.NIL);
    assertTrue(E.stringTrim(F).stringEq(E) == f_lisp.T);
    assertTrue(F.stringTrim(F).stringEq(G) == f_lisp.T);
    assertTrue(E.stringTrim(F).stringEq(F) == f_lisp.NIL);

    try {
      assertTrue(H.stringTrim(F) == f_lisp.T);
      fail("Should have thrown a LispValueNotAStringException.");
    } catch (LispValueNotAStringException e) {}
  }

  public void teststringLeftTrim()
  {
  }

  public void teststringLeftTrim2()
  {
  }

  public void teststringRightTrim()
  {
  }

  public void teststringRightTrim2()
  {
  }


  public void testsubst()
  {}
  public void testsubstring()
  {}
  public void testsymbolp()
  {}
  public void testsymbol_function()
  {}
  public void testsymbol_name()
  {}
  public void testsymbol_package()
  {}
  public void testsymbol_plist()
  {}
  public void testsymbol_value()
  {}
  public void testtenth()
  {}
  public void testthird()
  {}
  public void testtype_of()  {  }

  public void testtypep()  {  }

  public void testzerop()
  {}

  // Arithmetic functions

  public void testAdd1()
  {
    A = f_lisp.makeReal(3.1);
    B = f_lisp.makeReal(3.2);


    // float-float addition
    assertTrue(A.add(B).subtract(f_lisp.makeReal(6.3)).lessThan(f_lisp.makeReal(0.00000000001)) == f_lisp.T);
  }

  public void testAdd2()
  {
    A = f_lisp.makeReal(3.1);
    E = f_lisp.makeInteger(14);

    // float-integer addition
    assertTrue(A.add(E).eql(f_lisp.makeReal(17.1)) == f_lisp.T);
  }

  public void testAdd3()
  {
    C = f_lisp.makeReal(3.3);
    F = f_lisp.makeInteger(7);


    // integer-float addition
    assertTrue(F.add(C).eql(f_lisp.makeReal(10.3)) == f_lisp.T);
  }

  public void testAdd4()
  {
    E = f_lisp.makeInteger(14);
    F = f_lisp.makeInteger(7);
    G = f_lisp.makeInteger(21);

    // integer-integer addition
    assertTrue(E.add(F).eql(G) == f_lisp.T);

  }

  public void testAdd5()
  {
    A = f_lisp.makeReal(3.0);
    B = f_lisp.makeReal(3.2);
    C = f_lisp.makeReal(3.3);
    D = f_lisp.makeReal(4.5);
    E = f_lisp.makeInteger(14);

    // Float->integer result conversion.
    assertTrue(A.add(B).add(C).add(D).eql(E) == f_lisp.T);
  }

  public void testAdd6()
  {
    A = f_lisp.makeReal(3.0);
    B = f_lisp.makeReal(3.2);
    C = f_lisp.makeReal(3.3);
    D = f_lisp.makeReal(4.5);
    E = f_lisp.makeInteger(14);

    // Float->integer result conversion.
    assertTrue(E.eql(A.add(B).add(C).add(D)) == f_lisp.T);
  }

  public void testDivide()
  {
    A = f_lisp.makeReal(3.1);
    B = f_lisp.makeReal(3.2);
    C = f_lisp.makeReal(3.3);
    D = f_lisp.makeReal(4.5);
    E = f_lisp.makeInteger(14);
    F = f_lisp.makeInteger(7);
    G = f_lisp.makeInteger(21);


    // float-float division
    assertTrue(D.divide(C).eql(f_lisp.makeInteger(9).divide(f_lisp.makeReal(6.6))) == f_lisp.T);

    // float-integer division
    assertTrue(D.divide(E).eql(f_lisp.makeInteger(9).divide(f_lisp.makeReal(28.0))) == f_lisp.T);

    // Float->integer result conversion.
    assertTrue(f_lisp.makeReal(13.5).divide(f_lisp.makeReal(4.5)).eql(f_lisp.makeInteger(3)) == f_lisp.T);
  }


  public void testMultiply()
  {
    // there was a problem with multiplying by zero.  Make sure it is gone.
    A = f_lisp.makeInteger(6);
    B = f_lisp.makeInteger(7);
    C = f_lisp.makeInteger(0);
    D = f_lisp.makeInteger(8);

    E = A.multiply(B.multiply(C.multiply(D)));
    assertTrue(E.zerop() == f_lisp.T);

    // Repeat the test with some floating-point numbers.
    A = f_lisp.makeReal(6.14);
    B = f_lisp.makeReal(7);
    C = f_lisp.makeReal(0);
    D = f_lisp.makeReal(12.12121212);

    E = A.multiply(B.multiply(C.multiply(D)));
    assertTrue(E.zerop() == f_lisp.T);
  }



  public void testSubtract1()
  {
    A = f_lisp.makeReal(5.1);
    B = f_lisp.makeReal(14.2);


    // float-float subtraction
    assertTrue(B.subtract(A).subtract(f_lisp.makeReal(9.1)).lessThan(f_lisp.makeReal(0.00000000001)) == f_lisp.T);
  }

  public void testSubtract2()
  {
    A = f_lisp.makeReal(3.1);
    B = f_lisp.makeInteger(14);

    // float-integer subtraction
    assertTrue(A.subtract(B).subtract(f_lisp.makeReal(-10.9)).lessThan(f_lisp.makeReal(0.00000000001)) == f_lisp.T);
  }

  public void testSubtract3()
  {
    A = f_lisp.makeReal(3.3);
    B = f_lisp.makeInteger(7);


    // integer-float subtraction
    assertTrue(B.subtract(A).subtract(f_lisp.makeReal(3.7)).lessThan(f_lisp.makeReal(0.00000000001)) == f_lisp.T);
  }

  public void testSubtract4()
  {
    A = f_lisp.makeInteger(14);
    B = f_lisp.makeInteger(7);
    C = f_lisp.makeInteger(21);

    // integer-integer subtraction
    assertTrue(C.subtract(A).eql(B) == f_lisp.T);

  }

  public void testSubtract5()
  {
    A = f_lisp.makeInteger(5);
    B = f_lisp.makeReal(3.2);
    C = f_lisp.makeReal(3.3);
    D = f_lisp.makeReal(9.5);
    E = f_lisp.makeReal(21.0);

    // Float->integer result conversion.
    assertTrue(E.subtract(D).subtract(C).subtract(B).subtract(A).lessThan(f_lisp.makeReal(0.000001)) == f_lisp.T);
  }

  public void testSubtract6()
  {
    A = f_lisp.makeInteger(5);
    B = f_lisp.makeReal(3.2);
    C = f_lisp.makeReal(3.3);
    D = f_lisp.makeReal(9.5);
    E = f_lisp.makeReal(21.0);

    // Float->integer result conversion.
    assertTrue(A.subtract(E).subtract(D).subtract(C).subtract(B).lessThan(f_lisp.makeReal(0.000001)) == f_lisp.T);
  }


  // String functions
  public void testToString()
  {
    assertTrue("A".equals(A.toString()));
    assertTrue("B".equals(B.toString()));
    assertTrue("C".equals(C.toString()));
    assertTrue("\"a test string\"".equals(f_String.toString()));
    assertTrue("a test string".equals(f_String.toStringSimple()));
  }

  // Test a long list
  public void testLongList()
  {
    LispValue longList_1 = f_lisp.NIL;
    LispValue longList_2 = f_lisp.NIL;

    for (int i=0; i<14000; ++i)
    {
      longList_1 = new StandardLispCons(f_lisp, A, longList_1);
      longList_2 = new StandardLispCons(f_lisp, B, longList_2);
    }

    assertTrue(longList_1.basic_length() == 14000);
    assertTrue(longList_2.basic_length() == 14000);
    assertTrue(longList_1.basic_length() == longList_2.basic_length());

    //System.out.println("long list is " + longList);
  }

  // SK:  25 Jul 2009 
  public void testIntegerParse()
  {
    LispParser parser = new LispParser(f_lisp, "+1");
    try 
    {
      LispValue val = parser.parse();
      assertTrue(val.basic_integerp() && val.toString().equalsIgnoreCase("1"));
    } catch (Exception e2) {
      fail("Incorrectly converted +1: " + e2.getMessage());
    }   
  }
  
}