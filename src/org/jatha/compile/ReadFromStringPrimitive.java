/**
 * Copyright (c) 2007, all rights reserved Hewett Research, LLC
 *
 * User: hewett
 * Date: Apr 25, 2007
 * Time: 9:42:05 PM
 *
 */

package org.jatha.compile;

import org.jatha.Jatha;
import org.jatha.machine.SECDMachine;

public class ReadFromStringPrimitive extends LispPrimitive
{
  public ReadFromStringPrimitive(Jatha lisp)
  {
    super(lisp, "READ-FROM-STRING", 1);
  }

  public void Execute(SECDMachine machine)
  {
    machine.S.push(machine.S.pop().readFromString());
    machine.C.pop();
  }
}
