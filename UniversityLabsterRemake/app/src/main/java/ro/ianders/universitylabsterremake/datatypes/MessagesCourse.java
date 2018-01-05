package ro.ianders.universitylabsterremake.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul.iusztin on 31.12.2017.
 */

public class MessagesCourse {
    private String key; // same as the course
    private List<Message> allMessages;

    public MessagesCourse() {
    }

    public MessagesCourse(String key, List<Message> allMessages) {
        this.key = key;
        this.allMessages = allMessages;
    }

    public MessagesCourse(String key) {
        this.key = key;
        this.allMessages = new ArrayList<>();
    }


    public String getKey() {
        return key;
    }

    public List<Message> getAllMessages() {
        return allMessages;
    }

    public void addMessage(Message message) {
        allMessages.add(message);
    }

    public void removeMessage(Message message) {
        allMessages.remove(message);
    }

    public boolean equals(Object o) {
        return (o instanceof MessagesCourse) ? ((MessagesCourse) o).key.equals(this.key) : false ;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(key).append("\n");

        if(allMessages != null)
        for(Message m : allMessages)
            stringBuilder.append(m.getUserUID()).append(": ").append(m.getMessage()).append("\n");
        else
            stringBuilder.append("null").append("\n");

        return stringBuilder.toString();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();

        m.put(DatabaseConstants.NOTES_KEY, key);
        m.put(DatabaseConstants.NOTES_MESSAGES, allMessages);

        return m;
    }


}
