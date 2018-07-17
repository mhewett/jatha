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
 *
 *
 *        File:  StandardLispValue.java  - root of datatype hierarchy in Jatha
 *
 *      Author:  Micheal Hewett
 *     Created:  12 Jan 1997, from original of 21 June 1995
 *
 *    Compiler:  javac 1.0.2
 *
 * Description:  Declarations for the LISP-type data types
 *
 ****************************************************************************
 *  Content Summary
 *  ---------------
 *
 *  Values can be any type:
 *
 *     integer, float, symbol, string, list
 *
 *  This file contains the function definitions to perform all the
 *  operations involving these values.
 *
 *  Classes:
 *
 *     LispException
 *       LispConstantRedefinedException
 *       LispIndexOutOfRangeException
 *       LispUnboundVariableException
 *       LispUndefinedFunctionException
 *       LispUndefinedPackageException
 *
 *       LispValueNotAnAtomException
 *       LispValueNotAFunctionException
 *       LispValueNotAnIntegerException
 *       LispValueNotAListException
 *       LispValueNotANumberException
 *       LispValueNotASequenceException
 *       LispValueNotASymbolException
 *
 *     LispValue
 *	     LispConsOrNil                 (abstract)
 *         LispCons
 *         LispNil
 *       LispAtom                      (abstract)
 *         LispCharacter
 *         LispNumber                  (abstract)
 *           LispInteger
 *           LispReal
 *         LispString
 *         LispSymbol
 *           LispConstant
 *             LispKeyword
 *       LispStructure      (NYI)
 *       LispHashTable
 *       LispForeignObject             (abstract)
 *         LispJavaObject
 *           JavaConnection     (NYI)
 *           JavaInetAddress    (NYI)
 *           JavaServerSocket   (NYI)
 *           JavaSocket         (NYI)
 *           JavaURL            (NYI)
 *           JavaURLEncoder     (NYI)
 *
 *
 *     LispValueFactory
 *
 *  All operations are defined on LispValue (e.g. '+') so that you can
 *  legally (in Java) add any two values but at runtime you will get
 *  an exception unless you actually have valid operands.
 *
 *  Use the ValueFactory object to create the values.
 *
 *
 ****************************************************************************
 */

package org.jatha.dynatype;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import java.util.List;
import java.util.RandomAccess;
import org.jatha.Jatha;



//----------------------------  LispValue  ---------------------------------

// * @date    Wed Feb 19 17:01:40 1997
/**
 * LispValue is the root of all of the dynamically-typed LISP-like
 * structures.  It contains definitions of all methods that operate
 * on Lisp values.  This class is not instantiated directly.  Instead,
 * use a LispValueFactory instance to create the instances.
 * @see LispValueFactory
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 * @version 1.0
 *
 */
public abstract class StandardLispValue implements LispValue    // Base class for all the LISP data types
{
  protected Jatha f_lisp = null;

  public StandardLispValue()
  {
    super();
  }

  public StandardLispValue(Jatha lisp)
  {
    f_lisp = lisp;
  }

  public Jatha getLisp()
  {
    return f_lisp;
  }

  public void setLisp(Jatha lisp)
  {
    f_lisp = lisp;
  }

  public String internal_getName()
  {
    throw new LispValueNotASymbolException("The argument to internal_getName()");
  }

  public void internal_princ(PrintStream os)
  { os.print("#<unprintable object>"); }
  public void internal_princ_as_cdr(PrintStream os)
  { os.print(" . "); internal_princ(os);  }

  public void internal_prin1(PrintStream os)
  { os.print("#<unprintable object>"); }
  public void internal_prin1_as_cdr(PrintStream os)
  { os.print(" . "); internal_prin1(os);  }

  public void internal_print(PrintStream os)
  { System.err.print("#<unprintable object>"); }
  public void internal_print_as_cdr(PrintStream os)
  { os.print(" . "); internal_print(os);  }


  // Prints information for the APROPOS function
  public void apropos_print(PrintWriter out)
  {
    System.err.print("** Internal error: apropos_print called on non-Symbol: #<unprintable object>");
  }

  public boolean basic_atom()      { return false; }
  public boolean basic_bignump()   { return false; }
  public boolean basic_consp()     { return false; }
  public boolean basic_constantp() { return false; }
  public boolean basic_floatp()    { return false; }
  public boolean basic_foreignp()  { return false; }
  public boolean basic_integerp()  { return false; }
  public boolean basic_functionp() { return false; }
  public boolean basic_keywordp()  { return false; }
  public int     basic_length()    { throw new LispValueNotAListException("The argument to basic_length"); }
  public boolean basic_listp()     { return false; }
  public boolean basic_macrop()    { return false; }
  public boolean basic_null()      { return false; }
  public boolean basic_numberp()   { return false; }
  public boolean basic_stringp()   { return false; }
  public boolean basic_symbolp()   { return false; }

  public boolean uses(LispValue pkg) { throw new LispValueNotAPackageException("the argument to uses"); }

  /**
   * Wrapper for member().
   * @return true if the object is in the list.
   */
  public boolean contains(LispValue object)        { throw new LispValueNotAListException("The argument to contains"); }

  // Comparable interface.  Uses case-insensitive string comparison
  public int compareTo(LispValue o)
  {
    return this.toStringSimple().compareTo(o.toStringSimple());
    /*
    else
      return this.toStringSimple().compareTo(String.valueOf(o));  // Fix from Stephen Starkey, 21 April 2005
      */
  }

  // Implementation of Iterator interface
  public Iterator<LispValue> iterator()       { return null;  }


  /**
   * Returns a Java equivalent of the object.
   * For example, the number 3 is returned as an instance of new Integer(3).
   * If it can't be converted to a more useful Java object, it returns a String representation.
   */
  public Object toJava()
  {
    return toStringSimple();
  }


  /**
   * Returns a Java equivalent of the object.
   * For example, the number 3 is returned as an instance of new Integer(3).
   * If it can't be converted to a more useful Java object, it returns a String representation.
   * You can optionally send in a hint as to what type is preferred.
   */
  public Object toJava(String typeHint)
  {
    return toStringSimple();
  }

  /**
   * Returns the Lisp value as a Collection.  Most useful
   * for lists, which are turned into Collections.
   * But also works for single values.
   */
  public Collection<LispValue> toCollection()
  {
    ArrayList<LispValue> result = new ArrayList<LispValue>(1);
    result.add(this);
    return result;
  }
  
  
  /**
   * Returns the Lisp value as a List guaranteed to 
   * implement RandomAccess.
   */
  public List<LispValue> toRandomAccess()
  {
    Collection<LispValue> collection = toCollection();
    if (collection instanceof List && collection instanceof RandomAccess)
    {
      return (List<LispValue>) collection;
    }
    else
    {
      return new ArrayList<LispValue>(collection);
    }
  }


  // @author  Micheal S. Hewett    hewett@cs.stanford.edu
  // @date    Wed Feb 19 17:18:50 1997
   /**
   * <code>toString()</code> returns a printed representation
   * of the form (as printed by <code>(prin1)</code>) in
   * a Java string.
   * @return String The value in a string.
   */
  public String toString() { return "<unprintable object>"; }

  /**
   * Same as toString unless you are getting a String representation
   * of an array.  Then it uses the columnSeparator variable to separate columns in the output.
   * @param columnSeparator optional column separator string, defaults to a single space.
   * @return a String containing a printed representation of the value.
   */
  public String toString(String columnSeparator)
  {
    return toString();
  }

  /**
   * Prints a short version of the item.  Can optionally
   * send in the number of elements to print.  Most useful
   * for arrays or long lists.
   */
  public String toStringShort()
  {
    return toStringShort(5);
  }

  /**
   * Prints out a short version of the value.  Defaults to 5 elements
   * @param numberOfElements the maximum number of elements to print.
   */
  public String toStringShort(int numberOfElements)
  {
    return toString();
  }


  /**
   * Strips double-quotes from a LispString value.
   */
  public String toStringSimple()
  {
    return toString();
  }


  public String toStringAsCdr()
  {
    StringBuffer buf = new StringBuffer();

    buf.append(" . ");
    buf.append(toString());

    return buf.toString();
  }

  /**
   * This printer is protected and won't print more
   * than *PRINT-LENGTH* items or *PRINT-LEVEL* levels.
   * @param length
   * @param level
   */
  public String toString_internal(long length, long level)
  {
    return this.toString();
  }

  /**
   * *PRINT-LEVEL* is usually small enough to not have a stack overflow,
   * so we use recursion here.  Probably should be written like
   * toStringAsCdr_internal, though.
   */
  public String toStringAsCar_internal(long length, long level)
  {
    if (level > f_lisp.getPrintLevel().getLongValue())
    {
      System.err.println("Printing list deeper than *PRINT-LEVEL*.  Truncated.");
      return "...";
    }
    
    // else
    return toString_internal(length, level + 1);
   }

  /**
   * Counts cdrs so as not to have runaway lists.
   */
  public String toStringAsCdr_internal(long length, long level)
  {
    if (length > f_lisp.getPrintLength().getLongValue())
    {
      System.err.println("Printing list...longer than *PRINT-LENGTH*.  Truncated.");
      System.err.println("Next few items are: ");
      LispValue ptr = this;
      for (int i=0; i<10; ++i)
      {
        if (! (ptr instanceof LispCons))
          break;
        System.err.println("    " + ptr.car());
        ptr = ptr.cdr();
      }
      return "...";
    }

    else if (this instanceof LispCons)
    {
      StringBuffer buf = new StringBuffer();

      buf.append(" ");
      buf.append(this.car().toStringAsCar_internal(length, level+1));
      buf.append(this.cdr().toStringAsCdr_internal(length+1, level));
      return buf.toString();
    }

    else
      return this.toStringAsCdr();
  }


  //  ****  Handling special (dynamically-bound) variables   *********

  public void set_special(boolean value)
  { throw new LispValueNotASymbolException("The argument to set_special");  }

  public boolean specialP()
  { throw new LispValueNotASymbolException("The argument to specialP");  }

  public void adjustSpecialCount(int amount)
  { throw new LispValueNotASymbolException("The argument to adjustSpecialCount");  }

  public int get_specialCount()
  { throw new LispValueNotASymbolException("The argument to get_specialCount");  }


  // Packages
  public void setPackage(LispPackage newPackage)
  {
    System.err.println("\n*** INTERNAL ERROR: LispValue.setPackage() '" + this + "'" + " to " + newPackage);
  }


/* ------------------  LISP functions    ------------------------------ */

  /**
   * Returns the absolute value of any number, including complex.
   * The result is always a real number.
   */
  public LispValue     abs       ()
  { throw new LispValueNotANumberException("The first argument to ABS");  }

  /**
   * Arccos function, argument in radians.
   * Also called Inverse Cosine, this is the
   * angle whose cosine is the argument.
   */
  public LispValue acos()
  {
    throw new LispValueNotANumberException("The first argument to ACOS");
  }

  public LispValue aref(LispValue args)
  { 
    throw new LispValueNotAnArrayException("The first argument to AREF");  
  }
  public LispValue setf_aref(LispValue location, LispValue value)
  { 
    throw new LispValueNotAnArrayException("The first argument to SETF-AREF"); 
  }  
  public LispValue arrayDimensions()
  { 
    throw new LispValueNotAnArrayException("The first argument to ARRAY-DIMENSIONS"); 
  }
  public LispValue     arrayp   ()  { return f_lisp.NIL; }

  /**
   * Arcsin function, argument in radians.
   * Also called Inverse Sine, this is the
   * angle whose sine is the argument.
   */
  public LispValue asin()
  {
    throw new LispValueNotANumberException("The first argument to ASIN");
  }

  /**
   * Arctan function, argument in radians.
   * Also called Inverse Tangent, this is the
   * angle whose tangent is the argument.
   */
  public LispValue atan()
  {
    throw new LispValueNotANumberException("The first argument to ATAN");
  }

  /**
   * Arctan function, argument in radians.
   * Also called Inverse Tangent, this is the
   * angle whose tangent is y/x, where y is
   * the first argument and x is the second argument.
   */
  public LispValue atan2(LispValue x)
  {
    throw new LispValueNotANumberException("The first argument to ATAN2");
  }

  public LispValue     append       (LispValue otherList)
  { throw new LispValueNotAListException("The first argument to APPEND");  }

  public LispValue     apply        (LispValue args)
  { throw new LispValueNotAFunctionException("The first argument to APPLY");  }

  public LispValue     assoc        (LispValue index)
  { throw new LispValueNotAListException("The second argument to ASSOC"); }

  public LispValue     atom         ()  { return f_lisp.NIL;  }

  public LispValue     bignump      ()  { return f_lisp.NIL; }

  public LispValue     boundp       ()
  { throw new LispValueNotASymbolException("The argument to BOUNDP");  }

  public LispValue     butlast      ()
  { throw new LispValueNotAListException("The argument to BUTLAST");  }

  public LispValue     car          ()
  { throw new LispValueNotAConsException("The argument to CAR");  }

  public LispValue     setf_car          (LispValue newCar)
  { throw new LispValueNotAConsException("The argument to SETF-CAR");  }
  
  public LispValue     cdr          ()
  { throw new LispValueNotAConsException("The argument to CDR");  }

  public LispValue     setf_cdr          (LispValue newCdr)
  { throw new LispValueNotAConsException("The argument to SETF-CDR");  }
  
  public LispValue     ceiling      ()
  { throw new LispValueNotANumberException("The first argument to CEILING"); }

  public LispValue     characterp   ()  { return f_lisp.NIL; }

  public LispValue     clrhash      ()
  { throw new LispValueNotAHashtableException("The argument to CLRHASH"); }

  /**
   * Concatenate a string or symbol to other strings or symbols.
   * This does not have all the functionality of concatenate in Common LISP.
   * Pass in a list of values, the first of which must be the symbol STRING.
   * All values (except the first) are converted to strings and appended to
   * the end of the string.
   * This returns a new LispString.
   */
  public LispValue concatenate(LispValue values)
  {
    // First value must be the atom 'STRING'
    LispValue concatType = values.car();
    if (concatType.toString().equalsIgnoreCase("string") || concatType.toString().toUpperCase().endsWith(":STRING"))
    {
      StringBuffer buff = new StringBuffer(values.basic_length() * 5);
      buff.append(this.toStringSimple());

      Iterator<LispValue> valuesIt = values.cdr().iterator();
      while (valuesIt.hasNext())
      {
        LispValue value = valuesIt.next();
        if (value instanceof LispString)
          buff.append(value.toStringSimple());
        else
          buff.append(value.toString());
      }
      return new StandardLispString(f_lisp, buff.toString());
    }
    else
      throw new LispValueNotAStringException("The first argument to Concatenate (" + concatType + ") must be 'STRING");

  }

  public LispValue     consp        ()  { return f_lisp.NIL;  }

  public LispValue     constantp    ()
  {
    return basic_constantp() ? f_lisp.T : f_lisp.NIL;  // Modification suggested by Jean-Pierre Gaillardon  7 Apr 2005
  }

  public LispValue     copy_list    ()
  { throw new LispValueNotAListException("The argument to COPY-LIST");  }

  /**
   * Returns a full copy of any list, tree, array or table,
   * copying all the leaf elements.
   * Atoms like symbols, and numbers are not copied.
   * In Java, a string is not mutable so strings are also not copied.
   */
  public LispValue     copy         ()
  { return this; }

  /**
   * Cosine function, argument in radians.
   */
  public LispValue     cos          ()
  { throw new LispValueNotANumberException("The argument to COS"); }

  /**
   * Cotangent function, 1/tan(x), argument in radians.
   */
  public LispValue cot()
  {
    throw new LispValueNotANumberException("The first argument to COT");
  }

  /**
   * Cosecant function, 1/sin(x), argument in radians.
   */
  public LispValue csc()
  {
    throw new LispValueNotANumberException("The first argument to CSC");
  }

  /**
   * Converts a numeric value from degrees to radians.
   * @return The value in radians.
   */
  public LispValue degreesToRadians()
  {
    throw new LispValueNotANumberException("The argument to DegreesToRadians");
  }

  public LispValue     eighth       ()
  { throw new LispValueNotASequenceException("The first argument to EIGHTH"); }

  public LispValue elt(int index)
  {
    throw new LispValueNotASequenceException("The first argument to ELT");
  }

  public LispValue     elt          (LispValue index)
  { throw new LispValueNotASequenceException("The first argument to ELT"); }

  public LispValue     eq           (LispValue val)
  {
    if (this == val)
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

  public LispValue     eql          (LispValue val)
  {
    if (this == val)
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

  public LispValue     equal        (LispValue val)
  {
    if (this == val)
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }

  /**
   * Calculate the object raised to the power of n.
   */
  public LispValue expt(LispValue n)
  {
    throw new LispValueNotANumberException("The argument to EXPT");
  }
  
  /**
   * Compute the factorial of a non-negative integer.
   * Reals are truncated to the nearest integer.
   */
  public LispValue factorial()
  {
    throw new LispValueNotANumberException("The argument to FACTORIAL");
  }

  public LispValue     fboundp      ()
  { throw new LispValueNotASymbolException("The argument to FBOUNDP");  }

  public LispValue floatp()  { return f_lisp.NIL; }

  public LispValue     fifth        ()
  { throw new LispValueNotASequenceException("The first argument to FIFTH"); }

  public LispValue     first        ()
  { throw new LispValueNotASequenceException("The first argument to FIRST"); }

  public LispValue     floor        ()
  { throw new LispValueNotANumberException("The first argument to FLOOR"); }

  public LispValue     fourth       ()
  { throw new LispValueNotASequenceException("The first argument to FOURTH"); }

  public LispValue     funcall      (LispValue args)
  { throw new LispValueNotAFunctionException("The first argument to FUNCALL"); }

  public LispValue functionp()
  { return f_lisp.NIL; }

  public LispValue     gethash      (LispValue key)
  { throw new LispValueNotAHashtableException("The second argument to GETHASH"); }

  public LispValue     gethash      (LispValue key, LispValue defawlt)
  { throw new LispValueNotAHashtableException("The second argument to GETHASH"); }

  public LispValue setf_gethash(LispValue key, LispValue value)
  { throw new LispValueNotAHashtableException("The second argument to SETF-GETHASH"); }

  public LispValue     hashtablep   ()  { return f_lisp.NIL; }

  public LispValue hash_table_count ()
  { throw new LispValueNotAHashtableException("The argument to HASH-TABLE-COUNT"); }

  public LispValue hash_table_size ()
  { throw new LispValueNotAHashtableException("The argument to HASH-TABLE-SIZE"); }

  public LispValue hash_table_rehash_size ()
  { throw new LispValueNotAHashtableException("The argument to HASH-TABLE-REHASH-SIZE"); }

  public LispValue hash_table_rehash_threshold ()
  { throw new LispValueNotAHashtableException("The argument to HASH-TABLE-REHASH-THRESHOLD"); }

  public LispValue hash_table_test ()
  { throw new LispValueNotAHashtableException("The argument to HASH-TABLE-TEST"); }

  public LispValue     integerp     ()  { return f_lisp.NIL; }

  public LispValue     keywordp     ()  { return f_lisp.NIL; }

  public LispValue     last         ()
  { throw new LispValueNotAListException("The argument to LAST");  }

  public LispValue     length       ()
  { throw new LispValueNotASequenceException("The argument to LENGTH");  }

  public LispValue     lisp_null    () { return f_lisp.NIL; }

  public LispValue     list         ()  { return new StandardLispCons(f_lisp, f_lisp.NIL, f_lisp.NIL);  }

  public LispValue     listp        ()  { return f_lisp.NIL;  }

  public LispValue     max          (LispValue args)
  { throw new LispValueNotANumberException("One of the arguments to MAX"); }

  public LispValue     min          (LispValue args)
  { throw new LispValueNotANumberException("One of the arguments to MIN"); }

  public LispValue     member       (LispValue elt)
  { throw new LispValueNotAListException("The second argument to MEMBER");  }

  public LispValue     nconc        (LispValue arg)
  { throw new LispValueNotAListException("The argument to NCONC");  }

  public LispValue     negate       ()
  { throw new LispValueNotANumberException("The argument to NEGATE");  }

  /**
   * Returns NIL if eql returns T, and vice versa.
   * @param val
   */
  public LispValue     neql          (LispValue val)
  {
    if (eql(val) == f_lisp.T)
      return f_lisp.NIL;
    else
      return f_lisp.T;
  }

  public LispValue     ninth        ()
  { throw new LispValueNotASequenceException("The first argument to NINTH"); }

  public LispValue     nreverse     ()
  { throw new LispValueNotAListException("The argument to NREVERSE");  }

  public LispValue     numberp      () { return f_lisp.NIL; }

  /**
   * Pops a list and returns the first element.
   * <b>NOTE</b>: Because Java's variable values aren't accessible
   * to Jatha, the following doesn't work as expected:
   * <pre>
   *   LispValue l1 = lisp.makeList(A, B);
   *   l1.pop();   // works correctly, l1 is now (B)
   *   l1.pop();   // doesn't work correctly.  l1 is now (NIL . NIL)
   * </pre>
   * Jatha can't reassign l1 as expected.
   * <p>
   * However, the following does work:
   * <pre>
   *   LispValue l1 = new LispSymbol(lisp, "L1");
   *   l1.setq(lisp.makeList(A, B));
   *   l1.pop();  // works correctly.  The value of L1 is now (B).
   *   l1.pop();  // works correctly.  The value of L1 is now NIL.
   *   l1.push(A); // works correctly.  The value of L1 is now (A).
   *   assert(l1.symbol_value().equal(lisp.makeList(A)) == lisp.T);
   * </pre>
   *
   * @return the first element of the list
   * @throws LispValueNotASymbolException
   */
  public LispValue     pop          ()
  { throw new LispValueNotASymbolException("The argument to POP");  }

  /**
   * Returns the index of an element in a sequence.
   * Currently works for lists and strings.
   * Comparison is by EQL for lists and CHAR= for lists.
   * Returns NIL if the element is not found in the sequence.
   */
  public LispValue position(LispValue element)
  {
    throw new LispValueNotASequenceException("The argument to POSITION (" + element + ")");
  }

  public LispValue     prin1        ()  { internal_prin1(System.out); return this; }

  public LispValue     princ        ()  { internal_princ(System.out); return this; }

  public LispValue     print        ()
  { System.out.println(); internal_print(System.out); System.out.print(" "); return this; }

  /**
   * Pushes an element onto a list and returns the list.
   * <b>NOTE</b>: Because Java's variable values aren't accessible
   * to Jatha, the following doesn't work as expected:
   * <pre>
   *   LispValue l1 = LispValue.NIL;
   *   l1.push(A); // doesn't work correctly.  l1 is still NIL.
   *   l1 = l1.push(A);  // works correctly.
   * </pre>
   * Jatha can't reassign l1 as expected.
   * <p>
   * However, the following does work:
   * <pre>
   *   LispValue l1 = new LispSymbol("L1");
   *   l1.setq(LispValue.NIL);
   *   l1.push(B); // works correctly.  The value of L1 is now (B).
   *   l1.push(A); // works correctly.  The value of L1 is now (A B).
   *   assert(l1.symbol_value().equal(LispValueFactory.makeList(A, B)) == LispValue.T);
   * </pre>
   *
   * @return the new list.
   * @throws LispValueNotASymbolException
   */
  public LispValue     push         (LispValue value)
  { throw new LispValueNotASymbolException("The second argument to PUSH");  }

  /**
   * Converts a numeric value from radians to degrees.
   * @return The value in degrees.
   */
  public LispValue radiansToDegrees()
  {
    throw new LispValueNotANumberException("The argument to RadianstoDegrees");
  }

  public LispValue     rassoc       (LispValue index)
  { throw new LispValueNotAListException("The second argument to RASSOC");  }

  /**
   * Reads a value from the given string.
   * @return a LispValue as read by the LISP Reader
   */
  public LispValue readFromString()
  {
    throw new LispValueNotAStringException("The argument to READ-FROM-STRING");
  }

  /**
   * Computes 1/x of the given number.  Only valid for numbers.
   * @return a LispReal
   */
  public LispValue reciprocal()
  {
    throw new LispValueNotANumberException("The argument to RECIPROCAL");
  }

  public LispValue remhash(LispValue key)
  { throw new LispValueNotAHashtableException("The second argument to REMHASH"); }

  public LispValue     remove       (LispValue elt)
  { throw new LispValueNotASequenceException("The second argument to REMOVE");  }

  /**
   * Synonym for CDR.
   */
  public LispValue     rest         ()
  { throw new LispValueNotASequenceException("The argument to REST");  }

  public LispValue     reverse      ()
  { throw new LispValueNotASequenceException("The argument to REVERSE"); }

  public LispValue     rplaca       (LispValue newCar)
  { throw new LispValueNotAListException("The first argument to RPLACA");  }

  public LispValue     rplacd       (LispValue newCdr)
  { throw new LispValueNotAListException("The first argument to RPLACD");  }

  /**
   * Secant function, 1/cos(x), argument in radians.
   */
  public LispValue sec()
  {
    throw new LispValueNotANumberException("The first argument to SEC");
  }

  public LispValue     second       ()
  { throw new LispValueNotASequenceException("The first argument to SECOND"); }

  public LispValue     setf_symbol_function(LispValue newFunction)
  { throw new LispValueNotASymbolException("The argument to SETF-SYMBOL-FUNCTION"); }

  public LispValue setf_symbol_plist(LispValue newPlist)
  { throw new LispValueNotASymbolException("The argument to SETF-SYMBOL-PLIST"); }

  public LispValue setf_symbol_value(LispValue newValue)
  { throw new LispValueNotASymbolException("The argument to SETF-SYMBOL-VALUE"); }

  public LispValue setq(LispValue newValue)
  { throw new LispValueNotASymbolException("The argument to SETQ"); }

  public LispValue     seventh      ()
  { throw new LispValueNotASequenceException("The first argument to SEVENTH"); }

  public LispValue     sixth        ()
  { throw new LispValueNotASequenceException("The first argument to SIXTH"); }

  /**
   * Sine trigonometric function.  Argument is in radians.
   */
  public LispValue     sin          ()
  { throw new LispValueNotANumberException("The argument to SIN"); }

  /**
   * Square root.  Accepts negative numbers.
   */
  public LispValue     sqrt         ()
  { throw new LispValueNotANumberException("The argument to SQRT"); }

  public LispValue     stringp      ()     { return f_lisp.NIL; }

  /**
   * For Common LISP compatibility, but identical to stringCapitalize.
   */
  public LispValue nstringCapitalize()
  {
    throw new LispValueNotAStringException("The argument to nstringCapitalize");
  }

  /**
   * For Common LISP compatibility, but identical to stringDowncase.
   */
  public LispValue nstringDowncase()
  {
    throw new LispValueNotAStringException("The argument to nstringDowncase");
  }

  /**
   * For Common LISP compatibility, but identical to stringUpcase.
   */
  public LispValue nstringUpcase()
  {
    throw new LispValueNotAStringException("The argument to nstringUpcase");
  }

  /**
   * Converts a String, Symbol or Character to a string.
   */
  public LispValue string()
  {
    throw new LispValueNotConvertableToAStringException("The argument to String");
  }

  /**
   * Capitalizes the first character of a string and
   * converts the remaining characters to lower case.
   */
  public LispValue stringCapitalize()
  {
    throw new LispValueNotAStringException("The argument to stringCapitalize");
  }

  /**
   * Converts all of the characters to lower case.
   */
  public LispValue stringDowncase()
  {
    throw new LispValueNotAStringException("The argument to stringDowncase");
  }

  /**
   * Not in Common LISP, but useful.  This is case-sensitive.
   */
  public LispValue stringEndsWith(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringEndsWith");
  }

  /**
   * Returns T if the argument is an identical string to
   * the object.  Character comparison is case-sensitive.
   * This is the LISP string= function.
   */
  public LispValue stringEq(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringEq");
  }

  /**
   * Returns T if the argument is an identical string to
   * the object.  Character comparison is case-insensitive.
   * STRING-EQUAL.
   */
  public LispValue stringEqual(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringEqual");
  }

  /**
   * This is the LISP string-greaterp function.
   * Case-insensitive comparison for ordering.
   */
  public LispValue stringGreaterP(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringGreaterP");
  }

  /**
   * This is the LISP string&gt; function.
   * Case-sensitive comparison for ordering.
   */
  public LispValue stringGreaterThan(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringGreaterThan");
  }

  /**
   * This is the LISP string&gt;= function.
   * Case-sensitive comparison for ordering.
   */
  public LispValue stringGreaterThanOrEqual(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringGreaterThanOrEqual");
  }

  /**
   * Trims the left end of the string by deleting whitespace on both ends.
   */
  public LispValue stringLeftTrim()
  {
    throw new LispValueNotAStringException("The argument to stringLeftTrim");
  }

  /**
   * Trims the left end of the string by deleting characters in the input string on both ends.
   */
  public LispValue stringLeftTrim(LispValue deleteBag)
  {
    throw new LispValueNotAStringException("The argument to stringLeftTrim");
  }

  /**
   * This is the LISP string-lessp function.
   * Case-insensitive comparison for ordering.
   */
  public LispValue stringLessP(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringLessP");
  }

  /**
   * This is the LISP string&lt; function.
   * Case-sensitive comparison for ordering.
   */
  public LispValue stringLessThan(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringLessThan");
  }

  /**
   * This is the LISP string&lt;= function.
   * Case-sensitive comparison for ordering.
   */
  public LispValue stringLessThanOrEqual(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringLessThanOrEqual");
  }

  /**
   * Returns T if the argument is not STRING= the given string.
   */
  public LispValue stringNeq(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringNeq");
  }

  /**
   * This is the LISP string-not-greaterp function.
   * Case-insensitive comparison for ordering.
   */
  public LispValue stringNotGreaterP(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringNotGreaterP");
  }

  /**
   * This is the LISP string-not-lessp function.
   * Case-insensitive comparison for ordering.
   */
  public LispValue stringNotLessP(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringNotLessP");
  }

  /**
   * Trims the right end of the string by deleting whitespace on both ends.
   */
  public LispValue stringRightTrim()
  {
    throw new LispValueNotAStringException("The argument to stringRightTrim");
  }

  /**
   * Trims the right end of the string by deleting characters in the input string on both ends.
   */
  public LispValue stringRightTrim(LispValue deleteBag)
  {
    throw new LispValueNotAStringException("The argument to stringRightTrim");
  }

  /**
   * Not in Common LISP, but useful.  This is case-sensitive.
   */
  public LispValue stringStartsWith(LispValue arg)
  {
    throw new LispValueNotAStringException("The argument to stringStartsWith");
  }

  /**
   * Trims the string by deleting whitespace on both ends.
   */
  public LispValue stringTrim()
  {
    throw new LispValueNotAStringException("The argument to stringTrim");
  }

  /**
   * Trims the string by deleting characters in the input string on both ends.
   */
  public LispValue stringTrim(LispValue deleteBag)
  {
    throw new LispValueNotAStringException("The argument to stringTrim");
  }

  /**
   * Converts all the characters to upper case.
   */
  public LispValue stringUpcase()
  {
    throw new LispValueNotAStringException("The argument to stringUpcase");
  }

  public LispValue     subst(LispValue newValue, LispValue oldValue)
  { throw new LispValueNotAListException("The third argument to SUBST");  }

  public LispValue substring(LispValue start)
  { throw new LispValueNotASequenceException("The operand of substring"); }

  public LispValue substring(LispValue start, LispValue end)
  { throw new LispValueNotASequenceException("The operand of substring"); }

  public LispValue     symbolp      ()     { return f_lisp.NIL; }

  public LispValue     symbol_function() throws LispException
  { throw new LispValueNotASymbolException("The argument to SYMBOL_FUNCTION");  }

  public LispValue     symbol_name()
  { throw new LispValueNotASymbolException("The argument to SYMBOL_NAME");  }

  public LispValue     symbol_package()
  { throw new LispValueNotASymbolException("The argument to SYMBOL_PACKAGE");  }

  public LispValue     symbol_plist()
  { throw new LispValueNotASymbolException("The argument to SYMBOL_PLIST");  }

  public LispValue     symbol_value() throws LispException
  { throw new LispValueNotASymbolException("The argument to SYMBOL_VALUE");  }

  /**
   * Tangent trigonometric function.  Argument is in radians.
   */
  public LispValue     tan          ()
  { throw new LispValueNotANumberException("The argument to TAN"); }

  public LispValue     tenth        ()
  { throw new LispValueNotASequenceException("The first argument to TENTH"); }

  public LispValue     third       ()
  { throw new LispValueNotASequenceException("The first argument to THIRD"); }

  // Everything not anything else is a T, although this return value is illegal in CLTL2.
  public LispValue     type_of     ()  { return f_lisp.T;   }

  // Everything not anything else is a T, although this return value is illegal in CLTL2.
  public LispValue     typep       (LispValue type)  { return f_lisp.NIL;   }

  public LispValue     zerop       ()
  { throw new LispValueNotANumberException("The argument to ZEROP"); }


  // Arithmetic functions

  public LispValue     add         (LispValue args)
  { throw new LispValueNotANumberException("An argument to + (add)"); }

  public LispValue     divide      (LispValue args)
  { throw new LispValueNotANumberException("An argument to / (divide)"); }

  public LispValue     multiply    (LispValue args)
  { throw new LispValueNotANumberException("An argument to * (multiply)"); }

  public LispValue     subtract    (LispValue args)
  { throw new LispValueNotANumberException("An argument to - (subtract)"); }

  public LispValue greaterThan(LispValue arg)
  { throw new LispValueNotANumberException("An argument to > (greater than)"); }

  public LispValue greaterThanOrEqual(LispValue arg)
  { throw new LispValueNotANumberException("An argument to >= (greater than or equal)"); }

  public LispValue lessThan(LispValue arg)
  { throw new LispValueNotANumberException("An argument to < (less than)"); }

  public LispValue lessThanOrEqual(LispValue arg)
  { throw new LispValueNotANumberException("An argument to > (less than or equal)"); }

  public LispValue equalNumeric(LispValue arg)
  { throw new LispValueNotANumberException("An argument to = (numeric equal)"); }

    public LispValue documentation(final LispValue type) {
        throw new LispValueNotASymbolException("The first argument to DOCUMENTATION");
    }
    public LispValue setf_documentation(final LispValue type, final LispValue value) {
        throw new LispValueNotASymbolException("The first argument to SETF-DOCUMENTATION");
    }

    public void showStackTrace()
    {
      try {
        throw new Exception("stack trace");
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

}
