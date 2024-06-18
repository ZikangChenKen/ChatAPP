package yc138_zc45.microMVC.model;

import java.util.UUID;

import common.room.command.ARoomAlgoCmd;
import common.room.datapacket.RoomDataPacket;
import common.room.message.IRoomErrorMessage;
import common.room.message.IRoomFailureMessage;
import provided.datapacket.IDataPacketID;
import provided.mixedData.MixedDataKey;
import yc138_zc45.mainMVC.model.MainModel;
import yc138_zc45.miniMVC.model.IMicroAdapter;

/**
 * Command for guess response.
 */
public class GuessResponseCmd extends ARoomAlgoCmd<IGuessResponseMessage> {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = -6776965467076935364L;
	


//    public GuessResponseCmd() {
//    	
//    }
    
	@Override
	public Void apply(IDataPacketID index, RoomDataPacket<IGuessResponseMessage> host, Void... params) {
		// TODO Auto-generated method stub
		System.out.println("In guess response apply");
		if (host.getData() == null) {
			getCmd2ModelAdpt().sendMessageToReceiver(IRoomErrorMessage.make("Data is null in GuessRespondCmd", host), host.getSender());
		}
        MixedDataKey<IMicroAdapter> adapterKey = new MixedDataKey<IMicroAdapter>(MainModel.ID, "IMicroAdapter", IMicroAdapter.class);
//		System.out.println("The game id issssssssssss: " + MainModel.ID);
        IMicroAdapter microAdpt = getCmd2ModelAdpt().get(adapterKey);
		if (microAdpt == null) {
			getCmd2ModelAdpt().sendMessageToReceiver(IRoomFailureMessage.make("Can't find the micro room because micro adapter is null", host), host.getSender());
		}
//		microAdpt.displayMsg(host.getData().getMsg() + "GUessResponseCmd");
		System.out.println("Displaying results on the screen maybe");
		microAdpt.displayMsg(host.getData().getMsg());
		
		return null;
	}

}
