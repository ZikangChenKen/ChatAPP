package yc138_zc45.mainMVC.model;

import common.network.messageReceiver.INetworkMessageReceiver;

import java.rmi.RemoteException;

import common.network.datapacket.NetworkDataPacket;
import common.network.message.INetworkMessage;
import common.network.messageReceiver.INamedNetworkMessageReceiver;

/**
 * Class representing named connection.
 */
public class NamedNetworkMessageReceiver implements INamedNetworkMessageReceiver{
	
	private static final long serialVersionUID = 5843964274271466760L;

	/**
	 * The name of the Named Connection
	 */
	private String name;
	
	/**
	 * The conStub of the Named Connection
	 */
	private INetworkMessageReceiver connectStub;
	
	private NetworkDataPacket<? extends INetworkMessage> message;
	
	/**
	 * The constructor of the Named Connection.
	 * @param name the name of the Named Connection
	 * @param conStub the conStub of the Named Connection
	 */
	public NamedNetworkMessageReceiver(String name, INetworkMessageReceiver conStub) {
		this.name = name;
		this.connectStub = conStub;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public INetworkMessageReceiver getNetworkStub() {
		// TODO Auto-generated method stub
		return this.connectStub;
	}
	
	
    @Override
    public boolean equals(Object other) {
//       if(null != other && other instanceof INamedConnection) {
//    	   //return this.getConnectionStub().equals(other.g)
//    	  
//          return ((INamedConnection) other).getConnectionStub().equals(this.getConnectionStub());  // delegate to the stubs
//       }
    	if (!(other instanceof INamedNetworkMessageReceiver o)) {
    		return false;
    	}
    	return this.getNetworkStub().equals(o.getNetworkStub());
    	
       //return false;
    }
    
    @Override 
    public int hashCode() {
        return this.getNetworkStub().hashCode();  // delegate to the stub
    }

	@Override
	public void receiveMessage(NetworkDataPacket<? extends INetworkMessage> message) throws RemoteException {
		// TODO Auto-generated method stub
		this.message = message;
	}
	

}
