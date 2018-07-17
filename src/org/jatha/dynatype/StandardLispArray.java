package org.jatha.dynatype;

import java.io.*;
import java.util.*;

import org.jatha.Jatha;

public class StandardLispArray extends StandardLispValue implements LispArray
{
  protected LispValue[] theArray;
  protected ArrayList<Integer> dimensions;
  protected ArrayList<Integer> multipliers;
  protected int totalSize;
  protected LispValue defaultValue;

  public StandardLispArray(Jatha lisp, LispValue dimensionsArg) throws LispException
  {
    super(lisp);

    f_lisp = lisp;

    dimensions = new ArrayList<Integer>();
    defaultValue = lisp.NIL;

    Collection collection = dimensionsArg.toCollection();
    totalSize = 1;
    for (Object obj : collection)
    {
      if (((LispValue) obj).basic_integerp())
      {
        long objLongValue = ((LispNumber) obj).getLongValue();

        if (objLongValue > Integer.MAX_VALUE)
        {
          throw new LispIndexOutOfRangeException(String.format("%d", objLongValue));
        }

        dimensions.add((int) objLongValue);
        
        long proposedSize = totalSize * objLongValue;
        if (proposedSize > Integer.MAX_VALUE)
        {
          throw new LispIndexOutOfRangeException(String.format("%d", proposedSize));
        }
        
        totalSize = (int) proposedSize;
      }
      else
      {          
        throw new LispValueNotAnIntegerException(obj.toString());
      }
    }

    theArray = new LispValue[totalSize];
    multipliers = new ArrayList<Integer>(dimensions.size());

    for (int i = 0; i < dimensions.size(); i++)
    {
      int multiplier = 1;
      for (int j = i + 1; j < dimensions.size(); j++)
      {
        multiplier *= dimensions.get(j);
      }
      multipliers.add(i, multiplier);
    }
  }
  
  public StandardLispArray(Jatha lisp, LispValue dimensionsArg, List<LispValue> data) throws LispException
  {
      this(lisp, dimensionsArg);
      
      int idx = 0;
      for (LispValue val : data)
      {
          theArray[idx++] = val;
      }
  }

  @Override
  public void internal_princ(PrintStream os)
  {
    os.print(toString());
  }

  @Override
  public void internal_prin1(PrintStream os)
  {
    os.print(toString());
  }

  @Override
  public void internal_print(PrintStream os)
  {
    os.print(toString());
  }

  @Override
  public Object toJava()
  {
    return toCollection();
  }
  
  @Override
  public LispValue arrayp()  
  { 
    return f_lisp.T; 
  }
  
  public LispValue arrayDimensions()
  {
      LispValue rest = f_lisp.NIL;
      for (int i = dimensions.size() - 1; i >= 0; i--)
      {
          rest = f_lisp.makeCons(f_lisp.makeInteger(dimensions.get(i)), rest);
      }
      return rest;
  }

  public void assign(final StandardLispArray value)
  {
    this.theArray = new LispValue[value.theArray.length];
    System.arraycopy(value.theArray, 0, this.theArray, 0, value.theArray.length);
    this.dimensions = (ArrayList<Integer>) (value.dimensions.clone());
    this.multipliers = (ArrayList<Integer>) (value.multipliers.clone());
    this.totalSize = value.totalSize;
    this.defaultValue = value.defaultValue;
  }

  @Override
  public Collection toCollection()
  {
    return toCollectionSubarray(0, 0);
  }
  
  private Collection toCollectionSubarray(int offset, int depth)
  {
    Collection collection = new ArrayList();
    int multiplier = multipliers.get(depth);
    if (depth < dimensions.size() - 1)
    {
      for (int i = 0; i < dimensions.get(depth); i++)
      {
        collection.add(toCollectionSubarray(offset + i * multiplier, depth + 1));
      }
    }
    else
    {
      for (int i = 0; i < dimensions.get(depth); i++)
      {
        collection.add(lookup(offset + i * multiplier));
      }
    }
    return collection;
  }
  

  @Override
  public LispValue type_of()
  {
    return f_lisp.ARRAY_TYPE;
  }

  @Override
  public LispValue typep(LispValue type)
  {
    LispValue result = super.typep(type);

    if ((result == f_lisp.T) || (type == f_lisp.ARRAY_TYPE))
    {
      return f_lisp.T;
    }
    else
    {
      return f_lisp.NIL;
    }
  }

  @Override
  public String toString()
  {
    StringBuffer buffer = new StringBuffer(String.format("#%dA(", dimensions.size()));
    toStringSubarray(buffer, 0, 0);
    buffer.append(')');
    return buffer.toString();
  }

  private void toStringSubarray(StringBuffer buffer, int offset, int depth)
  {
    int multiplier = multipliers.get(depth);
    if (depth < dimensions.size() - 1)
    {
      for (int i = 0; i < dimensions.get(depth); i++)
      {
        buffer.append('(');
        toStringSubarray(buffer, offset + i * multiplier, depth + 1);
        buffer.append(')');
      }
    }
    else
    {
      for (int i = 0; i < dimensions.get(depth); i++)
      {
        if (i > 0)
        {
          buffer.append(' ');
        }
        buffer.append(lookup(offset + i * multiplier).toString());
      }
    }
  }
  
  private LispValue lookup(int idx)
  {
    if (idx >= theArray.length)
    {
      return defaultValue;
    }
    return theArray[idx] == null ? defaultValue : theArray[idx];
  }

  
  private int indexFromLocation(LispValue location) throws LispException
  {
    long index = 0;
    for (int i = 0; i < multipliers.size(); i++)
    {
      LispValue val = location.car();
      if (val.basic_integerp())
      {
        long longValue = ((LispNumber) val).getLongValue();
        if (longValue >= dimensions.get(i))
        {
          throw new LispIndexOutOfRangeException(String.format("%d", longValue));
        }
        index += (int) longValue * multipliers.get(i);
      }
      else
      {
        throw new LispValueNotAnIntegerException(val.toString());
      }
      location = location.cdr();
    }
    return (int) index;
  }
    
  @Override
  public LispValue setf_aref(LispValue location, LispValue value) throws LispException
  { 
    int index = indexFromLocation(location);
    theArray[index] = value;
    return value;
  }

  @Override
  public LispValue aref(LispValue args) throws LispException
  {
    // Either the arguments are given as a list in the first argument, or 
    // are given as the rest of the arguments.
    int index = args.car().basic_listp() 
            ? indexFromLocation(args.car()) : indexFromLocation(args);
    return lookup(index);
  }
}



