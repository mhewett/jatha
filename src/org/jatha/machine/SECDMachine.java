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

package org.jatha.machine;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.compile.*;


// @date    Sat Feb  1 21:04:49 1997
/**
 * The SECDMachine contains the registers and
 * basic functionality of the SECD machine.
 * It exports constants corresponding to each
 * primitive machine instruction and to each
 * primitive LISP operation, so that the compiler
 * may insert them into code.
 *
 * A modification to the standard SECD machine is
 * the new 'B' register that handles dynamic
 * binding.
 *
 * @see SECDop
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 */
public class SECDMachine    // extends Abstract Machine !
{

  Jatha f_lisp = null;

  public static boolean DEBUG = false;

  // ------  Registers  --------------

  public SECDRegister S = null;  // Random names nobody will accidentally use
  public SECDRegister E = null;  // These should be protected from user change.
  public SECDRegister C = null;
  public SECDRegister D = null;
  // The B register is for dynamic bindings.  It contains a hash table
  // that indexes on symbol name.  The value is a list of values,
  // the most recent value at the front of the list.
  //
  // There is a B register for each machine so that it will
  // function correctly in a multi-threaded environment.
  public LispValue B = null;

    // A X register for dumping tag information, as a stack. This is the same register as D, but not totally. =)
  public SECDRegister X = null;

/* ------------------  BASIC MACHINE OPS   ------------------------------ */

  public SECDop AP = null;
  public SECDop BLK = null; //OB: new opcode June 2005
  public SECDop DAP = null;
  public SECDop DUM = null;
  public SECDop JOIN = null;
  public SECDop LD = null;
  public SECDop LD_GLOBAL = null;
  public SECDop LDC = null;
  public SECDop LDF = null;
  public SECDop LDFC = null;
  public SECDop LDR = null;   //##JPG new opcode  April 2005
  public SECDop LIS = null;
  public SECDop NIL = null;
  public SECDop RAP = null;
  public SECDop RTN = null;
  public SECDop RTN_IF = null;
  public SECDop RTN_IT = null;
  public SECDop SEL = null;
  public SECDop SP_BIND = null;
  public SECDop SP_UNBIND = null;
  public SECDop STOP = null;
  public SECDop T = null;
  public SECDop TAG_B = null;
  public SECDop TAG_E = null;
  public SECDop TEST = null;

  public SECDMachine(Jatha lisp)
  {
    f_lisp = lisp;

    S = new SECDRegister(f_lisp, "S-05171955");  // Random names nobody will accidentally use
    E = new SECDRegister(f_lisp, "E-06141957");  // These should be protected from user change.
    C = new SECDRegister(f_lisp, "C-06151962");
    D = new SECDRegister(f_lisp, "D-06071966");
    X = new SECDRegister(f_lisp, "X-02324255");
    B = new StandardLispHashTable(f_lisp, f_lisp.NIL, f_lisp.NIL,
                                  f_lisp.NIL, f_lisp.NIL);
    AP     = new opAP(f_lisp);
    BLK    = new opBLK(f_lisp);
    DAP    = new opDAP(f_lisp);
    DUM    = new opDUM(f_lisp);
    JOIN   = new opJOIN(f_lisp);
    LD     = new opLD(f_lisp);
    LD_GLOBAL = new opLD_GLOBAL(f_lisp);
    LDC    = new opLDC(f_lisp);
    LDF    = new opLDF(f_lisp);
    LDFC   = new opLDFC(f_lisp);
    LDR    = new opLDR(f_lisp);  //##JPG init new opcode LDR  April 2005
    LIS    = new opLIS(f_lisp);
    NIL    = new opNIL(f_lisp);
    RAP    = new opRAP(f_lisp);
    RTN    = new opRTN(f_lisp);
    RTN_IF = new opRTN_IF(f_lisp);
    RTN_IT = new opRTN_IT(f_lisp);
    SEL    = new opSEL(f_lisp);
    SP_BIND   = new opSP_BIND(f_lisp);
    SP_UNBIND = new opSP_UNBIND(f_lisp);
    STOP   = new opSTOP(f_lisp);
    T      = new opT(f_lisp);
    TAG_B  = new opTAG_B(f_lisp);
    TAG_E  = new opTAG_E(f_lisp);
    TEST   = new opTEST(f_lisp);

 }


  public Jatha getLisp()
  {
    return f_lisp;
  }


/* ------------------  SPECIAL BINDING   ------------------------------ */



  // Assume the caller has verified that this is a special variable.
  public void special_bind(LispValue symbol, LispValue value)
  {
    // System.err.println("Special bind called on: " + symbol);

    if (symbol.basic_constantp())
    {
      // Cause a LispConstant Redefined error
      symbol.setf_symbol_value(value);
    }
    else
    {
      LispValue bindings = B.gethash(symbol, f_lisp.NIL);

      B.setf_gethash(symbol, f_lisp.makeCons(value, bindings));
      symbol.adjustSpecialCount(+1);
    }
  }


  public void special_unbind(LispValue symbol)
  {
    LispValue bindings = B.gethash(symbol, f_lisp.NIL);

    // System.err.println("Special unbind called on: " + symbol);

    B.setf_gethash(symbol, bindings.cdr());
    symbol.adjustSpecialCount(-1);
  }


  // Sets the binding of a special variable.
  public void special_set(LispValue symbol, LispValue value)
  {
    if (symbol.get_specialCount() > 0)
    {
      LispValue bindings = B.gethash(symbol, f_lisp.NIL);
      B.setf_gethash(symbol, f_lisp.makeCons(value, bindings.cdr()));
    }
    else
      symbol.setf_symbol_value(value);
  }


  // Assume the caller has verified that this is a special variable.
  public LispValue get_special_value(LispValue symbol)
  {
    // System.err.println("specialCount of " + symbol + " is " + symbol.get_specialCount());

    if (symbol.get_specialCount() > 0)
      return B.gethash(symbol).car();
    else
      return symbol.symbol_value();
  }


  public LispValue Execute(LispValue code, LispValue globals)
    throws CompilerException
  {
    LispValue opcode;


    // System.out.print("\nExecuting code: ");
    // code.prin1();

    S.assign(f_lisp.NIL);
    E.assign(globals);
    C.assign(code);
    D.assign(f_lisp.NIL);

    opcode = C.value().car();

    while ((opcode != STOP) && (opcode != f_lisp.NIL))
    {
      if (DEBUG)
      {
        // Test output.
        //
        System.out.print("\n  S: " + S.value());
        System.out.print("\n  E: " + E.value());
        System.out.print("\n  C: " + C.value());
        System.out.print("\n  D: " + D.value());
        System.out.print("\n  B: " + B.toString());
        System.out.print("\n  X: " + X.value());
        // System.out.print(" of class " + opcode.getClass().getName());
        System.out.print("\n" + opcode);   // Testing
        System.out.flush();
      }

      if (opcode != null) {
        ((LispPrimitive)opcode).Execute(this);
      }
      else
      {
        System.err.println("internal error in Jatha.SECDMachine.Execute: opcode is null");
        System.err.println("remaining code is " + C.value().toString());
      }

      try {
        opcode = C.value().car();  // Each opcode pops the C register as necessary
      } catch (Exception e) {
        e.printStackTrace();
        System.err.print("\n  S: " + S.value());
        System.err.print("\n  E: " + E.value());
        System.err.print("\n  C: " + C.value());
        System.err.print("\n  D: " + D.value());
        System.out.print("\n  B: " + B.toString());
        System.out.print("\n  X: " + X.value());
        // System.out.print(" of class " + opcode.getClass().getName());
        System.err.print("\n" + opcode);   // Testing
        System.err.flush();

        opcode = f_lisp.NIL;
      }
    }

    return  S.value().car(); //  Top value on Stack is the return value.
  }

  public void setStackValue(SECDRegister e, LispValue val)
  {
  }
} // End of class SECDMachine.


