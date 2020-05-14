 

import java.awt.HeadlessException;
import javax.swing.*;
import java.awt.*;

public class Preview extends JFrame
{
  public Preview(Component vc) throws HeadlessException {
    this.setSize(new Dimension(320,240));
    this.setUndecorated(true);
    this.getContentPane().add(vc);
  }

  public Preview() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    this.setTitle("Preview Pane");
  }

}
