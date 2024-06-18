package yc138_zc45.miniMVC.model;

import common.room.message.IRequestCmdMessage;
import provided.datapacket.IDataPacketID;

/**
 * Message for Request Command 
 */
public class RequestCmdMessage implements IRequestCmdMessage {
	
	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = -2126311597276782627L;
	/**
	 * message ID.
	 */
	private IDataPacketID msgID;
	
	/**
	 * @param id id
	 */
	public RequestCmdMessage(IDataPacketID id) {
		this.msgID = id;
	}

	@Override
	public IDataPacketID getMsgID() {
		return this.msgID;
	}

}
