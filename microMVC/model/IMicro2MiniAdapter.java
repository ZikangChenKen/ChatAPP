package yc138_zc45.microMVC.model;

import common.room.message.IRoomMessage;

/**
 * The adapter from micro to mini.
 */
public interface IMicro2MiniAdapter {
	
	/**
	 * Send the msg.
	 * @param msg
	 * @return
	 */
	public String sendMsg(IRoomMessage msg);
}
