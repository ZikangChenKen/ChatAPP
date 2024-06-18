package yc138_zc45.mainMVC.model;

import java.rmi.RemoteException;

import common.network.messageReceiver.INetworkMessageReceiver;
import common.network.datapacket.NetworkDataPacket;
import common.network.datapacket.NetworkDataPacketAlgo;
import common.network.message.INetworkMessage;
/**
 * The class represents the network.
 */
public class Network implements INetworkMessageReceiver{
	
	/**
	 * The app algo of the connection
	 */
	private NetworkDataPacketAlgo algo;
	
	/**
	 * Constructor of the Connection
	 * @param algo the app algo representing the app algo of the Connection
	 */
	public Network(NetworkDataPacketAlgo algo) {
		this.algo = algo;
	}

	/**
	 * Get the Algo of the Connection
	 * @return algo, the AppAlgo of the connection
	 */
	public NetworkDataPacketAlgo getAlgo() {
		return this.algo;
	}

	@Override
	public void receiveMessage(NetworkDataPacket<? extends INetworkMessage> message) throws RemoteException {
		message.execute(this.algo);
		
	}
//	
//    @Override
//    public boolean equals(Object other) {
//       if(null != other && other instanceof Connection) {
//          return ((Connection) other).getAlgo().equals(this.getAlgo());  // delegate to the stubs
//       }
//       return false;
//    }
//    
//    @Override 
//    public int hashCode() {
//        return this.getAlgo().hashCode();  // delegate to the stub
//    }

}
