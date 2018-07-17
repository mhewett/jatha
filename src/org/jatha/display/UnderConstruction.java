package org.jatha.display;

import java.awt.*;
import javax.swing.*;

/**
 * This is a generic dialog that tells people that some part of the software is not yet working.
 */
public class UnderConstruction extends JFrame {
  private JTextArea jTextArea1 = new JTextArea();
  private JTextPane jTextPane1 = new JTextPane();

  public UnderConstruction(String name, String description) {
    try {
      jbInit(name,description);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit(String title, String message) throws Exception {

    jTextArea1.setLineWrap(true);
    jTextArea1.setWrapStyleWord(true);
    jTextPane1.setBackground(SystemColor.text);
    jTextPane1.setFont(new java.awt.Font("Serif", 1, 25));
    jTextPane1.setForeground(Color.blue);
    jTextPane1.setText("Under Construction");
    jTextArea1.setText(message);
    this.getContentPane().add(jTextPane1, BorderLayout.NORTH);
    this.getContentPane().add(jTextArea1, BorderLayout.CENTER);
    this.getContentPane().add(jTextPane1, BorderLayout.NORTH);
    this.getContentPane().add(jTextArea1, BorderLayout.CENTER);
    this.setSize(new Dimension(675, 600));
    this.setTitle(title);
    this.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = this.getSize();
    if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
    }
    this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    this.setVisible(true);
   }

}