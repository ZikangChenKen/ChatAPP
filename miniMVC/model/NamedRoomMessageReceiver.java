package yc138_zc45.miniMVC.model;

import java.rmi.RemoteException;
import java.util.UUID;

import common.network.messageReceiver.INamedNetworkMessageReceiver;
import common.network.messageReceiver.INetworkMessageReceiver;
import common.room.datapacket.RoomDataPacket;
import common.room.message.IRoomMessage;
import common.room.messageReceiver.INamedRoomMessageReceiver;
import common.room.messageReceiver.IRoomMessageReceiver;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import yc138_zc45.mainMVC.model.NamedNetworkMessageReceiver;

/**
 * Concrete implementation of INamedRoomMessageReceiver.
 */
public class NamedRoomMessageReceiver implements INamedRoomMessageReceiver {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1427315778638261668L;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private UUID channelID;
	/**
	 * 
	 */
	private IRoomMessageReceiver roomMessageReceiver;
	/**
	 * network named receiver
	 */
	private INamedNetworkMessageReceiver networkstub;
	
	/**
	 * @param name channel name
	 * @param roomMessageReceiver room message receiver stub
	 * @param channelID channel id
	 */
	public NamedRoomMessageReceiver(String name, IRoomMessageReceiver roomMessageReceiver, UUID channelID, INamedNetworkMessageReceiver networkstub) {
		this.name = name;
		this.roomMessageReceiver = roomMessageReceiver;
		this.channelID = channelID;
		this.networkstub = networkstub;
		ILoggerControl.getSharedLogger().log(LogLevel.INFO, "network receiver in concrete classsssssssssssssss: " + this.networkstub);
		
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public IRoomMessageReceiver getRoomConnectionStub() {
		return this.roomMessageReceiver;
	}
	
	/**
	 * @return channel id
	 */
	public UUID getID() {
		return this.channelID;
	}

	@Override
	public INamedNetworkMessageReceiver getNetworkDyad() {
		// TODO Auto-generated method stub
//		return new NamedNetworkMessageReceiver( this.name, constub);
		ILoggerControl.getSharedLogger().log(LogLevel.INFO, "The net work receiver is " + this.networkstub);
		return this.networkstub;
	}

	@Override
	public void receiveMessage(RoomDataPacket<? extends IRoomMessage> message) throws RemoteException {
		// TODO Auto-generated method stub
		this.roomMessageReceiver.receiveMessage(message);
	}

}
