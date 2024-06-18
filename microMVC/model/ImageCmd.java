package yc138_zc45.microMVC.model;

import java.net.URL;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import common.room.command.ARoomAlgoCmd;
import common.room.datapacket.RoomDataPacket;
import common.room.message.IRoomErrorMessage;
import common.room.message.IRoomFailureMessage;
import provided.datapacket.IDataPacketID;
import provided.mixedData.MixedDataKey;
import yc138_zc45.mainMVC.model.MainModel;
import yc138_zc45.miniMVC.model.IMicroAdapter;

/**
 * Command to process the image message
 */
public class ImageCmd extends ARoomAlgoCmd<IImageMessage>{
	
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = -1030098869946143557L;
	
//	
//	public ImageCmd() {
//	}

	@Override
	public Void apply(IDataPacketID index, RoomDataPacket<IImageMessage> host, Void... params) {
		if (host.getData() == null) {
			getCmd2ModelAdpt().sendMessageToReceiver(IRoomErrorMessage.make("Data is null in ImageCmd", host), host.getSender());
		}
		
		String urlStr = host.getData().getMsg();
		MixedDataKey<IMicroAdapter> adapterKkey = new MixedDataKey<IMicroAdapter>(MainModel.ID, "IMicroAdapter", IMicroAdapter.class);
		IMicroAdapter microAdpt = getCmd2ModelAdpt().get(adapterKkey);
		try {
		    ImageIcon imageIcon = new ImageIcon(new URL(urlStr));
		    JLabel imageLabel = new JLabel(imageIcon);
		    // Use the imageLabel as desired in the GUI.
		    microAdpt.addToMicroView(() -> {
				return imageLabel;
			});
		} catch(Exception e) {
			getCmd2ModelAdpt().sendMessageToReceiver(IRoomFailureMessage.make("Found remote exception in ImageCmd", host), host.getSender());
			//getCmd2ModelAdpt().sendMessageToReceiver(rdp, null);
		    e.printStackTrace();
		}
		
		return null;
	}

}
