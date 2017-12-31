package ro.ianders.universitylabsterremake.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul.iusztin on 31.12.2017.
 */

public class MessagesCourse {
    private String key; // same as the course
    private List<MessagesPerSchedule> allMessages;

    public MessagesCourse() {
    }

    public MessagesCourse(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public List<MessagesPerSchedule> getAllMessages() {
        return allMessages;
    }

    public void addMessagePerSchedule(MessagesPerSchedule messagesPerSchedule) {
        allMessages.add(messagesPerSchedule);
    }

    public void removeMessagePerSchedule(MessagesPerSchedule messagesPerSchedule) {
        allMessages.remove(messagesPerSchedule);
    }

    public boolean equals(Object o) {
        return (o instanceof MessagesCourse) ? ((MessagesCourse) o).key.equals(this.key) : false ;
    }
}
