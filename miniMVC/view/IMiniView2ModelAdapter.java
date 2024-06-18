package yc138_zc45.miniMVC.view;


/**
 * @param <TDropListItem> drop item
 * 
 */
public interface IMiniView2ModelAdapter {
	/**
	 * Send string messages.
	 * @param msg message to send.
	 */
	public void sendTextMessage(String msg);

	/**
	 * Send string messages.
	 * @param msg message to send.
	 */
	public void sendBooleanMessage(String msg);
	
	/**
	 * Leave the room.
	 */
	public void leave();
	
	/**
	 * Create a micro view on remote user interface
	 */
	public void createMicroView();
	
	public void sendRandNumber(int rand);
	
}
