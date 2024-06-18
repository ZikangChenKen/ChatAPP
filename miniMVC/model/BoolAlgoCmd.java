package yc138_zc45.miniMVC.model;

import common.room.datapacket.RoomDataPacket;
import common.room.command.ARoomAlgoCmd;
import common.room.command.ICmd2LocalAdapter;
import provided.datapacket.IDataPacketID;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

/**
 * Boolean command algo class
 */
public class BoolAlgoCmd extends ARoomAlgoCmd<IBooleanMessageData>{

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -5625281098571770215L;
	
	public BoolAlgoCmd() {
		
	}

	@Override
	public Void apply(IDataPacketID index, RoomDataPacket<IBooleanMessageData> host, Void... params) {
		String msg = host.getData().getMsg();
		int value;
		
		ILoggerControl.getSharedLogger().log(LogLevel.INFO, "Message: " + msg + this.getCmd2ModelAdpt());
		
		try {
			value = Integer.parseInt(msg);
			String res = host.getSender().toString() + ": false";
			if (value == 1) {
				res = host.getSender().toString() + ": true";
			} else if (value != 0) {
				res = value + " is not a valid integer";
			}
			//this.getCmd2ModelAdpt().displayBoolean(res);
			
		} catch(Exception e) {
			//this.getCmd2ModelAdpt().displayBoolean("Not an integer!!!");
		}
		return null;
	}

}
