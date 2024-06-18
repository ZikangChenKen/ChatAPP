package yc138_zc45.microMVC.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;

/**
 * Micro view.
 */
public class MicroView extends JPanel {
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = -6942369287749983162L;
	
	/**
	 * View to model adapter.
	 */
	private IMicroView2ModelAdapter v2mAdpt;
	
	/**
	 * Panel to display images
	 */
	private JPanel displayPanel = new JPanel(); 
	
	/**
	 * Control panel
	 */
	private final JPanel ControlPanel = new JPanel();
	
	/**
	 * Message displaying panel
	 */
	private final JPanel MessagePanel = new JPanel();
	
	/**
	 * Message displaying panel
	 */
	private final JPanel ImagePanel = new JPanel();
	
	/**
	 * Text field to type in message
	 */
	private final JTextField MessageTextField = new JTextField();
	
	/**
	 * Button to send message
	 */
	private final JButton MessageBttn = new JButton("Guess");
	
	/**
	 * Drop list that contains image names
	 */
	private JComboBox<String> comboBox = new JComboBox<String>();
	
	/**
	 * Button to send image
	 */
	private JButton btnSendImage = new JButton("Send Image");
	
	/**
	 * Button to send message
	 */
	private final JButton btnSendRoom = new JButton("Send Message to Room");
	
	/**
	 * Image
	 */
	private JLabel image = new JLabel();
	
	/**
	 * Button to leave the room
	 */
	private final JButton LeaveRoomBttn = new JButton("Leave");
	
	/**
	 * Text area for displaying message
	 */
	private final JTextArea taDisplay = new JTextArea();
	
	/**
	 * Button to send custom message
	 */
//	private final JButton btnSendCustomMsg = new JButton("Send Custom Message");
	
	/**
	 * Button to create a micro-view on the remote user interface
	 */
	//private final JButton btnCreateMini = new JButton("Create Mini");
	
	
	/**
	 * Constructor for micro view.
	 * @param v2madpt
	 */
	public MicroView(IMicroView2ModelAdapter v2madpt) {
		this.v2mAdpt = v2madpt;
		
		this.init();
		
	}
	
	/**
	 * Initialize the micro view.
	 */
	public void init() {
		setLayout(new BorderLayout(0, 0));
		ControlPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Number Bomb!", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		add(ControlPanel, BorderLayout.NORTH);
		MessagePanel.setBorder(new TitledBorder(null, "Guess", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		ImagePanel.setBorder(new TitledBorder(null, "Image", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		ControlPanel.add(MessagePanel);
		ControlPanel.add(ImagePanel);
		
		comboBox.setToolTipText("Select the image you want to send");
		comboBox.addItem("boyNextDoor");
		comboBox.addItem("CuteBoy");
		comboBox.addItem("CutePanda");
		comboBox.addItem("Drake");
		comboBox.addItem("JieBrotherDont");
		comboBox.addItem("Master");
		ImagePanel.add(comboBox);
		
		MessagePanel.setLayout(new GridLayout(0, 1, 0, 0));
		ImagePanel.setLayout(new GridLayout(0, 1, 0, 0));
		MessageTextField.setToolTipText("Chosse an integer from 1-20");
//		MessageTextField.setText("Enter the message here");
		MessageTextField.setColumns(10);
		MessagePanel.add(MessageTextField);
		MessageBttn.setToolTipText("Click to send a normal text message");
		MessageBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = MessageTextField.getText();
				v2mAdpt.sendGuessMessage(msg);
			}
		});
		MessagePanel.add(MessageBttn);
		
		btnSendImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.sendImageMessage(comboBox.getItemAt(comboBox.getSelectedIndex()));
			}
		});
		ImagePanel.add(btnSendImage);

//		btnSendCustomMsg.setToolTipText("Click to send a custom text message");
//		btnSendCustomMsg.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				String msg = MessageTextField.getText();
//				v2mAdpt.sendBooleanMessage(msg);
//			}
//		});
//		MessagePanel.add(btnSendCustomMsg);

//		btnCreateMini.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				v2mAdpt.createMicroView();
//			}
//		});
		
//		MessagePanel.add(btnCreateMini);
		LeaveRoomBttn.setToolTipText("Click to leave the chat room");
		
		displayPanel.setToolTipText("Images will pop up here");
		 
		displayPanel.setBackground(Color.WHITE);
		displayPanel.setBounds(6, 78, 575, 384);
		add(displayPanel);
		displayPanel.add(image);
		displayPanel.add(taDisplay);
		
	}
	
	/**
	 * Start the micro view.
	 */
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	/**
	 * Display message.
	 * @param msg
	 */
	public void displayMsg(String msg) {
		// TODO Auto-generated method stub
		System.out.println("DISPLAYYYYYYYY: " + msg);
		taDisplay.append(msg + "\n");
		displayPanel.repaint();
		System.out.println(taDisplay.getText());
		
	}

	
	/**
	 * Add an image to the current view
	 * @param componentSupplier The image component factory
	 */
	public void addImage(Supplier<JComponent> componentSupplier) {
		displayPanel.remove(image);
		JLabel temp = (JLabel) componentSupplier.get();
		displayPanel.add(temp);
		image = temp;
		displayPanel.repaint();
	}
	
//	public void update() {
//		displayPanel.repaint();
//	}

}
