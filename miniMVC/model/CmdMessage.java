package yc138_zc45.miniMVC.model;

import common.room.message.ICmdMessage;
import common.room.message.IRoomMessage;
import common.room.command.ARoomAlgoCmd;
import provided.datapacket.IDataPacketID;

/**
 * Concrete cmd message
 */
public class CmdMessage implements ICmdMessage{
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -2885695175450438552L;
	/**
	 * Message command encapsulated.
	 */
	private ARoomAlgoCmd<? extends IRoomMessage> messageCmd;
	/**
	 * Message id encapsulated.
	 */
	private IDataPacketID msgID;
	
	/**
	 * @param messageCmd message cmd
	 * @param msgID msg id
	 */
	public CmdMessage(ARoomAlgoCmd<? extends IRoomMessage> messageCmd, IDataPacketID msgID) {
		System.out.println("Constructing CmdMessage nowwww");
		this.messageCmd = messageCmd;
		this.msgID = msgID;
	}

	@Override
	public ARoomAlgoCmd<? extends IRoomMessage> getMessageCmd() {

		return this.messageCmd;
	}

	@Override
	public IDataPacketID getMsgID() {

		return this.msgID;
	}
	
	
	

}
