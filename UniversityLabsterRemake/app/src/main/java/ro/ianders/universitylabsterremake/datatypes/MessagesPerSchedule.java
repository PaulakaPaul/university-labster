package ro.ianders.universitylabsterremake.datatypes;

import java.util.List;

/**
 * Created by paul.iusztin on 31.12.2017.
 */

public class MessagesPerSchedule {

    private List<Message> notes;
    private int indexOfSchedule; // we need this so we can connect this with the right schedule

    public MessagesPerSchedule(List<Message> notes, int indexOfSchedule) {
        this.notes = notes;
        this.indexOfSchedule = indexOfSchedule;
    }

    public MessagesPerSchedule() {}

    public List<Message> getNotes() {
        return notes;
    }

    public int getIndexOfSchedule() {
        return indexOfSchedule;
    }

    public void addNote(Message note) {
        notes.add(note);
    }

    public void removeNote(Message note) {
        notes.remove(note);
    }
}
