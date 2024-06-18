package yc138_zc45.microMVC.model;

import java.util.UUID;
import java.util.function.Supplier;

import javax.swing.JComponent;

import common.room.command.ARoomAlgoCmd;
import common.room.datapacket.RoomDataPacket;
import common.room.message.IRoomErrorMessage;
import common.room.message.ISendTextMessageData;
import provided.datapacket.IDataPacketID;
import provided.logger.ILogger;
import provided.mixedData.MixedDataKey;
import yc138_zc45.mainMVC.model.MainModel;
import yc138_zc45.microMVC.view.IMicroView2ModelAdapter;
import yc138_zc45.microMVC.view.MicroView;
import yc138_zc45.miniMVC.model.IMicroAdapter;

//APIDatapacket = type-narrowed datapacket for this API
//APIDatapacketAlgoCmd = type-narrowed datapacket visitor command for this API.
//IGameMicroAdapter = a game-specific adapter to the micro-MVC used by other game-related commands 
//                    in the SAME LOCAL client application.
/**
 * GameInitCmd class AKA "micro room controller"
 */
public class GameInitCmd extends ARoomAlgoCmd<IGameInitMsg> {
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 2680688587385183663L;
	
	/**
	 * game UUID
	 */
//	private UUID gameUUID;
	/**
	 * game model
	 */
	private transient MicroModel gameMicroModel;
	/**
	 * game view
	 */
	private transient MicroView gameMicroView;
	/**
	 * logger
	 */
	//private ILogger logger;
	
	/**
	 * url to be combined to full path of the image
	 */
	private String rmiURL;
	

	/**
	 * Constructor is called by the game server because that's who instantiates the game commands.
	 * @param gameUUID The UUID identifying the current instance of the game server.
	 */
	public GameInitCmd(String rmiURL) {//ILogger logger, String rmiURL) {
		//this.gameUUID = gameUUID;
		//this.logger = logger;

		this.rmiURL = rmiURL;
	}
	

	public Void apply(IDataPacketID index, RoomDataPacket<IGameInitMsg> host, Void... nu ) {
	   
		// The name and signature of the method may vary as it is defined by the API.
		
		getCmd2ModelAdpt().displayGUIComponentNonScrolled("My Game View",
			() -> {
				// This command is essentially the micro-controller, so there's 
				// no real need for an explicit named class for the micro-controller.
				// The typical controller's constructor code is just being written out here:
				
				// Instantiate the model and view components with their normal adapters.
				// The adapter code is elided here as it is game-dependent.  Use adapter names that fit the game implemenation.
				gameMicroModel = new MicroModel(new IMicroModel2ViewAdapter() {

					@Override
					public void sendGuessMsg(IGuessMessage msg) {
						//getCmd2ModelAdpt().sendMessageToRoom(msg);
						System.out.println("In game ctrler !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						System.out.println(getCmd2ModelAdpt() == null);
						System.out.println(msg.getMsg());
						getCmd2ModelAdpt().sendMessageToReceiver(msg, host.getSender());
						
					}

					@Override
					public void sendImageMsg(IImageMessage msg) {
						//TODO MAY CHANGE THIS RECEIVER TO DIRECT ROOM
						getCmd2ModelAdpt().sendMessageToRoom(msg);
					}

//					@Override
//					public void displayMsg(String msg) {
//						// TODO Auto-generated method stub
//						gameMicroView.displayMsg(msg);
//					}
//
//					@Override
//					public void addComponent(Supplier<JComponent> componentSupplier, String label) {
//						// TODO Auto-generated method stub
//						gameMicroView.addComponent(componentSupplier, label);
//					}
					
				});//, logger);
				gameMicroView = new MicroView(new IMicroView2ModelAdapter(){

					@Override
					public void sendGuessMessage(String msg) {
						System.out.println("in sending guess msg !!!!!!!!!!!!!!!!!!!????????");
						gameMicroModel.sendGuessMsg(IGuessMessage.make(msg));
					}

					@Override
					public void sendImageMessage(String image) {
						if (rmiURL == null) {
							getCmd2ModelAdpt().sendMessageToReceiver(IRoomErrorMessage.make("rmi URL is null in GameInitCmd", host), host.getSender());
						}
						String url = rmiURL + "yc138_zc45/microMVC/model/Images/" + image + ".jpeg";
						gameMicroModel.sendImageMsg(IImageMessage.make(url));
					}


					
				});
				
				// Start the model and view
				gameMicroModel.start();
				gameMicroView.start();
				

				IMicroAdapter microAdpt = new IMicroAdapter() {

					@Override
					public void addToMicroView(Supplier<JComponent> supplier) {
						gameMicroView.addImage(supplier);
					}

					@Override
					public void displayMsg(String msg) {
						// TODO Auto-generated method stub
						System.out.println("MSGGGGGGGG" + msg);
						gameMicroView.displayMsg(msg);
					}
				};
				
				System.out.println("Hereeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
				
				// Make the keys to the game adapters. Use an informative description string.

				System.out.println("The ID Put issssssss = " + MainModel.ID);
				MixedDataKey<IMicroAdapter> adapterKey = new MixedDataKey<IMicroAdapter>(MainModel.ID, "IMicroAdapter", IMicroAdapter.class);
				// Make a unique key for each adapter being stored. 
		
				// Store the adapter(s) in the local storage for other game-commands to retrieve.
				// The name and signature of the method may vary as it is defined by the API.
				getCmd2ModelAdpt().put(adapterKey, microAdpt);
				System.out.println("SHAAAAAABIIIIIIII         ~!!!!!!!!" + getCmd2ModelAdpt().get(adapterKey));
				
				
				System.out.println(getCmd2ModelAdpt().get(adapterKey) == null);
				
				// Add as many adapters to the local storage as needed, each with their own key.
				
				return gameMicroView;  // Return the micro-view so that it will be displayed on the client's UI.
			}
		);
		return null;
		
		
	}



}