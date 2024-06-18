package yc138_zc45.microMVC.model;

import java.util.function.Supplier;

import javax.swing.JComponent;

import common.room.message.ISendTextMessageData;

/**
 * Adapter from micro model to view.
 */
public interface IMicroModel2ViewAdapter {
//	/**
//	 * Display the message
//	 * @param msg the message we want to display
//	 */
//	public void displayMsg(String msg);
//	
//	/**
//	 * Add component to the current view
//	 * @param componentSupplier The component factory
//	 * @param label A label to be displayed with the component
//	 */
//	public void addComponent(Supplier<JComponent> componentSupplier, String label);
//	
//	public int getGuess();
	
	/**
	 * Send the text message.
	 * @param msg
	 */
	void sendGuessMsg(IGuessMessage msg);
	
	/**
	 * Send the image message.
	 * @param msg
	 */
	void sendImageMsg(IImageMessage msg);
}
