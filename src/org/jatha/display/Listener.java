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

package org.jatha.display;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.jatha.Jatha;
import org.jatha.display.action.RunGCTask;
import org.jatha.display.action.RunAproposTask;
import org.jatha.display.action.RunLoadFileTask;
import org.jatha.dynatype.*;

import javax.swing.*;


public class Listener extends JFrame  implements ActionListener
{
  protected static final int NUM_INPUT_ROWS  = 15;
  protected static final int NUM_OUTPUT_ROWS = 30;
  protected static final int NUM_COLS = 100;
  protected LispInput   f_in;
  protected JTextArea   f_out;
  protected Jatha       f_lisp;

  protected int         f_numberOfHistoryitems = 25;

  JMenuBar f_menuBar = new JMenuBar();

  JMenu    f_jathaMenu    = new JMenu("Jatha");
  JMenu    f_lispMenu     = new JMenu("LISP");
  JMenu    f_historyMenu  = new JMenu("History");
  JMenu    f_windowMenu   = new JMenu("Window");

  LispValue f_listenerColumnsSymbol     = null;
  LispValue f_listenerInputRowsSymbol   = null;
  LispValue f_listenerOutputRowsSymbol  = null;


  protected String f_prompt;

  public Listener(Jatha lisp, String title, String thePrompt)
  {
    super(title);

    f_lisp = lisp;

    // These variables can be used to control the size of the Listener window
    f_listenerColumnsSymbol = f_lisp.getEval().intern("*LISTENER-COLUMNS*");
    if (! f_listenerColumnsSymbol.specialP())
    {
      f_listenerColumnsSymbol.setf_symbol_value(f_lisp.makeInteger(NUM_COLS));
      f_listenerColumnsSymbol.set_special(true);
    }

    f_listenerInputRowsSymbol = f_lisp.getEval().intern("*LISTENER-INPUT-ROWS*");
    if (! f_listenerInputRowsSymbol.specialP())
    {
      f_listenerInputRowsSymbol.setf_symbol_value(f_lisp.makeInteger(NUM_INPUT_ROWS));
      f_listenerInputRowsSymbol.set_special(true);
    }

    f_listenerOutputRowsSymbol = f_lisp.getEval().intern("*LISTENER-OUTPUT-ROWS*");
    if (! f_listenerOutputRowsSymbol.specialP())
    {
      f_listenerOutputRowsSymbol.setf_symbol_value(f_lisp.makeInteger(NUM_OUTPUT_ROWS));
      f_listenerOutputRowsSymbol.set_special(true);
    }

    f_prompt = thePrompt;
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    setupMenuBar();

    getContentPane().setLayout(new BorderLayout());
    setBackground(Color.white);
    setFont(new Font("", 0, 0));
    setResizable(true);

    f_in  = new LispInput(lisp, this, NUM_INPUT_ROWS, NUM_COLS);
    f_in.setFont(new Font("Courier", Font.PLAIN, 12));

    getContentPane().add("North", f_in);
    //f_mainPane.setTopComponent(f_in);

    f_out = new JTextArea(NUM_OUTPUT_ROWS, NUM_COLS);
    f_out.setBackground(Color.white);
    //out.setBackground(new Color(255, 220, 85));
    f_out.setFont(new Font("Courier", Font.PLAIN, 12));
    f_out.setEditable(false);
    f_out.setVisible(true);

    JScrollPane scroller = new JScrollPane(f_out, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    message(f_lisp.getVersionString() + "\n");
    getContentPane().add("Center", scroller);
    //f_mainPane.setBottomComponent(f_out);

    //f_mainPane.setOneTouchExpandable(true);
    //f_mainPane.setResizeWeight(0.0);
    //getContentPane().add(f_mainPane);

    pack();
    setVisible(true);
  }


  protected void setupMenuBar()
  {

    // menu bar
    // INFO
    JMenuItem infoItem = new JMenuItem(f_lisp.getVersionName() + " " + f_lisp.getVersionMajor() + "." + f_lisp.getVersionMinor() + "." + f_lisp.getVersionMicro() + ", " + f_lisp.getVersionDate());
    infoItem.setEnabled(false);
    f_jathaMenu.add(infoItem);

    f_jathaMenu.addSeparator();

    // QUIT
    JMenuItem exitItem = new JMenuItem("Quit");
    exitItem.addActionListener(this);
    f_jathaMenu.add(exitItem);

    // LISP

    // -- Apropos
    JMenuItem aproposItem = new JMenuItem(new RunAproposTask(f_lisp, this, "apropos", null, "Lists all defined symbols and functions", null, null));
    f_lispMenu.add(aproposItem);

    // -- Load
    JMenuItem loadItem = new JMenuItem(new RunLoadFileTask(f_lisp, this, "load file...", null, "Select and load a file into LISP", null, null));
    f_lispMenu.add(loadItem);

    f_lispMenu.addSeparator();
    JMenuItem gcItem = new JMenuItem(new RunGCTask(f_lisp, this, "run GC", null, "Runs the Java garbage collector", null, null));
    f_lispMenu.add(gcItem);

    // HISTORY


    // WINDOW
    JMenuItem statusItem = new JMenuItem("Window settings");
    statusItem.addActionListener(this);
    f_windowMenu.add(statusItem);
    f_windowMenu.addSeparator();

    JMenuItem setInputRowsItem = new JMenuItem("Set input rows");
    setInputRowsItem.addActionListener(this);
    f_windowMenu.add(setInputRowsItem);

    JMenuItem setOutputRowsItem = new JMenuItem("Set output rows");
    setOutputRowsItem.addActionListener(this);
    f_windowMenu.add(setOutputRowsItem);

    JMenuItem setColumnsItem = new JMenuItem("Set columns");
    setColumnsItem.addActionListener(this);
    f_windowMenu.add(setColumnsItem);


    // Main MENU BAR
    f_menuBar.add(f_jathaMenu);
    f_menuBar.add(f_lispMenu);
    f_menuBar.add(f_historyMenu);
    f_menuBar.add(f_windowMenu);
    setJMenuBar(f_menuBar);
  }

  public void message(LispValue expr)
  {
    message(expr, false);
  }

  public void message(LispValue expr, boolean showPrompt)
  {
    if (showPrompt)
      message(f_prompt, false);
    message(expr.toString() + "\n", false);
  }

  public void message(String msg, boolean showPrompt)
  {
    if (showPrompt)
      message(f_prompt, false);
    f_out.append(msg);
  }

  public void message(String msg)
  {
    message(msg, false);
  }

  public void redraw()
  {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        //setVisible(false);
        //invalidate();
        pack();
        //setVisible(true);

      }
    });
  }

  public void addHistoryItem(String s)
  {
    JMenuItem newItem = new JMenuItem(s);
    newItem.addActionListener(this);
    f_historyMenu.add(newItem, 0);

    while (f_historyMenu.getItemCount() > f_numberOfHistoryitems)
      f_historyMenu.remove(f_numberOfHistoryitems);
  }


  public int getNumberOfHistoryitems()
  {
    return f_numberOfHistoryitems;
  }

  public void setNumberOfHistoryitems(int numberOfHistoryitems)
  {
    f_numberOfHistoryitems = numberOfHistoryitems;
  }

  public String getPrompt()
  {
    return f_prompt;
  }

  public void setPrompt(String prompt)
  {
    f_prompt = prompt;
  }

  /**
   * ActionListener - handles menu events.
   */
  public void actionPerformed(ActionEvent event)
  {
    if (event.getSource() instanceof JMenuItem)
    {
      JMenuItem menuItem = (JMenuItem) event.getSource();
      String op = menuItem.getText();

      if (op.equals("Quit"))
        f_lisp.exit();

      else if (op.equalsIgnoreCase("window settings"))
      {
        eval("*LISTENER-COLUMNS*");
        eval("*LISTENER-INPUT-ROWS*");
        eval("*LISTENER-OUTPUT-ROWS*");
      }

      else if (op.equalsIgnoreCase("set input rows"))
        f_in.setText("(SETQ *LISTENER-INPUT-ROWS* ");

      else if (op.equalsIgnoreCase("set output rows"))
        f_in.setText("(SETQ *LISTENER-OUTPUT-ROWS* ");

      else if (op.equalsIgnoreCase("set columns"))
        f_in.setText("(SETQ *LISTENER-COLUMNS* ");

      else  // Must be a history item
        eval(op);

    }
  }

  /**
   * EVAL - evaluate the input, placing it in the history
   * and displaying the output in the output area.
   */
  public void eval(String s)
  {
    f_in.eval(s);
  }

  /**
   * This would be faster if it were event-driven.
   */
  public void checkForWindowSettingsChanges()
  {
    if (f_out.getColumns() != ((LispNumber)f_listenerColumnsSymbol.symbol_value()).getLongValue())
    {
      int newValue = (int)((LispNumber)f_listenerColumnsSymbol.symbol_value()).getLongValue();
      f_in.setColumns(newValue);
      f_out.setColumns(newValue);
      redraw();
    }

    else if (f_in.getRows() != ((LispNumber)f_listenerInputRowsSymbol.symbol_value()).getLongValue())
    {
      int newValue = (int)((LispNumber)f_listenerInputRowsSymbol.symbol_value()).getLongValue();
      f_in.setRows(newValue);
      redraw();
    }

    else if (f_out.getRows() != ((LispNumber)f_listenerOutputRowsSymbol.symbol_value()).getLongValue())
    {
      int newValue = (int)((LispNumber)f_listenerOutputRowsSymbol.symbol_value()).getLongValue();
      f_out.setRows(newValue);
      redraw();
    }
  }

}

