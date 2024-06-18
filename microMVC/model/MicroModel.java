package yc138_zc45.microMVC.model;

import provided.logger.ILogger;

/**
 * Micro model
 */
public class MicroModel {
	
	/**
	 * Model to view Adapter.
	 */
	private IMicroModel2ViewAdapter m2vadpt;
	
	/**
	 * Logger.
	 */
	//private ILogger logger;
	
	
	/**
	 * Micro model constructor
	 * @param m2vadpt model to view adapter
	 */
	public MicroModel(IMicroModel2ViewAdapter m2vadpt) {//, ILogger logger) {
		this.m2vadpt = m2vadpt;
		//this.logger = logger;
	}
	
	/**
	 * Start the micro model.
	 */
	public void start() {
		
	}
	
	/**
	 * Send text message.
	 * @param msg
	 */
	public void sendGuessMsg(IGuessMessage msg) {
		this.m2vadpt.sendGuessMsg(msg);
		
	}
	
	/**
	 * Send image message.
	 * @param msg
	 */
	public void sendImageMsg(IImageMessage msg) {
		this.m2vadpt.sendImageMsg(msg);
	}
}
