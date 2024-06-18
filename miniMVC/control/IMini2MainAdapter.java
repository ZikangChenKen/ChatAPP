package yc138_zc45.miniMVC.control;

import java.util.HashSet;

import common.room.messageReceiver.INamedRoomMessageReceiver;
import provided.pubsubsync.IPubSubSyncManager;
import provided.rmiUtils.RMIPortConfigWithBoundName;
import yc138_zc45.miniMVC.model.NameIDDyad;

/**
 * Mini to Main adapter.
 */
public interface IMini2MainAdapter {
	/**
	 * Exit the current chat room
	 * @param roomInfo The info of the room exit
	 */
	public void exit(NameIDDyad roomInfo);
	
	/**
	 * Get the name of the model
	 * @return the name
	 */
	public String getUserName();

	/**
	 * Get the RMI config of the model
	 * @return t he current RMI config of the model
	 */
	public RMIPortConfigWithBoundName getConfig();
	
	/**
	 * Get the pub sub manager of the chat room
	 * @return the pub sub manager 
	 */
	public IPubSubSyncManager getPubSubManger();
	
	/**
	 * Get the RMI URL.
	 * @return
	 */
	public String getRMIURL();
	

}
