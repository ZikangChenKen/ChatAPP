package yc138_zc45.miniMVC.control;

import java.util.HashSet;
import java.util.function.Supplier;

import javax.swing.JComponent;

import common.network.messageReceiver.INamedNetworkMessageReceiver;
import common.room.datapacket.RoomDataPacket;
import common.room.message.ISendTextMessageData;
import common.room.messageReceiver.INamedRoomMessageReceiver;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import yc138_zc45.microMVC.model.IGameInitMsg;
import yc138_zc45.miniMVC.model.IBooleanMessageData;
import yc138_zc45.miniMVC.model.IMiniModel2ViewAdapter;
import yc138_zc45.miniMVC.model.MiniModel;
import yc138_zc45.miniMVC.view.IMiniView2ModelAdapter;
import yc138_zc45.miniMVC.view.MiniView;
import yc138_zc45.miniMVC.model.NameIDDyad;
import yc138_zc45.miniMVC.model.TextMessageData;

/**
 * Chat room controller.
 */
public class MiniController {
	
	/**
	 * The system logger to use. Change and/or customize this logger as desired.
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();
	
	/**
	 * The Room to App Adapter
	 */
	private IMini2MainAdapter mini2MainAdpt;
	
	/**
	 * The model of the Controller
	 */
    private MiniModel model;
    
    /**
     * The view of the Controller
     */
    private MiniView view;
    
    /**
     * network receiver
     */
    private INamedNetworkMessageReceiver networkReceiver;
    
    /**
     * Runnable object.
     */
    private Runnable startSubModelFn = () -> {
    	throw new IllegalStateException("[startSubModel] Neer set properly!");
    };
    
    /**
     * Constructor for a new room
     * @param roomName Room name
     * @param mini2MainAdpt mini to main adapter
     */
    public MiniController(String roomName, IMini2MainAdapter mini2MainAdpt, INamedNetworkMessageReceiver networkReceiver){
    	this.mini2MainAdpt = mini2MainAdpt;
    	ILoggerControl.getSharedLogger().log(LogLevel.INFO, "network receiver in Mini Control: " + networkReceiver);
    	this.networkReceiver = networkReceiver;
    	init(roomName);
    	this.startSubModelFn = () -> {
    		model.start();
    	};
    	
    }
    
    
    /**
     * Constructor for joining an exist room
     * @param roomInfo room name and id dyad
     * @param mini2MainAdpt mini to main adapter
     */
    public MiniController(NameIDDyad roomInfo, IMini2MainAdapter mini2MainAdpt, INamedNetworkMessageReceiver networkReceiver){
        this.mini2MainAdpt = mini2MainAdpt;
        ILoggerControl.getSharedLogger().log(LogLevel.INFO, "network receiver in Mini Control: " + networkReceiver);
        this.networkReceiver = networkReceiver;
        init(roomInfo.getName());
        this.startSubModelFn = () -> {
        	model.start(roomInfo.getID());
        };
        
        
        
    }
    
    
    /**
     * Initiate the mini model and view.
     * @param roomName Room name
     */
    private void init(String roomName) {
    	this.model = new MiniModel(mini2MainAdpt.getUserName(), roomName, mini2MainAdpt.getPubSubManger(), mini2MainAdpt.getConfig(),
    			new IMiniModel2ViewAdapter() {

					@Override
					public void displayMsg(String msg) {
						view.append(msg);
						
					}

					@Override
					public Runnable addComponent(Supplier<JComponent> componentSupplier, String label) {
						return view.addComponent(componentSupplier, label);
					}

					@Override
					public void displayNewMember(String member) {
						// TODO Auto-generated method stub
						view.addFriend(member);
					}
    		
    	}, sysLogger, mini2MainAdpt.getRMIURL(), networkReceiver);
    	this.view = new MiniView(new IMiniView2ModelAdapter() {

			@Override
			public void sendTextMessage(String msg) {
				model.sendMsg(new RoomDataPacket<ISendTextMessageData>(new TextMessageData(msg), model.getNamedRoomMsgReceiver()));
			}

			@Override
			public void leave() {
				quit();
			}

			@Override
			public void createMicroView() {
				/**
				 * TODO Figure out the something in here.
				 */
				model.sendMsg(new RoomDataPacket<IGameInitMsg>(IGameInitMsg.make("something"), model.getNamedRoomMsgReceiver()));
				
			}

			@Override
			public void sendBooleanMessage(String msg) {
				model.sendMsg(new RoomDataPacket<IBooleanMessageData>(IBooleanMessageData.make(msg), model.getNamedRoomMsgReceiver()));
			}

			@Override
			public void sendRandNumber(int rand) {
				// TODO Auto-generated method stub
				
				System.out.println("Want to save rand numberrrrrrrrrrrrrrrrrrrrrrrr");
				model.saveRandNumber(rand);
			}
    	});
    }
    
    /**
     * Quit the room
     */
    public void quit() {
    	model.leave();
    	mini2MainAdpt.exit(model.getNameIDDyad());
    }

	/**
	 * @return name & id dyad
	 */
	public NameIDDyad getNameIDDyad(){
		return model.getNameIDDyad();
	}
	
	/**
	 * @return view
	 */
	public JComponent start() {
		this.startSubModelFn.run();
		this.view.start();
		return view;
	}


}
