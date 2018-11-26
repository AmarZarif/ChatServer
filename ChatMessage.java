
import java.io.Serializable;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.

    private int type;
    private String message;
    private String logoutMessage;

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

    private void broadcast(String message){


    }


}
