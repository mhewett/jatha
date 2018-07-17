/*
 * ===================================================================
 * Copyright (c) 2003-2004, Hewett Research, Inc.
 *
 * The author can be contacted via email at
 * "hewett@hewettresearch.com"
 *
 * ===================================================================
 *
 *  RunGCTask.java
 *
 * -------------------------------------------------------------------
 * User: hewett
 * Date: Dec 13, 2005
 * Time: 8:55:55 PM
 * -------------------------------------------------------------------
 */

package org.jatha.display.action;

import org.jatha.Jatha;
import org.jatha.display.Listener;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * RunGCTask executes the "gc-all" command.
 */
public class RunGCTask extends ApplicationAction
{
  protected Listener f_listener = null;

  public RunGCTask(Jatha mainApp, Listener listener, String name, Icon icon,
                   String tooltip, KeyStroke acceleratorKey, Integer mnemonic)
  {
    super(mainApp, name, icon, tooltip, acceleratorKey, mnemonic);

    f_listener = listener;
  }

  public void actionPerformed(ActionEvent e)
  {
    f_listener.eval("(gc-full)");
  }

}
