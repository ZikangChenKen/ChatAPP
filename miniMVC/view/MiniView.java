package yc138_zc45.miniMVC.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;
import provided.utils.view.TabbedFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextPane;

/**
 * The view for the client who sends tasks to be executed by the server.
 */
public class MiniView extends JPanel { 

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -933244443672550867L;

	/**
	 * View to model adapter
	 */
	private IMiniView2ModelAdapter v2mAdpt;
	
	/**
	 * Control panel
	 */
	private final JPanel ControlPanel = new JPanel();
	
	/**
	 * Message displaying panel
	 */
	private final JPanel MessagePanel = new JPanel();
	
	/**
	 * Text field to type in message
	 */
	private final JTextField MessageTextField = new JTextField();
	
	/**
	 * Button to send message
	 */
	private final JButton MessageBttn = new JButton("Send");
	
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
	private final JButton btnSendCustomMsg = new JButton("Send Custom Message");
	
	/**
	 * Button to create a micro-view on the remote user interface
	 */
	private final JButton btnCreateMicro = new JButton("Create Micro");
	private final JTextArea FriendListArea = new JTextArea();

	/**
	 * Create the frame.
	 * @param v2mAdpt The adapter to the model
	 */
	public MiniView (IMiniView2ModelAdapter v2mAdpt) {
		this.v2mAdpt = v2mAdpt;
		initGUI();
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		ControlPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "ChatRoom", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		add(ControlPanel, BorderLayout.NORTH);
		MessagePanel.setBorder(new TitledBorder(null, "Send Message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		ControlPanel.add(MessagePanel);
		MessagePanel.setLayout(new GridLayout(0, 1, 0, 0));
		MessageTextField.setToolTipText("Enter your message here.");
//		MessageTextField.setText("Enter the message here");
		MessageTextField.setColumns(10);
		MessagePanel.add(MessageTextField);
		MessageBttn.setToolTipText("Click to send a normal text message");
		MessageBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = MessageTextField.getText();
				//System.out.println(msg);
				v2mAdpt.sendTextMessage(msg);
			}
		});
		MessagePanel.add(MessageBttn);

		btnSendCustomMsg.setToolTipText("Click to send a custom text message");
		btnSendCustomMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = MessageTextField.getText();
				v2mAdpt.sendBooleanMessage(msg);
			}
		});
//		MessagePanel.add(btnSendCustomMsg);

		btnCreateMicro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				IRandomizer rand = Randomizer.Singleton;
//				int correct = rand.randomInt(1, 100);
//				System.out.println("The correct number is " +correct);
//				v2mAdpt.sendRandNumber(correct);
				v2mAdpt.createMicroView();
			}
		});
		
		MessagePanel.add(btnCreateMicro);
		LeaveRoomBttn.setToolTipText("Click to leave the chat room");
		LeaveRoomBttn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.leave();
				
			}
		});
		
		ControlPanel.add(LeaveRoomBttn);
		
		add(taDisplay, BorderLayout.CENTER);
		FriendListArea.setToolTipText("Member List");
		
		add(FriendListArea, BorderLayout.WEST);
	}
	
	/**
	 * Start the view
	 */
	public void start() {
		setVisible(true);
	}
	
	/**
	 * Append the given message with a linefeed to the text area
	 * @param msg The message to display
	 */
	public void append(String msg) {
		taDisplay.append(msg+"\n");
	}
	
	public void addFriend(String friend) {
		FriendListArea.append(friend + "\n");
	}
	
	/**
	 * Add a component to the view.
	 * @param componentSupplier The component factory
	 * @param label A string to be displayed with the component
	 * @return 
	 */
	public Runnable addComponent(Supplier<JComponent> componentSupplier, String label) {
		TabbedFrame tabFrame = new TabbedFrame(label);
		tabFrame.setBounds(0,500,500,500);
		tabFrame.setMinimumSize(new Dimension(500,400));
		tabFrame.addComponentFac(label, componentSupplier);
		tabFrame.start();
		tabFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		return new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				tabFrame.dispatchEvent(new WindowEvent(tabFrame, WindowEvent.WINDOW_CLOSING));
			}
			
		};
	}
	
	
}