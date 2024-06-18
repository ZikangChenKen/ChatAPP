package yc138_zc45.mainMVC.model;

import java.util.UUID;

import common.network.message.IForcedRoomInviteData;
import common.network.message.INameIDDyad;
import yc138_zc45.miniMVC.model.NameIDDyad;

/**
 * force to invite the user to a certain room
 */
public class ForcedRoomInviteData implements IForcedRoomInviteData{

  /**
	 * 
	 */
	private static final long serialVersionUID = 2380179735915816237L;
  
//	private UUID roomID;
//	private String chatRoomName;
	private NameIDDyad nameIDDyad;
	
	
	public ForcedRoomInviteData(UUID roomID, String name) {
//		this.roomID = roomID;
//		this.chatRoomName = name;
		this.nameIDDyad = new NameIDDyad(roomID, name);
	}

//	public UUID getUUID() {
//		return this.roomID;
//	}
//
//	public String getFriendlyName() {
//		return this.chatRoomName;
//	}

	@Override
	public INameIDDyad getDyad() {
		// TODO Auto-generated method stub
		return this.nameIDDyad;
	}
    

}
