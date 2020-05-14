 

import javax.swing.UIManager;
import java.awt.*;

 

public class Application
{
  boolean packFrame = false;

  //Construct the application
  public Application()
  {
    WaitFrame wFrame = new WaitFrame();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    wFrame.setLocation((screenSize.width - wFrame.getWidth()) / 8, (screenSize.height - wFrame.getHeight()) / 8);
    wFrame.setUndecorated(true);
   
	wFrame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    wFrame.show();
    Frame frame = new Frame(wFrame);

    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
	  if (packFrame)
	  {
		  frame.pack();
	  }
	  else
	  {
		  frame.validate();
	  }
    //Center the window
    Dimension frameSize = frame.getSize();
    if (frameSize.height < screenSize.height)
    {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width < screenSize.width)
    {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2 );
    frame.setVisible(true);
  }

  //Main method
  public static void main(String[] args)
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new Application();
  }
}
