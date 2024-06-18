package yc138_zc45.miniMVC.model;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.*;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JComponent;

import common.room.datapacket.RoomDataPacketAlgo;
import common.room.message.IRoomMessage;
import common.room.message.IRoomRejectMessage;
import common.room.message.ISendTextMessageData;
import common.room.datapacket.RoomDataPacket;
import common.network.messageReceiver.INamedNetworkMessageReceiver;
import common.room.command.ARoomAlgoCmd;
import common.room.command.ICmd2LocalAdapter;
import common.room.messageReceiver.INamedRoomMessageReceiver;
import common.room.messageReceiver.IRoomMessageReceiver;
import common.room.message.IRequestCmdMessage;
import common.room.message.IRoomErrorMessage;
import common.room.message.ICmdMessage;
import provided.datapacket.IDataPacketID;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.mixedData.MixedDataDictionary;
import provided.mixedData.MixedDataKey;
import provided.pubsubsync.IPubSubSyncChannelUpdate;
import provided.pubsubsync.IPubSubSyncData;
import provided.pubsubsync.IPubSubSyncManager;
import provided.pubsubsync.IPubSubSyncUpdater;
import provided.rmiUtils.RMIPortConfigWithBoundName;
import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;
import yc138_zc45.microMVC.model.GuessGameCmd;
import yc138_zc45.microMVC.model.GuessResponseCmd;
import yc138_zc45.microMVC.model.GameInitCmd;
import yc138_zc45.microMVC.model.IGameInitMsg;
import yc138_zc45.microMVC.model.IGuessMessage;
import yc138_zc45.microMVC.model.IGuessResponseMessage;
import yc138_zc45.microMVC.model.IImageMessage;
import yc138_zc45.microMVC.model.ImageCmd;


/**
 * The model for the room
 */
public class MiniModel {
	/**
	 * User name to use
	 */
	private String userName;
	
	/**
	 * Room name
	 */
	private String roomName;
	
	/**
	 * Channel id
	 */
	private UUID channelID;
	
	/**
	 * Pubsub manager
	 */
	private IPubSubSyncManager pubManager;
	
	/**
	 * Model to view adapter
	 */
	private IMiniModel2ViewAdapter m2vAdpt;
	
	/**
	 * Room info 
	 */
	private NameIDDyad nameIdDyad;
	
	/**
	 * Current config
	 */
	private RMIPortConfigWithBoundName currentConfig;
	
	/**
	 * The logger.
	 */
	private ILogger logger;

	/**
	 * The message receiver.
	 */
	private IRoomMessageReceiver msgReceiver;

	/**
	 * The message receiver stub.
	 */
	private IRoomMessageReceiver msgReceiverStub;

	/**
	 * The named message receiver dyad.
	 */
	private INamedRoomMessageReceiver namedMsgReceiver;
	
	/**
	 * Local roster
	 */
	private HashSet<INamedRoomMessageReceiver> roster = new HashSet<>();
	
	/**
	 * Pubsub channel 
	 */
	private IPubSubSyncChannelUpdate<HashSet<INamedRoomMessageReceiver>> rosterUpdate;
	
	/**
	 * The cache.
	 */
	private List<RoomDataPacket<?>> cache = new ArrayList<>();
	
	/**
	 * RMI URL.
	 */
	private String rmiURL;
	
	//TODO: Need changing
	private MixedDataDictionary gameData = new MixedDataDictionary();
	
	/**
	 * Network receiver.
	 */
	private INamedNetworkMessageReceiver networkReceiver;
	
	/**
	 * Randomizer.
	 */
	private static IRandomizer rand = Randomizer.Singleton;
	//int correct = rand.randomInt(1, 20);
	//System.out.println("The correct number is " +correct);
	//this.correct = rand.randomInt(1, 20);
	
	/**
	 * Correct ans
	 */
	private static final int correct = rand.randomInt(1, 20);
	
	//private UUID gameID;

	/**
	 * Data packet algo to process room-level data
	 */
	private RoomDataPacketAlgo roomAlgo = new RoomDataPacketAlgo(new ARoomAlgoCmd<>() {

		private static final long serialVersionUID = 4799262612840834677L;

		@Override
		public Void apply(IDataPacketID index, RoomDataPacket<IRoomMessage> host, Void... params) {
			try {
				cache.add(host);
				host.getSender().getRoomConnectionStub().receiveMessage(new RoomDataPacket<IRequestCmdMessage>(new RequestCmdMessage(host.getData().getID()), namedMsgReceiver));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	});
	
	/**
	 * Command to local adapter.
	 */
	private ICmd2LocalAdapter cmd2LocalAdpt = new ICmd2LocalAdapter() {
		@Override
		public void displayTextMessage(String message, String senderName) {
			MiniModel.this.m2vAdpt.displayMsg(message);
		}

		@Override
		public Runnable displayGUIComponentNonScrolled(String label, Supplier<JComponent> compFac) {
			// TODO Auto-generated method stub
			return MiniModel.this.m2vAdpt.addComponent(compFac, label);
		}

		@Override
		public Runnable displayGUIComponentScrolled(String label, Supplier<JComponent> compFac) {
			// TODO Auto-generated method stub
			return MiniModel.this.m2vAdpt.addComponent(compFac, label);
		}

		@Override
		public void log(LogLevel level, String logMessage) {
			// TODO Auto-generated method stub
			MiniModel.this.logger.log(level, logMessage);
		}

		@Override
		public String getRoomReceiver() {
			// TODO Auto-generated method stub
			return MiniModel.this.userName;
		}

		@Override
		public String getRoomName() {
			// TODO Auto-generated method stub
			return MiniModel.this.roomName;
		}

		@Override
		public <T> void put(MixedDataKey<T> key, T value) {
			// TODO Auto-generated method stub
			MiniModel.this.gameData.put(key, value);
			
		}

		@Override
		public <T> T get(MixedDataKey<T> key) {
			// TODO Auto-generated method stub
			return MiniModel.this.gameData.get(key);
		}

		@Override
		public void sendMessageToRoom(IRoomMessage message) {
			// TODO Auto-generated method stub
			try {
				for (INamedRoomMessageReceiver other: MiniModel.this.roster) {
					System.out.println("Try to send something to room roster " + other);
					System.out.println("Sending: " + message);
					other.getRoomConnectionStub().receiveMessage(new RoomDataPacket<IRoomMessage>(message, MiniModel.this.namedMsgReceiver));
				}
				
				//MiniModel.this
			} catch (RemoteException e) {
				
				MiniModel.this.logger.log(LogLevel.ERROR, "Fail to send message");
				e.printStackTrace();
			}
			
		}

		@Override
		public void sendMessageToReceiver(IRoomMessage message, INamedRoomMessageReceiver receiver) {
			// TODO Auto-generated method stub
			try {
				receiver.receiveMessage(new RoomDataPacket<IRoomMessage>(message, MiniModel.this.namedMsgReceiver));
				
				//MiniModel.this
			} catch (RemoteException e) {
				
				MiniModel.this.logger.log(LogLevel.ERROR, "Fail to send message to " + receiver.getName());
				e.printStackTrace();
			}
			
		}

		@Override
		public HashSet<INamedRoomMessageReceiver> getRoomReceivers() {
			// TODO Auto-generated method stub
			return MiniModel.this.roster;
		}



//		@Override
//		public void displayNumber(Integer number) {
//			MiniModel.this.m2vAdpt.displayMsg(String.valueOf(number));
//		}
//
//		@Override
//		public void displayBoolean(String bool) {
//			ILoggerControl.getSharedLogger().log(LogLevel.ERROR, "Received request to display a message");
//			MiniModel.this.m2vAdpt.displayMsg(bool);
//		}


		
	};

	/**
	 * Constructor for the model.
	 * @param userName
	 * @param roomName
	 * @param pubManager
	 * @param currentConfig
	 * @param m2vAdpt
	 */
	public MiniModel(String userName, String roomName, IPubSubSyncManager pubManager, RMIPortConfigWithBoundName currentConfig, IMiniModel2ViewAdapter m2vAdpt, ILogger logger, String rmiURL, INamedNetworkMessageReceiver networkReceiver) {
		this.userName = userName;
		this.roomName = roomName;
		this.pubManager = pubManager;
		this.m2vAdpt = m2vAdpt;
		this.currentConfig = currentConfig;
		this.logger = logger;
		this.rmiURL = rmiURL;
		ILoggerControl.getSharedLogger().log(LogLevel.INFO, "network receiver in Mini Modellllllllllllllllllllllll: " + networkReceiver);
		this.networkReceiver = networkReceiver;

		this.setMsgReceiver();
	}
	
	/**
	 * Set the message receiver and correlated dyad.
	 */
	public void setMsgReceiver() {
		this.msgReceiver = new RoomMessageReceiver(this.roomAlgo);
		try {
			this.msgReceiverStub = (IRoomMessageReceiver) UnicastRemoteObject.exportObject(this.msgReceiver, this.currentConfig.stubPort);
			logger.log(LogLevel.INFO, "Other channel got the local stub: " + this.namedMsgReceiver);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			leave();
		} 
		ILoggerControl.getSharedLogger().log(LogLevel.INFO, "network receiver in Mini Model new NamedRMRrrrrrrrrrrrrrrrrrrrrrr: " + this.networkReceiver);
		this.namedMsgReceiver = new NamedRoomMessageReceiver(this.userName, this.msgReceiverStub, this.channelID, this.networkReceiver);
	}
	
	/**
	 * Leave the room
	 */
	public void leave() {
		rosterUpdate.update(IPubSubSyncUpdater.makeSetRemoveFn(this.namedMsgReceiver));
		rosterUpdate.unsubscribe();
	}
	
	/**
	 * @return
	 */
	public NameIDDyad getNameIDDyad() {
		return this.nameIdDyad;
	}
	
	/**
	 * Do initialization step for create channel.
	 */
	public void start() {
		logger.log(LogLevel.INFO, "start mini-model");
		
		
		this.rosterUpdate = pubManager.createChannel(this.roomName, roster, new Consumer<IPubSubSyncData<HashSet<INamedRoomMessageReceiver>>>(){
			
			@Override
			public void accept(IPubSubSyncData<HashSet<INamedRoomMessageReceiver>> t) {
				MiniModel.this.roster = t.getData();
				
			}
			
		}, new Consumer<String>(){

			@Override
			public void accept(String t) {
				
			}
		});
		this.rosterUpdate.update(IPubSubSyncUpdater.makeSetAddFn(this.namedMsgReceiver));
		
		MiniModel.this.m2vAdpt.displayMsg(roomName + " is created!");
		

		this.channelID = rosterUpdate.getChannelID();
		this.nameIdDyad = new NameIDDyad(this.channelID, this.roomName);
		
		initAlgo();

	}
	
	/**
	 * @param roomID channel id
	 */
	public void start(UUID roomID) {
		
		logger.log(LogLevel.INFO, "start mini-model");
		
		
		
		this.rosterUpdate = this.pubManager.subscribeToUpdateChannel(roomID, new Consumer<IPubSubSyncData<HashSet<INamedRoomMessageReceiver>>>(){
			
			@Override
			public void accept(IPubSubSyncData<HashSet<INamedRoomMessageReceiver>> t) {
				MiniModel.this.roster = t.getData();
				// update the chatapp view to reflect the new roster				
				MiniModel.this.m2vAdpt.displayMsg("Subscribe to channel: " + roomID);
				
			}	
			
		}, new Consumer<String>(){

			@Override
			public void accept(String t) {
				
			}
		});
		
		
	
		
		this.channelID = roomID;
		this.nameIdDyad = new NameIDDyad(this.channelID, this.roomName);
		
		initAlgo();
		this.rosterUpdate.update(IPubSubSyncUpdater.makeSetAddFn(this.namedMsgReceiver));

	}
	
	/**
	 * Initializa the algo.
	 */
	public void initAlgo() {
		System.out.println("running initAlgo()");
		this.roomAlgo.setCmd(IRequestCmdMessage.GetID(), new ARoomAlgoCmd<IRequestCmdMessage>(){

			private static final long serialVersionUID = 1074413064726924497L;
			
			@Override
			public Void apply(IDataPacketID index, RoomDataPacket<IRequestCmdMessage> host, Void... params) {
				if (host.getData() == null) {
					RoomDataPacket<IRoomErrorMessage> rdp = new RoomDataPacket<>(IRoomErrorMessage.make("This is a null message", host), namedMsgReceiver);
					try {
						host.getSender().receiveMessage(rdp);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				
				IDataPacketID id = host.getData().getMsgID();
				
				if (id == ISendTextMessageData.GetID()) {
					RoomDataPacket<IRoomRejectMessage> rdp = new RoomDataPacket<>(IRoomRejectMessage.make("This is a well-known message type", host), namedMsgReceiver);
					try {
						host.getSender().receiveMessage(rdp);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				
				try {
					logger.log(LogLevel.INFO, "Hereeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
					
					CmdMessage cmdM = new CmdMessage((ARoomAlgoCmd<?>) roomAlgo.getCmd(id), id);
					logger.log(LogLevel.INFO, "wwwwwwwwwwwwwwwwwwwww");
					RoomDataPacket<ICmdMessage> rdp = new RoomDataPacket<>(cmdM, namedMsgReceiver);
					logger.log(LogLevel.INFO, "uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu");
					logger.log(LogLevel.INFO, "sender: " + host.getSender());
					logger.log(LogLevel.INFO, "stub: " + host.getSender().getRoomConnectionStub());
					host.getSender().getRoomConnectionStub().receiveMessage(rdp);
					logger.log(LogLevel.INFO, "qqqqqqqqqqqqqqqqqqqqqqqqqqqq");
					
					logger.log(LogLevel.INFO, "YEAHHHHHHHHHHHHHHHHHHHHHHH");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				return null;
		}});

		this.roomAlgo.setCmd(ICmdMessage.GetID(), new ARoomAlgoCmd<ICmdMessage>(){

			private static final long serialVersionUID = 1074413064726924497L;

			@Override
			public Void apply(IDataPacketID index, RoomDataPacket<ICmdMessage> host, Void... params) {
				IDataPacketID id = host.getData().getMsgID();
				ARoomAlgoCmd<?> cmd = host.getData().getMessageCmd();
				cmd.setCmd2ModelAdpt(cmd2LocalAdpt);
				roomAlgo.setCmd(id, cmd);
				for (RoomDataPacket<?> msg : cache) {
					if (msg.getData().getID().equals(id)) {
						msg.execute(roomAlgo);
					}
				}
				return null;
		}});

		this.roomAlgo.setCmd(ISendTextMessageData.GetID(), new ARoomAlgoCmd<ISendTextMessageData>() {

			private static final long serialVersionUID = -6988234857828117450L;

			@Override
			public Void apply(IDataPacketID index, RoomDataPacket<ISendTextMessageData> host, Void... params) {
				String msg = host.getData().getText();
				String res = host.getSender().toString() + ": " + msg;
				MiniModel.this.m2vAdpt.displayMsg(res);
				return null;
			}
				
		});

//		var cmdBool = new BoolAlgoCmd();
//		cmdBool.setCmd2ModelAdpt(cmd2LocalAdpt);
//		ILoggerControl.getSharedLogger().log(LogLevel.ERROR, "cmd2LocalAdpt: " + cmd2LocalAdpt);
//		this.roomAlgo.setCmd(IBooleanMessageData.GetID(), cmd);	
		//gameID = UUID.randomUUID();
		
		var cmdMicro = new GameInitCmd(MiniModel.this.rmiURL);//MiniModel.this.logger, this.rmiURL);
		cmdMicro.setCmd2ModelAdpt(cmd2LocalAdpt);
		roomAlgo.setCmd(IGameInitMsg.GetID(), cmdMicro);
		
		var cmdImage = new ImageCmd();
		cmdImage.setCmd2ModelAdpt(cmd2LocalAdpt);
		roomAlgo.setCmd(IImageMessage.GetID(), cmdImage);
		

		//System.out.println("In mini model, the correct number is: " + this.correct);
		var cmdGame = new GuessGameCmd();
		cmdGame.setCmd2ModelAdpt(cmd2LocalAdpt);
		roomAlgo.setCmd(IGuessMessage.GetID(), cmdGame);
		
		
		var cmdGuessRespond = new GuessResponseCmd();
		cmdGuessRespond.setCmd2ModelAdpt(cmd2LocalAdpt);
		roomAlgo.setCmd(IGuessResponseMessage.GetID(), cmdGuessRespond);

	}
	
	/**
	 * Do the send message.
	 * @param msg
	 */
	public void sendMsg(RoomDataPacket<? extends IRoomMessage> msg) {
		for (INamedRoomMessageReceiver msgReceiver: this.roster) {
			try{
				
				msgReceiver.getRoomConnectionStub().receiveMessage(msg);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 * Get the named room message receiver.
	 * @return
	 */
	public INamedRoomMessageReceiver getNamedRoomMsgReceiver() {
		return this.namedMsgReceiver;
	}
	
	/**
	 * Save the random number.
	 * @param rand
	 */
	public void saveRandNumber(int rand) {
		// TODO Auto-generated method stub
//		this.correct = rand;
//		
//		
//		
//		System.out.println("In mini model saveRandNumber, correct number is: " + correct);
	}
}
