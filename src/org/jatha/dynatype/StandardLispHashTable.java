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

import java.io.*;
import java.util.*;

import org.jatha.Jatha;


// date    Mon Feb 24 22:40:45 1997
/**
 * Implements the Common LISP 'hashtable' type, including
 * all four types: eq, eql, equal, and equalp has tables.
 * The functions gethash, remhash, and setf-gethash are
 * used to perform operatios on hash tables.
 *
 * @see LispValue
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 * @version 1.0
 *
 */
public class StandardLispHashTable extends StandardLispValue implements LispHashTable
{


/* ------------------  Public variables   ------------------------------ */
  /**
   * Use this value to create an EQ hash table.
   *
   *
   */
  LispValue EQ = null;

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon Feb 24 22:52:39 1997
  /**
   * Use this value to create an EQL hash table.
   */
  LispValue EQL = null;

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon Feb 24 22:52:39 1997
  /**
   * Use this value to create an EQUAL hash table.
   */
  LispValue EQUAL = null;

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon Feb 24 22:52:39 1997
  /**
   * Use this value to create an EQUALP hash table.
   */
  LispValue EQUALP = null;



  // Defaults - from Allegro CL v4.3
  /**
   * Default to an EQL hash table.  From Allegro CL 4.3.
   */
  LispValue DEFAULT_TYPE = EQL;

  /**
   * Default size is 103.  From Allegro CL 4.3.
   */
  LispInteger DEFAULT_SIZE = new StandardLispInteger(f_lisp, 103);

  /**
   * Default rehash size is 1.2.  From Allegro CL 4.3.
   */
  LispReal DEFAULT_REHASH_SIZE = new StandardLispReal(f_lisp, 1.2);


  /**
   * Default rehash threshold is 640777/999999.  From Allegro CL 4.3.
   */
  LispReal DEFAULT_REHASH_THRESHOLD = new StandardLispReal(f_lisp, 640777 / 999999.0);


  public void initializeConstants()
  {

    EQ = f_lisp.EVAL.intern("EQ-HASH-TABLE",
                            (LispPackage)(f_lisp.findPackage("KEYWORD")));

    EQL = f_lisp.EVAL.intern("EQL-HASH-TABLE",
                             (LispPackage)(f_lisp.findPackage("KEYWORD")));

    EQUAL = f_lisp.EVAL.intern("EQUAL-HASH-TABLE",
                               (LispPackage)(f_lisp.findPackage("KEYWORD")));

    EQUALP = f_lisp.EVAL.intern("EQUALP-HASH-TABLE",
                                (LispPackage)(f_lisp.findPackage("KEYWORD")));

    DEFAULT_TYPE             = EQL;
  }


  protected LispValue type               = DEFAULT_TYPE;
  protected LispValue size               = DEFAULT_SIZE;
  protected LispValue rehashSize         = DEFAULT_REHASH_SIZE;
  protected LispValue rehashThreshold    = DEFAULT_REHASH_THRESHOLD;

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon Feb 24 22:56:30 1997
  /**
   * The actual hash table.
   *
   * @see java.util.HashMap
   */
  protected HashMap theHashTable;


/* ------------------  Constructors   ------------------------------ */

  public StandardLispHashTable()
  {
    super();
  }


  /**
   * Creates an eql hash table with default sizes
   * and rehash thresholds.  The defaults are from
   * Allegro Common Lisp 4.3
   */
  public StandardLispHashTable(Jatha lisp)
  {
    super(lisp);
    f_lisp = lisp;
  }


    public StandardLispHashTable(final StandardLispHashTable input) {
        this(input.f_lisp,input.type,input.size,input.rehashSize,input.rehashThreshold);
        this.theHashTable = (HashMap)input.toJava();
    }

  // author  Micheal S. Hewett    hewett@cs.stanford.edu
  // date    Mon Feb 24 22:40:45 1997
  /**
   * Send in the test type, size, rehash-size, and rehash-threshold
   * or NIL for any of the parameters.
   */
  public StandardLispHashTable(Jatha lisp, LispValue typeArg, LispValue sizeArg,
		   LispValue rehashSizeArg, LispValue rehashThresholdArg)
  {
    super(lisp);

    f_lisp = lisp;

    initializeConstants();

    // Check parameters
    if (typeArg != f_lisp.NIL)
      type = typeArg;
    else
      type = DEFAULT_TYPE;

    if (sizeArg != f_lisp.NIL)
      size = sizeArg;
    else
      size = DEFAULT_SIZE;

    if (rehashSizeArg != f_lisp.NIL)
      rehashSize = rehashSizeArg;
    else
      rehashSize = DEFAULT_REHASH_SIZE;

    if (rehashThresholdArg != f_lisp.NIL)
      rehashThreshold = rehashThresholdArg;
    else
      rehashThreshold = DEFAULT_REHASH_THRESHOLD;

    // Check the argument types
    if (! size.basic_integerp())
    {
      size = DEFAULT_SIZE;
      //      throw new WrongArgumentTypeException("MAKE-HASH-TABLE",
      //      throw new CompilerException("MAKE-HASH-TABLE requires SIZE to be an integer"
      //				  + " but received a " + size.type_of());
    }

    if (! rehashSize.basic_floatp())
    {
      rehashSize = DEFAULT_REHASH_SIZE;
      //      throw new CompilerException("MAKE-HASH-TABLE requires REHASH-SIZE to be an integer"
      //				  + " but received a " + size.type_of());
      //      throw new WrongArgumentTypeException("MAKE-HASH-TABLE",
      //					   "REHASH-SIZE to be a real number",
      //					   "a " + rehashSize.type_of());
    }

    if (! rehashThreshold.basic_floatp())
    {
      rehashThreshold = DEFAULT_REHASH_THRESHOLD;
      //      throw new CompilerException("MAKE-HASH-TABLE requires REHASH-THRESHOLD to be an integer"
      //				  + " but received a " + size.type_of());
      //      throw new WrongArgumentTypeException("MAKE-HASH-TABLE",
      //					   "REHASH-THRESHOLD to be a real number",
      //					   "a " + rehashThreshold.type_of());
    }

    if ((type != EQ) && (type != EQL) && (type != EQUAL) && (type != EQUALP))
    {
      type = DEFAULT_TYPE;
      //      throw new CompilerException("MAKE-HASH-TABLE requires TYPE to be EQ, EQL, EQUAL, or EQUALP"
      //				  + " but received " + size.type_of());
      //      throw new WrongArgumentTypeException("MAKE-HASH-TABLE",
      //					   "REHASH-THRESHOLD to be a real number",
      //					   "a " + rehashThreshold.type_of());
    }


    // Create the hash table.
    theHashTable = new HashMap((int)  (((LispInteger)size).getLongValue()),
				 (float)(((LispReal)rehashSize).getDoubleValue()));
  }

  public boolean basic_atom()
  {
    return true;
  }

/* ------------------  PUBLIC non-LISP functions   ------------------------------ */

  public void    internal_princ(PrintStream os) { os.print(toString()); }
  public void    internal_prin1(PrintStream os) { os.print(toString()); }
  public void    internal_print(PrintStream os) { os.print(toString()); }

  /**
   * Returns a Java HashMap containing the contents of this HashTable.
   */
  public Object toJava()
  {
    return theHashTable.clone();
  }

    public void assign(final StandardLispHashTable value) {
        this.theHashTable = (HashMap)(value.theHashTable.clone());
    }

  /**
   * Returns the Hash Table as a Collection.  Creates
   * (key . val) cons pairs and stores them in the collection.
   * Returns an ArrayList.
   */
  public Collection toCollection()
  {
    Collection result = new ArrayList(theHashTable.size());
    for (Iterator iterator = theHashTable.keySet().iterator(); iterator.hasNext();)
    {
      Object key = iterator.next();
      Object val = theHashTable.get(key);
      result.add(f_lisp.makeCons(f_lisp.toLisp(key), f_lisp.toLisp(val)));
    }
    return result;// super.toCollection();
  }
/* ------------------  LISP functions   ------------------------------ */

  public LispValue clrhash() { theHashTable.clear(); return this; }


  // Note - 'default' is a reserved word, hence the spelling below.
  public LispValue gethash(LispValue key) { return gethash(key, f_lisp.NIL); }

  public LispValue gethash(LispValue key, LispValue defawlt)
  {
    LispValue result = (LispValue)(theHashTable.get(key));

    if (result == null)
      return defawlt;
    else
      return result;
  }

  public LispValue remhash(LispValue key)
  {
    LispValue result = (LispValue)(theHashTable.remove(key));

    if (result == null)
      return f_lisp.NIL;
    else
      return result;
  }

  public LispValue setf_gethash(LispValue key, LispValue value)
  {
    theHashTable.put(key, value);

    return value;
  }


  public LispValue hashtablep   ()  { return f_lisp.T; }

  public LispValue hash_table_count ()
  { return new StandardLispInteger(f_lisp, theHashTable.size()); }

  /** This should return the number of possible entries
   *  until the table is full, but Java 1.1 doesn't give us
   *  access to that number.
   */
  public LispValue hash_table_size ()
  { return new StandardLispInteger(f_lisp, theHashTable.size()); }

  /** This should return the rehash-threshold, but
   *  Java 1.1 doesn't let us access that number.
   */
  public LispValue hash_table_rehash_size () { return new StandardLispReal(f_lisp, 1.0); }

  /** Java 1.1 doesn't let us access this number.
   */
  public LispValue hash_table_rehash_threshold () { return new StandardLispReal(f_lisp, 1.0); }

  public LispValue hash_table_test () { return f_lisp.EVAL.intern("EQL"); }

  public LispValue type_of      ()  { return f_lisp.HASHTABLE_TYPE; }
  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.HASHTABLE_TYPE))
      return f_lisp.T;
    else
      return f_lisp.NIL;
  }




/*

//
//maphash function hash-table
//with-hash-table-iterator (mname hash-table) {form}*
//
//sxhash object
//
//sxhash computes a hash code for an object and returns the hash code as a
//non-negative fixnum. A property of sxhash is that
//   (equal x y) implies (= (sxhash x) (sxhash y))
//
//The manner in which the hash code is computed is implementation-dependent
//but independent of the particular ``incarnation'' or ``core image.'' Hash
//values produced by sxhash may be written out to files, for example, and
//meaningfully read in again into an instance of the same implementation.

*/
}



