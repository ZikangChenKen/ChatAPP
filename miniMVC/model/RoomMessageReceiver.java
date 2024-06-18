package yc138_zc45.miniMVC.model;

import java.rmi.RemoteException;

import common.room.datapacket.RoomDataPacket;
import common.room.datapacket.RoomDataPacketAlgo;
import common.room.message.IRoomMessage;
import common.room.messageReceiver.IRoomMessageReceiver;

/**
 * Concrete room message receiver
 */
public class RoomMessageReceiver implements IRoomMessageReceiver {
	
	/**
	 * The algo.
	 */
	private RoomDataPacketAlgo algo;

	
	/**
	 * @param algo room data packet algo
	 */
	public RoomMessageReceiver(RoomDataPacketAlgo algo) {
		this.algo = algo;
	}
	
	
	@Override
	public void receiveMessage(RoomDataPacket<? extends IRoomMessage> message) throws RemoteException {
		message.execute(algo);
	}
}
