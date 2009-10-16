package net.humandoing.jarindexer;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * User: danwin
 * Date: Jun 23, 2005
 * Time: 10:58:28 AM
 */
public class CloseWindowAdapter extends WindowAdapter {

  private JFrame parent;

  public CloseWindowAdapter(JFrame parent) {
    this.parent = parent;
  }

  public void windowClosing(WindowEvent e) {
    try {
      Database.getInstance().shutdown();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    parent.setVisible(false);
    parent.dispose();
    System.exit(0);
  }

}
