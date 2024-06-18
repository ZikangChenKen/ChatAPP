package yc138_zc45.microMVC.view;

/**
 * Adapter from micro view to model.
 */
public interface IMicroView2ModelAdapter {
	
	/**
	 * Send a text message.
	 * @param msg
	 */
	public void sendGuessMessage(String msg);
	
	//public void sendRandomNum(int rand);
	
	/**
	 * Send the image.
	 * @param image
	 */
	public void sendImageMessage(String image);
	
//	public void createMicroView();
//
//	public void leave();

}
