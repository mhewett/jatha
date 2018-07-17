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

import org.jatha.dynatype.LispValue;
import org.jatha.Jatha;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.ArrayList;


// @date    Wed Mar  5 09:03:03 1997
/**
 * LispInput is a text field that does parenthesis
 * matching and sends its input off to a Lisp
 * Evaluator.
 *
 * @see java.awt.TextField
 * @author  Micheal S. Hewett    hewett@cs.stanford.edu
 *
 */
class LispInput extends JPanel implements Runnable, ActionListener, KeyListener
{
  public static boolean DEBUG = false;

  /* ------------------  PRIVATE variables   ------------------------------ */
  protected JTextArea   f_inputArea      = null;

  protected int         f_matchingPosition = -1;
  protected boolean     f_flashing         = false;
  protected Thread      f_myThread         = null;
  protected Graphics    f_myGraphics       = null;
  protected String      f_input;

  // GUI stuff
  JButton f_largerAreaButton  = new JButton("larger");
  JButton f_smallerAreaButton = new JButton("smaller");
  JButton f_evalButton        = new JButton("EVAL");

  // Font info
  protected FontMetrics fontInfo         = null;
  protected int         fontWidth        = 0;
  protected int         fontHeight       = 0;
  protected Color       fgColor          = null;
  protected Color       bgColor          = null;

  protected int         hFudge = 8;
  protected int         vFudge = 7;

  protected Listener    f_parent;

  protected String      f_saveBuffer  = "";
  protected String      f_lastCommand = "";
  protected boolean     f_firstCharOfCommand = true;
  protected int         f_commandMultiplier = 1;

  protected Font        f_defaultFont = new Font("Courier", Font.PLAIN, 12);

  // Used when matching parentheses
  protected Highlighter.HighlightPainter f_goodPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.cyan);
  protected Highlighter.HighlightPainter f_badPainter =  new DefaultHighlighter.DefaultHighlightPainter(Color.magenta);

  protected java.util.List f_highlights    = new ArrayList(10);
  protected Integer        f_highlightLock = new Integer(17);

  /**
   * Edit this to change the size of the input area.
   * (mh) 2005 Nov 06
   */
  protected int         f_defaultNumberOfInputLines = 15;

  private   Jatha       f_lisp = null;




  /* ------------------  CONSTRUCTOR   ------------------------------ */

  public LispInput(Jatha lisp, Listener parent, int rows, int cols)
  {
    super();

    f_lisp   = lisp;
    f_parent = parent;

    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(new Color(170, 170, 170));  // new Color(255, 255, 153)); // yellow

    // Set up the input area
    f_inputArea = new JTextArea(rows, cols);
    f_inputArea.setFont(f_defaultFont);
    f_inputArea.addKeyListener(this);
    f_inputArea.addCaretListener(new BracketMatcher(this));

    JScrollPane scroller = new JScrollPane(f_inputArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    add(scroller);

    f_largerAreaButton.addActionListener(this);
    f_largerAreaButton.setActionCommand("largerArea");
    f_largerAreaButton.setBackground(this.getBackground());

    f_smallerAreaButton.addActionListener(this);
    f_smallerAreaButton.setActionCommand("smallerArea");
    f_smallerAreaButton.setBackground(this.getBackground());

    f_evalButton.addActionListener(this);
    f_evalButton.setActionCommand("eval");
    f_evalButton.setBackground(this.getBackground());


    // Submit/Expand/Shrink buttons
    Box buttonPanel = new Box(BoxLayout.X_AXIS);
    buttonPanel.add(f_evalButton);
    buttonPanel.add(Box.createHorizontalGlue());
    buttonPanel.add(f_largerAreaButton);
    buttonPanel.add(f_smallerAreaButton);

    add(buttonPanel);

    /*
    myThread = new Thread(this, "Parenthesis Matching");
    myThread.start();
    */
  }

  /* ------------------  PUBLIC methods   ------------------------------ */

  // Set the font - can't do this until it is visible.
  public void setFontInfo()
  {
    f_myGraphics = this.getGraphics();

    fontInfo   = this.getFontMetrics(this.getFont());
    fontWidth  = fontInfo.charWidth('A');
    fontHeight = fontInfo.getHeight();
    fgColor    = this.getForeground();
    bgColor    = this.getBackground().brighter();  // Motif is doing something to us.

    // hFudge     = fontWidth - 2;
  }


  /**
   * Input should be a regular string, which is parsed and evaluated.
   */
  public void eval(String inputString)
  {
    f_parent.message(inputString + "\n", true);
    LispValue result = f_lisp.load(inputString, true);
    if (result.basic_null())
      f_parent.message(";; *** ERROR - unbalanced parentheses in input\n", false);
    else
    {
      f_parent.addHistoryItem(inputString);

      // f_parent.message(f_lisp.eval(input));   // result will be printed in the Output window.
      f_lastCommand = inputString;

      // Select all the text in the input box
      setText(f_lastCommand);
      clearHighlights();
      selectAll();
    }

//    LispParser parser = new LispParser(f_lisp, inputString + " ");
//    LispValue value = f_lisp.NIL;
//    boolean valid   = true;
//
//    try {
//      value = parser.parse();
//      if (DEBUG)
//        System.err.println("Parser produced: " + value);
//    }
//    catch (EOFException ex)
//    { // display a dialog?
//      f_parent.message("\n*** Incomplete LISP Input - fix it and try again.\n");
//      valid = false;
//    }
//
//    if (valid)
//    {
//      eval(value);
//
      f_parent.checkForWindowSettingsChanges();
//    }
  }

  /**
   * Input should be a LispString.
   * @param input a LispString containing a command.
   */
  public void eval(LispValue input)
  {
    f_parent.addHistoryItem(input.toString());

    f_parent.message(input, true);
    f_parent.message(f_lisp.eval(input));   // result will be printed in the Output window.

    f_lastCommand = input.toString();

    // Select all the text in the input box
    setText(f_lastCommand);
    selectAll();
  }


  // This is called when the user hits RETURN in the input window.
  public void actionPerformed(ActionEvent e)
  {
    if (DEBUG)
      System.err.println("ActionPerformed: " + e.getActionCommand());

    String command = e.getActionCommand();

    if (command.equals("comboBoxEdited") || command.equals("eval"))
    {
      String inStr = f_inputArea.getText();
      if (DEBUG)
        System.err.println("Retrievd typed input: " + inStr);

      if (inStr.trim().equals(""))
        return;

      if (DEBUG)
        System.err.println("LispInput: got input: " + inStr);

      eval(inStr);
      f_firstCharOfCommand = true;
    }

    else if (e.getActionCommand().equals("largerArea"))
      incrementEditorLines(1);


    else if (e.getActionCommand().equals("smallerArea"))
      incrementEditorLines(-1);

  }

  public void incrementEditorLines(int increment)
  {
    int numRows = f_inputArea.getRows() + increment;
    if (numRows > 0)
    {
      f_inputArea.setRows(numRows);
      f_parent.redraw();

      if (DEBUG)
        System.err.println("Listener input now has " + numRows + " rows");

      f_inputArea.setText("(setq *LISTENER-INPUT-ROWS* " + numRows + ")");
      ActionEvent newEvent = new ActionEvent(f_inputArea, ActionEvent.ACTION_PERFORMED, "comboBoxEdited");
      actionPerformed(newEvent);
    }
  }



  public void keyPressed(KeyEvent  e) {  }
  public void keyReleased(KeyEvent e) {  }

  /**
   * Implements parenthesis matching
   */
  public void keyTyped(KeyEvent e)
  {
    char key = e.getKeyChar();

    // System.err.println("LispInput.keyTyped: " + key + " (" + e.getKeyCode() +")");
    if ((key == Character.LINE_SEPARATOR) && (e.isControlDown()))
    //   Toolkit.getDefaultToolkit().
    //       getSystemEventQueue().
    //      postEvent(new ActionEvent(e.getSource(),
    //                                ActionEvent.ACTION_PERFORMED,
    //                                 "comboBoxEdited"));
      actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "comboBoxEdited"));

    //int    quoteCount = 0;
    //int    parenCount = 0;

    // Check for editing characters
    //if (handleEmacsCommand(e))
    //  return;


    // Reset command multiplier
    //f_commandMultiplier = 1;


    /*
    // Interrupt the parenthesis matching if it's in progress
    synchronized (myThread)
    {
      if (flashing)
      {
        myThread.interrupt();
      }
    }

    // PARENTHESIS MATCHING
    if (key == ')')
    {
      input = f_comboBox.getSelectedItem().toString() + " ";

      // Count doublequotes - don't check if we are in a string
      for (int i=0; i<input.length(); ++i)
        if (input.charAt(i) == '"') ++ quoteCount;

      if ((quoteCount % 2) == 0)    // Do a paren match
      {
        for (int i=input.length()-1; i>=0; --i)
          if (input.charAt(i) == '"')
            while (input.charAt(--i) != '"');
          else
            if (input.charAt(i) == ')')
              ++parenCount;
            else if (input.charAt(i) == '(')
            {
              --parenCount;
              if (parenCount == 0)  // Highlight the paren for an instant
              {
                matchingPosition = i;
                // Determine whether the matchingPosition is visible
                if ((f_comboBox.getCaretPosition() - matchingPosition) < this.getColumns())
                {
                  synchronized(myThread) { myThread.notify(); }
                }
              }
            }
      }
    }
    */
  }


  /**
   * Returns true if no further event handling is necessary.
   */
  boolean handleEmacsCommand(KeyEvent e)
  {
    //char     key     = e.getKeyChar();
    //int      type    = e.getID();
    // boolean  delChar = true;

    return false;

    /*
    // 29 Sep 2003 (mh)
    // TextField no longer enters Emacs commands into the field
    // so we don't need to delete the character when it is typed.

    // If they type an editing command as the first character of
    // a command, the old command will be deleted, which we don't
    // want.  So we check for first characters and restore the text.

    if (key >= 27)    // not a Control character
    {
      FirstCharOfCommand = false;
      return false;
    }

    if (FirstCharOfCommand)
    {
      f_comboBox.setSelectedItem(LastCommand);
      FirstCharOfCommand = false;
      // delChar = false;
    }

    // EMACS bindings
    if (key == 1)     // CTRL-A, Beginning of Line
    {
      //if (delChar) eraseLastCharTyped();  // Stupid TextField doesn't let us filter characters.
      /*
      if (type == KeyEvent.KEY_TYPED)
        this.setCaretPosition(0);
        */
/*
      CommandMultiplier = 1;
      return true;
    }

    if (key == 2)   // CTRL-B BACKWARD one space
    {
      //if (delChar) eraseLastCharTyped();  // Stupid TextField doesn't let us filter characters.

      int p = getCaretPosition();

      for (int i=0; i<CommandMultiplier; ++i)
        if (p > 0)
          this.setCaretPosition(this.getCaretPosition() - 1);

      CommandMultiplier = 1;
      return true;
    }

    if (key == 4)   // CTRL-D DELETE forward
    {
      //if (delChar) eraseLastCharTyped();  // Stupid TextField doesn't let us filter characters.

      for (int i=0; i<CommandMultiplier; ++i)
      {
        this.setCaretPosition(this.getCaretPosition() + 1);
        eraseLastCharTyped();  // Deletes the previous character.
      }

      CommandMultiplier = 1;
      return true;
    }

    if (key == 5)   // CTRL-E,  EOL
    {
      //if (delChar) eraseLastCharTyped();  // Stupid TextField doesn't let us filter characters.
      if (type == KeyEvent.KEY_TYPED)
        this.setCaretPosition(this.getText().length()+1);

      CommandMultiplier = 1;
      return true;
    }

    if (key == 6)   // CTRL-F FORWARD one space
    {
      //if (delChar) eraseLastCharTyped();  // Stupid TextField doesn't let us filter characters.
      for (int i=0; i<CommandMultiplier; ++i)
        this.setCaretPosition(this.getCaretPosition() + 1);

      CommandMultiplier = 1;
      return true;
    }

    if (key == 11)  // CTRL-K Kill to end of line
    {
      //if (delChar) eraseLastCharTyped();  // Stupid TextField doesn't let us filter characters.

      saveText(killToEndOfLine());

      CommandMultiplier = 1;
      return true;
    }

    if (key == 21)  // CTRL-U Command repeat x4
    {
      //if (delChar) eraseLastCharTyped();  // Stupid TextField doesn't let us filter characters.

      CommandMultiplier *= 4;
      return true;
    }

    if (key == 25)  // CTRL-Y Yank kill buffer
    {
      //if (delChar) eraseLastCharTyped();  // Stupid TextField doesn't let us filter characters.
      for (int i=0; i<CommandMultiplier; ++i)
        restoreText();

      CommandMultiplier = 1;
      return true;
    }

    return false;
    */
  }


  // Editing command - used to erase control characters.
  void eraseLastCharTyped()
  {
/*    String s = getText();
    int    p = getCaretPosition();
    if (p > 0)
    {
      this.setText(s.substring(0, p-1) + s.substring(p));
      this.setCaretPosition(p-1);
    }
    */
  }


  // Editing - deletes string to end of line.
  String killToEndOfLine()
  {
    /*
    String s = getText();
    int    p = getCaretPosition();
    String killed;

    killed = s.substring(p);
    this.setText(s.substring(0, p));

    return killed;
    */
    return "";
  }

  // Editing - save deleted text in a buffer
  void saveText(String s)
  {
    f_saveBuffer = s;
  }


  // Editing - restore deleted text from a buffer
  void restoreText()
  {
    /*
    String s = getText();
    int    p = getCaretPosition();

    setText(s.substring(0, p) + SaveBuffer + s.substring(p));
    setCaretPosition(p + SaveBuffer.length());
    */
  }


  public void run()
  {
    int h, v;     // Top left corner of character to be boxed.

    synchronized (f_myThread) {
      while (true)
      {
        try { f_myThread.wait(); }  // Wait to be notified to start flashing
        catch (InterruptedException e) {}

        // can't do this until we are visible.
        if (fontInfo == null) setFontInfo();

        f_flashing = true;
        // draw a box around the matching character.
        /* Fixed-width font
        // The font is a fixed-width font.
        // h = matchingPosition * fontWidth + hFudge;
        */

        // Variable-width font
        h = hFudge + fontInfo.stringWidth(f_input.substring(0, f_matchingPosition));
        v = vFudge;

        f_myGraphics.setXORMode(Color.white);
        f_myGraphics.fillRect(h, v, fontWidth, fontHeight);

        // Sleep for 0.5 sec or until user types another key
        try { f_myThread.wait(500L); }
        catch (InterruptedException e) {}
        finally {
          f_flashing = false;
          f_myGraphics.fillRect(h, v, fontWidth, fontHeight);
          f_myGraphics.setPaintMode();
        }
      }
    }
  }


  public String getText()
  {
    return f_inputArea.getText();
  }

  public void setText(String newText)
  {
    f_inputArea.setText(newText);
  }

  public void selectAll()
  {
    f_inputArea.selectAll();
    //((JTextComponent)f_comboBox.getEditor().getEditorComponent()).selectAll();
  }

  public int getColumns()
  {
    return f_inputArea.getColumns();
  }

  public void setColumns(int newValue)
  {
    f_inputArea.setColumns(newValue);
  }

  public void setRows(int newValue)
  {
    f_inputArea.setRows(newValue);
  }

  public int getRows()
  {
    return f_inputArea.getRows();
  }

  public void addGoodHighlight(int start, int end)
  {
    addHighlight(start, end, f_goodPainter);
  }

  public void addBadHighlight(int start, int end)
  {
    addHighlight(start, end, f_badPainter);
  }

  public void addHighlight(int start, int end, Highlighter.HighlightPainter painter)
  {
    Highlighter highlighter = f_inputArea.getHighlighter();

    synchronized(f_highlightLock) {
      try {
        f_highlights.add(highlighter.addHighlight(start, end, painter));
      } catch (BadLocationException ble) {
        // ignore
      }
    }
  }

  public void clearHighlights()
  {
    Highlighter highlighter = f_inputArea.getHighlighter();

    if (highlighter != null)
    {
      synchronized (f_highlightLock) {
        for (Iterator iterator = f_highlights.iterator(); iterator.hasNext();) {
          Highlighter.Highlight highlight = (Highlighter.Highlight) iterator.next();
          highlighter.removeHighlight(highlight);
        }
        f_highlights.clear();
      }
    }
  }


}


class BracketMatcher implements CaretListener
{
  protected LispInput f_input;

  public BracketMatcher(LispInput input)
  {
    f_input = input;
  }

  public void caretUpdate(CaretEvent e)
  {
    JTextComponent source = (JTextComponent) e.getSource();
    int pos = e.getDot();
    Document doc = source.getDocument();
    char key = ' ';

    f_input.clearHighlights();

    try {
      key = doc.getText(e.getDot() - 1, 1).charAt(0);
    } catch (BadLocationException ble) {  // ?? when does this happen?
      return;
    }

    // PARENTHESIS MATCHING
    if (key == ')')
    {
      String input = f_input.getText();
      int quoteCount = 0;
      int parenCount = 0;

      // Count doublequotes - don't check if we are in a string
      for (int i=0; i<pos; ++i)
        if (input.charAt(i) == '"') ++ quoteCount;

      if ((quoteCount % 2) != 0)    // Don't do a paren match if we are inside a string
        return;

      // Find the matching paren
      for (int i=pos-1; i>=0; --i)
      {
        if (input.charAt(i) == '"')
          while (input.charAt(--i) != '"');
        else if (input.charAt(i) == ')')
          ++parenCount;
        else if (input.charAt(i) == '(')
        {
          --parenCount;
          if (parenCount == 0)  // Highlight the paren for an instant
          {
            f_input.addGoodHighlight(i, i+1);
            f_input.addGoodHighlight(pos-1, pos);
            return;
          }
        }
      }

      // If we get here, we didn't match anything.
      f_input.addBadHighlight(pos-1, pos);

    }
  }
}
