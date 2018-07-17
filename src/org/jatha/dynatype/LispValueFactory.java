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

package org.jatha.dynatype;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

import org.jatha.Jatha;
import org.jatha.read.LispParser;

//-----------------------------  LispValueFactory  --------------------------


//* @date    Thu Feb 20 12:06:13 1997
/**
 * <p>
 * <font color="red">As of Jatha 1.5.3, do not use this class.
 * Use the non-static routines Jatha.makeXYZ() instead.</font>
 * </p>
 * <p>
 * The LispValueFactory knows how to create values of different types.
 * Make an instance of this class and use it to create instances of the types.
 * </p>
 * The only possible error is an OutOfMemoryError, which Java will
 * generate on its own, so we don't do any error checking.
 *
 * Example:
 * <pre>
 *   LispValueFactory vf = new LispValueFactory();
 *   LispValue         x = vf.makeCons(NIL, NIL);
 * </pre>
 * @see LispValue
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 * @version 1.0
 *
 */
public class LispValueFactory
{
  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:08:32 1997
  /**
   * makeCons(a,b) creates a new Cons cell, initialized with
   * the values a and b as the CAR and CDR respectively.
   *
   * @see LispCons
   * @param theCar
   * @param theCdr
   * @return LispValue
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispCons makeCons(Jatha lisp, LispValue theCar, LispValue theCdr)
  {  return new StandardLispCons(lisp, theCar, theCdr); }


  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:10:00 1997
  /**
   * Creates a LISP list from the elements of the Collection.
   * which must be LispValue types.
   *
   * @see LispValue
   *
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispConsOrNil makeList(Jatha lisp, Collection elements)
  {
    // Use array so as to iterate from the end to the beginning.
    Object[] elArray = elements.toArray();
    LispConsOrNil result  = lisp.NIL;

    for (int i=elArray.length-1; i>= 0; i--)
      result = new StandardLispCons(lisp, (LispValue)(elArray[i]), result);

    return result;
  }


  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:10:00 1997
  /**
   * Creates a list of the 1, 2 or 3 or 4 item parameters,
   * and returns it.
   *
   * @see LispCons
   * @see LispValue
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispCons makeList(Jatha lisp, LispValue first)
  {  return new StandardLispCons(lisp, first, lisp.NIL); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispCons makeList(Jatha lisp, LispValue first, LispValue second)
  {  return new StandardLispCons(lisp, first, new StandardLispCons(lisp, second, lisp.NIL)); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispCons makeList(Jatha lisp, LispValue first, LispValue second, LispValue third)
  {  return new StandardLispCons(lisp, first,
                   new StandardLispCons(lisp, second,
                          new StandardLispCons(lisp, third, lisp.NIL))); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispCons makeList(Jatha lisp, LispValue first, LispValue second,
			   LispValue third, LispValue fourth)
  {  return new StandardLispCons(lisp, first,
                   new StandardLispCons(lisp, second,
                          new StandardLispCons(lisp, third,
				       new StandardLispCons(lisp, fourth, lisp.NIL))));
  }


  /**
   * Each element of the collection should be a LispConsOrNil.
   * The elements will be non-destructively appended to each other.
   * The result is one list.
   * Note that this operation is expensive in terms of storage.
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispConsOrNil makeAppendList(Jatha lisp, Collection elements)
  {
    if (elements.size() == 0)
      return lisp.NIL;

    LispValue result  = lisp.NIL;
    for (Iterator iterator = elements.iterator(); iterator.hasNext();)
    {
      LispValue o = (LispValue) iterator.next();
      result = result.append(o);
    }

    return (LispConsOrNil)result;
  }


  /**
   * Each element of the collection should be a LispConsOrNil.
   * The elements will be destructively appended to each other.
   * The result is one list.
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispConsOrNil makeNconcList(Jatha lisp, Collection elements)
  {
    if (elements.size() == 0)
      return lisp.NIL;

    LispValue result  = lisp.NIL;
    for (Iterator iterator = elements.iterator(); iterator.hasNext();)
    {
      LispValue o = (LispValue) iterator.next();
      result = result.nconc(o);
    }

    return (LispConsOrNil)result;
  }


  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:16:21 1997
  /**
   * Creates a LispInteger type initialized with the value
   * provided and returns it.
   * @see LispInteger
   * @see LispValue
   * @return LispInteger
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispInteger     makeInteger (Jatha lisp, Long value)
  { return new StandardLispInteger(lisp, value.longValue()); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispInteger     makeInteger (Jatha lisp, long value)
  { return new StandardLispInteger(lisp, value); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispInteger     makeInteger (Jatha lisp, Integer value)
  { return new StandardLispInteger(lisp, value.longValue()); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispInteger     makeInteger (Jatha lisp, int value)
  { return new StandardLispInteger(lisp, (long)value); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispInteger     makeInteger (Jatha lisp)
  { return new StandardLispInteger(lisp, 0); }

  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Tue May 20 23:09:54 1997
  /**
   * Creates a LispBignum type initialized with the value provided.
   * @see LispBignum
   * @see java.math.BigInteger
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispBignum makeBignum(Jatha lisp, BigInteger value)
  { return new StandardLispBignum(lisp, value); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispBignum makeBignum(Jatha lisp, LispInteger value)
  { return new StandardLispBignum(lisp, BigInteger.valueOf(value.getLongValue())); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispBignum makeBignum(Jatha lisp, double value)
  { return new StandardLispBignum(lisp, BigInteger.valueOf((long)value)); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispBignum makeBignum(Jatha lisp, long value)
  { return new StandardLispBignum(lisp, BigInteger.valueOf(value)); }


  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:19:15 1997
  /**
   * Creates an instance of LispReal initialized with
   * the given value.
   * @see LispInteger
   * @see LispValue
   * @return LispReal
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispReal        makeReal    (Jatha lisp, Double value)
  { return new StandardLispReal(lisp, value.doubleValue()); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispReal        makeReal    (Jatha lisp, double value)
  { return new StandardLispReal(lisp, value); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispReal        makeReal    (Jatha lisp, Float value)
  { return new StandardLispReal(lisp, value.doubleValue()); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispReal        makeReal    (Jatha lisp, float value)
  { return new StandardLispReal(lisp, (double)value); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispReal        makeReal    (Jatha lisp)
  { return new StandardLispReal(lisp, 0.0); }


  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:20:13 1997
  /**
   * Creates a LispString from a Java string.
   *
   * @see LispString
   * @see LispValue
   * @return LispString
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispString      makeString  (Jatha lisp, String str)
  { return new StandardLispString(lisp, str); }


  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:20:57 1997
  /**
   * Creates a LispSymbol from a string or LispString.
   * This method does <b>not</b> intern the symbol.
   *
   * @see LispSymbol
   * @see LispValue
   * @return LispSymbol
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispSymbol      makeSymbol  (Jatha lisp, String      symbolName)
  { return new StandardLispSymbol(lisp, symbolName); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispSymbol      makeSymbol  (Jatha lisp, LispString  symbolName)
  { return new StandardLispSymbol(lisp, symbolName); }


  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:20:57 1997
  /**
   * Creates a LispConstant (a type of Symbol whose value
   * can not be changed).  This method does <b>not</b>
   * intern the symbol.
   *
   * @see LispConstant
   * @see LispSymbol
   * @see LispValue
   * @return LispSymbol
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispSymbol      makeConstant  (Jatha lisp, String      symbolName)
  { return new StandardLispConstant(lisp, symbolName); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispSymbol      makeConstant  (Jatha lisp, LispString  symbolName)
  { return new StandardLispConstant(lisp, symbolName); }


  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:20:57 1997
  /**
   * Creates a LispKeyword (a type of Symbol that evaluates
   * to itself).  This method does <b>not</b> intern the symbol.
   *
   * @see LispKeyword
   * @see LispConstant
   * @see LispSymbol
   * @see LispValue
   * @return LispSymbol
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispSymbol      makeKeyword  (Jatha lisp, String      symbolName)
  { return new StandardLispKeyword(lisp, symbolName); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispSymbol      makeKeyword  (Jatha lisp, LispString  symbolName)
  { return new StandardLispKeyword(lisp, symbolName); }


  //* @author  Micheal S. Hewett    hewett@cs.stanford.edu
  //* @date    Thu Feb 20 12:20:57 1997
  /**
   * Creates a LispNil (the funny symbol/cons that is the LISP NIL).
   * This method does <b>not</b> intern the symbol.
   *
   * @see StandardLispNIL
   * @see LispCons
   * @see LispSymbol
   * @see LispValue
   * @return LispSymbol
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static StandardLispNIL         makeNIL     (Jatha lisp, String      symbolName)
  { return new StandardLispNIL(lisp, symbolName); }

  /**
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static StandardLispNIL         makeNIL     (Jatha lisp, LispString  symbolName)
  { return new StandardLispNIL(lisp, symbolName); }

  /**
   * Turns a Java object into a LISP object.
   *
   * @param obj
   * @deprecated  Use the same method on the Jatha class instead
   */
  public static LispValue toLisp(Jatha lisp, Object obj) // TODO: Is this where we use dynatype.LispForeignObject?
  {
    if (obj == null)
      return lisp.NIL;

    if (obj instanceof LispValue)
      return (LispValue)obj;

    if (obj instanceof Integer)
      return new StandardLispInteger(lisp, ((Integer)obj).intValue());

    else if (obj instanceof Long)
      return new StandardLispInteger(lisp, ((Long)obj).longValue());

    else if (obj instanceof Double)
      return new StandardLispReal(lisp, ((Double)obj).doubleValue());

    else if (obj instanceof Float)
      return new StandardLispReal(lisp, ((Float)obj).doubleValue());

    else if (obj instanceof String)
      return new StandardLispString(lisp, (String)obj);

    try {
      return (new LispParser(lisp, obj.toString(), LispParser.PRESERVE)).parse();
    } catch (Exception e) {
      System.err.println("Error in LispValueFactory.toLisp(" + obj + ")");
    } 
    return lisp.NIL;
  }

}
