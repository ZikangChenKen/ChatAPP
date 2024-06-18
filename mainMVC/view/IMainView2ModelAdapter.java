package yc138_zc45.mainMVC.view;

import java.rmi.RemoteException;

/**
 * The main view to model adapter.
 * @param <TDropListItem> drop item
 */
public interface IMainView2ModelAdapter<TDropListItem1,TDropListItem2> {
	
	/**
	 * Manually connect to the remote system at the given IP address
	 * @param userName.
	 */
	void login(String userName);

	/**
	 * Shut down the RMI and quit the application
	 */
	void quit();
	
	/**
	 * create the new chat room based on the name given.
	 * @param name the name of the new chat room.
	 */
	public void createRoom(String name);
	
	/**
	 * Invite a selected user to the selected chat room
	 * @param room Room to invite
	 * @param userName User to invite
	 * @throws RemoteException 
	 */
	public void invite(TDropListItem1 room, TDropListItem2 userName) throws RemoteException;
	
}
