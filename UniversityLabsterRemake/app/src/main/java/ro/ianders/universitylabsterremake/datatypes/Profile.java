package ro.ianders.universitylabsterremake.datatypes;

import android.graphics.Bitmap;

/**
 * Created by paul.iusztin on 13.12.2017.
 */

public class Profile {
    private String firstName;
    private String lastName;
    private String email;
    private String picture;

    public Profile() {}

    public Profile(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Profile(String firstName, String lastName, String email, String picture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
    }

    public Profile(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() { return picture; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPicture(String picture) {
        this.picture = picture; }
}
