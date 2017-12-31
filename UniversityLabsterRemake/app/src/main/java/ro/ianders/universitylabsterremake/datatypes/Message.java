package ro.ianders.universitylabsterremake.datatypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by paul.iusztin on 31.12.2017.
 */

public class Message {
    private String userUID;
    private String message;

    public Message() {}

    public Message(String userUID, String message) {
        this.userUID = userUID;
        this.message = message;
    }

    public String getUserUID() {
        return userUID;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return userUID + ": " + message;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put(DatabaseConstants.MESSAGE_USERUID, userUID);
        map.put(DatabaseConstants.MESSAGE_STRING_MESSAGE, message);

        return map;
    }
}
