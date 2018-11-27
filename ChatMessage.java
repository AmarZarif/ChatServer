

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
    private String recipient;

    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public ChatMessage(int type, String message, String recipient) {
        this.type = type;
        this.message = message;
        this.recipient = recipient;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipient() {
        return recipient;
    }
}
