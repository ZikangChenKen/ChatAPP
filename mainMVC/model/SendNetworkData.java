package yc138_zc45.mainMVC.model;

import java.util.HashSet;

import common.network.message.ISendNetworkData;
import common.network.messageReceiver.INamedNetworkMessageReceiver;

/**
 * Concrete implementation of connection message
 */
public class SendNetworkData implements ISendNetworkData{

    /**
	 * serial id
	 */
	private static final long serialVersionUID = 3821868469169974415L;

	/**
	 * the set of named connections
	 */
	private HashSet<INamedNetworkMessageReceiver> namedConnections;


    /**
	 * the constructor
     * @param namedConnections the named connections
     */
    public SendNetworkData(HashSet<INamedNetworkMessageReceiver> namedConnections) {
        this.namedConnections = namedConnections;
    }

	@Override
	public HashSet<INamedNetworkMessageReceiver> getFriends() {
		return this.namedConnections;
	}


}