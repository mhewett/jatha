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

import org.jatha.Jatha;

import java.util.Iterator;


// @date    Thu Mar 27 13:35:07 1997
/**
 * An abstract class for the CONS and NIL data types.
 *
 * @see LispValue
 * @see LispInteger
 * @see LispReal
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
public abstract class StandardLispConsOrNil extends StandardLispValue  implements LispConsOrNil
{
  public StandardLispConsOrNil()
  {
    super();
  }


  public StandardLispConsOrNil(Jatha lisp)
  {
    super(lisp);
  }


  public boolean basic_consp()
  {
    if (this == f_lisp.NIL)
      return false;
    else
      return true;
  }

  public boolean basic_listp()  { return true; }

  // ------ LISP methods  ----------

  public LispValue butlast()
  {
    if (cdr().consp() != f_lisp.T)
      return f_lisp.NIL;
    else
      return (f_lisp.makeCons(car(), cdr().butlast()));
  }

  public LispValue elt (LispValue index)
  {
    long indexValue;

    if (!index.basic_integerp())
      throw new LispValueNotAnIntegerException("to ELT");
    else
    {
      indexValue = ((LispInteger)index).getLongValue();
      if ((indexValue < 0) || (indexValue > (((LispInteger)this.length()).getLongValue() - 1)))
	throw new LispIndexOutOfRangeException(String.valueOf(indexValue) + " to ELT");

      // All is okay
      LispValue element = this;

      for (int i = 0; i < indexValue; ++i)  element = element.cdr();

      return element.car();
    }
  }


  public LispValue length ()
  {
    long       count = 0;
    LispValue  ptr   = this;

    while (ptr != f_lisp.NIL) { ++count; ptr = ptr.cdr(); }

    return new StandardLispInteger(f_lisp, count);
  }

  public LispValue     listp        ()  { return f_lisp.T;  }

  /**
   * If this object is NIL, returns the argument.
   * Otherwise, destructively appends the argument to this one.
   */
  public LispValue nconc(LispValue arg)
  {
    try {
      if (this == arg)
        throw new Exception("nconc: attaching me to myself: " + arg);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (this == f_lisp.NIL)
      return arg;

    else if (arg.basic_consp())
      this.last().rplacd(arg);

    return this;
  }

  public LispValue nreverse ()
  {
    LispValue head    = this;
    LispValue next    = cdr();
    LispValue result  = f_lisp.NIL;

    // p stays one ahead of the main list pointer.
    while (head != f_lisp.NIL)
    {
      next = head.cdr();     // Save pointer to next element in list.
      head.rplacd(result);   // Alter cdr of head.
      result = head;         // Reset pointer to top of result
      head = next;           // Start over with the next element of the list.
    }

    return result;
  }

  public LispValue pop ()
  {
    // If we get to this method, this must be NIL.
    return this;
  }

  /**
   * Returns the index of an element in a sequence.
   * Currently works for lists and strings.
   * Comparison is by EQL for lists and CHAR= for lists.
   * Returns NIL if the element is not found in the sequence.
   */
  public LispValue position(LispValue element)
  {
    LispValue ptr = this;
    int index = 0;
    while ((ptr != f_lisp.NIL) && (ptr.car().eql(element).basic_null()))
    {
      ptr = ptr.cdr();
      index++;
    }

    if (ptr == f_lisp.NIL)
      return f_lisp.NIL;
    else
      return f_lisp.makeInteger(index);
  }

  public LispValue reverse ()
  {
    LispValue result = f_lisp.NIL;

    for (LispValue p=this; p != f_lisp.NIL; p = p.cdr() )
      result = new StandardLispCons(f_lisp, p.car(), result);

    return result;
  }


  /* Implementation of Iterator interface */
  /**
   * Returns an iterator over the clauses of the path.
   * Each element type is a Clause.
   */
  public Iterator iterator()
  {
    return new LispConsIterator(this);
  }


}
