package yc138_zc45.microMVC.model;

import java.util.UUID;

import common.room.command.ARoomAlgoCmd;
import common.room.datapacket.RoomDataPacket;
import common.room.message.IRoomErrorMessage;
import common.room.message.IRoomFailureMessage;
import provided.datapacket.IDataPacketID;
import provided.mixedData.MixedDataKey;
import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;
import yc138_zc45.mainMVC.model.MainModel;
import yc138_zc45.miniMVC.model.IMicroAdapter;
import yc138_zc45.miniMVC.model.MiniModel;

/**
 * The command for guess.
 */
public class GuessGameCmd extends ARoomAlgoCmd<IGuessMessage> {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 2730774503314369773L;
	
	/**
	 * Randomizer.
	 */
	private IRandomizer rand = Randomizer.Singleton;
	
	/**
	 * correct answer.
	 */
	private int correct;
	private int count = 1;

	
	
	/**
	 * Constructor 
	 * @param correct
	 */
	
	public GuessGameCmd() {

		this.correct = this.rand.randomInt(1, 20);
		System.out.println("In Game Cmd, correct number is: " + this.correct);
	}
	

	@Override
	public Void apply(IDataPacketID index, RoomDataPacket<IGuessMessage> host, Void... params) {
		// TODO Auto-generated method stub
		
//		MixedDataKey<IMicroAdapter> adapterKey = new MixedDataKey<IMicroAdapter>(MainModel.ID, "IMicroAdapter", IMicroAdapter.class);
//		IMicroAdapter microAdpt = getCmd2ModelAdpt().get(adapterKey);
		
		if (host.getData() == null) {
			getCmd2ModelAdpt().sendMessageToReceiver(IRoomErrorMessage.make("The message itself is damaged in GuessGameCmd", host), host.getSender());
		}
		
		String msg = host.getData().getMsg();
		int guess = Integer.MIN_VALUE; 
		
		//System.out.println();
		//MixedDataKey<IMicroAdapter> adapterKey = new MixedDataKey<IMicroAdapter>(gameUUID, "IMicroAdapter", IMicroAdapter.class);
		
		

		String feedback = "";
		try {
			guess = Integer.parseInt(msg);
		} catch (Exception e) {
			
			//MiniModel.this.m2vAdpt.displayMsg(msg + " is not an integer.");
			
//			microAdpt.displayMsg(msg + ": Invalid input! Please type in an integer guess.");
			feedback = "Invalid input! Please type in an integer guess.";
			
		}
		try {
			if (guess != Integer.MIN_VALUE) {
				if (guess <= 0 || guess > 20) {
					feedback = "Your guess is out of range!";
				} else {
					if (guess < correct) {
						feedback = "Your guess is smaller moron!";
					} else if (guess == correct) {
						feedback = "Terrorist win!\nYour trial is " + count;
					} else {
						feedback = "Your guess is bigger moron!";
					}
				}
				count++;
				
			}
//			microAdpt.displayMsg(msg + ": " + feedback + " GUEEESSSGAGMEEEECMD");
		} catch (Exception e) {
			getCmd2ModelAdpt().sendMessageToReceiver(IRoomFailureMessage.make("Failure when processing the command", host), host.getSender());
			e.printStackTrace();
		}
		
		System.out.println("Sending the guess response message to others");
		 
		
		getCmd2ModelAdpt().sendMessageToReceiver(IGuessResponseMessage.make(msg + ": " + feedback), host.getSender());
		
		return null;
	}
	

}
