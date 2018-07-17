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

package org.jatha.compile;

import java.io.*;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;


// @date    Fri Jan 31 17:31:40 1997
/**
 * The LispPrimitive class makes the
 * transition from LISP code to Java code.  There
 * is a LispPrimitive for each builtin LISP function.
 *
 *  1) Create the new LISP primitive as an instance of
 *     this class.  It must have several methods as
 *     described below.
 *  2) Register the new primitive with the compiler.
 *
 * Each primitive must implement one method:
 *
 *   public void Execute(SECDMachine machine)
 *
 * @see org.jatha.compile.LispCompiler
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
public abstract class LispPrimitive extends StandardLispValue
{
  public static LispValue s_PRIMITIVE_TAG = null;
  
  // Fields
  protected long minNumberOfArgs;
  protected long maxNumberOfArgs;

  /**
   * Set inlineP to true if the function effectively evaluates itself
   * simply by compiling its argument list.  This is true for
   * functions like LIST, LIST*, and QUOTE.  This inhibits putting
   * the function call on the stack, thus saving a millisecond of time.
   *
   * @see org.jatha.compile.LispPrimitive
   */
  public boolean inlineP = false;


  /**
   * the <tt>functionName</tt> is part of the string that
   * gets printed when the instruction appears in a printed list.
   */
  protected String    functionName;
  protected LispValue functionNameSymbol;

  public void initConstants()
  {
    if (s_PRIMITIVE_TAG == null)
      s_PRIMITIVE_TAG = f_lisp.getEval().intern("PRIMITIVE", 
          (LispPackage)f_lisp.findPackage("KEYWORD"));
  }
  
  /**
   * The output of this function is printed when the
   * instruction needs to be printed.
   */
  public String toString()
  {
    return "#<function " + functionName + " " + parameterCountString() + ">";
  }

  public boolean basic_functionp()
  {
    return true;
  }


/* ------------------  PRINT FUNCTION   ------------------------------ */

  /**
   * printCode prints a list of compiled code in a nice manner.
   * Calls the 'grindef' function on each primitive.
   * Grindef is an historical LISP function not found in Common LISP.
   *
   * Example:
   * <pre>
   *   printCode(compiled-function, 2);
   * </pre>
   * @see LispPrimitive
   * @param code the code to be printed, with indent 2.
   */
  public void printCode(LispValue code)
  {
    printCode(code, 2);
  }

  public void printCode(LispValue code, int indentAmount)
  {
    while (code != f_lisp.NIL)
    {
      // Cleaned up code handler to handle all cases correctly (mh) 9 Mar 2008
      //System.out.println("Printing code: " + code);
      if (code.car().eql(s_PRIMITIVE_TAG) == f_lisp.T)
        code = ((LispPrimitive)code.second()).grindef(code, indentAmount);  // handles built-in functions
      else
        code = ((LispPrimitive)code.car()).grindef(code, indentAmount);  // handles user-defined functions
    }
  }


  public LispValue grindef(LispValue code, int indentAmount)
  {
    indent(indentAmount);
    System.out.print(functionName);
    f_lisp.NEWLINE.internal_princ(System.out);

    return code.cdr();
  }


  public void indent(int amount)
  {
    for (int i=0; i<amount; ++i)
      f_lisp.SPACE.internal_princ(System.out);
  }


/* ------------------ CONSTRUCTORS    ------------------------------ */

  /**
   * The constructor for the LispPrimitive class.
   * @see org.jatha.compile.LispCompiler
   * @param fnName The LISP function name being implemented.
   * @param minArgs      The minimum number of Arguments to this function.
   * @param maxArgs      The maximum number of Arguments to this function.
   */
  public LispPrimitive(Jatha lisp, String fnName, long minArgs, long maxArgs)
  {
    super(lisp);
    minNumberOfArgs     = minArgs;
    maxNumberOfArgs     = maxArgs;
    functionName        = fnName;
    functionNameSymbol  = new StandardLispSymbol(f_lisp, fnName);
    
    initConstants();
  }

  public LispPrimitive(Jatha lisp, String fnName, long minArgs)
  {
    super(lisp);
    minNumberOfArgs     = minArgs;
    maxNumberOfArgs     = minArgs;  // default value
    functionName        = fnName;
    functionNameSymbol  = new StandardLispSymbol(f_lisp, fnName);
    
    initConstants();
  }

  public LispPrimitive(Jatha lisp, String fnName) // Abstract machine ops have no args
  {
    super(lisp);
    minNumberOfArgs     = 0;         // default value
    maxNumberOfArgs     = 0;         // default value
    functionName        = fnName;
    functionNameSymbol  = new StandardLispSymbol(f_lisp, fnName);
    
    initConstants();
  }

  public String    LispFunctionNameString() { return functionName; }
  public LispValue LispFunctionNameSymbol() { return functionNameSymbol; }

  public void internal_princ(PrintStream os) { os.print(toString()); }
  public void internal_prin1(PrintStream os) { os.print(toString()); }
  public void internal_print(PrintStream os) { os.print(toString()); }

  /**
   * This method returns <code>true</code> if
   * the list of arguments satisfies the length restrictions
   * posed by the function, and <code>false</code> otherwise.
   * @see LispPrimitive
   * @param numberOfArguments  usually the result of args.length()
   * @return boolean
   */
  boolean validArgumentLength(LispValue numberOfArguments)
  {
    long numArgs = ((LispInteger)numberOfArguments).getLongValue();

    return ((minNumberOfArgs <= numArgs)
	    && (numArgs <= maxNumberOfArgs));
  }


  /**
   * This method returns <code>true</code> if
   * the list of arguments satisfies the length and format restrictions
   * posed by the function, and <code>false</code> otherwise.
   * It calls <code>validArgumentLength</code>, so the programmer
   * doesn't need to call it.
   * <p>
   * This method is called by the compiler.
   *
   * @see LispPrimitive
   * @see LispCompiler
   * @param args the list of arguments.
   * @return boolean
   */
  public boolean validArgumentList(LispValue args)
  {
    // ?? Need to check keywords, etc. here.
    return (validArgumentLength(args.length()));
  }

  /**
   * This method returns a Java string denoting the length of
   * the expected argument list in some readable form.
   * <p>
   * This method is called by the compiler when an argument count
   * exception is generated.
   *
   * @see LispPrimitive
   * @see LispCompiler
   * @return a Java string denoting the length of the expected argument list.
   */
  public String parameterCountString()
  {
    String result = Long.toString(minNumberOfArgs);

    if (maxNumberOfArgs == Long.MAX_VALUE)
      result += "...";
    else if (maxNumberOfArgs != minNumberOfArgs)
      result += " " + maxNumberOfArgs;

    return result;
  }


  /**
   * Execute performs the operation using the abstract machine
   * registers.  Arguments are found on the S register stack,
   * in reverse order.  UNLIMITED argument lists are collected
   * into a list which is the top element on the stack.
   *
   * The implementation should pop an appropriate number of arguments
   * from the stack, perform a computation, then push a result
   * back on the S stack.  The instruction should then be popped from
   * the C (code) register.  A LispValueFactory objects is available
   * in the static variable <code>LispValueFactory</code>.
   *
   * Example implementations:
   * <pre>
   *   <code>FIRST</code>
   *   class FirstPrimitive extends LispPrimitive
   *   {
   *     public First()
   *     {
   *       super("FIRST", 1);   // 1 argument
   *     }
   *
   *     public void Execute(SECDMachine machine)
   *     {
   *       LispValue arg = machine.S.pop();
   *
   *       machine.S.push(my_first(arg));
   *       machine.C.pop();
   *     }
   *   }
   * </pre>
   *
   * A multi-argument function must pop the arguments in reverse order.
   * <pre>
   *     public void Execute(SECDMachine machine)
   *     {
   *       LispValue arg2 = machine.S.pop();
   *       LispValue arg1 = machine.S.pop();
   *
   *       machine.S.push(my_new_function(arg1, arg2));
   *       machine.C.pop();
   *     }
   *   }
   * </pre>
   *
   * To register the new primitive, call:
   * <pre>
   *    Jatha.COMPILER.Register(new FirstPrimitive());
   * </pre>
   * @see org.jatha.compile.LispCompiler
   * @param machine   The abstract machine instance.
   */
  public abstract void Execute(SECDMachine machine)
    throws CompilerException;


   // Called only on Builtin Functions
   LispValue BuiltinFunctionCode(LispValue fn)
   {
     return ((LispFunction)fn).getCode().second();
   }


  /**
   * The CompileArgs method turns the arguments of the function call
   * into SECD abstract machine code.  Most functions won't need to
   * override the default code generation, but ones that do funny
   * things with argument lists will need to.
   *
   * @see LispCompiler
   * @param compiler
   * @param args
   * @param valueList
   * @param code
   * @return LispValue The code generated and cons'ed onto the front of the incoming code.
   */
  public LispValue CompileArgs(LispCompiler compiler, SECDMachine machine, LispValue args,
			       LispValue valueList, LispValue code)
    throws CompilerException
  {
    return  compiler.compileArgsLeftToRight(args, valueList, code);
  }

  // Todo: PROGN compiles right-to-left, but executes left-to-right, thus recursive calls are not correctly compiled
  

  public LispValue CompileArgs(LispCompiler compiler, SECDMachine machine, LispValue function,
			       LispValue args, LispValue valueList, LispValue code)
    throws CompilerException
  {
    if (this.inlineP)
      return  CompileArgs(compiler, machine, args, valueList, code);
    else
    {
      if (! (function instanceof LispFunction))
        function = function.symbol_function();

      LispValue fncode = ((LispFunction)function).getCode().second();

      return  CompileArgs(compiler, machine, args, valueList, f_lisp.makeCons(fncode, code));
    }
  }


}


