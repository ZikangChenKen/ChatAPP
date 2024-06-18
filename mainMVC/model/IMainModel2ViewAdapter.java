package yc138_zc45.mainMVC.model;

import javax.swing.JComponent;

import common.network.messageReceiver.INamedNetworkMessageReceiver;
import yc138_zc45.miniMVC.model.NameIDDyad;

/**
 * The main model to view adapter.
 */
public interface IMainModel2ViewAdapter {
	/**
	 * Display the given message on the view
	 * @param msg The message to display
	 */
	void displayMsg(String msg);
	
	/**
	 * Add component to the view
	 * @param component the JComponent we want to connect
	 * @param name the name we want to connect
	 * @return Runnable element
	 */
	public Runnable addComponent(String name, JComponent component);
	
	/**
	 * Add connection to the Adapter
	 * @param con the INamedConnection we want to add
	 */
	public void addConnection(INamedNetworkMessageReceiver con);
	
	/**
	 * Add room to the Adapter
	 * @param room the room to be added
	 */
	public void addRoom(NameIDDyad room);

	/**
	 * Remove room from choices
	 * @param roomInfo the room info of the room to be removed
	 */
	public void removeRoom(NameIDDyad roomInfo);
	
	/**
	 * Remove the connection from the GUI
	 * @param con Connection to be removed
	 */
	public void removeConnection(INamedNetworkMessageReceiver con);
}
