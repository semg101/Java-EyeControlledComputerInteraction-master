 

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.media.*;
import svm.*;
import javax.media.protocol.*;

 

public class Frame extends JFrame
{
	Processor processor;
	svm_model model;
	JButton detect = new JButton();
	boolean detectFaces = false;
	boolean track = false,drawEyes = true,drawNose = true,drawBte = false,
		drawROI = false,drawMotion = false,drawBlink = true,drawBrows = true;
	PushBufferDataSource pbds = null;
	CaptureDeviceInfo cdi;
	Component vc,cc;
	Preview preview;
	int cThreshold = 20;
	int bThreshold = 30;
	int brThreshold = 160;
	double blinkLength = 0.5;
	boolean enable = false;
	double xScale = 0,yScale = 0;

	/////////////////////////////////////////////////

	JPanel contentPane;
	MenuBar jMenuBar1 = new MenuBar();
	Menu file = new Menu();
	MenuItem exit = new MenuItem();
	Menu help = new Menu();
	MenuItem about = new MenuItem();
	BorderLayout borderLayout1 = new BorderLayout();
	JButton refresh = new JButton();
	JButton stop = new JButton();
	JCheckBox drawE = new JCheckBox();
	JCheckBox drawR = new JCheckBox();
	JCheckBox drawN = new JCheckBox();
	JCheckBox drawB = new JCheckBox();
	JCheckBox drawM = new JCheckBox();
	SpinnerModel cModel = new SpinnerNumberModel(cThreshold, 1, 255, 1);
	JSpinner cThresholdSpinner = new JSpinner(cModel);
	JLabel jLabel1 = new JLabel();
	JPanel jPanel2 = new JPanel();
	SpinnerModel bModel = new SpinnerNumberModel(bThreshold, 1, 525, 1);
	JSpinner bThresholdSpinner = new JSpinner(bModel);
	JLabel jLabel4 = new JLabel();
	JCheckBox drawBl = new JCheckBox();
	JLabel jLabel5 = new JLabel();
	SpinnerModel bLenModel = new SpinnerNumberModel(blinkLength, 0.1d, 1d, 0.1d);
	JSpinner bLength = new JSpinner(bLenModel);
	JLabel jLabel6 = new JLabel();
	JButton EnVis = new JButton();
	JPanel jPanel4 = new JPanel();
	JLabel jLabel7 = new JLabel();
	JLabel jLabel8 = new JLabel();
	SpinnerModel brModel = new SpinnerNumberModel(brThreshold, 1, 255, 5);
	JSpinner browThresholdSpinner = new JSpinner(brModel);
	JPanel jPanel1 = new JPanel();
	JCheckBox drawBr = new JCheckBox();
	JPanel jPanel3 = new JPanel();
	JLabel jLabel2 = new JLabel();
	JLabel jLabel3 = new JLabel();
	JSpinner xSpinner = new JSpinner();
	JLabel jLabel9 = new JLabel();
	JSpinner ySpinner = new JSpinner();


	//Construct the frame
	public Frame(WaitFrame wFrame)
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try
		{
			jbInit();
			model = svm.svm_load_model("Model.txt");
			findCaptureDevice();
			this.getContentPane().setCursor(Cursor.getDefaultCursor());
			wFrame.hide();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	////////////////////////////////////////

	private void findCaptureDevice()
	{
		DevicesFinder devicesFinder = new DevicesFinder();
		pbds = devicesFinder.findDevices(this);
		if( pbds != null )
		{
			ProcessEffectLauncher pel = new ProcessEffectLauncher();
			processor = pel.open(pbds, cdi, this);
			processor.start();
			vc = processor.getVisualComponent();
			vc.setLocation(5,2);
			vc.setSize(new Dimension(320,240));
			this.getContentPane().add(vc);
			cc = processor.getControlPanelComponent();
			cc.setSize(new Dimension(35,20));
			cc.setLocation(488,78);
			this.getContentPane().add(cc);
		}
	}

	////////////////////////////////////////

	//Component initialization
	private void jbInit() throws Exception  
	{
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(null);
		this.setResizable(false);
		this.setSize(new Dimension(480, 296));
		this.setTitle("Human Computer Interface");
		this.addWindowFocusListener(new Frame_this_windowFocusAdapter(this));
		file.setLabel("File");
		exit.setLabel("Exit");
		exit.addActionListener(new Frame_exit_ActionAdapter(this));
		help.setLabel("Help");
		about.setLabel("About");
		about.addActionListener(new Frame_about_ActionAdapter(this));
		detect.setBounds(new Rectangle(332, 1, 120, 23));
		detect.setText("Detect The Face");
		detect.addActionListener(new Frame_detect_actionAdapter(this));
		refresh.setText("REFRESH");
		refresh.setBounds(new Rectangle(332, 52, 120, 23));
		//detect.addActionListener(new Frame_detect_actionAdapter(this));
		refresh.addActionListener(new Frame_refresh_actionAdapter(this));

		drawE.setSelected(true);

		drawB.setText("Show BTE");
		drawB.setBounds(new Rectangle(14, 62, 88, 23));
		drawB.addActionListener(new Frame_drawB_actionAdapter(this));

		EnVis.setText("Enable Interface");
		EnVis.addActionListener(new Frame_EnVis_actionAdapter(this));
		EnVis.setBounds(new Rectangle(332, 26, 120, 23));
		EnVis.setEnabled(true);
		EnVis.setForeground(Color.red);
		jPanel4.setBorder(BorderFactory.createLineBorder(Color.black));
		jPanel4.setBounds(new Rectangle(562, 3, 118, 161));
		jPanel4.setLayout(null);
		jLabel7.setFont(new java.awt.Font("Dialog", 0, 14));

		file.add(exit);
		help.add(about);
		jMenuBar1.add(file);
		jMenuBar1.add(help);
		this.setMenuBar(jMenuBar1);
		jPanel4.add(jLabel7, null);
		jPanel4.add(jLabel1, null);
		jPanel4.add(browThresholdSpinner, null);
		jPanel4.add(jLabel8, null);
		jPanel4.add(cThresholdSpinner, null);
		jPanel4.add(jLabel4, null);
		jPanel4.add(bThresholdSpinner, null);
		contentPane.add(jPanel3, null);
		contentPane.add(jPanel1, null);
		jPanel1.add(jLabel5, null);
		jPanel1.add(jLabel6, null);
		jPanel1.add(bLength, null);
		contentPane.add(jPanel2, null);
		contentPane.add(EnVis, null);
		contentPane.add(detect, null);
		contentPane.add(stop, null);
		contentPane.add(refresh, null);
		jPanel2.add(drawE, null);
		jPanel2.add(drawN, null);
		jPanel2.add(drawB, null);
		jPanel2.add(drawM, null);
		jPanel2.add(drawBl, null);
		jPanel2.add(drawBr, null);
		jPanel2.add(drawR, null);
		contentPane.add(jPanel4, null);
		jPanel3.add(jLabel2, null);
		jPanel3.add(xSpinner, null);
		jPanel3.add(jLabel3, null);
		jPanel3.add(ySpinner, null);
		jPanel3.add(jLabel9, null); 
	}

	//File | Exit action performed
	public void exit_actionPerformed(ActionEvent e)
	{
		if( processor != null )
		{
			processor.stop();
			processor.close();
			this.remove(vc);
			this.remove(cc);
		}
		System.exit(0);
	}

	//Help | About action performed
	public void about_actionPerformed(ActionEvent e) 
	{
		Frame_AboutBox dlg = new Frame_AboutBox(this);
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(true);
		dlg.pack();
		dlg.show();
	}

	//Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) 
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) 
		{
			exit_actionPerformed(null);
		}
	}

	void refresh_actionPerformed(ActionEvent e)
	{
		this.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		EnVis.setEnabled(false);
		stop.setEnabled(false);
		EnVis.setText("Enable Interface");
		enable = false;
		if (processor != null)
		{
			processor.stop();
			processor.close();
			this.remove(vc);
			this.remove(cc);
			this.track = false;
			findCaptureDevice();
		}
		this.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}


	void detect_actionPerformed(ActionEvent e)
	{
		//start detection
		detectFaces = !detectFaces;
		stop.setEnabled(true);
		EnVis.setEnabled(true);
		stop.setEnabled(true);
	}

	void this_windowLostFocus(WindowEvent e)
	{
	}

	void this_windowGainedFocus(WindowEvent e)
	{
		if( preview != null )
		{
			preview.hide();
			this.getContentPane().add(vc);
		}
		if( enable )
			EnVis.doClick();
	}

	void stop_actionPerformed(ActionEvent e) 
	{
		track = false;
		stop.setEnabled(false);
		EnVis.setEnabled(false);
		EnVis.setText("Enable Interface");
		enable = false;
	}

	void drawE_actionPerformed(ActionEvent e)
	{
		drawEyes = !drawEyes;
	}

	void drawR_actionPerformed(ActionEvent e)
	{
		drawROI = !drawROI;
	}

	void drawN_actionPerformed(ActionEvent e)
	{
		drawNose = !drawNose;
	}

	void drawB_actionPerformed(ActionEvent e)
	{
		drawBte = !drawBte;
	}

	void drawM_actionPerformed(ActionEvent e)
	{
		drawMotion = !drawMotion;
	}

	void drawBl_actionPerformed(ActionEvent e) 
	{
		drawBlink = !drawBlink;
	}

	void EnVis_actionPerformed(ActionEvent e)
	{
		enable = !enable;
		if( !enable )
			EnVis.setText("Enable Interface");
		else
		{
			EnVis.setText("Disable Interface");
			preview = new Preview(vc);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			preview.setLocation( (int) screenSize.getWidth() - 320 - 5,
				5);
			this.setState(Frame.ICONIFIED);
			preview.setVisible(true);
			preview.show();
		}
	}

	void drawBr_actionPerformed(ActionEvent e)
	{
		drawBrows = !drawBrows;
	}



}

class Frame_exit_ActionAdapter implements ActionListener 
{
	Frame adaptee;

	Frame_exit_ActionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.exit_actionPerformed(e);
	}
}

class Frame_about_ActionAdapter implements ActionListener 
{
	Frame adaptee;

	Frame_about_ActionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.about_actionPerformed(e);
	}
}


class Frame_detect_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_detect_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.detect_actionPerformed(e);
	}
}


class Frame_this_windowFocusAdapter implements java.awt.event.WindowFocusListener 
{
	Frame adaptee;

	Frame_this_windowFocusAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void windowGainedFocus(WindowEvent e) 
	{
		adaptee.this_windowGainedFocus(e);
	}
	public void windowLostFocus(WindowEvent e) 
	{
		adaptee.this_windowLostFocus(e);
	}
}

class Frame_stop_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_stop_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.stop_actionPerformed(e);
	}
}

class Frame_drawE_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_drawE_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.drawE_actionPerformed(e);
	}
}

class Frame_drawR_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_drawR_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.drawR_actionPerformed(e);
	}
}

class Frame_drawN_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_drawN_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.drawN_actionPerformed(e);
	}
}

class Frame_drawB_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_drawB_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.drawB_actionPerformed(e);
	}
}

class Frame_drawM_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_drawM_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.drawM_actionPerformed(e);
	}
}

class Frame_drawBl_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_drawBl_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.drawBl_actionPerformed(e);
	}
}

class Frame_EnVis_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_EnVis_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.EnVis_actionPerformed(e);
	}
}

class Frame_drawBr_actionAdapter implements java.awt.event.ActionListener 
{
	Frame adaptee;

	Frame_drawBr_actionAdapter(Frame adaptee) 
	{
		this.adaptee = adaptee;
	}
	public void actionPerformed(ActionEvent e) 
	{
		adaptee.drawBr_actionPerformed(e);
	}
}


