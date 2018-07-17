package org.jatha.display.action;

import org.jatha.display.UnderConstruction;

import javax.swing.*;
import java.awt.event.ActionEvent;

import org.jatha.Jatha;

/**
 * Abstract base class used for Application actions.
 * The actions are performed by menu items, buttons and key strokes.
 * User: hewett
 * Date: Mar 7, 2003
 * Time: 11:50:02 AM
 */
public abstract class ApplicationAction extends AbstractAction
{
  protected Jatha    f_app = null;
  protected String   f_name = null;

  /**
   * Creates a new Action. 
   * @param mainApp The application managing this action.
   * @param name The name of the menu item corresponding to this action.
   * @param tooltip A string to be displayed as brief user help.
   * @param acceleratorKey The KeyStroke used to access this action as a menu item.
   * @param mnemonic The (int) character used to access this action when it is a button.
   */
  public ApplicationAction(Jatha mainApp, String name, Icon icon,
                           String tooltip, KeyStroke acceleratorKey, Integer mnemonic)
  {
    super(name, icon);
    f_app     = mainApp;
    f_name    = name;

    // Enabled by default.
    this.setEnabled(true);

    // Set various fields
    if (acceleratorKey != null)
      this.putValue(ACCELERATOR_KEY, acceleratorKey);

    if (mnemonic != null)
      this.putValue(MNEMONIC_KEY, mnemonic);

    if (name != null)
      this.putValue(NAME, name);

    if (tooltip != null)
      this.putValue(SHORT_DESCRIPTION, tooltip);
  }


   public String getText(){
      return (String) getValue(NAME);
   }

    public void setText(String text){
      putValue(NAME, text);
   }

   public ImageIcon getIcon(){
      return (ImageIcon) getValue(SMALL_ICON);
   }

   public void setIcon(ImageIcon icon){
      putValue(SMALL_ICON, icon);
   }

   public Integer getMnemonicKey(){
      return (Integer) getValue(MNEMONIC_KEY);
   }

   public void setMnemonicKey(Integer mnemonicKey){
      putValue(MNEMONIC_KEY, mnemonicKey);
   }

   public String getDescription(){
      return (String) getValue(SHORT_DESCRIPTION);
   }

   public void setDescription (String description){
      putValue(SHORT_DESCRIPTION, description);
   }

   public void actionPerformed(ActionEvent e) {
      new UnderConstruction(f_name, "The " + f_name + " operation is under construction.");
   }
}
