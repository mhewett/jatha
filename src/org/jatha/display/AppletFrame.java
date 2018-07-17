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

import org.jatha.Jatha;
import org.jatha.display.menu.*;

import javax.swing.*;
import java.awt.*;

/**
 * Main frame for Jatha when it is using its own window.
 * Contains a LispListener.
 */
public class AppletFrame extends JFrame
{
  Jatha myApplet;
  JPanel panel;

  public AppletFrame(String title, Jatha jatha, int width, int height)
  {
    // Create the frame with the specified title.
    super(title);
    myApplet = jatha;

    MenuBar menubar = new MenuBar();
    Menu file = new Menu("File", true);
    menubar.add(file);
    file.add(new QuitMenuItem("Quit"));
    this.setMenuBar(menubar);
    file.addActionListener(myApplet);

    panel = new JPanel();
    this.getContentPane().add("Center", panel);
    // this.setSize(width, height);
    this.pack();
    this.setVisible(true);

    myApplet.init();
    myApplet.start();
  }
}
