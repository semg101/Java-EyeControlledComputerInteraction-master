 

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class WaitFrame extends JFrame {
  JPanel jPanel1 = new JPanel();
  TitledBorder titledBorder1;
  JLabel jLabel1 = new JLabel();
  public WaitFrame() throws HeadlessException {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception
  {
    titledBorder1 = new TitledBorder("");
    this.getContentPane().setLayout(null);
    jPanel1.setBorder(BorderFactory.createLineBorder(Color.red);
    jPanel1.setBounds(new Rectangle(0, 0, 448, 57));
    jPanel1.setLayout(null);
    this.setSize(new Dimension(448, 57));
    jLabel1.setText("Loading \'Support Vector Machine\' model file, and connecting to the webcam.");
    jLabel1.setBounds(new Rectangle(39, 21, 371, 15));
    this.getContentPane().add(jPanel1, null);
    jPanel1.add(jLabel1, null);
  }

}
