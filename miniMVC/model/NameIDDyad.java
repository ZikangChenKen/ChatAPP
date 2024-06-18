package yc138_zc45.miniMVC.model;

import java.util.UUID;

import common.network.message.INameIDDyad;


/**
 * NameIDDyad of a room.
 */
public class NameIDDyad implements INameIDDyad {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4863659655928851007L;

	/**
	 * The UUID of the Chat room 
	 */
	private UUID ID;
	
	/**
	 * The name of the chat room
	 */
	private String name;

	/**
	 * The constructor of the NameIDDyad
	 * @param ID the channel ID of the room
	 * @param name the name of the room
	 */
	public NameIDDyad(UUID ID, String name){
		this.ID = ID;
		this.name = name;
	}

	/**
	 * Get the ID of the chat room 
	 * @return the ID of the chat room
	 */
	public UUID getID() {
		return this.ID;
	}

	/**
	 * Get the name of the chat room
	 * @return the name of the chat room
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the name of the chat room
	 * @return the name of the chat room
	 */
	public String toString() {
		return this.name;
	}
	
	/**
	 * Equal comparator of the NameIDDyad
	 * @param obj the object to compare 
	 * @return boolean value
	 */
	public boolean equal(Object obj) {
		if (!(obj instanceof NameIDDyad o)) {
			return false;
		}
		if (!o.getID().equals(this.ID)) {
			return false;
		}
		return true;
	}

}
