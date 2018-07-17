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
 */

package org.jatha.dynatype;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import java.util.List;
import org.jatha.Jatha;

/**
 * LispValueInterface defines the root of the Interfaces that define
 * the datatypes in the system.  Most representations
 * will pass around values as this type.
 * User: hewett
 * Date: Nov 7, 2003
 * Time: 2:49:43 PM
 */
public interface LispValue extends Comparable<LispValue>
{
  /* Interface copied from org.jatha.dyntatype.StandardLispValue. */


  public Jatha getLisp();

  void setLisp(Jatha lisp);

  public String internal_getName();

  public void internal_princ(PrintStream os);

  public void internal_princ_as_cdr(PrintStream os);

  public void internal_prin1(PrintStream os);

  public void internal_prin1_as_cdr(PrintStream os);

  public void internal_print(PrintStream os);

  public void internal_print_as_cdr(PrintStream os);


  /**
   * Prints information for the APROPOS function
   */
  public void apropos_print(PrintWriter out);

  /**
   * Returns Java true if the object is an atom.
   */
  public boolean basic_atom();

  /**
   * Returns Java true if the object is a Bignum.
   */
  public boolean basic_bignump();

  /**
   * Returns Java true if the object is a CONS cell.
   */
  public boolean basic_consp();

  /**
   * Returns Java true if the object is a constant.
   */
  public boolean basic_constantp();

  /**
   * Returns Java true if the object is a floating-point number.
   */
  public boolean basic_floatp();

  /**
   * Returns Java true if the object is a reference to an object in a foreign computer language.
   */
  public boolean basic_foreignp();

  /**
   * Returns Java true if the object is a function.
   */
  public boolean basic_functionp();

  /**
   * Returns Java true if the object is an integer.
   */
  public boolean basic_integerp();

  /**
   * Returns Java true if the object is a keyword.
   */
  public boolean basic_keywordp();

  /**
   * Returns the Java length of a list or string.
   */
  public int basic_length();

  /**
   * Returns Java true if the object is a CONS cell or NIL.
   */
  public boolean basic_listp();

  /**
   * Returns Java true if the object is a macro.
   */
  public boolean basic_macrop();

  /**
   * Returns Java true if the object is NIL.
   */
  public boolean basic_null();

  /**
   * Returns Java true if the object is a number.
   */
  public boolean basic_numberp();

  /**
   * Returns Java true if the object is a string.
   */
  public boolean basic_stringp();

  /**
   * Returns Java true if the object is a symbol.
   */
  public boolean basic_symbolp();


  /**
   * Wrapper for member().
   * @return true if the object is in the list.
   */
  public boolean contains(LispValue object);

  // Comparable interface.  Uses case-insensitive string comparison
  public int compareTo(LispValue o);

  // Implementation of Iterator interface
  public Iterator<LispValue> iterator();


  /**
   * Returns a Java equivalent of the object.
   * For example, the number 3 is returned as an instance of new Integer(3).
   * If it can't be converted to a more useful Java object, it returns a String representation.
   */
  public Object toJava();


  /**
   * Returns a Java equivalent of the object.
   * For example, the number 3 is returned as an instance of new Integer(3).
   * If it can't be converted to a more useful Java object, it returns a String representation.
   * You can optionally send in a hint as to what type is preferred.
   */
  public Object toJava(String typeHint);

  /**
   * Returns the Lisp value as a Collection.  Most useful
   * for lists, which are turned into Collections.
   * But also works for single values.
   */
  public Collection<LispValue> toCollection();

  /**
   * Returns the Lisp value as a List guaranteed to implement RandomAccess.
   */
  public List<LispValue> toRandomAccess();
  
  // @author  Micheal S. Hewett    hewett@cs.stanford.edu
  // @date    Wed Feb 19 17:18:50 1997
  /**
   * <code>toString()</code> returns a printed representation
   * of the form (as printed by <code>(prin1)</code>) in
   * a Java string.
   * @return String The value in a string.
   */
  public String toString();

  /**
   * Same as toString unless you are getting a String representation
   * of an array.  Then it uses the columnSeparator variable to separate columns in the output.
   * @param columnSeparator optional column separator string, defaults to a single space.
   * @return a String containing a printed representation of the value.
   */
  String toString(String columnSeparator);

  /**
   * Strips double-quotes and leading colons from a LispString value.
   */
  public String toStringSimple();

  /**
   * Prints a short version of the item.  Can optionally
   * send in the number of elements to print.  Most useful
   * for arrays or long lists.
   */
  public String toStringShort();

  /**
   * Prints out a short version of the Array.  Defaults to 5 elements
   * @param numberOfElements the maximum number of elements to print.
   */
  String toStringShort(int numberOfElements);


  public String toStringAsCdr();


  public String toString_internal(long length, long level);

  /**
   * Counts cars so as not to have runaway lists.
   * Max depth is determined by *PRINT-LEVEL*.
   */
  public String toStringAsCar_internal(long length, long level);

  /**
   * Counts cdrs so as not to have runaway lists.
   * Max length is determined by *PRINT-LENGTH*.
   */
  public String toStringAsCdr_internal(long length, long level);



  //  ****  Handling special (dynamically-bound) variables   *********

  public void set_special(boolean value);

  public boolean specialP();

  public void adjustSpecialCount(int amount);

  public int get_specialCount();


  // Packages
  public void setPackage(LispPackage newPackage);


/* ------------------  LISP functions    ------------------------------ */

  /**
   * Absolute value of a number.
   */
  public LispValue abs();

  /**
   * Append two lists together.  The first list is copied.
   * @param otherList
   */
  public LispValue append(LispValue otherList);

  /**
   * Apply a function to an argument list.
   * @param args
   */
  public LispValue apply(LispValue args);

  /**
   * Lookup a value in an array.
   * @param args
   */
  public LispValue aref(LispValue args);

  /**
   * Sets a value in an array.
   */
  public LispValue setf_aref(LispValue location, LispValue value);
  
  /**
   * Returns a list of the dimension sizes an array.
   */
  public LispValue arrayDimensions();
  
  /**
   * Returns T if the object is an array.
   */
  public LispValue arrayp();

  /**
   * Look up a value in an association list.
   * @param index
   */
  public LispValue assoc(LispValue index);

  /**
   * Returns T if the object is an atom.
   */
  public LispValue atom();

  /**
   * Returns T if the object is a Bignum.
   */
  public LispValue bignump();

  /**
   * Returns T if the symbol has been assigned a value.
   */
  public LispValue boundp();

  /**
   * Returns all but the last of the elements of a list.
   * Butlast is the mirror image of CDR.
   */
  public LispValue butlast();

  /**
   * Returns the first element of a list.
   * CAR of NIL is NIL.
   */
  public LispValue car();

  /**
   * Sets the first element of a list.
   */
  public LispValue setf_car(LispValue newCar);
  
  /**
   * Returns all but the first element of a list.
   * CDR of NIL is NIL.
   */
  public LispValue cdr();

  /**
   * Sets the tail of a list.
   */
  public LispValue setf_cdr(LispValue newCdr);
  
  /**
   * Returns T if the object is a Character.
   */
  public LispValue characterp();

  /**
   * Clears a hash table.
   */
  public LispValue clrhash();

  /**
   * Concatenate a string to another string.
   * Passing in any LispValue causes it to be converted to a string
   * and concatenated to the end.
   * This returns a new LispString.
   */
  public LispValue concatenate(LispValue value);

  /**
   * Returns T if the object is a CONS cell.
   * This is equivalent to asking whether it has a CAR and a CDR
   * and is not NIL.
   */
  public LispValue consp();

  /**
   * Returns T if the object is a constant.
   */
  public LispValue constantp();

  /**
   * Returns a copy of the top level of a list.
   * Does not copy the interior branches.
   */
  public LispValue copy_list();

  /**
   * Returns a full copy of any list, tree, array or table,
   * copying all the leaf elements.
   * Atoms like symbols, and numbers are not copied.
   * In Java, a string is not mutable so strings are also not copied.
   */
  public LispValue copy();

  /**
   * Converts a numeric value from degrees to radians.
   * @return The value in radians.
   */
  public LispValue degreesToRadians();

  /**
   * Returns the 8th element of a list.
   * If the list is shorter than 8 elements, returns NIL.
   */
  public LispValue eighth();

  /**
   * Returns the nth element of a list.
   * The zeroth element is the first element.
   * @param index
   */
  public LispValue elt(LispValue index);

  /**
   * Returns T if the argument is exactly identical
   * to the object.  That is, it must be exactly the
   * same memory reference.
   */
  public LispValue eq(LispValue val);

  /**
   * Returns T if the argument is EQ to the object or
   * if the arguments and object are numbers with equal values.
   */
  public LispValue eql(LispValue val);

  /**
   * Returns T if the argument is EQL or if two strings
   * are STRING= or if two trees have EQUAL subtrees.
   */
  public LispValue equal(LispValue val);

  /**
   * Calculate the object raised to the power of n.
   */
  public LispValue expt(LispValue n);

  /**
   * Compute the factorial of a non-negative integer.
   * Reals are truncated to the nearest integer.
   */
  public LispValue factorial();

  /**
   * Returns T if the symbol has an assigned function.
   */
  public LispValue fboundp();

  /**
   * Returns T if the object is a floating-point number.
   */
  public LispValue floatp();

  /**
   * Returns the fifth element of a list, or NIL if
   * the list is shorter than 5 elements.
   */
  public LispValue fifth();

  /**
   * Returns the first element of a list.
   * Identical to the CAR function.
   */
  public LispValue first();

  /**
   * Returns the 4th element of a list, or NIL if the list
   * is shorter than 4 elements.
   */
  public LispValue fourth();

  /**
   * Calls a functio non a list of arguments.
   */
  public LispValue funcall(LispValue args);

  /**
   * Retrieves values from a hash table.
   */
  public LispValue gethash(LispValue key);

  /**
   * Retrieves values from a hash table, returning a default
   * value if the key is not in the table.
   */
  public LispValue gethash(LispValue key, LispValue defawlt);


  /**
   * Sets a value in a hash table.
   */
  public LispValue setf_gethash(LispValue key, LispValue value);

  /**
   * Returns T if the object is a hashtable.
   */
  public LispValue hashtablep();

  /**
   * Returns the number of items in the hash table.
   */
  public LispValue hash_table_count();

  /**
   * Returns the total size of the hash table, including empty slots.
   */
  public LispValue hash_table_size();

  /**
   * Returns a floating-point number that indicates how large the
   * hash table will be after rehashing, as a percentage of the
   * current size.
   */
  public LispValue hash_table_rehash_size();

  /**
   * Returns a floating-point number that indicates how full the
   * table gets before it will expand and rehash its contents.
   */
  public LispValue hash_table_rehash_threshold();

  /**
   * Returns the function used when comparing keys in the hash table.
   * Default is EQL.
   */
  public LispValue hash_table_test();

  /**
   * Returns T if the object is an Integer.
   */
  public LispValue integerp();

  /**
   * Returns T if the object is a keyword.
   */
  public LispValue keywordp();

  /**
   * Returns the last cons cell in a list.
   * LAST of NIL is NIL.
   */
  public LispValue last();

  /**
   * Returns the length of a list or string.
   */
  public LispValue length();

  /**
   * Returns T if the object is NIL.
   */
  public LispValue lisp_null();

  /**
   * Creates a list from the object.
   * Creates a CONS cell and assigns the original object
   * to the CAR and NIL to the CDR.
   */
  public LispValue list();

  /**
   * Returns T if the object is a list.
   * True if it is a CONS cell or NIL.
   */
  public LispValue listp();

  /**
   * Returns the tail of the list starting at the
   * given element.  Uses EQL as the comparator.
   */
  public LispValue member(LispValue elt);

  /**
   * Destructively appends a list to the end of the
   * given list.
   */
  public LispValue nconc(LispValue arg);

  /**
   * Return the negative of a number.
   */
  public LispValue negate();

  /**
   * Not in the LISP standard, but useful so we
   * don't have to compose (NOT (EQL ...)) when creating expressions.
   */
  public LispValue neql(LispValue val);

  /**
   * Returns the ninth element of a list, or NIL if
   * the list is shorter than nine elements.
   */
  public LispValue ninth();

  /**
   * Destructively reverses the given list.
   * May or may not return the same pointer.
   */
  public LispValue nreverse();

  /**
   * Returns T if the object is any kind of number.
   */
  public LispValue numberp();

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
  public LispValue pop();

  /**
   * Returns the index of an element in a sequence.
   * Currently works for lists and strings.
   * Comparison is by EQL for lists and CHAR= for lists.
   * Returns NIL if the element is not found in the sequence.
   */
  public LispValue position(LispValue element);

  /**
   * Prints the value to standard output with *print-escape*
   * bound to T.
   * It will print escape characters in order to make the input readable
   * by a computer.
   */
  public LispValue prin1();

  /**
   * Prints the output so that it is readable to a person.
   */
  public LispValue princ();

  /**
   * Prints using prin1, except the output is preceded by a newline
   * and terminated by a space.
   */
  public LispValue print();

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
  public LispValue push(LispValue value);

  /**
   * Like ASSOC except it matches the CDR of the cell
   * instead of the CAR.
   */
  public LispValue rassoc(LispValue index);

  /**
   * Reads a value from the given string.
   * @return a LispValue as read by the LISP Reader
   */
  LispValue readFromString();

  /**
   * Computes 1/x of the given number.  Only valid for numbers.
   * @return a LispReal
   */
  public LispValue reciprocal();

  /**
   * Removes an element from a hash table.
   */
  public LispValue remhash(LispValue key);

  /**
   * Returns a copy of a list without all copies of the
   * given element.
   */
  public LispValue remove(LispValue elt);

  /**
   * Synonym for CDR.
   */
  public LispValue rest();

  /**
   * Returns the reversed value of a list.
   */
  public LispValue reverse();

  /**
   * Replaces the CAR of a CONS cell.
   */
  public LispValue rplaca(LispValue newCar);

  /**
   * Replaces the CDR of a CONS cell.
   */
  public LispValue rplacd(LispValue newCdr);

  /**
   * Returns the second element of a list or NIL
   * if the list is shorter than two elements.
   */
  public LispValue second();

  /**
   * Sets the function of a symbol.
   */
  public LispValue setf_symbol_function(LispValue newFunction);

  /**
   * Sets the property list of a symbol.
   */
  public LispValue setf_symbol_plist(LispValue newPlist);

  /**
   * Sets the value of a symbol.
   */
  public LispValue setf_symbol_value(LispValue newValue);

  /**
   * Assigns a value to a symbol.
   */
  public LispValue setq(LispValue newValue);

  /**
   * Returns the seventh element of a list or NIL
   * if the list is shorter than seven elements.
   */
  public LispValue seventh();

  /**
   * Returns the sixth element of a list or NIL
   * if the list is shorter than six elements.
   */
  public LispValue sixth();

  /**
   * Converts a String, Symbol or Character to a string.
   */
  public LispValue string();

  /**
   * Returns T if the object is a string.
   */
  public LispValue stringp();

  /**
   * Converts all the characters to upper case.
   */
  public LispValue stringUpcase();

  /**
   * Converts all of the characters to lower case.
   */
  public LispValue stringDowncase();

  /**
   * Capitalizes the first character of a string and
   * converts the remaining characters to lower case.
   */
  public LispValue stringCapitalize();

  /**
   * For Common LISP compatibility, but identical to stringUpcase.
   */
  public LispValue nstringUpcase();

  /**
   * For Common LISP compatibility, but identical to stringDowncase.
   */
  public LispValue nstringDowncase();

  /**
   * For Common LISP compatibility, but identical to stringCapitalize.
   */
  public LispValue nstringCapitalize();

  /**
   * Returns T if the argument is an identical string to
   * the object.  Character comparison is case-insensitive.
   * STRING-EQUAL.
   */
  public LispValue stringEqual(LispValue arg);

  /**
   * Returns T if the argument is an identical string to
   * the object.  Character comparison is case-sensitive.
   * This is the LISP string= function.
   */
  public LispValue stringEq(LispValue arg);

  /**
   * Returns T if the argument is not STRING= the given string.
   */
  public LispValue stringNeq(LispValue arg);

  /**
   * This is the LISP string&lt; function.
   * Case-sensitive comparison for ordering.
   */
  public LispValue stringLessThan(LispValue arg);

  /**
   * This is the LISP string-lessp function.
   * Case-insensitive comparison for ordering.
   */
  public LispValue stringLessP(LispValue arg);

  /**
   * This is the LISP string&gt; function.
   * Case-sensitive comparison for ordering.
   */
  public LispValue stringGreaterThan(LispValue arg);

  /**
   * This is the LISP string-greaterp function.
   * Case-insensitive comparison for ordering.
   */
  public LispValue stringGreaterP(LispValue arg);

  /**
   * This is the LISP string&lt;= function.
   * Case-sensitive comparison for ordering.
   */
  public LispValue stringLessThanOrEqual(LispValue arg);

  /**
   * This is the LISP string&gt;= function.
   * Case-sensitive comparison for ordering.
   */
  public LispValue stringGreaterThanOrEqual(LispValue arg);

  /**
   * This is the LISP string-not-lessp function.
   * Case-insensitive comparison for ordering.
   */
  public LispValue stringNotLessP(LispValue arg);

  /**
   * This is the LISP string-not-greaterp function.
   * Case-insensitive comparison for ordering.
   */
  public LispValue stringNotGreaterP(LispValue arg);

  /**
   * Not in Common LISP, but useful.  This is case-sensitive.
   */
  public LispValue stringEndsWith(LispValue arg);

  /**
   * Not in Common LISP, but useful.  This is case-sensitive.
   */
  public LispValue stringStartsWith(LispValue arg);

  /**
   * Trims the string by deleting whitespace on both ends.
   */
  public LispValue stringTrim();

  /**
   * Trims the string by deleting characters in the input string on both ends.
   */
  public LispValue stringTrim(LispValue deleteBag);

  /**
   * Trims the left end of the string by deleting whitespace on both ends.
   */
  public LispValue stringLeftTrim();

  /**
   * Trims the left end of the string by deleting characters in the input string on both ends.
   */
  public LispValue stringLeftTrim(LispValue deleteBag);

  /**
   * Trims the right end of the string by deleting whitespace on both ends.
   */
  public LispValue stringRightTrim();

  /**
   * Trims the right end of the string by deleting characters in the input string on both ends.
   */
  public LispValue stringRightTrim(LispValue deleteBag);

  /**
   * Replaces all <i>oldValues</i> in a tree with <i>newValue</i>.
   * The default test is EQL.
   */
  public LispValue subst(LispValue newValue, LispValue oldValue);

  /**
   * Returns the substring of a string starting with the nth element.
   * Substring(0) returns a copy of the string.
   */
  public LispValue substring(LispValue start);

  /**
   * Returns the substring of a string starting with the <i>start</i> element
   * and ending just before the <i>end</i> element.
   * Substring(3,5) returns a two-character string.
   */
  public LispValue substring(LispValue start, LispValue end);

  /**
   * Returns T if the object is a symbol.
   */
  public LispValue symbolp();

  /**
   * Returns the function assigned to a symbol.
   */
  public LispValue symbol_function() throws LispException;

  /**
   * Returns a string containing the name of a symbol.
   */
  public LispValue symbol_name();

  /**
   * Returns the package of a symbol.
   */
  public LispValue symbol_package();

  /**
   * Returns the property list of a symbol.
   */
  public LispValue symbol_plist();

  /**
   * Returns the value of a symbol.
   */
  public LispValue symbol_value() throws LispException;

  /**
   * Returns the tenth element of a list or NIL if the list
   * is less than ten elements long.
   */
  public LispValue tenth();

  /**
   * Returns the third element of a list or NIL if the list
   * is less than three elements long.
   */
  public LispValue third();

  /**
   * Converts a numeric value from radians to degrees.
   * @return The value in degrees.
   */
  public LispValue radiansToDegrees();

  // Everything not anything else is a T, although this return value is illegal in CLTL2.
  public LispValue type_of();

  // Everything not anything else is a T, although this return value is illegal in CLTL2.
  public LispValue typep(LispValue type);

  public LispValue zerop();


  // Arithmetic functions

  /**
   * Returns the sum of the object and the object(s) in the argument list.
   * This is the <code>+</code> function in LISP.
   */
  public LispValue add(LispValue args);

  /**
   * Returns the quotient of the object and the object(s) in the argument list.
   * This is the <code>/</code> function in LISP.
   */
  public LispValue divide(LispValue args);

  /**
   * Returns the product of the object and the object(s) in the argument list.
   * This is the <code>*</code> function in LISP.
   */
  public LispValue multiply(LispValue args);

  /**
   * Returns the difference of the object and the object(s) in the argument list.
   * This is the <code>-</code> function in LISP.
   */
  public LispValue subtract(LispValue args);

  /**
   * Returns T if the object prepended to the argument list is
   * in strictly decreasing order.
   */
  public LispValue greaterThan(LispValue arg);

  /**
   * Returns T if the object prepended to the argument list is
   * in non-increasing order.
   */
  public LispValue greaterThanOrEqual(LispValue arg);

  /**
   * Returns T if the object prepended to the argument list is
   * in strictly increasing order.
   */
  public LispValue lessThan(LispValue arg);

  /**
   * Returns T if the object prepended to the argument list is
   * in strictly non-decreasing order.
   */
  public LispValue lessThanOrEqual(LispValue arg);

  /**
   * Returns T if the object is EQUAL to its argument.
   */
  public LispValue equalNumeric(LispValue arg);

  /**
   * Returns the maximum element of a list of numbers.
   */
  LispValue     max          (LispValue args);

  /**
   * Returns the minimum element of a list of numbers.
   */
  LispValue     min          (LispValue args);

  /**
   * Arccos function.  Argument in radians.
   * Also called Inverse Cosine, this is the
   * angle whose cosine is the argument.
   */
  LispValue     acos          ();

  /**
   * Arcsin function.  Argument in radians.
   * Also called Inverse Sine, this is the
   * angle whose sine is the argument.
   */
  LispValue     asin          ();

  /**
   * Arctan function.  Argument in radians.
   * Also called Inverse Tangent, this is the
   * angle whose tangent is the argument.
   */
  LispValue     atan          ();

  /**
   * Arctan function.  Argument in radians.
   * Also called Inverse Tangent, this is the
   * angle whose tangent is y/x, where y is
   * the first argument and x is the second argument.
   */
  LispValue     atan2          (LispValue x);

  /**
   * Cosine function, argument in radians.
   */
  LispValue     cos          ();

  /**
   * Cosecant function, 1/sin(x), argument in radians.
   */
  LispValue     csc          ();

  /**
   * Cotangent function, 1/tan(x), argument in radians.
   */
  LispValue     cot          ();

  /**
   * Secant function, 1/cos(x), argument in radians.
   */
  LispValue     sec          ();

  /**
   * Sine trigonometric function, argument is in radians.
   */
  LispValue     sin          ();

  /**
   * Square root, accepts negative numbers.
   */
  LispValue     sqrt         ();

  /**
   * Tangent trigonometric function, argument is in radians.
   */
    LispValue     tan          ();

  /**
   * Returns the smallest integer greater than or equal
   * to the input number.
   */
  LispValue     ceiling      ();

  /**
   * Returns the largest integer less than or equal
   * to the input number.
   */
  LispValue     floor        ();

  LispValue elt(int index);

  LispValue functionp();

    /**
     * Returns the documentation string for this symbol, of the type specified.
     * The type may be any symbol, but the most common ones are:
     * <ul>
     * <li>variable (for defvar, defparameter, defconstant)</li>
     * <li>function (for defun, defmacro)</li>
     * <li>structure (for defstruct)</li>
     * <li>type (for deftype)</li>
     * <li>setf (for defsetf)</li>
     * </ul>
     * @param type a symbol
     * @return a LispString or NIL
     */
    LispValue documentation(final LispValue type);

    /**
     * Sets the documentation string for this symbol of the type specified.
     * The type may be any symbol, but the most common ones are:
     * <ul>
     * <li>variable (for defvar, defparameter, defconstant)</li>
     * <li>function (for defun, defmacro)</li>
     * <li>structure (for defstruct)</li>
     * <li>type (for deftype)</li>
     * <li>setf (for defsetf)</li>
     * </ul>
     * @param type a symbol
     * @param value a lispstring
     * @return value
     */
    LispValue setf_documentation(final LispValue type, final LispValue value);

  /**
   * Returns true if this package uses the given package
   */
  boolean uses(LispValue pkg);

}