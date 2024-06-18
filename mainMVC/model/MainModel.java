package yc138_zc45.mainMVC.model;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.UUID;

import common.network.datapacket.NetworkDataPacket;
import common.network.datapacket.NetworkDataPacketAlgo;
import common.network.message.IForcedRoomInviteData;
import common.network.message.INetworkErrorMessage;
import common.network.message.INetworkFailureMessage;
import common.network.message.INetworkMessage;
import common.network.message.INetworkRejectMessage;
import common.network.message.IQuitMessage;
import common.network.message.ISendNetworkData;
//import common.network.message.Impl.ConnectionMessage;
import common.network.command.ANetworkAlgoCmd;
import common.network.messageReceiver.INetworkMessageReceiver;
import common.room.datapacket.RoomDataPacket;
import common.room.message.IRoomErrorMessage;
import common.room.message.IRoomFailureMessage;
import common.room.messageReceiver.INamedRoomMessageReceiver;
import common.network.messageReceiver.IInitialConnection;
import common.network.messageReceiver.INamedNetworkMessageReceiver;
import provided.datapacket.IDataPacketID;
import provided.logger.ILogEntry;
import provided.logger.ILogEntryFormatter;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.pubsubsync.IPubSubSyncConnection;
import provided.pubsubsync.IPubSubSyncManager;
//import provided.remoteCompute.compute.IRemoteTaskViewAdapter;
//import provided.remoteCompute.compute.IRemoteTaskViewAdapter;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.RMIPortConfigWithBoundName;
import provided.rmiUtils.RMIUtils;
import yc138_zc45.miniMVC.control.IMini2MainAdapter;
import yc138_zc45.miniMVC.control.MiniController;
import yc138_zc45.miniMVC.model.NameIDDyad;

/**
 * The main model for the app
 * @author swong
 *
 */
public class MainModel {
	public static final UUID ID = UUID.randomUUID();
	
	/**
	 * The system logger in use
	 */
	private ILogger sysLogger;
	
	/**
	 * A logger that logs to the view and the system logger
	 */
	private ILogger viewLogger;
	
	/**
	 * The IRMIUtils in use
	 */
	private IRMIUtils rmiUtils;
	
	/**
	 * The adapter to the view
	 */
	private IMainModel2ViewAdapter m2vAdpt;

	/**
	 * The currently selected app config to use
	 */
	private RMIPortConfigWithBoundName currentConfig;
	
	/**
	 * Local registry.
	 */
	private Registry registry;
	
	/**
	 * connect stub
	 */
	private INetworkMessageReceiver connectStub;
	
	/**
	 * The name to use
	 */
	private String userName;
	
	/**
	 * Application B
	 */
	private IInitialConnection otherAppConnectionStub;
	
	/**
	 * 
	 */
	private INetworkMessageReceiver localStub;
	
	/**
	 * name-connection dyad.
	 */
	private INamedNetworkMessageReceiver nameConnection; 
	
	/**
	 * Set of connected stubs 
	 */
	private HashSet<INamedNetworkMessageReceiver> knownConnections = new HashSet<>();
	
	/**
	 * Room set.
	 */
	private HashSet<IMain2MiniAdapter> roomSet = new HashSet<>();
	
	
	/**
	 * manager
	 */
	private IPubSubSyncManager manager;
	
	private HashSet<UUID> boyNextDoor = new HashSet<>();
	
	
	/**
	 * Application A
	 */
	private IInitialConnection initialConectionStub = new IInitialConnection() {
		@Override
		public void receiveNetworkDyads(HashSet<INamedNetworkMessageReceiver> namedConnections) throws RemoteException {
			HashSet<INamedNetworkMessageReceiver> extended = new HashSet<>(knownConnections);

			knownConnections.addAll(namedConnections);
			for (INamedNetworkMessageReceiver user : knownConnections) {
				if (!extended.contains(user)) {
					m2vAdpt.addConnection(user);
					user.getNetworkStub().receiveMessage(new NetworkDataPacket<ISendNetworkData>(new SendNetworkData(extended), nameConnection));
				}
			}

		}
	};
	

	
	/**
	 * Data packet algo to process app-level data
	 */
	private NetworkDataPacketAlgo appAlgo = new NetworkDataPacketAlgo(new ANetworkAlgoCmd<> (){

		private static final long serialVersionUID = 780524591219133867L;

		@Override
		public Void apply(IDataPacketID index, NetworkDataPacket<INetworkMessage> host, Void... params) {
			return null;
		}
		
	});

	/**
	 * Construct the model
	 * @param logger The system logger
	 * @param currentConfig The current app config
	 * @param m2vAdpt The adapter to the view
	 */
	public MainModel(ILogger logger, RMIPortConfigWithBoundName currentConfig, IMainModel2ViewAdapter m2vAdpt) {
		
		this.sysLogger = logger;
		this.currentConfig = currentConfig;
		this.m2vAdpt = m2vAdpt;
		rmiUtils = new RMIUtils(logger);
		// Make a logger that logs to the view
		viewLogger = ILoggerControl.makeLogger(new ILogEntryProcessor(){
			ILogEntryFormatter formatter = ILogEntryFormatter.MakeFormatter("[%1s] %2s");   // custom log entry formatting  "[level] msg"
			@Override
			public void accept(ILogEntry logEntry) {
				MainModel.this.m2vAdpt.displayMsg(formatter.apply(logEntry));  // plain "this" refers to the ILogEntryProcessor!
			}
			
		}, LogLevel.INFO);
		
		
		viewLogger.append(sysLogger);  // Chain the system logger to the end of the view logger so that anything logged to the view also goes to the system log (default = to console).
		
	}
	
	/**
	 * Get the internal IRMIUtils instance being used.    The discovery model start method needs the main model's IRMIUtils.
	 * ONLY call the method AFTER the model, i.e. the internal IRMIUtils, has been started!
	 * @return The internal IRMIUtils instance
	 */
	public IRMIUtils getRMIUtils() {
		return this.rmiUtils;
	}
	
	/**
	 * Login with the supplied userName.
	 * @param userName
	 */
	public void login(String userName) {
		this.userName = userName;
		this.initAlgo();
		this.initialize();
		viewLogger.log(LogLevel.INFO, "Client ready, using app config: "+currentConfig);
		viewLogger.log(LogLevel.INFO, "User Name: "+ this.userName);
		
	}

	/**
	 * Process the newly acquired stub.  This is the method that the discovery model uses in "Client" or "Client + Server" usage modes 
	 * @param stub stub
	 * @param computeStub The newly acquired stub 
	 */
	public void connectToStub(IInitialConnection stub) { 
		try {
			//TODO send knownConnections to other through dataPacket.
			//stub.receiveNamedConnections(this.knownConnections);
			stub.receiveNetworkDyads(knownConnections);
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start the model
	 */
	public void start() {
		rmiUtils.startRMI(currentConfig.classServerPort);  // Start the RMI system using an appropriate class file server port

		
	}
	
	/**
	 * Stop the RMI system and quit the app with the given exit code.
	 * @param exitCode Exit code: 0 = normal, -1 = error 
	 */
	public void quit(int exitCode) {
		// Quit all rooms
		
		System.out.println("In quit methoddddddddddddddddddddddddddddddddd");
		for (IMain2MiniAdapter room : this.roomSet) {
			room.quit();
		}
		
		// Disconnect all users
		for (var friend: this.knownConnections) {
			try {
				System.out.println("0000000000000000000000000000000000000000000000");
				//Concrete quitMessage class?
				friend.receiveMessage(new NetworkDataPacket<IQuitMessage>(IQuitMessage.make(friend, friend.getName() + ": Quit successfully!"), this.nameConnection));
				System.out.println("1111111111111111111111111111111111");
			} catch (RemoteException e) {
				try {
					friend.receiveMessage(new NetworkDataPacket<INetworkFailureMessage>(INetworkFailureMessage.make("Fail to send quit message to " + friend.getName(), null), this.nameConnection ));
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				viewLogger.log(LogLevel.ERROR, "Fail to send quit message to other friend");
				e.printStackTrace();
			}
			
		}
		
		rmiUtils.stopRMI();
		System.exit(exitCode);
	}
	
	/**
	 * Initialize the fields.
	 */
	public void initialize() {
		try {
			
			
			
			this.registry = this.rmiUtils.getLocalRegistry();
			viewLogger.log(LogLevel.INFO, "Get the local registry: " + this.registry);
			this.localStub = new Network(this.appAlgo);
			this.connectStub = (INetworkMessageReceiver) UnicastRemoteObject.exportObject(this.localStub, currentConfig.stubPort);
			viewLogger.log(LogLevel.INFO, "Succeed connecting to other App: " + this.connectStub);
			
			
			this.nameConnection = new NamedNetworkMessageReceiver(this.userName, this.connectStub);
			
			this.knownConnections.add(nameConnection);
			this.otherAppConnectionStub = (IInitialConnection) UnicastRemoteObject.exportObject(this.initialConectionStub, currentConfig.stubPort);
			viewLogger.log(LogLevel.INFO, "Other App got the local stub: " + this.otherAppConnectionStub);
			this.registry.rebind(this.currentConfig.boundName, this.otherAppConnectionStub);
			this.manager = IPubSubSyncConnection.getPubSubSyncManager(sysLogger, rmiUtils, currentConfig.stubPort);
		} catch (Exception e) {
			viewLogger.log(LogLevel.ERROR, "Fail to get local registry");
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * create the new chat room based on the name given.
	 * @param name the name of the new chat room.
	 */
	public void createRoom(String name) {
		viewLogger.log(LogLevel.INFO, "Create a new room: " + name);
		Runnable[] cmd = new Runnable[1];
		
		MiniController newRoom = new MiniController(name, new IMini2MainAdapter () {

			@Override
			public void exit(NameIDDyad roomInfo) {
				// TODO Auto-generated method stub
				m2vAdpt.removeRoom(roomInfo);
				cmd[0].run();
			}

			@Override
			public String getUserName() {
				// TODO Auto-generated method stub
				return userName;
			}

			@Override
			public RMIPortConfigWithBoundName getConfig() {
				// TODO Auto-generated method stub
				return currentConfig;
			}

			@Override
			public IPubSubSyncManager getPubSubManger() {
				// TODO Auto-generated method stub
				return manager;
			}

			@Override
			public String getRMIURL() {
				return rmiUtils.getClassFileServerURL();
			}
			
		}, nameConnection);
		
		IMain2MiniAdapter main2MiniAdpt = new IMain2MiniAdapter() {

			@Override
			public void quit() {
				newRoom.quit();
			}

			
		};
		
		cmd[0] = m2vAdpt.addComponent(name, newRoom.start());
		m2vAdpt.addRoom(newRoom.getNameIDDyad());
		roomSet.add(main2MiniAdpt);
		boyNextDoor.add(newRoom.getNameIDDyad().getID());
		
		System.out.println("Room name: " + newRoom.getNameIDDyad().getName());
		System.out.println("Room ID: " + newRoom.getNameIDDyad().getID());
		
		
		
		
		
		viewLogger.log(LogLevel.INFO, "Succeed creating room: " + name);
	}
	
	/**
	 * Initialize the algo.
	 */
	private void initAlgo() {
		this.appAlgo.setCmd(ISendNetworkData.GetID(), new ANetworkAlgoCmd<ISendNetworkData>() {
			
			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -1268112097786018697L;

			@Override
			public Void apply(IDataPacketID index, NetworkDataPacket<ISendNetworkData> host, Void... params) {
				// TODO Auto-generated method stub
				ISendNetworkData data = host.getData();
				HashSet<INamedNetworkMessageReceiver> friends = data.getFriends();
				HashSet<INamedNetworkMessageReceiver> extended = new HashSet<>(knownConnections);
				viewLogger.log(LogLevel.INFO, "size of knownConnection: " + knownConnections.size());
				//extended.addAll(friends);
				knownConnections.addAll(friends);
				viewLogger.log(LogLevel.INFO, "size of extended: " + extended.size());
				for (INamedNetworkMessageReceiver user : knownConnections) {
					//viewLogger.log(LogLevel.INFO, "User: " + user.getName());
					viewLogger.log(LogLevel.INFO, "User: " + user.toString());
					if (!extended.contains(user)) {
						
						m2vAdpt.addConnection(user);
						viewLogger.log(LogLevel.INFO, "Successfully add connection to user");
						SendNetworkData extendedMessage = new SendNetworkData(extended);
						viewLogger.log(LogLevel.INFO, "Get extended Message: " + extendedMessage.toString());
						//ConnectionDataPacket<ISendConnectionData> extendedData = new ConnectionDataPacket<>(extendedMessage);
						try {
							//viewLogger.log(LogLevel.INFO, "hhhhhh: ");
							user.getNetworkStub().receiveMessage(
									new NetworkDataPacket<ISendNetworkData>(extendedMessage, nameConnection));
							//viewLogger.log(LogLevel.INFO, "?????????: ");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							try {
								user.getNetworkStub().receiveMessage(new NetworkDataPacket<INetworkErrorMessage>(INetworkErrorMessage.make("Error occured during connection", host), nameConnection));
							} catch (RemoteException el) {
								el.printStackTrace();
							}
						}
					}
				}
				//knownConnections = extended;
				
				return null;
			}
			
		});

		this.appAlgo.setCmd(IQuitMessage.GetID(), new ANetworkAlgoCmd<IQuitMessage>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8779060432268006537L;

			@Override
			public Void apply(IDataPacketID index, NetworkDataPacket<IQuitMessage> host, Void... params) {
				System.out.println("Get in the Quit Cmdddddddddddddddddddddddddddddddddddddd");
				if (host.getData() == null) {
					try {
						host.getSender().getNetworkStub().receiveMessage(new NetworkDataPacket<INetworkErrorMessage>(INetworkErrorMessage.make("Fail to send quit message to " + host.getSender().getName(), null), MainModel.this.nameConnection ));
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					knownConnections.remove(host.getSender());
					m2vAdpt.removeConnection(host.getSender());
					m2vAdpt.displayMsg(host.getData().getDescription());
				}
				
				return null;
			}
		});

		this.appAlgo.setCmd(INetworkErrorMessage.GetID(), new ANetworkAlgoCmd<INetworkErrorMessage>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8779060432268006537L;

			@Override
			public Void apply(IDataPacketID index, NetworkDataPacket<INetworkErrorMessage> host, Void... params) {
				m2vAdpt.displayMsg(host.getData().getDescription());
				return null;
			}
		});

		this.appAlgo.setCmd(INetworkFailureMessage.GetID(), new ANetworkAlgoCmd<INetworkFailureMessage>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8779060432268006537L;

			@Override
			public Void apply(IDataPacketID index, NetworkDataPacket<INetworkFailureMessage> host, Void... params) {
				m2vAdpt.displayMsg(host.getData().getDescription());
				return null;
			}
		});

		this.appAlgo.setCmd(INetworkRejectMessage.GetID(), new ANetworkAlgoCmd<INetworkRejectMessage>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8779060432268006537L;

			@Override
			public Void apply(IDataPacketID index, NetworkDataPacket<INetworkRejectMessage> host, Void... params) {
				m2vAdpt.displayMsg(host.getData().getDescription());
				return null;
			}
		});

		this.appAlgo.setCmd(IForcedRoomInviteData.GetID(), new ANetworkAlgoCmd<IForcedRoomInviteData>() {
			
			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -1268112097786018697L;

			@Override
			public Void apply(IDataPacketID index, NetworkDataPacket<IForcedRoomInviteData> host, Void... params) {
				if (host.getData() == null) {
					try {
						host.getSender().receiveMessage(new NetworkDataPacket<INetworkErrorMessage>(INetworkErrorMessage.make("The invitation message is null", host), MainModel.this.nameConnection));
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//INetworkErrorMessage.make("The invitation message is null", host)
				}
				//String roomName = host.getData().getFriendlyName();
				String roomName = host.getData().getDyad().getName();
				//UUID roomID = host.getData().getUUID();
				UUID roomID = host.getData().getDyad().getID();
				
			
				
				Runnable[] cmd = new Runnable[1];
				
				MiniController newRoom = new MiniController(new NameIDDyad(roomID, roomName), new IMini2MainAdapter () {

					@Override
					public void exit(NameIDDyad roomInfo) {
						// TODO Auto-generated method stub
						m2vAdpt.removeRoom(roomInfo);
						cmd[0].run();
					}

					@Override
					public String getUserName() {
						// TODO Auto-generated method stub
						return userName;
					}

					@Override
					public RMIPortConfigWithBoundName getConfig() {
						// TODO Auto-generated method stub
						return currentConfig;
					}

					@Override
					public IPubSubSyncManager getPubSubManger() {
						// TODO Auto-generated method stub
						return manager;
					}
					
					@Override
					public String getRMIURL() {
						return rmiUtils.getClassFileServerURL();
					}
					
				}, nameConnection);
				
//				IMain2MiniAdapter main2MiniAdpt = new IMain2MiniAdapter() {
//
//					@Override
//					public void quit() {
//						newRoom.quit();
//					}
//
//					
//				};
				System.out.println("The room id wanna join is: " + roomID);
				//TODO: 
				if (!boyNextDoor.contains(roomID)) {
					cmd[0] = m2vAdpt.addComponent(roomName, newRoom.start());
					m2vAdpt.addRoom(newRoom.getNameIDDyad());
					boyNextDoor.add(roomID);
					//roomSet.add(main2MiniAdpt);					
				} else {
					try {
						host.getSender().receiveMessage(new NetworkDataPacket<INetworkRejectMessage>(INetworkRejectMessage.make("Room: " + newRoom.toString() + " has already existed!", host), MainModel.this.nameConnection));
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
				
			}
			
		});
		
	}

	/**
	 * Invite a selected user to the selected chat room
	 * @param room room dyad
	 * @param user user being invited
	 * @throws RemoteException e
	 */
	public void invite(NameIDDyad room, INamedNetworkMessageReceiver user) throws RemoteException {
		user.getNetworkStub().receiveMessage(new NetworkDataPacket<IForcedRoomInviteData>(new ForcedRoomInviteData(room.getID(), room.getName()), this.nameConnection));
	}
}
