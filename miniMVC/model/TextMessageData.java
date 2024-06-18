package yc138_zc45.miniMVC.model;
import common.room.message.ISendTextMessageData;

/**
 * Concrete implementation of ISendTextMessageData.
 */
public class TextMessageData implements ISendTextMessageData{

    /**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 11030947367677336L;
	
	/**
	 * String representing the message.
	 */
	private String msg;

    public TextMessageData(String msg) {
        this.msg = msg;
    }

	@Override
	public String getText() {
		return this.msg;
	}

}
