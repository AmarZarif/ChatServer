
import java.io.Serializable;
/**
 * ChatMessage - Project 4
 *
 * @author Muhammad Raziq Raif Ramli, mramli@purdue.edu
 * @author Amar Zarif Azamin, aazamin@purdue.edu
 * @version 11/26/2018
 */
final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.

    private int type;
    private String message;

    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

}
