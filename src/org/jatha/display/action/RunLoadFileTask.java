/*
 * ===================================================================
 * Copyright (c) 2003-2005, Micheal S. Hewett.
 *
 * The author can be contacted via email at
 * "mike@hewetthome.net"
 *
 * ===================================================================
 *
 *  RunLoadfileTask.java
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
import java.io.File;

/**
 * RunAproposTask executes the "(apropos "")" command.
 */
public class RunLoadFileTask extends ApplicationAction
{
  protected Listener     f_listener = null;
  protected JFileChooser f_fileChooser = new JFileChooser();


  public RunLoadFileTask(Jatha mainApp, Listener listener, String name, Icon icon,
                   String tooltip, KeyStroke acceleratorKey, Integer mnemonic)
  {
    super(mainApp, name, icon, tooltip, acceleratorKey, mnemonic);

    f_listener = listener;
    f_fileChooser.setDialogTitle("Select file to load into LISP");
    f_fileChooser.setMultiSelectionEnabled(false);
  }

  public void actionPerformed(ActionEvent e)
  {
    String filename = null;

    if (f_fileChooser.showOpenDialog(f_listener) == JFileChooser.APPROVE_OPTION)
    {
      File file = f_fileChooser.getSelectedFile();
      filename = file.getAbsolutePath();
      f_listener.eval("(load \"" + filename + "\")");
    }
  }

}
