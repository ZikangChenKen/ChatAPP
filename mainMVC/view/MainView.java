package yc138_zc45.mainMVC.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.function.Supplier;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Dimension;

/**
 * The view for the client who sends tasks to be executed by the server.
 * 
 * 
 * @param <TDropListItem> the type in the droplist representing tasks
 */
public class MainView<TDropListItem1, TDropListItem2> extends JFrame { 

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -933244443672550867L;

	/**
	 * View to model adapter
	 */
	private IMainView2ModelAdapter<TDropListItem1, TDropListItem2> v2mAdpt;
	
	/**
	 * Content panel
	 */
	private JPanel contentPane = new JPanel();
	
	/**
	 * Operational panel
	 */
	private JPanel controlPanel = new JPanel();
	
	/**
	 * Button to create chat room
	 */
	private final JButton creatChatRoomBttn = new JButton("Create New ChatRoom");
	
	/**
	 * Text field to type in room name
	 */
	private JTextField roomNameTextField;
	
	/**
	 * Scroll panel
	 */
	private final JScrollPane scrollPane = new JScrollPane();
	
	/**
	 * Text area to display message 
	 */
	private final JTextArea taDisplay = new JTextArea();
	
	/**
	 * Tabbed panel
	 */
	private JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	
	/**
	 * Drop list 1
	 */
	private JComboBox<TDropListItem2> UserList = new JComboBox<>();
	
	/**
	 * Drop list 2
	 */
	JComboBox<TDropListItem1> ChatRoomList = new JComboBox<>();
	private final JPanel remoteHostPnl = new JPanel();
	private final JTextField ConnectTextField = new JTextField("");
	private final JButton btnConnect = new JButton();

	/**
	 * Create the frame.
	 * @param v2mAdpt The adapter to the model
	 */
	public MainView (IMainView2ModelAdapter<TDropListItem1, TDropListItem2> v2mAdpt) {
		this.v2mAdpt = v2mAdpt;
		initGUI();
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setTitle("Main GUI");
		setBounds(100, 100, 924, 406);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		contentPane.add(scrollPane);
		taDisplay.setToolTipText("Panel to display messages");
		
		scrollPane.setViewportView(taDisplay);
		scrollPane.setColumnHeaderView(controlPanel);
		
		controlPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "ChatApp", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		
		JButton quitBttn = new JButton("");
		quitBttn.setToolTipText("Hit to quit");
		quitBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.quit();
			}
		});
		quitBttn.setIcon(new ImageIcon(MainView.class.getResource("/yc138_zc45/mainMVC/view/remove.png")));
		controlPanel.add(quitBttn);
		controlPanel.add(remoteHostPnl);
		
		JPanel ChatRoomCtrlPanel = new JPanel();
		ChatRoomCtrlPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "ChatRoom Connect", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		controlPanel.add(ChatRoomCtrlPanel);
		ChatRoomCtrlPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		roomNameTextField = new JTextField();
		roomNameTextField.setToolTipText("Enter the room name you want to create");
		ChatRoomCtrlPanel.add(roomNameTextField);
		roomNameTextField.setColumns(10);
		ChatRoomList.setToolTipText("Choose the room you want to invite other users into");
		
		
		ChatRoomCtrlPanel.add(ChatRoomList);
		creatChatRoomBttn.setToolTipText("Hit to create new chat room with the name in the text field above");
		creatChatRoomBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.createRoom(roomNameTextField.getText());
			}
		});
		
		ChatRoomCtrlPanel.add(creatChatRoomBttn);
		
		JPanel AppCtrlPanel = new JPanel();
		AppCtrlPanel.setBorder(new TitledBorder(null, "User Connect", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		controlPanel.add(AppCtrlPanel);
		AppCtrlPanel.setLayout(new GridLayout(0, 1, 0, 0));
		UserList.setToolTipText("Choose the user you want to invite");
		

		
		
		AppCtrlPanel.add(UserList);
		
		JButton UserInviteBttn = new JButton("Invite");
		UserInviteBttn.setToolTipText("Hit to invite the selected user into the selected chat room");
		UserInviteBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					v2mAdpt.invite(ChatRoomList.getItemAt(ChatRoomList.getSelectedIndex()), UserList.getItemAt(UserList.getSelectedIndex()));
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		AppCtrlPanel.add(UserInviteBttn);
		remoteHostPnl.setToolTipText("panel for manual connection");
		remoteHostPnl.setBorder(new TitledBorder(null, "Remote Host: ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		
		remoteHostPnl.setLayout(new GridLayout(2, 1, 0, 0));
		ConnectTextField.setToolTipText("The IP address of the remote Compute Engine");
		ConnectTextField.setPreferredSize(new Dimension(100, 25));
		
		remoteHostPnl.add(ConnectTextField);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					v2mAdpt.login(ConnectTextField.getText());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		btnConnect.setToolTipText("Click to login to the remote Compute Engine");
		btnConnect.setText("Login");
		
		remoteHostPnl.add(btnConnect);
		scrollPane.setRowHeaderView(tabbedPane);
	}
	
	/**
	 * Start the view
	 */
	public void start() {
		setVisible(true);
	}
	
	/**
	 * Add the given component to the control panel,  then revalidating and packing the frame.
	 * @param comp The component to add
	 */
	public void addCtrlComponent(JComponent comp) {
		controlPanel.add(comp);  // Add the component to the control panel
		validate();  // re-runs the frame's layout manager to account for the newly added component 
		pack(); // resizes the frame and panels to make sure the newly added component is visible.  Note that this may adversely affect empty text displays without a preferred size setting.
	}
	
	/**
	 * Append the given message with a linefeed to the text area
	 * @param msg The message to display
	 */
	public void append(String msg) {
		taDisplay.append(msg+"\n");
	}

	public void addUser(TDropListItem2 user) {
		//UserList.addItem(user);
		UserList.insertItemAt(user, 0);
		UserList.setSelectedIndex(0);
	}

	public void addRoom(TDropListItem1 room) {
		ChatRoomList.insertItemAt(room, 0);
		ChatRoomList.setSelectedIndex(0);
	}

	public void removeUser(TDropListItem2 user) {
		UserList.removeItem(user);
	}

	public void removeRoom(TDropListItem1 room) {
		ChatRoomList.removeItem(room);
	}
	
	public Runnable addComponentFac(String name, Supplier<JComponent> supplier) {
		JComponent comp = supplier.get();
		tabbedPane.addTab(name, comp);
		return new Runnable() {
			@Override
			public void run() {
				tabbedPane.remove(comp);
			}
		};
		
	}
	
	
}