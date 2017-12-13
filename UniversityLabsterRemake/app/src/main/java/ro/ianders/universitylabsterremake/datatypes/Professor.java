package ro.ianders.universitylabsterremake.datatypes;

/**
 * Created by paul.iusztin on 12.12.2017.
 */

public class Professor {

    private String name;
    private String email;

    public Professor() {}

    public Professor(String name) {
        this.name = name;
    }

    public Professor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
