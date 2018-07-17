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

package org.jatha.util;

import java.util.*;

import org.jatha.dynatype.*;
import org.jatha.Jatha;


/**
 * This defines a hash table containing LispSymbol class elements.  They are
 * indexed by LispString class elements.
 */
public class SymbolTable extends TreeMap<String, LispValue>
{
  public static final long serialVersionUID = 1L;
  
  // Changed from HashMap to TreeMap, 10 May 2005 (mh), v2.5.0
  
  public static final int   HASH_TABLE_DEFAULT_SIZE        = 4096;
  public static final float HASH_TABLE_DEFAULT_LOAD_FACTOR = (float) 0.8;

  private Jatha f_lisp = null;


  public SymbolTable(Jatha lisp)
  {
    super(); // HASH_TABLE_DEFAULT_SIZE, HASH_TABLE_DEFAULT_LOAD_FACTOR);
    f_lisp = lisp;
  }

  public SymbolTable(Jatha lisp, int defaultSize)
  {
    super(); // defaultSize, HASH_TABLE_DEFAULT_LOAD_FACTOR);
    f_lisp = lisp;
 }

  public SymbolTable(Jatha lisp, int initialSize, float loadFactor)
  {
    super(); // initialSize, loadFactor);
    f_lisp = lisp;
  }

  // Use the Hashtable methods PUT and GET.
  // The 'value' is supposed to always be a symbol,
  // but because NIL is both a Symbol and a List, we
  // must make the type be 'LispValue' instead of
  // 'LispSymbol'.
  public synchronized LispValue put(LispString key, LispValue value)
  {
    try {
      super.put(key.getValue(), value);   // Index by the Java String.
      return value;
    }
    catch (NullPointerException e)
    {
      System.err.println("NullPointerException for " + key + "/" +
          value + " in SymbolTable.put.");
      return null;
    }
  }

  // Returns NIL if the entry is not there.
  public synchronized LispValue get(LispString key)
  {
    Object val = super.get(key.getValue());   // Index by the Java String

    if (val == null)
      return f_lisp.NIL;
    else
      return (LispValue) val;
  }


  // This is useful when a symbol is declared a constant.
  // The old value (a symbol) will be removed and the new
  // constant will take its place.
  public synchronized LispValue replace(LispString key, LispValue value)
  {
    super.remove(key.getValue());

    put(key, value);

    return value;
  }


  // date created: Mon Aug  5 11:34:45 2002
  // created by:   Micheal S. Hewett    hewett@smi.stanford.edu
  /**
   * Returns an Iterator across the symbols of the SymbolTable.
   * Each symbol is a string.
   *
   */
  public Iterator keys()
  {
    return this.keySet().iterator();
  }



}
